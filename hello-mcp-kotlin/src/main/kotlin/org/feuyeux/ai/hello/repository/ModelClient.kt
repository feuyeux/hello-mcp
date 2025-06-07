package org.feuyeux.ai.hello.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.feuyeux.ai.hello.utils.DotEnv

/**
 * AI模型客户端类
 * 提供与各种AI模型服务的连接
 */
class ModelClient {
    
    companion object {
        private val logger = KotlinLogging.logger {}
        
        /**
         * 构建智谱AI模型客户端
         */
        fun buildZhiPuAiModel(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
                
                engine {
                    requestTimeout = 30_000
                }
            }
        }
        
        /**
         * 构建文心模型客户端
         */
        fun buildWenxinModel(): HttpClient {
            return HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
                
                engine {
                    requestTimeout = 30_000
                }
            }
        }
        
        /**
         * 测试智谱AI连接
         */
        fun testZhiPuAiConnection(): Boolean = runBlocking {
            return@runBlocking try {
                val apiKey = DotEnv.getZhipuAiKey()
                val client = buildZhiPuAiModel()
                
                // 简单的连接测试（不实际调用API）
                logger.info { "智谱AI API密钥: ${if (apiKey.isNotEmpty()) "已配置" else "未配置"}" }
                
                client.close()
                apiKey.isNotEmpty()
            } catch (e: Exception) {
                logger.error(e) { "智谱AI连接测试失败" }
                false
            }
        }
        
        /**
         * 测试千帆模型连接
         */
        fun testQianfanConnection(): Boolean = runBlocking {
            return@runBlocking try {
                val apiKeys = DotEnv.getQianfanTokenKeys()
                val client = buildWenxinModel()
                
                logger.info { "千帆API密钥: ${if (apiKeys.isNotEmpty()) "已配置" else "未配置"}" }
                
                client.close()
                apiKeys.isNotEmpty() && apiKeys.all { it.isNotEmpty() }
            } catch (e: Exception) {
                logger.error(e) { "千帆模型连接测试失败" }
                false
            }
        }
        
        /**
         * 获取智谱AI密钥
         */
        fun getZhiPuAiKey(): String? {
            return try {
                DotEnv.getZhipuAiKey()
            } catch (e: Exception) {
                logger.warn { "未找到智谱AI API密钥，请设置ZHIPUAI_API_KEY环境变量" }
                null
            }
        }
        
        /**
         * 获取千帆密钥
         */
        fun getQianfanKeys(): Array<String>? {
            return try {
                DotEnv.getQianfanTokenKeys()
            } catch (e: Exception) {
                logger.warn { "未找到千帆API密钥，请设置QIANFAN_API_KEY和QIANFAN_API_SECRET_KEY环境变量" }
                null
            }
        }
    }
}

/**
 * AI聊天请求数据类
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

/**
 * AI聊天消息数据类
 */
@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * AI聊天响应数据类
 */
@Serializable
data class ChatResponse(
    val id: String? = null,
    val choices: List<ChatChoice>? = null,
    val usage: Usage? = null
)

/**
 * AI聊天选择数据类
 */
@Serializable
data class ChatChoice(
    val message: ChatMessage,
    val finish_reason: String? = null
)

/**
 * AI使用统计数据类
 */
@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
