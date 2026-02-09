# Daily Blockchain Transaction Ingestion Script
# This script fetches the latest transactions and can be scheduled to run daily

param(
    [string]$ServerUrl = "http://localhost:8080"
)

Write-Host "=== Daily Blockchain Analytics Ingestion ===" -ForegroundColor Cyan
Write-Host "Started at: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
Write-Host ""

# Check if server is running
try {
    Invoke-RestMethod -Uri "$ServerUrl/api/health" -Method Get -TimeoutSec 5 | Out-Null
    Write-Host "Server is running" -ForegroundColor Green
} catch {
    Write-Host "Error: Server is not running at $ServerUrl" -ForegroundColor Red
    Write-Host "Please start the server first: sbt backend/run" -ForegroundColor Yellow
    exit 1
}

# Get API key from environment
$apiKey = $env:ETHERSCAN_API_KEY
if (-not $apiKey) {
    Write-Host "Warning: ETHERSCAN_API_KEY not set in environment" -ForegroundColor Yellow
    Write-Host "Reading from .env file..." -ForegroundColor Yellow
    if (Test-Path ".env") {
        $envContent = Get-Content ".env"
        foreach ($line in $envContent) {
            if ($line -match "^ETHERSCAN_API_KEY=(.+)$") {
                $apiKey = $matches[1]
                break
            }
        }
    }
}

# Configuration - Add your wallet addresses here
$wallets = @(
    @{
        network = "Ethereum"
        address = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045"
        apiKey = $apiKey
        # Fetch last 10,000 blocks (~2 days)
        startBlock = $null
        endBlock = $null
    }
)

# To add more wallets, uncomment and modify:
# $wallets += @{
#     network = "Base"
#     address = "0xYourWalletAddress"
#     apiKey = $env:BASESCAN_API_KEY
#     startBlock = $null
#     endBlock = $null
# }

$totalSuccess = 0
$totalFailed = 0

# Process each wallet
foreach ($wallet in $wallets) {
    Write-Host "Processing: $($wallet.address)" -ForegroundColor Cyan
    Write-Host "  Network: $($wallet.network)" -ForegroundColor Gray

    # Build request
    $requestBody = @{
        network = $wallet.network
        walletAddress = $wallet.address
        apiKey = $wallet.apiKey
    }

    if ($wallet.startBlock) {
        $requestBody.startBlock = $wallet.startBlock
    }

    if ($wallet.endBlock) {
        $requestBody.endBlock = $wallet.endBlock
    }

    $json = $requestBody | ConvertTo-Json

    # Make ingestion request
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/analytics/ingest" -Method Post -Body $json -ContentType 'application/json'

        Write-Host "  Success!" -ForegroundColor Green
        Write-Host "    Fetched: $($response.totalFetched)" -ForegroundColor Gray
        Write-Host "    Normalized: $($response.totalNormalized)" -ForegroundColor Gray
        Write-Host "    Persisted: $($response.totalPersisted)" -ForegroundColor Gray

        $totalSuccess += $response.totalPersisted

        if ($response.failures -and $response.failures.Count -gt 0) {
            Write-Host "    Failures: $($response.failures.Count)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "  Failed!" -ForegroundColor Red
        Write-Host "    Error: $_" -ForegroundColor Red
        $totalFailed++
    }

    Write-Host ""
}

# Summary
Write-Host "=== Summary ===" -ForegroundColor Cyan
Write-Host "Transactions persisted: $totalSuccess" -ForegroundColor Green
if ($totalFailed -gt 0) {
    Write-Host "Wallets failed: $totalFailed" -ForegroundColor Red
}
Write-Host "Completed: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray

# Log to file
$logDir = Join-Path $PSScriptRoot "logs"
if (-not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

$logFile = Join-Path $logDir "ingestion-$(Get-Date -Format 'yyyy-MM-dd').log"
$logEntry = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] Ingested $totalSuccess transactions from $($wallets.Count) wallets"
Add-Content -Path $logFile -Value $logEntry

Write-Host ""
Write-Host "Log: $logFile" -ForegroundColor Gray

exit 0
