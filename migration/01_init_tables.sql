-- =========================
-- ASSETS TABLE
-- =========================
CREATE TABLE IF NOT EXISTS assets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- CRYPTO, STOCK, GOLD
    quantity NUMERIC(18,6) NOT NULL,
    current_value NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- EXPENSES TABLE
-- =========================
CREATE TABLE IF NOT EXISTS expenses (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    amount NUMERIC(18,2) NOT NULL
);

-- =========================
-- INDEXES (good practice)
-- =========================
CREATE INDEX IF NOT EXISTS idx_assets_type ON assets(type);
CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(date);
CREATE INDEX IF NOT EXISTS idx_expenses_category ON expenses(category);
