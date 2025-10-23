using Microsoft.Extensions.Logging;
using System.CommandLine;

namespace Feuyeux.AI.Hello.Client;

/// <summary>
/// MCP 客户端测试程序
/// </summary>
public class TestClient
{
    public static async Task<int> Main(string[] args)
    {
        var rootCommand = new RootCommand("MCP Client Tests");

        var portOption = new Option<int>(
            "--port",
            getDefaultValue: () => 9900,
            description: "Port to connect to MCP server"
        );

        rootCommand.AddOption(portOption);

        rootCommand.SetHandler(async (port) =>
        {
            using var loggerFactory = LoggerFactory.Create(builder =>
            {
                builder.AddConsole();
                builder.SetMinimumLevel(LogLevel.Information);
            });

            var logger = loggerFactory.CreateLogger<HelloClient>();
            var client = new HelloClient(logger, $"http://localhost:{port}");

            Console.WriteLine($"连接到 MCP 服务器: http://localhost:{port}");
            await RunAllTests(client);
        }, portOption);

        return await rootCommand.InvokeAsync(args);
    }

    private static async Task RunAllTests(HelloClient client)
    {
        await TestListTools(client);
        await TestGetElementByName(client);
        await TestGetElementByPosition(client);
    }

    /// <summary>
    /// 测试1: 列举Hello MCP工具
    /// </summary>
    private static async Task TestListTools(HelloClient client)
    {
        Console.WriteLine("\n=== 测试1: 列举Hello MCP工具 ===");
        var tools = await client.ListTools();
        Console.WriteLine($"\n列举到的工具:\n{tools}\n");
    }

    /// <summary>
    /// 测试2: 测试Hello MCP - 按名称查询
    /// </summary>
    private static async Task TestGetElementByName(HelloClient client)
    {
        Console.WriteLine("=== 测试2: 测试Hello MCP - 按名称查询 ===");
        var result = await client.GetElement("氢");
        Console.WriteLine($"查询氢元素结果: {result}\n");
    }

    /// <summary>
    /// 测试3: 测试MCP工具调用 - 按位置查询
    /// </summary>
    private static async Task TestGetElementByPosition(HelloClient client)
    {
        Console.WriteLine("=== 测试3: 测试MCP工具调用 - 按位置查询 ===");
        var result = await client.GetElementByPosition(6);
        Console.WriteLine($"查询原子序数为6的元素结果: {result}\n");
    }
}
