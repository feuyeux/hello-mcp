import Foundation
import ModelContextProtocol

#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

/// Hello MCP 客户端
/// 用于与 MCP 服务器交互，调用元素周期表相关的工具
public class HelloClient {
    private let baseURL: String
    private let endpoint: String
    
    public init(baseURL: String = "http://localhost:9900") {
        self.baseURL = baseURL
        self.endpoint = "\(baseURL)/mcp"
    }
    
    /// 列举所有可用工具
    public func listTools() async throws -> String {
        let client = Client(
            info: Implementation(
                name: "hello-mcp-client",
                version: "0.1.0"
            )
        )
        
        let transport = StreamableHTTPClientTransport(
            url: URL(string: endpoint)!
        )
        
        try await client.connect(transport: transport)
        let _ = try await client.initialize()
        
        let result = try await client.listTools()
        
        var toolsList: [String] = []
        for tool in result.tools {
            toolsList.append("工具名称: \(tool.name), 描述: \(tool.description)")
        }
        
        try await client.close()
        
        let toolsStr = toolsList.joined(separator: "\n")
        print("列举工具成功:\n\(toolsStr)")
        return toolsStr
    }
    
    /// 根据元素名称查询元素信息
    public func getElement(name: String) async throws -> String {
        print("查询元素: \(name)")
        
        let client = Client(
            info: Implementation(
                name: "hello-mcp-client",
                version: "0.1.0"
            )
        )
        
        let transport = StreamableHTTPClientTransport(
            url: URL(string: endpoint)!
        )
        
        try await client.connect(transport: transport)
        let _ = try await client.initialize()
        
        let result = try await client.callTool(
            name: "get_element",
            arguments: ["name": name]
        )
        
        var content = ""
        if let textContent = result.content.first, case .text(let text) = textContent {
            content = text.text
        }
        
        try await client.close()
        
        print("查询元素 \(name) 成功: \(content)")
        return content
    }
    
    /// 根据原子序数查询元素信息
    public func getElementByPosition(position: Int) async throws -> String {
        print("查询位置元素: \(position)")
        
        let client = Client(
            info: Implementation(
                name: "hello-mcp-client",
                version: "0.1.0"
            )
        )
        
        let transport = StreamableHTTPClientTransport(
            url: URL(string: endpoint)!
        )
        
        try await client.connect(transport: transport)
        let _ = try await client.initialize()
        
        let result = try await client.callTool(
            name: "get_element_by_position",
            arguments: ["position": position]
        )
        
        var content = ""
        if let textContent = result.content.first, case .text(let text) = textContent {
            content = text.text
        }
        
        try await client.close()
        
        print("查询位置元素 \(position) 成功: \(content)")
        return content
    }
}
