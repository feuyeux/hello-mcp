# Hello MCP Swift(Doing)

<https://github.com/modelcontextprotocol/swift-sdk/tags>


```bash
$ swift --version
swift-driver version: 1.127.14.1 Apple Swift version 6.2 (swiftlang-6.2.0.19.9 clang-1700.3.19.1)
Target: arm64-apple-macosx26.0
```

```bash
swift package resolve
swift build
```

```bash
cd mcp-server
swift run mcp-server
swift run mcp-server --port 9900
swift run mcp-server --log-level DEBUG
swift run mcp-server --json-response
```

```bash
cd mcp-client
swift run test-client --port 9900
swift run test-ollama --port 9900
```
