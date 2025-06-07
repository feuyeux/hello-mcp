using Microsoft.Extensions.Logging;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Feuyeux.AI.Hello;

public class McpTool
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("description")]
    public string Description { get; set; } = string.Empty;

    [JsonPropertyName("inputSchema")]
    public object InputSchema { get; set; } = new();
}

public class McpToolCall
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("arguments")]
    public JsonElement Arguments { get; set; }
}

public class McpToolResponse
{
    [JsonPropertyName("content")]
    public List<McpContent> Content { get; set; } = new();

    [JsonPropertyName("isError")]
    public bool IsError { get; set; }
}

public class McpContent
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = "text";

    [JsonPropertyName("text")]
    public string Text { get; set; } = string.Empty;
}

public class McpServer
{
    private readonly ILogger<McpServer> _logger;
    private readonly List<McpTool> _tools;

    public McpServer(ILogger<McpServer> logger)
    {
        _logger = logger;
        _tools = new List<McpTool>
        {
            new()
            {
                Name = "get_element",
                Description = "Get information about a chemical element by its symbol",
                InputSchema = new
                {
                    type = "object",
                    properties = new
                    {
                        symbol = new
                        {
                            type = "string",
                            description = "Chemical symbol of the element (e.g., H, He, Li)"
                        }
                    },
                    required = new[] { "symbol" }
                }
            },
            new()
            {
                Name = "get_element_by_position",
                Description = "Get information about a chemical element by its atomic number",
                InputSchema = new
                {
                    type = "object",
                    properties = new
                    {
                        position = new
                        {
                            type = "number",
                            description = "Atomic number of the element (1-118)"
                        }
                    },
                    required = new[] { "position" }
                }
            }
        };
    }

    public List<McpTool> ListTools()
    {
        _logger.LogInformation("Listing available tools");
        return _tools;
    }

    public async Task<McpToolResponse> CallTool(McpToolCall toolCall)
    {
        _logger.LogInformation("Calling tool: {ToolName}", toolCall.Name);

        try
        {
            return toolCall.Name switch
            {
                "get_element" => HandleGetElement(toolCall.Arguments),
                "get_element_by_position" => HandleGetElementByPosition(toolCall.Arguments),
                _ => new McpToolResponse
                {
                    Content = new List<McpContent>
                    {
                        new() { Type = "text", Text = $"Unknown tool: {toolCall.Name}" }
                    },
                    IsError = true
                }
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error calling tool {ToolName}", toolCall.Name);
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = $"Error: {ex.Message}" }
                },
                IsError = true
            };
        }
    }

    private McpToolResponse HandleGetElement(JsonElement arguments)
    {
        if (!arguments.TryGetProperty("symbol", out var symbolElement))
        {
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = "Missing required parameter: symbol" }
                },
                IsError = true
            };
        }

        var symbol = symbolElement.GetString() ?? "";
        var element = PeriodicTable.GetElement(symbol);

        if (element == null)
        {
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = $"Element not found: {symbol}" }
                },
                IsError = true
            };
        }

        var json = JsonSerializer.Serialize(element, new JsonSerializerOptions { WriteIndented = true });
        return new McpToolResponse
        {
            Content = new List<McpContent>
            {
                new() { Type = "text", Text = json }
            }
        };
    }

    private McpToolResponse HandleGetElementByPosition(JsonElement arguments)
    {
        if (!arguments.TryGetProperty("position", out var positionElement))
        {
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = "Missing required parameter: position" }
                },
                IsError = true
            };
        }

        if (!positionElement.TryGetInt32(out var position))
        {
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = "Invalid position parameter: must be a number" }
                },
                IsError = true
            };
        }

        var element = PeriodicTable.GetElementByPosition(position);

        if (element == null)
        {
            return new McpToolResponse
            {
                Content = new List<McpContent>
                {
                    new() { Type = "text", Text = $"Element not found at position: {position}" }
                },
                IsError = true
            };
        }

        var json = JsonSerializer.Serialize(element, new JsonSerializerOptions { WriteIndented = true });
        return new McpToolResponse
        {
            Content = new List<McpContent>
            {
                new() { Type = "text", Text = json }
            }
        };
    }

    public async Task Start()
    {
        _logger.LogInformation("MCP Server started");
        _logger.LogInformation("Available tools: {ToolCount}", _tools.Count);
        
        foreach (var tool in _tools)
        {
            _logger.LogInformation("- {ToolName}: {ToolDescription}", tool.Name, tool.Description);
        }

        // In a real implementation, this would start a server to handle MCP requests
        // For demo purposes, we'll just keep the server running
        Console.WriteLine("MCP Server is running. Press Ctrl+C to stop.");
        
        try
        {
            await Task.Delay(Timeout.Infinite);
        }
        catch (TaskCanceledException)
        {
            _logger.LogInformation("MCP Server stopped");
        }
    }
}
