import contextlib
import logging
from collections.abc import AsyncIterator
from typing import Any

import anyio
import click
import mcp.types as types
from mcp.server.lowlevel import Server
from mcp.server.streamable_http_manager import StreamableHTTPSessionManager
from mcp.types import TextContent, Tool
from pydantic import AnyUrl
from starlette.applications import Starlette
from starlette.middleware.cors import CORSMiddleware
from starlette.routing import Mount
from starlette.types import Receive, Scope, Send

from periodic_table import periodic_table

# Configure logging
logger = logging.getLogger(__name__)

# https://github.com/modelcontextprotocol/python-sdk/tree/main/examples/servers/simple-streamablehttp
@click.command()
@click.option("--port", default=9900, help="Port to listen on for HTTP")
@click.option(
    "--log-level",
    default="INFO",
    help="Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)",
)
@click.option(
    "--json-response",
    is_flag=True,
    default=False,
    help="Enable JSON responses instead of SSE streams",
)
def main(
    port: int,
    log_level: str,
    json_response: bool,
) -> int:
    # Configure logging
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    )

    app = Server("mcp-server")

    @app.call_tool()
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

    @app.list_tools()
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

    # Create the session manager with our app and event store
    session_manager = StreamableHTTPSessionManager(
        app=app,
        json_response=json_response,
    )

    # ASGI handler for streamable HTTP connections
    async def handle_streamable_http(scope: Scope, receive: Receive, send: Send) -> None:
        await session_manager.handle_request(scope, receive, send)

    @contextlib.asynccontextmanager
    async def lifespan(app: Starlette) -> AsyncIterator[None]:
        """Context manager for managing session manager lifecycle."""
        async with session_manager.run():
            logger.info("Application started with StreamableHTTP session manager!")
            try:
                yield
            finally:
                logger.info("Application shutting down...")

    # Create an ASGI application using the transport
    starlette_app = Starlette(
        debug=True,
        routes=[
            Mount("/mcp/", app=handle_streamable_http),
        ],
        lifespan=lifespan,
    )

    # Wrap ASGI application with CORS middleware to expose Mcp-Session-Id header
    # for browser-based clients (ensures 500 errors get proper CORS headers)
    starlette_app = CORSMiddleware(
        starlette_app,
        allow_origins=["*"],  # Allow all origins - adjust as needed for production
        allow_methods=["GET", "POST", "DELETE"],  # MCP streamable HTTP methods
        expose_headers=["Mcp-Session-Id"],
    )

    import uvicorn

    uvicorn.run(starlette_app, host="127.0.0.1", port=port)

    return 0
