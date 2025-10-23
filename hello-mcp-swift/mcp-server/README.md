# MCP Server

元素周期表查询 MCP 服务器

## 运行

```bash
# 使用默认端口
swift run mcp-server

# 使用自定义端口
swift run mcp-server --port 9900

# 自定义日志级别
swift run mcp-server --log-level DEBUG

# 启用 JSON 响应模式
swift run mcp-server --json-response
```

## 可用工具

- `get_element`: 根据元素名称获取元素周期表元素信息
- `get_element_by_position`: 根据元素在周期表中的位置（原子序数）查询元素信息
