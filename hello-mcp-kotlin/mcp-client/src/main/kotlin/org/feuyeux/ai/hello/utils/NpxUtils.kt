package org.feuyeux.ai.hello.utils

/**
 * NPX工具类
 * 提供与npm/npx相关的工具方法
 */
object NpxUtils {

    /**
     * 构建npx命令
     * 根据操作系统类型返回正确的npx命令
     * @return npx命令字符串
     */
    fun buildNpxCmd(): String {
        val osName = System.getProperty("os.name").lowercase()
        return if (osName.contains("win")) {
            "npx.cmd"
        } else {
            "npx"
        }
    }

    /**
     * 构建npm命令
     * 根据操作系统类型返回正确的npm命令
     * @return npm命令字符串
     */
    fun buildNpmCmd(): String {
        val osName = System.getProperty("os.name").lowercase()
        return if (osName.contains("win")) {
            "npm.cmd"
        } else {
            "npm"
        }
    }

    /**
     * 检查node是否已安装
     * @return 如果node已安装返回true，否则返回false
     */
    fun isNodeInstalled(): Boolean {
        return try {
            val process = ProcessBuilder("node", "--version").start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查npm是否已安装
     * @return 如果npm已安装返回true，否则返回false
     */
    fun isNpmInstalled(): Boolean {
        return try {
            val process = ProcessBuilder(buildNpmCmd(), "--version").start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查npx是否已安装
     * @return 如果npx已安装返回true，否则返回false
     */
    fun isNpxInstalled(): Boolean {
        return try {
            val process = ProcessBuilder(buildNpxCmd(), "--version").start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取node版本
     * @return node版本字符串，如果获取失败返回null
     */
    fun getNodeVersion(): String? {
        return try {
            val process = ProcessBuilder("node", "--version").start()
            if (process.waitFor() == 0) {
                process.inputStream.bufferedReader().readText().trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取npm版本
     * @return npm版本字符串，如果获取失败返回null
     */
    fun getNpmVersion(): String? {
        return try {
            val process = ProcessBuilder(buildNpmCmd(), "--version").start()
            if (process.waitFor() == 0) {
                process.inputStream.bufferedReader().readText().trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
