mod periodic_table;

use anyhow::Result;
use http_body_util::Full;
use hyper::body::{Bytes, Incoming};
use hyper::server::conn::http1;
use hyper::service::service_fn;
use hyper::{Method, Request, Response, StatusCode};
use hyper_util::rt::TokioIo;
use periodic_table::{get_element_by_name, get_element_by_position};
// rmcp 导入（当前未使用，保留以备将来使用）
// use rmcp::model::{CallToolResult, ServerInfo, TextContent, Tool};
use serde_json::json;
use std::net::SocketAddr;
use tokio::net::TcpListener;
use tracing::{error, info};

#[tokio::main]
async fn main() -> Result<()> {
    // 初始化日志
    tracing_subscriber::fmt()
        .with_env_filter(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| tracing_subscriber::EnvFilter::new("info")),
        )
        .init();

    // 解析命令行参数
    let args: Vec<String> = std::env::args().collect();
    let port = if args.len() > 2 && args[1] == "--port" {
        args[2].parse::<u16>().unwrap_or(9900)
    } else {
        9900
    };

    let addr = SocketAddr::from(([127, 0, 0, 1], port));
    let listener = TcpListener::bind(addr).await?;

    info!("MCP Rust Server 已启动");
    info!("服务器地址: http://{}", addr);
    info!("MCP 端点: http://{}/mcp", addr);
    info!("健康检查: http://{}/health", addr);

    loop {
        let (stream, _) = listener.accept().await?;
        let io = TokioIo::new(stream);

        tokio::task::spawn(async move {
            if let Err(err) = http1::Builder::new()
                .serve_connection(io, service_fn(handle_request))
                .await
            {
                error!("Error serving connection: {:?}", err);
            }
        });
    }
}

async fn handle_request(req: Request<Incoming>) -> Result<Response<Full<Bytes>>> {
    let path = req.uri().path();
    let method = req.method();

    // 健康检查
    if path == "/health" && method == Method::GET {
        let body = json!({
            "status": "UP",
            "server": "mcp-server"
        });
        return Ok(Response::builder()
            .status(StatusCode::OK)
            .header("Content-Type", "application/json")
            .body(Full::new(Bytes::from(body.to_string())))
            .unwrap());
    }

    // MCP 端点
    if path == "/mcp" {
        return handle_mcp_request(req).await;
    }

    // 404
    Ok(Response::builder()
        .status(StatusCode::NOT_FOUND)
        .body(Full::new(Bytes::from("Not Found")))
        .unwrap())
}

async fn handle_mcp_request(req: Request<Incoming>) -> Result<Response<Full<Bytes>>> {
    let method = req.method();

    match *method {
        Method::POST => handle_mcp_post(req).await,
        Method::GET => handle_mcp_get(req).await,
        Method::DELETE => handle_mcp_delete(req).await,
        _ => Ok(Response::builder()
            .status(StatusCode::METHOD_NOT_ALLOWED)
            .body(Full::new(Bytes::from("Method Not Allowed")))
            .unwrap()),
    }
}

async fn handle_mcp_post(req: Request<Incoming>) -> Result<Response<Full<Bytes>>> {
    // 读取请求体
    let whole_body = http_body_util::BodyExt::collect(req.into_body())
        .await?
        .to_bytes();
    let body_str = String::from_utf8(whole_body.to_vec())?;

    info!("POST 请求: {}", body_str);

    // 解析 JSON-RPC 请求
    let json_req: serde_json::Value = serde_json::from_str(&body_str)?;
    let method = json_req["method"].as_str().unwrap_or("");
    let id = json_req.get("id").cloned();

    let response = match method {
        "initialize" => {
            let result = json!({
                "protocolVersion": "2025-06-18",
                "capabilities": {
                    "tools": {}
                },
                "serverInfo": {
                    "name": "mcp-server",
                    "version": "0.1.0"
                }
            });
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": result
            })
        }
        "tools/list" => {
            info!("处理 tools/list 请求");
            let tools = list_tools();
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": tools
            })
        }
        "tools/call" => {
            let params = json_req.get("params");
            let result = call_tool(params)?;
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": result
            })
        }
        _ => {
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "error": {
                    "code": -32601,
                    "message": format!("Method not found: {}", method)
                }
            })
        }
    };

    Ok(Response::builder()
        .status(StatusCode::OK)
        .header("Content-Type", "application/json")
        .header("Mcp-Session-Id", format!("{}", uuid::Uuid::new_v4()))
        .body(Full::new(Bytes::from(response.to_string())))
        .unwrap())
}

