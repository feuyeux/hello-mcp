using Microsoft.Extensions.Logging;
using ModelContextProtocol.Server;
using System.ComponentModel;

namespace Feuyeux.AI.Hello.Server;

/// <summary>
/// MCP 服务器工具类
/// 使用 MCP SDK 的 Attribute-based API
/// </summary>
[McpServerToolType]
public class PeriodicTableTools
{
    private readonly ILogger<PeriodicTableTools> _logger;

    public PeriodicTableTools(ILogger<PeriodicTableTools> logger)
    {
        _logger = logger;
    }

    [McpServerTool]
    [Description("根据元素名称获取元素周期表元素信息")]
    public string GetElement(
        [Description("元素的中文名称，如'氢'、'氦'等")] string name)
    {
        _logger.LogInformation("查询元素: {Name}", name);

        if (string.IsNullOrWhiteSpace(name))
            return "元素名称不能为空";

        // 尝试按名称、英文名或符号查找
        var element = PeriodicTable.GetElementByName(name) ?? PeriodicTable.GetElement(name);

        if (element == null)
            return "元素不存在";

        return $"元素名称: {element.Name} ({element.Pronunciation}, {element.EnglishName}), " +
               $"原子序数: {element.AtomicNumber}, 符号: {element.Symbol}, " +
               $"相对原子质量: {element.AtomicWeight:F3}, 周期: {element.Period}, " +
               $"族: {element.Group}";
    }

    [McpServerTool]
    [Description("根据元素在周期表中的位置（原子序数）查询元素信息")]
    public string GetElementByPosition(
        [Description("元素的原子序数，范围从1到118")] int position)
    {
        _logger.LogInformation("查询位置元素: {Position}", position);

        if (position < 1 || position > 118)
            return "原子序数必须在1-118之间";

        var element = PeriodicTable.GetElementByPosition(position);

        if (element == null)
            return "元素不存在";

        return $"元素名称: {element.Name} ({element.Pronunciation}, {element.EnglishName}), " +
               $"原子序数: {element.AtomicNumber}, 符号: {element.Symbol}, " +
               $"相对原子质量: {element.AtomicWeight:F3}, 周期: {element.Period}, " +
               $"族: {element.Group}";
    }
}
