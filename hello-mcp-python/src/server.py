#!/usr/bin/env python3
"""
MCP 标准服务器实现

使用 MCP SDK 的 SSE (Server-Sent Events) HTTP 传输层，符合 MCP 协议规范。
提供元素周期表查询工具。
"""

import asyncio
import logging
import uvicorn
from mcp.server import Server
from mcp.server.sse import SseServerTransport
from mcp.types import (
    Tool,
    TextContent,
)
from starlette.applications import Starlette
from starlette.routing import Route
from starlette.requests import Request
from starlette.responses import Response
from periodic_table import periodic_table

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("hello-mcp-server")

# 创建 MCP 服务器实例
mcp_server = Server("hello-mcp-python")

@mcp_server.list_tools()
async def list_tools() -> list[Tool]:
    """
    列出所有可用工具
    """
    logger.info("处理 tools/list 请求")
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

@mcp_server.call_tool()
async def call_tool(name: str, arguments: dict) -> list[TextContent]:
    """
    处理工具调用
    """
    logger.info(f"处理 tools/call 请求: {name}")
    
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

# 创建 SSE 传输层
sse_transport = SseServerTransport("/messages")

async def handle_sse(request: Request) -> Response:
    """处理 SSE 连接"""
    logger.info("新的 SSE 连接建立")
    async with sse_transport.connect_sse(
        request.scope,
        request.receive,
        request._send
    ) as streams:
        await mcp_server.run(
            streams[0],
            streams[1],
            mcp_server.create_initialization_options()
        )
    return Response()

async def handle_messages(request: Request) -> Response:
    """处理消息端点"""
    logger.info("收到消息请求")
    return await sse_transport.handle_post_message(request)

async def handle_health(request: Request) -> Response:
    """健康检查端点"""
    return Response(
        content='{"status": "UP", "server": "hello-mcp-python"}',
        media_type="application/json"
    )

# 创建 Starlette 应用
app = Starlette(
    debug=True,
    routes=[
        Route("/health", handle_health, methods=["GET"]),
        Route("/sse", handle_sse, methods=["GET"]),
        Route("/messages", handle_messages, methods=["POST"]),
    ],
)

def main():
    """主函数"""
    logger.info("启动 MCP 服务器...")
    logger.info("使用 SSE HTTP 传输层")
    logger.info("服务器地址: http://127.0.0.1:8000")
    logger.info("SSE 端点: http://127.0.0.1:8000/sse")
    logger.info("消息端点: http://127.0.0.1:8000/messages")
    
    # 运行服务器
    uvicorn.run(
        app,
        host="127.0.0.1",
        port=8000,
        log_level="info"
    )

if __name__ == "__main__":
    main()
