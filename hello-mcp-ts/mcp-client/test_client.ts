import { HelloClient } from "./client.js";

async function runAllTests(port: number) {
  const client = new HelloClient(`http://localhost:${port}`);

  try {
    console.log("=== 测试1: 列举Hello MCP工具 ===");
    const tools = await client.listTools();
    console.log(`\n列举到的工具:\n${tools}\n`);

    console.log("=== 测试2: 测试Hello MCP - 按名称查询 ===");
    const result = await client.getElement("氢");
    console.log(`查询氢元素结果: ${result}\n`);

    console.log("=== 测试3: 测试MCP工具调用 - 按位置查询 ===");
    const result2 = await client.getElementByPosition(6);
    console.log(`查询原子序数为6的元素结果: ${result2}\n`);
  } finally {
    await client.close();
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

console.log(`连接到 MCP 服务器: http://localhost:${port}\n`);
runAllTests(port).catch(console.error);
