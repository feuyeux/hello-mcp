package org.feuyeux.ai.hello.utils

import java.io.File

/**
 * 目录工具类
 * 提供与文件系统操作相关的工具方法
 */
object DirUtils {

    /**
     * 获取用户主目录
     * @return 用户主目录的绝对路径
     */
    fun getUserDir(): String {
        return System.getProperty("user.dir") ?: "."
    }

    /**
     * 获取用户家目录
     * @return 用户家目录的绝对路径
     */
    fun getUserHome(): String {
        return System.getProperty("user.home") ?: "~"
    }

    /**
     * 获取系统临时目录
     * @return 系统临时目录的绝对路径
     */
    fun getTempDir(): String {
        return System.getProperty("java.io.tmpdir") ?: "/tmp"
    }

    /**
     * 检查文件或目录是否存在
     * @param path 文件或目录路径
     * @return 如果存在返回true，否则返回false
     */
    fun exists(path: String): Boolean {
        return File(path).exists()
    }

    /**
     * 创建目录（如果不存在）
     * @param path 目录路径
     * @return 如果创建成功或已存在返回true，否则返回false
     */
    fun createDirectory(path: String): Boolean {
        val dir = File(path)
        return dir.exists() || dir.mkdirs()
    }

    /**
     * 获取文件的绝对路径
     * @param path 相对或绝对路径
     * @return 绝对路径
     */
    fun getAbsolutePath(path: String): String {
        return File(path).absolutePath
    }

    /**
     * 获取文件名（不含路径）
     * @param path 文件路径
     * @return 文件名
     */
    fun getFileName(path: String): String {
        return File(path).name
    }

    /**
     * 获取父目录路径
     * @param path 文件或目录路径
     * @return 父目录路径，如果没有父目录返回null
     */
    fun getParentDir(path: String): String? {
        return File(path).parent
    }
}
