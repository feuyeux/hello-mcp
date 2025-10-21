import argparse
import asyncio
import logging

from client import HelloClient

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


async def test_list_tools(port: int):
    """测试1: 列举Hello MCP工具"""
    logger.info("=== 测试1: 列举Hello MCP工具 ===")
    client = HelloClient(base_url=f"http://localhost:{port}")
    tools = await client.list_tools()
    print(f"\n列举到的工具:\n{tools}\n")


async def test_get_element_by_name(port: int):
    """测试2: 测试Hello MCP - 按名称查询"""
    logger.info("=== 测试2: 测试Hello MCP - 按名称查询 ===")
    client = HelloClient(base_url=f"http://localhost:{port}")
    result = await client.get_element("氢")
    print(f"查询氢元素结果: {result}\n")


async def test_get_element_by_position(port: int):
    """测试3: 测试MCP工具调用 - 按位置查询"""
    logger.info("=== 测试3: 测试MCP工具调用 - 按位置查询 ===")
    client = HelloClient(base_url=f"http://localhost:{port}")
    result = await client.get_element_by_position(6)
    print(f"查询原子序数为6的元素结果: {result}\n")


async def run_all_tests(port: int):
    """运行所有测试用例"""
    await test_list_tools(port)
    await test_get_element_by_name(port)
    await test_get_element_by_position(port)


def main():
    """Entry point for the test command"""
    parser = argparse.ArgumentParser(description="MCP Client Tests")
    parser.add_argument(
        "--port",
        type=int,
        default=9900,
        help="Port to connect to MCP server (default: 9900)",
    )
    args = parser.parse_args()

    logger.info(f"连接到 MCP 服务器: http://localhost:{args.port}")
    asyncio.run(run_all_tests(args.port))


if __name__ == "__main__":
    main()
