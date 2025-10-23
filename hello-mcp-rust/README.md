# Hello MCP Rust

https://github.com/modelcontextprotocol/rust-sdk/tags

```bash
cd mcp-server
cargo run --bin server
cargo run --bin server -- --port 9900
```

```bash
.\run-test.ps1 client 9900
.\run-test.ps1 ollama 9900
```

```bash
cd mcp-client
cargo run --bin test_client -- --port 9900
cargo run --bin test_ollama -- --port 9900
```
