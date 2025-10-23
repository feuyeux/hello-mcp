@echo off
setlocal

set MODE=%1
if "%MODE%"=="" set MODE=help

if /i "%MODE%"=="server" (
    echo Starting C# MCP Server...
    cd mcp-server
    dotnet run
) else if /i "%MODE%"=="client" (
    echo Starting C# MCP Client Tests...
    cd mcp-client
    dotnet build TestClient.csproj -v q -nologo >nul 2>&1
    dotnet bin\Debug\net9.0\TestClient.dll
) else if /i "%MODE%"=="ollama" (
    echo Starting C# MCP Ollama Integration Test...
    cd mcp-client
    dotnet build TestOllama.csproj -v q -nologo >nul 2>&1
    dotnet bin\Debug\net9.0\TestOllama.dll
) else (
    echo Usage: %0 [server^|client^|ollama]
    echo   server - Start MCP server
    echo   client - Run MCP client tests
    echo   ollama - Run Ollama integration test
)

endlocal
