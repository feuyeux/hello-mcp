#!/usr/bin/env python3
"""
MCP Streamable HTTP 通信演示脚本

这个脚本详细展示了 MCP 通过 Streamable HTTP 进行通信的完整过程，
包括连接建立、协议握手、工具调用和流式消息处理。
"""

import asyncio
import json
import logging
import time
from typing import Dict, Any

import httpx

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s',
    datefmt='%H:%M:%S'
)
logger = logging.getLogger("mcp-demo")

class MCPStreamableHTTPDemo:
    """MCP Streamable HTTP 通信演示类"""
    
    def __init__(self, base_url: str = "http://127.0.0.1:8000"):
        self.base_url = base_url
        self.session = None
        self.request_id = 0
    
    async def __aenter__(self):
        self.session = httpx.AsyncClient(timeout=30.0, trust_env=False)
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session:
            await self.session.aclose()
    
    def _get_next_id(self) -> int:
        self.request_id += 1
        return self.request_id
    
    def _print_section(self, title: str, content: str = ""):
        """打印格式化的章节标题"""
        print(f"\n{'='*60}")
        print(f"🔍 {title}")
        print('='*60)
        if content:
            print(content)
    
    def _print_http_request(self, method: str, url: str, headers: Dict = None, data: str = None):
        """打印 HTTP 请求格式"""
        print(f"\n📤 HTTP Request:")
        print(f"{method} {url}")
        if headers:
            for key, value in headers.items():
                print(f"{key}: {value}")
        if data:
            print(f"\nRequest Body:")
            try:
                formatted = json.dumps(json.loads(data), indent=2, ensure_ascii=False)
                print(formatted)
            except:
                print(data)
    
    def _print_http_response(self, status: int, headers: Dict = None, data: str = None):
        """打印 HTTP 响应格式"""
        print(f"\n📥 HTTP Response:")
        print(f"Status: {status}")
        if headers:
            for key, value in headers.items():
                print(f"{key}: {value}")
        if data:
            print(f"\nResponse Body:")
            try:
                formatted = json.dumps(json.loads(data), indent=2, ensure_ascii=False)
                print(formatted)
            except:
                print(data)
    
    async def demo_health_check(self):
        """演示: 健康检查"""
        self._print_section("步骤 1: 服务器健康检查", 
                          "首先检查服务器是否正常运行")
        
        url = f"{self.base_url}/"
        self._print_http_request("GET", url)
        
        try:
            response = await self.session.get(url)
            self._print_http_response(
                response.status_code,
                dict(response.headers),
                response.text
            )
            
            if response.status_code == 200:
                print("✅ 服务器健康检查通过")
                return True
            else:
                print("❌ 服务器健康检查失败")
                return False
                
        except Exception as e:
            print(f"❌ 连接失败: {e}")
            return False
    
    async def demo_list_tools(self):
        """演示: 列出可用工具"""
        self._print_section("步骤 2: 获取可用工具列表", 
                          "使用 MCP 协议查询服务器提供的工具")
        
        request_data = {
            "jsonrpc": "2.0",
            "id": self._get_next_id(),
            "method": "tools/list",
            "params": {}
        }
        
        url = f"{self.base_url}/mcp"
        headers = {"Content-Type": "application/json"}
        data = json.dumps(request_data, ensure_ascii=False)
        
        self._print_http_request("POST", url, headers, data)
        
        try:
            response = await self.session.post(url, json=request_data, headers=headers)
            self._print_http_response(
                response.status_code,
                dict(response.headers),
                response.text
            )
            
            if response.status_code == 200:
                result = response.json()
                tools = result.get("result", {}).get("tools", [])
                print(f"✅ 成功获取 {len(tools)} 个工具:")
                for i, tool in enumerate(tools, 1):
                    print(f"   {i}. {tool['name']}: {tool['description']}")
                return tools
            else:
                print("❌ 获取工具列表失败")
                return []
                
        except Exception as e:
            print(f"❌ 请求失败: {e}")
            return []
    
    async def demo_call_tool(self, tool_name: str, arguments: Dict[str, Any]):
        """演示: 调用工具"""
        self._print_section(f"步骤 3: 调用工具 '{tool_name}'", 
                          f"使用参数: {arguments}")
        
        request_data = {
            "jsonrpc": "2.0",
            "id": self._get_next_id(),
            "method": "tools/call",
            "params": {
                "name": tool_name,
                "arguments": arguments
            }
        }
        
        url = f"{self.base_url}/mcp"
        headers = {"Content-Type": "application/json"}
        data = json.dumps(request_data, ensure_ascii=False)
        
        self._print_http_request("POST", url, headers, data)
        
        try:
            response = await self.session.post(url, json=request_data, headers=headers)
            self._print_http_response(
                response.status_code,
                dict(response.headers),
                response.text
            )
            
            if response.status_code == 200:
                result = response.json()
                content = result.get("result", {}).get("content", [])
                if content:
                    print("✅ 工具调用成功，结果:")
                    for item in content:
                        print(f"   📄 {item.get('text', '')}")
                return result
            else:
                print("❌ 工具调用失败")
                return None
                
        except Exception as e:
            print(f"❌ 请求失败: {e}")
            return None
    
    async def demo_stream_connection(self, duration: int = 10):
        """演示: 流式连接"""
        self._print_section("步骤 4: 建立流式连接", 
                          f"通过 Server-Sent Events 监听服务器消息 ({duration}秒)")
        
        url = f"{self.base_url}/mcp/stream"
        headers = {"Accept": "text/event-stream"}
        
        self._print_http_request("GET", url, headers)
        
        try:
            message_count = 0
            start_time = time.time()
            
            async with self.session.stream("GET", url, headers=headers) as response:
                print(f"\n📥 Stream Response:")
                print(f"Status: {response.status_code}")
                print("Content-Type: text/event-stream")
                print("Connection: keep-alive")
                print("\n🔄 流式消息:")
                
                async for line in response.aiter_lines():
                    if time.time() - start_time > duration:
                        break
                        
                    if line.startswith("data: "):
                        try:
                            data = json.loads(line[6:])
                            message_count += 1
                            timestamp = time.strftime("%H:%M:%S")
                            print(f"   [{timestamp}] 消息 {message_count}: {data.get('method', 'unknown')}")
                            
                            if data.get("method") == "notifications/initialized":
                                print(f"      🎯 服务器初始化完成")
                                capabilities = data.get("params", {}).get("capabilities", {})
                                print(f"      📋 服务器能力: {list(capabilities.keys())}")
                            elif data.get("method") == "notifications/ping":
                                print(f"      💓 心跳消息")
                                
                        except json.JSONDecodeError:
                            print(f"   [?] 无法解析的消息: {line}")
            
            print(f"✅ 流式连接结束，共收到 {message_count} 条消息")
            
        except Exception as e:
            print(f"❌ 流式连接失败: {e}")
    
    async def run_complete_demo(self):
        """运行完整的演示"""
        print("🚀 MCP Streamable HTTP 通信演示")
        print("==================================")
        print("本演示将展示 MCP 通过 HTTP 进行通信的完整过程")
        
        # 1. 健康检查
        if not await self.demo_health_check():
            print("\n❌ 服务器不可用，演示结束")
            return
        
        # 2. 获取工具列表
        tools = await self.demo_list_tools()
        if not tools:
            print("\n❌ 无法获取工具列表，演示结束")
            return
        
        # 3. 调用工具示例
        demo_calls = [
            ("get_element", {"name": "氢"}),
            ("get_element", {"name": "硅"}),
            ("get_element_by_position", {"position": 6}),
            ("get_element", {"name": "不存在的元素"})  # 错误示例
        ]
        
        for tool_name, arguments in demo_calls:
            await self.demo_call_tool(tool_name, arguments)
            await asyncio.sleep(1)  # 间隔演示
        
        # 4. 流式连接演示
        await self.demo_stream_connection(duration=5)
        
        # 总结
        self._print_section("演示完成", 
                          "🎉 MCP Streamable HTTP 通信演示结束")
        print("主要特性:")
        print("  ✅ RESTful API 设计")
        print("  ✅ JSON-RPC 2.0 协议")
        print("  ✅ Server-Sent Events 流式通信")
        print("  ✅ 异步并发处理")
        print("  ✅ 完整的错误处理")
        print("  ✅ 实时状态监控")

async def main():
    """主函数"""
    print("等待服务器启动...")
    await asyncio.sleep(2)
    
    async with MCPStreamableHTTPDemo() as demo:
        await demo.run_complete_demo()

if __name__ == "__main__":
    asyncio.run(main())
