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
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}
