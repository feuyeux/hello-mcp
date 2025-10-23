import Foundation
import MCP
import ArgumentParser
import Logging

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
        
        // 配置日志
        var logger = Logger(label: "mcp.server")
        logger.logLevel = .debug
        
        // 创建 MCP 服务器
        let server = Server(
            name: "mcp-server",
            version: "0.1.0",
            capabilities: .init(
                tools: .init(listChanged: false)
            )
        )
        
        // 注册工具列表处理器
        await server.withMethodHandler(ListTools.self) { _ in
            print("处理 tools/list 请求")
            return ListTools.Result(
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
        await server.withMethodHandler(CallTool.self) { params in
            print("处理 tools/call 请求: \(params.name)")
            
            let name = params.name
            let arguments = params.arguments ?? [:]
            
            switch name {
            case "get_element":
                if let elementName = arguments["name"]?.stringValue,
                   let element = PeriodicTable.findElement(byName: elementName) {
                    let text = "元素名称: \(element.name) (\(element.pronunciation), \(element.englishName)), " +
                              "原子序数: \(element.atomicNumber), 符号: \(element.symbol), " +
                              "相对原子质量: \(String(format: "%.3f", element.atomicWeight)), 周期: \(element.period), " +
                              "族: \(element.group)"
                    return CallTool.Result(content: [.text(text)])
                }
                return CallTool.Result(content: [.text("元素不存在")])
                
            case "get_element_by_position":
                if let position = arguments["position"]?.intValue,
                   position >= 1 && position <= 118,
                   let element = PeriodicTable.findElement(byAtomicNumber: position) {
                    let text = "元素名称: \(element.name) (\(element.pronunciation), \(element.englishName)), " +
                              "原子序数: \(element.atomicNumber), 符号: \(element.symbol), " +
                              "相对原子质量: \(String(format: "%.3f", element.atomicWeight)), 周期: \(element.period), " +
                              "族: \(element.group)"
                    return CallTool.Result(content: [.text(text)])
                }
                return CallTool.Result(content: [.text("元素不存在")])
                
            default:
                return CallTool.Result(
                    content: [.text("未知工具: \(name)")],
                    isError: true
                )
            }
        }
        
        // 使用 HTTP 传输层（Streamable HTTP）
        print("使用 HTTP 传输层，端口: \(port)")
        let transport = HTTPServerTransport(port: port, logger: logger)
        
        try await server.start(transport: transport)
        
        print("MCP 服务器已启动")
        print("服务器地址: http://localhost:\(port)")
        print("MCP 端点: http://localhost:\(port)/mcp")
        print("健康检查: http://localhost:\(port)/health")
        
        // 保持服务器运行
        await server.waitUntilCompleted()
    }
}
