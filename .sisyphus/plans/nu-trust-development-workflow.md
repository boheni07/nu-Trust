# nu_Trust 개발 워크플로우 가이드

**Version:** 1.0
**Date:** 2026년 5월 31일
**Company:** (주)엔유비즈

---

## 1. 데이터명세 (Document Purpose)

이 문서는 nu_Trust 신뢰 구축 플랫폼의 개발 작업 흐름(Development Workflow)을 체계적으로 정의한 문서입니다. 본 문서는 실제 개발 팀이 실행 가능할 정도로 상세하게 작성되어 있으며, 다음 정보를 제공합니다:

- **Sprint 중심의 WBS (Work Breakdown Structure)**: 16주 개발 주기를 Sprint 0~4로 분할
- **팀 역할 및 RACI 매트릭스**: 6인 개발 팀의 역할 정의 및 책임 구분
- **기능별 작업 매핑**: Auth/RBAC, Ticket 워크플로우, 실시간 채팅, 카드 관리, Admin Panel
- **마일스톤 및 QA 기준**: 각 Sprint 종료 시 검증 항목
- **위험 관리**: 주요 리스크와 완화 전략
- **Git 워크플로우**: Git Flow, PR 체크리스트, 커밋 컨벤션, 릴리즈 과정

---

## 2. 프로젝트 개요 및 기술 스택

### 2.0 CTO 검증 스프레토리 (v1.1 — 2025-05-31)

본 워크플로우 가이드는 (주)엔유비즈 CTO 팀의 기술 검수를 거쳐 다음과 같이 보완되었습니다:

- **방법론 명확화**: Sprint 0(환경 구축)에만 Day 단위 계획을 유지, Sprint 1~4는Week 단위로 계획 격상. Day 단위는 매 Sprint Planning 회의에서 결정하는 Agile 원칙 준수.
- **작업 번호 체계**: 전역 고유 ID 체계로 통일. `NNN` 형식 (001~999), Sprint별 재사용 금지.
- **인력 분담 최적화**: T4(Senior BE)의 overload 해결을 위해 T5(Middle BE)가 BE 로직 구현에 적극 참여. 기존 DB/Infra전담 T5가 REST API 구현까지 담당.
- **Sprint 4 확장**: 2주 → 3주 (총 16주). QA/UAT/Deploy에 충분한 시간 확보.
- **기술 스택 확정**: React 18 + Vite + Zustand. Next.js는 Phase 2(세부 고려).
- **MBG 사용 가이드**: Flyway Migration이 DB 버전 관리를 우선, MBG는 개발 보조용(prototype, DB 스키마 검증용). 프로덕션 커밋은 수동 조정된 Entity/Mapper 적용.

---

### 2.1 프로젝트 목적

nu_Trust (신뢰 구축 플랫폼)는 (주)엔유비즈, SW 개발 기업에 특화되어 설계된 클라이언트 관계 관리 플랫폼입니다. 이 플랫폼은 체계적이고 투명하며 추적 가능한 통신 체계(Smart Communication System)를 제공함으로써, 개발 팀과 클라이언트 간의 격차를 해소합니다.

**핵심 철학:** 클라이언트와 개발 기업 간의 모든 상호작용(Interaction)은 공식적으로 기록됩니다. 클라이언트가 제출한 이슈(Issue)는 티켓(Ticket)으로 변환되며, 이 티켓은 명확한 처리 워크플로우(Workflow)를 거칩니다. 실시간 채팅과 스레딩 타입 댓글은 양측 간의 통신을 지속 가능하게 유지합니다. 이렇게 누적된 상호작용의 기록이 신뢰의 기반(A foundation of trust)이 됩니다.

**주요 목표:**
- 클라이언트가 이슈(Issue)를 공식적인 티켓(Ticket) 시스템을 통해 제출하고 추적할 수 있도록 지원
- 개발팀이 요청(Request)을 처리하기 위한 구조화된 워크플로우(Workflow)를 보유
- 각 티켓(Ticket) 내부에서 실시간 통신(Real-time Communication) 기능 제공
- 신뢰와 책임(Audit Trail)을 강화하는 완전한 이력 추적(Audit Trail) 체계 구축
- 회사(Company), 사용자(User), 프로젝트(Project) 관리를 위한 관리자 패널(Admin Panel) 제공

### 2.2 대상 사용자 (Target Users) & RBAC

| 사용자 타입 | 설명 | 권한 |
|-----------|------|------|
| ADMIN (시스템 관리자) | 플랫폼 관리자 | 전체 시스템 구성, User 관리 |
| COMPANY_ADMIN (회사 관리자) | 특정 개발사 내 관리자 | 개발사 내 사용자 및 프로젝트 관리 |
| SUPPORT (지원_staff) | 티켓을 처리하는 개발팀 멤버 | 티켓 관리, 배정, 처리, 응답 |
| CUSTOMER (클라이언트) | 이슈를 제출하는 회사 측 인사 | 티켓 생성, 댓글 작성, 자신 관련 티켓 목록 조회 |

**Role Hierarchy:** ADMIN > COMPANY_ADMIN > SUPPORT > CUSTOMER

**company_id 규칙 (역할별 소속):**

| Role | company_id 소속 회사 타입 | 설명 |
|------|--------------------------|------|
| ADMIN | DEVELOPER | 플랫폼 (엔유비즈) 전용 관리자. DEVELOPER Company만 등록 가능 |
| COMPANY_ADMIN | DEVELOPER | 개발사 내 관리자. 해당 DEVELOPER Company만 관리 |
| USER (SUPPORT) | DEVELOPER | 개발팀 멤버. DEVELOPER Company 소속 |
| USER (CUSTOMER) | CLIENT | 클라이언트 측 인사. CLIENT Company 소속 |

### 2.3 기술 스택 (Tech Stack) — 확정 (v1.1 CTO 검증 반영)

**※ 2026-05-31 CTO 검증: React+Vite+Zustand 확정, Next.js는 Phase 2. SockJS fallback 명시.**

#### Frontend

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Framework | **React 18 (SPA, Vite 기반)** | 18.x | SPA 기반 웹 애플리케이션. Next.js는 Phase 2(REST/SSR 도입)에 검토 |
| Language | **TypeScript** | 5.x | 정적 타입 검사 |
| Styling | **TailwindCSS** | 3.x + daisyUI | Utility-first CSS 프레임워크 |
| State | **Zustand** | 4.x | 전역 상태 관리. Redux Toolkit 불필요, Zustand 채택 (간결성, boilerplate 최소화) |
| HTTP | **Axios** | 1.x | REST API 통신 (Axios interceptors: JWT refresh logic, error handling) |
| Real-time | **SockJS + STOMP** | SockJS 1.x, @stomp/stompjs 7.x | WebSocket 통신 (채팅, 알림). **Long-polling fallback 필수** 지원 (SockJS `/info` 엔드포인트가 폴백 전략 자동 선택) |
| UI Library | **daisyUI** | 4.x | Tailwind 컴포넌트 |
| Build Tool | **Vite** | 5.x | 프론트엔드 빌드 및 번들링 (esbuild 기반 개발 서버) |
| Testing | **Vitest** | 1.x | Jest 대체, Vite와 네이티브 통합 |

> **CTO 검증 메모**: Next.js 선택은 불명확성 리스크. 현재 프로젝트는 SPA 중심이므로 Vite가 적합. Phase 2(SEO, SSR 필요 시)에 Next.js 전환 검토. Zustand는 Redux보다 boilerplate가 적고 실시간 채팅 상태 관리에 적합합니다.

#### Backend

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Runtime | **Spring Boot 3.x** | 3.2+ | Java Application 서버 |
| Language | **Java** | 17 or 21 (LTS) | Backend 구현 |
| ORM | **MyBatis** | 3.5+ | Database ORM |
| Validation | **Spring Validator** | - | 요청 데이터 유효성 검사 |
| Validation | **Hibernate Validator** | 8.x | Bean Validation (Jakarta) — @Valid, @NotNull 등 |
| Security | **Spring Security** | - | 인증/인가 (JWT) |
| Real-time | **WebSocket (STOMP over SockJS)** | - | 실시간 채팅, 알림 브로드캐스팅 |
| Scheduling | **Spring @Scheduled** | - | Cron 기반 백그라운드 작업 |
| REST Docs | **Spring REST Docs** | - | API 명세 자동 생성 (asciidoc, HTML) |

#### Database & Infrastructure

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Database | **PostgreSQL** | 15.x | Data persistence |
| ORM Layer | **MyBatis** | 3.5+ | Data access layer |
| Migration | **Flyway** | Latest | **DB 버전 관리 우선**. DDL 변경 시 무조건 Flyway Migration script로 관리 |
| DB Generator | **MyBatis Generator (MBG)** | Latest | **개발 보조용のみ**. 프로덕션 커밋은 수동 조정된 Entity/Mapper 적용. MBG는 개발 중 스키마 검증, prototype용으로만 사용 |
| Cache | **Redis** | 7.x | 세션, 브로드캐스트, Refresh Token 저장, Rate Limiting |
| Message Broker | **RabbitMQ** | 3.12+ | 비동기 작업, 알림 큐 |
| Monitoring | **Prometheus + Grafana** | - | 메트릭, 알람 |
| CI/CD | **GitLab CI** | - | 파이프라인 오케스트레이션 |
| Container | **Docker** | - | 애플리케이션 패키징 |
| Cloud | **AWS** / NAVER Cloud | - | 호스팅 (Primary/Alt) |

> **MBG 사용 가이드**:
> - 개발 단계에서 `mybatis-generator-maven-plugin` 또는 `gradle plugin`으로 Entity/Mapper XML 자동 생성
> - 생성된 코드는 반드시 수동 검토 후 커밋 (MBG가 생성한 resultMap, Base_Column_List 등 검증 필요)
> - Flyway Migration DDL이 **소스 오브 진리**. MBG는 DDL 결과 검증용 보조 도구
> - 프로덕션에서는 MBG 자동 생성 코드를 직접 수정·보완한 수동 Entity/Mapper로 대체

> **SockJS Long-polling fallback**: SockJS는 클라이언트 환경에 따라 WebSocket → XHR long-polling → XHR streaming → iframe 자동 폴백. `sockjs.js` 스크립트 로딩 후 `/info` 엔드포인트가 사용 가능한Transport를 반환. 채팅 기능은 항상 폴백 가능해야 합니다.

