## 4. 데이터 모델 (ERD)

---



## 4.1 DDL 전체 스키마 (PostgreSQL 15)

아래는 모든 테이블, 인덱스, 제약조건, 트리거에 대한 완전한 PostgreSQL DDL 스키마입니다.

### 4.1.1 공통 설정

```sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gin;

-- 시간대 설정
SET timezone = 'Asia/Seoul';
SET datestyle = 'ISO, YMD';
```

### 4.1.2 companies 테이블

개발사(엔유비즈)와 고객사를 구분하여 관리하는 테이블.

```sql
CREATE TABLE companies (
    id              BIGSERIAL PRIMARY KEY,
    company_name    VARCHAR(255) NOT NULL,
    company_type    VARCHAR(20) NOT NULL CONSTRAINT chk_company_type CHECK (company_type IN ('DEVELOPER', 'CLIENT')),
    representative_name VARCHAR(255),
    business_number VARCHAR(20) UNIQUE,
    phone           VARCHAR(30),
    address         VARCHAR(500),
    email           VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_companies_type ON companies (company_type);
```
### 4.1.3 users 테이블

Role 기반 접근 제어(Role-Based Access Control, RBAC)가 적용된 시스템 사용자.
- ADMIN (플랫폼 관리자): 전체 시스템 관리
- COMPANY_ADMIN (회사 관리자): 특정 개발사(엔유비즈) 내 사용자 및 프로젝트 관리
- USER (일반 사용자): SUPPORT (개발팀 멤버) 또는 CUSTOMER (클라이언트 측 인사)

TRIMS (Hard Delete) 전략: User 테이블은 소프트 딜리트 없이 하드 삭제만 적용됨. 데이터 보존 정책(Data Retention Policy)에 따라 일정 기간 이후 물리적 삭제.

```sql
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    parent_id       BIGINT NULL CONSTRAINT fk_users_parent REFERENCES users(id) ON DELETE SET NULL,
    email           VARCHAR(255) NOT NULL CONSTRAINT uk_users_email UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    username        VARCHAR(100) NOT NULL CONSTRAINT uk_users_username UNIQUE,
    phone           VARCHAR(30),
    role            VARCHAR(20) NOT NULL CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'COMPANY_ADMIN', 'USER')),
    company_id      BIGINT NOT NULL CONSTRAINT fk_users_company REFERENCES companies(id) ON DELETE RESTRICT,
    status          VARCHAR(20) NOT NULL CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISABLED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_parent_id ON users (parent_id);
CREATE INDEX idx_users_company_id ON users (company_id);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_status ON users (status);

-- updated_at 자동 갱신 트리거
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE OF updated_at ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Role Hierarchy:** COMPANY_ADMIN > USER(SUPPORT/CUSTOMER)

**company_id 규칙 (역할별 소속):**

| Role | company_id 소속 회사 타입 | 설명 |
|------|--------------------------|------|
| ADMIN | DEVELOPER | 플랫폼 (엔유비즈) 전용 관리자. DEVELOPER Company만 등록 가능 |
| COMPANY_ADMIN | DEVELOPER | 개발사 내 관리자. 해당 DEVELOPER Company만 관리 |
| USER (SUPPORT) | DEVELOPER | 개발팀 멤버. DEVELOPER Company 소속 |
| USER (CUSTOMER) | CLIENT | 클라이언트 측 인사. CLIENT Company 소속 |

**Soft Delete 전략:** users 테이블은 Soft Delete 없음 (Hard Delete 적용). GDPR 등 개인정보 규제에 따른 데이터 파기 정책 적용.
### 4.1.4 projects 테이블

각 고객사는 여러 프로젝트를 보유할 수 있습니다. Soft Delete 전략 적용.

```sql
CREATE TABLE projects (
    id                  BIGSERIAL PRIMARY KEY,
    company_id          BIGINT NOT NULL CONSTRAINT fk_projects_company REFERENCES companies(id) ON DELETE RESTRICT,
    customer_company_id BIGINT NULL CONSTRAINT fk_projects_customer_company REFERENCES companies(id) ON DELETE SET NULL,
    project_name        VARCHAR(255) NOT NULL,
    owner_id            BIGINT NOT NULL CONSTRAINT fk_projects_owner REFERENCES users(id) ON DELETE RESTRICT,
    contract_date       DATE,
    start_date          DATE,
    end_date            DATE,
    status              VARCHAR(20) NOT NULL CONSTRAINT chk_projects_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'ON_HOLD', 'CANCELLED')),
    description         TEXT,
    deleted_at          TIMESTAMPTZ NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_projects_name_owner UNIQUE (owner_id, project_name)
);

