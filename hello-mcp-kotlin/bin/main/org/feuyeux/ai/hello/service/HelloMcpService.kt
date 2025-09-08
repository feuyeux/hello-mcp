package org.feuyeux.ai.hello.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import org.feuyeux.ai.hello.PeriodicTable

/**
 * HelloMcpService类
 * 
 * 提供元素周期表查询服务的工具类，通过MCP协议将方法暴露为工具。
 * 包含了完整的元素周期表数据和查询接口，支持按名称和序号查询元素信息。
 */
class HelloMcpService {
    
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    
    /**
     * 根据元素中文名称查询元素信息
     * 
     * 此方法通过MCP协议暴露为工具，可供LLM调用。
     * 
     * @param name 元素的中文名称，如"氢"、"氦"等
     * @return 元素的详细信息字符串，包括名称、读音、英文名、原子序数等；如果未找到返回"元素不存在"
     */
    fun getElement(name: String): String {
        logger.info { "获取元素周期表元素信息: $name" }
        return PeriodicTable.getElement(name)
    }
    
    /**
     * 根据元素在周期表中的位置（原子序数）查询元素信息
     * 
     * 此方法通过MCP协议暴露为工具，可供LLM调用。
     * 
     * @param position 元素的原子序数，范围从1到118
     * @return 元素的详细信息字符串，包括名称、读音、英文名、原子序数等；如果未找到返回错误信息
     */
    fun getElementByPosition(position: Int): String {
        logger.info { "获取元素周期表第${position}个元素的信息" }
        return PeriodicTable.getElementByPosition(position)
    }
    
    /**
     * 列出指定周期的所有元素
     * 
     * @param period 周期数，范围从1到7
     * @return 该周期所有元素的信息
     */
    fun getElementsByPeriod(period: Int): String {
        logger.info { "获取第${period}周期的所有元素" }
        
        if (period < 1 || period > 7) {
            return "周期数无效，应在1到7之间"
        }
        
        val elements = PeriodicTable.getElementsByPeriod(period)
        if (elements.isEmpty()) {
            return "第${period}周期没有元素"
        }
        
        return buildString {
            appendLine("第${period}周期的元素（共${elements.size}个）：")
            elements.forEach { element ->
                appendLine(PeriodicTable.formatElement(element))
            }
        }
    }
    
    /**
     * 列出指定族的所有元素
     * 
     * @param group 族名，如"IA"、"VIIA"、"0族"等
     * @return 该族所有元素的信息
     */
    fun getElementsByGroup(group: String): String {
        logger.info { "获取${group}族的所有元素" }
        
        val elements = PeriodicTable.getElementsByGroup(group)
        if (elements.isEmpty()) {
            return "${group}族没有元素或族名无效"
        }
        
        return buildString {
            appendLine("${group}族的元素（共${elements.size}个）：")
            elements.forEach { element ->
                appendLine(PeriodicTable.formatElement(element))
            }
        }
    }
    
    /**
     * 搜索元素（支持多种搜索方式）
     * 
     * @param query 搜索关键词（可以是原子序数、符号、中文名、英文名）
     * @return 搜索结果
     */
    fun searchElement(query: String): String {
        logger.info { "搜索元素: $query" }
        
        if (query.isBlank()) {
            return "搜索关键词不能为空"
        }
        
        val element = PeriodicTable.searchElement(query)
        return if (element != null) {
            PeriodicTable.formatElement(element)
        } else {
            "未找到匹配的元素: $query"
        }
    }
    
    /**
     * 获取周期表统计信息
     * 
     * @return 周期表的统计信息
     */
    fun getPeriodicTableStats(): String {
        val allElements = PeriodicTable.getAllElements()
        val totalElements = allElements.size
        
        val elementsByPeriod = allElements.groupBy { it.period }
        val elementsByGroup = allElements.groupBy { it.group }
        
        return buildString {
            appendLine("元素周期表统计信息：")
            appendLine("总元素数: $totalElements")
            appendLine("周期分布:")
            elementsByPeriod.entries.sortedBy { it.key }.forEach { (period, elements) ->
                appendLine("  第${period}周期: ${elements.size}个元素")
            }
            appendLine("主要族分布:")
            val mainGroups = listOf("IA", "IIA", "IIIA", "IVA", "VA", "VIA", "VIIA", "0族")
            mainGroups.forEach { group ->
                val count = elementsByGroup[group]?.size ?: 0
                if (count > 0) {
                    appendLine("  ${group}: ${count}个元素")
                }
            }
            val transitionElements = elementsByGroup.entries
                .filter { it.key.contains("B") }
                .sumOf { it.value.size }
            if (transitionElements > 0) {
                appendLine("  过渡元素: ${transitionElements}个")
            }
            val lanthanides = elementsByGroup["镧系"]?.size ?: 0
            val actinides = elementsByGroup["锕系"]?.size ?: 0
            if (lanthanides > 0) appendLine("  镧系元素: ${lanthanides}个")
            if (actinides > 0) appendLine("  锕系元素: ${actinides}个")
        }
    }
}

/**
 * MCP工具描述数据类
 */
@Serializable
data class McpTool(
    val name: String,
    val description: String,
    val parameters: Map<String, String> = emptyMap()
)
