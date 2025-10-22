# Hello MCP Kotlin

<https://github.com/modelcontextprotocol/kotlin-sdk/tags>

Build

```sh
gradle build
```

Server

```sh
gradle :mcp-server:run
```

Client

```sh
gradle :mcp-client:test
gradle :mcp-client:test --tests HelloMcpTests.testHelloMcpByName
gradle :mcp-client:test --tests LlmMcpIntegrationTest.testLlmWithMcpTools
```
