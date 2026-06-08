# nu_Trust 개발 계획서

**Version:** 1.0
**Date:** 2026년 5월 30일
**Company:** (주)엔유비즈
**Slogan:** Empowering Connections, Building Trust. (연결에 힘을 더하고, 신뢰를 구축합니다.)

---

## 1. 프로젝트 개요

### 1.1 목적

nu_Trust (신뢰 구축 플랫폼)은 (주)엔유비즈, SW 개발 기업에 특화되어 설계된 클라이언트 관계 관리 플랫폼입니다. 이 플랫폼은 체계적이고 투명하며 추적 가능한 통신 체계(Smart Communication System)를 제공함으로써, 개발 팀과 클라이언트 간의 격차를 해소합니다.

### 1.2 핵심 철학

클라이언트와 개발 기업 간의 모든 상호작용(Interaction)은 공식적으로 기록됩니다. 클라이언트가 제출한 이슈(Issue)는 티켓(Ticket)으로 변환되며, 이 티켓은 명확한 처리 워크플로우(Workflow)를 거칩니다. 실시간 채팅과 스레딩 타입 댓글은 양측 간의 통신을 지속 가능하게 유지합니다. 이렇게 누적된 상호작용의 기록이 신뢰의 기반(A foundation of trust)이 됩니다.

### 1.3 주요 목표

- 클라이언트가 이슈(Issue)를 공식적인 티켓(Ticket) 시스템을 통해 제출하고 추적할 수 있도록 지원
- 개발팀이 요청(Request)을 처리하기 위한 구조화된 워크플로우(Workflow)를 보유
- 각 티켓(Ticket) 내부에서 실시간 통신(Real-time Communication) 기능 제공
- 신뢰와 책임(Audit Trail)을 강화하는 완전한 이력 추적(Audit Trail) 체계 구축
- 회사(Company), 사용자(User), 프로젝트(Project) 관리를 위한 관리자 패널(Admin Panel) 제공

### 1.4 대상 사용자 (Target Users)

| 사용자 타입 | 설명 | 권한 |
|-----------|------|------|
| 클라이언트 (Customer) | 이슈를 제출하는 회사 측 인사 | 티켓 생성, 댓글 작성, 자신 관련 티켓 목록 조회 |
| 지원_staff (Support Staff) | 티켓을 처리하는 개발팀 멤버 | 티켓 관리, 배정, 처리, 응답 |
| 시스템 관리자 (System Admin) | 플랫폼 관리자 | 전체 시스템 구성, User 관리 |
| 프로젝트 매니저 (Project Manager) | 프로젝트 리딩 | 프로젝트 전체 범위 관리 및 Oversight |

---

## 2. 벤치마킹 조사 결과

### 2.1 국내 서비스 분석

다음 국내 IT 서비스는 nu_Trust 모델과 관련성이 높은 사례로 연구되었습니다:

| 서비스 | 주요 특징 | 관련 참고 Features |
|---------|---------|-------------------|
| **Riido** | AI 기반 프로젝트 관리 | 현대적인 UI 디자인 패턴, 국내 시장 최적화, AI 기반 Task 배정 |
| **WatchTek** | ITSM with 자동 감지 | SLA 관리, 서비스 카탈로그, 국내 기업 지원 워크플로우 |
| **Telemant** | 통합 서비스 데스크 | 다중 통신 채널을 위한 통합 인터페이스, Ticket 통합 |
| **Yooncoms** | ITSM 솔루션 | 국내 비즈니스 프로세스 통합, 리포트 생성 |
| **STeG E-GENE ITSM** | 기업용 ITSM | 기업급 Workflow 엔진, Role Hierarchy, 국내 규제 준수 |
| **KROOT** | AI 소프트웨어 프로젝트 플랫폼 | AI 기반 프로젝트 리스크 예측, 자원 배정 자동화 |

### 2.2 해외 서비스 분석

| 서비스 | 주요 특징 | 관련 참고 Features |
|---------|---------|-------------------|
| **Linear** | 프로젝트/이슈 추적 | Minimalist UI, 빠른 상태 전환 워크플로우, Team용 실시간 동기화 |
| **Jira** | 엔터프라이즈 프로젝트 관리 | 고도화된 Workflow 엔진, 방대한 커스터마이징 가능 |
| **GitHub Issues** | Git 기반 개발용 이슈 추적 | Lightweight Ticket 시스템, Commit/PR 연계 |
| **Asana** | 팀 업무 관리 | 간결한 Task 구조, 시각적 Timeline |
| **Front** | 공유 이메일/메신저 | Ticket + 채팅 통합형 UX |
| **Intercom** | 고객 소통 플랫폼 | Real-time 채팅 + Ticket 통합 + 지능형 라우팅 |

### 2.3 벤치마킹 분석 요약 (Analysis Summary)

| 항목 | Key Findings |
|------|-------------|
| **UI 스타일** | Linear의 미니멀하고 직관적인 디자인 참고 |
| **Workflow** | Jira의 상태 전이(State Transition) 모델과 Asana의 가벼운 UX 결합 |
| **실시간 통신** | Intercom의 채팅 + 티켓 통합 UX와 Front의 멀티채널 통합 패턴 참고 |
| **국내 적응** | 전자정부 표준프레임워크의 검증된 아키텍처와 국내 IT 서비스의 Business Model 적용 |
| **신뢰 구축** | 모든 Interaction을 기록하는 Audit Trail을 핵심 차별점으로 설정 |

---

## 3. 시스템 아키텍처

### 3.1 전체 아키텍처 (Architecture Overview)

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
|  (ORM)                        +----+-----+-----+                      |
+---+----+---------------------+ Queue: ticket.notify  |                  |
      |                            |   |    |          |
+---v----+---------------------+ Queue: ticket.ws       |                  |
|  PostgreSQL                       +---> Queue: ticket.expired           |
|  (RDS / Private Subnet)                                 |                  |
+---------------------------------------------------------+                  |
|                                                                              |
+------------------------------------------------------------------------------+
 NOTE: This ASCII art is a simplified overview of the actual architecture.
