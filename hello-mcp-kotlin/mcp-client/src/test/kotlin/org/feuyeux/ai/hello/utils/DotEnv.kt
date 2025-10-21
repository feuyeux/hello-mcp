package org.feuyeux.ai.hello.utils

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * 环境变量加载工具类
 *
 * 用于加载和管理应用程序的环境变量配置
 */
object DotEnv {
    /**
     * 加载环境变量
     * 初始化应用程序所需的环境变量配置
     */
    fun loadEnv() {
        logger.info { "环境变量加载完成" }
        // 这里可以添加具体的环境变量加载逻辑
        // 例如从 .env 文件中加载环境变量
    }

    /**
     * 获取智谱AI的API密钥
     *
     * @return API密钥
     */
    fun getZhiPuAiApiKey(): String? {
        return System.getenv("ZHIPUAI_API_KEY")
    }

    /**
     * 获取智谱AI密钥（别名方法）
     *
     * @return API密钥
     */
    fun getZhipuAiKey(): String? {
        return getZhiPuAiApiKey()
    }

    /**
     * 获取千帆API密钥
     *
     * @return API密钥
     */
    fun getQianFanApiKey(): String? {
        return System.getenv("QIANFAN_API_KEY")
    }

    /**
     * 获取千帆Token密钥对
     *
     * @return Token密钥数组 [apiKey, secretKey]
     */
    fun getQianfanTokenKeys(): Array<String?> {
        val apiKey = System.getenv("QIANFAN_API_KEY")
        val secretKey = System.getenv("QIANFAN_SECRET_KEY")
        return arrayOf(apiKey, secretKey)
    }

    /**
     * 获取高德地图API密钥
     *
     * @return API密钥
     */
    fun getLbsGaode(): String? {
        return System.getenv("AMAP_MAPS_API_KEY")
    }
}
