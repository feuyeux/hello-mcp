import { HelloClient } from "./client.js";

async function testListTools(port: number) {
  console.log("=== 测试1: 列举Hello MCP工具 ===");
  const client = new HelloClient(`http://localhost:${port}`);
  const tools = await client.listTools();
  console.log(`\n列举到的工具:\n${tools}\n`);
}

async function testGetElementByName(port: number) {
  console.log("=== 测试2: 测试Hello MCP - 按名称查询 ===");
  const client = new HelloClient(`http://localhost:${port}`);
  const result = await client.getElement("氢");
  console.log(`查询氢元素结果: ${result}\n`);
}

async function testGetElementByPosition(port: number) {
  console.log("=== 测试3: 测试MCP工具调用 - 按位置查询 ===");
  const client = new HelloClient(`http://localhost:${port}`);
  const result = await client.getElementByPosition(6);
  console.log(`查询原子序数为6的元素结果: ${result}\n`);
}

async function runAllTests(port: number) {
  await testListTools(port);
  await testGetElementByName(port);
  await testGetElementByPosition(port);
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