```

### 3.2 구성 요소 (Components)

- **Frontend (React.js, TypeScript):** SPA 기반 웹 애플리케이션으로, Ticket 관리, 채팅, Admin Panel UI 제공.
- **Backend (Spring Boot 3.x, Java):** REST API, WebSocket 서버, Ticket Workflow 엔진을 담당하는 Application 서버.
- **Database (MyBatis, PostgreSQL):** Application 데이터 영속성 (Persistence).
- **Message Broker (RabbitMQ):** 비동기 Task 처리 (Notification, WebSocket 브로드캐스팅, 만료 감지 Cron).
- **Communication (WebSocket):** 실시간 채팅, Typing Indicator, 새 메시지 브로드캐스팅.
- **Fallback:** 브라우저 호환성을 위한 Long-polling 지원.

---

### 3.4 API Gateway Policy (API 게이트웨이 정책)

모든 외부 요청은 API Gateway 를 경유하여 Backend 서비스로 라우팅됩니다. GateWay 는 인증 검증, Rate Limiting, 요청 라우팅, 로깅, CORS 처리 등 공통 Cross-Cutting Concerns 를 담당합니다.

**Gateway 기술选型 (Technology Selection):**

| 옵션 | 특징 | Nu-Trust 적합성 |
|------|------|-----------------|
| **NGINX / NGINX Plus** | 고성능 Reverse Proxy, Rate Limiting 내장, JWT Header 검증 가능 | Small-to-Medium scale 에 적합, 운영 단순화 |
| **Kong Gateway** | Open-source NGINX 기반, Plugin ecosystem (JWT, Rate Limit, AuthZ) 풍부, Admin Dashboard 포함 | Plugin 확장성 우수, 대규모 scale 에 적합 |
| **AWS ALB (Application Load Balancer)** | AWS Fully Managed, WAF 연동, Target Group 라우팅 | AWS 완전 이점 시 유용,하지만 API Level Rate Limiting 은 제한적 |

**Nu-Trust 권장:** **Staging/Prod 에서 Kong Gateway 또는 NGINX Plus 사용.** Kong 은 Plugin(예: kong-plugin-rate-limiting, kong-plugin-jwt-auth)으로 API Level Rate Limiting 과 JWT 검증을 Gateway Level 에서 처리할 수 있어 Backend 서비스의 부담을 줄일 수 있습니다. Small-to-Medium 초기에는 NGINX 로 시작하고, 트래픽 증가 시 Kong 또는 EKS + ALB 로 확장할 수 있는 아키텍처를 목표합니다.

**API Gateway 에서 처리할 항목:**
1. **Authentication:** JWT Token Header 검증 (Access Token 유효성, Expired Time)
2. **Rate Limiting:** Endpoint Type 별 Rate Limiting 적용 (Section 3.5)
3. **CORS:** Origin 검증 (Frontend 도메인만 허용)
4. **Request Logging:** 모든 요청/응답 latency, status code, method, endpoint logging
5. **TLS Termination:** HTTPS 종료, 내부 통신은 HTTP (Private Subnet 내)

---

### 3.5 Endpoint Rate Limiting Policy (엔드포인트별 요청 속도 제한 정책)

API Gateway Level 에서 Endpoint Type 별 Rate Limiting 을 적용하여 DDoS, Brute-Force 공격 및 자원의 남용을 방지합니다.

**Endpoint Type 별 Rate Limit 값:**

| Endpoint Type | Rate Limit | Window | 적용 예시 |
|--------------|-----------|--------|----------|
| Login / Authentication | 10 requests / minute | IP 기반 | Brute-Force password 공격 방어 |
| Password Reset | 5 requests / hour | IP 기반 | 재설정 토큰 남용 방지 |
| Ticket Create | 30 requests / hour | User 기반 | Spam Ticket 방지 |
| Standard CRUD (조회/수정) | 120 requests / minute | User 기반 | Normal usage |
| WebSocket Connect | 5 connections / minute | User 기반 | WebSocket 남용 방지 |
| File Upload | 10 requests / hour | User 기반 | 대용량 Upload 남용 방지 |
| Admin Bulk Operations | 10 requests / minute | Role 기반 | Bulk actions 남용 방지 |
| **All Endpoints (Global)** | **1000 requests / minute** | IP 기반 | DDoS 보호 |

**Rate Limit Response (HTTP 429 Too Many Requests):**
```json
{
  "error": "RATE_LIMITED",
  "message": "요청 한도를 초과했습니다. 30 초 후 다시 시도하세요.",
  "retryAfter": 30
}
```
- Response Header 에 `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` 포함
- Client-side: 429 응답 시 Retry-After 값만큼 대기 후 exponential backoff (초기 1초, 최대 30초)

---

### 3.6 Cache Invalidation Policy (캐시 무효화 정책)

Redis Cache 사용 시 **무효화 정책 (Invalidation Policy)** 을 명확히 정의하여 Stale Data (낡은 데이터) 가 제공되는 것을 방지합니다.

**Cache Usage 원칙:**
- Cache 는 **Read-Through / Write-Through** 패턴으로 운영
- Cache 는 절대 Source of Truth 가 되어서는 안됨
- 모든 Write Operation 은 즉시 Cache 를 무효화하거나 Update 함

**Cache Invalidation 전략:**

| 전략 | 적용 대상 | 설명 |
|------|---------|------|
| **TTL 기반 (expire)** | User Session, JWT Blacklist, frequently accessed Ticket List | TTL 5~30 분 (데이터 freshness 요구도 기준) |
| **Event-based Invalidation (DEL)** | Ticket Detail, Project Data, User Profile | Write(Insert/Update/Delete) 시 해당 Key 즉시 DEL |
| **Write-Through (SET + Expire)** | Processing Plan, Notification Status | Write 시 Cache 도 동시에 Update (일관성 보장) |
| **Lazy Expiration Check** | Cache Hit 시 TTL 확인 → 만료 시 DB 재호출 | Stale check |

**Cache Key Naming convention:** `{entity}:{id}:{version}` — version column 을 함께 사용하여 optimistic lock 과 함께 stale 캐시 방지.

**Redis Eviction Policy:** `allkeys-lru` (가장 최근에 사용되지 않은 Key부터 삭제) — Redis Memory Overflow 방지.

---

### 3.7 Database Unification (데이터베이스 통일 정책)

이 문서에서 **Frontend 기술스택의 Technology Stack (Section 9)** 에 MySQL 이 언급되어 있었으나, 모든 DDL 스키마 (Section 4) 는 PostgreSQL 15 로 작성되어 있습니다.

**결정: 모든 Database 는 PostgreSQL 15 로 통일합니다. (완료)**

MySQL 과 PostgreSQL 이 혼재되면 운영 복잡성 (2 가지 DBA 지식, 별도 Backups, 다른 Monitoring, 다른 CI Config) 이 기하급수적으로 증가합니다. Nu-Trust 는 PostgreSQL 15 단일 Database 를 원칙으로 합니다.

- **Data Persistence:** PostgreSQL 15 (Amazon RDS)
- **Cache:** Redis (Amazon ElastiCache) — Cache Layer 는 별도 (DB Engine 상관 없음)
- **Migration Tool:** PostgreSQL Migration 전문 도구 사용 (아래 Section 4.11.1 참조)

**전량 교체 완료 (Section 9, ASCII Diagram, Section 3.2):** Section 9 Technology Stack, Section 3.2 구성 요소 목록, Section 3 구조 도면 등 MySQL 을 언급한 모든 항목이 PostgreSQL 15 로 치환되었습니다.

---

## 4. 데이터 모델 (ERD) — 요약

### 개요

PostgreSQL 15 기반의 관계형 데이터 모델. 총 **26개 테이블** (3개 파티션 테이블 포함)로 구성되며, RBAC, 파티션, 소프트 딜리트, FTS, JSONB 등 현대적 DB 패턴을 활용.

**상세 전체 스키마(DDL, 인덱스, 파티션, 트리거, 제약조건)는 별도 문서를 참조:**
📄 **[데이터 모델 (ERD) — 전체 스키마](./nu-trust-data-model-erd.md)**

### 주요 구성

| 섹션 | 내용 |
|------|------|
| 4.1 DDL 전체 스키마 | PostgreSQL 15 DDL — 26개 테이블 정의, pgcrypto/btree_gin extension, 시간대 설정 |
| 4.1.2 companies | 회사 관리 (DEVELOPER/CLIENT 구분, business_number 유니크) |
| 4.1.3 users | RBAC (ADMIN/COMPANY_ADMIN/USER), 계층구조(parent_id), TRIMS 전략(하드딜리트만) |
| 4.1.4 projects | 프로젝트 관리, 소프트 딜리트(deleted_at), JSONB metadata |
| 4.1.5~4.1.7 junction/related | project_managers, project_support_managers, project_assignments, card_assignments 등 |
| 4.1.8 tickets | 티켓 핵심 테이블, FTS(infrastructure_search_vector), JSONB notes |
| 4.1.9 processing_plans | 처리 계획 (JSONB plan_data) |
| 4.1.10~4.1.12 ticket_comments/files, chat_messages | 대댓글 지원, 첨부파일, 채팅 메시지(월별 파티션) |
| 4.1.13~4.1.15 extension_requests, ticket_histories | 데드라인 연장, 상태전환 이력(분기별 파티션) |
| 4.1.16 audit_logs | 전역 감사 로그, JSONB GIN 인덱스 |
| 4.1.17~4.1.19 notifications | 이벤트 타입/구독/선호도/발송 기록(JSONB payload, 월별 파티션) |
| 4.1.20~4.1.23 lookup tables | ticket/project status, priority, card_type (고정 값) |
| 4.1.24 login_history | 로그인 기록 (IP, 성공 여부 イン덱스) |
| 4.2 ERD 관계 정의 | 1:N, N:M junction 테이블, FK 제약조건, CASCADE/RESTRICT 정책 |
| 4.3 인덱스 정책 | B-tree 기본 + GIN(JSONB), FTS(infrastructure_search_vector), composite 인덱스 |
| 4.4 파티션 정책 | pg_catalog.partitions 확인, chat_messages(월별), ticket_histories(분기별), notification_logs(월별) |
| 4.5 소프트 딜리트 | projects, tickets deleted_at 활용, `WHERE deleted_at IS NULL` 필터 공통 |
| 4.6 트리거 | updated_at 자동 갱신, event-based 자동 생성 |
| 4.7 확장 테이블 | ticket_histories_part_master, notification_logs_part_master 등 |
| 4.8 요약 | 26개 테이블 전체 목록, 파티션/SoftDelete 매트릭스 |

---

**⚠️ 전체 DDL, 인덱스, 파티션, 트리거, ERD 상세는 별도 문서 참조:** [데이터 모델 (ERD) 전체](./nu-trust-data-model-erd.md)


## 5. 상태 정의 및 워크플로우

---

### 5.1 상태 정의 (States)

Ticket의 상태(State)는 다음 7가지로 정의됩니다:

| 상태 코드 | 한글 명칭 | 설명 | 색상 |
|-----------|----------|------|------|
| `REGISTERED` | 등록됨 | Ticket이 처음 생성된 상태 | Grey (#6B7280) |
| `RECEIVED` | 접수됨 | 개발팀이 Ticket을 확인하고 검토를 수용한 상태 | Blue (#3B82F6) |
| `PROCESSING` | 처리 중 | 실제 작업이 시작됨 | Amber (#F59E0B) |
| `DELAYED` | 지체됨 | 완료기한을 넘겨 상태 | Red (#EF4444) |
| `COMPLETION_REQUESTED` | 완료 요청됨 | 완료 요청 발생 (승인/반려 대상) | Purple (#8B5CF6) |
| `APPROVED` | 승인됨 | 완료 요청 승인 (COMPLETED로 자동 전이) | Green (#10B981) |
| `COMPLETED` | 완료됨 | 최종 완료 상태 - 읽기 전용 (Read-only), Archive | Dark Green (#047857) |

### 5.4 동시성 제어 전략 (Optimistic Locking)

동시 수정 충돌을 방지하기 위해 Optimistic Locking 패턴을 적용한다.

#### 5.4.1 version column 적용

모든 업데이트 대상 테이블에 `version BIGINT NOT NULL DEFAULT 0` column 추가:

```sql
ALTER TABLE tickets ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE projects ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE processing_plans ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER extension_requests ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
```

#### 5.4.2 업데이트 쿼리 패턴

```sql
-- version 체크 후 업데이트 (Spring Data JPA / MyBatis)
UPDATE tickets
SET status = 'PROCESSING', version = version + 1, updated_at = CURRENT_TIMESTAMP
WHERE id = ? AND version = ?;

