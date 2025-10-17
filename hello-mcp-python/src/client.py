#!/usr/bin/env python3
"""
MCP 标准客户端实现

使用 MCP SDK 的 SSE (Server-Sent Events) HTTP 传输层，符合 MCP 协议规范。
"""

import asyncio
import logging
from mcp import ClientSession
from mcp.client.sse import sse_client

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("hello-mcp-client")

class HelloMCPClient:
    """MCP 客户端封装类"""
    
    def __init__(self, base_url: str = "http://127.0.0.1:8000"):
        self.base_url = base_url
        self.session: ClientSession | None = None
        self.exit_stack = None
    
    async def connect(self):
        """
        连接到 MCP 服务器
        """
        logger.info(f"连接到 MCP 服务器: {self.base_url}")
        
        # 使用 SSE 传输层连接
        sse_transport = sse_client(
            url=f"{self.base_url}/sse",
            headers={"Content-Type": "application/json"}
        )
        
        self.exit_stack = await sse_transport.__aenter__()
        read_stream, write_stream = self.exit_stack
        
        # 创建客户端会话
        self.session = ClientSession(read_stream, write_stream)
        
        # 初始化连接
        await self.session.initialize()
        logger.info("MCP 客户端初始化成功")
    
    async def list_tools(self) -> list:
        """列出可用工具"""
        if not self.session:
            raise RuntimeError("客户端未连接")
        
        logger.info("请求工具列表")
        result = await self.session.list_tools()
        return result.tools
    
    async def call_tool(self, name: str, arguments: dict) -> str:
        """
        调用工具
        
        Args:
            name: 工具名称
            arguments: 工具参数
            
        Returns:
            工具返回的文本内容
        """
        if not self.session:
            raise RuntimeError("客户端未连接")
        
        logger.info(f"调用工具: {name}")
        result = await self.session.call_tool(name, arguments)
        
        # 提取文本内容
        if result.content and len(result.content) > 0:
            return result.content[0].text
        return ""
    
    async def get_element_by_name(self, name: str) -> str:
        """根据名称获取元素信息"""
        return await self.call_tool("get_element", {"name": name})
    
    async def get_element_by_position(self, position: int) -> str:
        """根据位置获取元素信息"""
        return await self.call_tool("get_element_by_position", {"position": position})
    
    async def close(self):
        """关闭连接"""
        if self.exit_stack:
            await self.exit_stack.__aexit__(None, None, None)
        logger.info("MCP 客户端已关闭")

async def test_mcp_client():
    """测试 MCP 客户端功能"""
    print("=== 测试 MCP 客户端 ===")
    
    client = HelloMCPClient()
    
    try:
        # 连接到服务器
        await client.connect()
        
        # 列出工具
        print("\n=== 列出可用工具 ===")
        tools = await client.list_tools()
        print("可用工具:")
        for tool in tools:
            print(f"  - {tool.name}: {tool.description}")
        
        # 测试根据名称获取元素
        print("\n=== 测试根据名称获取元素 ===")
        result = await client.get_element_by_name("硅")
        print(f"硅元素信息: {result}")
        
        result = await client.get_element_by_name("氢")
        print(f"氢元素信息: {result}")
        
        # 测试根据位置获取元素
        print("\n=== 测试根据位置获取元素 ===")
        result = await client.get_element_by_position(14)
        print(f"第14号元素信息: {result}")
        
        result = await client.get_element_by_position(6)
        print(f"第6号元素信息: {result}")
        
        # 测试错误情况
        print("\n=== 测试错误情况 ===")
        try:
            result = await client.get_element_by_name("不存在的元素")
            print(f"不存在元素: {result}")
        except Exception as e:
            print(f"不存在元素错误: {e}")
        
        try:
            result = await client.get_element_by_position(999)
            print(f"无效位置: {result}")
        except Exception as e:
            print(f"无效位置错误: {e}")
    
    finally:
        await client.close()

async def main():
    """主函数"""
    await test_mcp_client()

if __name__ == "__main__":
    asyncio.run(main())
