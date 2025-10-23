# PowerShell script to run tests with proper UTF-8 encoding
# Set console encoding to UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
$env:RUST_LOG = "info"

$Test = if ($args.Count -gt 0) { $args[0] } else { "client" }
$Port = if ($args.Count -gt 1) { $args[1] } else { 9900 }

Write-Host "Running MCP Test..." -ForegroundColor Green
Write-Host "Test Type: $Test" -ForegroundColor Cyan
Write-Host "Port: $Port" -ForegroundColor Cyan
Write-Host ""

Set-Location mcp-client

if ($Test -eq "client") {
    cargo run --bin test_client -- --port $Port
} elseif ($Test -eq "ollama") {
    cargo run --bin test_ollama -- --port $Port
} else {
    Write-Host "Unknown test type: $Test" -ForegroundColor Red
    Write-Host "Available options: client, ollama" -ForegroundColor Yellow
    exit 1
}
