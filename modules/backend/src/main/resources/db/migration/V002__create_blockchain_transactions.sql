-- Create blockchain_transactions table for Web3 analytics
-- Design optimized for Excel Power Query consumption and pivot table analysis

CREATE TABLE IF NOT EXISTS blockchain_transactions (
    -- Primary key and unique identifier
    tx_hash VARCHAR(66) PRIMARY KEY,

    -- Temporal columns (indexed for analytics)
    timestamp TIMESTAMPTZ NOT NULL,
    date DATE NOT NULL,

    -- Network and address columns
    chain VARCHAR(50) NOT NULL,
    from_address VARCHAR(42) NOT NULL,
    to_address VARCHAR(42) NOT NULL,

    -- Amount columns (numeric with high precision)
    amount_native NUMERIC(38, 18) NOT NULL,
    amount_usd NUMERIC(38, 2) NOT NULL,

    -- Gas columns (optional)
    gas_used BIGINT,
    gas_price_gwei NUMERIC(20, 9)
);

-- Create indexes for common query patterns
CREATE INDEX idx_transactions_date ON blockchain_transactions(date);
CREATE INDEX idx_transactions_chain ON blockchain_transactions(chain);
CREATE INDEX idx_transactions_from_address ON blockchain_transactions(from_address);
CREATE INDEX idx_transactions_chain_date ON blockchain_transactions(chain, date);

-- Comments for documentation
COMMENT ON TABLE blockchain_transactions IS 'Normalized blockchain transaction data for Web3 analytics. One row = one transaction. Optimized for Excel Power Query consumption.';
COMMENT ON COLUMN blockchain_transactions.tx_hash IS 'Unique transaction hash (primary key)';
COMMENT ON COLUMN blockchain_transactions.date IS 'Transaction date (UTC) - used for pivot table grouping';
COMMENT ON COLUMN blockchain_transactions.chain IS 'Blockchain network name (e.g., Ethereum, Base, Polygon)';
COMMENT ON COLUMN blockchain_transactions.amount_native IS 'Transaction amount in native token (e.g., ETH, MATIC)';
COMMENT ON COLUMN blockchain_transactions.amount_usd IS 'Transaction amount in USD at transaction date';
