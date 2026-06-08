-- V003__companies.sql
-- Company table for CRUD API (Sprint 2)

CREATE TABLE IF NOT EXISTS companies (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200)  NOT NULL,
    business_number VARCHAR(20),
    address         VARCHAR(500),
    status          VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

ALTER TABLE users ADD CONSTRAINT fk_users_company_id FOREIGN KEY (company_id) REFERENCES companies(id);

-- Seed data
INSERT INTO companies (name, business_number, status) VALUES
    ('(주)엔유비즈',      '1234567890', 'ACTIVE'),
    ('엔유트러스트 파트너사1', '9876543210', 'ACTIVE'),
    ('엔유트러스트 파트너사2', '1122334455', 'INACTIVE');
