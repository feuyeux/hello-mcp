using Microsoft.Extensions.Logging;
using ModelContextProtocol;
using ModelContextProtocol.Server;

namespace Feuyeux.AI.Hello;

/// <summary>
/// MCP 服务器应用
/// 使用 MCP SDK 的 SseServerTransport (SSE HTTP 传输)
/// </summary>
public class McpServerApplication
{
    private readonly ILogger<McpServerApplication> _logger;

    public McpServerApplication(ILogger<McpServerApplication> logger)
    {
        _logger = logger;
    }

    public async Task Start()
    {
        _logger.LogInformation("启动 MCP 服务器...");

        // 创建 MCP 服务器
        var server = new McpServer(
            new ServerInfo("hello-mcp-csharp", "1.0.0"),
            new ServerCapabilities
            {
                Tools = new ToolsCapability { ListChanged = true }
            }
        );

        // 注册工具列表处理器
        server.OnListTools(() =>
        {
            _logger.LogInformation("处理 tools/list 请求");
            return Task.FromResult(new List<Tool>
            {
                new Tool
                {
                    Name = "getElement",
                    Description = "根据元素名称获取元素周期表元素信息（支持中文名、英文名或符号）",
                    InputSchema = new
                    {
                        type = "object",
                        properties = new
                        {
                            name = new
                            {
                                type = "string",
                                description = "元素的中文名称、英文名或符号"
                            }
                        },
                        required = new[] { "name" }
                    }
                },
                new Tool
                {
                    Name = "getElementByPosition",
                    Description = "根据元素在周期表中的位置（原子序数）查询元素信息",
                    InputSchema = new
                    {
                        type = "object",
                        properties = new
                        {
                            position = new
                            {
                                type = "integer",
                                description = "元素的原子序数，范围从1到118"
                            }
                        },
                        required = new[] { "position" }
                    }
                }
            });
        });

        // 注册工具调用处理器
        server.OnCallTool(async (toolName, arguments) =>
        {
            _logger.LogInformation("处理 tools/call 请求: {ToolName}", toolName);

            try
            {
                string result = toolName switch
                {
                    "getElement" => HandleGetElement(arguments),
                    "getElementByPosition" => HandleGetElementByPosition(arguments),
                    _ => $"未知工具: {toolName}"
                };

                return new ToolResult
                {
                    Content = new List<Content>
                    {
                        new TextContent { Text = result }
                    }
                };
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "工具调用失败");
                return new ToolResult
                {
                    Content = new List<Content>
                    {
                        new TextContent { Text = $"错误: {ex.Message}" }
                    },
                    IsError = true
                };
            }
        });

        // 使用 SSE HTTP 传输层
        _logger.LogInformation("使用 SSE HTTP 传输层，端口: 8066");
        await server.ConnectAsync(new SseServerTransport(8066));
    }

    private string HandleGetElement(Dictionary<string, object> arguments)
    {
        var name = arguments["name"]?.ToString() ?? "";
        var element = PeriodicTable.GetElement(name);
        
        if (element == null)
            return "元素不存在";
            
        return $"元素名称: {element.Name}, 原子序数: {element.AtomicNumber}, 符号: {element.Symbol}";
    }

    private string HandleGetElementByPosition(Dictionary<string, object> arguments)
    {
        var position = Convert.ToInt32(arguments["position"]);
        var element = PeriodicTable.GetElementByPosition(position);
        
        if (element == null)
            return "元素不存在";
            
        return $"元素名称: {element.Name}, 原子序数: {element.AtomicNumber}, 符号: {element.Symbol}";
    }
}
