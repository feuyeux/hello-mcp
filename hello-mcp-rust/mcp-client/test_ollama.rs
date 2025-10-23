mod client;
mod ollama_client;

use anyhow::Result;
use client::HelloClient;
use ollama_client::{Message, OllamaClient};
use serde_json::json;
use std::env;
use tracing::{error, info};

/// 测试 LLM 通过工具调用查询元素
async fn test_llm_with_mcp_tools(port: u16) -> Result<()> {
    let ollama_client = OllamaClient::new("http://localhost:11434", "qwen2.5:latest");
    let hello_client = HelloClient::new(&format!("http://localhost:{}", port));

    info!("=== 测试: LLM 通过工具调用查询元素 ===");

    // 获取可用工具
    let tools = get_tools(&hello_client).await?;

    // 构建消息
    let mut messages = Vec::new();
    let query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量";
    messages.push(Message::new("user", query));

    // 第一次调用 LLM
    info!("第一次调用 LLM: {}", query);
    let response = ollama_client.chat(&messages, &tools).await?;

    info!("LLM 响应角色: {}", response.role);
    info!("LLM 响应内容: {}", response.content);

    // 检查是否有工具调用
    if response.has_tool_calls() {
        info!("LLM 决定调用工具，工具数量: {}", response.tool_calls.len());

        // 执行工具调用
        for tool_call in &response.tool_calls {
            info!("执行工具: {}", tool_call.name);
            info!("工具参数: {:?}", tool_call.arguments);

            let tool_result = ollama_client
                .execute_tool_call(tool_call, &hello_client)
                .await;
            info!("工具执行结果: {}", tool_result);

            // 将工具结果添加到消息历史
            messages.push(Message::new("assistant", ""));
            messages.push(Message::new("tool", &tool_result));
        }

        // 第二次调用 LLM，让其基于工具结果生成最终答案
        info!("第二次调用 LLM，生成最终答案...");
        let final_response = ollama_client.chat(&messages, &tools).await?;

        info!("最终答案: {}", final_response.content);
        println!("\n最终答案: {}\n", final_response.content);
        info!("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息");
    } else {
        info!("LLM 没有调用工具，直接返回了答案: {}", response.content);
        info!("这可能是因为 LLM 已经知道答案，或者不支持工具调用");
    }

    Ok(())
}

/// 获取可用工具列表
async fn get_tools(hello_client: &HelloClient) -> Result<Vec<serde_json::Value>> {
    let client = reqwest::Client::new();

    // 初始化会话
    let init_request = json!({
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
        .post(hello_client.endpoint())
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
    let list_request = json!({
        "jsonrpc": "2.0",
        "id": 2,
        "method": "tools/list",
        "params": {}
    });

    let http_response = client
        .post(hello_client.endpoint())
        .header("Accept", "application/json, text/event-stream")
        .header("Content-Type", "application/json")
        .header("mcp-session-id", &session_id)
        .json(&list_request)
        .send()
        .await?;

    let response_text = http_response.text().await?;

    // 解析 SSE 格式
    let response: serde_json::Value = if response_text.starts_with("event:") {
        let data_line = response_text
            .lines()
            .find(|line| line.starts_with("data:"))
            .ok_or_else(|| anyhow::anyhow!("No data line in SSE response"))?;
        let json_str = data_line.strip_prefix("data:").unwrap_or("").trim();
        serde_json::from_str(json_str)?
    } else {
        serde_json::from_str(&response_text)?
    };

    let mut tools = Vec::new();
    if let Some(tools_array) = response["result"]["tools"].as_array() {
        for tool in tools_array {
            tools.push(json!({
                "type": "function",
                "function": {
                    "name": tool["name"],
                    "description": tool["description"],
                    "parameters": tool["inputSchema"],
                }
            }));
        }
    }

    Ok(tools)
}

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
    let args: Vec<String> = env::args().collect();
    let port = if args.len() > 2 && args[1] == "--port" {
        args[2].parse::<u16>().unwrap_or(9900)
    } else {
        9900
    };

    info!("连接到 MCP 服务器: http://localhost:{}", port);

    match test_llm_with_mcp_tools(port).await {
        Ok(_) => Ok(()),
        Err(e) => {
            error!("测试失败: {}", e);
            info!(
                "提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载\n\
                启动命令: ollama serve\n\
                下载模型: ollama pull qwen2.5:latest"
            );
            Err(e)
        }
    }
}
