# Hello MCP Java

https://github.com/modelcontextprotocol/java-sdk/tags

## Server

```sh
mvn compile -pl mcp-server
```

```sh
mvn exec:java -pl mcp-server

# PowerShell - 注意引号）
mvn exec:java -pl mcp-server "-Dexec.args=--port 9909"

# CMD
mvn exec:java -pl mcp-server -Dexec.args="--port 9909"

# 启动脚本（推荐）
.\start-server.ps1 9909
```

## Client

```sh
# 使用默认端口 9900
mvn test -pl mcp-client

# 使用自定义端口连接服务器（PowerShell）
mvn test -pl mcp-client "-Dmcp.server.port=9909"

# 使用自定义端口连接服务器（CMD）
mvn test -pl mcp-client -Dmcp.server.port=9909

# 运行特定测试
mvn test "-Dtest=HelloMcpTests#testHelloMcpByName" -pl mcp-client

# 运行特定测试并指定端口
mvn test "-Dtest=HelloMcpTests#testHelloMcpByName" "-Dmcp.server.port=9909" -pl mcp-client
```
