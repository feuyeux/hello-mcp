import Foundation
import ArgumentParser
import Client
import ModelContextProtocol

@main
struct TestOllamaCommand: AsyncParsableCommand {
    static let configuration = CommandConfiguration(
        commandName: "test-ollama",
        abstract: "Ollama MCP Integration Test"
    )
    
    @Option(name: .long, help: "Port to connect to MCP server")
    var port: Int = 9900
    
    func run() async throws {
        print("=== 测试: LLM 通过工具调用查询元素 ===")
        
        let ollamaClient = OllamaClient()
        let helloClient = HelloClient(baseURL: "http://localhost:\(port)")
        
        do {
            // 获取可用工具
            let client = Client(
                info: Implementation(
                    name: "hello-mcp-client",
                    version: "0.1.0"
                )
            )
            
            let transport = StreamableHTTPClientTransport(
                url: URL(string: "http://localhost:\(port)/mcp")!
            )
            
            try await client.connect(transport: transport)
            let _ = try await client.initialize()
            let toolsResult = try await client.listTools()
            
            // 转换工具格式为 Ollama 格式
            var tools: [[String: AnyCodable]] = []
            for tool in toolsResult.tools {
                let toolDict: [String: AnyCodable] = [
                    "type": .string("function"),
                    "function": .dictionary([
                        "name": .string(tool.name),
                        "description": .string(tool.description),
                        "parameters": .dictionary(convertToAnyCodable(tool.inputSchema))
                    ])
                ]
                tools.append(toolDict)
            }
            
            try await client.close()
            
            // 构建消息
            var messages: [Message] = []
            let query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量"
            messages.append(Message(role: "user", content: query))
            
            // 第一次调用 LLM
            print("第一次调用 LLM: \(query)")
            let response = try await ollamaClient.chat(messages: messages, tools: tools)
            
            print("LLM 响应角色: \(response.role)")
            print("LLM 响应内容: \(response.content)")
            
            // 检查是否有工具调用
            if response.hasToolCalls {
                print("LLM 决定调用工具，工具数量: \(response.toolCalls.count)")
                
                // 执行工具调用
                for toolCall in response.toolCalls {
                    print("执行工具: \(toolCall.name)")
                    print("工具参数: \(toolCall.arguments)")
                    
                    let toolResult = try await ollamaClient.executeToolCall(
                        toolCall: toolCall,
                        helloClient: helloClient
                    )
                    print("工具执行结果: \(toolResult)")
                    
                    // 将工具结果添加到消息历史
                    messages.append(Message(role: "assistant", content: ""))
                    messages.append(Message(role: "tool", content: toolResult))
                }
                
                // 第二次调用 LLM，让其基于工具结果生成最终答案
                print("第二次调用 LLM，生成最终答案...")
                let finalResponse = try await ollamaClient.chat(messages: messages, tools: tools)
                
                print("最终答案: \(finalResponse.content)")
                print("\n最终答案: \(finalResponse.content)\n")
                print("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息")
            } else {
                print("⚠️ LLM 没有调用工具，直接返回了答案: \(response.content)")
                print("这可能是因为 LLM 已经知道答案，或者不支持工具调用")
            }
            
        } catch {
            print("❌ 测试失败: \(error)")
            print("提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载")
            print("启动命令: ollama serve")
            print("下载模型: ollama pull qwen2.5:latest")
        }
    }
    
    func convertToAnyCodable(_ dict: [String: Any]) -> [String: AnyCodable] {
        return dict.mapValues { AnyCodable($0) }
    }
}
