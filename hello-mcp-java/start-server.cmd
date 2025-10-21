@echo off
chcp 65001 >nul
cd mcp-server
mvn exec:java -Dexec.args="--port 9900"
