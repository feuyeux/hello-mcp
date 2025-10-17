package org.feuyeux.ai.hello;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.feuyeux.ai.hello.container.TomChat;
import org.feuyeux.ai.hello.service.HelloMcpService;

import java.nio.charset.StandardCharsets;

@Slf4j
public class HelloMcpServer {

  public static void main(String[] args) {
    // 设置控制台输出编码为 UTF-8
      System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
      System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));

      var mcpService = new HelloMcpService();
    var transportProvider =
        HttpServletStreamableServerTransportProvider.builder().mcpEndpoint("hello-mcp").build();

    McpSyncServer server =
        McpServer.sync(transportProvider)
            .serverInfo("hello-mcp-server", "1.0.0")
            .capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build())
            .tools(
                McpServerFeatures.SyncToolSpecification.builder()
                    .tool(
                        McpSchema.Tool.builder()
                            .name("getElement")
                            .description("根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）。参数: name (string) - 元素名称")
                            .build())
                    .callHandler(
                        (call, context) -> {
                          Object nameObj = context.arguments().get("name");
                          String name = nameObj != null ? nameObj.toString() : "";
                          log.info("接收到参数 name: {}", name);
                          var element = mcpService.getElement(name);
                          return McpSchema.CallToolResult.builder().addTextContent(element).build();
                        })
                    .build(),
                McpServerFeatures.SyncToolSpecification.builder()
                    .tool(
                        McpSchema.Tool.builder()
                            .name("getElementByPosition")
                            .description(
                                "根据元素在周期表中的位置（原子序数）查询元素信息。参数: position (integer) - 元素的原子序数（1-118）")
                            .build())
                    .callHandler(
                        (call, context) -> {
                          Object positionObj = context.arguments().get("position");
                          int position =
                              positionObj != null ? Integer.parseInt(positionObj.toString()) : 0;
                          log.info("接收到参数 position: {}", position);
                          var element = mcpService.getElementByPosition(position);
                          return McpSchema.CallToolResult.builder().addTextContent(element).build();
                        })
                    .build())
            .build();

    var tomcat = TomChat.createTomcatServer("", 9900, transportProvider);
    try {
      tomcat.start();
    } catch (LifecycleException e) {
      throw new RuntimeException(e);
    }
  }
}
