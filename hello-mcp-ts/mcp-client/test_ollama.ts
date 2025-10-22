import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StreamableHTTPClientTransport } from "@modelcontextprotocol/sdk/client/streamableHttp.js";
import { OllamaClient, Message } from "./ollama_client.js";

async function testLlmWithMcpTools(port: number = 3000) {
  const ollamaClient = new OllamaClient();
  const mcpEndpoint = `http://localhost:${port}/mcp`;

  try {
    console.log("=== 测试: LLM 通过工具调用查询元素 ===");

    // 获取可用工具
    const transport = new StreamableHTTPClientTransport(new URL(mcpEndpoint));
    const client = new Client(
      {
        name: "ollama-test-client",
        version: "1.0.0",
      },
      {
        capabilities: {},
      }
    );

    await client.connect(transport);

    const toolsResult = await client.listTools();

    // 转换工具格式为 Ollama 格式
    const tools = toolsResult.tools.map((tool) => ({
      type: "function",
      function: {
        name: tool.name,
        description: tool.description,
        parameters: tool.inputSchema,
      },
    }));

    await client.close();

    // 构建消息
    const messages: Message[] = [];
    const query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量";
    messages.push(new Message("user", query));

    // 第一次调用 LLM
    console.log(`\n第一次调用 LLM: ${query}`);
    const response = await ollamaClient.chat(messages, tools);

    console.log(`LLM 响应角色: ${response.role}`);
    console.log(`LLM 响应内容: ${response.content}`);

    // 检查是否有工具调用
    if (response.hasToolCalls()) {
      console.log(`\nLLM 决定调用工具，工具数量: ${response.toolCalls.length}`);

      // 执行工具调用
      for (const toolCall of response.toolCalls) {
        console.log(`\n执行工具: ${toolCall.name}`);
        console.log(`工具参数:`, toolCall.args);

        const toolResult = await ollamaClient.executeToolCall(toolCall, mcpEndpoint);
        console.log(`工具执行结果: ${toolResult}`);

        // 将工具结果添加到消息历史
        messages.push(new Message("assistant", ""));
        messages.push(new Message("tool", toolResult));
      }

      // 第二次调用 LLM，让其基于工具结果生成最终答案
      console.log("\n第二次调用 LLM，生成最终答案...");
      const finalResponse = await ollamaClient.chat(messages, tools);

      console.log(`\n最终答案: ${finalResponse.content}`);
      console.log("\n✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息");
    } else {
      console.log(`\nLLM 没有调用工具，直接返回了答案: ${response.content}`);
      console.log("这可能是因为 LLM 已经知道答案，或者不支持工具调用");
    }
  } catch (error) {
    console.error("测试失败:", error);
    console.log(
      "\n提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载\n" +
        "启动命令: ollama serve\n" +
        "下载模型: ollama pull qwen2.5:latest"
    );
  }
}

// 解析命令行参数
const args = process.argv.slice(2);
let port = 3000;

for (let i = 0; i < args.length; i++) {
  if (args[i] === "--port" && i + 1 < args.length) {
    port = parseInt(args[i + 1], 10);
  }
}

console.log(`连接到 MCP 服务器: http://localhost:${port}`);
testLlmWithMcpTools(port).catch(console.error);
