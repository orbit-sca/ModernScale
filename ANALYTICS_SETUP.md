# Web3 Blockchain Analytics System - Setup Guide

## Overview

This document describes the new blockchain analytics system for ingesting, normalizing, and exporting Web3 transaction data for Excel Power Query consumption.

## Architecture

```
┌─────────────────────┐
│ Blockchain APIs     │ (Etherscan, Basescan, etc.)
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ BlockchainApiClient │ Fetch raw transaction data
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ TxIngestionService  │ Normalize & enrich with USD prices
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ TransactionRepo     │ Persist to PostgreSQL
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ CsvExportService    │ Stream as CSV
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ GET /analytics/     │ Excel Power Query endpoint
│  transactions.csv   │
└─────────────────────┘
```

## Components

### 1. Domain Models (`modules/shared/src/main/scala/com/brice/domain/analytics.scala`)

- `TransactionRow` - Canonical transaction format
- `BlockchainNetwork` - Supported networks
- `RawTransaction` - API response format
- `DailyPrice` - USD price lookup
- `IngestionConfig` - Ingestion configuration
- `IngestionResult` - Batch result

### 2. Blockchain API Client (`modules/backend/.../clients/BlockchainApiClient.scala`)

Fetches raw transaction data from Etherscan-style APIs:
- Ethereum: https://api.etherscan.io/api
- Base: https://api.basescan.org/api
- Polygon: https://api.polygonscan.com/api
- Arbitrum: https://api.arbiscan.io/api
- Optimism: https://api-optimistic.etherscan.io/api

### 3. Price Service (`modules/backend/.../services/PriceService.scala`)

Provides USD price lookups for tokens by date:
- `InMemory` - Mock implementation (current)
- `CoinGecko` - Stub for future implementation

**TODO**: Integrate with CoinGecko API for real historical prices.

### 4. Ingestion Service (`modules/backend/.../services/TransactionIngestionService.scala`)

Normalizes raw blockchain data:
1. Filters failed transactions (`isError != "0"`)
2. Filters zero-value transactions
3. Converts wei to native token (ETH, MATIC, etc.)
4. Normalizes addresses to lowercase
5. Enriches with USD prices
6. Returns `TransactionRow` list

### 5. Transaction Repository (`modules/backend/.../repositories/TransactionRepository.scala`)

Quill-based PostgreSQL repository:
- `upsert(tx)` - Insert or update single transaction
- `upsertBatch(txs)` - Batch insert (idempotent)
- `findAll()` - Get all transactions
- `findByDateRange()` - Filter by date
- `findByChain()` - Filter by chain
- `count()` - Total count

### 6. CSV Export Service (`modules/backend/.../services/CsvExportService.scala`)

Exports transactions as CSV:
- UTF-8 BOM for Excel compatibility
- Stable column ordering
- Streaming for large datasets

### 7. Analytics Routes (`modules/backend/.../http/AnalyticsRoutes.scala`)

HTTP endpoints:
- `GET /analytics/transactions.csv` - CSV export for Excel
- `GET /analytics/transactions` - JSON format
- `GET /analytics/stats` - Analytics statistics
- `POST /analytics/ingest` - Trigger manual ingestion

## Database Setup

### Step 1: Run Flyway Migration

The schema is defined in `modules/backend/src/main/resources/db/migration/V002__create_blockchain_transactions.sql`.

Run Flyway migration:
```bash
sbt "backend/flywayMigrate"
```

This creates the `blockchain_transactions` table with:
- `tx_hash` (VARCHAR(66)) - Primary key
- `timestamp` (TIMESTAMPTZ) - Transaction time
- `date` (DATE) - Transaction date (UTC)
- `chain` (VARCHAR(50)) - Network name
- `from_address` (VARCHAR(42)) - Sender
- `to_address` (VARCHAR(42)) - Receiver
- `amount_native` (NUMERIC(38,18)) - Native token amount
- `amount_usd` (NUMERIC(38,2)) - USD amount
- `gas_used` (BIGINT) - Gas used
- `gas_price_gwei` (NUMERIC(20,9)) - Gas price in Gwei

### Step 2: Configure Database Connection