-- 영향받은 row가 0개면 동시 충돌 발생
```

#### 5.4.3 충돌 처리 정책

| 상황 | HTTP Status | Response Message | Client Action |
|------|-------------|-----------------|---------------|
| 동시 수정 감지 (version mismatch) | 409 Conflict | "다른 사용자가 수정 중입니다. 페이지를 새로고침 해주세요." | 자동REFRESH + 메시지 표시 |
| 상태 전이 규칙 위배 | 422 Unprocessable | "현재 상태에서 해당 전이가 불가능합니다." | 상태 전이 규칙 안내 |

#### 5.4.4 적용 대상 테이블

| 테이블 | Optimistic Locking 적용 | 이유 |
|--------|----------------------|------|
| tickets | YES | 상태 전이 + 수정 동시성 방지가 핵심 |
| processing_plans | YES | 계획을 동시에 수정하는 것 방지 |
| extension_requests | YES | 연장 요청 승인/반려 동시성 |
| projects | YES | 프로젝트 정보 동시 수정 방지 |
| users | NO | RBAC로 접근 제어 + 드문 업데이트 |
| 조회 전용 테이블 | NO | 상태 타입, 우선순위 타입 등 |

---

### 5.2 상태 전이 다이어그램 (State Transition Diagram)

```
REGISTERED --> RECEIVED --> PROCESSING --> DELAYED --> COMPLETION_REQUESTED --> APPROVED --> COMPLETED
                                      |              ^
                                  (연장 신청)    (승인/반려)
