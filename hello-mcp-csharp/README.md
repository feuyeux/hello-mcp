# Hello MCP - C# Implementation

This is a C# implementation of the Model Context Protocol (MCP) with a periodic table query tool.

## Features

- MCP Server with periodic table query tools
- MCP Client for testing tool calls
- Two query methods:
  - `get_element`: Query element by symbol (e.g., "H", "He", "Li")
  - `get_element_by_position`: Query element by atomic number (1-118)

## Project Structure

```
hello-mcp-csharp/
├── HelloMCP.csproj       # Project configuration
├── README.md            # This file
├── Program.cs           # Main entry point
├── PeriodicTable.cs     # Element data and queries
├── McpServer.cs         # MCP server implementation
└── McpClient.cs         # MCP client for testing
```

## Setup

1. Make sure you have .NET 8.0 SDK installed
2. Restore dependencies:
   ```bash
   dotnet restore
   ```

## Usage

### Run MCP Server
```bash
dotnet run server
```

### Run MCP Client (Test)
```bash
dotnet run client
```

### Build
```bash
dotnet build
```

## API

### get_element
Query element by chemical symbol.

**Parameters:**
- `symbol` (string): Chemical symbol (e.g., "H", "He", "Li")

**Example:**
```json
{
  "name": "get_element",
  "arguments": {
    "symbol": "H"
  }
}
```

### get_element_by_position
Query element by atomic number.

**Parameters:**
- `position` (number): Atomic number (1-118)

**Example:**
```json
{
  "name": "get_element_by_position", 
  "arguments": {
    "position": 1
  }
}
```

## Sample Response

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

## Dependencies

- .NET 8.0
- Microsoft.Extensions.Hosting
- Microsoft.Extensions.Logging
- Newtonsoft.Json
- System.Text.Json

## Notes

This implementation provides a foundation for MCP protocol in C#. The periodic table data includes the first 20 elements and can be extended as needed.

The implementation uses Microsoft's dependency injection and logging frameworks for a production-ready structure.
