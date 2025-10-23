import Foundation
import ModelContextProtocol
import ArgumentParser

@main
struct MCPServerCommand: AsyncParsableCommand {
    static let configuration = CommandConfiguration(
        commandName: "mcp-server",
        abstract: "MCP Server - 元素周期表查询服务"
    )
    
    @Option(name: .long, help: "服务器监听端口")
    var port: Int = 9900
    
    @Option(name: .long, help: "日志级别 (DEBUG, INFO, WARNING, ERROR)")
    var logLevel: String = "INFO"
    
    @Flag(name: .long, help: "启用 JSON 响应模式而不是 SSE 流")
    var jsonResponse: Bool = false
    
    func run() async throws {
        print("MCP Server starting...")
        print("端口: \(port)")
        print("日志级别: \(logLevel)")
        print("响应模式: \(jsonResponse ? "JSON" : "SSE Stream")")
        
        // 创建 MCP 服务器
        let server = Server(
            info: Implementation(
                name: "mcp-server",
                version: "0.1.0"
            ),
            capabilities: ServerCapabilities(
                tools: .init(listChanged: false)
            )
        )
        
        // 注册工具列表处理器
        server.addHandler { (_: ListToolsRequest) -> ListToolsResult in
            print("处理 tools/list 请求")
            return ListToolsResult(
                tools: [
                    Tool(
                        name: "get_element",
                        description: "根据元素名称获取元素周期表元素信息",
                        inputSchema: [
                            "type": "object",
                            "properties": [
                                "name": [
                                    "type": "string",
                                    "description": "元素的中文名称，如'氢'、'氦'等"
                                ]
                            ],
                            "required": ["name"]
                        ]
                    ),
                    Tool(
                        name: "get_element_by_position",
                        description: "根据元素在周期表中的位置（原子序数）查询元素信息",
                        inputSchema: [
                            "type": "object",
                            "properties": [
                                "position": [
                                    "type": "number",
                                    "description": "元素的原子序数，范围从1到118"
                                ]
                            ],
                            "required": ["position"]
                        ]
                    )
                ]
            )
        }
        
        // 注册工具调用处理器
        server.addHandler { (request: CallToolRequest) -> CallToolResult in
            print("处理 tools/call 请求: \(request.params.name)")
            
            let name = request.params.name
            let arguments = request.params.arguments ?? [:]
            
            switch name {
            case "get_element":
                if let elementName = arguments["name"] as? String,
                   let element = PeriodicTable.findElement(byName: elementName) {
                    let text = "元素名称: \(element.name) (\(element.pronunciation), \(element.englishName)), " +
                              "原子序数: \(element.atomicNumber), 符号: \(element.symbol), " +
                              "相对原子质量: \(String(format: "%.3f", element.atomicWeight)), 周期: \(element.period), " +
                              "族: \(element.group)"
                    return CallToolResult(content: [.text(TextContent(text: text))])
                }
                return CallToolResult(content: [.text(TextContent(text: "元素不存在"))])
                
            case "get_element_by_position":
                if let position = arguments["position"] as? Int,
                   position >= 1 && position <= 118,
                   let element = PeriodicTable.findElement(byAtomicNumber: position) {
                    let text = "元素名称: \(element.name) (\(element.pronunciation), \(element.englishName)), " +
                              "原子序数: \(element.atomicNumber), 符号: \(element.symbol), " +
                              "相对原子质量: \(String(format: "%.3f", element.atomicWeight)), 周期: \(element.period), " +
                              "族: \(element.group)"
                    return CallToolResult(content: [.text(TextContent(text: text))])
                }
                return CallToolResult(content: [.text(TextContent(text: "元素不存在"))])
                
            default:
                return CallToolResult(
                    content: [.text(TextContent(text: "未知工具: \(name)"))],
                    isError: true
                )
            }
        }
        
        // 使用 Streamable HTTP 传输层
        print("使用 Streamable HTTP 传输层，端口: \(port)")
        let transport = StreamableHTTPServerTransport(
            host: "127.0.0.1",
            port: port,
            path: "/mcp",
            useSSE: !jsonResponse
        )
        
        try await server.connect(transport: transport)
        
        // 保持服务器运行
        try await Task.sleep(for: .seconds(.max))
    }
}
