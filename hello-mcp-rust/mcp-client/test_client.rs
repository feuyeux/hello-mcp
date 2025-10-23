mod client;

use anyhow::Result;
use client::HelloClient;
use std::env;
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
    let args: Vec<String> = env::args().collect();
    let port = if args.len() > 2 && args[1] == "--port" {
        args[2].parse::<u16>().unwrap_or(9900)
    } else {
        9900
    };

    let base_url = format!("http://localhost:{}", port);
    info!("连接到 MCP 服务器: {}\n", base_url);

    let client = HelloClient::new(&base_url);

    // 测试1: 列举工具
    info!("=== 测试1: 列举Hello MCP工具 ===");
    match client.list_tools().await {
        Ok(tools) => {
            println!("\n列举到的工具:\n{}\n", tools);
        }
        Err(e) => {
            error!("测试1失败: {}", e);
            return Err(e);
        }
    }

    // 测试2: 按名称查询
    info!("=== 测试2: 测试Hello MCP - 按名称查询 ===");
    match client.get_element("氢").await {
        Ok(result) => {
            println!("查询氢元素结果: {}\n", result);
        }
        Err(e) => {
            error!("测试2失败: {}", e);
            return Err(e);
        }
    }

    // 测试3: 按位置查询
    info!("=== 测试3: 测试MCP工具调用 - 按位置查询 ===");
    match client.get_element_by_position(6).await {
        Ok(result) => {
            println!("查询原子序数为6的元素结果: {}\n", result);
        }
        Err(e) => {
            error!("测试3失败: {}", e);
            return Err(e);
        }
    }

    Ok(())
}
