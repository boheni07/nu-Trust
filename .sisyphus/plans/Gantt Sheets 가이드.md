# nu_Trust — Excel Gantt 차트 연동 가이드

**Version:** 1.0
**Date:** 2026년 5월 31일
**Company:** (주)엔유비즈

---

## 1. 개요

이 문서는 `(주)엔유비즈` nu_Trust 개발 프로젝트의 WBS(Work Breakdown Structure)를 Excel 스프레드시트에서 Gantt 차트로 시각화하는 방법을 설명합니다.

기존 CSV 파일(`nus-trust-wbs-gantt.csv`)을 Excel로 불러와 **간트 차트(SmartArt 또는 차트 시각화)**로 변환하는 전 과정을 다룹니다.

---

## 2. CSV → Excel 불러오기

### 2.1단계: CSV 파일 열기

1. Excel을 실행합니다.
2. **파일 → 열기** → `nus-trust-wbs-gantt.csv` 선택
3. UTF-8 인코딩 문제 시: **데이터 → 텍스트/CSV에서** 메뉴를 사용

### 2.2단계: 데이터 → 테이블로 변환

1. 데이터를 불러온 후, **A1 셀을 클릭**
2. **삽입 → 테이블** (또는 `Ctrl+T`)
3. "범위에 헤더 있음" 체크박스를 켭니다.
4. 이제 **175개 Task**가 구조화된 테이블로 변환되었습니다.

### 2.3 열 구조 (11개 필드)

| 열 | 필드명 | 설명 | 예시 |
|---|---|---|---|
| A | Task ID | 전역 고유 ID | 001, 144, 221 |
| B | Parent ID | 상위 Task ID (없으면 비움) | 001, 104, |
| C | Sprint | Sprint 번호 | Sprint 0, Sprint 1, Sprint 2 |
| D | Week | Sprint 내 주기 | Day 1, Wk 3-4, Wk 1 |
| E | Task Name | 작업명 | JWT 토큰 발급/검증 API |
| F | Assignee | 담당자 | T1, T2, T4 |
| G | Role | 역할 | Infra, BE, FE, DB |
| H | Start Date | 시작日期 | 2026-06-08 |
| I | End Date | 종료日期 | 2026-06-09 |
| J | Duration (days) | 일수 | 2, 10, 3 |
| K | Dependencies | 의존성 Task ID | 101, 104 |

---

## 3. Excel 간트 차트 (간판보) 생성

### 방법 1: 조건부 서식 사용 (추천)

가장 직관적이고 수정이 쉬운 방법입니다.

#### 3.1 조건부 서식 규칙 준비

1. Task Name 열(E)을 기준으로, 각 행의 시작/종료 날짜를 **가로 막대**로 표현
2. **Home → 조건부 서식 → 규칙 만들기의** 사용

#### 3.2 공식 예시

F열(또는 별도 Gantt 막대 열)에 다음과 같은 수식을 사용:

```
=AND($H2<TODAY()+1, $I2>=TODAY()-1) → 완료/진행 중
```

또는 **Date HeaderRow를 만들어 가로로 배치**:

1. 행 E(작업명) 왼쪽, A~D열 바로 오른쪽에 날짜 헤더를 만듭니다.
   - K1에는 "Day 1", L1에는 "Day 3", ..., W1에는 "Wk 15-16"
2. 각 셀에 조건부 서식 공식 적용:

```excel
=AND($I2>=K$1, $H2<=K$1)
```

