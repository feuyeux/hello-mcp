#!/usr/bin/env python3

import asyncio
import json
import logging
from typing import Dict, Any, Optional

import httpx

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("hello-mcp-http-client")

class MCPHttpClient:
    """MCP HTTP 客户端"""
    
    def __init__(self, base_url: str = "http://127.0.0.1:8000"):
        self.base_url = base_url
        self.session = None
        self.request_id = 0
    
    async def __aenter__(self):
        """异步上下文管理器入口"""
        self.session = httpx.AsyncClient(
            timeout=30.0,
            trust_env=False  # 不使用环境变量中的代理设置
        )
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """异步上下文管理器出口"""
        if self.session:
            await self.session.aclose()
    
    def _get_next_id(self) -> int:
        """获取下一个请求ID"""
        self.request_id += 1
        return self.request_id
    
    async def _send_request(self, method: str, params: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """发送HTTP请求到MCP服务器"""
        if not self.session:
            raise RuntimeError("客户端未初始化，请使用 async with")
        
        request_data = {
            "jsonrpc": "2.0",
            "id": self._get_next_id(),
            "method": method,
            "params": params or {}
        }
        
        logger.info(f"发送请求: {request_data}")
        
        try:
            response = await self.session.post(
                f"{self.base_url}/mcp",
                json=request_data,
                headers={"Content-Type": "application/json"}
            )
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"收到响应: {result}")
            
            return result
            
        except httpx.HTTPError as e:
            logger.error(f"HTTP 请求错误: {e}")
            raise
        except json.JSONDecodeError as e:
            logger.error(f"JSON 解析错误: {e}")
            raise
    
    async def list_tools(self) -> list:
        """列出可用工具"""
        response = await self._send_request("tools/list")
        
        if "error" in response:
            raise RuntimeError(f"服务器错误: {response['error']}")
        
        return response.get("result", {}).get("tools", [])
    
    async def call_tool(self, name: str, arguments: Dict[str, Any]) -> str:
        """调用工具"""
        response = await self._send_request("tools/call", {
            "name": name,
            "arguments": arguments
        })
        
        if "error" in response:
            raise RuntimeError(f"服务器错误: {response['error']}")
        
        content = response.get("result", {}).get("content", [])
        if content and len(content) > 0:
            return content[0].get("text", "")
        
        return ""
    
    async def get_element_by_name(self, name: str) -> str:
        """根据名称获取元素信息"""
        return await self.call_tool("get_element", {"name": name})
    
    async def get_element_by_position(self, position: int) -> str:
        """根据位置获取元素信息"""
        return await self.call_tool("get_element_by_position", {"position": position})
    
    async def test_connection(self) -> bool:
        """测试连接"""
        try:
            if not self.session:
                raise RuntimeError("客户端未初始化")
            
            response = await self.session.get(f"{self.base_url}/")
            response.raise_for_status()
            
            result = response.json()
            logger.info(f"服务器状态: {result}")
            
            return True
            
        except Exception as e:
            logger.error(f"连接测试失败: {e}")
            return False

class MCPStreamClient:
    """MCP 流式客户端"""
    
    def __init__(self, base_url: str = "http://127.0.0.1:8000"):
        self.base_url = base_url
        self.session = None
    
    async def __aenter__(self):
        """异步上下文管理器入口"""
        self.session = httpx.AsyncClient(
            timeout=None,
            trust_env=False  # 不使用环境变量中的代理设置
        )
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        """异步上下文管理器出口"""
        if self.session:
            await self.session.aclose()
    
    async def listen_stream(self):
        """监听服务器流式消息"""
        if not self.session:
            raise RuntimeError("客户端未初始化")
        
        try:
            async with self.session.stream(
                "GET",
                f"{self.base_url}/mcp/stream",
                headers={"Accept": "text/event-stream"}
            ) as response:
                response.raise_for_status()
                
                async for line in response.aiter_lines():
                    if line.startswith("data: "):
                        try:
                            data = json.loads(line[6:])  # 移除 "data: " 前缀
                            logger.info(f"收到流式消息: {data}")
                            yield data
                        except json.JSONDecodeError:
                            logger.warning(f"无法解析流式数据: {line}")
                            continue
        
        except Exception as e:
            logger.error(f"流式连接错误: {e}")
            raise

async def test_mcp_http_client():
    """测试MCP HTTP客户端功能"""
    print("=== 测试 MCP HTTP 客户端 ===")
    
    async with MCPHttpClient() as client:
        # 测试连接
        print("\n=== 测试连接 ===")
        if await client.test_connection():
            print("✅ 服务器连接成功")
        else:
            print("❌ 服务器连接失败")
            return
        
        try:
            # 列出工具
            print("\n=== 列出可用工具 ===")
            tools = await client.list_tools()
            print("可用工具:")
            for tool in tools:
                print(f"  - {tool['name']}: {tool['description']}")
            
            # 测试根据名称获取元素
            print("\n=== 测试根据名称获取元素 ===")
            result = await client.get_element_by_name("硅")
            print(f"硅元素信息: {result}")
            
            # 测试根据位置获取元素
            print("\n=== 测试根据位置获取元素 ===")
            result = await client.get_element_by_position(14)
            print(f"第14号元素信息: {result}")
            
            # 测试其他元素
            print("\n=== 测试其他元素 ===")
            result = await client.get_element_by_name("氢")
            print(f"氢元素信息: {result}")
            
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
        
        except Exception as e:
            print(f"测试过程中发生错误: {e}")

async def test_mcp_stream_client():
    """测试MCP流式客户端"""
    print("\n=== 测试 MCP 流式客户端 ===")
    
    async with MCPStreamClient() as stream_client:
        try:
            message_count = 0
            async for message in stream_client.listen_stream():
                message_count += 1
                print(f"消息 {message_count}: {message}")
                
                # 只监听前5个消息作为演示
                if message_count >= 5:
                    break
        
        except Exception as e:
            print(f"流式客户端错误: {e}")

async def main():
    """主函数"""
    print("启动 MCP HTTP 客户端测试...")
    
    # 等待服务器启动
    print("等待服务器启动...")
    await asyncio.sleep(2)
    
    # 测试HTTP客户端
    await test_mcp_http_client()
    
    # 测试流式客户端
    await test_mcp_stream_client()

if __name__ == "__main__":
    asyncio.run(main())
