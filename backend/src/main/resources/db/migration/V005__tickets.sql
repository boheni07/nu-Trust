-- V005: Ticket Management Tables

-- tickets 테이블
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

-- processing_plans 테이블
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

-- ticket_comments 테이블
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
