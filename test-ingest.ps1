$json = @'
{
  "network": "Ethereum",
  "walletAddress": "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045",
  "apiKey": "BWAQQ37NVQTAC5YX5Z6U4XH1I3PNFWHQ4K",
  "startBlock": 19000000,
  "endBlock": 19010000
}
'@

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/analytics/ingest' -Method Post -Body $json -ContentType 'application/json'
    Write-Host "Success!"
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error: $_"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody"
    }
}
