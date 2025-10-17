# Hello MCP

This repository contains **Model Context Protocol (MCP)** implementations across multiple programming languages, demonstrating a periodic table query tool.

All implementations follow the **MCP Protocol Version 2025-06-18** specification and use **SSE (Server-Sent Events) HTTP transport** for communication.

## 📚 Resources

- [MCP Documentation](https://modelcontextprotocol.io/)
- [MCP Specification](https://spec.modelcontextprotocol.io/)
- [Protocol Schema 2025-06-18](https://github.com/modelcontextprotocol/modelcontextprotocol/blob/main/schema/2025-06-18/schema.ts)

## 🔧 Official SDKs

1. [TypeScript SDK](https://github.com/modelcontextprotocol/typescript-sdk)
2. [Python SDK](https://github.com/modelcontextprotocol/python-sdk)
3. [Java SDK](https://github.com/modelcontextprotocol/java-sdk)
4. [Kotlin SDK](https://github.com/modelcontextprotocol/kotlin-sdk)
5. [C# SDK](https://github.com/modelcontextprotocol/csharp-sdk)
6. [Rust SDK](https://github.com/modelcontextprotocol/rust-sdk)
7. [Swift SDK](https://github.com/modelcontextprotocol/swift-sdk)
8. [Go SDK](https://github.com/modelcontextprotocol/go-sdk)
9. [PHP SDK](https://github.com/modelcontextprotocol/php-sdk)

## Overview

Each implementation provides:

- MCP Server with periodic table query tools
- MCP Client for testing tool calls
- Two query methods:
  - `get_element`: Query element by symbol (e.g., "H", "He", "Li")
  - `get_element_by_position`: Query element by atomic number (1-118)

## Language Implementations

| Language   | SDK                                                    | Directory           | Port |
| :--------- | :----------------------------------------------------- | :------------------ | :--- |
| Java       | <https://github.com/modelcontextprotocol/java-sdk>       | `hello-mcp-java/`   | 8061 |
| Kotlin     | <https://github.com/modelcontextprotocol/kotlin-sdk>     | `hello-mcp-kotlin/` | 8062 |
| Python     | <https://github.com/modelcontextprotocol/python-sdk>     | `hello-mcp-python/` | 8063 |
| TypeScript | <https://github.com/modelcontextprotocol/typescript-sdk> | `hello-mcp-ts/`     | 8064 |
| Rust       | <https://github.com/modelcontextprotocol/rust-sdk>       | `hello-mcp-rust/`   | 8065 |
| C#         | <https://github.com/modelcontextprotocol/csharp-sdk>     | `hello-mcp-csharp/` | 8066 |
| Swift      | <https://github.com/modelcontextprotocol/swift-sdk>      | `hello-mcp-swift/`  | 8067 |

## API Examples

### Query by Symbol

```json
{
  "name": "get_element",
  "arguments": {
    "symbol": "H"
  }
}
```

### Query by Atomic Number

```json
{
  "name": "get_element_by_position",
  "arguments": {
    "position": 1
  }
}
```

### Sample Response

```json
{
  "symbol": "H",
  "name": "Hydrogen",
  "atomicNumber": 1,
  "atomicWeight": 1.008,
  "period": 1,
  "group": 1,
  "phase": "gas",
  "type": "nonmetal"
}
```
