import argparse
import asyncio
import logging

from mcp import ClientSession
from mcp.client.streamable_http import streamablehttp_client

from client import HelloClient
from ollama_client import Message, OllamaClient

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


async def test_llm_with_mcp_tools(port: int):
    """测试 LLM 通过工具调用查询元素"""
    ollama_client = OllamaClient()
    hello_client = HelloClient(base_url=f"http://localhost:{port}")

    try:
        logger.info("=== 测试: LLM 通过工具调用查询元素 ===")

        # 获取可用工具
        async with streamablehttp_client(hello_client.endpoint) as (
            read_stream,
            write_stream,
            _,
        ):
            async with ClientSession(read_stream, write_stream) as session:
                await session.initialize()
                tools_result = await session.list_tools()

                # 转换工具格式为Ollama格式
                tools = []
                for tool in tools_result.tools:
                    tools.append(
                        {
                            "type": "function",
                            "function": {
                                "name": tool.name,
                                "description": tool.description,
                                "parameters": tool.inputSchema,
                            },
                        }
                    )

        # 构建消息
        messages = []
        query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量"
        messages.append(Message("user", query))

        # 第一次调用 LLM
        logger.info(f"第一次调用 LLM: {query}")
        response = await ollama_client.chat(messages, tools)

        logger.info(f"LLM 响应角色: {response.role}")
        logger.info(f"LLM 响应内容: {response.content}")

        # 检查是否有工具调用
        if response.has_tool_calls():
            logger.info(f"LLM 决定调用工具，工具数量: {len(response.tool_calls)}")

            # 执行工具调用
            for tool_call in response.tool_calls:
                logger.info(f"执行工具: {tool_call.name}")
                logger.info(f"工具参数: {tool_call.arguments}")

                tool_result = await ollama_client.execute_tool_call(tool_call, hello_client)
                logger.info(f"工具执行结果: {tool_result}")

                # 将工具结果添加到消息历史
                messages.append(Message("assistant", ""))
                messages.append(Message("tool", tool_result))

            # 第二次调用 LLM，让其基于工具结果生成最终答案
            logger.info("第二次调用 LLM，生成最终答案...")
            final_response = await ollama_client.chat(messages, tools)

            logger.info(f"最终答案: {final_response.content}")
            print(f"\n最终答案: {final_response.content}\n")
            logger.info("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息")

        else:
            logger.warning(f"LLM 没有调用工具，直接返回了答案: {response.content}")
            logger.info("这可能是因为 LLM 已经知道答案，或者不支持工具调用")

    except Exception as e:
        logger.error(f"测试失败: {e}")
        logger.info(
            "提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载\n"
            "启动命令: ollama serve\n"
            "下载模型: ollama pull qwen2.5:latest"
        )
    finally:
        await ollama_client.close()


def main():
    """Entry point for the Ollama integration test"""
    parser = argparse.ArgumentParser(description="Ollama MCP Integration Test")
    parser.add_argument(
        "--port",
        type=int,
        default=9900,
        help="Port to connect to MCP server (default: 9900)",
    )
    args = parser.parse_args()

    logger.info(f"连接到 MCP 服务器: http://localhost:{args.port}")
    asyncio.run(test_llm_with_mcp_tools(args.port))


if __name__ == "__main__":
    main()
