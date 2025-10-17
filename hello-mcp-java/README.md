# Hello MCP Java

Server

```sh
mvn spring-boot:run -pl mcp-server
```

Client

```sh
mvn test -pl mcp-client
mvn test -Dtest=HelloMcpTests#testHelloMcpCall -pl mcp-client
```