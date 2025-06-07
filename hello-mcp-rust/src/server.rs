use hello_mcp_rust::periodic_table::PERIODIC_TABLE;
use serde_json::{json, Value};
use tokio::io::{self, AsyncBufReadExt, AsyncWriteExt, BufReader};
use tracing::{error, info};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt::init();
    
    info!("Hello MCP Rust Server starting...");
    
    let stdin = io::stdin();
    let mut stdout = io::stdout();
    let mut reader = BufReader::new(stdin);
    let mut line = String::new();
    
    loop {
        line.clear();
        match reader.read_line(&mut line).await {
            Ok(0) => break, // EOF
            Ok(_) => {
                if let Ok(request) = serde_json::from_str::<Value>(&line) {
                    let response = handle_request(request).await;
                    let response_str = serde_json::to_string(&response).unwrap();
                    stdout.write_all(response_str.as_bytes()).await?;
                    stdout.write_all(b"\n").await?;
                    stdout.flush().await?;
                }
            }
            Err(e) => {
                error!("Error reading line: {}", e);
                break;
            }
        }
    }
    
    Ok(())
}

async fn handle_request(request: Value) -> Value {
    let method = request["method"].as_str().unwrap_or("");
    let id = request.get("id");
    
    match method {
        "tools/list" => {
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": {
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
                }
            })
        }
        "tools/call" => {
            let params = &request["params"];
            let tool_name = params["name"].as_str().unwrap_or("");
            let arguments = &params["arguments"];
            
            let result = match tool_name {
                "get_element" => {
                    let name = arguments["name"].as_str().unwrap_or("");
                    get_element_by_name(name)
                }
                "get_element_by_position" => {
                    let position = arguments["position"].as_u64().unwrap_or(0) as u8;
                    get_element_by_position(position)
                }
                _ => json!({
                    "content": [{
                        "type": "text",
                        "text": format!("未知工具: {}", tool_name)
                    }],
                    "isError": true
                })
            };
            
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": result
            })
        }
        "initialize" => {
            json!({
                "jsonrpc": "2.0",
                "id": id,
                "result": {
                    "protocolVersion": "2024-11-05",
                    "capabilities": {
                        "tools": {}
                    },
                    "serverInfo": {
                        "name": "hello-mcp-rust",
                        "version": "1.0.0"
                    }
                }
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
    }
}

fn get_element_by_name(name: &str) -> Value {
    if name.is_empty() {
        return json!({
            "content": [{
                "type": "text",
                "text": "元素名称不能为空"
            }],
            "isError": true
        });
    }
    
    if let Some(element) = PERIODIC_TABLE.iter().find(|e| e.name == name) {
        json!({
            "content": [{
                "type": "text",
                "text": format!(
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
            }]
        })
    } else {
        json!({
            "content": [{
                "type": "text",
                "text": "元素不存在"
            }],
            "isError": true
        })
    }
}

fn get_element_by_position(position: u8) -> Value {
    if position < 1 || position > 118 {
        return json!({
            "content": [{
                "type": "text",
                "text": "原子序数必须在1-118之间"
            }],
            "isError": true
        });
    }
    
    if let Some(element) = PERIODIC_TABLE.iter().find(|e| e.atomic_number == position) {
        json!({
            "content": [{
                "type": "text",
                "text": format!(
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
            }]
        })
    } else {
        json!({
            "content": [{
                "type": "text",
                "text": "元素不存在"
            }],
            "isError": true
        })
    }
}
