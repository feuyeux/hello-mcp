#!/usr/bin/env node

import express from 'express';
import { z } from 'zod';
import { periodicTable } from './periodic-table.js';

// MCP Tool schema
const ToolCallSchema = z.object({
  name: z.string(),
  arguments: z.record(z.any())
});

const GetElementArgsSchema = z.object({
  symbol: z.string()
});

const GetElementByPositionArgsSchema = z.object({
  position: z.number()
});

class McpServer {
  private tools = [
    {
      name: "get_element",
      description: "Get information about a chemical element by its symbol",
      inputSchema: {
        type: "object",
        properties: {
          symbol: {
            type: "string",
            description: "Chemical symbol of the element (e.g., H, He, Li)"
          }
        },
        required: ["symbol"]
      }
    },
    {
      name: "get_element_by_position",
      description: "Get information about a chemical element by its atomic number",
      inputSchema: {
        type: "object",
        properties: {
          position: {
            type: "number",
            description: "Atomic number of the element (1-118)"
          }
        },
        required: ["position"]
      }
    }
  ];

  listTools() {
    console.log('Listing available tools');
    return { tools: this.tools };
  }

  async callTool(toolCall: z.infer<typeof ToolCallSchema>) {
    console.log(`Calling tool: ${toolCall.name}`);
    
    try {
      switch (toolCall.name) {
        case "get_element":
          return await this.handleGetElement(toolCall.arguments);
        case "get_element_by_position":
          return await this.handleGetElementByPosition(toolCall.arguments);
        default:
          return {
            content: [{ type: "text", text: `Unknown tool: ${toolCall.name}` }],
            isError: true
          };
      }
    } catch (error) {
      console.error(`Error calling tool ${toolCall.name}:`, error);
      return {
        content: [{ type: "text", text: `Error: ${error instanceof Error ? error.message : 'Unknown error'}` }],
        isError: true
      };
    }
  }

  private async handleGetElement(args: any) {
    const parsed = GetElementArgsSchema.safeParse(args);
    if (!parsed.success) {
      return {
        content: [{ type: "text", text: "Missing required parameter: symbol" }],
        isError: true
      };
    }

    const element = periodicTable.getElement(parsed.data.symbol);
    if (!element) {
      return {
        content: [{ type: "text", text: `Element not found: ${parsed.data.symbol}` }],
        isError: true
      };
    }

    return {
      content: [{ type: "text", text: JSON.stringify(element, null, 2) }]
    };
  }

  private async handleGetElementByPosition(args: any) {
    const parsed = GetElementByPositionArgsSchema.safeParse(args);
    if (!parsed.success) {
      return {
        content: [{ type: "text", text: "Missing required parameter: position" }],
        isError: true
      };
    }

    const element = periodicTable.getElementByPosition(parsed.data.position);
    if (!element) {
      return {
        content: [{ type: "text", text: `Element not found at position: ${parsed.data.position}` }],
        isError: true
      };
    }

    return {
      content: [{ type: "text", text: JSON.stringify(element, null, 2) }]
    };
  }

  async start() {
    const app = express();
    app.use(express.json());

    app.get('/tools', (req, res) => {
      res.json(this.listTools());
    });

    app.post('/tools/call', async (req, res) => {
      const result = await this.callTool(req.body);
      res.json(result);
    });

    const port = process.env.PORT || 3000;
    app.listen(port, () => {
      console.log(`MCP Server started on port ${port}`);
      console.log(`Available tools: ${this.tools.length}`);
      this.tools.forEach(tool => {
        console.log(`- ${tool.name}: ${tool.description}`);
      });
    });
  }
}

const server = new McpServer();
server.start().catch(console.error);
