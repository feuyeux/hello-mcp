#!/usr/bin/env python3

import asyncio
import subprocess
import sys
from mcp.client.session import ClientSession
from mcp.client.stdio import stdio_client

async def test_mcp_client():
    """
    测试MCP客户端
    """
    # 启动服务器进程
    server_process = subprocess.Popen(
        [sys.executable, "src/server.py"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )

    async with stdio_client(server_process.stdout, server_process.stdin) as (read, write):
        async with ClientSession(read, write) as session:
            # 初始化
            await session.initialize()

            # 测试获取工具列表
            print("=== 测试获取工具列表 ===")
            tools = await session.list_tools()
            print("可用工具:", [tool.name for tool in tools.tools])

            # 测试根据名称获取元素
            print("\n=== 测试根据名称获取元素 ===")
            result1 = await session.call_tool("get_element", {"name": "硅"})
            print("硅元素信息:", result1.content[0].text if result1.content else "无内容")

            # 测试根据位置获取元素
            print("\n=== 测试根据位置获取元素 ===")
            result2 = await session.call_tool("get_element_by_position", {"position": 14})
            print("第14号元素信息:", result2.content[0].text if result2.content else "无内容")

    # 清理进程
    server_process.terminate()
    server_process.wait()

if __name__ == "__main__":
    asyncio.run(test_mcp_client())
