#!/usr/bin/env python3

import asyncio
import logging
from contextlib import asynccontextmanager
from typing import Dict, Any

import uvicorn
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import StreamingResponse
from mcp.server import Server
from mcp.server.models import InitializationOptions
from mcp.server import NotificationOptions
from mcp.types import (
    CallToolRequest,
    CallToolResult,
    ListToolsRequest,
    TextContent,
    Tool,
)
from periodic_table import periodic_table

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("hello-mcp-http-server")

# 创建 MCP 服务器实例
mcp_server = Server("hello-mcp-python-http")

# 全局变量存储服务器状态
server_initialized = False

@mcp_server.list_tools()
async def handle_list_tools() -> list[Tool]:
    """
    列出所有可用工具
    """
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
async def handle_call_tool(name: str, arguments: dict) -> list[TextContent]:
    """
    处理工具调用
    """
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

# HTTP 流处理类
class MCPHttpStream:
    def __init__(self):
        self.request_queue = asyncio.Queue()
        self.response_queue = asyncio.Queue()
        self.running = False
    
    async def start_processing(self):
        """开始处理请求"""
        self.running = True
        
        # 不需要手动初始化MCP服务器，它会在处理请求时自动初始化
        
        while self.running:
            try:
                # 从队列中获取请求
                request_data = await asyncio.wait_for(self.request_queue.get(), timeout=1.0)
                
                # 处理请求
                response = await self.process_request(request_data)
                
                # 将响应放入响应队列
                await self.response_queue.put(response)
                
            except asyncio.TimeoutError:
                continue
            except Exception as e:
                logger.error(f"处理请求时出错: {e}")
                await self.response_queue.put({"error": str(e)})
    
    async def process_request(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """处理单个请求"""
        try:
            method = request_data.get("method")
            params = request_data.get("params", {})
            
            if method == "tools/list":
                tools = await handle_list_tools()
                return {
                    "jsonrpc": "2.0",
                    "id": request_data.get("id"),
                    "result": {
                        "tools": [tool.model_dump() for tool in tools]
                    }
                }
            
            elif method == "tools/call":
                tool_name = params.get("name")
                arguments = params.get("arguments", {})
                
                result = await handle_call_tool(tool_name, arguments)
                return {
                    "jsonrpc": "2.0",
                    "id": request_data.get("id"),
                    "result": {
                        "content": [content.model_dump() for content in result]
                    }
                }
            
            else:
                return {
                    "jsonrpc": "2.0",
                    "id": request_data.get("id"),
                    "error": {
                        "code": -32601,
                        "message": f"未知方法: {method}"
                    }
                }
                
        except Exception as e:
            return {
                "jsonrpc": "2.0",
                "id": request_data.get("id"),
                "error": {
                    "code": -32603,
                    "message": f"内部错误: {str(e)}"
                }
            }
    
    async def add_request(self, request_data: Dict[str, Any]):
        """添加请求到队列"""
        await self.request_queue.put(request_data)
    
    async def get_response(self) -> Dict[str, Any]:
        """获取响应"""
        return await self.response_queue.get()
    
    def stop(self):
        """停止处理"""
        self.running = False

# 全局流处理实例
mcp_stream = MCPHttpStream()

@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    # 启动时
    logger.info("启动 MCP HTTP 服务器...")
    
    # 启动流处理
    processing_task = asyncio.create_task(mcp_stream.start_processing())
    
    yield
    
    # 关闭时
    logger.info("关闭 MCP HTTP 服务器...")
    mcp_stream.stop()
    processing_task.cancel()
    try:
        await processing_task
    except asyncio.CancelledError:
        pass

# 创建 FastAPI 应用
app = FastAPI(
    title="MCP HTTP Server",
    description="Model Context Protocol HTTP Server for Periodic Table",
    version="1.0.0",
    lifespan=lifespan
)

@app.get("/")
async def root():
    """根路径"""
    return {"message": "MCP HTTP Server is running", "version": "1.0.0"}

@app.post("/mcp")
async def handle_mcp_request(request: Request):
    """处理 MCP 请求"""
    try:
        # 获取请求数据
        request_data = await request.json()
        logger.info(f"收到请求: {request_data}")
        
        # 添加到处理队列
        await mcp_stream.add_request(request_data)
        
        # 获取响应
        response = await mcp_stream.get_response()
        logger.info(f"发送响应: {response}")
        
        return response
        
    except Exception as e:
        logger.error(f"处理请求时出错: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/mcp/stream")
async def handle_mcp_stream():
    """处理 MCP 流式请求"""
    import json
    
    async def generate_responses():
        """生成流式响应"""
        # 发送服务器信息
        server_info = {
            "jsonrpc": "2.0",
            "method": "notifications/initialized",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": mcp_server.get_capabilities(
                    notification_options=NotificationOptions(),
                    experimental_capabilities={},
                ).model_dump(),
                "serverInfo": {
                    "name": "hello-mcp-python-http",
                    "version": "1.0.0"
                }
            }
        }
        yield f"data: {json.dumps(server_info)}\n\n"
        
        # 持续监听和处理请求
        while True:
            try:
                # 这里可以实现更复杂的流式处理逻辑
                await asyncio.sleep(1)
                
                # 发送心跳
                heartbeat = {
                    "jsonrpc": "2.0",
                    "method": "notifications/ping",
                    "params": {"timestamp": asyncio.get_event_loop().time()}
                }
                yield f"data: {json.dumps(heartbeat)}\n\n"
                
            except Exception as e:
                logger.error(f"流式处理错误: {e}")
                break
    
    return StreamingResponse(
        generate_responses(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers": "*",
            "Access-Control-Allow-Methods": "*",
        }
    )

if __name__ == "__main__":
    # 运行服务器
    uvicorn.run(
        "http_server:app",
        host="127.0.0.1",
        port=8000,
        reload=True,
        log_level="info"
    )
