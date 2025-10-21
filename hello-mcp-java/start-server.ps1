# PowerShell script to start server with UTF-8 encoding
# Usage: .\start-server.ps1 [port]
# Example: .\start-server.ps1 9900

# Set console encoding to UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null

# Set Java encoding options
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

$port = if ($args.Count -gt 0) { $args[0] } else { "9900" }
Write-Host "Starting server on port $port"

Set-Location mcp-server
mvn exec:java -D"exec.args=--port $port"
