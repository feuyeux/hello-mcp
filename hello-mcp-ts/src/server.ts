#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { SSEServerTransport } from "@modelcontextprotocol/sdk/server/sse.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import express from "express";
import { periodicTable } from "./periodic-table.js";

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(express.json());

// 创建 MCP 服务器
const mcpServer = new Server(
  {
    name: "hello-mcp-ts",
    version: "1.0.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

// 工具列表
mcpServer.setRequestHandler(ListToolsRequestSchema, async () => {
  console.log("处理 tools/list 请求");
  return {
    tools: [
      {
        name: "get_element",
        description: "根据元素名称获取元素周期表元素信息",
        inputSchema: {
          type: "object",
          properties: {
            name: {
              type: "string",
              description: "元素的中文名称，如'氢'、'氦'等",
            },
          },
          required: ["name"],
        },
      },
      {
        name: "get_element_by_position",
        description: "根据元素在周期表中的位置（原子序数）查询元素信息",
        inputSchema: {
          type: "object",
          properties: {
            position: {
              type: "number",
              description: "元素的原子序数，范围从1到118",
            },
          },
          required: ["position"],
        },
      },
    ],
  };
});

// 工具调用处理
mcpServer.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;
  console.log(`处理 tools/call 请求: ${name}`);

  switch (name) {
    case "get_element": {
      const elementName = args.name as string;
      if (!elementName) {
        return {
          content: [
            {
              type: "text",
              text: "元素名称不能为空",
            },
          ],
        };
      }

      const element = periodicTable.find((el) => el.name === elementName);
      if (!element) {
        return {
          content: [
            {
              type: "text",
              text: "元素不存在",
            },
          ],
        };
      }

      return {
        content: [
          {
            type: "text",
            text: `元素名称: ${element.name} (${element.pronunciation}, ${element.englishName}), 原子序数: ${element.atomicNumber}, 符号: ${element.symbol}, 相对原子质量: ${element.atomicWeight.toFixed(3)}, 周期: ${element.period}, 族: ${element.group}`,
          },
        ],
      };
    }

    case "get_element_by_position": {
      const position = args.position as number;
      if (position < 1 || position > 118) {
        return {
          content: [
            {
              type: "text",
              text: "原子序数必须在1-118之间",
            },
          ],
        };
      }

      const element = periodicTable.find((el) => el.atomicNumber === position);
      if (!element) {
        return {
          content: [
            {
              type: "text",
              text: "元素不存在",
            },
          ],
        };
      }

      return {
        content: [
          {
            type: "text",
            text: `元素名称: ${element.name} (${element.pronunciation}, ${element.englishName}), 原子序数: ${element.atomicNumber}, 符号: ${element.symbol}, 相对原子质量: ${element.atomicWeight.toFixed(3)}, 周期: ${element.period}, 族: ${element.group}`,
          },
        ],
      };
    }

    default:
      throw new Error(`Unknown tool: ${name}`);
  }
});

// 健康检查端点
app.get("/health", (req, res) => {
  res.json({ status: "UP", server: "hello-mcp-ts" });
});

// SSE 端点 - 符合 MCP 协议
app.get("/sse", async (req, res) => {
  console.log("新的 SSE 连接建立");
  
  // 设置 SSE 响应头
  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");
  
  // 创建 SSE 传输层
  const transport = new SSEServerTransport("/messages", res);
  
  // 连接 MCP 服务器
  await mcpServer.connect(transport);
  
  // 处理连接关闭
  req.on("close", () => {
    console.log("SSE 连接已关闭");
  });
});

// 消息端点 - 处理客户端请求
app.post("/messages", async (req, res) => {
  console.log("收到消息请求:", req.body);
  
  // 这里应该由 SSEServerTransport 处理
  // 但为了兼容性，我们也可以直接处理
  res.json({ received: true });
});

// 启动服务器
app.listen(PORT, () => {
  console.log(`MCP TypeScript Server 已启动`);
  console.log(`服务器地址: http://localhost:${PORT}`);
  console.log(`SSE 端点: http://localhost:${PORT}/sse`);
  console.log(`消息端点: http://localhost:${PORT}/messages`);
  console.log(`健康检查: http://localhost:${PORT}/health`);
});
