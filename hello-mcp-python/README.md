# Hello MCP Python

<https://github.com/modelcontextprotocol/python-sdk/tags>

```sh
cd mcp-server
# Using default port
uv run mcp-server
# Using custom port
uv run mcp-server --port 9900
# Custom logging level
uv run mcp-server --log-level DEBUG
# Enable JSON responses instead of SSE streams
uv run mcp-servers --json-response
```

```sh
cd mcp-client
# 运行基础测试
uv run test-client --port 9900
# 运行Ollama集成测试
uv run test-ollama --port 9900
```
