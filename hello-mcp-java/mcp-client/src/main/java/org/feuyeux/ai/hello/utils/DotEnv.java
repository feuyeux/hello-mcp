package org.feuyeux.ai.hello.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 环境变量加载工具类
 *
 * <p>用于加载和管理应用程序的环境变量配置
 */
public class DotEnv {

  private static final Logger log = LoggerFactory.getLogger(DotEnv.class);

  /** 加载环境变量 初始化应用程序所需的环境变量配置 */
  public static void loadEnv() {
    log.info("环境变量加载完成");
    // 这里可以添加具体的环境变量加载逻辑
    // 例如从 .env 文件中加载环境变量
  }

  /**
   * 获取智谱AI的API密钥
   *
   * @return API密钥
   */
  public static String getZhiPuAiApiKey() {
    return System.getenv("ZHIPUAI_API_KEY");
  }

  /**
   * 获取智谱AI密钥（别名方法）
   *
   * @return API密钥
   */
  public static String getZhipuAiKey() {
    return getZhiPuAiApiKey();
  }

  /**
   * 获取千帆API密钥
   *
   * @return API密钥
   */
  public static String getQianFanApiKey() {
    return System.getenv("QIANFAN_API_KEY");
  }

  /**
   * 获取千帆Token密钥对
   *
   * @return Token密钥数组 [apiKey, secretKey]
   */
  public static String[] getQianfanTokenKeys() {
    String apiKey = System.getenv("QIANFAN_API_KEY");
    String secretKey = System.getenv("QIANFAN_SECRET_KEY");
    return new String[] {apiKey, secretKey};
  }

  /**
   * 获取高德地图API密钥
   *
   * @return API密钥
   */
  public static String getLbsGaode() {
    return System.getenv("AMAP_MAPS_API_KEY");
  }
}
