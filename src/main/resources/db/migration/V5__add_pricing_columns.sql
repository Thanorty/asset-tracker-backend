-- Add columns for external price tracking
ALTER TABLE assets ADD COLUMN IF NOT EXISTS symbol VARCHAR(20);
ALTER TABLE assets ADD COLUMN IF NOT EXISTS external_id VARCHAR(100);
ALTER TABLE assets ADD COLUMN IF NOT EXISTS unit_price DOUBLE PRECISION;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS last_price_update TIMESTAMP;
ALTER TABLE assets ADD COLUMN IF NOT EXISTS currency VARCHAR(10) DEFAULT 'USD';

CREATE INDEX IF NOT EXISTS idx_assets_external_id ON assets(external_id);
CREATE INDEX IF NOT EXISTS idx_assets_symbol ON assets(symbol);

