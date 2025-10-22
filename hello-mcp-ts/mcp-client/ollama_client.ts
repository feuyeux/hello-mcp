import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StreamableHTTPClientTransport } from "@modelcontextprotocol/sdk/client/streamableHttp.js";

/**
 * 消息类
 */
export class Message {
  constructor(
    public role: string,
    public content: string
  ) {}

  toDict(): { role: string; content: string } {
    return { role: this.role, content: this.content };
  }
}

/**
 * 工具调用类
 */
export class ToolCall {
  constructor(
    public name: string,
    public args: Record<string, any>
  ) {}
}

/**
 * 聊天响应类
 */
export class ChatResponse {
  constructor(
    public role: string,
    public content: string,
    public toolCalls: ToolCall[] = []
  ) {}

  hasToolCalls(): boolean {
    return this.toolCalls.length > 0;
  }
}

/**
 * Ollama 客户端
 * 用于与 Ollama API 交互，支持工具调用
 */
export class OllamaClient {
  private baseUrl: string;
  private model: string;

  constructor(baseUrl: string = "http://localhost:11434", model: string = "qwen2.5:latest") {
    this.baseUrl = baseUrl;
    this.model = model;
  }

  /**
   * 发送聊天请求
   */
  async chat(messages: Message[], tools: any[]): Promise<ChatResponse> {
    try {
      console.log(`发送聊天请求到 Ollama: model=${this.model}, messages=${messages.length}`);

      // 构建请求体
      const requestBody = {
        model: this.model,
        stream: false,
        messages: messages.map((msg) => msg.toDict()),
        tools: tools,
      };

      // 发送请求
      const response = await fetch(`${this.baseUrl}/api/chat`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        throw new Error(`Ollama API 请求失败: ${response.status}`);
      }

      const responseJson = await response.json();
      const messageNode = responseJson.message || {};

      const role = messageNode.role || "assistant";
      const content = messageNode.content || "";

      // 解析工具调用
      const toolCalls: ToolCall[] = [];
      if (messageNode.tool_calls) {
        for (const toolCallNode of messageNode.tool_calls) {
          const functionNode = toolCallNode.function || {};
          toolCalls.push(
            new ToolCall(
              functionNode.name || "",
              functionNode.arguments || {}
            )
          );
        }
      }

      return new ChatResponse(role, content, toolCalls);
    } catch (error) {
      console.error("Ollama 请求失败:", error);
      throw new Error(`Ollama 请求失败: ${error}`);
    }
  }

  /**
   * 执行工具调用
   */
  async executeToolCall(toolCall: ToolCall, mcpEndpoint: string): Promise<string> {
    try {
      console.log(`执行工具调用: ${toolCall.name}, 参数:`, toolCall.args);

      // 创建 MCP 客户端
      const transport = new StreamableHTTPClientTransport(new URL(mcpEndpoint));
      const client = new Client(
        {
          name: "ollama-mcp-client",
          version: "1.0.0",
        },
        {
          capabilities: {},
        }
      );

      await client.connect(transport);

      try {
        let result: string;

        if (toolCall.name === "get_element") {
          const name = toolCall.args.name;
          const response = await client.callTool({
            name: "get_element",
            arguments: { name },
          });
          result = response.content[0].type === "text" ? response.content[0].text : "";
        } else if (toolCall.name === "get_element_by_position") {
          let position = toolCall.args.position;
          if (typeof position === "number" && !Number.isInteger(position)) {
            position = Math.floor(position);
          }
          const response = await client.callTool({
            name: "get_element_by_position",
            arguments: { position },
          });
          result = response.content[0].type === "text" ? response.content[0].text : "";
        } else {
          result = JSON.stringify({ error: `未知工具: ${toolCall.name}` });
        }

        console.log(`工具调用结果: ${result}`);
        return result;
      } finally {
        await client.close();
      }
    } catch (error) {
      console.error("工具调用失败:", error);
      return JSON.stringify({ error: String(error) });
    }
  }
}
