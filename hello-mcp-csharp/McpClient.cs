using Microsoft.Extensions.Logging;
using System.Text.Json;

namespace Feuyeux.AI.Hello;

public class McpClient
{
    private readonly ILogger<McpClient> _logger;
    private readonly McpServer _server;

    public McpClient(ILogger<McpClient> logger, McpServer server)
    {
        _logger = logger;
        _server = server;
    }

    public async Task TestTools()
    {
        _logger.LogInformation("Starting MCP Client tests");

        // List available tools
        var tools = _server.ListTools();
        _logger.LogInformation("Available tools: {ToolCount}", tools.Count);

        // Test get_element
        await TestGetElement();

        // Test get_element_by_position
        await TestGetElementByPosition();

        // Test error cases
        await TestErrorCases();

        _logger.LogInformation("MCP Client tests completed");
    }

    private async Task TestGetElement()
    {
        _logger.LogInformation("Testing get_element tool");

        var testCases = new[] { "H", "He", "C", "O", "Au" };

        foreach (var symbol in testCases)
        {
            var toolCall = new McpToolCall
            {
                Name = "get_element",
                Arguments = JsonSerializer.SerializeToElement(new { symbol })
            };

            var response = await _server.CallTool(toolCall);
            
            if (response.IsError)
            {
                _logger.LogWarning("Error for symbol {Symbol}: {Error}", symbol, response.Content.First().Text);
            }
            else
            {
                _logger.LogInformation("Element {Symbol}: {Response}", symbol, response.Content.First().Text);
            }
        }
    }

    private async Task TestGetElementByPosition()
    {
        _logger.LogInformation("Testing get_element_by_position tool");

        var testCases = new[] { 1, 2, 6, 8, 20, 79 };

        foreach (var position in testCases)
        {
            var toolCall = new McpToolCall
            {
                Name = "get_element_by_position",
                Arguments = JsonSerializer.SerializeToElement(new { position })
            };

            var response = await _server.CallTool(toolCall);
            
            if (response.IsError)
            {
                _logger.LogWarning("Error for position {Position}: {Error}", position, response.Content.First().Text);
            }
            else
            {
                _logger.LogInformation("Position {Position}: {Response}", position, response.Content.First().Text);
            }
        }
    }

    private async Task TestErrorCases()
    {
        _logger.LogInformation("Testing error cases");

        // Test invalid symbol
        var invalidSymbolCall = new McpToolCall
        {
            Name = "get_element",
            Arguments = JsonSerializer.SerializeToElement(new { symbol = "XYZ" })
        };

        var response = await _server.CallTool(invalidSymbolCall);
        _logger.LogInformation("Invalid symbol test: {IsError} - {Message}", response.IsError, response.Content.First().Text);

        // Test invalid position
        var invalidPositionCall = new McpToolCall
        {
            Name = "get_element_by_position",
            Arguments = JsonSerializer.SerializeToElement(new { position = 999 })
        };

        response = await _server.CallTool(invalidPositionCall);
        _logger.LogInformation("Invalid position test: {IsError} - {Message}", response.IsError, response.Content.First().Text);

        // Test missing parameters
        var missingParamCall = new McpToolCall
        {
            Name = "get_element",
            Arguments = JsonSerializer.SerializeToElement(new { })
        };

        response = await _server.CallTool(missingParamCall);
        _logger.LogInformation("Missing parameter test: {IsError} - {Message}", response.IsError, response.Content.First().Text);

        // Test unknown tool
        var unknownToolCall = new McpToolCall
        {
            Name = "unknown_tool",
            Arguments = JsonSerializer.SerializeToElement(new { })
        };

        response = await _server.CallTool(unknownToolCall);
        _logger.LogInformation("Unknown tool test: {IsError} - {Message}", response.IsError, response.Content.First().Text);
    }
}