```

### 5.3 상태 전이 규칙 (Transition Rules)

#### Rule 1: REGISTERED --> RECEIVED

```
REGISTERED --> RECEIVED
```

- 개발팀이 Ticket을 확인하고 검토를 수용하면 `RECEIVED`로 전이됩니다.
- 이 전이는 오직 Support Staff 이상이 수행할 수 있습니다.

#### Rule 2: RECEIVED --> PROCESSING

```
RECEIVED --> PROCESSING
```

- 처리 계획(Processing Plan)이 작성/승인되면 `PROCESSING`으로 전이됩니다.
- 클라이언트(Customer)와 개발팀(Support) 모두 처리 계획을 작성할 수 있습니다.
- 완료 기한(Deadline)은 RECEIVED 상태에서 클라이언트가 설정하거나, SUPPORT가 조정합니다.

#### Rule 3: PROCESSING --> DELAYED

```
PROCESSING --> DELAYED
```

- Ticket이 완료 기한을 경과하고 연장 요청이 승인되지 않으면 `DELAYED`로 자동 전이됩니다.
- 이 규칙은 주기적으로 Cron Job으로 실행되며, 만료된 Ticket을 감지합니다.

#### Rule 4: PROCESSING --> COMPLETION_REQUESTED (완료 요청)

```
PROCESSING --> COMPLETION_REQUESTED
```

- 클라이언트(Customer) 또는 개발팀(Support) 모두 완료 요청할 수 있습니다.
- 요청이 발생하면 `COMPLETION_REQUESTED` 상태로 전환되며, 승인자(Approver)의 검토 대기 상태가 됩니다.

#### Rule 5: COMPLETION_REQUESTED --> PROCESSING (완료 반려)

```
COMPLETION_REQUESTED --> PROCESSING (반려 시)
```

- Approver가 완료 요청을 반려하면 상태가 `PROCESSING`으로 되돌아갑니다.
- 반려 사유(Reason)는 기록되며, 이는 해당 Ticket의 History Timeline에 표시됩니다.

#### Rule 6: COMPLETION_REQUESTED --> APPROVED --> COMPLETED (완료 승인)

```
COMPLETION_REQUESTED --> APPROVED --> COMPLETED
```

- Approver가 완료 요청을 승인하면 상태가 `APPROVED`로 전이된 후, 즉시 `COMPLETED`로 자동 전이됩니다.
- `COMPLETED` 상태에서는 읽기 전용(Read-only)이며, Ticket은 Archive 처리됩니다.

#### Rule 7: PROCESSING --> PROCESSING (연장 요청)

```
PROCESSING --> (연장 요청) --> PROCESSING (새 기한 적용)
```

- Ticket이 `PROCESSING` 상태이고 완료 기한이 도래하기 전에만 가능합니다.
- 요청자(클라이언트 또는 Support)는 원하는 연장 완료일과 사유를 기재하여 신청합니다.
- 승인자가 요청을 검토하고 승인하거나 반려합니다.
- 승인 시: 상태는 여전히 `PROCESSING`이며, 새 완료 기한이 적용되어 작업이 계속 진행됩니다.
- 반려 시: 상태는 기존 `PROCESSING`으로 유지되며, 기존 기한에 따라 진행됩니다.

#### Rule 8: OVERDUE Rule

```
PROCESSING --> DELAYED (Auto)
```

- `DELAYED` 상태에서는 연장 요청이 불가능합니다.
- `DELAYED` 상태에서는 완료 요청만 요청할 수 있습니다.

#### Rule 9: COMPLETION 워크플로우

```
PROCESSING --> COMPLETION_REQUESTED --> APPROVED --> COMPLETED
              | (반려 시)
         PROCESSING
