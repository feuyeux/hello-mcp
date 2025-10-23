import Foundation
import ArgumentParser
import Client

@main
struct TestClientCommand: AsyncParsableCommand {
    static let configuration = CommandConfiguration(
        commandName: "test-client",
        abstract: "MCP Client Tests"
    )
    
    @Option(name: .long, help: "Port to connect to MCP server")
    var port: Int = 9900
    
    func run() async throws {
        print("=== 测试1: 列举Hello MCP工具 ===")
        await testListTools(port: port)
        
        print("\n=== 测试2: 测试Hello MCP - 按名称查询 ===")
        await testGetElementByName(port: port)
        
        print("\n=== 测试3: 测试MCP工具调用 - 按位置查询 ===")
        await testGetElementByPosition(port: port)
    }
    
    func testListTools(port: Int) async {
        do {
            let client = HelloClient(baseURL: "http://localhost:\(port)")
            let tools = try await client.listTools()
            print("\n列举到的工具:\n\(tools)\n")
        } catch {
            print("错误: \(error)")
        }
    }
    
    func testGetElementByName(port: Int) async {
        do {
            let client = HelloClient(baseURL: "http://localhost:\(port)")
            let result = try await client.getElement(name: "氢")
            print("查询氢元素结果: \(result)\n")
        } catch {
            print("错误: \(error)")
        }
    }
    
    func testGetElementByPosition(port: Int) async {
        do {
            let client = HelloClient(baseURL: "http://localhost:\(port)")
            let result = try await client.getElementByPosition(position: 6)
            print("查询原子序数为6的元素结果: \(result)\n")
        } catch {
            print("错误: \(error)")
        }
    }
}