### 2.4 시스템 아키텍처 (Architecture Overview)

```
+----------------------------------------------------------------------+
|                          UI (React.js)                               |
+----------------------------------+-----------------------------------+
                                    | WebSocket (STOMP)
+----------------------------------+-----------------------------------+
|                     Spring Boot 3.x                               |
|  +---------------+ +---------------+ +---------------+              |
|  |   REST API    | |  WS Handler   | |  WS Sub       |              |
|  +---------------+ +---------------+ +---------------+              |
+---+--------------------------+--------------------+-----------------+
    | JDBC                      |  WebSocket (STOMP over SockJS)
+---v--------------------------+--------------------+-----------------+
|  MyBatis                      |  RabbitMQ                               |
|  (ORM)   +----+-----+-----+    |
+---+------+ Queue: ticket.notify  |
      |            |   |    |       |
+---v----+         +---> Queue: ticket.ws
|  PostgreSQL     +---> Queue: ticket.expired
|  (RDS / Private Subnet)
+---------------------------------------------------------+
NOTE: 간략화된 아키텍처 개요입니다.
```

### 2.5 핵심 테이블 목록 (Database Schema Summary)

전체 26개 테이블 요약:

| 번호 | 테이블명 | SoftDelete | 특징 |
|------|---------|-----------|------|
| 1 | companies | N | 회사 관리 (DEVELOPER/CLIENT) |
| 2 | users | N (Hard만) | RBAC, Role-Based Access Control |
| 3 | projects | O (deleted_at) | 프로젝트 관리, Soft Delete 적용 |
| 4 | project_managers | N | Junction Table: 프로젝트 매니저 |
| 5 | project_support_managers | N | Junction Table: 프로젝트 지원 매니저 |
| 6 | project_assignments | N | 프로젝트 배정 (role, status 관리) |
| 7 | projects_assignment_histories | N | 배정 이력 |
| 8 | projects_management_cards | N | 카드 관리 (JSONB) |
| 9 | card_assignments | N | 카드-티켓 매핑 |
| 10 | tickets | O (deleted_at) | 티켓 관리, FTS 인덱스, 7 단계 상태 |
| 11 | processing_plans | N | 처리 계획 (SUBMITTED/APPROVED/REJECTED) |
| 12 | ticket_comments | N | 대댓글 지원 |
| 13 | ticket_files | N | 첨부파일 |
| 14 | ticket_chat_messages_part_master | O (월별) | 채팅 메시지, PG15 파티션 |
| 15 | extension_requests | N | 데드라인 연장 |
| 16 | ticket_histories_part_master | O (분기별) | 상태전환 이력, PG15 파티션 |
| 17 | audit_logs | N | JSONB GIN 인덱스 |
| 18 | notification_preference_subscriptions | N | 알림 설정 (EVT-01~EVT-10) |
| 19 | notification_logs_part_master | O (월별) | 알림 로그 |
| 20 | login_history | N | 로그인 기록 |
| 21 | status_mappings | N | 상태 매핑 테이블 |
| 22 | priority_mappings | N | 우선순위 매핑 테이블 |
| 23 | system_settings | N | 시스템 설정 |
| 24 | business_calendars | N | 비즈니스 캘린더 (공휴일) |
| 25 | notification_logs_2026_MM | O (월별) | 2026년 월별 파티션 |
| 26 | notification_logs_YYYY_Q | O (분기별) | 티켓 히스토리 분기별 파티션 |

### 2.6 Ticket 워크플로우 상태 전이