```

- 클라이언트 또는 Support 팀 모두 완료 요청할 수 있습니다.
- 요청이 발생하면 `COMPLETION_REQUESTED` 상태가 생성됩니다.
- 승인권자(Approver)가 작업을 검토하여 선택합니다:
  - **승인 (Approve)**: `APPROVED`를 거쳐 `COMPLETED`로 자동 전환
  - **반려 (Reject)**: `PROCESSING`으로 되돌아가며, 사유 기록

### 5.4 이력 추적 (History Tracking)

상태 전이(State Transition)가 발생할 때마다 매번 `ticket_histories` Table에 Record가 기록됩니다:

| Field | 내용 |
|-------|------|
| `ticket_id` | 대상 Ticket |
| `from_status` | 이전 상태 코드 |
| `to_status` | 새 상태 코드 |
| `changed_by` | 전이를 발동한 User ID |
| `changed_at` | 전이 일시 |
| `reason` | 해당 설명 (nullable) |

이 Table은 Ticket 상세 페이지에 표시되는 History Timeline(이력 타임라인)의 기반이 되는 완전한 Audit Trail입니다.

---

### 5.5 API 검증 레이어 명세 (Validation Rules)

Backend API 레이어에서 RBAC(Role-Based Access Control) 및 소속 검증 로직을 적용하는 상세 규칙. 모든 API 엔드포인트는 JWT 토큰 검증 → Role 검증 → 소속 검증 → 비즈니스 로직 순으로 체이닝됨.

#### Rule A: 회원가입 (Sign-Up) 검증

| 검증 항목 | 설명 | 예외 처리 |
|---------|------|----------|
| email 고유성 | 중복 없는 고유 전자우편 | 409 Conflict |
| role에 따른 company_id 규칙 | ADMIN → DEVELOPER만 선택 가능, SUPPORT → DEVELOPER만 선택 가능, CUSTOMER → CLIENT만 선택 가능 | 400 Bad Request |
| company_exists | company_id가 존재하는지 FK 검증 | 400 Bad Request |
| password_strength | 최소 8자, 특수문자 포함 | 400 Bad Request |

```
SUPPORT 회원가입 시:
1. role="SUPPORT" 확인
2. company_type="DEVELOPER"인 회사 중 하나만 드롭다운 표시
3. company_id 필수 값 검증
4. users.company_id = 해당 DEVELOPER company_id
```

```
CUSTOMER 회원가입 시:
1. role="CUSTOMER" 확인
2. company_type="CLIENT"인 회사만 드롭다운 표시
3. company_id 필수 값 검증
4. users.company_id = 해당 CLIENT company_id
```

#### Rule B: 프로젝트 정보 관리 카드 CRUD 검증

| API Operation | RBAC 권한 | 소속 검증 | 예외 처리 |
|-------------|-----------|----------|----------|
| GET /cards | 전체 읽기 허용 (READ) | 없음 | 404 (카드 없음) |
| POST /cards | ADMIN 또는 프로젝트 배정된 SUPPORT | project_assignments 테이블에서 assignment_type=SUPPORT_MEMBER AND project_id=요청_PROJECT AND status=ACTIVE 확인 | 403 Forbidden |
| PUT /cards/{id} | ADMIN 또는 카드 작성자/SUPPORT | 위 POST 동일 + created_by=요청자 확인 | 403 Forbidden |
| DELETE /cards/{id} | ADMIN 전용 | project_assignments 관계 없어도 삭제 가능 | 404 (카드 없음) |

```
SUPPORT의 프로젝트 카드 POST/PUT 검증 로직:
1. JWT 토큰에서 user_id, role 추출
2. role이 CUSTOMER이면 → 403 Forbidden
3. project_assignments 테이블에서 (user_id, project_id, SUPPORT_MEMBER, ACTIVE) 일치하는 레코드 조회
4. 하나도 없으면 → 403 Forbidden ("해당 프로젝트에 배정되지 않았습니다")
5. 하나 이상 있으면 → API 실행
```

#### Rule C: 회사 등록 검증 (Companies CRUD)

| API Operation | RBAC 권한 | 예외 처리 |
|-------------|-----------|----------|
| POST /companies (DEVELOPER) | ADMIN 전용 | 403 Forbidden (ADMIN 외) |
| POST /companies (CLIENT) | ADMIN, SUPPORT | SUPPORT는 자신의 company_id와 같은 type의 CLIENT만 생성 가능 |
| GET /companies | 모든 인증된 사용자 | 없음 |
| PUT /companies/{id} | ADMIN 또는 해당 회사 소속 사용자 | 403 Forbidden |
| DELETE /companies/{id} | ADMIN 전용 | 409 Conflict (사용자/프로젝트 연결 시) |

#### Rule D: 프로젝트 배정 검증 (Project Assignments)

| API Operation | RBAC 권한 | 예외 처리 |
|-------------|-----------|----------|
| POST /assignments (SUPPORT) | ADMIN 또는 해당 프로젝트 SUPPORT_MEMBER | 403 Forbidden |
| PUT /assignments/{id} (revise) | ADMIN 또는 해당 프로젝트 SUPPORT_MEMBER | 403 Forbidden |
| DELETE /assignments/{id} (제거) | ADMIN 또는 해당 프로젝트 SUPPORT_MEMBER | 409 Conflict (Active 티켓 있으면) |
| POST /assignments (CLIENT) | ADMIN 또는 해당 프로젝트 SUPPORT_MEMBER | 403 Forbidden |
| POST /customer-register | ADMIN, SUPPORT (DEVELOPER 소속) | SUPPORT는 CLIENT만 SELECT 가능 |

#### Rule E: Ticket CRUD 검증

| API Operation | RBAC 권한 | 소속 검증 | 예외 처리 |
|-------------|-----------|----------|----------|
| POST /tickets | CUSTOMER (소속 CLIENT 프로젝트), ADMIN | project에 customer_company_id=요청자.company_id 확인 | 403 Forbidden |
| PUT/TICKET/{id} (state transition) | SUPPORT (배정된 프로젝트) | project_assignments에서 support 배정 확인 | 403 Forbidden |
| GET /tickets | 역할별 필터링 | CUSTOMER: 소속 CLIENT만, SUPPORT: 소속 DEVELOPER 프로젝트만, ADMIN: 전체 | 404 (데이터 없음) |
| GET /tickets/{id} | 모든 인증된 사용자 (동일 프로젝트 내) | 동일 project_id에 belong해야 함 | 403 Forbidden |

#### Rule F: 프로젝트 배정 검증 상세 흐름 (Flowchart)

```
API 요청 → JWT 토큰 검증 → Role 추출 → RBAC 검증 → 소속 검증 → API 실행/403

