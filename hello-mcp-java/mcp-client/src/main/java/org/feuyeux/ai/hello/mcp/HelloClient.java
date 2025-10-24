package org.feuyeux.ai.hello.mcp;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * HelloClient类
 *
 * <p>此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。 使用 HTTP 客户端连接到 WebFlux 服务端。
 */
@Slf4j
public class HelloClient {

  @Getter private static int serverPort = 9900; // 默认端口
  private static McpAsyncClient client;

  public static void initClient() {
    var transport =
        HttpClientStreamableHttpTransport.builder("http://localhost:" + serverPort)
            .endpoint("mcp/")
            .build();
    client = McpClient.async(transport).requestTimeout(Duration.ofHours(10)).build();
    var resultMono = client.initialize();
    log.info("{}", Objects.requireNonNull(resultMono.block()));
  }

  public static void initClientWithServerPort(int port) {
    serverPort = port;
    initClient();
  }

  public static void destroyClient() {
    if (client != null) {
      client.close();
      log.info("MCP客户端已关闭");
    }
  }

  public static McpSchema.ListToolsResult listToolsResult() {
    return client.listTools().block();
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

    log.info("列举工具成功: {}", toolsList);
    return toolsList.toString();
  }

  public static String getElement(String name) {
    log.info("查询元素: {}", name);
    McpSchema.CallToolResult result =
        client
            .callTool(
                McpSchema.CallToolRequest.builder()
                    .name("get_element")
                    .arguments(Map.of("name", name))
                    .build())
            .block();

    log.info("查询元素 {} 成功: {}", name, result.content());
    return result.content().toString();
  }

  public static String getElementByPosition(int position) {
    log.info("查询位置元素: {}", position);
    McpSchema.CallToolResult result =
        client
            .callTool(
                McpSchema.CallToolRequest.builder()
                    .name("get_element_by_position")
                    .arguments(Map.of("position", position))
                    .build())
            .block();
    log.info("查询位置元素 {} 成功: {}", position, Objects.requireNonNull(result).content());
    return result.content().toString();
  }
}
