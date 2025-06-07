#!/usr/bin/env python3

import asyncio
import logging
from mcp.server.models import InitializationOptions
from mcp.server import NotificationOptions, Server
from mcp.types import (
    CallToolRequest,
    CallToolResult,
    ListToolsRequest,
    TextContent,
    Tool,
)
from periodic_table import periodic_table

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("hello-mcp-server")

# 创建服务器实例
server = Server("hello-mcp-python")

@server.list_tools()
async def handle_list_tools() -> list[Tool]:
    """
    列出所有可用工具
    """
    return [
        Tool(
            name="get_element",
            description="根据元素名称获取元素周期表元素信息",
            inputSchema={
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "元素的中文名称，如'氢'、'氦'等",
                    }
                },
                "required": ["name"],
            },
        ),
        Tool(
            name="get_element_by_position",
            description="根据元素在周期表中的位置（原子序数）查询元素信息",
            inputSchema={
                "type": "object",
                "properties": {
                    "position": {
                        "type": "number",
                        "description": "元素的原子序数，范围从1到118",
                    }
                },
                "required": ["position"],
            },
        ),
    ]

@server.call_tool()
async def handle_call_tool(name: str, arguments: dict) -> list[TextContent]:
    """
    处理工具调用
    """
    if name == "get_element":
        element_name = arguments.get("name")
        if not element_name:
            return [TextContent(type="text", text="元素名称不能为空")]
        
        # 查找元素
        element = next((el for el in periodic_table if el.name == element_name), None)
        if not element:
            return [TextContent(type="text", text="元素不存在")]
        
        result_text = (
            f"元素名称: {element.name} ({element.pronunciation}, {element.english_name}), "
            f"原子序数: {element.atomic_number}, 符号: {element.symbol}, "
            f"相对原子质量: {element.atomic_weight:.3f}, 周期: {element.period}, "
            f"族: {element.group}"
        )
        return [TextContent(type="text", text=result_text)]
    
    elif name == "get_element_by_position":
        position = arguments.get("position")
        if position is None:
            return [TextContent(type="text", text="位置参数不能为空")]
        
        if not isinstance(position, int) or position < 1 or position > 118:
            return [TextContent(type="text", text="原子序数必须在1-118之间")]
        
        # 查找元素
        element = next((el for el in periodic_table if el.atomic_number == position), None)
        if not element:
            return [TextContent(type="text", text="元素不存在")]
        
        result_text = (
            f"元素名称: {element.name} ({element.pronunciation}, {element.english_name}), "
            f"原子序数: {element.atomic_number}, 符号: {element.symbol}, "
            f"相对原子质量: {element.atomic_weight:.3f}, 周期: {element.period}, "
            f"族: {element.group}"
        )
        return [TextContent(type="text", text=result_text)]
    
    else:
        raise ValueError(f"未知工具: {name}")

async def main():
    # 运行服务器使用标准输入输出传输
    from mcp.server.stdio import stdio_server

    async with stdio_server() as (read_stream, write_stream):
        await server.run(
            read_stream,
            write_stream,
            InitializationOptions(
                server_name="hello-mcp-python",
                server_version="1.0.0",
                capabilities=server.get_capabilities(
                    notification_options=NotificationOptions(),
                    experimental_capabilities={},
                ),
            ),
        )

if __name__ == "__main__":
    asyncio.run(main())
