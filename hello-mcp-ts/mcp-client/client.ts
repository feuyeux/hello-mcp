import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StreamableHTTPClientTransport } from "@modelcontextprotocol/sdk/client/streamableHttp.js";

/**
 * Hello MCP 客户端类
 * 用于与 MCP 服务器交互
 */
export class HelloClient {
  private client: Client | null = null;
  private transport: StreamableHTTPClientTransport | null = null;

  constructor(public baseUrl: string = "http://localhost:3000") {}

  get endpoint(): string {
    return `${this.baseUrl}/mcp`;
  }

  private async ensureConnected(): Promise<void> {
    if (!this.client || !this.transport) {
      this.transport = new StreamableHTTPClientTransport(new URL(this.endpoint));
      this.client = new Client(
        {
          name: "hello-mcp-client",
          version: "1.0.0",
        },
        {
          capabilities: {},
        }
      );
      await this.client.connect(this.transport);
    }
  }

  async close(): Promise<void> {
    if (this.client) {
      await this.client.close();
      this.client = null;
      this.transport = null;
    }
  }

  /**
   * 列举所有可用工具
   */
  async listTools(): Promise<string> {
    await this.ensureConnected();
    const result = await this.client!.listTools();
    const toolsList = result.tools.map(
      (tool) => `工具名称: ${tool.name}, 描述: ${tool.description}`
    );
    return toolsList.join("\n");
  }

  /**
   * 根据元素名称查询元素信息
   */
  async getElement(name: string): Promise<string> {
    await this.ensureConnected();
    const result = await this.client!.callTool({
      name: "get_element",
      arguments: { name },
    });
    return result.content[0].type === "text" ? result.content[0].text : "";
  }

  /**
   * 根据原子序数查询元素信息
   */
  async getElementByPosition(position: number): Promise<string> {
    await this.ensureConnected();
    const result = await this.client!.callTool({
      name: "get_element_by_position",
      arguments: { position },
    });
    return result.content[0].type === "text" ? result.content[0].text : "";
  }
}
