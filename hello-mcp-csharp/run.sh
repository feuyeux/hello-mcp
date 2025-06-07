#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting C# MCP Server..."
        dotnet run server
        ;;
    client)
        echo "Starting C# MCP Client..."
        dotnet run client
        ;;
    *)
        echo "Usage: $0 [server|client]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        ;;
esac
