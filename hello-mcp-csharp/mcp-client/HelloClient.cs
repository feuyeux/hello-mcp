using Microsoft.Extensions.Logging;
using ModelContextProtocol.Client;

namespace Feuyeux.AI.Hello.Client;

/// <summary>
/// HelloClient 类
/// 
/// 此类负责创建与MCP服务器的连接，用于调用元素周期表相关的工具。
/// 使用 HttpClientTransport，默认使用 StreamableHTTP 传输（自动回退到 SSE）。
/// </summary>
public class HelloClient
{
    private readonly ILogger<HelloClient> _logger;
    private readonly string _baseUrl;
    private readonly string _endpoint;

    public HelloClient(ILogger<HelloClient> logger, string baseUrl = "http://localhost:9900")
    {
        _logger = logger;
        _baseUrl = baseUrl;
        _endpoint = baseUrl; // ASP.NET Core MapMcp() 映射到根路径
    }

    /// <summary>
    /// 列举所有可用工具
    /// </summary>
    public async Task<string> ListTools()
    {
        try
        {
            _logger.LogInformation("列举所有可用工具");
            
            var transport = new HttpClientTransport(new HttpClientTransportOptions
            {
                Endpoint = new Uri(_endpoint)
            });
            
            await using var client = await McpClient.CreateAsync(transport);
            var tools = await client.ListToolsAsync();

            var toolsList = new List<string>();
            foreach (var tool in tools)
            {
                toolsList.Add($"工具名称: {tool.Name}, 描述: {tool.Description}");
            }

            var result = string.Join("\n", toolsList);
            _logger.LogInformation("列举工具成功:\n{Tools}", result);
            return result;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "列举工具失败");
            throw;
        }
    }

    /// <summary>
    /// 根据元素名称查询元素信息
    /// </summary>
    public async Task<string> GetElement(string name)
    {
        try
        {
            _logger.LogInformation("查询元素: {Name}", name);
            
            var transport = new HttpClientTransport(new HttpClientTransportOptions
            {
                Endpoint = new Uri(_endpoint)
            });
            
            await using var client = await McpClient.CreateAsync(transport);
            var arguments = new Dictionary<string, object?> { ["name"] = name };
            var result = await client.CallToolAsync("get_element", arguments);

            var content = "";
            if (result.Content.Count > 0 && result.Content[0] is ModelContextProtocol.Protocol.TextContentBlock textBlock)
            {
                content = textBlock.Text;
            }
            _logger.LogInformation("查询元素 {Name} 成功: {Result}", name, content);
            return content;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "查询元素失败");
            throw;
        }
    }

    /// <summary>
    /// 根据原子序数查询元素信息
    /// </summary>
    public async Task<string> GetElementByPosition(int position)
    {
        try
        {
            _logger.LogInformation("查询位置元素: {Position}", position);
            
            var transport = new HttpClientTransport(new HttpClientTransportOptions
            {
                Endpoint = new Uri(_endpoint)
            });
            
            await using var client = await McpClient.CreateAsync(transport);
            var arguments = new Dictionary<string, object?> { ["position"] = position };
            var result = await client.CallToolAsync("get_element_by_position", arguments);

            var content = "";
            if (result.Content.Count > 0 && result.Content[0] is ModelContextProtocol.Protocol.TextContentBlock textBlock)
            {
                content = textBlock.Text;
            }
            _logger.LogInformation("查询位置元素 {Position} 成功: {Result}", position, content);
            return content;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "查询位置元素失败");
            throw;
        }
    }
}
