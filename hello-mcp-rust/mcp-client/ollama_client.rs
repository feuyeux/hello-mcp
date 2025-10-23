use anyhow::Result;
use reqwest::Client;
use serde::{Deserialize, Serialize};
use serde_json::{json, Value};
use std::collections::HashMap;
use tracing::{debug, error, info};

use crate::client::HelloClient;

/// 消息类
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Message {
    pub role: String,
    pub content: String,
}

impl Message {
    pub fn new(role: &str, content: &str) -> Self {
        Self {
            role: role.to_string(),
            content: content.to_string(),
        }
    }
}

/// 工具调用类
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ToolCall {
    pub name: String,
    pub arguments: HashMap<String, Value>,
}

/// 聊天响应类
#[derive(Debug, Clone)]
pub struct ChatResponse {
    pub role: String,
    pub content: String,
    pub tool_calls: Vec<ToolCall>,
}

impl ChatResponse {
    pub fn has_tool_calls(&self) -> bool {
        !self.tool_calls.is_empty()
    }
}

/// Ollama 客户端
/// 
/// 用于与 Ollama API 交互，支持工具调用
pub struct OllamaClient {
    base_url: String,
    model: String,
    http_client: Client,
}

impl OllamaClient {
    pub fn new(base_url: &str, model: &str) -> Self {
        Self {
            base_url: base_url.to_string(),
            model: model.to_string(),
            http_client: Client::builder()
                .timeout(std::time::Duration::from_secs(300))
                .build()
                .unwrap(),
        }
    }

    /// 发送聊天请求
    /// 
    /// # Arguments
    /// * `messages` - 消息列表
    /// * `tools` - 可用工具列表
    /// 
    /// # Returns
    /// 响应消息
    pub async fn chat(&self, messages: &[Message], tools: &[Value]) -> Result<ChatResponse> {
        info!(
            "发送聊天请求到 Ollama: model={}, messages={}",
            self.model,
            messages.len()
        );

        // 构建请求体
        let request_body = json!({
            "model": self.model,
            "stream": false,
            "messages": messages,
            "tools": tools,
        });

        debug!(
            "请求 JSON: {}",
            serde_json::to_string_pretty(&request_body)?
        );

        // 发送请求
        let response = self
            .http_client
            .post(format!("{}/api/chat", self.base_url))
            .json(&request_body)
            .header("Content-Type", "application/json")
            .send()
            .await?;

        let status = response.status();
        debug!("响应状态: {}", status);

        if !status.is_success() {
            let error_text = response.text().await?;
            error!("Ollama API 请求失败: {}", error_text);
            return Err(anyhow::anyhow!("Ollama API 请求失败: {}", status));
        }

        let response_json: Value = response.json().await?;
        debug!("响应内容: {}", serde_json::to_string_pretty(&response_json)?);

        let message_node = &response_json["message"];
        let role = message_node["role"].as_str().unwrap_or("assistant").to_string();
        let content = message_node["content"].as_str().unwrap_or("").to_string();

        // 解析工具调用
        let mut tool_calls = Vec::new();
        if let Some(tool_calls_array) = message_node["tool_calls"].as_array() {
            for tool_call_node in tool_calls_array {
                if let Some(function_node) = tool_call_node["function"].as_object() {
                    let name = function_node["name"].as_str().unwrap_or("").to_string();
                    let arguments = function_node["arguments"]
                        .as_object()
                        .map(|obj| {
                            obj.iter()
                                .map(|(k, v)| (k.clone(), v.clone()))
                                .collect::<HashMap<String, Value>>()
                        })
                        .unwrap_or_default();

                    tool_calls.push(ToolCall { name, arguments });
                }
            }
        }

        Ok(ChatResponse {
            role,
            content,
            tool_calls,
        })
    }

    /// 执行工具调用
    /// 
    /// # Arguments
    /// * `tool_call` - 工具调用信息
    /// * `hello_client` - MCP客户端实例
    /// 
    /// # Returns
    /// 工具执行结果
    pub async fn execute_tool_call(
        &self,
        tool_call: &ToolCall,
        hello_client: &HelloClient,
    ) -> String {
        info!(
            "执行工具调用: {}, 参数: {:?}",
            tool_call.name, tool_call.arguments
        );

        let result = match tool_call.name.as_str() {
            "get_element" => {
                if let Some(name) = tool_call.arguments.get("name").and_then(|v| v.as_str()) {
                    hello_client.get_element(name).await.unwrap_or_else(|e| {
                        json!({"error": e.to_string()}).to_string()
                    })
                } else {
                    json!({"error": "缺少参数: name"}).to_string()
                }
            }
            "get_element_by_position" => {
                if let Some(position) = tool_call.arguments.get("position") {
                    let pos = if position.is_f64() {
                        position.as_f64().unwrap() as i32
                    } else {
                        position.as_i64().unwrap_or(0) as i32
                    };
                    hello_client
                        .get_element_by_position(pos)
                        .await
                        .unwrap_or_else(|e| json!({"error": e.to_string()}).to_string())
                } else {
                    json!({"error": "缺少参数: position"}).to_string()
                }
            }
            _ => json!({"error": format!("未知工具: {}", tool_call.name)}).to_string(),
        };

        info!("工具调用结果: {}", result);
        result
    }
}
