# Excel Power Query Setup Guide

## Connect to Blockchain Analytics CSV Endpoint

This guide shows you how to connect Excel Power Query to your blockchain analytics API to automatically refresh transaction data.

### Prerequisites
- Microsoft Excel (2016 or later, or Microsoft 365)
- Backend server running on `http://localhost:8080`
- Some transaction data ingested (use the `/analytics/ingest` endpoint)

### Step-by-Step Instructions

#### 1. Open Excel and Access Power Query
1. Open Microsoft Excel
2. Go to the **Data** tab on the ribbon
3. Click **Get Data** → **From Other Sources** → **From Web**

#### 2. Configure the Web Connection
1. In the dialog that appears, select the **Advanced** option
2. Enter the URL: `http://localhost:8080/analytics/transactions.csv`
3. Click **OK**

#### 3. Load the Data
1. Power Query will connect to your API and preview the CSV data
2. You should see columns:
   - date
   - chain
   - tx_hash
   - from_address
   - to_address
   - amount_native
   - amount_usd
   - gas_used
   - gas_price_gwei
3. Click **Load** to import the data into Excel

#### 4. Refresh the Data
Once loaded, you can refresh the data anytime:
- Right-click on the table → **Refresh**
- Or use the **Refresh All** button on the Data tab
- Or set up automatic refresh (see below)

#### 5. Set Up Automatic Refresh (Optional)
1. Right-click on the connection in the **Queries & Connections** pane
2. Select **Properties**
3. Under **Refresh Control**, check:
   - ✅ Enable background refresh
   - ✅ Refresh data when opening the file
   - ✅ Refresh this connection on Refresh All
4. Optionally, set a refresh interval (e.g., every 60 minutes)

### Advanced: Power Query Transformations

Once the data is loaded, you can apply transformations:

#### Filter by Blockchain Network
```m
// In Power Query Editor, add this step:
= Table.SelectRows(#"Previous Step", each ([chain] = "Ethereum"))
```

#### Calculate Total Volume
```m
// Add a column for running total:
= Table.AddColumn(#"Previous Step", "Running Total", each List.Sum(Table.FirstN(#"Previous Step", [Index])[amount_usd]), type number)
```

#### Group by Chain
```m
// Summarize by blockchain:
= Table.Group(#"Previous Step", {"chain"}, {
    {"Total Transactions", each Table.RowCount(_), Int64.Type},
    {"Total Volume USD", each List.Sum([amount_usd]), type number}
})
```

### Creating Visualizations

With the data loaded, create charts:

1. **Volume Over Time**: Line chart with date on X-axis, amount_usd on Y-axis
2. **Transactions by Chain**: Pie chart showing distribution across blockchains
3. **Top Gas Users**: Bar chart of transactions sorted by gas_used
4. **Daily Volume**: Pivot table with date grouped by day

### Troubleshooting

**Error: "Unable to connect"**
- Ensure the backend server is running: `sbt backend/run`
- Check that you can access `http://localhost:8080/analytics/transactions.csv` in your browser

**Error: "No data to load"**
- The database might be empty
- Ingest some transactions first using the POST endpoint

**Data not refreshing**
- Check your internet/network connection
- Verify the server is still running
- Try manually refreshing: Data tab → Refresh All

### Production Deployment

When deploying to production:

1. **Update the URL**: Change from `localhost:8080` to your production domain:
   ```
   https://yourdomain.com/analytics/transactions.csv
   ```

2. **Add Authentication** (if needed):
   - In Power Query, click **Edit Credentials**
   - Select **Basic** or **API Key** authentication
   - Enter your credentials

3. **HTTPS**: Ensure your production server uses HTTPS for security

4. **CORS**: If Excel has issues connecting, verify your backend allows CORS for Excel's user agent

### API Endpoint Details

**Endpoint**: `GET /analytics/transactions.csv`

**Response Format**: CSV with UTF-8 BOM encoding

**Headers**:
- `Content-Type: text/csv`
- `Content-Disposition: attachment; filename=transactions.csv`
- `Cache-Control: no-cache`

**Rate Limiting**: None currently, but consider adding for production

### Next Steps

1. ✅ Ingest transaction data from blockchain networks
2. ✅ Connect Excel Power Query to the CSV endpoint
3. Create pivot tables and charts in Excel
4. Set up automatic refresh for real-time analytics
5. Share the Excel workbook with your team

---

**Need Help?**
- Check server logs for errors
- Test the endpoint in your browser: `http://localhost:8080/analytics/transactions.csv`
- Verify transactions exist in the database
