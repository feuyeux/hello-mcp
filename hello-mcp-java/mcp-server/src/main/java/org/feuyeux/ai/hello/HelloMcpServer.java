package org.feuyeux.ai.hello;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.feuyeux.ai.hello.container.TomChat;
import org.feuyeux.ai.hello.service.HelloMcpService;

@Slf4j
public class HelloMcpServer {

  public static void main(String[] args) {
    // 设置控制台输出编码为 UTF-8
    System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));

    // 解析端口参数，默认为 9900
    int port = 9900;
    for (int i = 0; i < args.length; i++) {
      if ("--port".equals(args[i]) && i + 1 < args.length) {
        try {
          port = Integer.parseInt(args[i + 1]);
          log.info("使用指定端口: {}", port);
        } catch (NumberFormatException e) {
          log.error("无效的端口号: {}, 使用默认端口 9900", args[i + 1]);
        }
        break;
      }
    }

    var mcpService = new HelloMcpService();
    var transportProvider =
        HttpServletStreamableServerTransportProvider.builder().mcpEndpoint("mcp/").build();

    // 创建 MCP Server 并构建
    // 注意：server 变量在构建完成后不再直接使用，但 build 仍然正常工作
    // 原因：McpServer.sync() 在构建过程中会将 server 实例注册到 transportProvider 内部
    // transportProvider 持有 server 的引用，当 HTTP 请求到来时通过它调用已注册的 server
    // 这种设计将 server 的生命周期与 transportProvider 绑定，由 transportProvider 统一管理
    McpSyncServer server =
        McpServer.sync(transportProvider)
            .serverInfo("hello-mcp-server", "1.0.0")
            .capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build())
            .tools(
                McpServerFeatures.SyncToolSpecification.builder()
                    .tool(
                        McpSchema.Tool.builder()
                            .name("get_element")
                            .description("根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）")
                            .inputSchema(
                                new McpSchema.JsonSchema(
                                    "object",
                                    Map.of(
                                        "name",
                                        Map.of(
                                            "type", "string",
                                            "description", "元素的中文名、英文名或符号，如'氢'、'Hydrogen'或'H'")),
                                    List.of("name"),
                                    null,
                                    null,
                                    null))
                            .build())
                    .callHandler(
                        (call, context) -> {
                          Object nameObj = context.arguments().get("name");
                          String name = nameObj != null ? nameObj.toString() : "";
                          log.info("[{}]接收到参数 name: {}", call.sessionId(), name);
                          var element = mcpService.getElement(name);
                          return McpSchema.CallToolResult.builder().addTextContent(element).build();
                        })
                    .build(),
                McpServerFeatures.SyncToolSpecification.builder()
                    .tool(
                        McpSchema.Tool.builder()
                            .name("get_element_by_position")
                            .description("根据元素在周期表中的位置（原子序数）查询元素信息")
                            .inputSchema(
                                new McpSchema.JsonSchema(
                                    "object",
                                    Map.of(
                                        "position",
                                        Map.of(
                                            "type",
                                            "integer",
                                            "description",
                                            "元素的原子序数，范围从1到118",
                                            "minimum",
                                            1,
                                            "maximum",
                                            118)),
                                    List.of("position"),
                                    null,
                                    null,
                                    null))
                            .build())
                    .callHandler(
                        (call, context) -> {
                          Object positionObj = context.arguments().get("position");
                          int position =
                              positionObj != null ? Integer.parseInt(positionObj.toString()) : 0;
                          log.info("[{}]接收到参数 position: {}", call.sessionId(), position);
                          var element = mcpService.getElementByPosition(position);
                          return McpSchema.CallToolResult.builder().addTextContent(element).build();
                        })
                    .build())
            .build();

    var tomcat = TomChat.createTomcatServer("", port, transportProvider);
    try {
      tomcat.start();
      log.info("服务器已启动，监听端口: {}", port);

      // 添加关闭钩子
      Runtime.getRuntime()
          .addShutdownHook(
              new Thread(
                  () -> {
                    try {
                      tomcat.stop();
                      tomcat.destroy();
                    } catch (Exception e) {
                      log.error("关闭Tomcat失败", e);
                    }
                  }));

      // 阻塞主线程
      Thread.currentThread().join();
    } catch (LifecycleException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      log.info("服务器被中断");
    }
  }
}
