# Hello MCP - Swift

Swift实现的MCP服务器和客户端。

## 构建

```bash
swift build
```

## 运行

### 启动MCP服务器

```bash
swift run HelloMCPServer
```

### 运行客户端测试

```bash
swift run HelloMCPClient
```

## 功能

- `get_element`: 根据元素名称获取元素周期表信息
- `get_element_by_position`: 根据原子序数获取元素信息

## 开发

### 运行测试

```bash
swift test
```

### 格式化代码

```bash
swift-format format --in-place Sources/
```

## 注意事项

由于MCP Swift SDK可能还在开发中，本实现提供了基础的结构和功能演示。如果官方SDK可用，请根据官方文档进行调整。