POST /cards (SUPPORT 요청 시):
  1. 토큰에서 user_id=SUP-042, role=SUPPORT 추출
  2. company_id가 DEVELOPER 타입 확인
  3. project_assignments 조회:
     WHERE user_id=SUP-042 AND project_id=PROJ-007 AND assignment_type=SUPPORT_MEMBER AND status=ACTIVE
  4. 레코드 존재 → API 실행
  4. 레코드 미존재 → 403 반환 ("배정되지 않은 프로젝트입니다")
```
## 6. UI/UX Specifications (UI/UX Design) — 요약

### 개요

Linear에서 영감받은 Clean & Minimal 디자인 철학 기반의 UI/UX 설계. Ticket 상세 페이지를 주요 상호작용 Surface로 하며, Admin Panel(Company/User/Project/System 관리), 프로젝트 상세, 대시보드, 검색, 컴포넌트 시스템, 다크 모드, 애니메이션, 에러/빈 상태 UX, 전역 i18n 등을 포함.

**상세 UI/UX 전체 명세는 별도 문서를 참조:**
📄 **[UI/UX Specifications (UI/UX Design) — 전체 명세](./nu-trust-uiux-specification.md)**

### 주요 구성

| 섹션 | 내용 |
|------|------|
| 6.1 Ticket 상세 페이지 레이아웃 | Status Ribbon, Ticket Details, Action Buttons, Info Section, History Timeline, Chat/Comment Section |
| 6.2 Status Ribbon Specifications | 전체 너비 Horizontal Gradient Bar, 상태 Badge, Color Transition, 타임스탬프 |
| 6.3 Action Buttons | current_status별 조건부 렌더링 (REGISTERED→DELIVERED/COMPLETED) |
| 6.4 Chat/Comment Section | WebSocket(STOMP over SockJS) Real-time, Typing Indicator, 첨부파일 Inline Preview |
| 6.5 Admin Panel | Company/User/Project/System 관리 기능 개요 |
| 6.6 Project Detail 페이지 | Header(Server/SW), Filterable Ticket List, Quick Actions |
| 6.7 디자인 철학 | Clean & Minimal, 국내 비즈니스 관행, Responsive Layout, Accessible Color Palette |
| 6.8 Admin Panel UI 명세 | ADMIN/SUPPORT/CUSTOMER 역할별 대시보드 및 메뉴 구조 (ASCII 레이아웃 포함) |
| 6.9 Admin Panel 상세 UI | Company 목록/등록/수정, 사용자 목록/생성/역할 변경, 프로젝트 배정, Business Calendar |
| 6.10 프로젝트 상세 페이지 | Server/SW 카드 그리드, Ticket 테이블, Quick Tickets, Chart(상태/타입/기간별) |
| 6.11 디자인 시스템 | Color palette, Typography, Spacing(4px grid), Button/Input/Modal/Card/Tooltip 컴포넌트 |
| 6.12 다크 모드 | Theme Provider 기반, prefers-color-scheme 자동 감지, LocalStorage 지속화, Contrast Ratio 검증 |
| 6.13 Animation/Motion | Easing curve(standard/decelerate/accelerate/spring), Timing Matrix, Reduced Motion 지원 |
| 6.14 회사/사용자 관리 UI | Company 목록/등록/수정 페이지, User 목록/생성/역할 변경/비활성화 |
| 6.15 대시보드 | 역할별 대시보드, Chart/Widget, Recent Activity Feed, Filter/Search |
| 6.16 검색/필터/정렬 | 전역 검색(Global Search), Filter Drawer, 정렬 옵션, Saved Filter |
| 6.17 빈 상태 | Empty State 패턴, CTA 배치, 예외 상황 처리 |
| 6.18 에러/토스트 UX | 에러 디스플레이, 토스트 알림/Error Boundary, 429 Rate Limit |
| 6.19 에러 페이지 | 404/500/503 페이지, 에러 리포트 기능, 리디렉션 |
| 6.20 전역 i18n | Next-intl 기반, ko/en 언어, Date/Number/Currency formatter, i18n Testing Checklist |
## 7. CI/CD 및 배포

### 8.1 GitLab CI/CD Pipeline

| Stage | 이름 | Actions |
|-------|------|---------|
| 1 | **validate** | Frontend ESLint, Java SpotBugs/Checkstyle 실행 |
| 2 | **test** | Frontend Jest 테스트, Java JUnit 테스트 실행 |
| 3 | **build** | Frontend npm build, Java Gradle 빌드 (JAR 생성) |
| 4 | **deploy-stage** | Stage 환경에 Docker Image push 및 deploy |
| 5 | **deploy-prod** | Production 환경에 Docker Image push 및 deploy (수동 승인 필요) |

### 8.2 배포 환경

- **Infrastructure:** AWS (또는 국내 클라우드: NAVER Cloud/LaunchSOL)
- **Frontend:** S3 + CloudFront (정적 파일 배포, CDN)
- **Backend:** EC2 또는 ECS Fargate (Docker 컨테이너)
- **Database:** Amazon RDS for PostgreSQL 15 (Private Subnet, Multi-AZ)
- **Cache:** Amazon ElastiCache (Redis) - 세션 관리, WebSocket 브로드캐스팅
- **Message Broker:** AWS RabbitMQ (또는 EKS에서 RabbitMQ StatefulSet)
- **Monitoring:** Prometheus + Grafana (or AWS CloudWatch)

### 8.3 개발/Stage/Production 구분

| Environment | Purpose | Database | URL |
|-------------|---------|----------|-----|
| Local | 개별 개발자 로컬 테스트 | Local PostgreSQL 15 | localhost:3000 / localhost:8080 |
| Dev | 기능 개발 및 통합 테스트 | Dev RDS (PostgreSQL) | dev.nutrust.kr |
| Stage | QA 및 Release 검증 | Stage RDS (PostgreSQL) | stage.nuttrust.kr |
| Production | 실제 서비스 | Prod RDS (PostgreSQL) | nutrust.kr |

### 8.4 Code Review Process

모든 PR(Pull Request)은 최소 1명 이상 Reviewer 승인 후 Merge된다.

| 항목 | 규칙 |
|------|------|
| Reviewer | 최소 1명 (Domain 담당자 우선, 교차 Review 허용) |
| Checklist | (1) 테스트 커버리지 80%+ (2) Naming convention (3) Security vuln 체크 (4) 성능 regressione 체크 |
| Auto Request Review | Size Large(+20 라인) → 2 Reviewer, Security Module → Security담당 필수 Reviewer |
| Merge Policy | Squash Merge 권장. Linear commit history 유지. |
| WIP PR | `[WIP]` prefix → Merge 불가. 완료 후 Reviewer assigned. |

### 8.5 Test Strategy

| 테스트 유형 | 도구 | 실행 시기 | 커버리지 목표 |
|-----------|------|----------|-------------|
| Frontend Unit | Jest + React Testing Library | PR 전 로컬, CI 자동 | 80% 이상 |
| Backend Unit | JUnit 5 + Mockito | PR 전 로컬, CI 자동 | 80% 이상 |
| Integration | Testcontainers + WireMock | CI Stage deploy 전 | 핵심 API 100% |
| E2E | Playwright | Release 전, 정기 Cron | 핵심 플로우 100% |
| Performance | k6 | Release 전 | Core API p95 < 300ms |
| Security | OWASP Dependency-Check + Snyk | CI validate stage | Block on CRITICAL/HIGH |

### 8.6 Code Quality & Tech Debt Management

| 항목 | 도구 | 규칙 |
|------|------|------|
| Static Analysis | SonarQube | Quality Gate: Bugs=0, Vulnerabilities=0, Code Smells<10, Duplicated Lines<3% |
| CI Caching | Gradle Build Cache, npm ci cache | CI 빌드 시간 단축 (target: 20%↓) |
| Tech Debt Track | SonarQube Tech Debt Rate < 5% | Release Checklist에 SonarQube Report 포함 |

---

## 8. Development Roadmap

### Phase 1: Base Setup (1-2 주)

- [ ] 프로젝트 Repository 세팅 (Frontend/Backend 분리)
- [ ] 전자정부 표준프레임워크 기반 Backend 세팅 (Spring Boot 3.x)
- [ ] React + TypeScript Frontend 프로젝트 생성
- [ ] CI/CD Pipeline (GitLab CI) 초기 설정
- [ ] Development/Stage/Production Environment 세팅

### Phase 2: Core Infrastructure (2-3 주)

- [ ] Authentication 및 Authorization 구현 (JWT + Session-based Hybrid)
- [ ] User Management CRUD (Admin)
- [ ] Company Management CRUD
- [ ] Project Management CRUD
- [ ] Role-based Access Control (RBAC) 구현

### Phase 3: Ticket System (3-4 주)

- [ ] Ticket CRUD (생성, 조회, 수정, 삭제)
- [ ] Ticket State Transition Engine 구현 (Section 5 참조)
- [ ] Extension Request workflow
- [ ] Completion Request workflow
- [ ] Ticket History Timeline UI
- [ ] Processing Plan CRUD

### Phase 4: Chat/Communication (2-3 주)

- [ ] WebSocket (STOMP over SockJS) 서버 구현
- [ ] Ticket Chat 기능 (실시간 메시지)
- [ ] Ticket Comment 기능 (Threaded Comment)
- [ ] File Attachment in Chat/Comment
- [ ] Typing Indicator
- [ ] Fallback: Long-polling 지원

### Phase 5: Admin Panel & Notifications (2 주)

- [ ] Admin: Company/User/Project 관리 UI
- [ ] System 설정 (Business Calendar, Notification Settings)
- [ ] Email/In-app Notification (RabbitMQ 기반 비동기)
- [ ] Overdue Detection Cron Job

### Phase 6: QA, Polish, Launch (2-3 주)

- [ ] End-to-End Testing (Playwright)
- [ ] Integration Testing
- [ ] Security Audit (OWASP Top 10)
- [ ] Performance Testing
- [ ] UI Polish (Animation, Responsiveness, Accessibility)
- [ ] Production Deployment
- [ ] User Documentation (Korean only)

---

## 9. Technology Stack

### Frontend

| Category | Technology | Version |
|----------|-----------|---------|
| Framework | React.js | 18.x |
| Type System | TypeScript | 5.x |
| Styling | Tailwind CSS | 3.x |
| State Management | Zustand | latest |
| WebSocket Client | SockJS + STOMP | stompjs |
| Build Tool | Vite | 5.x |
| Testing | Jest + React Testing Library | latest |
| E2E Testing | Playwright | latest |

### Backend

| Category | Technology | Version |
|----------|-----------|---------|
| Framework | Spring Boot 3.x | 3.2+ |
| Language | Java | 17 or 21 (LTS) |
| ORM | MyBatis | 3.5+ |
| Validation | Bean Validation (Jakarta) | - |
| Security | Spring Security + JWT | latest |
| WebSocket | Spring WebSocket + STOMP over SockJS | - |
| Message Broker Integration | Spring AMQP (RabbitMQ) | - |
| Build Tool | Gradle | 8.x |
| Testing | JUnit 5 + Mockito | latest |
| **DB Migration** | **Flyway** | **latest** (DB 버전 관리) |

### Infrastructure

| Category | Technology | Purpose |
|----------|-----------|---------|
| CI/CD | GitLab CI | Pipeline orchestration |
| Container | Docker | Application packaging |
| Orchestration | (Optional) Kubernetes/EKS | Scalability |
| Database | **PostgreSQL** | **15.x** Data persistence (MySQL 아님) |
| Cache | Redis | Session, Broadcast |
| Message Broker | RabbitMQ | Async tasks, notifications |
| Monitoring | Prometheus + Grafana | Metrics, Alerting |
| Cloud (Primary) | AWS | Hosting |
| Cloud (Alt) | NAVER Cloud / LaunchSOL | Domestic option |

---

## 10. Risk Management & Mitigation

| Risk | Probability | Impact | Mitigation Strategy |
|------|-------------|--------|-------------------|
| Workflow 복잡성으로 인한 개발 지연 | Medium | High | State Machine 패턴 적용, 단계별 구현 (Phase 3 분리) |
| WebSocket 연결 불안정 | Medium | Medium | Long-polling fallback, Reconnection logic 구현 |
| RBAC 구현 복잡성 | Medium | Medium | Role hierarchy 명확히 설정, 테스트 커버리지 확보 |
| 대용량 File Upload 처리 | Low | Medium | Chunked Upload, Progress bar 표시 |
| 국내 클라우드 규제 준수 (정보보호) | Low | High | NAVER Cloud/LaunchSOL 대안 준비, 인트라넷 배포 옵션 검토 |
| Scope Creep (기능 확장 요청) | High | Medium | Phase 기반 분기, 추가 기능은 차기 Release로 미루기 |
| **DB 버전 충돌 (개발/Stage/Prod)** | **Medium** | **High** | **Flyway Migration Tool 도입, 모든 DB 변경은 Migration Script로만 적용** |
| **Optimistic Locking 충돌** | **Medium** | **Medium** | **version column 적용, 409 Conflict 처리 + auto-refresh UX** |
| **Notification Spam (대량 발송)** | **Medium** | **Medium** | **Batching Policy (5분 묶음), 일일 50건 Cap, Quiet Hours** |

---

## 11. Onboarding Plan (새 개발자 진입 가이드)

신규 개발자가 nu-trust 프로젝트에 빠르게 참여할 수 있도록 하는 Onboarding 절차를 정의한다.

### 11.1 Local Environment Setup (1일 차)

```
1. Dependencies 설치:
   - Node.js 20 LTS (nvm install 20)
   - Java JDK 17 or 21 (LTS)
   - Docker (PostgreSQL, Redis, RabbitMQ)
   - GitLab CLI (可选)