Add to `.env` or `application.conf`:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/modernscale
DATABASE_USER=postgres
DATABASE_PASSWORD=yourpassword
```

### Step 3: Wire Up Services in Application

Update `HttpServer.scala` to include analytics services (see example below).

## API Keys Setup

### Blockchain API Keys

Get free API keys from:
- Etherscan: https://etherscan.io/apis
- Basescan: https://basescan.org/apis
- Polygonscan: https://polygonscan.com/apis

Add to `.env`:
```
ETHERSCAN_API_KEY=your_etherscan_key
BASESCAN_API_KEY=your_basescan_key
POLYGONSCAN_API_KEY=your_polygonscan_key
```

### Price Data API (Optional)

For production, integrate CoinGecko:
- CoinGecko API: https://www.coingecko.com/en/api

## Usage

### 1. Ingest Transactions

POST request to `/analytics/ingest`:
```json
{
  "network": "Ethereum",
  "walletAddress": "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
  "apiKey": "YOUR_ETHERSCAN_API_KEY",
  "startBlock": null,
  "endBlock": null
}
```

Response:
```json
{
  "network": "Ethereum",
  "totalFetched": 150,
  "totalNormalized": 145,
  "totalPersisted": 145,
  "failures": []
}
```

### 2. Export to Excel

**Method 1: Power Query (Recommended)**

1. Open Excel
2. Data → Get Data → From Other Sources → From Web
3. Enter URL: `http://localhost:8080/analytics/transactions.csv`
4. Load data
5. Create Pivot Table:
   - Rows: `date` (grouped by Month/Week)
   - Columns: `chain`
   - Values: `amount_usd` (Sum), Count of `tx_hash`, Distinct count of `from_address`

**Method 2: Direct Download**

Visit `http://localhost:8080/analytics/transactions.csv` to download CSV.

### 3. Refresh Data

In Excel:
- Data → Refresh All
- Or right-click table → Refresh

Power Query will re-fetch from the API.

## Excel Pivot Table Example

**Rows:** date (grouped by Week)
**Columns:** chain
**Values:**
- Sum of amount_usd
- Count of tx_hash (= transaction count)
- Distinct count of from_address (= unique wallets)

**Result:**
```
Week          | Ethereum | Base    | Polygon | Total
------------- | -------- | ------- | ------- | ------
2024-W01      | $45,230  | $12,400 | $8,900  | $66,530
2024-W02      | $52,100  | $15,600 | $9,300  | $77,000
...
```

## Frontend Integration

The frontend displays the embedded Excel dashboard via iframe or OneDrive embed:

```scala
div(
  cls := "analytics-dashboard",
  iframe(
    src := "https://onedrive.live.com/embed?...",
    width := "100%",
    height := "600px"
  )
)
```

Or use Microsoft Office Online embedding.

## Testing

### Test Ingestion

```bash
curl -X POST http://localhost:8080/analytics/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "network": "Ethereum",
    "walletAddress": "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
    "apiKey": "YOUR_API_KEY"
  }'
```

### Test CSV Export

```bash
curl http://localhost:8080/analytics/transactions.csv
```

### Test Stats

```bash
curl http://localhost:8080/analytics/stats
```

## Production Deployment

1. Set up PostgreSQL database
2. Run Flyway migrations
3. Configure API keys in environment variables
4. Set up scheduled ingestion (cron job or cloud scheduler)
5. Enable Excel Auto-Refresh on OneDrive/SharePoint
6. Monitor API rate limits

## Limitations & Future Enhancements

### Current Limitations
- Mock price data (not real historical prices)
- No automated scheduling for ingestion
- Single wallet address per network
- No token transfers (ERC-20, NFTs)

### Future Enhancements
1. **Price Service**: Integrate CoinGecko for real historical prices
2. **Scheduling**: Add Quartz scheduler for automatic ingestion
3. **Token Tracking**: Support ERC-20 token transfers
4. **NFT Analytics**: Track NFT transactions
5. **Multi-Wallet**: Support multiple wallet addresses
6. **Real-time**: WebSocket streaming for live updates
7. **Caching**: Redis caching for API responses
8. **Rate Limiting**: Smart rate limiting for blockchain APIs

## Troubleshooting

### "No transactions found"
- Check API key is valid
- Verify wallet address format (0x...)
- Check network is correct
- Verify start/end block range

### "Failed to parse response"
- Check API endpoint is accessible
- Verify API response format
- Check for API rate limiting

### "Excel won't load CSV"
- Check CSV endpoint is accessible from Excel machine
- Verify CORS headers if needed
- Check UTF-8 BOM is present

### "Prices are all 0"
- Price service is using mock data
- Implement CoinGecko integration for real prices

## Support

For questions or issues:
- Check logs: `tail -f logs/application.log`
- Enable debug logging: `ZIO.logLevel(LogLevel.Debug)`
- Review API documentation: Etherscan API Docs

---

**Built with:**
- Scala 3
- ZIO 2.0
- Quill (PostgreSQL)
- zio-http
- Flyway (migrations)
