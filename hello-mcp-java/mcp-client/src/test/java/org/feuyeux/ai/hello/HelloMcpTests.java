package org.feuyeux.ai.hello;

import static org.feuyeux.ai.hello.utils.DotEnv.loadEnv;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.hello.mcp.HelloClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * MCP测试套件
 *
 * <p>包含所有MCP相关测试功能： - MCP工具调试和列举 - 直接MCP工具调用测试 - 文件系统、Hello和地图MCP服务器测试
 */
@Slf4j
public class HelloMcpTests {

  @BeforeAll
  public static void init() {
    loadEnv();
    // 支持通过系统属性设置端口: -Dmcp.server.port=9900
    String portStr = System.getProperty("mcp.server.port");
    if (portStr != null && !portStr.isEmpty()) {
      try {
        int port = Integer.parseInt(portStr);
        HelloClient.setServerPort(port);
        log.info("使用系统属性指定的端口: {}", port);
      } catch (NumberFormatException e) {
        log.warn("无效的端口号: {}, 使用默认端口", portStr);
      }
    }
  }

  @Test
  @DisplayName("列举Hello MCP工具")
  public void testListTools() {
    String tools = HelloClient.listTools();
    log.info("列举到的工具: \n{}", tools);
  }

  @Test
  @DisplayName("测试Hello MCP - 按名称查询")
  public void testHelloMcpByName() {
    String result = HelloClient.getElement("氢");
    log.info("查询氢元素结果: {}", result);
  }

  @Test
  @DisplayName("测试MCP工具调用 - 按位置查询")
  public void testMcpToolByPosition() {
    String result = HelloClient.getElementByPosition(6);
    log.info("查询原子序数为6的元素结果: {}", result);
  }
}
