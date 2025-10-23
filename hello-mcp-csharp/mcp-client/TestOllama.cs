using Microsoft.Extensions.Logging;
using ModelContextProtocol.Client;
using System.CommandLine;

namespace Feuyeux.AI.Hello.Client;

/// <summary>
/// Ollama MCP 集成测试
/// </summary>
public class TestOllama
{
    public static async Task<int> Main(string[] args)
    {
        var rootCommand = new RootCommand("Ollama MCP Integration Test");

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

            Console.WriteLine($"连接到 MCP 服务器: http://localhost:{port}");
            await TestLlmWithMcpTools(loggerFactory, port);
        }, portOption);

        return await rootCommand.InvokeAsync(args);
    }

    /// <summary>
    /// 测试 LLM 通过工具调用查询元素
    /// </summary>
    private static async Task TestLlmWithMcpTools(ILoggerFactory loggerFactory, int port)
    {
        var logger = loggerFactory.CreateLogger<TestOllama>();
        var ollamaLogger = loggerFactory.CreateLogger<OllamaClient>();
        var helloLogger = loggerFactory.CreateLogger<HelloClient>();

        using var ollamaClient = new OllamaClient(ollamaLogger);
        var helloClient = new HelloClient(helloLogger, $"http://localhost:{port}");

        try
        {
            logger.LogInformation("=== 测试: LLM 通过工具调用查询元素 ===");

            // 获取可用工具
            var endpoint = $"http://localhost:{port}";
            var transport = new HttpClientTransport(new HttpClientTransportOptions
            {
                Endpoint = new Uri(endpoint)
            });

            await using var mcpClient = await McpClient.CreateAsync(transport);
            var toolsResult = await mcpClient.ListToolsAsync();

            // 转换工具格式为 Ollama 格式
            var tools = new List<object>();
            foreach (var tool in toolsResult)
            {
                // 使用反射获取 InputSchema，因为它可能是动态类型
                var inputSchemaProperty = tool.GetType().GetProperty("InputSchema");
                var inputSchema = inputSchemaProperty?.GetValue(tool);

                tools.Add(new
                {
                    type = "function",
                    function = new
                    {
                        name = tool.Name,
                        description = tool.Description,
                        parameters = inputSchema ?? new { }
                    }
                });
            }

            // 构建消息
            var messages = new List<Message>();
            var query = "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量";
            messages.Add(new Message("user", query));

            // 第一次调用 LLM
            Console.WriteLine($"\n第一次调用 LLM: {query}");
            logger.LogInformation("第一次调用 LLM: {Query}", query);
            var response = await ollamaClient.Chat(messages, tools);

            Console.WriteLine($"LLM 响应角色: {response.Role}");
            Console.WriteLine($"LLM 响应内容: {response.Content}");
            logger.LogInformation("LLM 响应角色: {Role}", response.Role);
            logger.LogInformation("LLM 响应内容: {Content}", response.Content);

            // 检查是否有工具调用
            if (response.HasToolCalls())
            {
                Console.WriteLine($"\nLLM 决定调用工具，工具数量: {response.ToolCalls!.Count}");
                logger.LogInformation("LLM 决定调用工具，工具数量: {Count}", response.ToolCalls!.Count);

                // 执行工具调用
                foreach (var toolCall in response.ToolCalls!)
                {
                    Console.WriteLine($"\n执行工具: {toolCall.Function.Name}");
                    var argsJson = System.Text.Json.JsonSerializer.Serialize(toolCall.Function.Arguments);
                    Console.WriteLine($"工具参数: {argsJson}");
                    
                    logger.LogInformation("执行工具: {Name}", toolCall.Function.Name);
                    logger.LogInformation("工具参数: {Arguments}", argsJson);

                    var toolResult = await ollamaClient.ExecuteToolCall(toolCall, helloClient);
                    Console.WriteLine($"工具执行结果: {toolResult}");
                    logger.LogInformation("工具执行结果: {Result}", toolResult);

                    // 将工具结果添加到消息历史
                    messages.Add(new Message("assistant", ""));
                    messages.Add(new Message("tool", toolResult));
                }

                // 第二次调用 LLM，让其基于工具结果生成最终答案
                Console.WriteLine("\n第二次调用 LLM，生成最终答案...");
                logger.LogInformation("第二次调用 LLM，生成最终答案...");
                var finalResponse = await ollamaClient.Chat(messages, tools);

                Console.WriteLine($"\n最终答案: {finalResponse.Content}\n");
                logger.LogInformation("最终答案: {Content}", finalResponse.Content);
                logger.LogInformation("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息");
            }
            else
            {
                Console.WriteLine($"\nLLM 没有调用工具，直接返回了答案: {response.Content}");
                Console.WriteLine("这可能是因为 LLM 已经知道答案，或者不支持工具调用");
                logger.LogWarning("LLM 没有调用工具，直接返回了答案: {Content}", response.Content);
                logger.LogInformation("这可能是因为 LLM 已经知道答案，或者不支持工具调用");
            }
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "测试失败");
            Console.WriteLine("\n提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载");
            Console.WriteLine("启动命令: ollama serve");
            Console.WriteLine("下载模型: ollama pull qwen2.5:latest\n");
        }
    }
}
