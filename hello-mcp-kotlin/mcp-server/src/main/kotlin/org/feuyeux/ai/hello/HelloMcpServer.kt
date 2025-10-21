package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*
import org.feuyeux.ai.hello.protocol.*
import org.feuyeux.ai.hello.service.HelloMcpService
import java.io.PrintStream
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

fun main() {
    // 设置控制台输出编码为 UTF-8
    System.setOut(PrintStream(System.out, true, StandardCharsets.UTF_8))
    System.setErr(PrintStream(System.err, true, StandardCharsets.UTF_8))
    
    val mcpService = HelloMcpService()
    
    embeddedServer(Netty, port = 9900) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        routing {
            // MCP endpoint
            post("/hello-mcp") {
                try {
                    val request = call.receive<JsonRpcRequest>()
                    logger.info { "收到请求: ${request.method}" }
                    
                    val response = when (request.method) {
                        "initialize" -> handleInitialize(request)
                        "tools/list" -> handleListTools(request)
                        "tools/call" -> handleCallTool(request, mcpService)
                        else -> JsonRpcResponse(
                            id = request.id,
                            error = JsonRpcError(-32601, "Method not found")
                        )
                    }
                    
                    call.respond(response)
                } catch (e: Exception) {
                    logger.error(e) { "处理请求失败" }
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        JsonRpcResponse(
                            error = JsonRpcError(-32603, "Internal error: ${e.message}")
                        )
                    )
                }
            }
        }
    }.start(wait = true)
    
    logger.info { "MCP Server started on port 9900" }
}

private fun handleInitialize(request: JsonRpcRequest): JsonRpcResponse {
    val result = InitializeResult(
        protocolVersion = "2024-11-05",
        capabilities = ServerCapabilities(
            tools = ToolsCapability(listChanged = true),
            logging = JsonObject(emptyMap())
        ),
        serverInfo = Implementation(
            name = "hello-mcp-server",
            version = "1.0.0"
        )
    )
    
    return JsonRpcResponse(
        id = request.id,
        result = Json.encodeToJsonElement(result)
    )
}

private fun handleListTools(request: JsonRpcRequest): JsonRpcResponse {
    val tools = listOf(
        Tool(
            name = "getElement",
            description = "根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）",
            inputSchema = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("object"),
                    "properties" to JsonObject(
                        mapOf(
                            "name" to JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("string"),
                                    "description" to JsonPrimitive("元素名称")
                                )
                            )
                        )
                    ),
                    "required" to JsonArray(listOf(JsonPrimitive("name")))
                )
            )
        ),
        Tool(
            name = "getElementByPosition",
            description = "根据元素在周期表中的位置（原子序数）查询元素信息",
            inputSchema = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("object"),
                    "properties" to JsonObject(
                        mapOf(
                            "position" to JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("integer"),
                                    "description" to JsonPrimitive("元素的原子序数（1-118）")
                                )
                            )
                        )
                    ),
                    "required" to JsonArray(listOf(JsonPrimitive("position")))
                )
            )
        )
    )
    
    val result = ListToolsResult(tools)
    return JsonRpcResponse(
        id = request.id,
        result = Json.encodeToJsonElement(result)
    )
}

private fun handleCallTool(request: JsonRpcRequest, mcpService: HelloMcpService): JsonRpcResponse {
    val params = request.params ?: return JsonRpcResponse(
        id = request.id,
        error = JsonRpcError(-32602, "Invalid params")
    )
    
    val callRequest = Json.decodeFromJsonElement<CallToolRequest>(params)
    logger.info { "调用工具: ${callRequest.name}" }
    
    val resultText = when (callRequest.name) {
        "getElement" -> {
            val name = callRequest.arguments?.get("name")?.jsonPrimitive?.content ?: ""
            logger.info { "接收到参数 name: $name" }
            mcpService.getElement(name)
        }
        "getElementByPosition" -> {
            val position = callRequest.arguments?.get("position")?.jsonPrimitive?.int ?: 0
            logger.info { "接收到参数 position: $position" }
            mcpService.getElementByPosition(position)
        }
        else -> "未知工具: ${callRequest.name}"
    }
    
    val result = CallToolResult(
        content = listOf(Content(type = "text", text = resultText))
    )
    
    return JsonRpcResponse(
        id = request.id,
        result = Json.encodeToJsonElement(result)
    )
}
