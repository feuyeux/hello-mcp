#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting Swift MCP Server..."
        swift run HelloMCPServer
        ;;
    client)
        echo "Starting Swift MCP Client..."
        swift run HelloMCPClient
        ;;
    *)
        echo "Usage: $0 [server|client]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        ;;
esac
