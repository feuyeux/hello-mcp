# Hello MCP - Kotlin

A Kotlin implementation of the Model Context Protocol (MCP) providing periodic table query tools with MCP server and client components using the official Kotlin MCP SDK.

## Run

```sh
touch .env
```

Add API keys to `.env` file:
- https://bigmodel.cn/usercenter/proj-mgmt/apikeys (ZhiPu AI)
- https://console.amap.com/dev/key/app (Amap Maps)

```env
ZHIPUAI_API_KEY=智谱大模型APIKEY
AMAP_MAPS_API_KEY=高德地图APIKEY
QIANFAN_API_KEY=百度千帆APIKEY
QIANFAN_API_SECRET_KEY=百度千帆SECRET
```

### Run MCP Server

```sh
# Start server on port 8062
gradle run --args="server"

# Or using shell script
./run.sh server
```

### Run MCP Client

```sh
# Test Hello MCP (Periodic Table)
gradle run --args="client"

# Test all functionality
gradle run --args="test"

# Run unit tests
gradle test
```

## Features

- **MCP Server & Client** - Official Kotlin MCP SDK implementation
- **Complete Periodic Table** - All 118 elements with Chinese/English names
- **Multiple Query Tools** - Element lookup by name, position, period, group
- **AI Model Integration** - ZhiPu AI, QianFan models support
- **File System & Map Tools** - Additional MCP client implementations
- **HTTP API & SSE** - REST endpoints and Server-Sent Events on port 8062

## MCP Tools

The implementation provides the following MCP tools:

### `getElement(name: String)`
Query element by name (supports Chinese, English names, and symbols)
```sh
# Examples: "氢", "Hydrogen", "H"
```

### `getElementByPosition(position: Int)`
Query element by atomic number (1-118)
```sh
# Example: position=1 returns hydrogen
```

### `getElementsByPeriod(period: Int)`
Get all elements in a specific period (1-7)
```sh
# Example: period=1 returns H, He
```

### `getElementsByGroup(group: String)`
Get all elements in a specific group/family
```sh
# Examples: "IA", "VIIA", "0族"
```

### `searchElement(query: String)`
Flexible element search
```sh
# Examples: "金属", "noble"
```

### `getElementStats()`
Get periodic table statistics
```sh
# Returns total elements, periods, groups, etc.
```

## Example Output

```json
{
  "content": [
    {
      "type": "text", 
      "text": "元素信息:\n名称: 氢\n符号: H\n原子序数: 1\n原子量: 1.008\n元素类型: 非金属"
    }
  ]
}
```

## HTTP API Endpoints

The server provides REST endpoints on `http://localhost:8062`:

```sh
GET /health                    # Health check
GET /tools                     # MCP tool definitions
GET /element/{name}            # Query by name/symbol
GET /element/position/{pos}    # Query by atomic number
GET /period/{period}           # Query by period
GET /group/{group}             # Query by group  
GET /search/{query}            # Search elements
GET /stats                     # Element statistics
GET /sse                       # Server-Sent Events
```

## Requirements

- **JDK 21** 
- **Gradle 8.14+**
- **Kotlin 2.1.20**

## Project Structure

```
hello-mcp-kotlin/
├── build.gradle.kts                          # Gradle build configuration  
├── run.sh                                    # Run script
├── .env                                      # Environment variables
├── README.md                                 # Documentation
└── src/
    ├── main/kotlin/org/feuyeux/ai/hello/
    │   ├── HelloMcpApplication.kt            # Main application
    │   ├── PeriodicTableServer.kt            # MCP server (port 8062)
    │   ├── PeriodicTableClient.kt            # Client implementation
    │   ├── PeriodicTable.kt                  # Periodic table data (118 elements)
    │   ├── service/HelloMcpService.kt        # MCP service layer
    │   ├── mcp/                              # MCP clients (Hello, FS, Map)
    │   ├── repository/ModelClient.kt         # AI model client
    │   └── utils/                            # Utilities (DotEnv, Dir, Npx)
    └── test/kotlin/org/feuyeux/ai/hello/     # Tests
```

## Architecture

### Server Components
- `PeriodicTableServer` - MCP server using official Kotlin SDK
- `HelloMcpService` - Service layer with tool implementations
- `PeriodicTable` - Comprehensive element data (118 elements)

### Client Components  
- `HelloClient` - Periodic table MCP client
- `FSClient` - File system MCP client
- `MapClient` - Map services MCP client
- `ModelClient` - AI model integration (ZhiPu, QianFan)

### Transport
- Uses official kotlin-sdk for MCP protocol
- Supports stdio transport for client-server communication
- HTTP endpoints for additional REST API access

## Dependencies

Uses official Kotlin MCP SDK and minimal dependencies:

- `io.modelcontextprotocol:kotlin-sdk:0.5.0` - Official Kotlin MCP SDK
- `org.jetbrains.kotlinx:kotlinx-coroutines-core` - Coroutines
- `org.jetbrains.kotlinx:kotlinx-serialization-json` - JSON Serialization  
- `io.github.oshai:kotlin-logging-jvm` - Logging
- `io.ktor:ktor-*` - HTTP server/client
- `org.slf4j:slf4j-nop` - SLF4J implementation

## Testing

```sh
# Run all tests
gradle test

# Test specific functionality
gradle run --args="client"  # Client tests
gradle run --args="test"    # Integration tests
```

Tests include:
- Element data validation and query functions
- MCP server-client communication  
- Tool functionality and error handling
- HTTP API endpoints and SSE

## Contributing

1. Ensure JDK 21 is installed
2. Use Gradle 8.14+
3. Follow Kotlin coding conventions
4. Add tests for new features
5. Update documentation as needed

## License

This project follows the same license as the official kotlin-sdk.
