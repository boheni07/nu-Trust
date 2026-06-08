# nu_Trust — WBS (Work Breakdown Structure)

**Target:** GitLab 이슈/마일스톤 연계용  
**Version:** 1.0  
**Date:** 2026-06-08  
**Project:** (주)엔유비즈 nu_Trust 신뢰 구축 CRM 플랫폼  
**기술 스택:**  
- FE: Vue 3.5 + Vite 8 (JS) → React 18 + TS (Phase 2)  
- BE: Spring Boot 3.2+ / Java 17 / MyBatis / Spring Security(JWT)  
- DB: PostgreSQL 15 / Flyway Migration  
- Infra: AWS(RDS, EC2/ECS, S3), Redis, RabbitMQ, Docker  

---

## 1. Sprint 0 — 환경 구축 (Week 1~2, 2주)

BE/FE 개발 환경 설정, CI/CD 파이프라인, DB 초기화

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 001 | 프로젝트 레포지토리 초기 설정 (GitLab, Branch 전략) | `infra` | P0 |
| 002 | GitLab Auto-DevOps 파이프라인 `.gitlab-ci.yml` 구축 | `infra` | P0 |
| 003 | Docker Compose 환경 구성 (PostgreSQL, Redis, RabbitMQ) | `infra` | P0 |
| 004 | PostgreSQL 스키마 Flyway Migration v001 생성 | `database` | P0 |
| 005 | FE 프로토타입 Git Push → Auto-DevOps 연동 검증 | `frontend` | P1 |

**마일스톤 M0:** Docker Compose로 로컬 환경 3개 서비스(DB, Redis, MQ) 구동, CI 파이프라인 Green

---

## 2. Sprint 1 — Authentication & RBAC (Week 3~4, 2주)

JWT 기반 로그인, 회원가입, BCrypt 비밀번호 해싱, Role 기반 접근 제어

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 101 | Spring Security + JWT 토큰 발급/검증 설정 | `auth` | P0 |
| 102 | 회원가입 API (`POST /api/auth/register`) | `auth` | P0 |
| 103 | 로그인 API (`POST /api/auth/login`), JWT Access/Refresh Token | `auth` | P0 |
| 104 | 토큰 갱신 API (`POST /api/auth/refresh`) | `auth` | P0 |
| 105 |/logout API, Refresh Token Redis 저장 | `auth` | P1 |
| 106 | RBAC Authorization Filter (@PreAuthorize) | `auth` | P0 |
| 107 | FE 로그인/회원가입 페이지 백엔드 연동 | `frontend` | P0 |

**마일스톤 M1:** ADMIN/SUPPORT/CUSTOMER 역할별 로그인 → 토큰 기반 접근 가능

---

## 3. Sprint 2 — Company & User Management (Week 5~6, 2주)

회사 등록/조회, 사용자 CRUD, Role별 company_id 스코프

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 201 | Company CRUD API (`/api/companies`) | `admin` | P0 |
| 202 | User CRUD API (`/api/users`) — Role별 company 스코프 | `admin` | P0 |
| 203 | User 상태 관리 (ACTIVE / INACTIVE / DISABLED) | `admin` | P0 |
| 204 | ADMIN vs COMPANY_ADMIN 권한 분리 | `admin` | P0 |
| 205 | FE CompaniesView 백엔드 연동 | `frontend` | P0 |
| 206 | FE UsersView 백엔드 연동 | `frontend` | P0 |

**마일스톤 M2:** 개발사/고객사 등록, 사용자 CRUD, 역할별 소속company 필터링

---

## 4. Sprint 3 — Project Management (Week 7~8, 2주)

프로젝트 CRUD, Project Manager / Support Manager 배정

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 301 | Project CRUD API (`/api/projects`) — Soft Delete | `project` | P0 |
| 302 | Project Manager 배정 API (`POST /api/projects/{id}/managers`) | `project` | P0 |
| 303 | Project Support Manager 배정 API | `project` | P0 |
| 304 | Project 상태 관리 (ACTIVE / COMPLETED / ON_HOLD / CANCELLED) | `project` | P1 |
| 305 | Project 상세 조회 API (관리자/멤버 정보 포함) | `project` | P1 |
| 306 | FE ProjectsView 백엔드 연동 | `frontend` | P0 |

