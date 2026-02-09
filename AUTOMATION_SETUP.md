# Daily Automation Setup Guide

This guide shows you how to set up automatic daily ingestion of blockchain transaction data.

## Overview

The automation system consists of:
1. **daily-ingest.ps1** - PowerShell script that fetches latest transactions
2. **Windows Task Scheduler** - Runs the script automatically every day
3. **Excel Auto-Refresh** - Updates your spreadsheet with new data

---

## Step 1: Configure Your Wallets

Edit `daily-ingest.ps1` and add your wallet addresses:

```powershell
$wallets = @(
    @{
        network = "Ethereum"
        address = "0xYourWalletAddress"
        apiKey = $env:ETHERSCAN_API_KEY
    },
    @{
        network = "Base"
        address = "0xAnotherWalletAddress"
        apiKey = $env:BASESCAN_API_KEY
    }
)
```

**Multiple Networks:**
- Each wallet can be on a different blockchain
- Use the appropriate API key for each network
- Supports: Ethereum, Base, Polygon, Arbitrum, Optimism

---

## Step 2: Test the Script Manually

Before scheduling, test that it works:

```powershell
# Make sure server is running first
cd c:\Users\Compl\Desktop\NewClient\newproject
.\daily-ingest.ps1
```

You should see output like:
```
=== Daily Blockchain Analytics Ingestion ===
✓ Server is running
Processing wallet: 0xd8dA6BF...
  ✓ Success!
    Fetched: 10 transactions
    Persisted: 8 transactions
```

---

## Step 3: Set Up Windows Task Scheduler

### Option A: Automatic Setup (Recommended)

Run this PowerShell command as **Administrator**:

```powershell
# Create scheduled task
$action = New-ScheduledTaskAction -Execute "PowerShell.exe" `
    -Argument "-ExecutionPolicy Bypass -File `"c:\Users\Compl\Desktop\NewClient\newproject\daily-ingest.ps1`""

$trigger = New-ScheduledTaskTrigger -Daily -At "2:00AM"

$settings = New-ScheduledTaskSettingsSet `
    -ExecutionTimeLimit (New-TimeSpan -Hours 1) `
    -RestartCount 3 `
    -RestartInterval (New-TimeSpan -Minutes 1)

Register-ScheduledTask `
    -TaskName "Blockchain Analytics Daily Ingestion" `
    -Action $action `
    -Trigger $trigger `
    -Settings $settings `
    -Description "Fetches latest blockchain transactions daily" `
    -RunLevel Highest
```

### Option B: Manual Setup via GUI

1. **Open Task Scheduler**
   - Press `Win + R`
   - Type `taskschd.msc` and press Enter

2. **Create New Task**
   - Click "Create Task" (not "Create Basic Task")
   - Name: `Blockchain Analytics Daily Ingestion`
   - Description: `Fetches latest blockchain transactions daily`
   - Check "Run with highest privileges"

3. **Triggers Tab**
   - Click "New"
   - Begin the task: `On a schedule`
   - Settings: `Daily`
   - Start: Choose time (e.g., 2:00 AM)
   - Click "OK"

4. **Actions Tab**
   - Click "New"
   - Action: `Start a program`
   - Program/script: `PowerShell.exe`
   - Arguments: `-ExecutionPolicy Bypass -File "c:\Users\Compl\Desktop\NewClient\newproject\daily-ingest.ps1"`
   - Start in: `c:\Users\Compl\Desktop\NewClient\newproject`
   - Click "OK"

5. **Conditions Tab**
   - Uncheck "Start the task only if the computer is on AC power"
   - Check "Wake the computer to run this task" (optional)

6. **Settings Tab**
   - Check "Allow task to be run on demand"
   - Check "Run task as soon as possible after a scheduled start is missed"
   - If the task fails, restart every: `1 minute`, Attempt to restart up to: `3 times`
   - Click "OK"

---

## Step 4: Configure Excel Auto-Refresh

Once you've connected Excel to the CSV endpoint, set up automatic refresh:

### In Excel:

1. **Open your Excel workbook** with the Power Query connection

2. **Access Connection Properties**
   - Right-click on the table → **Refresh**
   - Or: Data tab → Queries & Connections → Right-click your query → Properties

3. **Enable Auto-Refresh**
   - ✅ Enable background refresh
   - ✅ Refresh data when opening the file
   - ✅ Refresh every: `60 minutes` (or your preferred interval)
   - Click "OK"

4. **Save the Workbook**

Now Excel will automatically refresh with new data!

---

## Step 5: Verify It's Working

### Check Task Scheduler:
1. Open Task Scheduler (`taskschd.msc`)
2. Find "Blockchain Analytics Daily Ingestion"
3. Right-click → **Run** to test immediately
4. Check the "Last Run Result" column (should be 0x0 for success)

### Check Logs:
```powershell
# View today's log
Get-Content c:\Users\Compl\Desktop\NewClient\newproject\logs\ingestion-$(Get-Date -Format 'yyyy-MM-dd').log
```

### Check Database:
```powershell
docker exec postgres psql -U postgres -d modernscale -c "SELECT COUNT(*) FROM blockchain_transactions;"
```

---

## Advanced Configuration

### Adjust Block Range

By default, the script fetches the last 10,000 blocks (~2 days). To change:

```powershell
.\daily-ingest.ps1 -BlockRange 50000  # Fetch last ~7 days
```

Or edit the default in `daily-ingest.ps1`:
```powershell
param(
    [int]$BlockRange = 50000  # Change this
)
```

### Multiple Schedules

Create separate tasks for different times:
- **Morning**: 8:00 AM - Refresh before work starts
- **Afternoon**: 2:00 PM - Mid-day update
- **Evening**: 8:00 PM - End of day summary

### Email Notifications

Add email notifications when ingestion completes:

```powershell
# At the end of daily-ingest.ps1, add:
Send-MailMessage `
    -From "analytics@yourdomain.com" `
    -To "you@yourdomain.com" `
    -Subject "Daily Analytics: $totalSuccess transactions ingested" `
    -Body "Ingestion completed successfully" `
    -SmtpServer "smtp.gmail.com" `
    -Port 587 `
    -UseSsl `
    -Credential (Get-Credential)
