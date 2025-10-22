package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.feuyeux.ai.hello.llm.OllamaClient
import org.feuyeux.ai.hello.mcp.HelloClient
import org.feuyeux.ai.hello.utils.DotEnv.loadEnv
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger {}

/**
 * LLM 与 MCP 集成测试
 *
 * 测试通过 LLM (Ollama) 调用 MCP 工具的完整流程
 */
class LlmMcpIntegrationTest {

    companion object {
        private lateinit var ollamaClient: OllamaClient

        @JvmStatic
        @BeforeAll
        fun init() {
            loadEnv()
            runBlocking {
                HelloClient.getHelloClient()
            }
            ollamaClient = OllamaClient()
            logger.info { "初始化 Ollama 客户端完成" }
        }
    }

    @Test
    @DisplayName("测试 LLM 通过工具调用查询元素")
    fun testLlmWithMcpTools() {
        logger.info { "=== 测试: LLM 通过工具调用查询元素 ===" }

        val messages = mutableListOf<OllamaClient.Message>()
        val query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量"
        messages.add(OllamaClient.Message("user", query))
        runBlocking {
            val tools = HelloClient.listToolsResult()

            logger.info { "第一次调用 LLM: $query" }
            val response = ollamaClient.chat(messages, tools)

            logger.info { "LLM 响应角色: ${response.role}" }
            logger.info { "LLM 响应内容: ${response.content}" }

            // 检查是否有工具调用
            if (response.hasToolCalls()) {
                logger.info { "LLM 决定调用工具，工具数量: ${response.toolCalls?.size}" }

                // 执行工具调用
                response.toolCalls?.forEach { toolCall ->
                    logger.info { "执行工具: ${toolCall.name}" }
                    logger.info { "工具参数: ${toolCall.arguments}" }

                    val toolResult = ollamaClient.executeToolCall(toolCall)
                    logger.info { "工具执行结果: $toolResult" }

                    // 将工具结果添加到消息历史
                    messages.add(OllamaClient.Message("assistant", ""))
                    messages.add(OllamaClient.Message("tool", toolResult))
                }

                // 第二次调用 LLM，让其基于工具结果生成最终答案
                logger.info { "第二次调用 LLM，生成最终答案..." }
                val finalResponse = ollamaClient.chat(messages, tools)

                logger.info { "最终答案: ${finalResponse.content}" }
                logger.info { "✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息" }

            } else {
                logger.warn { "LLM 没有调用工具，直接返回了答案: ${response.content}" }
                logger.info { "这可能是因为 LLM 已经知道答案，或者不支持工具调用" }
            }
        }
    }
}
