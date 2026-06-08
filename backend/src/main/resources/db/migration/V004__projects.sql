-- V004: Project Management Tables

-- projects 테이블
CREATE TABLE projects (
    id                  BIGSERIAL PRIMARY KEY,
    company_id          BIGINT NOT NULL REFERENCES companies(id),
    customer_company_id BIGINT NULL REFERENCES companies(id),
    project_name        VARCHAR(255) NOT NULL,
    owner_id            BIGINT NOT NULL REFERENCES users(id),
    contract_date       DATE,
    start_date          DATE,
    end_date            DATE,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'ON_HOLD', 'CANCELLED')),
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

-- project_managers 연결 테이블
CREATE TABLE project_managers (
    project_id    BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    manager_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role          VARCHAR(50) NOT NULL DEFAULT 'PROJECT_ADMIN',
    assigned_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by   BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (project_id, manager_id)
);

CREATE INDEX idx_pm_assigned_by ON project_managers (assigned_by);

-- project_support_managers 연결 테이블
CREATE TABLE project_support_managers (
    project_id    BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    support_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role          VARCHAR(50) NOT NULL DEFAULT 'SUPPORT_SUPPORTER',
    assigned_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by   BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (project_id, support_id)
);

CREATE INDEX idx_psm_assigned_by ON project_support_managers (assigned_by);
