package org.feuyeux.ai.hello.utils;

/**
 * 目录工具类
 *
 * <p>提供与目录操作相关的工具方法
 */
public class DirUtils {

  /**
   * 获取用户当前工作目录
   *
   * @return 当前工作目录的路径
   */
  public static String getUserDir() {
    return System.getProperty("user.dir");
  }

  /**
   * 获取用户主目录
   *
   * @return 用户主目录的路径
   */
  public static String getUserHome() {
    return System.getProperty("user.home");
  }
}
