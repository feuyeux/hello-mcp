use serde_json::{json, Value};
use std::process::Stdio;
use tokio::io::{AsyncBufReadExt, AsyncWriteExt, BufReader};
use tokio::process::Command as TokioCommand;
use tracing::info;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt::init();
    
    info!("Hello MCP Rust Client starting...");
    
    // 启动服务器进程
    let mut server = TokioCommand::new("cargo")
        .args(&["run", "--bin", "server"])
        .stdin(Stdio::piped())
        .stdout(Stdio::piped())
        .stderr(Stdio::inherit())
        .spawn()?;
    
    let mut server_stdin = server.stdin.take().unwrap();
    let server_stdout = server.stdout.take().unwrap();
    let mut reader = BufReader::new(server_stdout);
    
    // 初始化
    let init_request = json!({
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {
                "name": "hello-mcp-client",
                "version": "1.0.0"
            }
        }
    });
    
    send_request(&mut server_stdin, &init_request).await?;
    let _init_response = read_response(&mut reader).await?;
    
    // 测试获取工具列表
    println!("=== 测试获取工具列表 ===");
    let list_tools_request = json!({
        "jsonrpc": "2.0",
        "id": 2,
        "method": "tools/list"
    });
    
    send_request(&mut server_stdin, &list_tools_request).await?;
    let tools_response = read_response(&mut reader).await?;
    
    if let Some(tools) = tools_response["result"]["tools"].as_array() {
        let tool_names: Vec<String> = tools
            .iter()
            .filter_map(|tool| tool["name"].as_str())
            .map(|name| name.to_string())
            .collect();
        println!("可用工具: {:?}", tool_names);
    }
    
    // 测试根据名称获取元素
    println!("\n=== 测试根据名称获取元素 ===");
    let get_element_request = json!({
        "jsonrpc": "2.0",
        "id": 3,
        "method": "tools/call",
        "params": {
            "name": "get_element",
            "arguments": {
                "name": "硅"
            }
        }
    });
    
    send_request(&mut server_stdin, &get_element_request).await?;
    let element_response = read_response(&mut reader).await?;
    
    if let Some(content) = element_response["result"]["content"][0]["text"].as_str() {
        println!("硅元素信息: {}", content);
    }
    
    // 测试根据位置获取元素
    println!("\n=== 测试根据位置获取元素 ===");
    let get_element_by_pos_request = json!({
        "jsonrpc": "2.0",
        "id": 4,
        "method": "tools/call",
        "params": {
            "name": "get_element_by_position",
            "arguments": {
                "position": 14
            }
        }
    });
    
    send_request(&mut server_stdin, &get_element_by_pos_request).await?;
    let element_by_pos_response = read_response(&mut reader).await?;
    
    if let Some(content) = element_by_pos_response["result"]["content"][0]["text"].as_str() {
        println!("第14号元素信息: {}", content);
    }
    
    // 关闭服务器
    server.kill().await?;
    
    Ok(())
}

async fn send_request(
    stdin: &mut tokio::process::ChildStdin,
    request: &Value,
) -> Result<(), Box<dyn std::error::Error>> {
    let request_str = serde_json::to_string(request)?;
    stdin.write_all(request_str.as_bytes()).await?;
    stdin.write_all(b"\n").await?;
    stdin.flush().await?;
    Ok(())
}

async fn read_response(
    reader: &mut BufReader<tokio::process::ChildStdout>,
) -> Result<Value, Box<dyn std::error::Error>> {
    let mut line = String::new();
    reader.read_line(&mut line).await?;
    let response: Value = serde_json::from_str(&line)?;
    Ok(response)
}
