package org.feuyeux.ai.hello

import io.github.oshai.kotlinlogging.KotlinLogging
import org.feuyeux.ai.hello.mcp.HelloClient
import org.feuyeux.ai.hello.utils.DotEnv.loadEnv
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger {}

/**
 * MCP测试套件
 *
 * 包含所有MCP相关测试功能：
 * - MCP工具调试和列举
 * - 直接MCP工具调用测试
 * - Hello MCP服务器测试
 */
class HelloMcpTests {

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            loadEnv()
        }
    }

    @Test
    @DisplayName("列举Hello MCP工具")
    fun testListTools() {
        val tools = HelloClient.listTools()
        logger.info { "列举到的工具: \n$tools" }
    }

    @Test
    @DisplayName("测试Hello MCP - 按名称查询")
    fun testHelloMcpByName() {
        val result = HelloClient.getElement("氢")
        logger.info { "查询氢元素结果: $result" }
    }

    @Test
    @DisplayName("测试MCP工具调用 - 按位置查询")
    fun testMcpToolByPosition() {
        val result = HelloClient.getElementByPosition(6)
        logger.info { "查询原子序数为6的元素结果: $result" }
    }
}
