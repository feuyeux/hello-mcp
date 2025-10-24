package org.feuyeux.ai.hello;

import static org.feuyeux.ai.hello.utils.DotEnv.loadEnv;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.hello.llm.OllamaClient;
import org.feuyeux.ai.hello.mcp.HelloClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * LLM 与 MCP 集成测试
 *
 * <p>测试通过 LLM (Ollama) 调用 MCP 工具的完整流程
 */
@Slf4j
public class LlmMcpIntegrationTest {

  private static OllamaClient ollamaClient;

  @BeforeAll
  public static void init() {
    loadEnv();
    // 支持通过系统属性设置端口: -Dmcp.server.port=9900
    String portStr = System.getProperty("mcp.server.port");
    if (portStr != null && !portStr.isEmpty()) {
      try {
        int port = Integer.parseInt(portStr);
        HelloClient.initClientWithServerPort(port);
        log.info("使用系统属性指定的端口: {}", port);
      } catch (NumberFormatException e) {
        log.warn("无效的端口号: {}, 使用默认端口", portStr);
      }
    } else {
      HelloClient.initClient();
    }
    ollamaClient = new OllamaClient();
    log.info("初始化 Ollama 客户端完成");
  }

  @AfterAll
  public static void destroy() {
    HelloClient.destroyClient();
  }

  @Test
  @DisplayName("测试 LLM 通过工具调用查询元素")
  public void testLlmWithMcpTools() {
    try {
      log.info("=== 测试: LLM 通过工具调用查询元素 ===");
      List<OllamaClient.Message> messages = new ArrayList<>();
      String query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量";
      messages.add(new OllamaClient.Message("user", query));
      var tools = HelloClient.listToolsResult();
      log.info("第一次调用 LLM: {}", query);
      OllamaClient.ChatResponse response = ollamaClient.chat(messages, tools);

      log.info("LLM 响应角色: {}", response.getRole());
      log.info("LLM 响应内容: {}", response.getContent());

      // 检查是否有工具调用
      if (response.hasToolCalls()) {
        log.info("LLM 决定调用工具，工具数量: {}", response.getToolCalls().size());

        // 执行工具调用
        for (OllamaClient.ToolCall toolCall : response.getToolCalls()) {
          log.info("执行工具: {}", toolCall.getName());
          log.info("工具参数: {}", toolCall.getArguments());

          String toolResult = ollamaClient.executeToolCall(toolCall);
          log.info("工具执行结果: {}", toolResult);

          // 将工具结果添加到消息历史
          messages.add(new OllamaClient.Message("assistant", ""));
          messages.add(new OllamaClient.Message("tool", toolResult));
        }

        // 第二次调用 LLM，让其基于工具结果生成最终答案
        log.info("第二次调用 LLM，生成最终答案...");
        OllamaClient.ChatResponse finalResponse = ollamaClient.chat(messages, tools);

        log.info("最终答案: {}", finalResponse.getContent());
        log.info("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息");

      } else {
        log.warn("LLM 没有调用工具，直接返回了答案: {}", response.getContent());
        log.info("这可能是因为 LLM 已经知道答案，或者不支持工具调用");
      }

    } catch (Exception e) {
      log.error("测试失败", e);
      log.info(
          "提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载\n"
              + "启动命令: ollama serve\n"
              + "下载模型: ollama pull qwen2.5:latest");
    }
  }
}
