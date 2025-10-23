param(
    [Parameter(Position=0)]
    [ValidateSet("server", "client", "ollama", "help")]
    [string]$Mode = "help",
    
    [Parameter()]
    [int]$Port = 9900,
    
    [Parameter()]
    [ValidateSet("Trace", "Debug", "Information", "Warning", "Error", "Critical")]
    [string]$LogLevel = "Information"
)

switch ($Mode) {
    "server" {
        Write-Host "Starting C# MCP Server..." -ForegroundColor Green
        Push-Location mcp-server
        try {
            dotnet run -- --port $Port --log-level $LogLevel
        } finally {
            Pop-Location
        }
    }
    "client" {
        Write-Host "Starting C# MCP Client Tests..." -ForegroundColor Green
        Push-Location mcp-client
        try {
            dotnet build TestClient.csproj -v q -nologo > $null 2>&1
            dotnet bin/Debug/net9.0/TestClient.dll --port $Port
        } finally {
            Pop-Location
        }
    }
    "ollama" {
        Write-Host "Starting C# MCP Ollama Integration Test..." -ForegroundColor Green
        Push-Location mcp-client
        try {
            dotnet build TestOllama.csproj -v q -nologo > $null 2>&1
            dotnet bin/Debug/net9.0/TestOllama.dll --port $Port
        } finally {
            Pop-Location
        }
    }
    default {
        Write-Host "Usage: .\run.ps1 [server|client|ollama] [-Port <port>] [-LogLevel <level>]" -ForegroundColor Yellow
        Write-Host "  server      - Start MCP server" -ForegroundColor Cyan
        Write-Host "  client      - Run MCP client tests" -ForegroundColor Cyan
        Write-Host "  ollama      - Run Ollama integration test" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Options:" -ForegroundColor Yellow
        Write-Host "  -Port       - Server port (default: 9900)" -ForegroundColor Cyan
        Write-Host "  -LogLevel   - Log level: Trace, Debug, Information, Warning, Error, Critical (default: Information)" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Examples:" -ForegroundColor Yellow
        Write-Host "  .\run.ps1 server" -ForegroundColor Gray
        Write-Host "  .\run.ps1 server -Port 8080 -LogLevel Debug" -ForegroundColor Gray
        Write-Host "  .\run.ps1 client -Port 8080" -ForegroundColor Gray
        Write-Host "  .\run.ps1 ollama" -ForegroundColor Gray
    }
}
