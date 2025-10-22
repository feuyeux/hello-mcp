# MCP Client

MCP TypeScript 客户端实现，用于与 MCP 服务器交互。

## 安装依赖

```bash
npm install
```

## 运行测试

### 基础客户端测试

```bash
# 使用默认端口 3000
npm run test:client

# 指定端口
npm run test:client -- --port 3000
```

### Ollama LLM 集成测试

```bash
# 确保 Ollama 已启动
ollama serve

# 下载模型（如果还没有）
ollama pull qwen2.5:latest

# 运行测试
npm run test:ollama

# 指定端口
npm run test:ollama -- --port 3000
```

## 功能

- `HelloClient` - 基础 MCP 客户端
  - `listTools()` - 列举所有可用工具
  - `getElement(name)` - 根据元素名称查询
  - `getElementByPosition(position)` - 根据原子序数查询

- `OllamaClient` - Ollama LLM 客户端
  - `chat(messages, tools)` - 发送聊天请求
  - `executeToolCall(toolCall, endpoint)` - 执行工具调用
