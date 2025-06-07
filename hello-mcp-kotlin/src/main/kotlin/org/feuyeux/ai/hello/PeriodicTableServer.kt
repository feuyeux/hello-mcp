package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.feuyeux.ai.hello.service.HelloMcpService

/**
 * PeriodicTableServer - 元素周期表MCP服务器
 * 
 * 实现了完整的MCP协议服务器，提供元素周期表查询工具
 */
class PeriodicTableServer {
    
    private val logger = KotlinLogging.logger {}
    private val mcpService = HelloMcpService()
    
    /**
     * 启动MCP服务器
     */
    suspend fun start(port: Int = 8062) {
        logger.info { "=== 启动元素周期表MCP服务器 ===" }
        logger.info { "服务器端口: $port" }
        
        val server = embeddedServer(CIO, port = port, host = "localhost") {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(SSE)
            
            routing {
                // 健康检查端点
                get("/health") {
                    call.respond(mapOf("status" to "healthy", "service" to "periodic-table-mcp"))
                }
                
                // MCP工具列表端点
                get("/tools") {
                    val tools = listOf(
                        mapOf(
                            "name" to "getElement",
                            "description" to "根据元素名称获取元素周期表元素信息",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(
                                    "name" to mapOf(
                                        "type" to "string",
                                        "description" to "元素的中文名称，如'氢'、'氦'等"
                                    )
                                ),
                                "required" to listOf("name")
                            )
                        ),
                        mapOf(
                            "name" to "getElementByPosition",
                            "description" to "获取元素周期表指定位置的元素信息",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(
                                    "position" to mapOf(
                                        "type" to "integer",
                                        "description" to "元素的原子序数，范围从1到118"
                                    )
                                ),
                                "required" to listOf("position")
                            )
                        ),
                        mapOf(
                            "name" to "getElementsByPeriod",
                            "description" to "获取指定周期的所有元素",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(
                                    "period" to mapOf(
                                        "type" to "integer",
                                        "description" to "周期数，范围从1到7"
                                    )
                                ),
                                "required" to listOf("period")
                            )
                        ),
                        mapOf(
                            "name" to "getElementsByGroup",
                            "description" to "获取指定族的所有元素",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(
                                    "group" to mapOf(
                                        "type" to "string",
                                        "description" to "族名，如'IA'、'VIIA'、'0族'等"
                                    )
                                ),
                                "required" to listOf("group")
                            )
                        ),
                        mapOf(
                            "name" to "searchElement",
                            "description" to "搜索元素（支持原子序数、符号、中文名、英文名）",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to mapOf(
                                    "query" to mapOf(
                                        "type" to "string",
                                        "description" to "搜索关键词"
                                    )
                                ),
                                "required" to listOf("query")
                            )
                        ),
                        mapOf(
                            "name" to "getPeriodicTableStats",
                            "description" to "获取元素周期表统计信息",
                            "parameters" to mapOf(
                                "type" to "object",
                                "properties" to emptyMap<String, Any>()
                            )
                        )
                    )
                    call.respond(mapOf("tools" to tools))
                }
                
                // 元素查询端点
                get("/element/{name}") {
                    val name = call.parameters["name"] ?: ""
                    val result = mcpService.getElement(name)
                    call.respond(mapOf("result" to result))
                }
                
                get("/element/position/{position}") {
                    val position = call.parameters["position"]?.toIntOrNull() ?: 0
                    val result = mcpService.getElementByPosition(position)
                    call.respond(mapOf("result" to result))
                }
                
                get("/period/{period}") {
                    val period = call.parameters["period"]?.toIntOrNull() ?: 0
                    val result = mcpService.getElementsByPeriod(period)
                    call.respond(mapOf("result" to result))
                }
                
                get("/group/{group}") {
                    val group = call.parameters["group"] ?: ""
                    val result = mcpService.getElementsByGroup(group)
                    call.respond(mapOf("result" to result))
                }
                
                get("/search/{query}") {
                    val query = call.parameters["query"] ?: ""
                    val result = mcpService.searchElement(query)
                    call.respond(mapOf("result" to result))
                }
                
                get("/stats") {
                    val result = mcpService.getPeriodicTableStats()
                    call.respond(mapOf("result" to result))
                }
                
                // SSE端点用于实时通信
                sse("/sse") {
                    send("data: MCP服务器已连接\n\n")
                    
                    // 发送可用工具列表
                    send("data: 可用工具: getElement, getElementByPosition, getElementsByPeriod, getElementsByGroup, searchElement, getPeriodicTableStats\n\n")
                    
                    // 保持连接
                    while (true) {
                        kotlinx.coroutines.delay(30_000)
                        send("data: 心跳\n\n")
                    }
                }
            }
        }
        
        logger.info { "MCP服务器启动成功，访问地址: http://localhost:$port" }
        logger.info { "可用端点:" }
        logger.info { "  健康检查: GET /health" }
        logger.info { "  工具列表: GET /tools" }
        logger.info { "  元素查询: GET /element/{name}" }
        logger.info { "  位置查询: GET /element/position/{position}" }
        logger.info { "  周期查询: GET /period/{period}" }
        logger.info { "  族查询: GET /group/{group}" }
        logger.info { "  元素搜索: GET /search/{query}" }
        logger.info { "  统计信息: GET /stats" }
        logger.info { "  SSE连接: GET /sse" }
        
        // 显示示例数据
        showSampleData()
        
        server.start(wait = true)
    }
    
    /**
     * 显示示例数据
     */
    private fun showSampleData() {
        logger.info { "=== 示例数据 ===" }
        
        // 显示氢元素信息
        val hydrogen = PeriodicTable.getElementByAtomicNumber(1)
        hydrogen?.let {
            logger.info { "氢元素: ${PeriodicTable.formatElement(it)}" }
        }
        
        // 显示碳元素信息
        val carbon = PeriodicTable.getElementByAtomicNumber(6)
        carbon?.let {
            logger.info { "碳元素: ${PeriodicTable.formatElement(it)}" }
        }
        
        // 显示统计信息
        logger.info { mcpService.getPeriodicTableStats() }
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val server = PeriodicTableServer()
            val port = args.getOrNull(0)?.toIntOrNull() ?: 8062
            server.start(port)
        }
    }
}

/**
 * PeriodicTableServerKt - 兼容性对象，用于主类运行
 */
object PeriodicTableServerKt {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val server = PeriodicTableServer()
        val port = args.getOrNull(0)?.toIntOrNull() ?: 8061
        server.start(port)
    }
}
