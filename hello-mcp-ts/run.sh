#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting TypeScript MCP Server..."
        npm run server
        ;;
    client)
        echo "Starting TypeScript MCP Client..."
        npm run client
        ;;
    *)
        echo "Usage: $0 [server|client]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        ;;
esac
