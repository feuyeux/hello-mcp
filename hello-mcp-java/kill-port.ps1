param([int]$Port)

$connections = netstat -ano | Select-String ":$Port"
if ($connections -match "LISTENING\s+(\d+)") {
    $pid = $matches[1]
    Write-Host "Found process with PID: $pid"
    Stop-Process -Id $pid -Force
    Write-Host "Successfully killed process $pid on port $Port"
} else {
    Write-Host "No process found using port $Port"
}
