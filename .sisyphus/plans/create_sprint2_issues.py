"""Create Sprint 2 GitLab Issues"""
import requests

HEADERS = {"PRIVATE-TOKEN": "glpat-pk9efo5ds39UU8bVydlzpm86MQp1OnoH.01.0w1kr24th"}
BASE = "http://192.168.0.245/api/v4"
PID = "boheni%2Fnu-trust"

SPRINT2 = [
    {
        "wbs": "201",
        "title": "[WBS:201] Company CRUD API",
        "labels": "admin,P1",
        "milestone_id": 32,
        "description": "## Summary\n회사 등록/조회/수정/삭제 API.\n\n## Acceptance Criteria\n- [ ] GET /api/companies - 회사 목록 조회\n- [ ] POST /api/companies - 회사 생성\n- [ ] GET /api/companies/{id} - 회사 상세\n- [ ] PUT /api/companies/{id} - 회사 수정\n- [ ] PATCH /api/companies/{id}/status - 상태 변경",
    },
    {
        "wbs": "202",
        "title": "[WBS:202] User CRUD API - Role별 company 스코프",
        "labels": "admin,P1",
        "milestone_id": 32,
        "description": "## Summary\n사용자 CRUD. COMPANY_ADMIN은 소속 company만, ADMIN은 전역 조회.\n\n## Acceptance Criteria\n- [ ] GET /api/users - 사용자 목록 (role별 scope 필터)\n- [ ] POST /api/users - 사용자 생성\n- [ ] GET /api/users/{id} - 사용자 상세\n- [ ] PUT /api/users/{id} - 사용자 수정",
    },
    {
        "wbs": "203",
        "title": "[WBS:203] User 상태 관리 (ACTIVE / INACTIVE / DISABLED)",
        "labels": "admin,P1",
        "milestone_id": 32,
        "description": "## Summary\nUser 엔티티의 상태 enum 관리.\n\n## Acceptance Criteria\n- [ ] Status enum 정의 (ACTIVE, INACTIVE, DISABLED)\n- [ ] 상태 변경 API\n- [ ] 상태별 로그인 차단 로직",
    },
    {
        "wbs": "204",
        "title": "[WBS:204] ADMIN vs COMPANY_ADMIN 권한 분리",
        "labels": "admin,P1",
        "milestone_id": 32,
        "description": "## Summary\nADMIN: 전역 전체 관리. COMPANY_ADMIN: 소속 company만 관리.\n\n## Acceptance Criteria\n- [ ] @PreAuthorize 규칙 적용\n- [ ] CompanyRepository에 scope 조건 추가",
    },
    {
        "wbs": "205",
        "title": "[WBS:205] FE CompaniesView 백엔드 연동",
        "labels": "frontend,P1",
        "milestone_id": 32,
        "description": "## Summary\nVue 3 프론트엔드 CompaniesView 컴포넌트 백엔드 API 연동.\n\n## Acceptance Criteria\n- [ ] GET /api/companies 목록 조회\n- [ ] POST /api/companies 등록\n- [ ] 상태 토글 버튼 연동",
    },
    {
        "wbs": "206",
        "title": "[WBS:206] FE UsersView 백엔드 연동",
        "labels": "frontend,P1",
        "milestone_id": 32,
        "description": "## Summary\nVue 3 프론트엔드 UsersView 컴포넌트 백엔드 API 연동.\n\n## Acceptance Criteria\n- [ ] GET /api/users 목록 조회\n- [ ] ROLE별 scope 필터링 적용\n- [ ] 상태 변경 버튼 연동",
    },
]

created = []
for item in SPRINT2:
    r = requests.post(
        f"{BASE}/projects/{PID}/issues",
        headers=HEADERS,
        json={
            "title": item["title"],
            "description": item["description"],
            "labels": item["labels"],
            "milestone_id": item["milestone_id"],
        },
    )
    d = r.json()
    iid = d.get("iid")
    created.append(f"WBS:{item['wbs']} -> Issue #{iid}")
    print(f"  Created: {item['title']} -> #{iid}")

print("\nSummary: " + ", ".join(created))