async fn handle_mcp_get(_req: Request<Incoming>) -> Result<Response<Full<Bytes>>> {
    // SSE 流（简化实现）- 返回空流，不发送任何事件
    // 这样可以避免 Python 客户端解析空 JSON 对象时出错
    Ok(Response::builder()
        .status(StatusCode::OK)
        .header("Content-Type", "text/event-stream")
        .header("Cache-Control", "no-cache")
        .header("Connection", "keep-alive")
        .body(Full::new(Bytes::from("")))
        .unwrap())
}

async fn handle_mcp_delete(_req: Request<Incoming>) -> Result<Response<Full<Bytes>>> {
    info!("DELETE 请求，关闭会话");
    Ok(Response::builder()
        .status(StatusCode::OK)
        .body(Full::new(Bytes::new()))
        .unwrap())
}

fn list_tools() -> serde_json::Value {
    json!({
        "tools": [
            {
                "name": "get_element",
                "description": "根据元素名称获取元素周期表元素信息",
                "inputSchema": {
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string",
                            "description": "元素的中文名称，如'氢'、'氦'等"
                        }
                    },
                    "required": ["name"]
                }
            },
            {
                "name": "get_element_by_position",
                "description": "根据元素在周期表中的位置（原子序数）查询元素信息",
                "inputSchema": {
                    "type": "object",
                    "properties": {
                        "position": {
                            "type": "number",
                            "description": "元素的原子序数，范围从1到118"
                        }
                    },
                    "required": ["position"]
                }
            }
        ]
    })
}

fn call_tool(params: Option<&serde_json::Value>) -> Result<serde_json::Value> {
    let params = params.ok_or_else(|| anyhow::anyhow!("Missing params"))?;
    let name = params["name"].as_str().ok_or_else(|| anyhow::anyhow!("Missing name"))?;
    let arguments = params.get("arguments");

    info!("处理 tools/call 请求: {}", name);

    let text = match name {
        "get_element" => {
            let element_name = arguments
                .and_then(|args| args.get("name"))
                .and_then(|v| v.as_str())
                .ok_or_else(|| anyhow::anyhow!("Missing element name"))?;

            if let Some(element) = get_element_by_name(element_name) {
                format!(
                    "元素名称: {} ({}, {}), 原子序数: {}, 符号: {}, 相对原子质量: {:.3}, 周期: {}, 族: {}",
                    element.name,
                    element.pronunciation,
                    element.english_name,
                    element.atomic_number,
                    element.symbol,
                    element.atomic_weight,
                    element.period,
                    element.group
                )
            } else {
                "元素不存在".to_string()
            }
        }
        "get_element_by_position" => {
            let position = arguments
                .and_then(|args| args.get("position"))
                .and_then(|v| v.as_u64())
                .ok_or_else(|| anyhow::anyhow!("Missing position"))? as u8;

            if position < 1 || position > 118 {
                "原子序数必须在1-118之间".to_string()
            } else if let Some(element) = get_element_by_position(position) {
                format!(
                    "元素名称: {} ({}, {}), 原子序数: {}, 符号: {}, 相对原子质量: {:.3}, 周期: {}, 族: {}",
                    element.name,
                    element.pronunciation,
                    element.english_name,
                    element.atomic_number,
                    element.symbol,
                    element.atomic_weight,
                    element.period,
                    element.group
                )
            } else {
                "元素不存在".to_string()
            }
        }
        _ => return Err(anyhow::anyhow!("Unknown tool: {}", name)),
    };

    Ok(json!({
        "content": [
            {
                "type": "text",
                "text": text
            }
        ]
    }))
}
