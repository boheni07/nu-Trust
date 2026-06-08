import csv
import openpyxl
from openpyxl.styles import (
    Font, PatternFill, Alignment, Border, Side, numbers
)
from openpyxl.formatting.rule import CellIsRule
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.datavalidation import DataValidation
from datetime import datetime, timedelta

# --- Sprint 색상 정의 ---
SPRINT_COLORS = {
    "Sprint 0": ("BFBFBF", "E8E8E8"),   # 회색 - 환경 구축
    "Sprint 1": ("4472C4", "D6E4F0"),   # 청록 - Auth/RBAC
    "Sprint 2": ("70AD47", "E2EFDA"),   # 녹색 - Ticket/워크플로우
    "Sprint 3": ("ED7D31", "FCE4D6"),   # 주황 - 실시간 채팅/카드
    "Sprint 4": ("9E480E", "F0E6D2"),   # 보라/갈색 - Admin/QA/Deploy
}

ROW_FILL_NORMAL = {"": "FFFFFF",
                   "Sprint 0": "E8E8E8",
                   "Sprint 1": "D6E4F0",
                   "Sprint 2": "E2EFDA",
                   "Sprint 3": "FCE4D6",
                   "Sprint 4": "F0E6D2",
                   }

ROW_FILL_HOVER = {"": "F5F5F5",
                  "Sprint 0": "D9D9D9",
                  "Sprint 1": "C9D9E8",
                  "Sprint 2": "D1E5CC",
                  "Sprint 3": "F5D5BE",
                  "Sprint 4": "E6D7C5",
                  }

THICK = Side(style='thick', color='4472C4')
MED = Side(style='medium', color='4472C4')
THIN = Side(style='thin', color='B0B0B0')

HEADER_FONT = Font(name='Malgun Gothic', bold=True, size=11, color='FFFFFF')
HEADER_FILL = PatternFill('solid', fgColor='2F5496')

TITLE_FONT = Font(name='Malgun Gothic', bold=True, size=16, color='2F5496')
SUBTITLE_FONT = Font(name='Malgun Gothic', bold=True, size=12, color='2F5496')
DATE_FONT = Font(name='Malgun Gothic', bold=True, size=10, color='555555')

CELL_FONT = Font(name='Malgun Gothic', size=10)
CELL_FONT_BOLD = Font(name='Malgun Gothic', size=10, bold=True)

DATA_DATE_FMT = 'YYYY-mm-DD'

THIN_BORDER = Border(top=THIN, bottom=THIN, left=THIN, right=THIN)
MEDIUM_BORDER = Border(top=MED, bottom=MED, left=THIN, right=THIN)
THICK_BORDER = Border(top=THICK, bottom=THICK, left=MED, right=MED)

TOP_BORDER = Border(top=THICK, bottom=THIN, left=THIN, right=THIN)

SPRINT_HEADERS = ['000']


