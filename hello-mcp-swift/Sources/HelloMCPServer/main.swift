import Foundation
import HelloMCPCore
import ModelContextProtocol

@main
struct HelloMCPServer {
    static func main() async {
        print("Hello MCP Swift Server starting...")
        
        // 创建 MCP 服务器
        let server = MCPServer(
            name: "hello-mcp-swift",
            version: "1.0.0",
            capabilities: ServerCapabilities(tools: true)
        )
        
        // 注册工具列表处理器
        server.onListTools {
            print("处理 tools/list 请求")
            return [
                Tool(
                    name: "getElement",
                    description: "根据元素名称获取元素周期表元素信息",
                    inputSchema: [
                        "type": "object",
                        "properties": [
                            "name": [
                                "type": "string",
                                "description": "元素的中文名称"
                            ]
                        ],
                        "required": ["name"]
                    ]
                ),
                Tool(
                    name: "getElementByPosition",
                    description: "根据元素在周期表中的位置（原子序数）查询元素信息",
                    inputSchema: [
                        "type": "object",
                        "properties": [
                            "position": [
                                "type": "integer",
                                "description": "元素的原子序数，范围从1到118"
                            ]
                        ],
                        "required": ["position"]
                    ]
                )
            ]
        }
        
        // 注册工具调用处理器
        server.onCallTool { name, arguments in
            print("处理 tools/call 请求: \(name)")
            
            switch name {
            case "getElement":
                if let elementName = arguments["name"] as? String,
                   let element = findElement(byName: elementName) {
                    return [TextContent(text: "元素名称: \(element.name), 原子序数: \(element.atomicNumber), 符号: \(element.symbol)")]
                }
                return [TextContent(text: "元素不存在")]
                
            case "getElementByPosition":
                if let position = arguments["position"] as? Int,
                   let element = findElement(byAtomicNumber: position) {
                    return [TextContent(text: "元素名称: \(element.name), 原子序数: \(element.atomicNumber), 符号: \(element.symbol)")]
                }
                return [TextContent(text: "元素不存在")]
                
            default:
                return [TextContent(text: "未知工具: \(name)")]
            }
        }
        
        // 使用 SSE HTTP 传输层
        print("使用 SSE HTTP 传输层，端口: 8067")
        let transport = SseServerTransport(port: 8067)
        await server.connect(transport: transport)
    }
}
