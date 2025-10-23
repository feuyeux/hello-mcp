using Microsoft.Extensions.Logging;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Feuyeux.AI.Hello.Client;

/// <summary>
/// 消息类
/// </summary>
public class Message
{
    [JsonPropertyName("role")]
    public string Role { get; set; } = string.Empty;

    [JsonPropertyName("content")]
    public string Content { get; set; } = string.Empty;

    public Message() { }

    public Message(string role, string content)
    {
        Role = role;
        Content = content;
    }
}

/// <summary>
/// 工具调用类
/// </summary>
public class ToolCall
{
    [JsonPropertyName("function")]
    public FunctionCall Function { get; set; } = new();
}

public class FunctionCall
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("arguments")]
    public Dictionary<string, object> Arguments { get; set; } = new();
}

/// <summary>
/// 聊天响应类
/// </summary>
public class ChatResponse
{
    [JsonPropertyName("message")]
    public ResponseMessage Message { get; set; } = new();
}

public class ResponseMessage
{
    [JsonPropertyName("role")]
    public string Role { get; set; } = string.Empty;

    [JsonPropertyName("content")]
    public string Content { get; set; } = string.Empty;

    [JsonPropertyName("tool_calls")]
    public List<ToolCall>? ToolCalls { get; set; }

    public bool HasToolCalls() => ToolCalls != null && ToolCalls.Count > 0;
}

/// <summary>
/// Ollama 客户端
/// 
/// 用于与 Ollama API 交互，支持工具调用
/// </summary>
public class OllamaClient : IDisposable
{
    private readonly ILogger<OllamaClient> _logger;
    private readonly HttpClient _httpClient;
    private readonly string _baseUrl;
    private readonly string _model;

    public OllamaClient(ILogger<OllamaClient> logger, string baseUrl = "http://localhost:11434", string model = "qwen2.5:latest")
    {
        _logger = logger;
        _baseUrl = baseUrl;
        _model = model;
        _httpClient = new HttpClient
        {
            Timeout = TimeSpan.FromSeconds(300)
        };
    }

    /// <summary>
    /// 发送聊天请求
    /// </summary>
    public async Task<ResponseMessage> Chat(List<Message> messages, List<object> tools)
    {
        try
        {
            _logger.LogInformation("发送聊天请求到 Ollama: model={Model}, messages={Count}", _model, messages.Count);

            var requestBody = new
            {
                model = _model,
                stream = false,
                messages,
                tools
            };

            var json = JsonSerializer.Serialize(requestBody, new JsonSerializerOptions
            {
                WriteIndented = true,
                DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull
            });
            _logger.LogDebug("请求 JSON: {Json}", json);

            var response = await _httpClient.PostAsJsonAsync($"{_baseUrl}/api/chat", requestBody);

            _logger.LogDebug("响应状态: {StatusCode}", response.StatusCode);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                _logger.LogError("Ollama API 请求失败: {StatusCode}, {Content}", response.StatusCode, errorContent);
                throw new HttpRequestException($"Ollama API 请求失败: {response.StatusCode}");
            }

            var chatResponse = await response.Content.ReadFromJsonAsync<ChatResponse>();
            if (chatResponse == null)
            {
                throw new InvalidOperationException("无法解析 Ollama 响应");
            }

            return chatResponse.Message;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Ollama 请求失败");
            throw;
        }
    }

    /// <summary>
    /// 执行工具调用
    /// </summary>
    public async Task<string> ExecuteToolCall(ToolCall toolCall, HelloClient helloClient)
    {
        try
        {
            _logger.LogInformation("执行工具调用: {Name}, 参数: {Arguments}", 
                toolCall.Function.Name, 
                JsonSerializer.Serialize(toolCall.Function.Arguments));

            string result;
            if (toolCall.Function.Name == "get_element")
            {
                // Ollama 可能返回 "name" 或 "element_name"
                var name = "";
                if (toolCall.Function.Arguments.TryGetValue("name", out var nameObj))
                {
                    name = nameObj.ToString() ?? "";
                }
                else if (toolCall.Function.Arguments.TryGetValue("element_name", out var elementNameObj))
                {
                    name = elementNameObj.ToString() ?? "";
                }
                
                if (string.IsNullOrEmpty(name))
                {
                    result = JsonSerializer.Serialize(new { error = "缺少参数: name 或 element_name" });
                }
                else
                {
                    result = await helloClient.GetElement(name);
                }
            }
            else if (toolCall.Function.Name == "get_element_by_position")
            {
                if (!toolCall.Function.Arguments.TryGetValue("position", out var positionObj))
                {
                    result = JsonSerializer.Serialize(new { error = "缺少参数: position" });
                }
                else
                {
                    var position = positionObj is JsonElement jsonElement 
                        ? jsonElement.GetInt32() 
                        : Convert.ToInt32(positionObj);
                    result = await helloClient.GetElementByPosition(position);
                }
            }
            else
            {
                result = JsonSerializer.Serialize(new { error = $"未知工具: {toolCall.Function.Name}" });
            }

            _logger.LogInformation("工具调用结果: {Result}", result);
            return result;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "工具调用失败");
            return JsonSerializer.Serialize(new { error = ex.Message });
        }
    }

    public void Dispose()
    {
        _httpClient?.Dispose();
    }
}
