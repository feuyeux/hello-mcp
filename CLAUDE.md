# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-language reference implementation of the **Model Context Protocol (MCP)** demonstrating a periodic table query tool. Each language implementation follows MCP Protocol Version 2025-06-18 and uses **Streamable HTTP transport**.

The repository contains implementations in: Python, TypeScript, Java, Go, Rust, Kotlin, C#, Swift, and PHP.

## Architecture

Each language implementation follows a consistent structure:
- `mcp-server/`: Server component exposing MCP tools (periodic table queries)
- `mcp-client/`: Client component that connects to and tests the server

All implementations expose similar functionality - a tool to query chemical elements by name or atomic number.

## Streamable HTTP Transport

All implementations use Streamable HTTP transport:
- Python: `StreamableHTTPSessionManager`
- TypeScript: `StreamableHTTPServerTransport`
- Java: `HttpServletStreamableServerTransportProvider`
- Go: `NewStreamableHTTPHandler`
- C#: `WithHttpTransport()`
- Rust: Custom JSON-RPC over HTTP
- Kotlin: SSE transport (SDK does not support Streamable HTTP yet)
- Swift: Custom HTTP transport

## Common Commands

### Python
```bash
cd hello-mcp-python/mcp-server
uv run mcp-server                    # Default port
uv run mcp-server --port 9900        # Custom port

cd hello-mcp-python/mcp-client
uv run test-client --port 9900       # Run tests
uv run test-ollama --port 9900        # Ollama integration
```

### TypeScript
```bash
cd hello-mcp-ts/mcp-server && npm install
cd hello-mcp-ts/mcp-client && npm install

cd hello-mcp-ts/mcp-server
PORT=3000 npm start

cd hello-mcp-ts/mcp-client
npm run test:client -- --port 3000
npm run test:ollama -- --port 3000
```

### Java
```bash
cd hello-mcp-java
mvn compile -pl mcp-server
mvn exec:java -pl mcp-server              # Default port
mvn exec:java -pl mcp-server "-Dexec.args=--port 9909"
./start-server.ps1 9909                   # PowerShell wrapper

mvn test -pl mcp-client                   # Run tests
mvn test -pl mcp-client "-Dmcp.server.port=9909"
mvn test "-Dtest=HelloMcpTests#testHelloMcpByName" -pl mcp-client  # Single test
```

### Kotlin
```bash
cd hello-mcp-kotlin
gradle build
gradle :mcp-server:run                     # Start server
gradle :mcp-client:test                    # Run tests
gradle :mcp-client:test --tests HelloMcpTests.testHelloMcpByName
```

### Go
```bash
cd hello-mcp-go/mcp-server
go run .                                   # Default port
go run . -port 9900                        # Custom port

cd hello-mcp-go/mcp-client
go run . test                               # Run tests
go run . ollama                             # Ollama integration
```

### Rust
```bash
cd hello-mcp-rust/mcp-server
cargo run --bin server                     # Default port
cargo run --bin server -- --port 9900      # Custom port

cd hello-mcp-rust/mcp-client
cargo run --bin test_client -- --port 9900
cargo run --bin test_ollama -- --port 9900
```

### C#
```bash
# Linux/macOS
./run.sh server
./run.sh client
./run.sh ollama

# Windows PowerShell
.\run.ps1 server
.\run.ps1 client
.\run.ps1 ollama

cd hello-mcp-csharp/mcp-server
dotnet run
dotnet run -- --port 9900

cd hello-mcp-csharp/mcp-client
dotnet run --project TestClient.csproj
dotnet run --project TestClient.csproj -- --port 9900
dotnet run --project TestOllama.csproj
dotnet run --project TestOllama.csproj -- --port 9900
```

### Swift
```bash
cd hello-mcp-swift
./run-test.ps1 client 9900
./run-test.ps1 ollama 9900
```

## MCP Inspector

To test servers interactively:
```bash
npx @modelcontextprotocol/inspector
```

## Key Files

- `README.md`: Main project documentation
- `doc/hello-mcp-java-flow.md`: Architecture flow documentation
- `doc/hello-mcp-kotlin-flow.md`: Architecture flow documentation

## Latest Dependency Versions

| Language | MCP SDK Version |
|----------|----------------|
| Python | 1.26.0 |
| TypeScript | 1.24.3+ |
| Java | 1.0.0 |
| Kotlin | 0.9.0 |
| Go | 1.0.0 |
| Rust | 0.16.0 |
| C# | 1.0.0 |
| Swift | main branch |

## Development Notes

- Each language implementation is independent and uses the official MCP SDK for that language
- Tests require a running server - start the server first, then run client tests
- Default server port varies by implementation (9900, 3000, 9909, etc.)
- Some implementations support Ollama integration for LLM testing
- Kotlin uses SSE transport as Streamable HTTP is not yet available in the Kotlin SDK
