package org.feuyeux.ai.hello.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * HelloClient类
 *
 * <p>此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。 使用 HTTP 客户端连接到 WebFlux 服务端。
 */
@Slf4j
public class HelloClient {

  private static int serverPort = 9900; // 默认端口
  private static HttpClientStreamableHttpTransport transport;

  static {
    initTransport();
  }

  /**
   * 设置服务器端口
   *
   * @param port 服务器端口号
   */
  public static void setServerPort(int port) {
    serverPort = port;
    initTransport();
    log.info("客户端连接端口已设置为: {}", port);
  }

  /**
   * 获取当前服务器端口
   *
   * @return 当前端口号
   */
  public static int getServerPort() {
    return serverPort;
  }

  private static void initTransport() {
    String baseUrl = "http://localhost:" + serverPort;
    transport = HttpClientStreamableHttpTransport.builder(baseUrl).endpoint("mcp").build();
  }

  public static McpSchema.ListToolsResult listToolsResult() {
    try (var client = McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build()) {
      client.initialize();
      return client.listTools();
    }
  }

  public static String listTools() {
    var result = listToolsResult();
    StringBuilder toolsList = new StringBuilder();
    for (var tool : result.tools()) {
      toolsList
          .append("工具名称: ")
          .append(tool.name())
          .append(", 描述: ")
          .append(tool.description())
          .append("\n");
    }

    log.debug("列举工具成功: {}", toolsList);
    return toolsList.toString();
  }

  public static String getElement(String name) {
    log.debug("查询元素: {}", name);
    try (var client = McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build()) {
      client.initialize();
      McpSchema.CallToolResult result =
          client.callTool(
              McpSchema.CallToolRequest.builder()
                  .name("getElement")
                  .arguments(Map.of("name", name))
                  .build());

      log.debug("查询元素 {} 成功: {}", name, result.content());
      return result.content().toString();
    }
  }

  public static String getElementByPosition(int position) {
    log.debug("查询位置元素: {}", position);
    try (var client = McpClient.sync(transport).requestTimeout(Duration.ofHours(10)).build()) {
      client.initialize();
      McpSchema.CallToolResult result =
          client.callTool(
              McpSchema.CallToolRequest.builder()
                  .name("getElementByPosition")
                  .arguments(Map.of("position", position))
                  .build());

      log.debug("查询位置元素 {} 成功: {}", position, result.content());
      return result.content().toString();
    }
  }
}
