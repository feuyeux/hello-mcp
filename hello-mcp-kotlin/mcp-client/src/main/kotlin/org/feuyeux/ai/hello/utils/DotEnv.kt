package org.feuyeux.ai.hello.utils

import java.io.FileReader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.exists

/**
 * 环境变量和配置工具类
 * 负责加载和管理配置信息，从.env文件或系统环境变量中获取API密钥等信息
 */
object DotEnv {

    /**
     * 获取智谱AI API密钥
     * @return 智谱AI API密钥字符串
     * @throws IllegalArgumentException 如果未找到API密钥
     */
    fun getZhipuAiKey(): String {
        return valueOf("ZHIPUAI_API_KEY")
            ?: throw IllegalArgumentException("no ZHIPUAI_API_KEY provided!")
    }

    /**
     * 获取千帆模型API密钥对
     */
    fun getQianfanModelApiKeys(): Array<String> {
        return arrayOf(
            valueOf("QIANFAN_ACCESS_KEY")
                ?: throw IllegalArgumentException("no QIANFAN_ACCESS_KEY provided!"),
            valueOf("QIANFAN_SECRET_KEY")
                ?: throw IllegalArgumentException("no QIANFAN_SECRET_KEY provided!")
        )
    }

    /**
     * 获取千帆Token密钥对
     */
    fun getQianfanTokenKeys(): Array<String> {
        return arrayOf(
            valueOf("QIANFAN_API_KEY")
                ?: throw IllegalArgumentException("no QIANFAN_API_KEY provided!"),
            valueOf("QIANFAN_API_SECRET_KEY")
                ?: throw IllegalArgumentException("no QIANFAN_API_SECRET_KEY provided!")
        )
    }

    /**
     * 获取百度地图API密钥
     * @return 百度地图API密钥字符串
     * @throws IllegalArgumentException 如果未找到API密钥
     */
    fun getLbsBaidu(): String {
        return valueOf("BAIDU_MAP_API_KEY")
            ?: throw IllegalArgumentException("no LBSYUN_BAIDU APIKEY provided!")
    }

    /**
     * 获取高德地图API密钥
     * @return 高德地图API密钥字符串
     * @throws IllegalArgumentException 如果未找到API密钥
     */
    fun getLbsGaode(): String {
        return valueOf("AMAP_MAPS_API_KEY")
            ?: throw IllegalArgumentException("no AMAP_MAPS_API_KEY APIKEY provided!")
    }

    /**
     * 获取地图服务提供商配置
     * @return 地图服务提供商名称（BAIDU或AMAP）
     * @throws IllegalArgumentException 如果未找到配置
     */
    fun getMcpMap(): String {
        return valueOf("MCP_MAP")
            ?: throw IllegalArgumentException("no MCP_MAP provided!")
    }

    /**
     * 获取配置值
     * 首先尝试从系统环境变量获取，然后从系统属性获取
     * @param key 配置键名
     * @return 配置值，如果未找到返回null
     */
    fun valueOf(key: String): String? {
        return System.getenv(key) ?: System.getProperty(key)
    }

    /**
     * 加载.env文件内容到系统属性
     * 递归向上搜索目录树，查找.env文件
     * @throws RuntimeException 如果找不到.env文件或加载过程出错
     */
    fun loadEnv() {
        // 搜索.env文件
        var path: Path? = Paths.get(".").toAbsolutePath()
        val maxDepth = 5 // 限制递归深度，避免无限循环

        repeat(maxDepth) {
            path?.let { currentPath ->
                val filePath = currentPath.resolve(".env")
                if (filePath.exists()) {
                    // 加载.env内容到系统属性
                    try {
                        val properties = Properties()
                        FileReader(filePath.toFile()).use { reader ->
                            properties.load(reader)
                        }
                        System.getProperties().putAll(properties)
                        return
                    } catch (e: Exception) {
                        throw RuntimeException("Error loading .env file: ${e.message}", e)
                    }
                }
                path = currentPath.parent
            }
        }

        throw RuntimeException("No .env file found within $maxDepth levels of directory hierarchy!")
    }
}