**마일스톤 M3:** 프로젝트 생성·관리·멤버 배정 완료

---

## 5. Sprint 4 — Ticket Core (Week 9~11, 3주)

티켓 생성/리스트/상세, 상태 전이 워크플로우, 댓글/이력

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 401 | Ticket CRUD API (`/api/tickets`) | `ticket` | P0 |
| 402 | Ticket 상태 전이 엔진 (REGISTERED → RECEIVED → PROCESSING → COMPLETED) | `ticket` | P0 |
| 403 | Ticket 목록 API — 상태/유형/프로젝트/assignee 필터 | `ticket` | P0 |
| 404 | Ticket 상세 API (상태리본, Action 버튼, 이력 포함) | `ticket` | P0 |
| 405 | Ticket Comment API (`/api/tickets/{id}/comments`) | `ticket` | P0 |
| 406 | Ticket 이력 Audit Trail API (`/api/tickets/{id}/history`) | `ticket` | P0 |
| 407 | Support Manager 배정 API (`PATCH /api/tickets/{id}/assign`) | `ticket` | P0 |
| 408 | 진행 상황 업데이트 API (`PATCH /api/tickets/{id}/progress`) | `ticket` | P1 |
| 409 | 완료 요청/승인/반려 API (`PATCH /api/tickets/{id}/complete`) | `ticket` | P0 |
| 410 | 지연(Delayed) 상태 관리 API | `ticket` | P1 |
| 411 | FE TicketList 백엔드 연동 | `frontend` | P0 |
| 412 | FE TicketDetail 백엔드 연동 (상태리본, 액션버튼, 이력타임라인) | `frontend` | P0 |

**마일스톤 M4:** 티켓 생명주기(생성→처리→완료) 전체 워크플로우 동작

---

## 6. Sprint 5 — Real-time Chat & Notification (Week 12~14, 3주)

WebSocket(STOMP/SockJS) 기반 실시간 채팅, 알림, 백그라운드 스케줄러

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 501 | WebSocket config (STOMP over SockJS) | `chat` | P0 |
| 502 | Ticket 내 채팅 메시지 CRUD (`/ws/app/ticket-{id}`) | `chat` | P0 |
| 503 | 채팅 메시지 WebSocket 브로드캐스팅 (RabbitMQ 연동) | `chat` | P0 |
| 504 |typing Indicator WebSocket 전송 | `chat` | P1 |
| 505 | 파일 첨부 메시지 지원 | `chat` | P1 |
| 506 | 알림 구독 API (새 티켓 배정, 상태 변경) | `notification` | P0 |
| 507 | RabbitMQ 비동기 알림 큐 | `notification` | P0 |
| 508 | Spring @Scheduled — 마감일 만료 감지 Cron | `backend` | P1 |
| 509 | FE Chat 컴포넌트 WebSocket 연동 | `frontend` | P0 |

**마일스톤 M5:** 티켓 내 실시간 채팅·알림 동작, 지연 감지 Cron 자동 실행

---

## 7. Sprint 6 — Admin Panel & System Settings (Week 15, 1주)

시스템 설정, 공지사항, 대시보드 통계

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 601 | System Settings API (비즈니스 캘린더, 공휴일) | `admin` | P1 |
| 602 | 대시보드 통계 API (티켓 수, 상태별 분포, SLA 준수율) | `dashboard` | P0 |
| 603 | 공지사항 API | `admin` | P2 |
| 604 | FE DashboardView 백엔드 연동 | `frontend` | P0 |
| 605 | FE SettingsView 백엔드 연동 | `frontend` | P2 |

**마일스톤 M6:** 대시보드 실시간 통계, 시스템 설정 UI 연동

---

## 8. Sprint 7 — QA/UAT/Deploy (Week 16, 1주)

