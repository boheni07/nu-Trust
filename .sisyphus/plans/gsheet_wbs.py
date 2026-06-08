#!/usr/bin/env python3
"""
WBS.md -> Google Sheets 연동 스크립트
시트 1: WBS 테이블, 시트 2: GANTT 차트

사용: python3 gsheet_wbs.py
환경: GOOGLE_APPLICATION_CREDENTIALS + WBS_SPREADSHEET_ID
"""

import os
import re
import sys
from datetime import datetime, timedelta

import gspread
from google.oauth2 import service_account

WBS_FILE = os.path.join(os.path.dirname(__file__), "WBS.md")
SPREADSHEET_ID = os.environ.get("WBS_SPREADSHEET_ID", "YOUR_SPREADSHEET_ID")
PROJECT_START = datetime(2026, 6, 8)


def parse_wbs_md(path: str):
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    sprints = []
    sprint_pattern = re.compile(
        r"##\s+\d+\.\s+(Sprint\s+\d+|Migration)\s*—\s*([^(]+?)\s*\(([W0-9~]+),?\s*(\d+)주\)",
        re.IGNORECASE,
    )
    sections = re.split(r"##\s+\d+\.", content)

    for i, section in enumerate(sections[1:]):
        match = sprint_pattern.search(section)
        if not match:
            continue

        sprint_name = match.group(1).strip()
        sprint_title = match.group(2).strip()
        week_range = match.group(3).strip()
        duration_weeks = int(match.group(4))

        lines = section.split("\n")
        header_line_idx = next((idx for idx, l in enumerate(lines) if "| WBS ID" in l), None)
        if header_line_idx is None:
            continue

        items = []
        in_table = False
        for line in lines:
            if "| WBS ID" in line:
                in_table = True
                continue
            if in_table and (line.strip().startswith("---") or line.strip() == ""):
                if items:
                    continue
                in_table = False
            if in_table and "|" in line:
                cells = [c.strip() for c in line.split("|") if c.strip()]
                if len(cells) >= 4:
                    wbs_raw = cells[0].replace("WBS_ID", "")
                    if not wbs_raw.strip().replace("-", "").isdigit():
                        continue
                    items.append({"wbs_id": cells[0], "title": cells[1], "label": cells[2], "priority": cells[3]})

        sprint_start_week = 1
        week_match = re.search(r"[Ww]eek\s+(\d+)", week_range)
        if week_match:
            sprint_start_week = int(week_match.group(1))

        sprint_start = PROJECT_START + timedelta(weeks=(sprint_start_week - 1) * 7)
        sprint_end = sprint_start + timedelta(weeks=duration_weeks * 7) - timedelta(days=1)

        sprints.append({
            "name": sprint_name,
            "title": sprint_title,
            "items": items,
            "duration_weeks": duration_weeks,
            "start_date": sprint_start.strftime("%Y-%m-%d"),
            "end_date": sprint_end.strftime("%Y-%m-%d"),
        })

    return sprints


def authenticate():
    credentials_path = os.environ.get(
        "GOOGLE_APPLICATION_CREDENTIALS",
        os.path.join(os.path.dirname(__file__), "gcs-key.json"),
    )
    if not os.path.exists(credentials_path):
        print(f"❌ Service Account 키 파일을 찾을 수 없습니다: {credentials_path}")
        print("   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your-key.json")
        sys.exit(1)
    scopes = ["https://www.googleapis.com/auth/spreadsheets", "https://www.googleapis.com/auth/drive"]
    return service_account.Credentials.from_service_account_file(credentials_path, scopes=scopes)


def main():
    print("=" * 60)
    print("  nu_Trust WBS -> Google Sheets 연동")
    print("=" * 60)

    print(f"\n📄 WBS.md 파싱 중...")
    sprints = parse_wbs_md(WBS_FILE)
    print(f"🔍 Sprint {len(sprints)}개, 총 {sum(len(s['items']) for s in sprints)}개 WBS 항목")

    print("\n🔐 인증 중...")
    gc = authenticate()

    print("\n📊 Google Sheets 업데이트 중...")

    try:
        if SPREADSHEET_ID == "YOUR_SPREADSHEET_ID" or not SPREADSHEET_ID:
            print("📝 새 스프레드시트 생성 중...")
            new_sp = gc.create("nu_Trust WBS & Gantt")
            SPREADSHEET_ID = new_sp.id
            print(f"✅ 새 시트: https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}")
        else:
            new_sp = gc.open_by_key(SPREADSHEET_ID)
            print(f"📂 기존 시트 로드: {SPREADSHEET_ID}")

        wbs_sheet = new_sp.worksheet("WBS")
    except Exception as e:
        print(f"❌ 시트 접근 오류: {e}")
        sys.exit(1)

    try:
        gantt_sheet = new_sp.worksheet("GANTT")
    except gspread.WorksheetNotFound:
        gantt_sheet = new_sp.add_worksheet(title="GANTT", rows="100", cols="25")

    header = ["WBS_ID", "Sprint", "Sprint 요약", "기능명", "Label", "Priority", "시작일", "종료일"]
    wbs_sheet.update([header])
    for sprint in sprints:
        for item in sprint["items"]:
            wbs_sheet.append_row([
                item["wbs_id"],
                sprint["name"],
                f"{sprint['title']} ({sprint['duration_weeks']}주)",
                item["title"],
                item["label"],
                item["priority"],
                sprint["start_date"],
                sprint["end_date"],
            ])
    print(f"✅ WBS 시트: {wbs_sheet.row_count} row")

    gantt_header = ["WBS_ID", "기능명", "시작일", "종료일", "소요(주)", "Sprint", "Label", "Priority"]
    gantt_sheet.clear()
    gantt_sheet.append_row(gantt_header)
    for sprint in sprints:
        for item in sprint["items"]:
            gantt_sheet.append_row([
                item["wbs_id"], item["title"], sprint["start_date"], sprint["end_date"],
                sprint["duration_weeks"], sprint["name"], item["label"], item["priority"],
            ])
    print(f"✅ GANTT 시트: {gantt_sheet.row_count} row")

    print(f"\n📎 https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}")
    print("=" * 60)
    print("  ✅ 완료!")
    print("=" * 60)


if __name__ == "__main__":
    main()
