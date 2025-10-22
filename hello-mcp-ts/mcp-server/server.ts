#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StreamableHTTPServerTransport } from "@modelcontextprotocol/sdk/server/streamableHttp.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import { createServer } from "node:http";
import { randomUUID } from "node:crypto";
import { periodicTable } from "./periodic_table.js";

const PORT = process.env.PORT || 3000;

// 创建 MCP 服务器
const mcpServer = new Server(
  {
    name: "mcp-server",
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

// 存储会话的传输层实例
const sessions = new Map<string, StreamableHTTPServerTransport>();

// 创建 HTTP 服务器
const httpServer = createServer(async (req, res) => {
  // 只处理 /mcp 路径
  if (!req.url?.startsWith("/mcp")) {
    if (req.url === "/health") {
      res.writeHead(200, { "Content-Type": "application/json" });
      res.end(JSON.stringify({ status: "UP", server: "mcp-server" }));
      return;
    }
    res.writeHead(404);
    res.end("Not Found");
    return;
  }

  // 获取或创建会话
  const sessionId = req.headers["mcp-session-id"] as string | undefined;
  let transport: StreamableHTTPServerTransport;

  if (sessionId && sessions.has(sessionId)) {
    transport = sessions.get(sessionId)!;
  } else {
    // 创建新的传输层实例
    transport = new StreamableHTTPServerTransport({
      sessionIdGenerator: () => randomUUID(),
      onsessioninitialized: async (id) => {
        console.log(`会话已初始化: ${id}`);
        sessions.set(id, transport);
      },
      onsessionclosed: async (id) => {
        console.log(`会话已关闭: ${id}`);
        sessions.delete(id);
      },
    });

    // 连接 MCP 服务器到传输层
    await mcpServer.connect(transport);
  }

  // 处理请求
  let body = "";
  req.on("data", (chunk) => {
    body += chunk.toString();
  });

  req.on("end", async () => {
    const parsedBody = body ? JSON.parse(body) : undefined;
    await transport.handleRequest(req, res, parsedBody);
  });
});

httpServer.listen(Number(PORT), () => {
  console.log(`MCP TypeScript Server 已启动`);
  console.log(`服务器地址: http://localhost:${PORT}`);
  console.log(`MCP 端点: http://localhost:${PORT}/mcp`);
  console.log(`健康检查: http://localhost:${PORT}/health`);
});
