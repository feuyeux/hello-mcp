package org.feuyeux.ai.hello.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.feuyeux.ai.hello.utils.DotEnv
import org.feuyeux.ai.hello.utils.NpxUtils

/**
 * MapClient类
 * 
 * 此类负责创建与地图MCP服务器的连接。
 * 支持高德地图API集成。
 */
class MapClient {
    
    companion object {
        private val logger = KotlinLogging.logger {}
        
        /**
         * 构建地图MCP客户端
         * 
         * 此方法创建一个HTTP客户端，用于连接地图服务。
         * 需要配置AMAP_MAPS_API_KEY环境变量。
         * 
         * @return HttpClient 配置好的HTTP客户端实例
         */
        fun buildMapClient(): HttpClient {
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
         * 获取地图API密钥
         */
        fun getMapApiKey(): String {
            return try {
                DotEnv.getLbsGaode()
            } catch (e: Exception) {
                logger.warn { "未找到高德地图API密钥，请设置AMAP_MAPS_API_KEY环境变量" }
                ""
            }
        }
        
        /**
         * 检查NPX可用性
         */
        fun checkNpxAvailability(): Boolean {
            val isAvailable = NpxUtils.isNpxInstalled()
            if (!isAvailable) {
                logger.warn { "NPX未安装，无法使用外部MCP服务器" }
            }
            return isAvailable
        }
        
        /**
         * 测试地图客户端连接
         */
        fun testConnection(): Boolean = runBlocking {
            return@runBlocking try {
                val client = buildMapClient()
                val apiKey = getMapApiKey()
                val npxAvailable = checkNpxAvailability()
                
                logger.info { "地图API密钥: ${if (apiKey.isNotEmpty()) "已配置" else "未配置"}" }
                logger.info { "NPX可用性: ${if (npxAvailable) "可用" else "不可用"}" }
                
                client.close()
                apiKey.isNotEmpty() && npxAvailable
            } catch (e: Exception) {
                logger.error(e) { "地图客户端连接测试失败" }
                false
            }
        }
    }
}
