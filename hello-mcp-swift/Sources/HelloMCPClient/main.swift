import Foundation
import HelloMCPCore
import ModelContextProtocol

@main
struct HelloMCPClient {
    static func main() async {
        print("=== Hello MCP Swift Client ===")
        
        // 创建客户端
        let client = MCPClient(
            name: "hello-mcp-client",
            version: "1.0.0"
        )
        
        do {
            // 使用 SSE HTTP 传输层连接
            print("连接到服务器: http://localhost:8067")
            let transport = SseClientTransport(url: "http://localhost:8067")
            try await client.connect(transport: transport)
            
            // 初始化
            let initResult = try await client.initialize()
            print("服务器: \(initResult.serverInfo.name)")
            
            // 列出工具
            print("\n=== 列出工具 ===")
            let tools = try await client.listTools()
            for tool in tools {
                print("  - \(tool.name): \(tool.description)")
            }
            
            // 测试查询元素
            print("\n=== 测试查询元素 ===")
            var result = try await client.callTool(name: "getElement", arguments: ["name": "氢"])
            print("氢元素: \(result)")
            
            result = try await client.callTool(name: "getElement", arguments: ["name": "硅"])
            print("硅元素: \(result)")
            
            // 测试按位置查询
            print("\n=== 测试按位置查询 ===")
            result = try await client.callTool(name: "getElementByPosition", arguments: ["position": 6])
            print("第6号元素: \(result)")
            
            try await client.close()
            
        } catch {
            print("错误: \(error)")
        }
    }
}
