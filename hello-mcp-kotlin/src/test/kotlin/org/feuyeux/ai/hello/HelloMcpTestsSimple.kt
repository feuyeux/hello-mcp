package org.feuyeux.ai.hello

import kotlinx.coroutines.test.runTest
import org.feuyeux.ai.hello.service.HelloMcpService
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * 基础周期表测试类
 * 
 * 测试周期表基本功能，包括元素查询、格式化、分类等
 */
class HelloMcpTestsSimple {

    @Test
    fun testPeriodicTableBasicOperations() = runTest {
        // Test getting element by symbol
        val hydrogen = PeriodicTable.getElementBySymbol("H")
        assertNotNull(hydrogen)
        assertEquals("H", hydrogen.symbol)
        assertEquals(1, hydrogen.atomicNumber)
        
        // Test getting element by atomic number
        val carbon = PeriodicTable.getElementByAtomicNumber(6)
        assertNotNull(carbon)
        assertEquals("C", carbon.symbol)
        assertEquals(6, carbon.atomicNumber)
        
        // Test formatting element
        val formatted = PeriodicTable.formatElement(hydrogen)
        assertTrue(formatted.contains("Hydrogen"))
        assertTrue(formatted.contains("H"))
        
        // Test getting all elements
        val allElements = PeriodicTable.getAllElements()
        assertTrue(allElements.size >= 118)
    }

    @Test
    fun testPeriodicTableCategories() = runTest {
        val elements = PeriodicTable.getAllElements()
        val nobleGases = elements.filter { it.group == "0族" }
        assertTrue(nobleGases.isNotEmpty())
        
        val alkaliMetals = elements.filter { it.group == "IA" }
        assertTrue(alkaliMetals.isNotEmpty())
        
        val alkaliEarthMetals = elements.filter { it.group == "IIA" }
        assertTrue(alkaliEarthMetals.isNotEmpty())
    }

    @Test
    fun testPeriodicTableSearch() = runTest {
        val elements = PeriodicTable.getAllElements()
        
        // Test search by name
        val hydrogenResults = elements.filter { it.name.contains("氢") || it.englishName.contains("hydrogen", ignoreCase = true) }
        assertTrue(hydrogenResults.isNotEmpty())
        assertEquals("H", hydrogenResults.first().symbol)
        
        // Test search by symbol
        val carbonResults = elements.filter { it.symbol == "C" }
        assertTrue(carbonResults.isNotEmpty())
        assertEquals("Carbon", carbonResults.first().englishName)
        
        // Test search by partial name
        val oxygenResults = elements.filter { it.name.contains("氧") || it.englishName.contains("oxy", ignoreCase = true) }
        assertTrue(oxygenResults.isNotEmpty())
        assertEquals("O", oxygenResults.first().symbol)
    }

    @Test
    fun testHelloMcpService() = runTest {
        val service = HelloMcpService()
        
        // Test getElement method
        val hydrogenResult = service.getElement("氢")
        assertTrue(hydrogenResult.contains("Hydrogen"))
        assertTrue(hydrogenResult.contains("H"))
        
        // Test getElementByPosition method
        val carbonResult = service.getElementByPosition(6)
        assertTrue(carbonResult.contains("Carbon"))
        assertTrue(carbonResult.contains("C"))
        
        // Test with English names
        val oxygenResult = service.getElement("Oxygen")
        assertTrue(oxygenResult.contains("Oxygen"))
        assertTrue(oxygenResult.contains("O"))
    }
}
