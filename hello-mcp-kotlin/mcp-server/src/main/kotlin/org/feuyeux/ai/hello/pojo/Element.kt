package org.feuyeux.ai.hello.pojo

/**
 * 元素类
 *
 * 此类表示化学元素周期表中的一个元素实体。
 * 包含元素的基本属性如原子序数、符号、名称等。
 */
data class Element(
    /** 元素的原子序数，表示元素在周期表中的位置 */
    val atomicNumber: Int,
    
    /** 元素的符号，如"H"代表氢 */
    val symbol: String,
    
    /** 元素的中文名称，如"氢" */
    val name: String,
    
    /** 元素的中文读音，如"qīng" */
    val pronunciation: String,
    
    /** 元素的英文名称，如"Hydrogen" */
    val englishName: String,
    
    /** 元素的相对原子质量 */
    val atomicWeight: Double,
    
    /** 元素在周期表中的周期（横行） */
    val period: Int,
    
    /** 元素在周期表中的族（纵列） */
    val group: String
)
