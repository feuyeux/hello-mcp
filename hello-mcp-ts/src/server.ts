#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import { periodicTable } from "./periodic-table.js";

const server = new Server(
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
server.setRequestHandler(ListToolsRequestSchema, async () => {
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
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

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

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("Hello MCP TypeScript Server running on stdio");
}

main().catch((error) => {
  console.error("Server error:", error);
  process.exit(1);
});
