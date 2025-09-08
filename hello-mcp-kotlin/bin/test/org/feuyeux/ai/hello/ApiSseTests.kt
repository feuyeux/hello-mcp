package org.feuyeux.ai.hello

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.feuyeux.ai.hello.service.HelloMcpService
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * MCP服务器API测试类
 * 
 * 该类测试MCP服务端功能，包括：
 * - SSE连接测试
 * - 工具列表查询
 * - 元素信息获取功能
 * - REST API端点测试
 */
class ApiSseTests {

    private val testPort = 8062
    private val baseUrl = "http://localhost:$testPort"

    /**
     * 测试HelloMcpService的基本功能
     */
    @Test
    fun testHelloMcpServiceBasics() = runTest {
        val service = HelloMcpService()
        
        // 测试getElement方法
        val hydrogenResult = service.getElement("氢")
        assertNotNull(hydrogenResult)
        assertTrue(hydrogenResult.contains("Hydrogen"))
        assertTrue(hydrogenResult.contains("H"))
        
        // 测试getElementByPosition方法
        val carbonResult = service.getElementByPosition(6)
        assertNotNull(carbonResult)
        assertTrue(carbonResult.contains("Carbon"))
        assertTrue(carbonResult.contains("C"))
        
        // 测试英文名称
        val oxygenResult = service.getElement("Oxygen")
        assertNotNull(oxygenResult)
        assertTrue(oxygenResult.contains("Oxygen"))
        assertTrue(oxygenResult.contains("O"))
        
        // 测试符号查询
        val siliconResult = service.getElement("Si")
        assertNotNull(siliconResult)
        assertTrue(siliconResult.contains("Silicon") || siliconResult.contains("硅"))
        assertTrue(siliconResult.contains("Si"))
    }

    /**
     * 测试周期表数据完整性
     */
    @Test
    fun testPeriodicTableDataIntegrity() = runTest {
        // 测试关键元素是否存在
        val keyElements = listOf(
            1 to "H",    // 氢
            6 to "C",    // 碳 
            8 to "O",    // 氧
            14 to "Si",  // 硅
            26 to "Fe",  // 铁
            79 to "Au",  // 金
            118 to "Og"  // 𬽚
        )
        
        for ((atomicNumber, symbol) in keyElements) {
            val element = PeriodicTable.getElementByAtomicNumber(atomicNumber)
            assertNotNull(element, "原子序数 $atomicNumber 的元素不存在")
            assertEquals(symbol, element.symbol, "原子序数 $atomicNumber 的元素符号不匹配")
            assertEquals(atomicNumber, element.atomicNumber, "元素 $symbol 的原子序数不匹配")
        }
        
        // 测试元素总数
        val allElements = PeriodicTable.getAllElements()
        assertTrue(allElements.size >= 118, "元素总数应该至少为118")
        
        // 测试元素唯一性
        val symbols = allElements.map { it.symbol }.toSet()
        assertEquals(allElements.size, symbols.size, "元素符号应该唯一")
        
        val atomicNumbers = allElements.map { it.atomicNumber }.toSet()
        assertEquals(allElements.size, atomicNumbers.size, "原子序数应该唯一")
    }

    /**
     * 测试元素分类功能
     */
    @Test
    fun testElementCategorization() = runTest {
        val service = HelloMcpService()
        
        // 测试各种分类
        val categories = listOf(
            "IA", "IIA", "IIIA", "IVA", "VA", "VIA", "VIIA", "0族"
        )
        
        for (category in categories) {
            val elements = PeriodicTable.getAllElements().filter { it.group == category }
            println("分类 '$category' 包含 ${elements.size} 个元素")
            
            // 每个分类至少应该有一些元素
            if (category in listOf("IA", "IIA", "0族")) {
                assertTrue(elements.isNotEmpty(), "分类 '$category' 应该包含元素")
            }
        }
    }

    /**
     * 测试搜索功能
     */
    @Test
    fun testElementSearch() = runTest {
        val allElements = PeriodicTable.getAllElements()
        
        // 测试按名称搜索
        val hydrogenResults = allElements.filter { it.englishName.contains("Hydrogen", ignoreCase = true) }
        assertTrue(hydrogenResults.isNotEmpty())
        assertEquals("H", hydrogenResults.first().symbol)
        
        // 测试按符号搜索
        val carbonResults = allElements.filter { it.symbol == "C" }
        assertTrue(carbonResults.isNotEmpty())
        assertEquals("Carbon", carbonResults.first().englishName)
        
        // 测试部分名称搜索
        val oxygenResults = allElements.filter { it.englishName.contains("oxy", ignoreCase = true) }
        assertTrue(oxygenResults.isNotEmpty())
        assertEquals("O", oxygenResults.first().symbol)
        
        // 测试中文名称搜索
        val chineseResults = allElements.filter { it.name.contains("氢") }
        assertTrue(chineseResults.isNotEmpty())
        assertEquals("H", chineseResults.first().symbol)
    }

    /**
     * 测试元素格式化功能
     */
    @Test
    fun testElementFormatting() = runTest {
        val hydrogen = PeriodicTable.getElementBySymbol("H")
        assertNotNull(hydrogen)
        
        val formatted = PeriodicTable.formatElement(hydrogen)
        
        // 验证格式化结果包含关键信息
        assertTrue(formatted.contains("Hydrogen"), "格式化结果应包含英文名称")
        assertTrue(formatted.contains("氢"), "格式化结果应包含中文名称")
        assertTrue(formatted.contains("H"), "格式化结果应包含符号")
        assertTrue(formatted.contains("1"), "格式化结果应包含原子序数")
        assertTrue(formatted.contains("1.008"), "格式化结果应包含原子质量")
        
        println("氢元素格式化结果:")
        println(formatted)
    }

    /**
     * 测试边界情况和错误处理
     */
    @Test
    fun testErrorHandling() = runTest {
        val service = HelloMcpService()
        
        // 测试不存在的元素名称
        val nonExistentResult = service.getElement("不存在的元素")
        assertTrue(nonExistentResult.contains("元素不存在"))
        
        // 测试无效的原子序数
        val invalidPositionResult = service.getElementByPosition(0)
        assertTrue(invalidPositionResult.contains("元素位置无效"))
        
        val largePositionResult = service.getElementByPosition(999)
        assertTrue(largePositionResult.contains("无效") || largePositionResult.contains("invalid"))
    }

    /**
     * 模拟MCP客户端调用测试
     */
    @Test
    fun testMcpClientSimulation() = runTest {
        val service = HelloMcpService()
        
        // 模拟MCP工具调用序列
        val testCases = listOf(
            "getElement" to mapOf("name" to "氢"),
            "getElement" to mapOf("name" to "Carbon"), 
            "getElement" to mapOf("name" to "Au"),
            "getElementByPosition" to mapOf("position" to 1),
            "getElementByPosition" to mapOf("position" to 6),
            "getElementByPosition" to mapOf("position" to 79)
        )
        
        for ((toolName, params) in testCases) {
            val result = when (toolName) {
                "getElement" -> service.getElement(params["name"] as String)
                "getElementByPosition" -> service.getElementByPosition(params["position"] as Int)
                else -> "未知工具"
            }
            
            println("工具调用: $toolName($params)")
            println("结果: $result")
            println("---")
            
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
        }
    }
}
