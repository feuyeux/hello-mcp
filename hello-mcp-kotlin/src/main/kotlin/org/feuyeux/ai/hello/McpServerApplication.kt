package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.transport.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.shared.*
import kotlinx.coroutines.runBlocking
import org.feuyeux.ai.hello.service.HelloMcpService

/**
 * MCP 服务器应用
 * 使用 MCP SDK 的 SseServerTransport (SSE HTTP 传输)
 */
class McpServerApplication {
    
    private val logger = KotlinLogging.logger {}
    private val mcpService = HelloMcpService()
    
    fun start() = runBlocking {
        logger.info { "启动 MCP 服务器..." }
        
        // 创建 MCP 服务器
        val server = Server(
            Implementation("hello-mcp-kotlin", "1.0.0"),
            ServerOptions(
                ServerCapabilities(
                    prompts = null,
                    resources = null,
                    tools = ServerCapabilities.Tools(listChanged = true),
                    logging = null
                )
            )
        )
        
        // 注册工具列表处理器
        server.setListToolsHandler {
            logger.info { "处理 tools/list 请求" }
            listOf(
                Tool(
                    name = "getElement",
                    description = "根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）",
                    inputSchema = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "name" to mapOf(
                                "type" to "string",
                                "description" to "元素的中文名称、英文名或符号"
                            )
                        ),
                        "required" to listOf("name")
                    )
                ),
                Tool(
                    name = "getElementByPosition",
                    description = "根据元素在周期表中的位置（原子序数）查询元素信息",
                    inputSchema = mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                            "position" to mapOf(
                                "type" to "integer",
                                "description" to "元素的原子序数，范围从1到118"
                            )
                        ),
                        "required" to listOf("position")
                    )
                )
            )
        }
        
        // 注册工具调用处理器
        server.setCallToolHandler { request ->
            val toolName = request.params.name
            val arguments = request.params.arguments
            
            logger.info { "处理 tools/call 请求: $toolName" }
            
            try {
                val result = when (toolName) {
                    "getElement" -> {
                        val name = arguments["name"] as? String ?: ""
                        mcpService.getElement(name)
                    }
                    "getElementByPosition" -> {
                        val position = (arguments["position"] as? Number)?.toInt() ?: 0
                        mcpService.getElementByPosition(position)
                    }
                    else -> "未知工具: $toolName"
                }
                
                CallToolResult(
                    content = listOf(TextContent(text = result)),
                    isError = false
                )
            } catch (e: Exception) {
                logger.error(e) { "工具调用失败" }
                CallToolResult(
                    content = listOf(TextContent(text = "错误: ${e.message}")),
                    isError = true
                )
            }
        }
        
        // 使用 SSE HTTP 传输层
        logger.info { "使用 SSE HTTP 传输层，端口: 8062" }
        val transport = io.modelcontextprotocol.kotlin.sdk.server.transport.SseServerTransport(8062)
        server.connect(transport)
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            McpServerApplication().start()
        }
    }
}
