using Microsoft.Extensions.Logging;
using ModelContextProtocol;
using ModelContextProtocol.Client;

namespace Feuyeux.AI.Hello;

/// <summary>
/// MCP 客户端应用
/// 使用 MCP SDK 的 SseClientTransport
/// </summary>
public class McpClientApplication
{
    private readonly ILogger<McpClientApplication> _logger;

    public McpClientApplication(ILogger<McpClientApplication> logger)
    {
        _logger = logger;
    }

    public async Task Test()
    {
        _logger.LogInformation("=== 测试 MCP 客户端 ===");

        // 创建客户端
        var client = new McpClient(
            new ClientInfo("hello-mcp-client", "1.0.0")
        );

        try
        {
            // 使用 SSE HTTP 传输层连接
            _logger.LogInformation("连接到服务器: http://localhost:8066");
            var transport = new SseClientTransport("http://localhost:8066");
            await client.ConnectAsync(transport);

            // 初始化
            var initResult = await client.InitializeAsync();
            _logger.LogInformation("服务器: {ServerName}", initResult.ServerInfo.Name);

            // 列出工具
            _logger.LogInformation("\n=== 列出工具 ===");
            var tools = await client.ListToolsAsync();
            foreach (var tool in tools)
            {
                _logger.LogInformation("  - {ToolName}: {Description}", tool.Name, tool.Description);
            }

            // 测试查询元素
            _logger.LogInformation("\n=== 测试查询元素 ===");
            var result = await client.CallToolAsync("getElement", new Dictionary<string, object>
            {
                ["name"] = "氢"
            });
            _logger.LogInformation("氢元素: {Result}", result.Content[0]);

            result = await client.CallToolAsync("getElement", new Dictionary<string, object>
            {
                ["name"] = "Silicon"
            });
            _logger.LogInformation("硅元素: {Result}", result.Content[0]);

            // 测试按位置查询
            _logger.LogInformation("\n=== 测试按位置查询 ===");
            result = await client.CallToolAsync("getElementByPosition", new Dictionary<string, object>
            {
                ["position"] = 6
            });
            _logger.LogInformation("第6号元素: {Result}", result.Content[0]);

            await client.CloseAsync();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "测试失败");
        }
    }
}
