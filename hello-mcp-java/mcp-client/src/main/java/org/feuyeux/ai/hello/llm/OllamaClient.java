package org.feuyeux.ai.hello.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.modelcontextprotocol.spec.McpSchema;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.hello.mcp.HelloClient;

/**
 * Ollama 客户端
 *
 * <p>用于与 Ollama API 交互，支持工具调用
 */
@Slf4j
public class OllamaClient {

  private static final String DEFAULT_BASE_URL = "http://localhost:11434";
  private static final String DEFAULT_MODEL = "qwen2.5:latest";
  private final String baseUrl;
  private final String model;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public OllamaClient() {
    this(DEFAULT_BASE_URL, DEFAULT_MODEL);
  }

  public OllamaClient(String baseUrl, String model) {
    this.baseUrl = baseUrl;
    this.model = model;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * 发送聊天请求
   *
   * @param messages 消息列表
   * @param tools 可用工具列表
   * @return 响应消息
   */
  public ChatResponse chat(List<Message> messages, McpSchema.ListToolsResult tools) {
    try {
      log.debug("发送聊天请求到 Ollama: model={}, messages={}", model, messages.size());

      ObjectNode requestBody = objectMapper.createObjectNode();
      requestBody.put("model", model);
      requestBody.put("stream", false);

      // 添加消息
      ArrayNode messagesArray = requestBody.putArray("messages");
      for (Message msg : messages) {
        ObjectNode msgNode = messagesArray.addObject();
        msgNode.put("role", msg.getRole());
        msgNode.put("content", msg.getContent());
      }
      List<McpSchema.Tool> toolList = tools.tools();
      ArrayNode toolsArray = requestBody.putArray("tools");
      for (McpSchema.Tool tool : toolList) {
        ObjectNode toolNode = toolsArray.addObject();
        toolNode.put("type", "function");
        ObjectNode functionNode = toolNode.putObject("function");
        functionNode.put("name", tool.name());
        functionNode.put("description", tool.description());
        functionNode.set("parameters", objectMapper.valueToTree(tool.meta()));
      }

      String requestJson = objectMapper.writeValueAsString(requestBody);
      log.debug("请求 JSON: {}", requestJson);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + "/api/chat"))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(requestJson))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      log.debug("响应状态: {}", response.statusCode());
      log.debug("响应内容: {}", response.body());

      if (response.statusCode() != 200) {
        throw new RuntimeException("Ollama API 请求失败: " + response.statusCode());
      }

      JsonNode responseJson = objectMapper.readTree(response.body());
      JsonNode messageNode = responseJson.get("message");

      ChatResponse chatResponse = new ChatResponse();
      chatResponse.setRole(messageNode.get("role").asText());
      chatResponse.setContent(
          messageNode.has("content") ? messageNode.get("content").asText() : "");

      // 解析工具调用
      if (messageNode.has("tool_calls")) {
        List<ToolCall> toolCalls = new ArrayList<>();
        JsonNode toolCallsNode = messageNode.get("tool_calls");
        for (JsonNode toolCallNode : toolCallsNode) {
          JsonNode functionNode = toolCallNode.get("function");
          ToolCall toolCall = new ToolCall();
          toolCall.setName(functionNode.get("name").asText());
          toolCall.setArguments(
              objectMapper.convertValue(functionNode.get("arguments"), java.util.Map.class));
          toolCalls.add(toolCall);
        }
        chatResponse.setToolCalls(toolCalls);
      }

      return chatResponse;

    } catch (Exception e) {
      log.error("Ollama 请求失败", e);
      throw new RuntimeException("Ollama 请求失败", e);
    }
  }

  public String executeToolCall(OllamaClient.ToolCall toolCall) {
    try {
      log.info("执行工具调用: {}, 参数: {}", toolCall.getName(), toolCall.getArguments());

      String result;
      switch (toolCall.getName()) {
        case "getElement":
          String name = (String) toolCall.getArguments().get("name");
          result = HelloClient.getElement(name);
          break;

        case "getElementByPosition":
          Object positionObj = toolCall.getArguments().get("position");
          int position;
          if (positionObj instanceof Integer) {
            position = (Integer) positionObj;
          } else if (positionObj instanceof Double) {
            position = ((Double) positionObj).intValue();
          } else {
            position = Integer.parseInt(positionObj.toString());
          }
          result = HelloClient.getElementByPosition(position);
          break;

        default:
          result = "{\"error\": \"未知工具: " + toolCall.getName() + "\"}";
      }

      log.info("工具调用结果: {}", result);
      return result;

    } catch (Exception e) {
      log.error("工具调用失败", e);
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  /** 消息类 */
  public static class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
      this.role = role;
      this.content = content;
    }

    public String getRole() {
      return role;
    }

    public String getContent() {
      return content;
    }
  }

  /** 工具调用类 */
  public static class ToolCall {
    private String name;
    private java.util.Map<String, Object> arguments;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public java.util.Map<String, Object> getArguments() {
      return arguments;
    }

    public void setArguments(java.util.Map<String, Object> arguments) {
      this.arguments = arguments;
    }
  }

  /** 聊天响应类 */
  public static class ChatResponse {
    private String role;
    private String content;
    private List<ToolCall> toolCalls;

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public List<ToolCall> getToolCalls() {
      return toolCalls;
    }

    public void setToolCalls(List<ToolCall> toolCalls) {
      this.toolCalls = toolCalls;
    }

    public boolean hasToolCalls() {
      return toolCalls != null && !toolCalls.isEmpty();
    }
  }
}
