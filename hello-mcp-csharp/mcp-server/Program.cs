using Feuyeux.AI.Hello.Server;
using Microsoft.Extensions.Logging;
using System.CommandLine;

var rootCommand = new RootCommand("Hello MCP C# Server - Model Context Protocol 服务器");

var portOption = new Option<int>(
    "--port",
    getDefaultValue: () => 9900,
    description: "服务器监听端口"
);

var logLevelOption = new Option<string>(
    "--log-level",
    getDefaultValue: () => "Information",
    description: "日志级别 (Trace, Debug, Information, Warning, Error, Critical)"
);

rootCommand.AddOption(portOption);
rootCommand.AddOption(logLevelOption);

rootCommand.SetHandler(async (port, logLevel) =>
{
    var builder = WebApplication.CreateBuilder();
    
    builder.Logging.ClearProviders();
    builder.Logging.AddConsole(options =>
    {
        options.LogToStandardErrorThreshold = LogLevel.Trace;
    });
    builder.Logging.SetMinimumLevel(ParseLogLevel(logLevel));
    
    builder.Services.AddMcpServer()
        .WithHttpTransport()
        .WithTools<PeriodicTableTools>();
    
    builder.WebHost.UseUrls($"http://127.0.0.1:{port}");
    
    var app = builder.Build();
    
    app.MapMcp();
    
    var logger = app.Services.GetRequiredService<ILogger<Program>>();
    logger.LogInformation("MCP 服务器已启动，监听 http://127.0.0.1:{Port}/mcp/v1", port);
    
    await app.RunAsync();
}, portOption, logLevelOption);

return await rootCommand.InvokeAsync(args);

static LogLevel ParseLogLevel(string logLevel)
{
    return logLevel.ToLower() switch
    {
        "trace" => LogLevel.Trace,
        "debug" => LogLevel.Debug,
        "information" => LogLevel.Information,
        "warning" => LogLevel.Warning,
        "error" => LogLevel.Error,
        "critical" => LogLevel.Critical,
        _ => LogLevel.Information
    };
}
