using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace Feuyeux.AI.Hello;

class Program
{
    static async Task Main(string[] args)
    {
        var host = CreateHostBuilder(args).Build();

        var mode = args.Length > 0 ? args[0] : "";

        switch (mode.ToLower())
        {
            case "server":
                await RunServer(host);
                break;
            case "client":
                await RunClient(host);
                break;
            default:
                Console.WriteLine("Usage: dotnet run [server|client]");
                Console.WriteLine("  server - Start MCP server");
                Console.WriteLine("  client - Run MCP client tests");
                break;
        }
    }

    static IHostBuilder CreateHostBuilder(string[] args) =>
        Host.CreateDefaultBuilder(args)
            .ConfigureServices((context, services) =>
            {
                services.AddSingleton<McpServer>();
                services.AddTransient<McpClient>();
            });

    static async Task RunServer(IHost host)
    {
        var logger = host.Services.GetRequiredService<ILogger<Program>>();
        var server = host.Services.GetRequiredService<McpServer>();

        logger.LogInformation("Starting MCP Server...");
        await server.Start();
    }

    static async Task RunClient(IHost host)
    {
        var logger = host.Services.GetRequiredService<ILogger<Program>>();
        var client = host.Services.GetRequiredService<McpClient>();

        logger.LogInformation("Starting MCP Client...");
        await client.TestTools();
    }
}
