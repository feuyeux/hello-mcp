use hello_mcp_rust::periodic_table::PERIODIC_TABLE;
use mcp_sdk::server::{Server, ServerCapabilities, Tool};
use mcp_sdk::transport::SseServerTransport;
use tracing::info;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt::init();
    
    info!("Hello MCP Rust Server starting...");
    
    // 创建 MCP 服务器
    let mut server = Server::new(
        "hello-mcp-rust",
        "1.0.0",
        ServerCapabilities {
            tools: Some(true),
            ..Default::default()
        },
    );
    
    // 注册工具列表处理器
    server.on_list_tools(|| {
        info!("处理 tools/list 请求");
        vec![
            Tool {
                name: "get_element".to_string(),
                description: "根据元素名称获取元素周期表元素信息".to_string(),
                input_schema: serde_json::json!({
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string",
                            "description": "元素的中文名称"
                        }
                    },
                    "required": ["name"]
                }),
            },
            Tool {
                name: "get_element_by_position".to_string(),
                description: "根据元素在周期表中的位置（原子序数）查询元素信息".to_string(),
                input_schema: serde_json::json!({
                    "type": "object",
                    "properties": {
                        "position": {
                            "type": "number",
                            "description": "元素的原子序数，范围从1到118"
                        }
                    },
                    "required": ["position"]
                }),
            },
        ]
    });
    
    // 注册工具调用处理器
    server.on_call_tool(|name, arguments| {
        info!("处理 tools/call 请求: {}", name);
        
        let result = match name.as_str() {
            "get_element" => {
                let element_name = arguments.get("name").and_then(|v| v.as_str()).unwrap_or("");
                get_element_by_name(element_name)
            }
            "get_element_by_position" => {
                let position = arguments.get("position").and_then(|v| v.as_u64()).unwrap_or(0) as u8;
                get_element_by_position(position)
            }
            _ => format!("未知工具: {}", name),
        };
        
        vec![mcp_sdk::Content::Text(result)]
    });
    
    // 使用 SSE HTTP 传输层
    info!("使用 SSE HTTP 传输层，端口: 8065");
    let transport = SseServerTransport::new(8065);
    server.connect(transport).await?;
    
    Ok(())
}

fn get_element_by_name(name: &str) -> String {
    if name.is_empty() {
        return "元素名称不能为空".to_string();
    }
    
    if let Some(element) = PERIODIC_TABLE.iter().find(|e| e.name == name) {
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

fn get_element_by_position(position: u8) -> String {
    if position < 1 || position > 118 {
        return "原子序数必须在1-118之间".to_string();
    }
    
    if let Some(element) = PERIODIC_TABLE.iter().find(|e| e.atomic_number == position) {
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
