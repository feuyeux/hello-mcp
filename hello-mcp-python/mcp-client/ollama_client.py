import asyncio
import json
import logging
from typing import Any, Dict, List, Optional

import httpx

from client import HelloClient

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


class Message:
    """消息类"""

    def __init__(self, role: str, content: str):
        self.role = role
        self.content = content

    def to_dict(self) -> Dict[str, str]:
        return {"role": self.role, "content": self.content}


class ToolCall:
    """工具调用类"""

    def __init__(self, name: str, arguments: Dict[str, Any]):
        self.name = name
        self.arguments = arguments


class ChatResponse:
    """聊天响应类"""

    def __init__(self, role: str, content: str, tool_calls: Optional[List[ToolCall]] = None):
        self.role = role
        self.content = content
        self.tool_calls = tool_calls or []

    def has_tool_calls(self) -> bool:
        return len(self.tool_calls) > 0


class OllamaClient:
    """
    Ollama 客户端
    
    用于与 Ollama API 交互，支持工具调用
    """

    def __init__(self, base_url: str = "http://localhost:11434", model: str = "qwen2.5:latest"):
        self.base_url = base_url
        self.model = model
        self.http_client = httpx.AsyncClient(timeout=300.0)

    async def chat(self, messages: List[Message], tools: List[Dict[str, Any]]) -> ChatResponse:
        """
        发送聊天请求
        
        Args:
            messages: 消息列表
            tools: 可用工具列表
            
        Returns:
            响应消息
        """
        try:
            logger.info(f"发送聊天请求到 Ollama: model={self.model}, messages={len(messages)}")

            # 构建请求体
            request_body = {
                "model": self.model,
                "stream": False,
                "messages": [msg.to_dict() for msg in messages],
                "tools": tools,
            }

            logger.debug(f"请求 JSON: {json.dumps(request_body, ensure_ascii=False, indent=2)}")

            # 发送请求
            response = await self.http_client.post(
                f"{self.base_url}/api/chat",
                json=request_body,
                headers={"Content-Type": "application/json"},
            )

            logger.debug(f"响应状态: {response.status_code}")
            logger.debug(f"响应内容: {response.text}")

            if response.status_code != 200:
                raise RuntimeError(f"Ollama API 请求失败: {response.status_code}")

            response_json = response.json()
            message_node = response_json.get("message", {})

            role = message_node.get("role", "assistant")
            content = message_node.get("content", "")

            # 解析工具调用
            tool_calls = []
            if "tool_calls" in message_node:
                for tool_call_node in message_node["tool_calls"]:
                    function_node = tool_call_node.get("function", {})
                    tool_call = ToolCall(
                        name=function_node.get("name", ""),
                        arguments=function_node.get("arguments", {}),
                    )
                    tool_calls.append(tool_call)

            return ChatResponse(role=role, content=content, tool_calls=tool_calls)

        except Exception as e:
            logger.error(f"Ollama 请求失败: {e}")
            raise RuntimeError(f"Ollama 请求失败: {e}")

    async def execute_tool_call(self, tool_call: ToolCall, hello_client: HelloClient) -> str:
        """
        执行工具调用
        
        Args:
            tool_call: 工具调用信息
            hello_client: MCP客户端实例
            
        Returns:
            工具执行结果
        """
        try:
            logger.info(f"执行工具调用: {tool_call.name}, 参数: {tool_call.arguments}")

            if tool_call.name == "get_element":
                name = tool_call.arguments.get("name")
                result = await hello_client.get_element(name)
            elif tool_call.name == "get_element_by_position":
                position = tool_call.arguments.get("position")
                if isinstance(position, float):
                    position = int(position)
                result = await hello_client.get_element_by_position(position)
            else:
                result = json.dumps({"error": f"未知工具: {tool_call.name}"}, ensure_ascii=False)

            logger.info(f"工具调用结果: {result}")
            return result

        except Exception as e:
            logger.error(f"工具调用失败: {e}")
            return json.dumps({"error": str(e)}, ensure_ascii=False)

    async def close(self):
        """关闭HTTP客户端"""
        await self.http_client.aclose()