CREATE INDEX idx_projects_company_id ON projects (company_id);
CREATE INDEX idx_projects_customer_company_id ON projects (customer_company_id);
CREATE INDEX idx_projects_owner_id ON projects (owner_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_deleted_at ON projects (deleted_at) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_projects_updated_at
    BEFORE UPDATE OF updated_at ON projects
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Soft Delete:** projects.deleted_at NULLABLE. 삭제된 프로젝트는 WHERE deleted_at IS NULL로 필터링.
### 4.1.5 project_managers 연결 테이블 (Junction Table)

프로젝트와 관리자(Admin)의 다대다 관계를 관리하는 연결 테이블.

```sql
CREATE TABLE project_managers (
    project_id    BIGINT NOT NULL CONSTRAINT fk_pm_project REFERENCES projects(id) ON DELETE CASCADE,
    manager_id    BIGINT NOT NULL CONSTRAINT fk_pm_manager REFERENCES users(id) ON DELETE CASCADE,
    role          VARCHAR(50) NOT NULL DEFAULT 'PROJECT_ADMIN',
    assigned_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by   BIGINT NOT NULL CONSTRAINT fk_pm_assigned_by REFERENCES users(id) ON DELETE SET NULL,
    PRIMARY KEY (project_id, manager_id)
);

CREATE INDEX idx_pm_assigned_by ON project_managers (assigned_by);
```

### 4.1.6 project_support_managers 연결 테이블 (Junction Table)

프로젝트와 지원 매니저(Project Support)를 연결하는 연결 테이블.

```sql
CREATE TABLE project_support_managers (
    project_id    BIGINT NOT NULL CONSTRAINT fk_psm_project REFERENCES projects(id) ON DELETE CASCADE,
    support_id    BIGINT NOT NULL CONSTRAINT fk_psm_support REFERENCES users(id) ON DELETE CASCADE,
    role          VARCHAR(50) NOT NULL DEFAULT 'PROJECT_SUPPORT',
    assigned_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by   BIGINT NOT NULL CONSTRAINT fk_psm_assigned_by REFERENCES users(id) ON DELETE SET NULL,
    PRIMARY KEY (project_id, support_id)
);

CREATE INDEX idx_psm_assigned_by ON project_support_managers (assigned_by);
```

### 4.1.7 project_assignments 테이블

지원 담당자와 클라이언트 사용자에게 프로젝트별 배정 관계를 관리하는 테이블. 프로젝트 카드 관리, 티켓 처리, 채팅 등 프로젝트 활동은 이에 기반한 배정 관계에 따라 권한이 결정됨.

```sql
CREATE TABLE project_assignments (
    id                  BIGSERIAL PRIMARY KEY,
    project_id          BIGINT NOT NULL CONSTRAINT fk_pa_project REFERENCES projects(id) ON DELETE CASCADE,
    user_id             BIGINT NOT NULL CONSTRAINT fk_pa_user REFERENCES users(id) ON DELETE CASCADE,
    assignment_type     VARCHAR(20) NOT NULL CONSTRAINT chk_pa_type CHECK (assignment_type IN ('SUPPORT_MEMBER', 'CLIENT_MEMBER')),
    assigned_at         TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by         BIGINT NOT NULL CONSTRAINT fk_pa_assigned_by REFERENCES users(id) ON DELETE SET NULL,
    status              VARCHAR(20) NOT NULL CONSTRAINT chk_pa_status CHECK (status IN ('ACTIVE', 'RESIGNED', 'REVOKED')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pa_project_id ON project_assignments (project_id);
CREATE INDEX idx_pa_user_id ON project_assignments (user_id);
CREATE INDEX idx_pa_assigned_by ON project_assignments (assigned_by);
```

### 4.1.8 project_assignment_histories 테이블

배정, 해지, 권한 변경 시 이력을 기록하는 테이블.

```sql
CREATE TABLE project_assignment_histories (
    id              BIGSERIAL PRIMARY KEY,
    assignment_id   BIGINT NOT NULL CONSTRAINT fk_pah_assignment REFERENCES project_assignments(id) ON DELETE CASCADE,
    action          VARCHAR(20) NOT NULL CONSTRAINT chk_pah_action CHECK (action IN ('ASSIGNED', 'RESIGNED', 'REVOKED')),
    changed_by      BIGINT NOT NULL CONSTRAINT fk_pah_changed_by REFERENCES users(id) ON DELETE SET NULL,
    reason          TEXT,
    changed_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pah_assignment_id ON project_assignment_histories (assignment_id);
CREATE INDEX idx_pah_changed_by ON project_assignment_histories (changed_by);
```
### 4.1.9 projects_management_cards 테이블

각 프로젝트(엔유비즈 프로젝트, 고객사별 프로젝트 양측 모두 포함)는 프로젝트 정보 관리 카드를 보유합니다. 관리 카드의 내용은 Server(SW) 정보, 접속 계정 정보, 운영을 위한 접속 계정 정

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `project_id` | BIGINT (FK) | 대상 프로젝트 (projects Table 참조) |
| `card_title` | VARCHAR(255) | 카드 제목 |
| `card_type` | VARCHAR(50) | 카드 유형 (기본값: DEFAULT) |
| `content` | JSONB | 프로젝트 정보 (서버, SW, 계정 정보) |
| `created_by` | BIGINT (FK) | 생성자 (users Table 참조) |
| `updated_by` | BIGINT (FK) | 최종 수정자 (users Table 참조) |
| `created_at` | TIMESTAMPTZ | 생성 일시 |
| `updated_at` | TIMESTAMPTZ | 최종 수정 일시 |

**content JSON 구조:**

```json
{
  "server_info": {
    "server_name": "서버명",
    "server_ip": "서버IP",
    "server_type": "Web / Database / Application / Other",
    "operating_system": "서버OS",
    "port": "포트번호",
    "protocol": "HTTP / FTP / SSH / Other",
    "access_url": "접속URL",
    "credentials": {
      "account": "접속계정ID",
      "temporary_password": "임시비밀번호(암호화저장)"
    }
  },
  "sw_info": {
    "software_name": "SW명",
    "version": "버전",
    "license_key": "라이선스키",
    "license_type": "Perpetual / Subscription / OpenSource",
    "license_expiry_date": "라이선스만료일",
    "vendor": "벤더사",
    "documentation_url": "문서URL"
  },
  "login_accounts": [
    {
      "service_name": "서비스명 (예: AWS Console, GitHub, Figma)",
      "account": "계정ID",
      "temporary_password": "임시비밀번호(암호화저장)",
      "url": "접속URL",
      "mfa_enabled": true
    }
  ],
  "operation_accounts": [
    {
      "service_name": "운영서비스명 (예: 배포계정, 모니터링)",
      "account": "계정ID",
      "temporary_password": "임시비밀번호(암호화저장)",
      "role": "Deployer / Viewer / Admin",
      "scope": "조회/운영권한"
    }
  ],
  "notes": [
    {
      "category": "참고사항/특이사항/주의점",
      "description": "내용"
    }
  ]
}
```

**Junction Tables:** `project_managers`, `project_support_managers` (users와의 Many-to-Many 관계)

```sql
CREATE TABLE projects_management_cards (
    id              BIGSERIAL PRIMARY KEY,
    project_id      BIGINT NOT NULL CONSTRAINT fk_pmcard_project REFERENCES projects(id) ON DELETE CASCADE,
    card_title      VARCHAR(255) NOT NULL,
    card_type       VARCHAR(50) NOT NULL DEFAULT 'DEFAULT',
    content         JSONB NULL,
    created_by      BIGINT NOT NULL CONSTRAINT fk_pmcard_created_by REFERENCES users(id) ON DELETE SET NULL,
    updated_by      BIGINT NOT NULL CONSTRAINT fk_pmcard_updated_by REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pmcard_project_id ON projects_management_cards (project_id);

CREATE TRIGGER trg_pmcard_updated_at
    BEFORE UPDATE OF updated_at ON projects_management_cards
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```
### 4.1.10 card_assignments 테이블

카드(관리 카드)에 대한 사용자 권한 배정을 관리하는 테이블.

```sql
CREATE TABLE card_assignments (
    id              BIGSERIAL PRIMARY KEY,
    entity_id       BIGINT NOT NULL,
    entity_type     VARCHAR(50) NOT NULL,
    assigned_to     BIGINT NOT NULL CONSTRAINT fk_ca_assigned_to REFERENCES users(id) ON DELETE CASCADE,
    assigned_by     BIGINT NOT NULL CONSTRAINT fk_ca_assigned_by REFERENCES users(id) ON DELETE SET NULL,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMPTZ,
    CONSTRAINT chk_ca_expiry CHECK (assigned_at <= expires_at OR expires_at IS NULL)
);

CREATE INDEX idx_ca_entity ON card_assignments (entity_id, entity_type);
CREATE INDEX idx_ca_assigned_to ON card_assignments (assigned_to);
CREATE UNIQUE INDEX idx_ca_unique ON card_assignments (entity_id, entity_type, assigned_to) WHERE expires_at IS NULL;
```

---

## 4.2 Tickets 관련 테이블

### 4.2.1 tickets 테이블

플랫폼 핵심 Entity. Soft Delete 전략 적용.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `project_id` | BIGINT (FK) | 상위 프로젝트 (projects Table 참조) ON DELETE CASCADE |
| `customer_id` | BIGINT (FK) | 생성한 고객 User (users Table 참조) ON DELETE RESTRICT |
| `assignee_id` | BIGINT (FK) | Nullable, 배정된 SUPPORT User (users Table 참조) ON DELETE SET NULL |
| `title` | VARCHAR(500) | Ticket 제목 |
| `type` | VARCHAR(50) | type IN ('dissatisfaction', 'improvement', 'addition', 'other') |
| `description` | TEXT | Ticket 상세 내용 |
| `desired_completion_date` | DATE | 클라이언트 희망 완료일 |
| `current_status` | VARCHAR(50) | status IN ('REGISTERED', 'RECEIVED', 'PROCESSING', 'DELAYED', 'COMPLETION_REQUESTED', 'APPROVED', 'COMPLETED') |
| `deadline` | TIMESTAMPTZ | 완료 기한 (타임존 고려) |
| `deleted_at` | TIMESTAMPTZ | Nullable, Soft Delete 플래그 |
| `created_at` | TIMESTAMPTZ | 생성 일시 |
| `updated_at` | TIMESTAMPTZ | 최종 수정 일시 |

```sql
CREATE TABLE tickets (
    id                          BIGSERIAL PRIMARY KEY,
    project_id                  BIGINT NOT NULL CONSTRAINT fk_tickets_project REFERENCES projects(id) ON DELETE CASCADE,
    customer_id                 BIGINT NOT NULL CONSTRAINT fk_tickets_customer_ref REFERENCES users(id) ON DELETE RESTRICT,
    assignee_id                 BIGINT NULL CONSTRAINT fk_tickets_assignee REFERENCES users(id) ON DELETE SET NULL,
    title                       VARCHAR(500) NOT NULL,
    type                        VARCHAR(50) NOT NULL CONSTRAINT chk_tickets_type CHECK (type IN ('dissatisfaction', 'improvement', 'addition', 'other')),
    description                 TEXT NOT NULL,
    desired_completion_date     DATE,
    current_status              VARCHAR(50) NOT NULL CONSTRAINT chk_tickets_status CHECK (current_status IN ('REGISTERED', 'RECEIVED', 'PROCESSING', 'DELAYED', 'COMPLETION_REQUESTED', 'APPROVED', 'COMPLETED')),
    deadline                    TIMESTAMPTZ,
    deleted_at                  TIMESTAMPTZ NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tickets_project_id ON tickets (project_id);
CREATE INDEX idx_tickets_customer_id ON tickets (customer_id);
CREATE INDEX idx_tickets_assignee_id ON tickets (assignee_id);
CREATE INDEX idx_tickets_status ON tickets (current_status);
CREATE INDEX idx_tickets_type ON tickets (type);
CREATE INDEX idx_tickets_deleted_at ON tickets (deleted_at) WHERE deleted_at IS NULL;

-- GIN Full-Text Search Index (title + description - 한국어 검색용)
CREATE INDEX idx_tickets_fts ON tickets USING GIN (
    to_tsvector('ko_postgres', title || ' ' || description)
);

CREATE TRIGGER trg_tickets_updated_at
    BEFORE UPDATE OF updated_at ON tickets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Soft Delete:** tickets.deleted_at NULLABLE. 삭제된 티켓은 WHERE deleted_at IS NULL로 필터링하여 조회.
### 4.2.2 processing_plans 테이블

Ticket 처리 계획을 사용자가 직접 작성하는 테이블.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 소속 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `writer_id` | BIGINT (FK) | 작성자 (users Table 참조) ON DELETE RESTRICT |
| `title` | VARCHAR(500) | 제목 |
| `content` | TEXT | 처리 계획 상세 |
| `status` | VARCHAR(20) | status IN ('SUBMITTED', 'APPROVED', 'REJECTED') |
| `created_at` | TIMESTAMPTZ | 생성 일시 |
| `updated_at` | TIMESTAMPTZ | 최종 수정 일시 |

```sql
CREATE TABLE processing_plans (
    id              BIGSERIAL PRIMARY KEY,
    ticket_id       BIGINT NOT NULL CONSTRAINT fk_pp_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    writer_id       BIGINT NOT NULL CONSTRAINT fk_pp_writer REFERENCES users(id) ON DELETE RESTRICT,
    title           VARCHAR(500) NOT NULL,
    content         TEXT,
    status          VARCHAR(20) NOT NULL CONSTRAINT chk_pp_status CHECK (status IN ('SUBMITTED', 'APPROVED', 'REJECTED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pp_ticket_id ON processing_plans (ticket_id);
CREATE INDEX idx_pp_writer_id ON processing_plans (writer_id);
CREATE INDEX idx_pp_status ON processing_plans (status);

CREATE TRIGGER trg_pp_updated_at
    BEFORE UPDATE OF updated_at ON processing_plans
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

### 4.2.3 ticket_comments 테이블

실시간 채팅과 별도의 댓글(Threaded Comment) 테이블.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 소속 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `writer_id` | BIGINT (FK) | 작성자 (users Table 참조) ON DELETE RESTRICT |
| `parent_id` | BIGINT (FK) | Nullable, 상위 댓글 (자기 참조 ON DELETE SET NULL) |
| `content` | TEXT | 댓글 본문 |
| `attached_files` | JSONB | Nullable, 첨부파일 목록(JSON) |
| `created_at` | TIMESTAMPTZ | 생성 일시 |

```sql
CREATE TABLE ticket_comments (
    id              BIGSERIAL PRIMARY KEY,
    ticket_id       BIGINT NOT NULL CONSTRAINT fk_tc_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    writer_id       BIGINT NOT NULL CONSTRAINT fk_tc_writer REFERENCES users(id) ON DELETE RESTRICT,
    parent_id       BIGINT NULL CONSTRAINT fk_tc_parent REFERENCES ticket_comments(id) ON DELETE SET NULL,
    content         TEXT NOT NULL,
    attached_files  JSONB NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tc_ticket_id ON ticket_comments (ticket_id);
CREATE INDEX idx_tc_writer_id ON ticket_comments (writer_id);
CREATE INDEX idx_tc_parent_id ON ticket_comments (parent_id);

-- 트레딩(스레드) 구조를 위한 자기 참조 인덱스
CREATE INDEX idx_tc_ticket_parent ON ticket_comments (ticket_id, parent_id);
```
### 4.2.4 ticket_files 테이블

첨부 파일 메타데이터용 테이블.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 소속 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `uploader_id` | BIGINT (FK) | 업로더 (users Table 참조) ON DELETE RESTRICT |
| `original_filename` | VARCHAR(500) | 원본 파일명 |
| `stored_filename` | VARCHAR(500) | 저장용 파일명 |
| `file_size` | BIGINT | 파일 크기 (bytes) |
| `mime_type` | VARCHAR(255) | MIME 타입 |
| `created_at` | TIMESTAMPTZ | 생성 일시 |

```sql
CREATE TABLE ticket_files (
    id                  BIGSERIAL PRIMARY KEY,
    ticket_id           BIGINT NOT NULL CONSTRAINT tf_file_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    uploader_id         BIGINT NOT NULL CONSTRAINT fk_tf_uploader REFERENCES users(id) ON DELETE RESTRICT,
    original_filename   VARCHAR(500) NOT NULL,
    stored_filename     VARCHAR(500) NOT NULL,
    file_size           BIGINT NOT NULL,
    mime_type           VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tf_ticket_id ON ticket_files (ticket_id);
CREATE INDEX idx_tf_uploader_id ON ticket_files (uploader_id);
```

### 4.2.5 ticket_chat_messages 테이블

채팅 실시간 메시지용 테이블. **PG 15 Native Table Partitioning** 대상 (RANGE by sent_at).

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 소속 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `user_id` | BIGINT (FK) | 발신자 (users Table 참조) ON DELETE RESTRICT |
| `content` | TEXT | 메시지 내용 |
| `attachments` | JSONB | Nullable, 첨부파일 URL 목록 |
| `is_edited` | BOOLEAN | 수정 여부 (기본값: FALSE) |
| `created_at` | TIMESTAMPTZ | 생성 일시 |
| `sent_at` | TIMESTAMPTZ | 전송 일시 (Partition Key) |

```sql
-- 원본 테이블 (Partition Master)
CREATE TABLE ticket_chat_messages_part_master (
    id                  BIGINT NOT NULL DEFAULT nextval('tickets_squad_sequence'::text),
    ticket_id           BIGINT NOT NULL CONSTRAINT fcm_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    user_id             BIGINT NOT NULL CONSTRAINT fcm_user REFERENCES users(id) ON DELETE RESTRICT,
    content             TEXT,
    attachments         JSONB NULL,
    is_edited           BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, sent_at)
) PARTITION BY RANGE (sent_at);

-- 파티션: 월별 파티션 생성 예시
CREATE TABLE ticket_chat_messages_2026_01 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE ticket_chat_messages_2026_02 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE ticket_chat_messages_2026_03 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE ticket_chat_messages_2026_04 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE ticket_chat_messages_2026_05 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE ticket_chat_messages_2026_06 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');

-- 파티션 인덱스 (각 파티션에 자동 생성)
CREATE INDEX fcm_ticket_id_idx ON ticket_chat_messages_part_master (ticket_id);
CREATE INDEX fcm_user_id_idx ON ticket_chat_messages_part_master (user_id);
CREATE INDEX fcm_time_idx ON ticket_chat_messages_part_master (ticket_id, sent_at);
```
### 4.2.6 extension_requests 테이블

만기 연장(Extension) 신청용 테이블.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 소속 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `requester_id` | BIGINT (FK) | 신청자 (users Table 참조) ON DELETE RESTRICT |
| `approver_id` | BIGINT (FK) | Nullable, 승인자 (users Table 참조) ON DELETE SET NULL |
| `requested_date` | DATE | 원하는 연장 완료일 |
| `reason` | TEXT | 연장 사유 |
| `status` | VARCHAR(20) | status IN ('PENDING', 'APPROVED', 'REJECTED') |
| `created_at` | TIMESTAMPTZ | 생성 일시 |

```sql
CREATE TABLE extension_requests (
    id              BIGSERIAL PRIMARY KEY,
    ticket_id       BIGINT NOT NULL CONSTRAINT er_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    requester_id    BIGINT NOT NULL CONSTRAINT er_requester REFERENCES users(id) ON DELETE RESTRICT,
    approver_id     BIGINT NULL CONSTRAINT er_approver REFERENCES users(id) ON DELETE SET NULL,
    requested_date  DATE NOT NULL,
    reason          TEXT,
    status          VARCHAR(20) NOT NULL CONSTRAINT chk_er_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_er_ticket_id ON extension_requests (ticket_id);
CREATE INDEX idx_er_requester_id ON extension_requests (requester_id);
CREATE INDEX idx_er_status ON extension_requests (status);

CREATE TRIGGER trg_er_updated_at
    BEFORE UPDATE OF updated_at ON extension_requests
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

### 4.2.7 ticket_histories 테이블

모든 상태 전이(State Transition)를 기록하는 테이블. **PG 15 Native Table Partitioning** 대상 (RANGE by changed_at).

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `ticket_id` | BIGINT (FK) | 대상 Ticket (tickets Table 참조) ON DELETE CASCADE |
| `from_status` | VARCHAR(50) | 이전 상태 코드 |
| `to_status` | VARCHAR(50) | 새 상태 코드 |
| `changed_by` | BIGINT (FK) | 변화 발동 User ID (users Table 참조) ON DELETE SET NULL |
| `changed_at` | TIMESTAMPTZ | 변경 일시 (Partition Key) |
| `reason` | TEXT | 해당 설명 (Nullable) |

```sql
-- 파티션 마스터 테이블
CREATE TABLE ticket_histories_part_master (
    id              BIGSERIAL,
    ticket_id       BIGINT NOT NULL CONSTRAINT th_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    from_status     VARCHAR(50) NOT NULL,
    to_status       VARCHAR(50) NOT NULL,
    changed_by      BIGINT NULL CONSTRAINT th_changed_by REFERENCES users(id) ON DELETE SET NULL,
    changed_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason          TEXT,
    PRIMARY KEY (id, changed_at)
) PARTITION BY RANGE (changed_at);

-- 분기별 파티션 생성 예시
CREATE TABLE ticket_histories_2026_Q1 PARTITION OF ticket_histories_part_master
    FOR VALUES FROM ('2026-01-01') TO ('2026-04-01');
CREATE TABLE ticket_histories_2026_Q2 PARTITION OF ticket_histories_part_master
    FOR VALUES FROM ('2026-04-01') TO ('2026-07-01');
CREATE TABLE ticket_histories_2026_Q3 PARTITION OF ticket_histories_part_master
    FOR VALUES FROM ('2026-07-01') TO ('2026-10-01');
CREATE TABLE ticket_histories_2026_Q4 PARTITION OF ticket_histories_part_master
    FOR VALUES FROM ('2026-10-01') TO ('2027-01-01');

-- 파티션 인덱스
CREATE INDEX th_ticket_id_idx ON ticket_histories_part_master (ticket_id);
CREATE INDEX th_changed_by_idx ON ticket_histories_part_master (changed_by);
CREATE INDEX th_time_idx ON ticket_histories_part_master (changed_at);
```
---

## 4.3 추가 테이블 (Enhanced DDL)

### 4.3.1 audit_logs 테이블

모든 데이터 변경 이력을 추적하는 감사 로그 테이블. JSONB(GIN) 인덱스를 통해 old_values/new_values高效検索.

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `entity_type` | VARCHAR(50) | 대상 Entity 유형 (tickets, projects, users, 등) |
| `entity_id` | BIGINT | 대상 Entity ID |
| `action` | VARCHAR(20) | action IN ('CREATE', 'UPDATE', 'DELETE', 'SOFT_DELETE') |
| `changed_by` | BIGINT (FK) | 변화 발동 User (users Table 참조) ON DELETE SET NULL |
| `changed_at` | TIMESTAMPTZ | 변경 일시 |
| `old_values` | JSONB | 변경 전 값 (nullable) |
| `new_values` | JSONB | 변경 후 값 (nullable) |

```sql
CREATE TABLE audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       BIGINT NOT NULL,
    action          VARCHAR(20) NOT NULL CONSTRAINT chk_al_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'SOFT_DELETE')),
    changed_by      BIGINT NULL CONSTRAINT fk_al_changed_by REFERENCES users(id) ON DELETE SET NULL,
    changed_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_values      JSONB NULL,
    new_values      JSONB NULL
);

-- FK 인덱스
CREATE INDEX idx_al_changed_by ON audit_logs (changed_by);

-- 복합 인덱스 (Entity별 이력 조회)
CREATE INDEX idx_al_entity ON audit_logs (entity_type, entity_id, changed_at);

-- GIN 인덱스 (JSONB高效검색)
CREATE INDEX idx_al_old_values ON audit_logs USING GIN (old_values);
CREATE INDEX idx_al_new_values ON audit_logs USING GIN (new_values);
```

### 4.3.2 notification_event_subscriptions 테이블

User별 이벤트(IN-APP, PUSH, EMAIL) 구독 설정 테이블.

| Field | Type | Notes |
|-------|------|-------|
| `user_id` | BIGINT (FK) | 구독 User (users Table 참조) ON DELETE CASCADE |
| `event_type` | VARCHAR(20) | 이벤트 유형 (EVT-01~EVT-10) |
| `channel` | VARCHAR(20) | 채널 (IN_APP, PUSH, EMAIL, SMS) |
| `is_active` | BOOLEAN | 활성 여부 (기본값: TRUE) |
| `created_at` | TIMESTAMPTZ | 생성 일시 |
| `PRIMARY KEY` | (user_id, event_type) | 복합 키 |

```sql
CREATE TABLE notification_event_subscriptions (
    user_id         BIGINT NOT NULL CONSTRAINT fk_nes_user REFERENCES users(id) ON DELETE CASCADE,
    event_type      VARCHAR(20) NOT NULL,
    channel         VARCHAR(20) NOT NULL CONSTRAINT chk_nes_channel CHECK (channel IN ('IN_APP', 'PUSH', 'EMAIL', 'SMS')),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, event_type)
);

