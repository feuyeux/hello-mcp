use mcp_sdk::client::Client;
use mcp_sdk::transport::SseClientTransport;
use tracing::info;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt::init();
    
    info!("Hello MCP Rust Client starting...");
    
    // 创建客户端
    let mut client = Client::new("hello-mcp-client", "1.0.0");
    
    // 使用 SSE HTTP 传输层连接
    info!("连接到服务器: http://localhost:8065");
    let transport = SseClientTransport::new("http://localhost:8065")?;
    client.connect(transport).await?;
    
    // 初始化
    let init_result = client.initialize().await?;
    info!("服务器: {}", init_result.server_info.name);
    
    // 列出工具
    info!("\n=== 列出工具 ===");
    let tools = client.list_tools().await?;
    for tool in &tools.tools {
        info!("  - {}: {}", tool.name, tool.description);
    }
    
    // 测试查询元素
    info!("\n=== 测试查询元素 ===");
    let result = client.call_tool("get_element", serde_json::json!({"name": "硅"})).await?;
    info!("硅元素: {:?}", result.content);
    
    let result = client.call_tool("get_element_by_position", serde_json::json!({"position": 14})).await?;
    info!("第14号元素: {:?}", result.content);
    
    client.close().await?;
    
    Ok(())
}