2. Docker Compose로 로컬 서비스 기동:
   docker-compose up -d postgres redis rabbitmq

3. DB Migration 실행:
   flyway -c config/flyway.conf migrate

4. Frontend 설치:
   cd frontend && npm install

5. Backend 실행:
   ./gradlew bootRun --continuous

6. 접근 확인:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api/v1
   - Adminer (DB): http://localhost:8081
   - RabbitMQ Management: http://localhost:15672
   - Redis: redis-cli ping
```

### 11.2 First Ticket (2~3일 차)

| 항목 | 내용 |
|------|------|
| First Ticket | "Good First Issue" 라벨이 붙은 Ticket 중 하나 선택 |
| Git Branch | `feat/GH-<issue-number>-describe-your-change` 규칙 |
| Code Style | SpotBugs, Checkstyle 통과 필수 |
| Test | 최소 Unit Test 작성 후 PR |
| CI Pipeline | GitLab CI Pipeline Green 통과 필수 |

### 11.3 Architecture Quick Guide (1주일 차)

| 학습 항목 | 학습 자료 |
|----------|----------|
| 프로젝트 구조 | `README.md` + 이 개발 계획서 (Section 3 참조) |
| 상태 전이 | Section 5 — State Machine 패턴 이해 |
| DB 스키마 | Section 4 — 테이블 구조, FK, 파티션 |
| CI/CD | Section 8 — Pipeline 흐름, 로컬 CI 실행법 |
| Testing | Section 8.5 — Test Strategy 이해, 테스트 샘플 코드 |

### 11.4 Development Guidelines

| 규칙 | 상세 |
|------|------|
| PR Size | 단일 PR 최대 200 라인 (크면 분리) |
| Commit Rule | Conventional Commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:` |
| Hotfix PR | Hotfix는 Merge 후 즉시 Deploy (CI Pipeline 자동) |

