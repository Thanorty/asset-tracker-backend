-- Add recurring expense fields
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS recurring BOOLEAN DEFAULT FALSE;
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS recurrence_interval VARCHAR(20); -- WEEKLY, MONTHLY, YEARLY
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS end_date DATE;

-- Add buy_price to assets for P/L calculation
ALTER TABLE assets ADD COLUMN IF NOT EXISTS buy_price DOUBLE PRECISION;

