package org.feuyeux.ai.hello.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

/**
 * HelloClient类
 * 
 * 此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。
 * 使用HTTP SSE方式实现MCP通信。
 */
class HelloClient {
    
    companion object {
        private val logger = KotlinLogging.logger {}
        
        /**
         * 构建HelloMCP客户端
         * 
         * 此方法创建一个HTTP客户端，连接到本地运行的服务器。
         * 设置了JSON序列化和内容协商。
         * 
         * @return HttpClient 配置好的HTTP客户端实例
         */
        fun buildHelloClient(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
                
                // 设置超时
                engine {
                    requestTimeout = 10_000
                }
            }
        }
        
        /**
         * 测试客户端连接
         */
        fun testConnection(): Boolean = runBlocking {
            return@runBlocking try {
                val client = buildHelloClient()
                logger.info { "MCP HelloClient 连接测试成功" }
                client.close()
                true
            } catch (e: Exception) {
                logger.error(e) { "MCP HelloClient 连接测试失败" }
                false
            }
        }
    }
}
