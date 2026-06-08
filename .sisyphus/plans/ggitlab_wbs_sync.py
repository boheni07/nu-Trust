"""
WBS → GitLab 이슈/마일스톤 자동 연동 스크립트
===============================================
WBS.md에 정의된 Sprint 마일스톤과 WBS 항목을 GitLab 프로젝트에
마일스톤과 이슈로 자동 생성합니다.

사용법:
    python3 ggitlab_wbs_sync.py

필수:
    - GitLab API token (PRIVATE-TOKEN)
    - requests 라이브러리
"""

import json
import requests
from datetime import datetime, timedelta

# ─── Configuration ───────────────────────────────────────────────
GITLAB_URL = "http://192.168.0.245/api/v4"
PROJECT_ID = 27  # boheni/nu-trust
TOKEN = "glpat-pk9efo5ds39UU8bVydlzpm86MQp1OnoH.01.0w1kr24th"
PROJECT_PATH = "boheni/nu-trust"
PROJECT_URL = "http://192.168.0.245/boheni/nu-trust"

HEADERS = {"PRIVATE-TOKEN": TOKEN}

PROJECT_START = datetime(2026, 6, 8)
PROJECT_END = datetime(2026, 9, 27)

# ─── Sprint / Milestone Definition ───────────────────────────────
SPRINTS = [
    {
        "milestone_id": 0,
        "milestone_title": "Milestone 0 — Sprint 0: 환경 구축",
        "week_start": 1,
        "duration_weeks": 2,
        "description": "Sprint 0 — 환경 구축 (Week 1~2, 2주)\n\nBE/FE 개발 환경 설정, CI/CD 파이프라인, DB 초기화\n\n**마일스톤 M0:** Docker Compose로 로컬 환경 3개 서비스(DB, Redis, MQ) 구동, CI 파이프라인 Green",
        "items": [
            {"wbs": "001", "title": "프로젝트 레포지토리 초기 설정 (GitLab, Branch 전략)",
             "labels": "infra", "priority": "P0"},
            {"wbs": "002", "title": "GitLab Auto-DevOps 파이프라인 `.gitlab-ci.yml` 구축",
             "labels": "infra", "priority": "P0"},
            {"wbs": "003", "title": "Docker Compose 환경 구성 (PostgreSQL, Redis, RabbitMQ)",
             "labels": "infra", "priority": "P0"},
            {"wbs": "004", "title": "PostgreSQL 스키마 Flyway Migration v001 생성",
             "labels": "database", "priority": "P0"},
            {"wbs": "005", "title": "FE 프로토타입 Git Push → Auto-DevOps 연동 검증",
             "labels": "frontend", "priority": "P1"},
        ],
    },
    {
        "milestone_id": 1,
        "milestone_title": "Milestone 1 — Sprint 1: Authentication & RBAC",
        "week_start": 3,
        "duration_weeks": 2,
        "description": "Sprint 1 — Authentication & RBAC (Week 3~4, 2주)\n\nJWT 기반 로그인, 회원가입, BCrypt 비밀번호 해싱, Role 기반 접근 제어\n\n**마일스톤 M1:** ADMIN/SUPPORT/CUSTOMER 역할별 로그인 → 토큰 기반 접근 가능",
        "items": [
            {"wbs": "101", "title": "Spring Security + JWT 토큰 발급/검증 설정",
             "labels": "auth", "priority": "P0"},
            {"wbs": "102", "title": "회원가입 API (`POST /api/auth/register`)",
             "labels": "auth", "priority": "P0"},
            {"wbs": "103", "title": "로그인 API (`POST /api/auth/login`), JWT Access/Refresh Token",
             "labels": "auth", "priority": "P0"},
            {"wbs": "104", "title": "토큰 갱신 API (`POST /api/auth/refresh`)",
             "labels": "auth", "priority": "P0"},
            {"wbs": "105", "title": "/logout API, Refresh Token Redis 저장",
             "labels": "auth", "priority": "P1"},
            {"wbs": "106", "title": "RBAC Authorization Filter (@PreAuthorize)",
             "labels": "auth", "priority": "P0"},
            {"wbs": "107", "title": "FE 로그인/회원가입 페이지 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
        ],
    },
    {
        "milestone_id": 2,
        "milestone_title": "Milestone 2 — Sprint 2: Company & User Management",
        "week_start": 5,
        "duration_weeks": 2,
        "description": "Sprint 2 — Company & User Management (Week 5~6, 2주)\n\n회사 등록/조회, 사용자 CRUD, Role별 company_id 스코프\n\n**마일스톤 M2:** 개발사/고객사 등록, 사용자 CRUD, 역할별 소속company 필터링",
        "items": [
            {"wbs": "201", "title": "Company CRUD API (`/api/companies`)",
             "labels": "admin", "priority": "P0"},
            {"wbs": "202", "title": "User CRUD API (`/api/users`) — Role별 company 스코프",
             "labels": "admin", "priority": "P0"},
            {"wbs": "203", "title": "User 상태 관리 (ACTIVE / INACTIVE / DISABLED)",
             "labels": "admin", "priority": "P0"},
            {"wbs": "204", "title": "ADMIN vs COMPANY_ADMIN 권한 분리",
             "labels": "admin", "priority": "P0"},
            {"wbs": "205", "title": "FE CompaniesView 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
            {"wbs": "206", "title": "FE UsersView 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
        ],
    },
    {
        "milestone_id": 3,
        "milestone_title": "Milestone 3 — Sprint 3: Project Management",
        "week_start": 7,
        "duration_weeks": 2,
        "description": "Sprint 3 — Project Management (Week 7~8, 2주)\n\n프로젝트 CRUD, Project Manager / Support Manager 배정\n\n**마일스톤 M3:** 프로젝트 생성·관리·멤버 배정 완료",
        "items": [
            {"wbs": "301", "title": "Project CRUD API (`/api/projects`) — Soft Delete",
             "labels": "project", "priority": "P0"},
            {"wbs": "302", "title": "Project Manager 배정 API (`POST /api/projects/{id}/managers`)",
             "labels": "project", "priority": "P0"},
            {"wbs": "303", "title": "Project Support Manager 배정 API",
             "labels": "project", "priority": "P0"},
            {"wbs": "304", "title": "Project 상태 관리 (ACTIVE / COMPLETED / ON_HOLD / CANCELLED)",
             "labels": "project", "priority": "P1"},
            {"wbs": "305", "title": "Project 상세 조회 API (관리자/멤버 정보 포함)",
             "labels": "project", "priority": "P1"},
            {"wbs": "306", "title": "FE ProjectsView 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
        ],
    },
    {
        "milestone_id": 4,
        "milestone_title": "Milestone 4 — Sprint 4: Ticket Core",
        "week_start": 9,
        "duration_weeks": 3,
        "description": "Sprint 4 — Ticket Core (Week 9~11, 3주)\n\n티켓 생성/리스트/상세, 상태 전이 워크플로우, 댓글/이력\n\n**마일스톤 M4:** 티켓 생명주기(생성→처리→완료) 전체 워크플로우 동작",
        "items": [
            {"wbs": "401", "title": "Ticket CRUD API (`/api/tickets`)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "402", "title": "Ticket 상태 전이 엔진 (REGISTERED → RECEIVED → PROCESSING → COMPLETED)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "403", "title": "Ticket 목록 API — 상태/유형/프로젝트/assignee 필터",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "404", "title": "Ticket 상세 API (상태리본, Action 버튼, 이력 포함)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "405", "title": "Ticket Comment API (`/api/tickets/{id}/comments`)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "406", "title": "Ticket 이력 Audit Trail API (`/api/tickets/{id}/history`)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "407", "title": "Support Manager 배정 API (`PATCH /api/tickets/{id}/assign`)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "408", "title": "진행 상태 업데이트 API (`PATCH /api/tickets/{id}/progress`)",
             "labels": "ticket", "priority": "P1"},
            {"wbs": "409", "title": "완료 요청/승인/반려 API (`PATCH /api/tickets/{id}/complete`)",
             "labels": "ticket", "priority": "P0"},
            {"wbs": "410", "title": "지연(Delayed) 상태 관리 API",
             "labels": "ticket", "priority": "P1"},
            {"wbs": "411", "title": "FE TicketList 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
            {"wbs": "412", "title": "FE TicketDetail 백엔드 연동 (상태리본, 액션버튼, 이력타임라인)",
             "labels": "frontend", "priority": "P0"},
        ],
    },
    {
        "milestone_id": 5,
        "milestone_title": "Milestone 5 — Sprint 5: Real-time Chat & Notification",
        "week_start": 12,
        "duration_weeks": 3,
        "description": "Sprint 5 — Real-time Chat & Notification (Week 12~14, 3주)\n\nWebSocket(STOMP/SockJS) 기반 실시간 채팅, 알림, 백그라운드 스케줄러\n\n**마일스톤 M5:** 티켓 내 실시간 채팅·알림 동작, 지연 감지 Cron 자동 실행",
        "items": [
            {"wbs": "501", "title": "WebSocket config (STOMP over SockJS)",
             "labels": "chat", "priority": "P0"},
            {"wbs": "502", "title": "Ticket 내 채팅 메시지 CRUD (`/ws/app/ticket-{id}`)",
             "labels": "chat", "priority": "P0"},
            {"wbs": "503", "title": "채팅 메시지 WebSocket 브로드캐스팅 (RabbitMQ 연동)",
             "labels": "chat", "priority": "P0"},
            {"wbs": "504", "title": "typing Indicator WebSocket 전송",
             "labels": "chat", "priority": "P1"},
            {"wbs": "505", "title": "파일 첨부 메시지 지원",
             "labels": "chat", "priority": "P1"},
            {"wbs": "506", "title": "알림 구독 API (새 티켓 배정, 상태 변경)",
             "labels": "notification", "priority": "P0"},
            {"wbs": "507", "title": "RabbitMQ 비동기 알림 큐",
             "labels": "notification", "priority": "P0"},
            {"wbs": "508", "title": "Spring @Scheduled — 마감일 만료 감지 Cron",
             "labels": "backend", "priority": "P1"},
            {"wbs": "509", "title": "FE Chat 컴포넌트 WebSocket 연동",
             "labels": "frontend", "priority": "P0"},
        ],
    },
    {
        "milestone_id": 6,
        "milestone_title": "Milestone 6 — Sprint 6: Admin Panel & System Settings",
        "week_start": 15,
        "duration_weeks": 1,
        "description": "Sprint 6 — Admin Panel & System Settings (Week 15, 1주)\n\n시스템 설정, 공지사항, 대시보드 통계\n\n**마일스톤 M6:** 대시보드 실시간 통계, 시스템 설정 UI 연동",
        "items": [
            {"wbs": "601", "title": "System Settings API (비즈니스 캘린더, 공휴일)",
             "labels": "admin", "priority": "P1"},
            {"wbs": "602", "title": "대시보드 통계 API (티켓 수, 상태별 분포, SLA 준수율)",
             "labels": "dashboard", "priority": "P0"},
            {"wbs": "603", "title": "공지사항 API",
             "labels": "admin", "priority": "P2"},
            {"wbs": "604", "title": "FE DashboardView 백엔드 연동",
             "labels": "frontend", "priority": "P0"},
            {"wbs": "605", "title": "FE SettingsView 백엔드 연동",
             "labels": "frontend", "priority": "P2"},
        ],
    },
    {
        "milestone_id": 7,
        "milestone_title": "Milestone 7 — Sprint 7: QA/UAT/Deploy",
        "week_start": 16,
        "duration_weeks": 1,
        "description": "Sprint 7 — QA/UAT/Deploy (Week 16, 1주)\n\n테스트 전역, UAT, 스테이징/프로덕션 배포\n\n**마일스톤 M7:** 프로덕션 1인차 배포 완료",
        "items": [
            {"wbs": "701", "title": "통합 End-to-End 테스트 (API + FE)",
             "labels": "qa", "priority": "P0"},
            {"wbs": "702", "title": "UAT (사용자 Acceptance 테스트) — 피드백 반영",
             "labels": "qa", "priority": "P0"},
            {"wbs": "703", "title": "스테이징 배포 (stage.nutrust.kr)",
             "labels": "deploy", "priority": "P0"},
            {"wbs": "704", "title": "프로덕션 배포 (nutrust.kr)",
             "labels": "deploy", "priority": "P0"},
            {"wbs": "705", "title": "성능/보안 테스트 (Rate Limiting, JWT 검증)",
             "labels": "security", "priority": "P1"},
        ],
    },
    {
        "milestone_id": 8,
        "milestone_title": "Milestone 8 — Phase 2: Migration",
        "week_start": 17,
        "duration_weeks": 0,
        "description": "Migration (Phase 2 — 별도 스프린트)\n\nVue 3(JS) → React 18 + TypeScript, Infra 확장",
        "items": [
            {"wbs": "801", "title": "FE 리팩토링: Vue 3(JS) → React 18 + TypeScript",
             "labels": "refactor", "priority": "P1"},
            {"wbs": "802", "title": "FE 상태관리: Zustand 도입",
             "labels": "refactor", "priority": "P1"},
            {"wbs": "803", "title": "TailwindCSS + daisyUI 도입",
             "labels": "refactor", "priority": "P1"},
            {"wbs": "804", "title": "API Gateway (Kong/NGINX) 스테이징/프로덕션 설정",
             "labels": "infra", "priority": "P1"},
            {"wbs": "805", "title": "AWS ECS/ECS Fargate 배포 마이그레이션",
             "labels": "infra", "priority": "P1"},
            {"wbs": "806", "title": "Prometheus + Grafana 모니터링 연동",
             "labels": "infra", "priority": "P1"},
        ],
    },
]

# WBS label colors (from WBS.md Label 가이드)
LABEL_COLORS = {
    "infra": "#cfd3d7",
    "database": "#428bcos",  # will override below
    "auth": "#d73a4a",
    "admin": "#a2eeef",
    "project": "#7057ff",
    "ticket": "#008672",
    "chat": "#ffd300",
    "notification": "#fbca04",
    "frontend": "#1d760d",
    "backend": "#d4c5f9",
    "qa": "#f2f2f2",
    "security": "#d73a4a",
    "deploy": "#e6e6e6",
    "refactor": "#bfd4f2",
    "dashboard": "#c5caff",
}
LABEL_COLORS["database"] = "#7057ff"  # use project-like color


def api(method, endpoint, json_data=None, params=None):
    """GitLab API 호출 (GET/POST) with error handling."""
    url = f"{GITLAB_URL}/{endpoint.lstrip('/')}"
    
    if method == "GET":
        resp = requests.get(url, headers=HEADERS, params=params, timeout=30)
    elif method == "POST":
        resp = requests.post(url, headers=HEADERS, json=json_data, timeout=30)
    else:
        raise ValueError(f"Unsupported method: {method}")
    
    if resp.status_code in (200, 201):
        return resp.json()
    elif resp.status_code == 404:
        return None
    else:
        print(f"  ❌ API {method} {endpoint} → HTTP {resp.status_code}")
        print(f"     Response: {resp.text[:300]}")
        return None


def calc_dates(week_start, duration_weeks):
    """Week 번호 → 시작/종료일 계산."""
    start = PROJECT_START + timedelta(days=(week_start - 1) * 7)
    if duration_weeks == 0:
        return start.strftime("%Y-%m-%d"), start.strftime("%Y-%m-%d")
    end = start + timedelta(days=duration_weeks * 7) - timedelta(days=1)
    return start.strftime("%Y-%m-%d"), end.strftime("%Y-%m-%d")


def create_milestones():
    """모든 마일스톤을 GitLab에 생성."""
    print("=" * 60)
    print("📌 GitLab 마일스톤 생성")
    print("=" * 60)
    
    milestone_ids = {}
    
    for sprint in SPRINTS:
        mid = sprint["milestone_id"]
        title = sprint["milestone_title"]
        
        # 이미 존재하면 건너뛰기
        existing = api("GET", f"projects/{PROJECT_ID}/milestones")
        for m in (existing or []):
            if m["title"] == title:
                milestone_ids[mid] = m["id"]
                print(f"  ⏭️  Milestone {mid} 이미 존재: {m['id']}: {m['title']}")
                break
        else:
            week_start = sprint["week_start"]
            duration_weeks = sprint["duration_weeks"]
            due_date, _ = calc_dates(week_start + duration_weeks, 0) if duration_weeks > 0 else (calc_dates(week_start, 0)[0], None)
            
            created = api("POST", f"projects/{PROJECT_ID}/milestones", {
                "title": title,
                "description": sprint["description"],
                "due_date": due_date,
            })
            
            if created:
                milestone_ids[mid] = created["id"]
                print(f"  ✅ Milestone {mid} → ID:{created['id']}")
            else:
                print(f"  ❌ Milestone {mid} 생성 실패")
    
    print(f"\n  마일스톤 총 {len(milestone_ids)}개 매핑 완료")
    return milestone_ids


def create_issues(milestone_ids):
    """所有 WBS 항목을 이슈로 생성."""
    print("=" * 60)
    print("📋 GitLab 이슈 생성")
    print("=" * 60)
    
    # 기존 이슈 제목 목록 (중복 방지)
    existing_issues = api("GET", f"projects/{PROJECT_ID}/issues", 
                          params={"per_page": 100, "state": "all"}) or []
    existing_titles = {i["title"].split(" [WBS:")[0].strip() for i in existing_issues}
    
    total_created = 0
    total_skipped = 0
    errors = []
    
    for sprint in SPRINTS:
        sprint_num = sprint["milestone_id"]
        milestone_id = milestone_ids.get(sprint_num)
        if not milestone_id:
            print(f"  ⚠️  Milestone {sprint_num} 없음 — 이슈 생성 건너뜀")
            continue
        
        week_start = sprint["week_start"]
        duration_weeks = sprint["duration_weeks"]
        due_date, _ = calc_dates(week_start + duration_weeks, 0) if duration_weeks > 0 else (calc_dates(week_start, 0)[0], None)
        
        for item in sprint["items"]:
            title_with_wbs = f"[WBS:{item['wbs']}] {item['title']}"
            title_plain = item['title']
            
            # 중복 체크
            if title_plain in existing_titles:
                total_skipped += 1
                continue
            
            # WBS description 구성
            description = f"""### WBS ID: {item['wbs']}
### Priority: {item['priority']}
### Sprint: Milestone {sprint_num}

---

**기능 설명:**

{sprint['description'].split('\n\n')[0]}

**마일스톤 달성 목표:**
{sprint['description'].split('\n\n')[-1]}
"""
            
            body = {
                "title": title_with_wbs,
                "description": description,
                "milestone_id": milestone_id,
                "due_date": due_date,
                "labels": [item["labels"], item["priority"]],
                "issue_type": "task",  # GitLab default
            }
            
            created = api("POST", f"projects/{PROJECT_ID}/issues", body)
            
            if created:
                total_created += 1
                issue_url = f"{PROJECT_URL}/-/issues/{created['iid']}"
                print(f"  ✅ [{item['wbs']}] {item['title'][:50]}... → #{created['iid']}")
                existing_titles.add(title_plain)
            else:
                errors.append(f"[{item['wbs']}] {item['title']}")
                print(f"  ❌ [{item['wbs']}] {item['title'][:50]}... 생성 실패")
    
    print(f"\n  생성: {total_created}, 건너뜀(중복): {total_skipped}")
    if errors:
        print(f"  ❌ 실패 {len(errors)}개: {', '.join(errors[:5])}")
    
    return total_created


def main():
    print("nu_Trust WBS → GitLab 동기화 시작")
    print(f"  GitLab: {PROJECT_URL}")
    print(f"  프로젝트: {PROJECT_ID}")
    print()
    
    milestone_ids = create_milestones()
    print()
    create_issues(milestone_ids)
    
    print(f"\n🎉 완료!")
    print(f"   GitLab에서 확인: {PROJECT_URL}/-/milestones")


if __name__ == "__main__":
    main()
