import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StreamableHTTPClientTransport } from "@modelcontextprotocol/sdk/client/streamableHttp.js";

/**
 * Hello MCP 客户端类
 * 用于与 MCP 服务器交互
 */
export class HelloClient {
  constructor(public baseUrl: string = "http://localhost:3000") {}

  get endpoint(): string {
    return `${this.baseUrl}/mcp`;
  }

  /**
   * 列举所有可用工具
   */
  async listTools(): Promise<string> {
    const transport = new StreamableHTTPClientTransport(new URL(this.endpoint));
    const client = new Client(
      {
        name: "hello-mcp-client",
        version: "1.0.0",
      },
      {
        capabilities: {},
      }
    );

    await client.connect(transport);

    try {
      const result = await client.listTools();
      const toolsList = result.tools.map(
        (tool) => `工具名称: ${tool.name}, 描述: ${tool.description}`
      );
      return toolsList.join("\n");
    } finally {
      await client.close();
    }
  }

  /**
   * 根据元素名称查询元素信息
   */
  async getElement(name: string): Promise<string> {
    const transport = new StreamableHTTPClientTransport(new URL(this.endpoint));
    const client = new Client(
      {
        name: "hello-mcp-client",
        version: "1.0.0",
      },
      {
        capabilities: {},
      }
    );

    await client.connect(transport);

    try {
      const result = await client.callTool({
        name: "get_element",
        arguments: { name },
      });
      return result.content[0].type === "text" ? result.content[0].text : "";
    } finally {
      await client.close();
    }
  }

  /**
   * 根据原子序数查询元素信息
   */
  async getElementByPosition(position: number): Promise<string> {
    const transport = new StreamableHTTPClientTransport(new URL(this.endpoint));
    const client = new Client(
      {
        name: "hello-mcp-client",
        version: "1.0.0",
      },
      {
        capabilities: {},
      }
    );

    await client.connect(transport);

    try {
      const result = await client.callTool({
        name: "get_element_by_position",
        arguments: { position },
      });
      return result.content[0].type === "text" ? result.content[0].text : "";
    } finally {
      await client.close();
    }
  }
}
