use anyhow::Result;
use tracing::info;

/// HelloClient 类
///
/// 使用 MCP StreamableHTTP 协议连接到服务器
pub struct HelloClient {
    endpoint: String,
}

/// 解析 SSE 格式响应
fn parse_sse_response(response_text: &str) -> Result<serde_json::Value> {
    if response_text.starts_with("event:") {
        // 提取 data: 后面的 JSON
        let data_line = response_text
            .lines()
            .find(|line| line.starts_with("data:"))
            .ok_or_else(|| anyhow::anyhow!("No data line in SSE response"))?;
        let json_str = data_line.strip_prefix("data:").unwrap_or("").trim();
        Ok(serde_json::from_str(json_str)?)
    } else {
        Ok(serde_json::from_str(response_text)?)
    }
}

impl HelloClient {
    pub fn new(base_url: &str) -> Self {
        Self {
            endpoint: format!("{}/mcp", base_url),
        }
    }

    /// 列举所有可用工具
    #[allow(dead_code)]
    pub async fn list_tools(&self) -> Result<String> {
        let client = reqwest::Client::new();

        // 初始化会话
        let init_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {
                    "name": "rust-mcp-client",
                    "version": "0.1.0"
                }
            }
        });

        let response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .json(&init_request)
            .send()
            .await?;

        let session_id = response
            .headers()
            .get("mcp-session-id")
            .and_then(|v| v.to_str().ok())
            .unwrap_or("")
            .to_string();

        // 列举工具
        let list_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 2,
            "method": "tools/list",
            "params": {}
        });

        let http_response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .header("mcp-session-id", &session_id)
            .json(&list_request)
            .send()
            .await?;

        let response_text = http_response.text().await?;
        let response = parse_sse_response(&response_text)?;

        let mut tools_list = Vec::new();
        if let Some(tools) = response["result"]["tools"].as_array() {
            for tool in tools {
                let name = tool["name"].as_str().unwrap_or("");
                let desc = tool["description"].as_str().unwrap_or("");
                tools_list.push(format!("工具名称: {}, 描述: {}", name, desc));
            }
        }

        let tools_str = tools_list.join("\n");
        info!("列举工具成功:\n{}", tools_str);
        Ok(tools_str)
    }

    /// 根据元素名称查询元素信息
    pub async fn get_element(&self, name: &str) -> Result<String> {
        info!("查询元素: {}", name);

        let client = reqwest::Client::new();

        // 初始化会话
        let init_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {
                    "name": "rust-mcp-client",
                    "version": "0.1.0"
                }
            }
        });

        let response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .json(&init_request)
            .send()
            .await?;

        let session_id = response
            .headers()
            .get("mcp-session-id")
            .and_then(|v| v.to_str().ok())
            .unwrap_or("")
            .to_string();

        // 调用工具
        let call_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 2,
            "method": "tools/call",
            "params": {
                "name": "get_element",
                "arguments": {
                    "name": name
                }
            }
        });

        let http_response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .header("mcp-session-id", &session_id)
            .json(&call_request)
            .send()
            .await?;

        let response_text = http_response.text().await?;
        let response = parse_sse_response(&response_text)?;

        let content = response["result"]["content"]
            .as_array()
            .and_then(|arr| arr.first())
            .and_then(|item| item["text"].as_str())
            .unwrap_or("")
            .to_string();

        info!("查询元素 {} 成功: {}", name, content);
        Ok(content)
    }

    /// 根据原子序数查询元素信息
    pub async fn get_element_by_position(&self, position: i32) -> Result<String> {
        info!("查询位置元素: {}", position);

        let client = reqwest::Client::new();

        // 初始化会话
        let init_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "protocolVersion": "2024-11-05",
                "capabilities": {},
                "clientInfo": {
                    "name": "rust-mcp-client",
                    "version": "0.1.0"
                }
            }
        });

        let response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .json(&init_request)
            .send()
            .await?;

        let session_id = response
            .headers()
            .get("mcp-session-id")
            .and_then(|v| v.to_str().ok())
            .unwrap_or("")
            .to_string();

        // 调用工具
        let call_request = serde_json::json!({
            "jsonrpc": "2.0",
            "id": 2,
            "method": "tools/call",
            "params": {
                "name": "get_element_by_position",
                "arguments": {
                    "position": position
                }
            }
        });

        let http_response = client
            .post(&self.endpoint)
            .header("Accept", "application/json, text/event-stream")
            .header("Content-Type", "application/json")
            .header("mcp-session-id", &session_id)
            .json(&call_request)
            .send()
            .await?;

        let response_text = http_response.text().await?;
        let response = parse_sse_response(&response_text)?;

        let content = response["result"]["content"]
            .as_array()
            .and_then(|arr| arr.first())
            .and_then(|item| item["text"].as_str())
            .unwrap_or("")
            .to_string();

        info!("查询位置元素 {} 成功: {}", position, content);
        Ok(content)
    }

    #[allow(dead_code)]
    pub fn endpoint(&self) -> &str {
        &self.endpoint
    }
}