```

### Slack/Discord Webhooks

Send notifications to Slack or Discord:

```powershell
# Slack webhook
$webhookUrl = "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
$body = @{
    text = "✅ Daily ingestion: $totalSuccess transactions added"
} | ConvertTo-Json

Invoke-RestMethod -Uri $webhookUrl -Method Post -Body $body -ContentType 'application/json'
```

---

## Troubleshooting

### Task Shows as "Running" but Never Completes
- The server might not be running
- Check task history: Task Scheduler → View → Show Task History
- Increase timeout in task settings

### PowerShell Scripts Are Blocked
Run as Administrator:
```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### API Rate Limiting
- Etherscan free tier: 5 calls/second, 100,000 calls/day
- Space out your wallet ingestions
- Use separate API keys for different chains

### Server Not Running at Scheduled Time
**Option 1:** Keep server running as a Windows service
```powershell
# Install as service (requires NSSM - Non-Sucking Service Manager)
nssm install BlockchainAnalytics "C:\Program Files\sbt\bin\sbt.bat" "backend/run"
```

**Option 2:** Start server in scheduled task
Modify the task to run a batch script that starts the server first:
```batch
@echo off
cd c:\Users\Compl\Desktop\NewClient\newproject
start /B sbt backend/run
timeout /t 30
powershell -ExecutionPolicy Bypass -File daily-ingest.ps1
```

---

## Monitoring Dashboard (Optional)

Create a simple monitoring dashboard to track ingestion:

```powershell
# monitor-ingestion.ps1
$logs = Get-ChildItem "logs\ingestion-*.log" | Sort-Object LastWriteTime -Descending | Select-Object -First 7

Write-Host "=== Last 7 Days Ingestion Summary ===" -ForegroundColor Cyan
foreach ($log in $logs) {
    $content = Get-Content $log.FullName
    Write-Host $content -ForegroundColor Gray
}

# Database stats
docker exec postgres psql -U postgres -d modernscale -c "
    SELECT
        DATE(date) as day,
        chain,
        COUNT(*) as transactions,
        SUM(amount_usd) as volume_usd
    FROM blockchain_transactions
    WHERE date >= CURRENT_DATE - INTERVAL '7 days'
    GROUP BY DATE(date), chain
    ORDER BY day DESC, chain;
"
```

---

## Next Steps

1. ✅ Test manual run: `.\daily-ingest.ps1`
2. ✅ Set up scheduled task
3. ✅ Configure Excel auto-refresh
4. ✅ Let it run for a few days
5. ✅ Build dashboard visualizations in Excel
6. ✅ Share with your team!

**Your blockchain analytics are now fully automated!** 🎉
