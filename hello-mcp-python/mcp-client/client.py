import asyncio
import logging

from mcp import ClientSession
from mcp.client.streamable_http import streamablehttp_client

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


class HelloClient:
    """
    HelloClient类
    
    此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。
    使用 HTTP 客户端连接到 StreamableHTTP 服务端。
    """

    def __init__(self, base_url: str = "http://localhost:9900"):
        self.base_url = base_url
        self.endpoint = f"{base_url}/mcp"

    async def list_tools(self) -> str:
        """列举所有可用工具"""
        async with streamablehttp_client(self.endpoint) as (
            read_stream,
            write_stream,
            _,
        ):
            async with ClientSession(read_stream, write_stream) as session:
                await session.initialize()
                result = await session.list_tools()

                tools_list = []
                for tool in result.tools:
                    tools_list.append(
                        f"工具名称: {tool.name}, 描述: {tool.description}"
                    )

                tools_str = "\n".join(tools_list)
                logger.info(f"列举工具成功:\n{tools_str}")
                return tools_str

    async def get_element(self, name: str) -> str:
        """根据元素名称查询元素信息"""
        logger.info(f"查询元素: {name}")
        async with streamablehttp_client(self.endpoint) as (
            read_stream,
            write_stream,
            _,
        ):
            async with ClientSession(read_stream, write_stream) as session:
                await session.initialize()
                result = await session.call_tool("get_element", arguments={"name": name})

                content = result.content[0].text if result.content else ""
                logger.info(f"查询元素 {name} 成功: {content}")
                return content

    async def get_element_by_position(self, position: int) -> str:
        """根据原子序数查询元素信息"""
        logger.info(f"查询位置元素: {position}")
        async with streamablehttp_client(self.endpoint) as (
            read_stream,
            write_stream,
            _,
        ):
            async with ClientSession(read_stream, write_stream) as session:
                await session.initialize()
                result = await session.call_tool(
                    "get_element_by_position", arguments={"position": position}
                )

                content = result.content[0].text if result.content else ""
                logger.info(f"查询位置元素 {position} 成功: {content}")
                return content