3. 조건부 서식 색상:
   - 완료: **파란색 (#4472C4)**
   - 진행 중: **주황색 (#ED7D31)**
   - 미시작: **회색 (#BFBFBF)**

#### 3.3 조건부 서식 규칙 관리

1. **Home → 조건부 서식 → 규칙 관리**
2. "이 워크시트 규칙" 탭에서 **범위 조정**
3. 175개 행 전체에 적용됩니다.

### 방법 2: 차트 시각화 (혼합형 차트 사용)

1. 필요한 열(Start Date, End Date, Task Name)만 선택
2. **삽입 → 차트 → 막대 차트(가로)**
3. **차트 도구 서식 → 데이터 색 변경**

### 방법 3: SmartArt Timeline 사용 (단순)

- 빠른 시각화용
- 정밀한 WBS 관리에는 적합하지 않음

---

## 4. Sprint별 필터 & 색상 구분

### 4.1 Sprint별 색상 체계

| Sprint | 담당 영역 | 색상 | 색상 코드 |
|--------|-----------|------|-----------|
| Sprint 0 | 환경 구축 | 회색 | #BFBFBF |
| Sprint 1 | Auth / RBAC | 청록 | #4472C4 |
| Sprint 2 | Ticket / 워크플로우 | 녹색 | #70AD47 |
| Sprint 3 | 실시간 채팅 / 카드 | 주황 | #ED7D31 |
| Sprint 4 | Admin Panel / QA / Deploy | 보라 | #9E480E |

### 4.2 조건부 서식으로 Sprint별 색 자동 적용

1. Task Name 열(E) 선택
2. **Home → 조건부 서식 → 규칙 만들기**
3. 수식 사용:

```excel
=$C2="Sprint 0"
```

4. 포맷 → 채우기 → 회색 선택
5. 마찬가지로 Sprint 1~4 규칙 추가

### 4.3 자동 필터 & 슬라이서

1. 테이블을 선택 → **디자인 → 슬라이서 삽입**
2. "Sprint"과 "Role" 슬라이서 선택
3. 이제 버튼 클릭으로 Sprint별/역할별 필터링 가능

---

## 5. 역할별 작업 분배 시각화

### 5.1 Role 필터링

1. **디자인 → 슬라이서 삽입** → Role 선택
2. Role 슬라이서에서 BE, FE, DB, Infra 중 선택
3. 해당 역할의 작업만 하이라이트됨

### 5.2 Assignee별 대시보드

별도 sheet에 피벗 테이블 생성:

1. **삽입 → 피벗 테이블**
2. Row: Assignee (T1, T2, ...)
3. Columns: Week (Wk 1-2, Wk 3-4, ...)
4. Values: Task Count (개수)

---

## 6. 주간 요약 Sheet 생성 (선택 사항)

### 6.1 Sprint별 주간 집계 Sheet

새로운 Sheet에 주간별 Task 수를 집계:

| Week | 총 Task | 완료 | 진행 중 | 미시작 |
|------|---------|------|--------|--------|
| Day 1 | 8 | 8 | 0 | 0 |
| Day 3 | 10 | 10 | 0 | 0 |
| Wk 3-4 | 25 | ... | ... | ... |
| Wk 15-16 | 5 | ... | ... | ... |

### 6.2 공식 예시

```excel
=COUNTIFS(Sheet1!$C:$C, "Sprint 1", Sheet1!$D:$D, "Wk 3-4")
```

---

## 7. 실전 팁

### 7.1 인쇄 최적화

1. **페이지 레이아웃 → 넓이 → 한 페이지에 맞추기**
2. **페이지 설정 → 여백** → 헤더/푸터에 Sprint 번호 추가
3. **방향:** 가로(Landscape)

### 7.2 진행률 업데이트

매 주(월요일) 다음을 수행:

1. 현재 날짜와 End Date 비교 → **완료**로 표시
2. `=IF(H2<=TODAY(), "완료", IF(I2>=TODAY(), "진행 중", "미시작"))`
3. 조건부 서식 색상 자동 변경

### 7.3 Dependencies 시각화 (선택 사항)

1. K열(Dependencies)에 "101, 104" 형태로 의존성 기록
2. 별도 sheet에 **종속성 매핑 테이블** 생성 후 피벗으로 시각화

### 7.4 대시보드 통합 Sheet (고급)

3개 이상의 Sheet를 하나의 대시보드에 통합:

1. **Dashboard** Sheet 생성
2. 슬라이서으로 Sprint 0~4 필터
3. 위쪽:주간 Task 수 (파이 차트)
4. 아래쪽: Sprint별 Task 목록 (테이블)

---

## 8. 파일 구조

```
nu-Trust/.sisyphus/plans/
├── nus-trust-wbs-gantt.csv    ← 원본 WBS 데이터 (175 tasks, UTF-8-sig)
├── nus-trust-wbs-gantt.xlsx   ← 간트 차트 연동용 Excel (생성/편집)
└── nu-trust-development-workflow.md  ← 원래 WBS Markdown (참고용)
```

CSV는 **수정 금지** (원본). Excel에서 복사하여 작업하세요.

---

## 9. 정기 유지 관리

| 주기 | 작업 | 담당 |
|------|------|------|
| 매주 월요일 | 진행률 업데이트 (완료/진행 중/미시작) | PM |
| Sprint 종료 시 | Sprint 완료 Sheet 이동 (Archive) | PM |
| Sprint Planning | Sprint 0 Day 단위 → Sprint 1+ Week 단위 계획 수립 | CTO |

---

## 10. 자주 묻는 질문 (FAQ)

**Q1. CSV가 Excel에서 깨져서 열립니다.**
→ CSV 파일이 **UTF-8-sig 인코딩(BOM 포함)**으로 작성되었습니다. Excel에서 **데이터 탭 → 텍스트/CSV에서** 메뉴를 사용하고 인코딩을 UTF-8로 선택하세요.

**Q2. 날짜가 YY/MM/DD 형식으로 나옵니다.**
→ 열 서식 → 사용자 지정 → 형식: **YYYY-mm-DD**로 변경하세요.

**Q3. Gantt 차트를 인쇄하면 너무 작게 나옵니다.**
→ 페이지 레이아웃 → 넓이 → **한 페이지에 맞추기** 설정을 사용하세요.

**Q4. 175개 Task를 모두 볼 수 없습니다.**
→ 스크롤 또는 슬라이서 필터로 필요한 Sprint만 선택하세요.

---

*본 가이드는 nu_Trust 프로젝트(개발 워크플로우 v1.0)의 실행을 지원합니다.*
