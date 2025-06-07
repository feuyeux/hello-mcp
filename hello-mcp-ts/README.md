# Hello MCP - TypeScript

TypeScript实现的MCP服务器和客户端。

## 安装依赖

```bash
npm install
```

## 运行

### 启动MCP服务器

```bash
npm run start:server
```

### 运行客户端测试

```bash
npm run start:client
```

## 开发

### 监听模式运行服务器

```bash
npm run dev:server
```

### 监听模式运行客户端

```bash
npm run dev:client
```

## 功能

- `get_element`: 根据元素名称获取元素周期表信息
- `get_element_by_position`: 根据原子序数获取元素信息

## 示例

```typescript
// 获取硅元素信息
const result = await client.callTool({
  name: "get_element",
  arguments: { name: "硅" }
});

// 获取第14号元素信息
const result = await client.callTool({
  name: "get_element_by_position",
  arguments: { position: 14 }
});
```
