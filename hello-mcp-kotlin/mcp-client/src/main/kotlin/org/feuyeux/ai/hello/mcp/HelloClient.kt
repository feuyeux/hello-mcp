package org.feuyeux.ai.hello.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.sse.*
import io.modelcontextprotocol.kotlin.sdk.CallToolResultBase
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ListToolsResult
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.SseClientTransport

private val logger = KotlinLogging.logger {}

/**
 * HelloClient类
 *
 * 此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。
 * 使用 HTTP 客户端连接到服务端。
 * 对标 Java 版本的 HelloClient.java
 */
object HelloClient {
    private const val BASE_URL = "http://localhost:3001/sse"
    lateinit var client: Client

    suspend fun getHelloClient(): Client {
        val transport = SseClientTransport(
            HttpClient() {
                install(SSE)
            },
            BASE_URL,
        )
        client = Client(
            Implementation("test", "1.0"),
        )
        client.connect(transport)
        return client
    }

    suspend fun listToolsResult(): ListToolsResult {
        return client.listTools()
    }

    suspend fun listTools(): String {
        val result = client.listTools()
        val toolsList = StringBuilder()
        for (tool in result.tools) {
            toolsList
                .append("工具名称: ")
                .append(tool.name)
                .append(", 描述: ")
                .append(tool.description)
                .append("\n")
        }

        logger.debug { "列举工具成功: $toolsList" }
        return toolsList.toString()
    }

    suspend fun getElement(name: String): String {
        logger.debug { "查询元素: $name" }
        val result = client.callTool("getElement", mapOf("name" to name)) as CallToolResultBase
        logger.debug { "查询元素 $name 成功: ${result.content}" }
        return result.content.toString()
    }

    suspend fun getElementByPosition(position: Int): String {
        logger.debug { "查询位置元素: $position" }
        val result =
            HelloClient.client.callTool("getElementByPosition", mapOf("position" to position)) as CallToolResultBase
        logger.debug { "查询元素 $position 成功: ${result.content}" }
        return result.content.toString()
    }
}
