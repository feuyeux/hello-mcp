import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { SSEClientTransport } from "@modelcontextprotocol/sdk/client/sse.js";

async function testMcpClient() {
  console.log("=== 测试 MCP 客户端 ===");

  // 使用 SSE HTTP 传输层
  const transport = new SSEClientTransport(
    new URL("http://localhost:3000/sse")
  );

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
    // 测试获取工具列表
    console.log("=== 测试获取工具列表 ===");
    const tools = await client.listTools();
    console.log("可用工具:", tools.tools.map((t) => t.name));

    // 测试根据名称获取元素
    console.log("\n=== 测试根据名称获取元素 ===");
    const result1 = await client.callTool({
      name: "get_element",
      arguments: { name: "硅" },
    });
    console.log("硅元素信息:", result1.content[0]);

    // 测试根据位置获取元素
    console.log("\n=== 测试根据位置获取元素 ===");
    const result2 = await client.callTool({
      name: "get_element_by_position",
      arguments: { position: 14 },
    });
    console.log("第14号元素信息:", result2.content[0]);

  } catch (error) {
    console.error("测试失败:", error);
  } finally {
    await client.close();
  }
}

testMcpClient().catch(console.error);
