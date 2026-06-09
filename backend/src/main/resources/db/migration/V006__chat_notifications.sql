-- Sprint 3: Chat, Notifications, Extension Requests
-- PG 15 Native Table Partitioning for chat_messages and notification_logs

SET timezone = 'Asia/Seoul';

-- ============================================================
-- 1. ticket_chat_messages (Partitioned by sent_at)
-- ============================================================
CREATE TABLE ticket_chat_messages_part_master (
    id              BIGINT NOT NULL DEFAULT nextval('tickets_squad_sequence'),
    ticket_id       BIGINT NOT NULL CONSTRAINT fcm_ticket REFERENCES tickets(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL CONSTRAINT fcm_user REFERENCES users(id) ON DELETE RESTRICT,
    content         TEXT NOT NULL,
    image_url       JSONB NULL,
    is_edited       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at         TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, sent_at)
) PARTITION BY RANGE (sent_at);

-- Monthly partitions for 2026
CREATE TABLE ticket_chat_messages_2026_06 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE ticket_chat_messages_2026_07 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE ticket_chat_messages_2026_08 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE ticket_chat_messages_2026_09 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE ticket_chat_messages_2026_10 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
CREATE TABLE ticket_chat_messages_2026_11 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE ticket_chat_messages_2026_12 PARTITION OF ticket_chat_messages_part_master
    FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');

CREATE INDEX fcm_ticket_id_idx ON ticket_chat_messages_part_master (ticket_id);
CREATE INDEX fcm_user_id_idx ON ticket_chat_messages_part_master (user_id);
CREATE INDEX fcm_time_idx ON ticket_chat_messages_part_master (ticket_id, sent_at);

-- ============================================================
-- 2. extension_requests
-- ============================================================
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

-- ============================================================
-- 3. notification_event_subscriptions
-- ============================================================
CREATE TABLE notification_event_subscriptions (
    user_id         BIGINT NOT NULL CONSTRAINT fk_nes_user REFERENCES users(id) ON DELETE CASCADE,
    event_type      VARCHAR(50) NOT NULL,
    channel         VARCHAR(20) NOT NULL CONSTRAINT chk_nes_channel CHECK (channel IN ('IN_APP', 'PUSH', 'EMAIL', 'SMS')),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, event_type)
);

CREATE INDEX idx_nes_channel ON notification_event_subscriptions (channel);
CREATE INDEX idx_nes_user_active ON notification_event_subscriptions (user_id, is_active);

-- ============================================================
-- 4. notification_preferences
-- ============================================================
CREATE TABLE notification_preferences (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL CONSTRAINT uk_notif_prefs UNIQUE CONSTRAINT fk_np_user REFERENCES users(id) ON DELETE CASCADE,
    in_app_enabled      BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled        BOOLEAN NOT NULL DEFAULT FALSE,
    email_enabled       BOOLEAN NOT NULL DEFAULT FALSE,
    sound_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    badge_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_hours_start   VARCHAR(5),
    quiet_hours_end     VARCHAR(5),
    quiet_hours_mode    VARCHAR(10),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_np_updated_at
    BEFORE UPDATE OF updated_at ON notification_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- 5. notification_logs (Partitioned by created_at)
-- ============================================================
CREATE TABLE notification_logs_part_master (
    id              BIGSERIAL,
    event_id        VARCHAR(50) NOT NULL,
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

-- Monthly partitions for 2026-07
CREATE TABLE notification_logs_2026_07 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE notification_logs_2026_08 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');
CREATE TABLE notification_logs_2026_09 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE notification_logs_2026_10 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
CREATE TABLE notification_logs_2026_11 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-11-01') TO ('2026-12-01');
CREATE TABLE notification_logs_2026_12 PARTITION OF notification_logs_part_master
    FOR VALUES FROM ('2026-12-01') TO ('2027-01-01');

CREATE INDEX nlg_user_id_idx ON notification_logs_part_master (target_user_id);
CREATE INDEX nlg_event_id_idx ON notification_logs_part_master (event_id);
CREATE INDEX nlg_status_idx ON notification_logs_part_master (status);
CREATE INDEX nlg_created_at_idx ON notification_logs_part_master (created_at);
CREATE INDEX nlg_ticket_id_idx ON notification_logs_part_master (ticket_id);
