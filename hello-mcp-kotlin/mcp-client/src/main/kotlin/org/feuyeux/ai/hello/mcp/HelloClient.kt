package org.feuyeux.ai.hello.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport
import io.modelcontextprotocol.spec.McpSchema
import java.time.Duration

private val logger = KotlinLogging.logger {}

/**
 * HelloClient类
 *
 * 此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。
 * 使用 HTTP 客户端连接到服务端。
 * 对标 Java 版本的 HelloClient.java
 */
object HelloClient {
    private const val BASE_URL = "http://localhost:9900"
    private val transport = HttpClientStreamableHttpTransport.builder(BASE_URL)
        .endpoint("hello-mcp")
        .build()
    
    fun listToolsResult(): McpSchema.ListToolsResult {
        McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build().use { client ->
            client.initialize()
            return client.listTools()
        }
    }
    
    fun listTools(): String {
        val result = listToolsResult()
        val toolsList = StringBuilder()
        for (tool in result.tools()) {
            toolsList
                .append("工具名称: ")
                .append(tool.name())
                .append(", 描述: ")
                .append(tool.description())
                .append("\n")
        }
        
        logger.debug { "列举工具成功: $toolsList" }
        return toolsList.toString()
    }
    
    fun getElement(name: String): String {
        logger.debug { "查询元素: $name" }
        McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build().use { client ->
            client.initialize()
            val result = client.callTool(
                McpSchema.CallToolRequest.builder()
                    .name("getElement")
                    .arguments(mapOf("name" to name))
                    .build()
            )
            
            logger.debug { "查询元素 $name 成功: ${result.content()}" }
            return result.content().toString()
        }
    }
    
    fun getElementByPosition(position: Int): String {
        logger.debug { "查询位置元素: $position" }
        McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build().use { client ->
            client.initialize()
            val result = client.callTool(
                McpSchema.CallToolRequest.builder()
                    .name("getElementByPosition")
                    .arguments(mapOf("position" to position))
                    .build()
            )
            
            logger.debug { "查询位置元素 $position 成功: ${result.content()}" }
            return result.content().toString()
        }
    }
}
