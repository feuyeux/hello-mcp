package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.ClientOptions
import io.modelcontextprotocol.kotlin.sdk.client.transport.StdioClientTransport
import kotlinx.coroutines.runBlocking

/**
 * MCP 客户端应用
 * 使用 MCP SDK 的 SseClientTransport (SSE HTTP 传输)
 */
class McpClientApplication {
    
    private val logger = KotlinLogging.logger {}
    
    fun test() = runBlocking {
        logger.info { "=== 测试 MCP 客户端 ===" }
        
        // 创建客户端
        val client = Client(
            Implementation("hello-mcp-client", "1.0.0"),
            ClientOptions()
        )
        
        // 使用 SSE HTTP 传输层连接到服务器
        val transport = io.modelcontextprotocol.kotlin.sdk.client.transport.SseClientTransport("http://localhost:8062")
        client.connect(transport)
        
        try {
            // 初始化
            val initResult = client.initialize()
            logger.info { "服务器: ${initResult.serverInfo.name}" }
            
            // 列出工具
            logger.info { "\n=== 列出工具 ===" }
            val tools = client.listTools()
            tools.tools.forEach { tool ->
                logger.info { "  - ${tool.name}: ${tool.description}" }
            }
            
            // 测试查询元素
            logger.info { "\n=== 测试查询元素 ===" }
            var result = client.callTool("getElement", mapOf("name" to "氢"))
            logger.info { "氢元素: ${result.content.firstOrNull()}" }
            
            result = client.callTool("getElement", mapOf("name" to "Silicon"))
            logger.info { "硅元素: ${result.content.firstOrNull()}" }
            
            // 测试按位置查询
            logger.info { "\n=== 测试按位置查询 ===" }
            result = client.callTool("getElementByPosition", mapOf("position" to 6))
            logger.info { "第6号元素: ${result.content.firstOrNull()}" }
            
        } finally {
            client.close()
        }
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            McpClientApplication().test()
        }
    }
}
