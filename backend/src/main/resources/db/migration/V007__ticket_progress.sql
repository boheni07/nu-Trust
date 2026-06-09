-- V007: Add ticket progress field (WBS 408)

ALTER TABLE tickets ADD COLUMN progress SMALLINT NOT NULL DEFAULT 0 CONSTRAINT chk_tickets_progress CHECK (progress >= 0 AND progress <= 100);