---

## Appendix A: Key Glossary (전문용어 정의)

| 영어 용어 | 한글 | 설명 |
|-----------|------|------|
| Ticket | 티켓 | 클라이언트가 제출한 Issue의 공식 기록, 처리 대상 |
| Processing Plan | 처리 계획 | Ticket 작업을 어떻게 처리할지에 대한 계획서 |
| State Transition | 상태 전이 | Ticket 상태가 한 값에서 다른 값으로 변경되는 것 |
| Audit Trail | 이력 추적 | 모든 변화 (상태 전이, 댓글, 파일 등) 를 기록하는 로그 |
| WebSocket | WebSocket | 실시간 양방향 통신 프로토콜 |
| STOMP | STOMP | 메시지 기반 메시징 프로토콜 (WebSocket 위에서 동작) |
| RBAC | Role-Based Access Control | Role 기반 접근 제어 |
| SLA | Service Level Agreement | 서비스 수준 계약 |
| E2E | End-to-End | 처음부터 끝까지 전체 시스템 테스트 |
| CI/CD | Continuous Integration/Continuous Deployment | 지속적인 통합 및 배포 |
| Cron Job | Cron Job | 주기적으로 실행되는 자동화 Task |
| Long-polling | Long-polling | WebSocket 대체 실시간 통신 기법 |

## Appendix B: Reference URLs

### Domestic
- 전자정부 표준프레임워크: <https://www.code.go.kr>
- Riido: <https://riido.co.kr>
- WatchTek: <https://watchtek.co.kr>

### International
- Linear: <https://linear.app>
- Jira: <https://www.atlassian.com/software/jira>
- Intercom: <https://www.intercom.com>

---


**문서 버전:** 1.0
**최종 수정일:** 2026년 5월 30일
**작성자:** (주)엔유비즈 개발팀
