# Hello MCP - Rust

Rust实现的MCP服务器和客户端。

## 构建

```bash
cargo build
```

## 运行

### 启动MCP服务器

```bash
cargo run --bin server
```

### 运行客户端测试

```bash
cargo run --bin client
```

## 功能

- `get_element`: 根据元素名称获取元素周期表信息
- `get_element_by_position`: 根据原子序数获取元素信息

## 开发

### 运行测试

```bash
cargo test
```

### 检查代码

```bash
cargo clippy
```

### 格式化代码

```bash
cargo fmt
```
