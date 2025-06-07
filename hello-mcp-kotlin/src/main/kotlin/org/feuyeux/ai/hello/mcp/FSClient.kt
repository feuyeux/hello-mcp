package org.feuyeux.ai.hello.mcp

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.feuyeux.ai.hello.utils.DirUtils
import org.feuyeux.ai.hello.utils.NpxUtils

/**
 * FSClient类
 * 
 * 此类负责创建与文件系统MCP服务器的连接。
 * 用于文件系统操作相关的工具调用。
 */
class FSClient {
    
    companion object {
        private val logger = KotlinLogging.logger {}
        
        /**
         * 构建文件系统MCP客户端
         * 
         * 此方法创建一个HTTP客户端，用于连接文件系统服务。
         * 
         * @return HttpClient 配置好的HTTP客户端实例
         */
        fun buildFSClient(): HttpClient {
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
         * 获取用户目录
         */
        fun getUserDirectory(): String {
            return DirUtils.getUserDir()
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
         * 测试文件系统客户端连接
         */
        fun testConnection(): Boolean = runBlocking {
            return@runBlocking try {
                val client = buildFSClient()
                val userDir = getUserDirectory()
                val npxAvailable = checkNpxAvailability()
                
                logger.info { "用户目录: $userDir" }
                logger.info { "NPX可用性: ${if (npxAvailable) "可用" else "不可用"}" }
                
                client.close()
                npxAvailable
            } catch (e: Exception) {
                logger.error(e) { "文件系统客户端连接测试失败" }
                false
            }
        }
    }
}
