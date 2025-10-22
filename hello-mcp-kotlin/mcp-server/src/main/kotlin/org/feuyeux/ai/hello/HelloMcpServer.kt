package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.util.collections.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.SseServerTransport
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.feuyeux.ai.hello.service.HelloMcpService

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = runBlocking {
    val port = args.getOrNull(1)?.toIntOrNull() ?: 3001
    runSseMcpServerWithPlainConfiguration(port)
}

suspend fun runSseMcpServerWithPlainConfiguration(port: Int) {
    val servers = ConcurrentMap<String, Server>()
    println("Starting sse server on port $port. ")
    println("Use inspector to connect to the http://localhost:$port/sse")

    embeddedServer(Netty, host = "0.0.0.0", port = port) {
        install(SSE)
        routing {
            sse("/sse") {
                val transport = SseServerTransport("/message", this)
                val server = configureServer()
                servers[transport.sessionId] = server
                server.onClose {
                    println("Server closed")
                    servers.remove(transport.sessionId)
                }
                server.connect(transport)
            }
            post("/message") {
                println("Received Message")
                val sessionId: String = call.request.queryParameters["sessionId"]!!
                val transport = servers[sessionId]?.transport as? SseServerTransport
                if (transport == null) {
                    call.respond(HttpStatusCode.NotFound, "Session not found")
                    return@post
                }

                transport.handlePostMessage(call)
            }
        }
    }.startSuspend(wait = true)
}

fun configureServer(): Server {
    val mcpService = HelloMcpService()

    val server = Server(
        Implementation(
            name = "mcp kotlin server",
            version = "0.1.0",
        ),
        ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = true),
                logging = JsonObject(emptyMap())
            ),
        ),
    )

    server.addTool(
        name = "getElement",
        description = "根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put(
                    "name",
                    buildJsonObject {
                        put("type", JsonPrimitive("string"))
                        put("description", JsonPrimitive("元素名称"))
                    },
                )
            },
            required = listOf("name"),
        ),
    ) { request ->
        val name = (request.arguments["name"] as? JsonPrimitive)?.content ?: ""
        logger.info { "接收到参数 name: $name" }
        CallToolResult(
            content = listOf(TextContent(mcpService.getElement(name))),
            structuredContent = buildJsonObject {
                put("content", JsonPrimitive(mcpService.getElement(name)))
            },
        )
    }

    server.addTool(
        name = "getElementByPosition",
        description = "根据元素在周期表中的位置（原子序数）查询元素信息",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put(
                    "position",
                    buildJsonObject {
                        put("type", JsonPrimitive("integer"))
                        put("description", JsonPrimitive("元素的原子序数(1-118)"))
                    },
                )
            },
            required = listOf("position"),
        ),
    ) { request ->
        val position: Int = request.arguments["position"]?.jsonPrimitive?.int ?: -1
        logger.info { "接收到参数 position: $position" }
        CallToolResult(
            content = listOf(TextContent(mcpService.getElementByPosition(position))),
            structuredContent = buildJsonObject {
                put("content", JsonPrimitive(mcpService.getElementByPosition(position)))
            },
        )
    }
    return server
}