def load_csv(path):
    rows = []
    with open(path, encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        for r in reader:
            rows.append(r)
    return rows


def build_gantt_timeline():
    """날짜 범위 기반으로 Gantt 막대용 날짜 범위 생성"""
    # 전체 기간: 2026-06-08 ~ 2026-10-19
    # 각 날짜를 열로 배치 (Day 단위)
    dates = []
    start = datetime(2026, 6, 8)
    end = datetime(2026, 10, 19)
    current = start
    while current <= end:
        dates.append(current)
        current += __import__('datetime').timedelta(days=1)
    return dates


def write_dashboard(ws, tasks):
    """대시보드 Sheet"""
    # 제목
    ws.merge_cells('A1:H1')
    ws['A1'].value = 'nu_Trust — 프로젝트 대시보드'
    ws['A1'].font = TITLE_FONT

    ws['A3'].value = 'Total Tasks'
    ws['A3'].font = SUBTITLE_FONT
    ws['B3'].value = len(tasks)
    ws['B3'].font = Font(size=14, bold=True, color='2F5496')

    # Sprint별 Task 수
    ws['A5'].value = 'Sprint별 Task 분포'
    ws['A5'].font = SUBTITLE_FONT

    sprint_counts = {}
    for t in tasks:
        sp = t.get('Sprint', '')
        sprint_counts[sp] = sprint_counts.get(sp, 0) + 1

    ws['A7'].value = 'Sprint'
    ws['A7'].font = CELL_FONT_BOLD
    ws['C7'].value = 'Task 수'
    ws['C7'].font = CELL_FONT_BOLD

    for i, sp in enumerate(['Sprint 0', 'Sprint 1', 'Sprint 2', 'Sprint 3', 'Sprint 4']):
        r = i + 8
        ws[f'A{r}'].value = sp
        ws[f'A{r}'].font = CELL_FONT
        ws[f'A{r}'].fill = PatternFill('solid', fgColor=SPRINT_COLORS.get(sp, ('FFFFFF', 'FFFFFF'))[0])
        ws[f'A{r}'].font = Font(name='Malgun Gothic', size=10, bold=True, color='FFFFFF')
        ws[f'C{r}'].value = sprint_counts.get(sp, 0)
        ws[f'C{r}'].font = CELL_FONT_BOLD
        ws[f'C{r}'].number_format = '#,##0'
        ws.column_dimensions['B'].width = 8

    # Role별 Task 수 (F열부터)
    ws['F5'].value = 'Role별 Task 분포'
    ws['F5'].font = SUBTITLE_FONT

    role_counts = {}
    for t in tasks:
        role = t.get('Role', '')
        role_counts[role] = role_counts.get(role, 0) + 1

    ws['F7'].value = 'Role'
    ws['F7'].font = CELL_FONT_BOLD
    ws['H7'].value = 'Task 수'
    ws['H7'].font = CELL_FONT_BOLD

    roles = sorted(role_counts.keys())
    for i, role in enumerate(roles):
        r = i + 8
        ws[f'F{r}'].value = role
        ws[f'F{r}'].font = CELL_FONT
        ws[f'H{r}'].value = role_counts[role]
        ws[f'H{r}'].font = CELL_FONT_BOLD

    ws['A2'].value = 'Base Date: 2026-06-08 (월요일) | 총 개발 주기: 16주 (2026.06.08 ~ 2026.10.19)'
    ws['A2'].font = DATE_FONT
    ws['A2'].alignment = Alignment(horizontal='left')

    # 컬럼 너비
    ws.column_dimensions['A'].width = 16
    ws.column_dimensions['C'].width = 12
    ws.column_dimensions['F'].width = 14
    ws.column_dimensions['H'].width = 12

    ws.sheet_properties.tabColor = '2F5496'


def write_wbs(ws, tasks):
    """WBS Sheet"""
    # 컬럼 너비
    ws.column_dimensions['A'].width = 8   # Task ID
    ws.column_dimensions['B'].width = 8   # Parent ID
    ws.column_dimensions['C'].width = 10  # Sprint
    ws.column_dimensions['D'].width = 10  # Week
    ws.column_dimensions['E'].width = 50  # Task Name
    ws.column_dimensions['F'].width = 8   # Assignee
    ws.column_dimensions['G'].width = 10  # Role
    ws.column_dimensions['H'].width = 12  # Start Date
    ws.column_dimensions['I'].width = 12  # End Date
    ws.column_dimensions['J'].width = 10  # Duration (days)
    ws.column_dimensions['K'].width = 20  # Dependencies

    # 헤더
    ws['A1'].value = 'Task ID'
    ws['B1'].value = 'Parent ID'
    ws['C1'].value = 'Sprint'
    ws['D1'].value = 'Week'
    ws['E1'].value = 'Task Name'
    ws['F1'].value = 'Assignee'
    ws['G1'].value = 'Role'
    ws['H1'].value = 'Start Date'
    ws['I1'].value = 'End Date'
    ws['J1'].value = 'Duration (days)'
    ws['K1'].value = 'Dependencies'

    for col in range(1, 12):
        cell = ws.cell(row=1, column=col)
        cell.font = HEADER_FONT
        cell.fill = HEADER_FILL
        cell.alignment = Alignment(horizontal='center', vertical='center', wrap_text=True)

    # Sprint별 구분선용 로직
    prev_sprint = ''
    data_start = 2
    for i, task in enumerate(tasks):
        r = data_start + i
        ws[f'A{r}'].value = task['Task ID']
        ws[f'B{r}'].value = task['Parent ID'] or ''
        ws[f'C{r}'].value = task['Sprint']
        ws[f'D{r}'].value = task['Week']
        ws[f'E{r}'].value = task['Task Name']
        ws[f'F{r}'].value = task['Assignee']
        ws[f'G{r}'].value = task['Role']
        ws[f'H{r}'].value = task['Start Date']
        ws[f'I{r}'].value = task['End Date']
        ws[f'J{r}'].value = int(task['Duration (days)'])
        ws[f'K{r}'].value = task['Dependencies'] or ''

        sprint = task['Sprint']
        fill_color = ROW_FILL_NORMAL.get(sprint, 'FFFFFF')
        base_fill = PatternFill('solid', fgColor=fill_color)

        border = THIN_BORDER
        if sprint != prev_sprint:
            border = TOP_BORDER

        for col_idx in range(1, 12):
            cell = ws.cell(row=r, column=col_idx)
            cell.font = CELL_FONT
            cell.alignment = Alignment(vertical='center')
            cell.border = border
            cell.fill = base_fill

            # 날짜 서식
            if col_idx in (8, 9):
                cell.number_format = DATA_DATE_FMT
                cell.alignment = Alignment(horizontal='center', vertical='center')
            # 정수 서식
            elif col_idx == 10:
                cell.number_format = '#,##0'
                cell.alignment = Alignment(horizontal='center', vertical='center')
            elif col_idx == 1:
                cell.alignment = Alignment(horizontal='center', vertical='center')
            elif col_idx in (2, 6, 7):
                cell.alignment = Alignment(horizontal='center', vertical='center')

        prev_sprint = sprint

    num_rows = len(tasks)
    end_row = data_start + num_rows - 1

    # 전체 범위 테두리 추가
    for col_idx in range(1, 12):
        # 상단 테두리
        ws.cell(row=data_start, column=col_idx).border = THICK_BORDER
        # 하단 테두리
        ws.cell(row=end_row, column=col_idx).border = Border(top=THIN, bottom=THICK, left=MED, right=MED)

    # 필터 활성화
    ws.auto_filter.ref = f'A1:K{end_row}'

    # Sprint 슬라이서용 원본 데이터 영역 설정 (필터용)
    ws.sheet_properties.tabColor = '70AD47'

    return end_row


def write_weekly_summary(ws, tasks):
    """주간 요약 Sheet"""
    ws.merge_cells('A1:H1')
    ws['A1'].value = 'nu_Trust — Sprint별 주간 Task 요약'
    ws['A1'].font = SUBTITLE_FONT

    # 주간별 집계
    week_data = {}
    for t in tasks:
        sprint = t['Sprint']
        week = t['Week']
        key = (sprint, week)
        week_data[key] = week_data.get(key, 0) + 1

    # 헤더
    ws['A3'].value = 'Sprint'
    ws['B3'].value = 'Week'
    ws['C3'].value = 'Task 수'
    ws['D3'].value = '비고'

    for col_idx in [1, 2, 3, 4]:
        cell = ws.cell(row=3, column=col_idx)
        cell.font = HEADER_FONT
        cell.fill = HEADER_FILL
        cell.alignment = Alignment(horizontal='center', vertical='center')

    row = 4
    prev_sprint = ''
    for (sprint, week), count in sorted(week_data.items()):
        r_fill = PatternFill('solid', fgColor=ROW_FILL_NORMAL.get(sprint, 'FFFFFF'))

        ws[f'A{row}'].value = sprint
        ws[f'B{row}'].value = week
        ws[f'C{row}'].value = count
        ws[f'D{row}'].value = 'Sprint'

        for col_idx in range(1, 5):
            cell = ws.cell(row=row, column=col_idx)
            cell.font = CELL_FONT
            cell.border = THIN_BORDER
            cell.fill = r_fill
            cell.alignment = Alignment(horizontal='center', vertical='center')

        prev_sprint = sprint
        row += 1

    ws.column_dimensions['A'].width = 12
    ws.column_dimensions['B'].width = 12
    ws.column_dimensions['C'].width = 10
    ws.column_dimensions['D'].width = 20

    # Sprint별 총합 Row
    total_row = row + 1
    ws[f'A{total_row}'].value = '총계'
    ws[f'A{total_row}'].font = Font(name='Malgun Gothic', size=10, bold=True)
    ws[f'C{total_row}'].value = len(tasks)
    ws[f'C{total_row}'].font = Font(name='Malgun Gothic', size=10, bold=True)
    # 총합 Row 강조
    total_fill = PatternFill('solid', fgColor='2F5496')
    for col_idx in range(1, 5):
        cell = ws.cell(row=total_row, column=col_idx)
        cell.font = Font(name='Malgun Gothic', size=10, bold=True, color='FFFFFF')
        cell.fill = total_fill
        cell.alignment = Alignment(horizontal='center', vertical='center')

    ws.sheet_properties.tabColor = 'ED7D31'


GANTT_CHART_SPRINT_COLORS = {
    "Sprint 0": "BFBFBF",
    "Sprint 1": "2F5496",
    "Sprint 2": "70AD47",
    "Sprint 3": "ED7D31",
    "Sprint 4": "9E480E",
}

GANTT_CHART_ROW_COLORS = {
    "Sprint 0": "E8E8E8",
    "Sprint 1": "D6E4F0",
    "Sprint 2": "E2EFDA",
    "Sprint 3": "FCE4D6",
    "Sprint 4": "F0E6D2",
}


def _excel_date_to_serial(dt: datetime) -> float:
    epoch = datetime(1899, 12, 30)
    return (dt - epoch).days


def write_gantt_chart(ws, tasks: list[dict], timeline_start: datetime | None = None) -> None:  # type: ignore
    """Gantt chart: 좌측 Task명 + 우측 수평 stacked bar (offset invisible + duration colored)"""
    from openpyxl.chart import BarChart, Reference

    t_start = timeline_start or datetime(2026, 6, 8)
    timeline_serial = _excel_date_to_serial(t_start)

    # Row headers
    chart_title = Font(name='Malgun Gothic', bold=True, size=14, color='2F5496')
    subtitle = Font(name='Malgun Gothic', bold=True, size=10, color='555555')
    gantt_font = Font(name='Malgun Gothic', size=9)

    # ── Title ──
    ws.merge_cells('A1:F1')
    ws['A1'].value = 'nu_Trust — Gantt 차트 (2026.06.08 ~ 2026.10.19)'
    ws['A1'].font = chart_title

    ws['A3'].value = '※ 왼쪽: Task / 오른쪽: 막대(색상=Sprint, 길이=Duration)'
    ws['A3'].font = subtitle

    # ── Date timeline header row ──
    week_header_row = 5
    ws.cell(row=week_header_row, column=1).value = 'Task'
    ws.cell(row=week_header_row, column=1).font = HEADER_FONT
    ws.cell(row=week_header_row, column=1).fill = HEADER_FILL
    ws.cell(row=week_header_row, column=1).alignment = Alignment(horizontal='center', vertical='center')

    # Write week markers across top (every 7 days)
    current = t_start
    week_col = 2
    for week_num in range(1, 17):
        date_label = current.strftime('%m/%d')
        ws.cell(row=week_header_row, column=week_col).value = date_label
        ws.cell(row=week_header_row, column=week_col).font = Font(name='Malgun Gothic', bold=True, size=9)
        ws.cell(row=week_header_row, column=week_col).fill = HEADER_FILL
        ws.cell(row=week_header_row, column=week_col).alignment = Alignment(horizontal='center', vertical='center')
        # Week separator vertical line
        ws.cell(row=week_header_row, column=week_col).border = THIN_BORDER
        current += timedelta(days=7)
        week_col += 1

    # Total width for timeline columns
    ws.column_dimensions['A'].width = 40
    for col_letter in ['B', 'C', 'D', 'E', 'F', 'G']:
        ws.column_dimensions[col_letter].width = 3

    # ── Task data table (rows below timeline header) ──
    data_start_row = week_header_row + 1  # row 6

    # Sort: by Start Date, then Task ID
    sorted_tasks = sorted(tasks, key=lambda t: (t['Start Date'], t['Task ID']))

    last_data_row = data_start_row + len(sorted_tasks) - 1

    for i, task in enumerate(sorted_tasks):
        r = data_start_row + i

        display_name = f"{task['Task ID']} | {task['Task Name']}"
        ws.cell(row=r, column=1).value = display_name
        ws.cell(row=r, column=1).font = gantt_font
        ws.cell(row=r, column=1).alignment = Alignment(vertical='center', wrap_text=True)

        sprint = task['Sprint']
        row_fill = GANTT_CHART_ROW_COLORS.get(sprint, 'FFFFFF')
        row_pattern = PatternFill('solid', fgColor=row_fill)

        # B col = offset (days from timeline start to task start)
        start_date = datetime.strptime(task['Start Date'], '%Y-%m-%d')
        end_date = datetime.strptime(task['End Date'], '%Y-%m-%d')
        offset_days = _excel_date_to_serial(start_date) - timeline_serial
        duration_days = (end_date - start_date).days + 1  # inclusive

        ws.cell(row=r, column=2).value = offset_days
        ws.cell(row=r, column=2).number_format = '0'
        ws.cell(row=r, column=2).fill = row_pattern

        ws.cell(row=r, column=3).value = duration_days
        ws.cell(row=r, column=3).number_format = '0'
        ws.cell(row=r, column=3).fill = row_pattern

        # Add thin borders to B and C for row visibility
        for c in (2, 3):
            ws.cell(row=r, column=c).border = THIN_BORDER
            ws.cell(row=r, column=c).alignment = Alignment(horizontal='center', vertical='center')

    ws.column_dimensions['A'].width = 42
    ws.column_dimensions['B'].width = 4
    ws.column_dimensions['C'].width = 4

    # ── Hidden task list for Y-axis labels ──
    label_col_letter = 'F'
    label_start_row = data_start_row
    for i, task in enumerate(sorted_tasks):
        r = label_start_row + i
        label_row = data_start_row + i
        full_label = f"{task['Task ID']} | {task['Task Name']}"
        ws.cell(row=label_start_row + i, column=6).value = full_label
        ws.cell(row=label_start_row + i, column=6).font = Font(name='Malgun Gothic', size=8)
        ws.column_dimensions['F'].width = 3
        ws.column_dimensions['G'].width = 3
        ws.column_dimensions['H'].width = 3

    last_label_row = label_start_row + len(sorted_tasks) - 1

    # ── Create Stacked Bar Chart ──
    # Data area: B5:C[last_data_row] (offset + duration)
    # Categories: F6:F[last_data_row] (task labels)

    chart_top_row = last_data_row + 4  # chart starts below the data

    chart = BarChart()
    chart.type = 'bar'          # horizontal bar
    chart.grouping = 'stacked'  # offset + duration stacked
    chart.width = 55
    chart.height = 65

    # Title
    chart.title = 'Sprint별 Task Gantt'
    chart.style = 10

    # Data series — 2 series (offset + duration)
    values_offset = Reference(ws, min_col=2, min_row=week_header_row, max_row=last_data_row)
    values_duration = Reference(ws, min_col=3, min_row=week_header_row, max_row=last_data_row)

    chart.add_data(values_offset, titles_from_data=False)
    chart.add_data(values_duration, titles_from_data=False)

    # Categories (Y-axis labels)
    cats = Reference(ws, min_col=6, min_row=label_start_row, max_row=last_label_row)
    chart.set_categories(cats)

    # X-axis
    chart.x_axis.title = "일수 (days)"  # type: ignore

    chart.series[0].graphicalProperties.noFill = True  # type: ignore # noqa
    chart.series[0].invertIfNegative = False  # type: ignore

    chart.series[1].graphicalProperties.solidFill = "2F5496"  # type: ignore

    # Fonts
    chart.y_axis.tick_label_font = Font(name='Malgun Gothic', size=8)  # type: ignore
    chart.x_axis.tick_label_font = Font(name='Malgun Gothic', size=8)  # type: ignore

    # Position chart
    ws.add_chart(chart, f'A{chart_top_row}')

    ws.sheet_properties.tabColor = '9E480E'  # type: ignore


def add_filter(ws, end_row=999):
    """필터 및 슬라이서용 셀 필터 설정"""
    ws.auto_filter.ref = f'A1:K{end_row}'


def main():
    tasks = load_csv('/home/boheni/nu-Trust/.sisyphus/plans/nus-trust-wbs-gantt.csv')
    wb = openpyxl.Workbook()
    wb.remove(wb.active)  # type: ignore

    # Sheet 1: WBS
    ws_wbs = wb.create_sheet('WBS', index=0)
    end_row = write_wbs(ws_wbs, tasks)

    # Sheet 2: Gantt 차트
    ws_gantt = wb.create_sheet('Gantt 차트', index=1)
    write_gantt_chart(ws_gantt, tasks)

    # Sheet 3: 대시보드
    ws_dash = wb.create_sheet('대시보드', index=2)
    write_dashboard(ws_dash, tasks)

    # Sheet 4: 주간 요약
    ws_weekly = wb.create_sheet('주간 요약', index=3)
    write_weekly_summary(ws_weekly, tasks)

    output_path = '/home/boheni/nu-Trust/.sisyphus/plans/nus-trust-wbs-gantt.xlsx'
    wb.save(output_path)
    print(f'Saved to {output_path}')


if __name__ == '__main__':
    main()
