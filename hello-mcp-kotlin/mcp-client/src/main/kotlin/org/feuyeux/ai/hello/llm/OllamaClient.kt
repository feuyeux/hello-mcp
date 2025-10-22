package org.feuyeux.ai.hello.llm

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.kotlin.sdk.ListToolsResult
import kotlinx.coroutines.runBlocking
import org.feuyeux.ai.hello.mcp.HelloClient
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val logger = KotlinLogging.logger {}

/**
 * Ollama 客户端
 *
 * 用于与 Ollama API 交互，支持工具调用
 */
class OllamaClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val model: String = DEFAULT_MODEL
) {
    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val objectMapper: ObjectMapper = ObjectMapper()

    companion object {
        private const val DEFAULT_BASE_URL = "http://localhost:11434"
        private const val DEFAULT_MODEL = "qwen2.5:latest"
    }

    /**
     * 发送聊天请求
     *
     * @param messages 消息列表
     * @param tools 可用工具列表
     * @return 响应消息
     */
    fun chat(messages: List<Message>, tools: ListToolsResult): ChatResponse {
        try {
            logger.debug { "发送聊天请求到 Ollama: model=$model, messages=${messages.size}" }

            val requestBody = objectMapper.createObjectNode()
            requestBody.put("model", model)
            requestBody.put("stream", false)

            // 添加消息
            val messagesArray = requestBody.putArray("messages")
            for (msg in messages) {
                val msgNode = messagesArray.addObject()
                msgNode.put("role", msg.role)
                msgNode.put("content", msg.content)
            }

            // 添加工具
            val toolList = tools.tools
            val toolsArray = requestBody.putArray("tools")
            for (tool in toolList) {
                val toolNode = toolsArray.addObject()
                toolNode.put("type", "function")
                val functionNode = toolNode.putObject("function")
                functionNode.put("name", tool.name)
                functionNode.put("description", tool.description)
                
                // 直接使用 inputSchema，让 Jackson 自动序列化
                // 但需要手动构建正确的格式
                val parametersNode = functionNode.putObject("parameters")
                parametersNode.put("type", "object")
                
                // 构建 properties
                val propertiesNode = parametersNode.putObject("properties")
                tool.inputSchema.properties?.forEach { (propName, propValue) ->
                    val propNode = propertiesNode.putObject(propName)
                    
                    // 将 JsonElement 转换为 Map 以便访问
                    val propJson = propValue.toString()
                    val propMap = objectMapper.readValue(propJson, Map::class.java) as Map<String, Any>
                    
                    // 提取 type 和 description
                    propMap.forEach { (key, value) ->
                        when (key) {
                            "type" -> {
                                // type 可能是 JsonPrimitive，需要提取其内容
                                val typeValue = if (value is Map<*, *>) {
                                    (value as Map<String, Any>)["content"] as? String ?: value.toString()
                                } else {
                                    value.toString()
                                }
                                propNode.put("type", typeValue)
                            }
                            "description" -> {
                                // description 也可能是 JsonPrimitive
                                val descValue = if (value is Map<*, *>) {
                                    (value as Map<String, Any>)["content"] as? String ?: value.toString()
                                } else {
                                    value.toString()
                                }
                                propNode.put("description", descValue)
                            }
                            else -> {
                                // 其他属性直接添加
                                when (value) {
                                    is String -> propNode.put(key, value)
                                    is Number -> propNode.put(key, value.toInt())
                                    is Boolean -> propNode.put(key, value)
                                }
                            }
                        }
                    }
                }
                
                // 添加 required 字段
                tool.inputSchema.required?.let { required ->
                    val requiredArray = parametersNode.putArray("required")
                    required.forEach { requiredArray.add(it) }
                }
            }

            val requestJson = objectMapper.writeValueAsString(requestBody)
            logger.debug { "请求 JSON: $requestJson" }

            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseUrl/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            logger.debug { "响应状态: ${response.statusCode()}" }
            logger.debug { "响应内容: ${response.body()}" }

            if (response.statusCode() != 200) {
                throw RuntimeException("Ollama API 请求失败: ${response.statusCode()}")
            }

            val responseJson = objectMapper.readTree(response.body())
            val messageNode = responseJson.get("message")

            val chatResponse = ChatResponse(
                role = messageNode.get("role").asText(),
                content = if (messageNode.has("content")) messageNode.get("content").asText() else ""
            )

            // 解析工具调用
            if (messageNode.has("tool_calls")) {
                val toolCalls = mutableListOf<ToolCall>()
                val toolCallsNode = messageNode.get("tool_calls")
                for (toolCallNode in toolCallsNode) {
                    val functionNode = toolCallNode.get("function")
                    val toolCall = ToolCall(
                        name = functionNode.get("name").asText(),
                        arguments = objectMapper.convertValue(
                            functionNode.get("arguments"),
                            Map::class.java
                        ) as Map<String, Any>
                    )
                    toolCalls.add(toolCall)
                }
                chatResponse.toolCalls = toolCalls
            }

            return chatResponse

        } catch (e: Exception) {
            logger.error(e) { "Ollama 请求失败" }
            throw RuntimeException("Ollama 请求失败", e)
        }
    }

    fun executeToolCall(toolCall: ToolCall): String {
        try {
            logger.info { "执行工具调用: ${toolCall.name}, 参数: ${toolCall.arguments}" }
            val result: String
            runBlocking {
                result = when (toolCall.name) {
                    "getElement" -> {
                        // Ollama 可能返回 "name" 或 "elementName"
                        val name = (toolCall.arguments["name"] ?: toolCall.arguments["elementName"]) as? String
                            ?: throw IllegalArgumentException("缺少参数 name 或 elementName")
                        HelloClient.getElement(name)
                    }

                    "getElementByPosition" -> {
                        val position = when (val positionObj = toolCall.arguments["position"]) {
                            is Int -> positionObj
                            is Double -> positionObj.toInt()
                            else -> positionObj.toString().toInt()
                        }
                        HelloClient.getElementByPosition(position)
                    }

                    else -> "{\"error\": \"未知工具: ${toolCall.name}\"}"
                }

                logger.info { "工具调用结果: $result" }
            }
            return result
        } catch (e: Exception) {
            logger.error(e) { "工具调用失败" }
            return "{\"error\": \"${e.message}\"}"
        }
    }

    /** 消息类 */
    data class Message(
        val role: String,
        val content: String
    )

    /** 工具调用类 */
    data class ToolCall(
        val name: String,
        val arguments: Map<String, Any>
    )

    /** 聊天响应类 */
    data class ChatResponse(
        val role: String,
        val content: String,
        var toolCalls: List<ToolCall>? = null
    ) {
        fun hasToolCalls(): Boolean = !toolCalls.isNullOrEmpty()
    }
}
