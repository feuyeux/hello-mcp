#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting Python MCP Server..."
        python src/server.py
        ;;
    client)
        echo "Starting Python MCP Client..."
        python src/client.py
        ;;
    *)
        echo "Usage: $0 [server|client]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        ;;
esac
