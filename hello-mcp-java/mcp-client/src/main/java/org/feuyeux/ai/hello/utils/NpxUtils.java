package org.feuyeux.ai.hello.utils;

/**
 * NPX命令工具类
 *
 * <p>提供与NPX命令构建相关的工具方法
 */
public class NpxUtils {

  /**
   * 构建NPX命令 根据操作系统返回适当的NPX命令
   *
   * @return NPX命令字符串
   */
  public static String buildNpxCmd() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("windows")) {
      return "npx.cmd";
    } else {
      return "npx";
    }
  }
}