CREATE INDEX idx_nes_channel ON notification_event_subscriptions (channel);
CREATE INDEX idx_nes_user_active ON notification_event_subscriptions (user_id, is_active);
```
### 4.3.3 notification_preferences 테이블 (FIXED: QUIET enum)

각 User별 알림 채널별 설정을 정의하는 테이블.

**Fix Note:** 기존 DDL의 quiet_hours_mode ENUM('SILENCE', 'QUEUE') DEFAULT 'QUIET'는 'QUIET'가 ENUM 목록에 없음. VARCHAR(20) + CHECK 제약조건으로 변경.

```sql
CREATE TABLE notification_preferences (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL CONSTRAINT uk_notif_prefs UNIQUE CONSTRAINT fk_np_user REFERENCES users(id) ON DELETE CASCADE,
    in_app_enabled  BOOLEAN DEFAULT TRUE,
    push_enabled    BOOLEAN DEFAULT FALSE,
    email_enabled   BOOLEAN DEFAULT TRUE,
    sound_enabled   BOOLEAN DEFAULT TRUE,
    badge_enabled   BOOLEAN DEFAULT TRUE,
    quiet_hours_start  TIME DEFAULT '22:00:00',
    quiet_hours_end    TIME DEFAULT '08:00:00',
    quiet_hours_mode  VARCHAR(20) DEFAULT 'QUIET' CONSTRAINT chk_npm CHECK (quiet_hours_mode IN ('SILENCE', 'QUEUE', 'QUIET')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_np_updated_at
    BEFORE UPDATE OF updated_at ON notification_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**이벤트별 상세 설정:** notification_event_subscriptions 테이블에서 User별 EVT-01~EVT-10까지 개별 Enable/Disable와 Channel Setting을 정의.

| Channel | 이벤트 | Setting 예 |
|---------|--------|----------|
| In-App Popup | EVT-04, EVT-05 | always |
| In-App Sidebar | EVT-01~EVT-03, EVT-06~EVT-10 | always |
| Push | EVT-01~EVT-06, EVT-08 | Quiet hours 제외 항상 |
| Email | EVT-05, EVT-09 | Daily digest (18시) |

**Quiet Hours (수면 모드):**

사용자 설정 시간에 P0-P1 알림은 전송되지 않고 Queue에 쌓여 Quiet Hours 종료 후 일괄 전송. Quiet hours를 'SILENCE' 모드로 설정하면 완전히 차단 (P2+만 알림). 'QUIET'는 Queue 상태로 알림을 일시 보류하고 Quiet Hours 종료 후 일괄 발송.
### 4.3.4 notification_logs 테이블 (Partitioned)

모든 알림 이력을 기록하는 테이블. **PG 15 Native Table Partitioning** 대상 (RANGE by created_at).

| Field | Type | Notes |
|-------|------|-------|
| `id` | BIGINT (PK) | Primary key |
| `event_id` | VARCHAR(20) | EVT-01~EVT-10 |
| `target_user_id` | BIGINT (FK) | 알림 수신자 (users Table 참조) ON DELETE CASCADE |
| `ticket_id` | BIGINT (FK) | Nullable, 관련 Ticket ON DELETE CASCADE |
| `project_id` | BIGINT (FK) | Nullable, 관련 Project ON DELETE SET NULL |
| `payload` | JSONB | 전체 알림 데이터 |
| `sent_via` | VARCHAR(20) | IN_APP / PUSH / EMAIL / SMS |
| `status` | VARCHAR(20) | QUEUED / SENT / FAILED / DELIVERED / READ / DISMISSED |
| `read_at` | TIMESTAMPTZ | 읽은 일시 (nullable) |
| `dismissed_at` | TIMESTAMPTZ | dismissed한 일시 (nullable) |
| `created_at` | TIMESTAMPTZ | 생성 일시 (Partition Key) |

```sql
-- 파티션 마스터 테이블
CREATE TABLE notification_logs_part_master (
    id              BIGSERIAL,
    event_id        VARCHAR(20) NOT NULL,
    target_user_id  BIGINT NOT NULL CONSTRAINT nlg_user REFERENCES users(id) ON DELETE CASCADE,
    ticket_id       BIGINT NULL CONSTRAINT nlg_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    project_id      BIGINT NULL CONSTRAINT nlg_project REFERENCES projects(id) ON DELETE SET NULL,
    payload         JSONB NULL,
    sent_via        VARCHAR(20) NULL CONSTRAINT chk_nlg_via CHECK (sent_via IN ('IN_APP', 'PUSH', 'EMAIL', 'SMS')),
    status          VARCHAR(20) NOT NULL DEFAULT 'QUEUED' CONSTRAINT chk_nlg_status CHECK (status IN ('QUEUED', 'SENT', 'FAILED', 'DELIVERED', 'READ', 'DISMISSED')),
    read_at         TIMESTAMPTZ NULL,
    dismissed_at    TIMESTAMPTZ NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 월별 파티션 생성 패턴
CREATE TABLE notification_logs_2026_01 PARTITION OF notification_logs_part_master FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
CREATE TABLE notification_logs_2026_02 PARTITION OF notification_logs_part_master FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');

-- 파티션 인덱스
CREATE INDEX nlg_user_id_idx ON notification_logs_part_master (target_user_id);
CREATE INDEX nlg_event_id_idx ON notification_logs_part_master (event_id);
CREATE INDEX nlg_status_idx ON notification_logs_part_master (status);
CREATE INDEX nlg_created_at_idx ON notification_logs_part_master (created_at);
CREATE INDEX nlg_ticket_id_idx ON notification_logs_part_master (ticket_id);
```

---

## 4.4 Status Lookup Tables

각 Entity Type별 상태/우선순위/유형 Lookup Table 정의.

### 4.4.1 ticket_status_types 테이블

```sql
CREATE TABLE ticket_status_types (
    id              BIGSERIAL PRIMARY KEY,
    status_key      VARCHAR(50) NOT NULL,
    entity_type     VARCHAR(50) NOT NULL DEFAULT 'tickets',
    display_name    VARCHAR(100) NOT NULL,
    color_code      VARCHAR(7) DEFAULT '#6B7280',
    sort_order      INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO ticket_status_types (status_key, display_name, color_code, sort_order) VALUES
    ('REGISTERED', '등록됨', '#6B7280', 1),
    ('RECEIVED', '접수됨', '#3B82F6', 2),
    ('PROCESSING', '처리 중', '#F59E0B', 3),
    ('DELAYED', '지체됨', '#EF4444', 4),
    ('COMPLETION_REQUESTED', '완료 요청됨', '#8B5CF6', 5),
    ('APPROVED', '승인됨', '#10B981', 6),
    ('COMPLETED', '완료됨', '#047857', 7);

CREATE UNIQUE INDEX uidx_ticket_status ON ticket_status_types (status_key, entity_type);
CREATE INDEX idx_ticket_status_entity ON ticket_status_types (status_key, entity_type);
```

### 4.4.2 ticket_priority_types 테이블

```sql
CREATE TABLE ticket_priority_types (
    id              BIGSERIAL PRIMARY KEY,
    priority_key    VARCHAR(50) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    color_code      VARCHAR(7) DEFAULT '#6B7280',
    sort_order      INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO ticket_priority_types (priority_key, display_name, color_code, sort_order) VALUES
    ('LOW', '저', '#6B7280', 1),
    ('MEDIUM', '중', '#3B82F6', 2),
    ('HIGH', '높음', '#F59E0B', 3),
    ('URGENT', '긴급', '#EF4444', 4);

CREATE UNIQUE INDEX uidx_ticket_priority ON ticket_priority_types (priority_key);
```

### 4.4.3 project_status_types 테이블

```sql
CREATE TABLE project_status_types (
    id              BIGSERIAL PRIMARY KEY,
    status_key      VARCHAR(50) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    color_code      VARCHAR(7) DEFAULT '#6B7280',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO project_status_types (status_key, display_name, color_code) VALUES
    ('ACTIVE', '활성', '#10B981'),
    ('COMPLETED', '완료', '#6B7280'),
    ('ON_HOLD', '보류', '#F59E0B'),
    ('CANCELLED', '취소', '#EF4444');

CREATE UNIQUE INDEX uidx_project_status ON project_status_types (status_key);
```

### 4.4.4 card_type_categories 테이블

```sql
CREATE TABLE card_type_categories (
    id              BIGSERIAL PRIMARY KEY,
    category_key    VARCHAR(50) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO card_type_categories (category_key, display_name, description) VALUES
    ('DEFAULT', '기본', '기본 프로젝트 카드'),
    ('SERVER', '서버', '서버 정보 카드'),
    ('SW', 'SW', '소프트웨어 라이선스 카드'),
    ('ACCOUNT', '계정', '접속 계정 카드');

CREATE UNIQUE INDEX uidx_card_type ON card_type_categories (category_key);
```
---

## 4.5 Entity-Relationships (ERD)

### 4.5.1 관계 정의

아래는 각 테이블 간의 관계(Foreign Key)와 참조 무결성을 명시합니다. 모든 FK 제약조건에는 ON DELETE CASCADE/RESTRICT/SET NULL이 명시되어 있습니다.

#### companies
- companies ↔ users: 1:N (회사 → 사용자)
  - `fk_users_company`: users.company_id → companies.id **ON DELETE RESTRICT**
- companies ↔ projects: 1:N (회사 → 프로젝트)
  - `fk_projects_company`: projects.company_id → companies.id **ON DELETE RESTRICT**

#### users
- users ↔ projects: 1:N (관리자 → 프로젝트)
  - `fk_projects_manager`: projects.manager_id → users.id **ON DELETE SET NULL**
- users ↔ tickets: 1:N (수취인)
  - `fk_tickets_customer`: tickets.customer_id → users.id **ON DELETE SET NULL**
- users ↔ tickets: 1:N (담당자)
  - `fk_tickets_assignee`: tickets.assignee_id → users.id **ON DELETE SET NULL**
- users ↔ tickets: 1:N (작성자)
  - `fk_tickets_author`: tickets.author_id → users.id **ON DELETE RESTRICT**
- users ↔ users: 자기참조 (계층구조)
  - `fk_users_parent`: users.parent_id → users(id) **ON DELETE SET NULL**

#### projects
- projects ↔ projects_management_cards: 1:N (프로젝트 → 카드)
  - `fk_pmc_project`: projects_management_cards.project_id → projects.id **ON DELETE CASCADE**
- projects ↔ tickets: 1:N (프로젝트 → 티켓)
  - `fk_tickets_project`: tickets.project_id → projects.id **ON DELETE CASCADE**

#### tickets
- tickets ↔ processing_plans: 1:N (티켓 → 처리 계획)
  - `fk_pp_ticket`: processing_plans.ticket_id → tickets.id **ON DELETE CASCADE**
- tickets ↔ ticket_comments: 1:N (티켓 → 댓글)
  - `fk_tc_ticket`: ticket_comments.ticket_id → tickets.id **ON DELETE CASCADE**
- tickets ↔ ticket_comments: 자기참조 (대댓글)
  - `fk_tc_parent`: ticket_comments.parent_comment_id → ticket_comments(id) **ON DELETE SET NULL**
- tickets ↔ ticket_files: 1:N (티켓 → 첨부파일)
  - `fk_tf_ticket`: ticket_files.ticket_id → tickets.id **ON DELETE CASCADE**
- tickets ↔ ticket_chat_messages_part_master: 1:N (티켓 → 채팅 메시지)
  - `fk_tcm_ticket`: ticket_chat_messages_part_master.ticket_id → tickets.id **ON DELETE CASCADE**
- tickets ↔ extension_requests: 1:N (티켓 → 연장 요청)
  - `fk_er_ticket`: extension_requests.ticket_id → tickets.id **ON DELETE CASCADE**
- tickets ↔ ticket_histories_part_master: 1:N (티켓 → 상태 전환 이력)
  - `fk_th_ticket`: ticket_histories_part_master.ticket_id → tickets.id **ON DELETE CASCADE**

#### card_assignments
- cards ↔ tickets: 다대다 (카드 ↔ 티켓)
  - `fk_sa_card`: card_assignments.card_id → projects_management_cards(id) **ON DELETE CASCADE**
  - `fk_sa_ticket`: card_assignments.ticket_id → tickets(id) **ON DELETE CASCADE**

#### project_managers / project_support_managers (Junction Tables)
- projects ↔ users (관리자 매핑): M:N
  - `fk_pm_project`: project_managers.project_id → projects(id) **ON DELETE CASCADE**
  - `fk_pm_user`: project_managers.user_id → users(id) **ON DELETE CASCADE**
  - `fk_psm_project`: project_support_managers.project_id → projects(id) **ON DELETE CASCADE**
  - `fk_psm_user`: project_support_managers.user_id → users(id) **ON DELETE CASCADE**

#### audit_logs
- users ↔ audit_logs: 1:N (작업자)
  - `fk_al_actor`: audit_logs.changed_by → users(id) **ON DELETE SET NULL**

#### notification
- users ↔ notification_event_subscriptions: 1:N (구독자)
  - `fk_nes_user`: notification_event_subscriptions.user_id → users(id) **ON DELETE CASCADE**
- users ↔ notification_preferences: 1:1 (설정)
  - `fk_np_user`: notification_preferences.user_id → users(id) **ON DELETE CASCADE**
- notification_event_types ↔ notification_event_subscriptions: 1:N
  - `fk_nes_event`: notification_event_subscriptions.event_type_id → notification_event_types(id) **ON DELETE CASCADE**
- users ↔ notification_logs_part_master: 1:N (발송 대상자)
  - `fk_nl_user`: notification_logs_part_master.user_id → users(id) **ON DELETE SET NULL**

### 4.6.2 ON DELETE Action 분류

| Action | 적용 대상 | 기준 |
|--------|----------|------|
| CASCADE | tickets → processing_plans, ticket_files, ticket_chat_messages, extension_requests, ticket_histories | 하위 데이터가 함께 삭제 |
| RESTRICT | users → projects, tickets → author, users → audit_logs | 데이터 참조 중이면 삭제 불가 |
| SET NULL | projects → manager_id, users → parent_id, tickets → customer/assignee_id, tickets → author | 참조만 해제하고 데이터는 보존 |
| SET NULL (FK만) | audit_logs → changed_by, notification_logs → user_id, ticket_histories → changed_by | 역사 기록 보존 |

---

### 4.6.3 DB Migration Tool (Flyway) 도입

DDL 변경 시 버전 관리와 이력 추적을 위해 **Flyway** Migration Tool을 도입합니다.

**핵심 원칙:**
- 모든 DDL 변경은 Migration Script(`V{version}__{description}.sql`)로 작성
- Script 이름은 `V1__initial_schema.sql`, `V2__add_tickets_table.sql` 형식
- Flyway Schema Versioning으로 DB 스키마 버전 관리
- Spring Boot와 Flyway 자동으로 연동 (Spring Boot 시작 시 Flyway 자동으로 실행)

**Migration 규칙:**
1. Schema Downgrade(이전 버전으로 되돌리기)는 Production 환경에서 금지
2. Migration 추가 시 반드시 테스트/Stage 환경에서 먼저 적용 검증
3. Migration Script 작성이 완료될 때까지 Feature Branch Merge 금지
4. Migration 실패 시 즉시 Rollback(수동) + Hotfix 진행

**Spring Boot 연동 설정:**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

---

## 4.6 인덱스 총괄

### 4.6.1 FK 인덱스

각 Foreign Key 컬럼에 BTREE 인덱스가 생성되어 조인 성능을 최적화합니다.

| 인덱스명 | 테이블 | 컬럼 |
|---------|--------|------|
| idx_users_parent_id | users | parent_id |
| idx_users_company_id | users | company_id |
| idx_users_role | users | role |
| idx_users_status | users | status |
| idx_projects_company_id | projects | company_id |
| idx_projects_manager | projects | manager_id |
| idx_projects_status | projects | status |
| idx_pm_project_id | project_managers | project_id |
| idx_pm_user_id | project_managers | manager_id |
| idx_psm_project_id | project_support_managers | project_id |
| idx_psm_user_id | project_support_managers | support_id |
| idx_sa_card_id | card_assignments | card_id |
| idx_sa_ticket_id | card_assignments | ticket_id |
| idx_tickets_project_id | tickets | project_id |
| idx_tickets_customer_id | tickets | customer_id |
| idx_tickets_assignee_id | tickets | assignee_id |
| idx_tickets_status | tickets | current_status |
| idx_tickets_type | tickets | type |
| idx_pp_ticket_id | processing_plans | ticket_id |
| idx_pp_writer_id | processing_plans | writer_id |
| idx_tp_ticket_id | ticket_comments | ticket_id |
| idx_tp_parent_id | ticket_comments | parent_comment_id |
| idx_tf_ticket_id | ticket_files | ticket_id |
| idx_er_ticket_id | extension_requests | ticket_id |
| idx_er_requester_id | extension_requests | requester_id |
| idx_th_ticket_id | ticket_histories_part_master | ticket_id |
| idx_al_entity | audit_logs | entity_type |
| idx_al_entity_id | audit_logs | entity_id |
| idx_al_changed_at | audit_logs | changed_at |
| idx_nes_user_active | notification_event_subscriptions | user_id |
| idx_nes_event_type | notification_event_subscriptions | event_type_id |
| idx_nl_user_id | notification_logs_part_master | user_id |
| idx_nl_event_id | notification_logs_part_master | event_type_id |

### 4.6.2 기능별 인덱스

| 인덱스명 | 테이블 | 타입 | 용도 |
|---------|--------|------|------|
| idx_tickets_fts | tickets | GIN (tsvector) | title + description 한국어 Full-Text Search |
| idx_tickets_deleted_at | tickets | BTREE (partial) | WHERE deleted_at IS NULL |
| idx_al_old_values | audit_logs | GIN (jsonb_path_ops) | old_values JSONB 검색 |
| idx_al_new_values | audit_logs | GIN (jsonb_path_ops) | new_values JSONB 검색 |

---

## 4.7 파티션 전략 (Partitioning Strategy)

### PG 15 Native Table Partitioning

| 테이블 | 파티션 키 | 파티션 단위 | PK 구조 |
|--------|----------|-----------|---------|
| ticket_chat_messages_part_master | sent_at | 월별 | (id, sent_at) |
| notification_logs_part_master | sent_at | 월별 | (id, sent_at) |
| ticket_histories_part_master | changed_at | 분기별 | (id, changed_at) |

### 4.7.1 파티션 생성 스크립트

```sql
-- 새로운 월별 파티션 생성 (pg_partman 또는 cronJob 기반)
CREATE TABLE ticket_chat_messages_2026_03 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE notification_logs_2026_03 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');

-- 분기별 파티션 (ticket_histories)
CREATE TABLE ticket_histories_2026_Q1 PARTITION OF ticket_histories_part_master
    FOR VALUES FROM ('2026-01-01') TO ('2026-04-01');

-- 오래된 파티션 분리 (보존정책 적용)
-- DETACH PARTITION ticket_chat_messages_2024_01;
-- DROP TABLE ticket_chat_messages_2024_01;
```

---

## 4.8 소프트 딜리트 정책 (Soft Delete Strategy)

| 테이블 | Soft Delete | 필드명 | 조회 필터 |
|--------|------------|--------|----------|
| users | NO (Hard Delete만) | - | 없음 |
| projects | YES | deleted_at TIMESTAMPTZ | WHERE deleted_at IS NULL |
| tickets | YES | deleted_at TIMESTAMPTZ | WHERE deleted_at IS NULL |
| 나머지 테이블 | YES (FK CASCADE) | - | 하위 데이터 자동 삭제 |

### 4.8.1 Soft Delete Archive Policy (보존 정책)

삭제된 데이터는 단순 `deleted_at` 설정이 아니라, 법규 준수 및 감사 요구사항에 따라 주기적으로 Archive → Purge 된다.

| 데이터 종류 | 보존 기간 | Archive 시점 | Purge 시점 | 저장 방식 |
|------------|----------|-------------|-----------|----------|
| tickets (삭제됨) | 3년 | deleted_at + 1년 이후 | deleted_at + 3년 | 별도 archive 파티션 테이블 |
| ticket_chat_messages | 5년 | sent_at + 3년 이후 | sent_at + 5년 | 파티션 DETACH → Archive Table |
| notification_logs | 2년 | sent_at + 1년 이후 | sent_at + 2년 | 파티션 DETACH → Archive Table |
| audit_logs | 10년 | changed_at + 5년 이후 | changed_at + 10년 | 파티션 DETACH → Archive Table |
| projects (삭제됨) | 5년 | deleted_at + 1년 이후 | deleted_at + 5년 | 별도 archive 파티션 테이블 |

### 4.8.2 Archive/Purge 운영 절차

```
1. Archive 준비 (분기별 Cron Job):
   - 파티션 DETACH: DETACH PARTITION ticket_chat_messages_2024_Q1;
   - Archive Table 생성: CREATE TABLE ticket_chat_messages_archive_2024_Q1 AS ...
   - 원본 파티션 삭제: DROP TABLE ticket_chat_messages_2024_Q1;

2. Archive 저장 위치:
   - PostgreSQL 별도 Tablespace (archive_tablespace)
   - AWS 환경: S3 Glacier Archive (1년 후)로 Cold Storage
   - 인트라넷 환경: 별도 Archive 서버 NAS

3. Purge 실행 (반기별 Cron Job):
   - 보존 기간 만료 Archive Table DROP
   - Example: DROP TABLE audit_logs_archive_2016_Q3; (10년 보존 만료)

4. Audit Trail:
   - Archive/Purge 이력은 별도 audit_log 테이블에 기록
   - 누가, 언제, 어떤 데이터를 Archive/Purge 했는지 추적
```

### 4.8.3 Archive 조회 API

```
GET /api/v1/admin/archive/tickets?period=2024-Q1
Response: Archived ticket data (read-only)
Permissions: Admin / Support Manager以上
```

---

## 4.9 공통 트리거

```sql
-- all_tables_updated_at_trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 적용 대상 테이블:
-- companies, users (trg_users_updated_at)
-- projects (trg_projects_updated_at)
-- cards (trg_pmc_updated_at)
-- tickets (trg_tickets_updated_at)
-- processing_plans (trg_pp_updated_at)
-- ticket_comments (trg_tc_updated_at)
-- extension_requests (trg_er_updated_at)
```

---

## 4.10 확장 테이블 (Extension Tables)

### login_history 테이블

사용자 로그인 기록 추적용 테이블. 보안 감사 및 이상 징후 감지.

```sql
CREATE TABLE login_history (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL CONSTRAINT fk_lh_user REFERENCES users(id) ON DELETE CASCADE,
    ip_address      INET NOT NULL,
    user_agent      TEXT,
    login_method    VARCHAR(20) NOT NULL CONSTRAINT chk_lh_method CHECK (login_method IN ('PASSWORD', 'SAML', 'LDAP', 'SSO')),
    success         BOOLEAN NOT NULL,
    failure_reason  VARCHAR(255),
    logged_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lh_user_id ON login_history (user_id);
CREATE INDEX idx_lh_logged_at ON login_history (logged_at);
CREATE INDEX idx_lh_ip ON login_history (ip_address);
CREATE INDEX idx_lh_success ON login_history (success);
```

---

## 4.11 요약

### 4.11.1 테이블 목록 총정리

| 번호 | 테이블명 | 파티션 | SoftDelete | 특징 |
|------|---------|--------|-----------|------|
| 1 | companies | N | N | 회사 관리 |
| 2 | users | N | N (Hard만) | RBAC, 계층구조 |
| 3 | projects | N | O (deleted_at) | 프로젝트 관리 |
| 4 | project_managers | N | N | Junction Table |
| 5 | project_support_managers | N | N | Junction Table |
| 6 | project_assignments | N | N | 프로젝트 배정 |
| 7 | projects_assignment_histories | N | N | 배정 이력 |
| 8 | projects_management_cards | N | N | 프로젝트 카드 (JSONB) |
| 9 | card_assignments | N | N | 카드-티켓 매핑 |
| 10 | tickets | N | O (deleted_at) | 티켓 관리, FTS 인덱스 |
| 11 | processing_plans | N | N | 처리 계획 |
| 12 | ticket_comments | N | N | 대댓글 지원 |
| 13 | ticket_files | N | N | 첨부파일 |
| 14 | ticket_chat_messages_part_master | O (월별) | N | 채팅 메시지, PG15 파티션 |
| 15 | extension_requests | N | N | 데드라인 연장 |
| 16 | ticket_histories_part_master | O (분기별) | N | 상태전환 이력, PG15 파티션 |
| 17 | audit_logs | N | N | JSONB GIN 인덱스 |
| 18 | notification_event_types | N | N | 알림 이벤트 정의 |
| 19 | notification_event_subscriptions | N | N | 이벤트별 구독 설정 |
| 20 | notification_preferences | N | N | Quiet Hours 포함 |
| 21 | notification_logs_part_master | O (월별) | N | 알림 발송 기록, PG15 파티션 |
| 22 | ticket_status_types | N | N | 조회 테이블 |
| 23 | ticket_priority_types | N | N | 조회 테이블 |
| 24 | project_status_types | N | N | 조회 테이블 |
| 25 | card_type_categories | N | N | 조회 테이블 |
| 26 | login_history | N | N | 로그인 기록 |

**총 26개 테이블** (3개 파티션 테이블 포함)


