#!/bin/bash

MODE=${1:-help}

case $MODE in
    server)
        echo "Starting Rust MCP Server..."
        cargo run -- server
        ;;
    client)
        echo "Starting Rust MCP Client..."
        cargo run -- client
        ;;
    *)
        echo "Usage: $0 [server|client]"
        echo "  server - Start MCP server"
        echo "  client - Run MCP client tests"
        ;;
esac