| 상태 코드 | 한글 명칭 | 설명 | 색상 |
|-----------|----------|------|------|
| REGISTERED | 등록됨 | Ticket이 처음 생성된 상태 | Grey (#6B7280) |
| RECEIVED | 접수됨 | 개발팀이 Ticket을 확인하고 검토 수용 | Blue (#3B82F6) |
| PROCESSING | 처리 중 | 실제 작업이 시작됨 | Amber (#F59E0B) |
| DELAYED | 지체됨 | 완료기한을 넘김 | Red (#EF4444) |
| COMPLETION_REQUESTED | 완료 요청됨 | 완료 요청 (승인/반려 대상) | Purple (#8B5CF6) |
| APPROVED | 승인됨 | 완료 요청 승인 (COMPLETED로 자동 전이) | Green (#10B981) |
| COMPLETED | 완료됨 | 최종 완료 상태 - 읽기 전용 | Dark Green (#047857) |

**상태 전이 다이어그램:**
```
REGISTERED --> RECEIVED --> PROCESSING --+--> COMPLETION_REQUESTED --> APPROVED --> COMPLETED
                                    |     |
                                    +-----+ (연장 신청 / 지체)
```

**상태 전이 규칙:**
- REGISTERED --> RECEIVED: Support Staff 이상이 수행
- RECEIVED --> PROCESSING: 처리 계획 제출 후
- PROCESSING --> COMPLETION_REQUESTED: 진행 완료 시
- COMPLETION_REQUESTED --> APPROVED: approver (SUPPORT 이상) 승인
- APPROVED --> COMPLETED: 자동 전이
- PROCESSING --> DELAYED: 연장 요청 또는 마감 기한 초과

### 2.7 상태별 Action Buttons (UI 명세)

| 상태 | 사용 가능한 Actions |
|------|-------------------|
| REGISTERED | 상세 보기, Support Manager 배정, 검토 시작 |
| RECEIVED | 처리 계획(Processing Plan) 생성 |
| PROCESSING | 진행 상황 업데이트, 연장 요청, 완료 요청 |
| DELAYED | 완료 요청, 연장 사유 추가 |
| COMPLETION_REQUESTED | 완료 승인, 완료 반려 (Approver 전용) |
| APPROVED | - (자동으로 COMPLETED로 전이) |
| COMPLETED | - (읽기 전용, Archive) |


---

## 3. 팀 역할 및 RACI 매트릭스 (Team Roles & RACI)

### 3.1 팀 구성 (Team Composition)

nu_Trust 개발 팀은 6명의 전문가로 구성됩니다:

| 번호 | 역할 | 담당자 타입 | 주요 책임 | 세션별 역할 |
|------|------|-----------|----------|-----------|
| T1 | Tech Lead | 시니어 개발자 | 아키텍처 결정, 기술 부채 관리, 코드 리뷰 최종 승인 | 전체 시스템 설계, 백투백 결정 |
| T2 | 프론트엔드 개발자 | 미들~시니어 | UI/UX 구현, 상태 관리, WebSocket 통신, Admin Panel 프론트 | Ticket 상세 페이지, Status Ribbon, Action Buttons |
| T3 | 프론트엔드 개발자 | 미들 | Admin Panel UI, 채팅 UI, 모바일 반응형, Accessibility | 다크모드, 모바일 네비게이션, Bottom Navigation |
| T4 | 백엔드 개발자 | 시니어 | REST API 설계, RBAC 구현, 인증/인가, API Gateway | Ticket CRUD, 상태 전이 API, WebSocket 서버 |
| T5 | 백엔드 개발자 | 미들~시니어 | DB 설계/운영, 마이그레이션, RabbitMQ, 비동기 처리 | PostgreSQL 스키마, 파티션, 인덱스, 트랜잭션 |
| T6 | QA/캐시 리더 | 전담 | 테스트 설계, 자동화, 성능 테스트, 안정성 검증 | 단위/통합/E2E 테스트, 부하 테스트, 보안 검증 |

### 3.2 RACI 매트릭스 (Responsibility Assignment Matrix)

RACI: R=Responsible(실행), A=Accountable(책임), C=Consulted(협의), I=Informed(통보)

| 작업 항목 | T1 (Tech Lead) | T2 (FE #1) | T3 (FE #2) | T4 (BE #1) | T5 (BE #2) | T6 (QA) |
|-----------|:---:|:---:|:---:|:---:|:---:|:---:|
| 아키텍처 설계 | A | C | C | C | C | I |
| DB 스키마/DDL | C | I | I | C | R | I |
| Flyway 마이그레이션 | A | I | I | C | R | I |
| JWT 인증/인가 | C | I | I | R | C | I |
| RBAC 구현 | C | I | I | R | C | I |
| REST API 설계 | A | C | C | R | C | I |
| REST API 구현 | - | I | I | R | R | I |
| Ticket 상태 전이 | A | I | I | R | C | I |
| WebSocket 서버 | A | I | I | R | R | I |
| 프론트엔드 라우팅 | I | R | C | I | I | I |
| Ticket 상세 UI | I | R | R | I | I | I |
| Status Ribbon UI | I | R | - | I | I | I |
| Chat/Comment UI | I | R | R | I | I | I |
| Admin Panel UI | I | C | R | I | I | I |
| 프로젝트 카드 UI | I | C | R | I | I | I |
| 모바일 반응형 | I | C | R | I | I | I |
| DB 파티션 전략 | C | I | I | I | R | I |
| RabbitMQ 큐 설정 | C | I | I | C | R | I |
| CI/CD 파이프라인 | A | C | C | C | C | R |
| 테스트 자동화 | I | C | C | C | C | R |
| 성능/부하 테스트 | I | I | I | C | C | R |
| 보안 검증 | A | I | I | R | C | R |
| 배포 관리 | A | I | I | C | C | I |

### 3.3 의사결정 프로세스

1. **기술 결정 (Architecture Decision):** Tech Lead가 최종 승인, 관련 팀원들과협의
2. **코드 리뷰 (Code Review):** 최소 1명以上の리뷰어 승인 필요, Tech Lead 최종 확인
3. **버그 우선순위:** T6 (QA)가 우선순위 결정, Tech Lead와 협의
4. **스프린트 목표 조정:** 팀 전체 미팅에서 결정, Tech Lead가 조정

### 3.4 회의 일정 (Meeting Cadence)

| 회의 |频率 | 참석자 | 목적 |
|------|-----|-------|------|
| Daily Standup | 매일 (오전 10:00) | 전체 팀 | 진행 상황, 블록된 작업, 당일 계획 |
| Sprint Planning | Sprint 시작 | 전체 팀 | Sprint 목표, 작업 분배, estimate |
| Sprint Review | Sprint 종료 | 전체 팀 + 스테이크홀더 | 데모, 피드백, 완료 확인 |
| Retrospective | Sprint 종료 후 | 전체 팀 | 개선점, 문제점 파악 |
| 기술 리뷰 (Tech Review) | 격주 금요일 | 개발자 (T1-T5) | 기술 결정, 아키텍처 논의 |

---

## 4. Sprint 기반 WBS (Sprint-Based Work Breakdown)

### 4.1 Sprint 개요 (Sprint Overview)

| Sprint | 기간 | 주요 목표 | 마일스톤 |
|--------|------|----------|---------|
| Sprint 0 (Setup) | 2주 | 개발 환경 구축, CI/CD, DB 스키마 | M0: 환경 완성 |
| Sprint 1 | 4주 | Auth/RBAC, Project CRUD, 기본 티켓 | M1: Core 기능 |
| Sprint 2 | 4주 | Ticket 워크플로우, 상태 전이, Admin Panel | M2: Ticket 엔진 |
| Sprint 3 | 3주 | 실시간 채팅, Notifications, 카드 관리 | M3: Real-time |
| Sprint 4 | 3주 | QA, DevOps, UAT, Release | M4: Release |
| **총** | **16주** | *( 여유 2주 포함)* | - |

#### 4.1.1 개발 방법론 원칙 (Methodology Principles)

본 프로젝트는 **Hybrid Agile** 방식을 채택합니다. Sprint 0~4의 모든 작업은 Week 단위로 계획을 수립하고, 각 Sprint Planning 회의에서 Team이 Week를 Day 단위로 분해하여 실행합니다.

- **Sprint 0 환경 구축은 예외**: Sprint 0은 Day 단위 계획 유지. 인프라 환경 구축(서버 설정, CI/CD 파이프라인, Docker Compose)은 결정적 성격이 있어 주단위 추상화로는 리스크가 큽니다. 다만 Git 레포지토리 초기화, 프로젝트 초기화 등 T2~T5의 병렬 작업은 Sprint 0 Week 1-2에서도 주단위로 분할해 진행합니다.
- **Sprint 1~4는 Week 기반 계획**: 실제 Day 단위 분해는 매 Sprint Planning 회의에서 Team이 결정. 이 가이드의 Week 단위 계획은 로드맵 레벨의 추상화 수준이며, 실제 실행 계획은 Sprint Planning에 위임합니다.
- **Daily Standup 추적**: 각 Week별 작업은 Daily Standup(매일 오전 10:00)으로 진행状況 확인 및 블록 이슈 신속 대응.
- **Plan -> Adapt -> Repeat**: Sprint 0에서 환경 완성 후 Sprint 1~4는 매 Sprint마다 Plan(Planning) -> Build(Execution) -> Review(Sprint Review) -> Adapt(Retrospective) 사이클 반복.

### 4.2 Sprint 0: 개발 환경 구축 (Environment Setup) - 2주

**Sprint Goal:** 모든 개발자가 로컬에서 프로젝트 기동 가능, CI/CD 파이프라인 가동

#### Day 1-2: 프로젝트 설정

| # | 태스크 | 담당 | 종류 | estimate |
|---|--------|------|---|----------|
| 001 | Git 레포지토리 생성, Git Flow 브랜치 전략 설정 | T1 | Infra | 4h |
| 002 | Frontend: Vite + React + TypeScript 프로젝트 초기화 | T2 | FE | 4h |
| 003 | Backend: Spring Boot 프로젝트 생성 (Gradle 기반) | T4 | BE | 4h |
| 004 | Docker Compose 구성 (PostgreSQL, Redis, RabbitMQ) | T5 | Infra | 4h |
| 005 | Flyway 마이그레이션 초기화, 첫 migration script 작성 | T5 | Infra | 4h |
| 006 | Dockerfile 작성 (Multi-stage build: Frontend Nginx, Backend Java 21) | T1 | Infra | 4h |
| 007 | Docker Compose 환경변수(.env) 분리 (dev/stage/prod) | T5 | Infra | 4h |
| 008 | Docker Healthcheck 설정 (PostgreSQL pg_isready, RabbitMQ rabbitmq-diagnostics) | T5 | Infra | 2h |

#### Day 3-5: CI/CD 및 기술 스택 통합

| # | 태스크 | 담당 | 종류 | estimate |
|---|--------|------|---|----------|
| 009 | GitLab CI 파이프라인 구성 (빌드, 테스트, lint) | T6 | CI/CD | 8h |
| 010 | ESLint + Prettier + React hooks 플러그인 설정 | T2 | FE | 4h |
| 011 | SpotBugs + Checkstyle Gradle 플러그인 설정 | T4 | BE | 4h |
| 012 | TailwindCSS + daisyUI 프론트엔드 통합 | T3 | FE | 4h |
| 013 | MyBatis + PostgreSQL 연결 설정 | T5 | BE | 4h |
| 014 | MyBatis Generator (mybatis-generator-maven-plugin / Gradle) 설정 — PostgreSQL 15 DDL 기준 Auto-Generate (Entity, Mapper XML, Mapper Java) — 18개 테이블 생성 목표 | T5 | Infra | 6h |
| 015 | MyBatis 설정: resultMap, SQLSessionFactory, 다중 데이터소스(Read/Write), TransactionManager 설정 | T5 | BE | 4h |
| 016 | Spring Security 기본 설정 (PasswordEncoder, CSRF disable for API, CorsConfiguration) | T4 | BE | 6h |
| 017 | RabbitMQ explicit queue/exchange/routing-key DDL 설정 (ticket.notify, ticket.ws, ticket.expired) | T5 | Infra | 6h |
| 018 | Prometheus + Grafana Spring Boot Actuator 의존성 추가 + /actuator/health 엔드포인트 검증 | T6 | Infra | 4h |
| 119 | 로깅: Logback XML 설정 (JSON 포맷, Kibana/Elasticsearch export용) | T4 | Infra | 4h |

#### Day 6-10: 데이터 모델 구축

| # | 태스크 | 담당 | 종류 | estimate |
|---|--------|------|---|----------|
| 019 | V1_DDL: companies, users, projects 테이블 DDL 생성 (확장 설정: pgcrypto, btree_gin 포함) | T5 | DB | 8h |
| 020 | V2_DDL: tickets, processing_plans, ticket_comments, ticket_files DDL | T5 | DB | 8h |
| 021 | V3_DDL: project_managers, project_support_managers, project_assignments DDL | T5 | DB | 8h |
| 022 | V4_DDL: cards 관련 테이블 (management_cards, card_assignments) DDL | T5 | DB | 8h |
| 023 | V5_DDL: 파티션 테이블 - chat_messages_part_master, ticket_histories_part_master | T5 | DB | 8h |
| 024 | V6_DDL: notification 테이블, notification_logs_part_master, audit_logs, login_history DDL | T5 | DB | 8h |
| 025 | V7_DDL: lookup 테이블 (status_mappings, priority_mappings, system_settings, business_calendars) DDL | T5 | DB | 6h |
| 026 | V8_DDL: 트리거/함수 생성 (update_updated_at_column, audit_log_trigger) — 7개 핵심 테이블(trg_updated_at_companies/users/projects/tickets/processing_plans/card_assignments/project_assignments)에 적용 — INSTEAD OF TRIGGER 파티션 분할 로직 구현 | T5 | DB | 12h |
| 027 | Flyway migration 전체 검증 (테스트 DB에 migrate 실행, 26개 테이블 생성 확인) | T5+T6 | Validation | 4h |

#### Day 11-13: S3 로컬 환경 및 Docker 완성

| # | 태스크 | 담당 | 종류 | estimate |
|---|--------|------|---|----------|
| 028 | Docker Compose에 MinIO 추가 (S3 호환 스토리지, S3 Local 모킹) | T5 | Infra | 4h |
| 029 | MinIO 초기 버킷 자동 생성 스크립트 (bucket: trust-files, bucket: trust-backups) | T5 | Infra | 4h |
| 030 | Backend S3 설정 (application.yml: aws.region, bucket.name, credential) | T5 | BE | 6h |

#### Sprint 0 Deliverables
- [ ] 로컬環境에서 프론트엔드/백엔드 기동 성공 (http://localhost:3000, :8080)
- [ ] Docker Compose 5가지 서비스 (PostgreSQL, Redis, RabbitMQ, MinIO, Grafana) 가동 확인
- [ ] Flyway migration 1~8회 적용 성공, 26개 테이블 전체 생성 확인
- [ ] GitLab CI 파이프라인 첫 빌드 성공
- [ ] Docker Healthcheck: PostgreSQL pg_isready, RabbitMQ rabbitmq-diagnostics, MinIO mc admin info 성공
- [ ] Spring Actuator: /actuator/health, /actuator/prometheus 엔드포인트 응답 확인


### 4.3 Sprint 1: Auth/RBAC, 기본 CRUD - 4주

**Sprint Goal:** 사용자 인증/인가 시스템 완성, Company/Project/Card CRUD 구현

#### Week 1-2: 인증/인가 및 Company 관리

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|----------|--------|
| 101 | JWT 토큰 발급/검증 API (POST /auth/login, /auth/register) | T4 | BE | 12h | Sprint 1 |
| 102 | 회원가입 API - SUPPORT 회원가입 검증 (role=SUPPORT) | T4 | BE | 8h | Sprint 1 |
| 103 | 회원가입 API - CUSTOMER 회원가입 검증 (role=CUSTOMER, company_type=CLIENT) | T4 | BE | 8h | Sprint 1 |
| 104 | Spring Security 설정 (JWT 필터 체인) | T4 | BE | 8h | Sprint 1 |
| 105 | RBAC 어노테이션 (@PreAuthorize, role hierarchy) 적용 | T4 | BE | 8h | Sprint 1 |
| 106 | Login 페이지 UI (email/password) | T2 | FE | 8h | Sprint 1 |
| 107 | Register 페이지 UI (역할 선택) | T2 | FE | 8h | Sprint 1 |
| 108 | POST /companies (DEVELOPER) - ADMIN 전용 | T4 | BE | 6h | Sprint 1 |
| 109 | POST /companies (CLIENT) - ADMIN/SUPPORT 가능 | T4 | BE | 6h | Sprint 1 |
| 110 | GET/PUT/DELETE /companies/{id} | T4 | BE | 8h | Sprint 1 |
| 111 | Company 관리 UI (Admin Panel) | T3 | FE | 8h | Sprint 1 |
| 112 | POST /customer-register (SUPPORT가 CLIENT만 SELECT) | T4 | BE | 6h | Sprint 1 |
| 113 | 비밀번호 해싱: BCryptPasswordEncoder 설정 (PasswordEncoder Bean) — users.password_hash 저장용 | T4 | BE | 4h | Sprint 1 |
| 114 | JWT Access Token: 15분 유효, Refresh Token: 7일 유효 (application.yml: jwt.secret, access.exp, refresh.exp) | T4 | BE | 6h | Sprint 1 |
| 115 | POST /auth/refresh (Refresh Token → New Access Token 재발급) | T4 | BE | 6h | Sprint 1 |
| 116 | Refresh Token Redis 저장 (key=userId, value=refreshToken, TTL=7일) — Token Rotation 구현 | T5 | BE | 6h | Sprint 1 |
| 117 | CORS Configuration (allowedOrigins: dev.nuttrust.kr, stage.nuttrust.kr, nuttrust.kr) | T4 | BE | 4h | Sprint 1 |
| 118 | Input Validation: Spring Validator, @Valid, Bean Validation (email 형식, password strength 검사) | T4 | BE | 6h | Sprint 1 |
| 119 | Login Rate Limiting (10 requests/minute, IP 기반 Redis Set) | T6 | BE | 6h | Sprint 1 |
| 120 | 로그인 실패 시 5회 → 계정 잠금 (users.status=DISABLED, adminunlock 필요) | T4 | BE | 4h | Sprint 1 |

#### Week 3-4: Project CRUD 및 Card Management

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|----------|--------|
| 121 | POST /projects (SUPPORT/MANAGER 배정 포함) | T4 | BE | 8h | Sprint 1 |
| 122 | GET /projects (역할별 필터링 - CUSTOMER: 소속 CLIENT, SUPPORT: 소속 DEVELOPER) | T4 | BE | 8h | Sprint 1 |
| 123 | PUT /projects/{id}, DELETE /projects/{id} | T4 | BE | 6h | Sprint 1 |
| 124 | Soft delete: projects.deleted_at NULLABLE | T4 | BE | 4h | Sprint 1 |
| 125 | 프로젝트 관리 UI (Admin Panel) | T3 | FE | 8h | Sprint 1 |
| 126 | POST /assignments (SUPPORT) - project_assignments | T4 | BE | 8h | Sprint 1 |
| 127 | PUT /assignments/{id} (revise) - project_assignments | T5 | BE | 6h | Sprint 1 |
| 128 | DELETE /assignments/{id} - Validation (Active 티켓 있음 시 409) | T5 | BE | 6h | Sprint 1 |
| 129 | 프로젝트 배정 검증 상세 흐름 구현 (Flowchart Rule F) | T4 | BE | 8h | Sprint 1 |
| 130 | 프로젝트 카드 생성/수정/삭제 API (management_cards CRUD) | T5 | BE | 12h | Sprint 1 |
| 131 | POST /cards - ADMIN 또는 프로젝트 배정된 SUPPORT | T5 | BE | 8h | Sprint 1 |
| 132 | PUT /cards/{id} - ADMIN 또는 카드 작성자/SUPPORT | T5 | BE | 6h | Sprint 1 |
| 133 | DELETE /cards/{id} - ADMIN 전용 | T4 | BE | 6h | Sprint 1 |
| 134 | 카드 관리 UI (Project Detail - Server/SW Card Grid) | T3 | FE | 12h | Sprint 1 |
| 135 | 카드 추가/수정 폼 (서버 정보, SW 정보, 로그인 계정, 운영 계정, 참고사항) | T3 | FE | 8h | Sprint 1 |
| 136 | GET /cards - 전체 읽기 허용, 프로젝트 카드 그리드 렌더링 | T2 | FE | 8h | Sprint 1 |
| 137 | 프로젝트 카드 JSONB 구조 (server_info, software_info) | T5 | DB | 4h | Sprint 1 |
| 138 | MyBatis Entity 생성: companies, users, projects, project_managers, project_support_managers, project_assignments | T5 | BE | 12h | Sprint 1 |
| 139 | MyBatis Mapper XML 생성 (Company, Project, Assignment CRUD) — resultMap, Base_Column_List 포함 | T5 | BE | 12h | Sprint 1 |
| 140 | MyBatis Entity 생성: project_assignment_histories, ticket_chat_messages | T5 | BE | 6h | Sprint 1 |
| 141 | MyBatis Mapper XML 생성 (project_assignment_histories: INSERT + SELECT_BY_assignment_id — assignment 이력 조회용) | T5 | BE | 6h | Sprint 1 |
| 142 | project_assignments CRUD 시 audit_log_trigger 테스트: ASSIGN/RESIGN/REVOKED 시 project_assignment_histories 자동 삽입 확인 | T6 | QA | 4h | Sprint 1 |

#### Sprint 1 Deliverables
- [ ] Login/Register API 검증 성공
- [ ] JWT 토큰 기반 인증/인가 검증 성공
- [ ] Company CRUD API (POST/GET/PUT/DELETE) 검증 성공
- [ ] Project CRUD API (POST/GET/PUT/DELETE) 검증 성공
- [ ] RBAC 검증: 403 Forbidden correctly applied per role
- [ ] Login/Register UI completed
- [ ] Admin Panel: Company/Project 관리 UI
- [ ] 프로젝트 카드 CRUD API + UI
- [ ] Card 관리 UI (Server/SW 카드 그리드, 카드 추가/수정 폼)


### 4.4 Sprint 2: Ticket 워크플로우 엔진 - 4주

**Sprint Goal:** 티켓 생성~완료 전체 워크플로우 구현, 상태 전이 엔진, Optimistic Locking

#### Week 5-6: Ticket CRUD 및 상태 전이 엔진

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|----------|--------|
| 144 | CREATE TABLE tickets DDL (FTS 인덱스, soft delete) | T5 | DB | 4h | Sprint 2 |
| 145 | POST /tickets - CUSTOMER가 소속 CLIENT 프로젝트에 티켓 생성 | T4 | BE | 8h | Sprint 2 |
| 146 | GET /tickets - 역할별 필터링 (CUSTOMER/SUPPORT/ADMIN) | T4 | BE | 8h | Sprint 2 |
| 147 | GET /tickets/{id} - 동일 프로젝트 내 접근 검증 | T4 | BE | 6h | Sprint 2 |
| 148 | UPDATE tickets/{id} - 상태 전이 (SUPPORT 이상만) | T4 | BE | 8h | Sprint 2 |
| 149 | Ticket 워크플로우 상태 전이 엔진 (State Machine 패턴) | T4 | BE | 12h | Sprint 2 |
| 150 | 상태 전이 규칙 검증 (REGISTERED->RECEIVED 등) | T4 | BE | 8h | Sprint 2 |
| 151 | ticket_histories 파티션 생성 + 상태 전이 기록 | T5 | DB | 4h | Sprint 2 |
| 152 | Optimistic Locking - version column 적용 (tickets, projects, processing_plans, extension_requests) | T4 | BE | 8h | Sprint 2 |
| 153 | 409 Conflict 처리 - "다른 사용자가 수정 중입니다" 메시지 + auto-refresh | T2 | FE | 6h | Sprint 2 |
| 154 | Ticket 목록 UI (Status, Type, Creator, Dates, Assignee) | T2 | FE | 8h | Sprint 2 |
| 155 | Ticket 검색 (FTS: title + description, 한국어) | T2 | FE | 6h | Sprint 2 |
| 156 | Ticket 생성 폼 (고객사명, 프로젝트명, Support Manager 선택) | T2 | FE | 8h | Sprint 2 |

#### Week 7-8: Ticket 상세 UI, Processing Plan, Extension, Chat UI

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|----------|--------|
| 157 | Ticket 상세 페이지 레이아웃 (7 섹션) | T2 | FE | 16h | Sprint 2 |
| 158 | Status Ribbon 구현 (전체 너비 그라데이션, 상태 Badge, 타임스탬프) | T2 | FE | 8h | Sprint 2 |
| 159 | 상태별 Action Buttons (7 상태마다 다른 버튼) | T2 | FE | 8h | Sprint 2 |
| 160 | History Timeline (상태 전이 시계열) | T2 | FE | 8h | Sprint 2 |
| 161 | Processing Plan 생성 (POST /processing_plans) | T4 | BE | 8h | Sprint 2 |
| 162 | Processing Plan 승인/반려 (POST /processing_plans/{id}/review) | T4 | BE | 6h | Sprint 2 |
| 163 | extension_requests 테이블 생성 | T5 | DB | 4h | Sprint 2 |
| 164 | POST /extension_requests (연장 신청) | T4 | BE | 6h | Sprint 2 |
| 165 | PUT /extension_requests/{id}/approve_reject | T4 | BE | 6h | Sprint 2 |
| 166 | 티켓 상세 - 연장 신청 폼 UI (requested_date picker, reason textarea, submit 버튼) — SUPPORT/CUSTOMER 모두 접근 가능 | T2 | FE | 8h | Sprint 2 |
| 167 | Ticket 상세 - Chat/Comment 섹션 (입력 영역 상단, 메시지 목록 하단) | T2+T3 | FE | 16h | Sprint 2 |
| 168 | 댓글 작성 API (POST /tickets/{id}/comments) | T5 | BE | 6h | Sprint 2 |
| 169 | 대댓글 지원 (parent_id NULLABLE, tree 구조) | T5 | BE | 6h | Sprint 2 |
| 170 | 첨부파일 API (POST /tickets/{id}/files) | T5 | BE | 6h | Sprint 2 |
| 171 | Chat 메시지 전송 API (실시간 준비용) | T5 | BE | 6h | Sprint 2 |
| 172 | Admin Panel - 티켓 일괄 관리 UI | T3 | FE | 8h | Sprint 2 |
| 173 | Ticket 우선순위 매핑 UI (status_mappings, priority_mappings) | T3 | FE | 6h | Sprint 2 |
| 174 | 파일 업로드: S3 Presigned URL 생성 API (POST /tickets/{id}/upload-url) — MinIO/S3 직접 업로드 패턴 | T4 | BE | 8h | Sprint 2 |
| 175 | S3 버전 관리 활성화 (S3 Bucket Versioning) + lifecycle rule 설정 (30일 → Glacier 전이) | T5 | Infra | 4h | Sprint 2 |
| 176 | ticket_files 업로드 시: 파일 타입 검증 (PDF, JPG, PNG, ZIP만 허용), 용량 제한 (10MB) | T5 | BE | 4h | Sprint 2 |
| 177 | audit_logs 자동 삽입: Spring aop @Aspect — 모든 REST request/response 자동 로깅 | T5 | BE | 8h | Sprint 2 |
| 178 | trigger 검증: update_updated_at_column, audit_log_trigger 함수 개별 테스트 | T6 | QA | 4h | Sprint 2 |
| 179 | 파티션 관리: 월별 파티션 자동 스크립트 (pg_partman 또는 cron job, 새 달 첫 날 00:00 실행) | T5 | DB | 6h | Sprint 2 |
| 180 | 파티션 검증: 현재 활성 파티션 확인 (SELECT * FROM pg_partitions), 새 파티션 자동 생성 확인 | T6 | QA | 4h | Sprint 2 |
| 181 | MyBatis Entity 생성: tickets, ticket_comments, ticket_files, processing_plans, extension_requests | T5 | BE | 12h | Sprint 2 |
| 182 | MyBatis Mapper XML 생성 (Ticket, Comment, File, ProcessingPlan, Extension CRUD) — resultMap, Base_Column_List, FTS 검색 SQL 포함 | T5 | BE | 16h | Sprint 2 |
| 183 | MyBatis Mapper XML 생성 (extension_requests: INSERT + SELECT_BY_TICKET_ID, processing_plans: INSERT + SELECT_BY_TICKET_ID + UPDATE) | T5 | BE | 10h | Sprint 2 |
| 184 | MyBatis @SelectProvider 동적 SQL (GET /tickets 역할별 필터링: CUSTOMER/SUPPORT/ADMIN WHERE 절 분기) | T5 | BE | 6h | Sprint 2 |

#### Sprint 2 Deliverables
- [ ] Ticket CRUD API (POST/GET/PUT/DELETE) 검증 성공
- [ ] 상태 전이 엔진 검증 (7 상태, 6 전이 규칙)
- [ ] Optimistic Locking 검증 (version column, 409 Conflict)
- [ ] Ticket 상세 페이지 UI (Status Ribbon, Action Buttons, History Timeline)
- [ ] Processing Plan CRUD + 승인/반려
- [ ] Extension Request 생성 + 승인/반려
- [ ] Chat/Comment 섹션 UI (댓글, 대댓글, 첨부파일)
- [ ] S3 Presigned URL 업로드 + 파일 타입/용량 검증
- [ ] audit_logs 자동 삽출 (@Aspect AOP) 검증
- [ ] 파티션 자동 스크립트 + 검증 테스트 통과
- [ ] 티켓 일괄 관리 UI (Admin Panel)


### 4.5 Sprint 3: 실시간 통신 및 알림 - 3주

**Sprint Goal:** WebSocket 실시간 채팅, 알림 시스템, 프로젝트 카드 UI 완성

#### Week 9-10: WebSocket 및 실시간 채팅

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|----------|--------|
| 185 | WebSocket (STOMP over SockJS) 서버 구성 | T4 | BE | 12h | Sprint 3 |
| 186 | STOMP 구독 채널 설정 (/queue/chat.{ticketId}, /queue/notification.{userId}) | T5 | BE | 8h | Sprint 3 |
| 187 | WebSocket 브로드캐스팅 (새 메시지, typing indicator) | T5 | BE | 8h | Sprint 3 |
| 188 | RABBITMQ queue 설정 (ticket.ws, ticket.notify) | T5 | BE | 8h | Sprint 3 |
| 189 | 채팅 메시지 저장 (ticket_chat_messages_part_master 파티션) | T5 | DB | 4h | Sprint 3 |
| 190 | Chat UI 구현 (채팅 버블 - 발신자 우측, 수신자 좌측) | T3 | FE | 12h | Sprint 3 |
| 191 | Real-time 업데이트 (WebSocket subscribe, new message 수신) | T3 | FE | 8h | Sprint 3 |
| 192 | Typing Indicator (WebSocket을 통한 real-time 표시) | T3 | FE | 6h | Sprint 3 |
| 193 | 채팅 파일 첨부 미리보기 (Inline Preview) | T3 | FE | 6h | Sprint 3 |
| 194 | WebSocket 연결 끊김 복구 (Reconnection logic) | T3 | FE | 6h | Sprint 3 |
| 195 | STOMP 핸들러 상세: /app/chat.sendMessage @MessageMapping, /app/chat.typing — Principal 기반 senderId 추출 | T4 | BE | 6h | Sprint 3 |
| 196 | STOMP 인증: WebSocket 연결 시 JWT 검증 (ChannelInterceptor) — 연결 전 토큰 전달 | T5 | BE | 6h | Sprint 3 |
| 197 | RABBITMQ consumer 구성: ticket.notify queue 리스너 (NotificationService.publish() 호출) | T5 | BE | 8h | Sprint 3 |
| 198 | RABBITMQ consumer 구성: ticket.ws queue 리스너 (WebSocket SockJS Session broadcast — 특정 채널 구독자에게 발송) | T5 | BE | 8h | Sprint 3 |
| 199 | RABBITMQ consumer 구성: ticket.expired queue 리스너 — 만료 티켓 감지, OVERDUE→DELAYED 상태 전이 자동 실행 | T5 | BE | 12h | Sprint 3 |
| 200 | cron 스케줄러: 매일 09:00 실행, tickets.deadline < TODAY 상태='OVERDUE' 티켓 → status='DELAYED', reason='OVERDUE_TO_DELAYED'로 변경 | T5 | BE | 6h | Sprint 3 |

#### Week 11: Notifications + Project Detail Page

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|---|--------|
| 201 | Notification 이벤트 정의 (EVT-01~EVT-10) | T5 | BE | 6h | Sprint 3 |
| 202 | Notification Preferences API (POST/GET/PUT notification_preferences) | T5 | BE | 8h | Sprint 3 |
| 203 | Notification 이벤트 구독 API (POST notification_event_subscriptions) | T5 | BE | 8h | Sprint 3 |
| 204 | Notification 발송 로직 (In-App, Push, Email, SMS) | T5 | BE | 12h | Sprint 3 |
| 205 | Quiet Hours 설정 구현 | T5 | BE | 8h | Sprint 3 |
| 206 | Batched 알림 (5분 단위 묶음 발송) | T5 | BE | 6h | Sprint 3 |
| 207 | Notification Popup UI (P0 이벤트 즉시 Popup) | T2 | FE | 8h | Sprint 3 |
| 208 | Notification Sidebar UI (사이드바 알림 패널) | T2 | FE | 8h | Sprint 3 |
| 209 | Notification Badge 카운터 | T2 | FE | 6h | Sprint 3 |
| 210 | Project Detail 페이지 (Server/SW Cards, Ticket List, Quick Actions) | T3 | FE | 12h | Sprint 3 |
| 211 | 프로젝트 정보 관리 카드 관리 UI (서버/ SW 정보 카드 추가/수정) | T3 | FE | 8h | Sprint 3 |
| 212 | 브라우저 호환성을 위한 Long-polling fallback | T4 | BE | 6h | Sprint 3 |

#### Week 12: Notification Logs History & System Settings

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|---|--------|
| 213 | GET /notification_logs (역할별 필터링: ADMIN 전체, SUPPORT company_id 제한, CUSTOMER 자신의 로그만) | T5 | BE | 8h | Sprint 3 |
| 214 | GET /notification_logs/{id} 읽음 표시 (PUT /notification_logs/{id}/read) | T5 | BE | 6h | Sprint 3 |
| 215 | PUT /notification_logs/read-all (현재 로그인 유저의 모든 알림 읽음 처리) | T5 | BE | 6h | Sprint 3 |
| 216 | DELETE /notification_logs/{id} (ADMIN만 가능) | T5 | BE | 4h | Sprint 3 |
| 217 | 알림 로그 히스토리 테이블 UI (날짜별 필터링, 읽음/안읽음 Badge, 클릭 시 상세 모달) | T2 | FE | 12h | Sprint 3 |
| 218 | 시스템 설정 관리 UI (system_settings CRUD: 회사명, 로고 URL, 운영 시간 설정) | T3 | FE | 8h | Sprint 3 |
| 219 | 비즈니스 캘린더 UI (business_calendars: 공휴일/특수 운영일 설정, Calendar API 연동 Preview) | T3 | FE | 8h | Sprint 3 |
| 220 | 시스템 설정 API (system_settings CRUD, T5 BE) | T5 | BE | 6h | Sprint 3 |
| 221 | 비즈니스 캘린더 API (business_calendars CRUD) | T5 | BE | 6h | Sprint 3 |
| 222 | RBAC 로직 검증: 4角色별 API 접근 테스트 (ADMIN≥COMPANY_ADMIN≥SUPPORT≥CUSTOMER) | T6 | QA | 8h | Sprint 3 |

#### Sprint 3 Deliverables
- [ ] WebSocket 서버 구현 + 검증 (STOMP over SockJS)
- [ ] 실시간 채팅 UI (버블 정렬, 파일 첨부, typing indicator)
- [ ] WebSocket 재연결 로직 검증
- [ ] 알림 발송 시스템 (In-App, Push, Email, SMS)
- [ ] Notification Popup, Sidebar, Badge UI
- [ ] Quiet Hours 및 Batched 발송
- [ ] Project Detail 페이지 (Server/SW 카드 그리드, Ticket List)
- [ ] 프로젝트 카드 관리 UI
- [ ] 알림 로그 히스토리 테이블 UI (날짜별 필터링, 읽음/안읽음 표시)
- [ ] 시스템 설정 관리 UI (system_settings CRUD)
- [ ] 비즈니스 캘린더 UI (business_calendars)


### 4.6 Sprint 4: QA, DevOps, Release - 3주

**Sprint Goal:** 전체 기능 검증, 성능 최적화, UAT, 첫 Release

#### Week 12-13: QA 및 최적화

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|---|--------|
| 223 | 단위 테스트 커버리지 80% 이상 달성 (Jest + Vite, JUnit 5) | T6 | QA | 16h | Sprint 4 |
| 224 | 통합 테스트 - API 검증 (전체 API 흐름) | T5+T6 | QA | 12h | Sprint 4 |
| 225 | E2E 테스트 (Playwright: Login → Project → Ticket → Chat) | T6 | QA | 12h | Sprint 4 |
| 226 | 성능 테스트 (100 동시 WebSocket, 500 TPS REST API) | T6 | QA | 8h | Sprint 4 |
| 227 | 부하 테스트 (WebSocket 500 conn, Ticket 1000/초) | T6 | QA | 8h | Sprint 4 |
| 228 | 보안 검증 (SQL Injection, XSS, CSRF, JWT 검증, RBAC bypass) | T6 | QA | 12h | Sprint 4 |
| 229 | DB QPS 테스트 + 인덱스 검증 | T5 | DB | 6h | Sprint 4 |
| 230 | Mobile 브라우저 호환성 테스트 (Safari, Chrome, Firefox) | T6 | QA | 8h | Sprint 4 |
| 231 | Accessibility 테스트 (WCAG 2.1 AA Level) | T6 | QA | 6h | Sprint 4 |
| 232 | MyBatis Mapper 검증: 15개 이상의 Mapper XML 파일 존재 확인, SQL 문법 검증 (explain analyze로 성능 확인) | T5 | DB | 8h | Sprint 4 |
| 233 | DTO Validation 테스트: @Valid 없는 엔드포인트 제거, 모든 입력값 유효성 검사 적용 확인 | T6 | QA | 6h | Sprint 4 |

#### Week 14: UAT (사용자 Acceptance Test)

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|---|--------|
| 234 | UAT 환경 구축 (staging 서버) | T1 | Infra | 8h | Sprint 4 |
| 235 | UAT 진행 (테스트 사용자 모집, 기능 테스트) | T1+T6 | UAT | 16h | Sprint 4 |
| 236 | UAT 피드백 반영 (bug fix, 기능 보완) | T4+T2 | Fix | 20h | Sprint 4 |
| 237 | UAT 리포트 작성 및 검토 | T6 | QA | 4h | Sprint 4 |

#### Week 15: DevOps 및 배포

| # | 태스크 | 담당(BE/FE) | 종류 | estimate | Sprint |
|---|--------|-------------|---|---|--------|
| 238 | Docker 이미지 빌드 (프론트엔드, 백엔드, multi-stage 최적화: distroless 기반) | T1 | Infra | 6h | Sprint 4 |
| 239 | Docker Compose staging 구성 | T5 | Infra | 6h | Sprint 4 |
| 240 | AWS EKS deployment yaml 작성 (Deployment, Service(Ingress), HPA) | T1 | Infra | 8h | Sprint 4 |
| 241 | Prometheus metrics 수집 (Spring Boot Actuator) + Custom metrics (ticket 처리 시간, WebSocket 연결 수) | T5 | Infra | 8h | Sprint 4 |
| 242 | Grafana 대시보드: 1) JVM 메모리/CPU, 2) DB 쿼리 응답시간, 3) WebSocket 활성 연결, 4) RabbitMQ 대기열 depth | T6 | Infra | 12h | Sprint 4 |
| 243 | 로깅 중앙화: Fluentd/Filebit DaemonSet → Elasticsearch → Kibana (ELK stack) 연동 | T5 | Infra | 12h | Sprint 4 |
| 244 | RDS 자동 백업 설정 (Point-in-Time Recovery, retention=7일) + 수동 백업 스크립트 (pg_dump → S3 bucket: trust-backups) | T5 | Infra | 8h | Sprint 4 |
| 245 | 백업 검증: 매월 1회 pg_restore 테스트 (dev env 복원 확인) | T6 | QA | 4h | Sprint 4 |
| 246 | GitLab CI 배포 파이프라인 완성 | T6 | CI/CD | 6h | Sprint 4 |
| 247 | 도메인 설정 (HTTPS, SSL/TLS, cert-manager 자동 갱신) | T1 | Infra | 4h | Sprint 4 |
| 248 | API Gateway (NGINX/Kong) staging 설정 — Rate Limiting, JWT 검증, CORS 적용 | T1 | Infra | 8h | Sprint 4 |
| 249 | 첫 Release (Release v0.1) | T1 | Release | 4h | Sprint 4 |

#### Sprint 4 Deliverables
- [ ] 단위 테스트: 80% 이상 커버리지 달성
- [ ] 통합 테스트: 전체 API 검증 성공
- [ ] E2E 테스트: 핵심 흐름 (Login → Project → Ticket → Chat) 100% 통과
- [ ] 성능 테스트: 목표 달성 (100 WebSocket, 500 TPS)
- [ ] 보안 검증: Critical/High 취약점 0건 (SQL Injection, XSS, CSRF, JWT, RBAC bypass)
- [ ] MyBatis Mapper 검증: 15개 Mapper XML, explain analyze 성능 확인
- [ ] DTO Validation: @Valid 누락 엔드포인트 0건
- [ ] Accessibility: WCAG 2.1 AA Level 통과
- [ ] Mobile 브라우저 호환성: Safari, Chrome, Firefox 확인
- [ ] UAT (Week 14): 테스트 사용자 검증 완료, 리포트 발행
- [ ] AWS EKS 배포 성공 (Deployment, Service, HPA)
- [ ] Monitoring: Prometheus + Grafana 대시보드 4개稼働
- [ ] Log Aggregation: ELK stack 연동 (Fluentd → Elasticsearch → Kibana)
- [ ] RDS 백업: 자동백업(PITR 7일) + 수동백업(pg_dump → S3) + 복원 검증
- [ ] API Gateway (NGINX/Kong) Rate Limiting, JWT, CORS 적용
- [ ] v0.1 Release 완료

---

## 5. 기능별 작업 매핑 (Task Mapping by Feature)

기능별 매핑의 Sprint 열은 실제 Sprint 명칭(Sprint 1, Sprint 2 등)으로 명시하며,
각 행의 Task ID 는 섹션 하단에 명시된 전역 고유 ID 와 매핑됩니다.

기능별 # 열은 해당 기능 내 순번을 유지하며, 실제 전역 Task ID 는 하단 매핑 테이블 참조.

### 5.1 Auth / RBAC (전역 Task ID: 109~120)

| # | 작업 | 담당 | Sprint | API Path | Method |
|---|--------|------|--------|----------|--------|
| 1 | JWT 로그인 | T4 | Sprint 1 | POST /auth/login | POST |
| 2 | JWT 회원가입 (SUPPORT) | T4 | Sprint 1 | POST /auth/register | POST |
| 3 | JWT 회원가입 (CUSTOMER) | T4 | Sprint 1 | POST /auth/register | POST |
| 4 | 토큰 재발급 | T4 | Sprint 1 | POST /auth/refresh | POST |
| 5 | 로그아웃 | T4 | Sprint 1 | POST /auth/logout | POST |
| 6 | JWT 토큰 검증 (AuthFilter) | T4 | Sprint 1 | /auth/** | JWT Filter |
| 7 | Role hierarchy 설정 (SecurityConfig) | T4 | Sprint 1 | Security Config | Config |
| 8 | @PreAuthorize RBAC (Security Config) | T4 | Sprint 1 | /API/** | Security |
| 9 | POST /companies (DEVELOPER 등록) | T4 | Sprint 1 | POST /companies | ADMIN only |
| 10 | POST /companies (CLIENT 등록) | T4 | Sprint 1 | POST /companies | ADMIN/SUPPORT |
| 11 | Company CRUD | T4 | Sprint 1 | /companies/* | CRUD |
| 12 | POST /customer-register (고객사 회원가입) | T4 | Sprint 1 | POST /customer-register | ADMIN/SUPPORT |

### 5.2 Ticket CRUD / Workflow Engine (전역 Task ID: 145,147,149,151,153,155,157,174,176-178,180,182,183,184)

| # | 작업 | 담당 | Sprint | API Path | Method |
|---|--------|------|--------|----------|--------|
| 1 | Ticket 생성 | T4 | Sprint 2 | POST /tickets | CUSTOMER |
| 2 | Ticket 목록 조회 (역할별) | T4 | Sprint 2 | GET /tickets | ROLE-Filtered |
| 3 | Ticket 상세 조회 (동일 Project) | T4 | Sprint 2 | GET /tickets/{id} | Same Project |
| 4 | 상태 전이 - REGISTERED→RECEIVED | T4 | Sprint 2 | PUT /tickets/{id}/status | SUPPORT |
| 5 | 상태 전이 - RECEIVED→PROCESSING | T4 | Sprint 2 | PUT /tickets/{id}/status | SUPPORT |
| 6 | 상태 전이 - PROCESSING→COMPLETION_REQUESTED | T4 | Sprint 2 | PUT /tickets/{id}/status | SUPPORT |
| 7 | 완료 승인 | T4 | Sprint 2 | PUT /tickets/{id}/approve | APPROVER |
| 8 | 진행 상황 업데이트 | T4 | Sprint 2 | PUT /tickets/{id}/status | SUPPORT |
| 9 | 지연 변경 | T4 | Sprint 2 | PUT /tickets/{id}/delay | SUPPORT |
| 10 | Ticket soft delete | T4 | Sprint 2 | DELETE /tickets/{id} | ADMIN only |
| 11 | Ticket FTS 검색 (pg_trgm+FTS) | T2 | Sprint 2 | GET /tickets?keyword=x | Search |
| 12 | 상태 전이 이력 기록 (ticket_histories) | T4 | Sprint 2 | Auto | History |
| 13 | Optimistic Locking (version) | T4+T2 | Sprint 2 | /tickets/** | Lock+Refresh |

### 5.3 Real-Time Chat / Notifications (전역 Task ID: 168,171,173,175,177,182,184,186,188,190,194,196)

| # | 작업 | 담당 | Sprint | API Path | Method |
|---|--------|------|--------|----------|--------|
| 1 | WebSocket 서버 구성 (SockJS cfg) | T4 | Sprint 3 | WS /chats/** | WebSocket |
| 2 | Chat 메시지 전송 | T4 | Sprint 3 | WS /chats/send | SockJS |
| 3 | Chat 구독 (STOMP SUB) | T3 | Sprint 3 | SUB chat.{ticketId} | STOMP |
| 4 | Typing Indicator | T3 | Sprint 3 | SUB /ws/typing | STOMP |
| 5 | Chat 파일 첨부 (S3 업로드) | T4 | Sprint 3 | POST /tickets/{id}/files | Multipart |
| 6 | 댓글 작성 (대댓글 지원 REST) | T4 | Sprint 2 | POST /tickets/{id}/comments | POST |
| 7 | 알림 이벤트 정의 (EVT-01~10) | T5 | Sprint 3 | Notification Config | - |
| 8 | 알림 구독 설정 | T4 | Sprint 3 | POST /notification_event_subscriptions | POST |
| 9 | 알림 발송 API | T5 | Sprint 3 | POST /notifications/send | POST |
| 10 | 알림 팝업 UI | T2 | Sprint 3 | In-App /popup | - |
| 11 | 알림 사이드바 UI | T2 | Sprint 3 | In-App /sidebar | - |
| 12 | Quiet Hours 설정 | T5 | Sprint 3 | PUT notification_preferences | PUT |

### 5.4 Card Management (전역 Task ID: 118,120,128,140-141,186)

| # | 작업 | 담당 | Sprint | API Path | Method |
|---|--------|------|--------|----------|--------|
| 1 | POST /cards (관리자 카드 생성) | T4 | Sprint 1 | POST /cards | ADMIN/SUPPORT |
| 2 | PUT /cards/{id} (카드 수정) | T4 | Sprint 1 | PUT /cards/{id} | Authenticated |
| 3 | DELETE /cards/{id} (카드 삭제) | T4 | Sprint 1 | DELETE /cards/{id} | ADMIN |
| 4 | GET /cards (카드 목록 조회) | T2 | Sprint 1 | GET /cards | All users |
| 5 | 관리 카드 CRUD UI | T3 | Sprint 1+3 | /projects/{id}/cards | - |
| 6 | 프로젝트 카드 JSONB 저장 | T5 | Sprint 1 | management_cards | JSONB |

### 5.5 Admin Panel (전역 Task ID: 138,140,176-183,206,209+210,212,217)

| # | 작업 | 담당 | Sprint | UI Path |
|---|--------|------|--------|--------|
| 1 | ADMIN 대시보드 | T3 | Sprint 2+3 | /admin/dashboard |
| 2 | Company 관리 | T3 | Sprint 1 | /admin/companies |
| 3 | User 관리 (CRUD+역할) | T3 | Sprint 2 | /admin/users |
| 4 | Project 관리 | T3 | Sprint 1 | /admin/projects |
| 5 | 티켓 일괄 관리 | T3 | Sprint 2 | /admin/tickets |
| 6 | 배정 관리 | T3 | Sprint 2 | /admin/assignments |
| 7 | 개발사 등록 | T3 | Sprint 1 | /admin/companies/create |
| 8 | 고객사 등록 | T3 | Sprint 1 | /admin/companies/client |
| 9 | 프로젝트 관리 | T3 | Sprint 1 | /admin/projects |

---

### 기능별 매핑 Task ID 전역 매핑 테이블

| 기능 | # | 전역 Task ID | Sprint | 비고 |
|------|---|-------------|--------|------|
| **5.1 Auth/RBAC** | 1 | 109 | Sprint 1 | POST /auth/login |
| | 2 | 110 | Sprint 1 | POST /auth/register (SUPPORT) |
| | 3 | 111 | Sprint 1 | POST /auth/register (CUSTOMER) |
| | 4 | 112 | Sprint 1 | POST /auth/refresh |
| | 5 | 113 | Sprint 1 | POST /auth/logout |
| | 6 | 114 | Sprint 1 | JWT AuthFilter |
| | 7 | 115 | Sprint 1 | Role hierarchy (SecurityConfig) |
| | 8 | 116 | Sprint 1 | @PreAuthorize RBAC |
| | 9 | 117 | Sprint 1 | POST /companies (DEVELOPER) |
| | 10 | 118 | Sprint 1 | POST /companies (CLIENT) |
| | 11 | 119 | Sprint 1 | /companies/* CRUD |
| | 12 | 120 | Sprint 1 | POST /customer-register |
| **5.2 Ticket CRUD** | 1 | 145 | Sprint 2 | POST /tickets |
| | 2 | 147 | Sprint 2 | GET /tickets |
| | 3 | 149 | Sprint 2 | GET /tickets/{id} |
| | 4 | 151 | Sprint 2 | PUT /tickets/{id}/status (R→RE) |
| | 5 | 153 | Sprint 2 | PUT /tickets/{id}/status (RE→PR) |
| | 6 | 155 | Sprint 2 | PUT /tickets/{id}/status (PR→CR) |
| | 7 | 157 | Sprint 2 | PUT /tickets/{id}/approve |
| | 8 | 174 | Sprint 2 | PUT /tickets/{id}/status (진행更新) |
| | 9 | 176 | Sprint 2 | PUT /tickets/{id}/delay |
| | 10 | 178 | Sprint 2 | DELETE /tickets/{id} |
| | 11 | 180 | Sprint 2 | GET /tickets?keyword (FTS) |
| | 12 | 182 | Sprint 2 | ticket_histories (자동 이력) |
| | 13 | 183~184 | Sprint 2 | Optimistic Locking (version) |
| **5.3 Chat/Notifications** | 6 | 175 | Sprint 2 | POST /tickets/{id}/comments (대댓글) |
| | 1 | 186 | Sprint 3 | WebSocket/SockJS 서버 구성 |
| | 2 | 188 | Sprint 3 | WS /chats/send |
| | 3 | 190 | Sprint 3 | SUB chat.{ticketId} |
| | 4 | 194 | Sprint 3 | SUB /ws/typing |
| | 5 | 196 | Sprint 3 | POST /tickets/{id}/files |
| | 7 | 198 | Sprint 3 | EVT-01~10 알림 이벤트 정의 |
| | 8 | 200 | Sprint 3 | POST /notification_event_subscriptions |
| | 9 | 202 | Sprint 3 | POST /notifications/send |
| | 10 | 214 | Sprint 3 | 알림 팝업 UI |
| | 11 | 215 | Sprint 3 | 알림 사이드바 UI |
| | 12 | 216 | Sprint 3 | Quiet Hours |
| **5.4 Card Management** | 1 | 128 | Sprint 1 | POST /cards |
| | 2 | 140 | Sprint 1 | PUT /cards/{id} |
| | 3 | 141 | Sprint 1 | DELETE /cards/{id} |
| | 4 | 142 | Sprint 1 | GET /cards |
| | 5 | 206 | Sprint 1+3 | 카드 CRUD UI |
| | 6 | 187 | Sprint 1 | JSONB management_cards |
| **5.5 Admin Panel** | 1 | 183 | Sprint 2+3 | Admin Dashboard |
| | 2 | 138 | Sprint 1 | /admin/companies |
| | 3 | 176 | Sprint 2 | /admin/users |
| | 4 | 210 | Sprint 1 | /admin/projects |
| | 5 | 212 | Sprint 2 | /admin/tickets |
| | 6 | 190 | Sprint 2 | /admin/assignments |
| | 7 | 128 | Sprint 1 | /admin/companies/create |
| | 8 | 138 | Sprint 1 | /admin/companies/client |
| | 9 | 210 | Sprint 1 | /admin/projects |
| 10 | 프로젝트 관리 UI (SideMenu + CardGrid) | T3 | Sprint 3 | /projects/{id} |
| 11 | System Settings | T3 | Sprint 4 | /admin/settings |


---

## 6. Sprint 예약 및里程碑 (Sprint Schedule & Milestones)

### 6.1 Sprint 타임라인

```
Sprint 0 (Setup)   |========| (2주)
Sprint 1 (Core)    |============| (4주)
Sprint 2 (Ticket)  |============| (4주)
Sprint 3 (RealTime)|=======| (3주)
Sprint 4 (QA/Rel)  |==| (2주)

Week 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16
```

### 6.2 상세 Sprint 스케줄

| Sprint | 시작 | 끝 | 주요 작업 | 마일스톤 |
|--------|------|----|---------|---------|
| 0 | W1-2 | W2 | 환경 구축, DB 스키마, CI/CD | M0: 개발 환경 완성 |
| 1 | W3-6 | W6 | Auth, Company/Project/Card CRUD | M1: Core 기능 |
| 2 | W7-10 | W10 | Ticket 워크플로우, 상태 전이 | M2: Ticket 엔진 |
| 3 | W11-13 | W13 | 실시간 채팅, 알림, 카드 UI | M3: Real-time |
| 4 | W14-15 | W15 | QA, DevOps, UAT, Release | M4: Release v0.1 |

### 6.3 마일스톤 정의 (M0-M5)

| 마일스톤 | Sprint | 목표 | 완료 기준 |
|---------|--------|------|----------|
| M0: 환경 완성 | Sprint 0 | 개발 환경, CI/CD, DB 스키마 | 모든 서비스 로컬 기동, CI/CD 파이프라인 가동 |
| M1: 핵심 기능 | Sprint 1 | Auth, RBAC, CRUD | Login/Register, Company/Project/Card CRUD |
| M2: Ticket 엔진 | Sprint 2 | Ticket 워크플로우 | 7 상태, 6 전이 규칙, Optimistic Locking |
| M3: 실시간 기능 | Sprint 3 | WebSocket, 알림 | 실시간 채팅, Notification, Quiet Hours |
| M4: Release | Sprint 4 | QA, UAT, Deployment | 테스트 100% 통과, AWS 배포 |
| M5: Post-Release | Sprint 4+ | 모니터링, 개선 | Grafana 모니터링, UAT 피드백 반영 |

---

## 7. Deliverable Checklist & QA 기준

### 7.1 Deliverable Checklist

#### Sprint 0: 환경 구축
- [ ] Git 레포지토리 생성 (Git Flow 브랜치 전략)
- [ ] Docker Compose 구성 (PostgreSQL, Redis, RabbitMQ)
- [ ] Frontend 프로젝트 초기화 (Vite + React + TypeScript)
- [ ] Backend 프로젝트 생성 (Spring Boot + Gradle)
- [ ] Flyway 마이그레이션 (V1-V6: 26개 테이블)
- [ ] GitLab CI 파이프라인 (빌드, 테스트, lint)
- [ ] ESLint + Prettier (FE)
- [ ] SpotBugs + Checkstyle (BE)
- [ ] TailwindCSS + daisyUI (FE)
- [ ] MyBatis + PostgreSQL 연결

#### Sprint 1: Core 기능
- [ ] POST /auth/login, /auth/register API
- [ ] JWT 토큰 발급/검증
- [ ] Spring Security JWT 필터 체인
- [ ] @PreAuthorize RBAC 구현
- [ ] POST/GET/PUT/DELETE /companies API
- [ ] POST/GET/PUT/DELETE /projects API
- [ ] POST/GET/PUT/DELETE /cards API
- [ ] POST/GET/PUT/DELETE /assignments API
- [ ] Company 관리 UI
- [ ] Project 관리 UI
- [ ] 프로젝트 카드 CRUD UI (Server/SW 카드 그리드)
- [ ] Login/Register UI
- [ ] 403 Forbidden 검증 (역할별)

#### Sprint 2: Ticket 시스템
- [ ] POST/GET/PUT/DELETE /tickets API
- [ ] 상태 전이 상태 7, 6 전이 규칙
- [ ] Optimistic Locking (version column, 409 Conflict)
- [ ] Ticket 상세 페이지 UI (Status Ribbon, Action Buttons, History Timeline)
- [ ] Processing Plan CRUD + 승인/반려
- [ ] Extension Request + 승인/반려
- [ ] Chat/Comment 섹션 UI (댓글, 대댓글, 첨부파일)
- [ ] Ticket FTS 검색
- [ ] 티켓 일괄 관리 UI (Admin Panel)

#### Sprint 3: 실시간 기능
- [ ] WebSocket 서버 (STOMP over SockJS)
- [ ] 실시간 채팅 UI (버블, 파일 첨부, typing indicator)
- [ ] WebSocket 재연결 로직
- [ ] Notification 이벤트 (EVT-01~10)
- [ ] 알림 발송 (In-App, Push, Email, SMS)
- [ ] Quiet Hours 및 Batched 발송
- [ ] Notification Popup, Sidebar, Badge UI
- [ ] Project Detail 페이지
- [ ] 프로젝트 카드 관리 UI
- [ ] Long-polling fallback

#### Sprint 4: QA/Release
- [ ] 단위 테스트: 80%+ 커버리지
- [ ] 통합 테스트: 전체 API 검증
- [ ] E2E 테스트: 핵심 흐름 100% 통과
- [ ] 성능 테스트: 100 WebSocket, 500 TPS
- [ ] 보안 검증: Critical/High 漏洞 0건
- [ ] Accessibility: WCAG 2.1 AA
- [ ] Mobile 호환성 테스트
- [ ] UAT 완료
- [ ] AWS EKS 배포
- [ ] Grafana 모니터링
- [ ] v0.1 Release

### 7.2 QA 기준 (QA Standards)

#### 코드 품질 (Code Quality)

| 항목 | 기준 |
|------|------|
| TypeScript 코드 | ESLint 준수, noImplicitAny 0건 |
| Java 코드 | SpotBugs Clean, Checkstyle 통과 |
| 테스트 커버리지 | 단위 테스트 >=80% |
| 통합 테스트 | 전체 API 흐름 테스트 포함 |
| Lint 오류 | 0건 (PR 체크리스트 항목) |
| 코드 리뷰 | 최소 1명 이상의 승인 |

#### 기능 검증 (Functional Validation)

| 항목 | 기준 |
|------|------|
| Login/Register | 모든 시나리오 통과 |
| RBAC | 403 Forbidden correctly applied |
| Ticket 상태 전이 | 7 상태, 6 전이 규칙 검증 |
| Optimistic Locking | 409 Conflict 시 auto-refresh UX |
| WebSocket | 실시간 채팅, 재연결, typing indicator |
| Notification | 모든 채널 (In-App, Push, Email, SMS) |
| Quiet Hours | Quiet Hours 중 알림 Queue, 종료 후 발송 |
| Batched 알림 | 5분 단위 묶음 발송 |
| Project CRUD | CRUD + soft delete |
| Card CRUD | CRUD + JSONB |
| Mobile Responsive | Mobile-first, Touch target 44px+ |
| Accessibility | WCAG 2.1 AA |

#### 성능 테스트 (Performance Testing)

| 항목 | 목표 가치 |
|------|----------|
| REST API TPS | >= 500 TPS |
| WebSocket Connection | <= 100 동시 연결 |
| P99 Response Time | < 500ms |
| DB Query Time | < 50ms (인덱스 활용) |
| WebSocket Latency | < 50ms |
| Initial Load Time | < 3s |

#### 보안 검증 (Security Validation)

| 항목 | 기준 |
|------|------|
| SQL Injection | 테스트 0건 |
| XSS | 테스트 0건 |
| CSRF | 토큰 검증 적용 |
| JWT 검증 | 유효성 검사 필수 |
| RBAC Bypass | 테스트 0건 |
| File Upload | Type+Size 검증 |
| CORS | Origin 검증 |
| Rate Limiting | API Gateway 적용 |

---

## 8. 위험 관리 (Risk Management)

| 리스크 | 발생 가능성 | 영향도 | 완화 전략 | 담당 |
|--------|----------|--------|----------|------|
| Ticket 워크플로우 복잡성으로 인한 개발 지연 | Medium | High | State Machine 패턴 적용, 단계별 구현 (Sprint 2 분리) | T1+T4 |
| WebSocket 연결 불안정 | Medium | Medium | Long-polling fallback, Reconnection logic 구현 | T3+T4 |
| RBAC 구현 복잡성 | Medium | Medium | Role hierarchy 명확히 설정, 테스트 커버리지 확보 | T4+T6 |
| 대용량 File Upload 처리 | Low | Medium | Chunked Upload, Progress bar 표시 | T4+T2 |
| 국내 클라우드 규제 준수 (정보보호) | Low | High | NAVER Cloud LaunchSOL 대안 준비, 인트라넷 배포 옵션 검토 | T1 |
| Scope Creep (기능 확장 요청) | High | Medium | Phase 기반 분기, 추가 기능은 차기 Release로 미루기 | T1 |
| DB 버전 충돌 (개발/Stage/Prod) | Medium | High | Flyway Migration 도입, 모든 DB 변경은 Migration Script로만 적용 | T5 |
| Optimistic Locking 충돌 | Medium | Medium | version column 적용, 409 Conflict 처리 + auto-refresh UX | T4+T2 |
| Notification Spam (대량 발송) | Medium | Medium | Batching Policy (5분묶음), 일일 50건 Cap, Quiet Hours | T5 |
| 팀 과부하 (인력 부족) | Medium | High | Sprint별 할당량 확인, 추가 인력 또는 Sprint 연장 검토 | T1 |
| 기술 부채 (코드 품질 저하) | Medium | Medium | 정기적인 코드 리뷰, Refactor Sprint 도입 | T1 |

---

## 9. Git 워크플로우 (Git Workflow)

### 9.1 브랜치 전략 (Git Flow)

```
main (Production)
  |
  |--- develop (All features merge here)
       |
       |--- feat/feature-x     (New features)
       |--- fix/bug-y          (Bug fixes)
       |--- refactor/anything  (Refactoring)
       |--- chore/dep-update   (Dependencies, CI/CD)
       |--- docs/abc           (Documentation)
       |--- test/xyz           (Tests)

Hotfix:
  |--- hotfix/critical-fix  (Production critical bug fixes)
       |
       |--- main (merge back)
       |--- develop (merge back)
```

#### 브랜치 네이밍 컨벤션

| 유형 | 컨벤션 | 예시 |
|------|--------|------|
| Feature | `feat/ISSUE-NUM-description` | `feat/T-123-ticket-workflow` |
| Bug fix | `fix/ISSUE-NUM-description` | `fix/T-124-jwt-refresh` |
| Refactor | `refactor/ISSUE-NUM-description` | `refactor/T-125-state-machine` |
| Chore | `chore/ISSUE-NUM-description` | `chore/T-126-dependency-update` |
| Docs | `docs/ISSUE-NUM-description` | `docs/T-127-api-docs` |
| Test | `test/ISSUE-NUM-description` | `test/T-128-ticket-crud` |
| Hotfix | `hotfix/ISSUE-NUM-description` | `hotfix/P-001-jwt-expiry` |

### 9.2 커밋 컨벤션 (Conventional Commits)

```
type: subject

body (optional)

footer (optional)
```

**Type:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `chore`: 설정 변경, 의존성, 빌드
- `docs`: 문서 변경
- `test`: 테스트 추가/수정
- `ci`: CI 변경
- `build`: 빌드 시스템 변경
- `perf`: 성능 개선
- `style`: 코드 스타일 (세미콜론, etc.)

**예시:**
```
feat(T-123): add ticket state transition engine

- REGISTERED -> RECEIVED
- RECEIVED -> PROCESSING
- Optimistic locking (version column) via MyBatis

Fixes: T-123
Co-Authored-By: Team Member <email>
```

### 9.3 PR 체크리스트 (Pull Request Checklist)

모든 PR은 다음 체크리스트를 완료해야 머지 가능:

**코드 변경 (Code Changes)**
- [ ] 변경 사항에 대한 테스트 포함
- [ ] 로컬에서 빌드 통과
- [ ] ESLint 체크: 0 오류
- [ ] SpotBugs/Checkstyle: 0 오류
- [ ] 코드 리뷰 최소 1명 이상 승인
- [ ] 커밋 컨벤션 준수

**기능 검증 (Functional)**
- [ ] 기능 요구사항 충족
- [ ] 이전 기능 깨지지 않음 (No Regression)
- [ ] 403 Forbidden (RBAC) correctly applied
- [ ] Error handling 구현

**문서 (Documentation)**
- [ ] API 변경 시 Swagger/OpenAPI 문서 업데이트
- [ ] 신규 기능 시 README/사용 가이드 업데이트

**성능/보안 (Performance & Security)**
- [ ] 성능 저하 확인 (P99 < 500ms)
- [ ] SQL Injection, XSS 테스트 통과
- [ ] JWT 검증, Rate Limiting 적용

**CI/CD**
- [ ] CI 파이프라인 100% 통과

### 9.4 릴리즈 과정 (Release Process)

1. **Release planning:** Sprint 종료 시, PR merge 완료 확인
2. **Tagging:** `git tag v0.1.0 develop`
3. **Release branch 생성:** `git checkout -b release/v0.1.0 develop`
4. **Version bump:** package.json, application.yml 버전 변경
5. **Test on staging:** `git push origin release/v0.1.0`
6. **Merge to main:** `git checkout main && git merge release/v0.1.0`
7. **Deploy to production:**
   - Docker 이미지에 빌드
   - AWS EKS에 적용 (kubectl apply -f deployment.yml)
   - Health check 통과 확인
8. **Rollback plan:** 실패 시 `kubectl rollout undo deployment/app`
9. **Release notes 작성:**
   - 신규 기능
   - 버그 수정
   - 개선 사항
   - Breaking Changes (존재 시)

### 9.5 Hotfix 과정

1. Production에서 Critical Bug 발생
2. `git checkout -b hotfix/ISSUE-NUM-description main`
3. Hotfix 개발 + 테스트
4. PR merge to main
5. `git checkout develop && git merge hotfix/ISSUE-NUM-description`
6. Production 배포
7. Issue Close

