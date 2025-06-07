package org.feuyeux.ai.hello;

import static org.feuyeux.ai.hello.mcp.FSClient.buildFSClient;
import static org.feuyeux.ai.hello.mcp.HelloClient.buildHelloClient;
import static org.feuyeux.ai.hello.repository.ModelClient.buildZhiPuAiModel;
import static org.feuyeux.ai.hello.utils.DotEnv.loadEnv;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;

/**
 * 合并的MCP测试套件
 *
 * <p>包含所有MCP相关测试功能： - MCP工具调试和列举 - 直接MCP工具调用测试 - AI模型与MCP工具集成测试 - 文件系统、Hello和地图MCP服务器测试
 */
@Slf4j
public class HelloMcpTests {

  @BeforeAll
  public static void init() {
    loadEnv();
  }

  // ==================== MCP 工具调试测试 ====================

  @Test
  @DisplayName("列举文件系统MCP工具")
  public void listFileSystemMcpTools() {
    log.info("=== 测试: 列举文件系统MCP工具 ===");

    try (var mcpClient = buildFSClient()) {
      var tools = mcpClient.listTools();
      log.info("可用文件系统MCP工具总数: {}", tools.tools().size());

      for (var tool : tools.tools()) {
        log.info("工具名称: {}", tool.name());
        log.info("工具描述: {}", tool.description());
        log.info("工具参数模式: {}", tool.inputSchema());
        log.info("---");
      }
    } catch (Exception e) {
      log.error("列举文件系统工具失败 ", e);
    }
  }

  @Test
  @DisplayName("列举Hello MCP工具")
  public void listHelloMcpTools() {
    log.info("=== 测试: 列举Hello MCP工具 ===");

    try (var mcpClient = buildHelloClient()) {
      var tools = mcpClient.listTools();
      log.info("可用Hello MCP工具总数: " + tools.tools().size());

      for (var tool : tools.tools()) {
        log.info("工具名称: " + tool.name());
        log.info("工具描述: " + tool.description());
        log.info("工具参数模式: " + tool.inputSchema());
        log.info("---");
      }
    } catch (Exception e) {
      System.err.println("列举Hello工具失败: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // ==================== 直接MCP工具调用测试 ====================

  @Test
  @DisplayName("直接调用文件系统MCP工具")
  public void testDirectFileSystemMcpCall() {
    log.info("=== 测试: 直接调用文件系统MCP工具 ===");

    try (var mcpClient = buildFSClient()) {
      // 尝试读取周期表文件
      var request =
          new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(
              "read_file", Map.of("path", "Periodic_table.md"));

      var result = mcpClient.callTool(request);
      log.info("直接MCP调用成功: {}", result);
      log.info("结果内容大小: {}", result.content().size());
    } catch (Exception e) {
      System.err.println("直接MCP调用异常: " + e.getMessage());
      log.error("", e);
    }
  }

  @Test
  @DisplayName("直接调用Hello MCP工具")
  public void testHelloMcpCall() {
    log.info("=== 测试: 直接调用Hello MCP工具 ===");

    try (var mcpClient = buildHelloClient()) {
      // 调用getElement工具
      var request =
          new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(
              "getElement", Map.of("name", "氢"));

      var result = mcpClient.callTool(request);
      log.info("Hello MCP调用成功: {}", result);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  // ==================== 多MCP服务器集成测试 ====================

  @Test
  @DisplayName("测试Hello MCP")
  public void testMcpServers() {
    try (var helloClient = buildHelloClient()) {

      // 首先列出可用的工具
      var tools = helloClient.listTools();
      log.info("可用工具数量: {}", tools.tools().size());
      for (var tool : tools.tools()) {
        log.info("工具: {} - {}", tool.name(), tool.description());
      }

      var mcpProvider = new SyncMcpToolCallbackProvider(helloClient);

      ChatClient chatClient =
          ChatClient.builder(buildZhiPuAiModel()).defaultToolCallbacks(mcpProvider).build();

      // 使用更自然的提示，测试AI是否能自动发现并使用合适的工具
      String question = "我想了解氢元素的详细信息，包括它的原子序数、符号和其他属性。";
      log.info("问题: {}", question);

      String response = chatClient.prompt(question).call().content();
      log.info("回答: {}", response);

    } catch (Exception e) {
      log.error("", e);
    }
  }

  @Test
  @DisplayName("测试MCP工具调用 - 按位置查询")
  public void testMcpToolByPosition() {
    try (var helloClient = buildHelloClient()) {

      var mcpProvider = new SyncMcpToolCallbackProvider(helloClient);

      ChatClient chatClient =
          ChatClient.builder(buildZhiPuAiModel()).defaultToolCallbacks(mcpProvider).build();

      // 测试按位置查询工具
      String question = "请使用getElementByPosition工具查询原子序数为6的元素信息。";
      log.info("问题: {}", question);

      String response = chatClient.prompt(question).call().content();
      log.info("回答: {}", response);

    } catch (Exception e) {
      log.error("", e);
    }
  }
}
