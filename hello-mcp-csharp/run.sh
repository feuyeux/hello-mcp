#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting C# MCP Server..."
        cd mcp-server
        dotnet run
        ;;
    client)
        echo "Starting C# MCP Client Tests..."
        cd mcp-client
        dotnet build TestClient.csproj -v q -nologo > /dev/null 2>&1
        dotnet bin/Debug/net9.0/TestClient.dll
        ;;
    ollama)
        echo "Starting C# MCP Ollama Integration Test..."
        cd mcp-client
        dotnet build TestOllama.csproj -v q -nologo > /dev/null 2>&1
        dotnet bin/Debug/net9.0/TestOllama.dll
        ;;
    *)
        echo "Usage: $0 [server|client|ollama]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        echo "  ollama - Run Ollama integration test"
        ;;
esac
