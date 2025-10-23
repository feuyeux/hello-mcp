# MCP Client

MCP 客户端，包含基础测试和 Ollama 集成测试

## 运行基础测试

```bash
# 运行基础测试
swift run test-client --port 9900
```

## 运行 Ollama 集成测试

```bash
# 运行 Ollama 集成测试
swift run test-ollama --port 9900
```

## 前置要求

### Ollama 测试

1. 安装 Ollama: https://ollama.ai/
2. 启动 Ollama 服务: `ollama serve`
3. 下载模型: `ollama pull qwen2.5:latest`
