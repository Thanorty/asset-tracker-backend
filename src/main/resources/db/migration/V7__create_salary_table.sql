-- Create salary table (base info only)
CREATE TABLE IF NOT EXISTS salaries (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    gross_salary DOUBLE PRECISION NOT NULL,
    salary_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    cycle_day INTEGER DEFAULT 25,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_salaries_user UNIQUE (user_id)
);

-- Dynamic deductions table: users add rows for EPF, SOCSO, tax, etc.
CREATE TABLE IF NOT EXISTS salary_deductions (
    id SERIAL PRIMARY KEY,
    salary_id BIGINT NOT NULL REFERENCES salaries(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    deduction_type VARCHAR(20) NOT NULL DEFAULT 'PERCENTAGE',
    value DOUBLE PRECISION NOT NULL DEFAULT 0,
    CONSTRAINT chk_deduction_type CHECK (deduction_type IN ('PERCENTAGE', 'FIXED'))
);
