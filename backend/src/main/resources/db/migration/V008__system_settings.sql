-- V008__system_settings.sql

CREATE TABLE business_calendars (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    calendar_name VARCHAR(255) NOT NULL,
    color_code VARCHAR(7),
    start_time TIME NOT NULL DEFAULT '09:00:00',
    end_time TIME NOT NULL DEFAULT '18:00:00',
    break_start TIME DEFAULT '12:00:00',
    break_end TIME DEFAULT '13:00:00',
    working_days VARCHAR(50) NOT NULL DEFAULT 'MON,TUE,WED,THU,FRI',
    is_default BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE holidays (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    holiday_date DATE NOT NULL,
    holiday_name VARCHAR(255) NOT NULL,
    holiday_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_business_calendars_company ON business_calendars(company_id);
CREATE INDEX idx_holidays_company ON holidays(company_id);
CREATE INDEX idx_holidays_date ON holidays(holiday_date);

ALTER TABLE tickets ADD COLUMN IF NOT EXISTS deadline_date DATE;