테스트 전역, UAT, 스테이징/프로덕션 배포

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 701 | 통합 End-to-End 테스트 (API + FE) | `qa` | P0 |
| 702 | UAT (사용자 acceptance 테스트) — 피드백 반영 | `qa` | P0 |
| 703 | 스테이징 배포 (stage.nutrust.kr) | `deploy` | P0 |
| 704 | 프로덕션 배포 (nutrust.kr) | `deploy` | P0 |
| 705 | 성능/보안 테스트 (Rate Limiting, JWT 검증) | `security` | P1 |

**마일스톤 M7:** 프로덕션 1인차 배포 완료

---

## 9. Migration (Phase 2 — 별도 스프린트)

| WBS ID | 항목 | GitLab Label | Priority |
|--------|------|-------------|----------|
| 801 | FE 리팩토링: Vue 3(JS) → React 18 + TypeScript | `refactor` | P1 |
| 802 | FE 상태관리: Zustand 도입 | `refactor` | P1 |
| 803 | TailwindCSS + daisyUI 도입 | `refactor` | P1 |
| 804 | API Gateway (Kong/NGINX) 스테이징/프로덕션 설정 | `infra` | P1 |
| 805 | AWS ECS/ECS Fargate 배포 마이그레이션 | `infra` | P1 |
| 806 | Prometheus + Grafana 모니터링 연동 | `infra` | P1 |

---

## 10. GitLab Label 가이드

| Label | Color | 사용 |
|-------|-------|------|
| `auth` | #d73a4a | 인증/인가 관련 |
| `admin` | #a2eeef | 관리자 패널 |
| `project` | #7057ff | 프로젝트 관리 |
| `ticket` | #008672 | 티켓 핵심 로직 |
| `chat` | #ffd300 | 실시간 채팅 |
| `notification` | #fbca04 | 알림 |
| `frontend` | #1d760d | 프론트엔드 |
| `backend` | #d4c5f9 | 백엔드 공통 |
| `infra` | #cfd3d7 | 환경/배포 |
| `qa` | #f2f2f2 | 테스트 |
| `security` | #d73a4a | 보안 |
| `refactor` | #bfd4f2 | 리팩토링/마이그레이션 |
| `deploy` | #e6e6e6 | 배포 |

**Priority 가이드:**
- **P0:** Must have — Sprint 필수 완료
- **P1:** Should have — 가능하면 완료
- **P2:** Could have — 여유 시

---

## 11. GitLab Milestone 가이드

| 마일스톤 | 기간 | 범위 |
|----------|------|------|
| Milestone 0 | Week 1~2 | Sprint 0 — 환경 구축 |
| Milestone 1 | Week 3~4 | Sprint 1 — Authentication & RBAC |
| Milestone 2 | Week 5~6 | Sprint 2 — Company & User Management |
| Milestone 3 | Week 7~8 | Sprint 3 — Project Management |
| Milestone 4 | Week 9~11 | Sprint 4 — Ticket Core |
| Milestone 5 | Week 12~14 | Sprint 5 — Real-time Chat & Notification |
| Milestone 6 | Week 15 | Sprint 6 — Admin Panel & System Settings |
| Milestone 7 | Week 16 | Sprint 7 — QA/UAT/Deploy |
| Milestone 8 | Phase 2 별도 | Migration (React+TS, Infra 확장) |

---

## 12. Sprint별 WBS 항목 수 요약

| Sprint | WBS 항목 수 | 기간 | 핵심 Deliverable |
|--------|------------|------|-----------------|
| S0 | 5 | 2주 | Docker/CI/DB 초기화 |
| S1 | 7 | 2주 | JWT 로그인 + RBAC |
| S2 | 6 | 2주 | Company/User CRUD |
| S3 | 6 | 2주 | Project CRUD + 매니저 배정 |
| S4 | 12 | 3주 | 티켓 워크플로우 전체 |
| S5 | 9 | 3주 | 실시간 채팅 + 알림 |
| S6 | 5 | 1주 | 대시보드 + 설정 |
| S7 | 5 | 1주 | QA/UAT/배포 |
| **합계** | **55** | **16주** | — |
| Phase 2 | 6 | 별도 | Migration |
