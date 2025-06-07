# Hello MCP - Python

Python实现的MCP服务器和客户端。

## 安装

```bash
pip install -r requirements.txt
```

## 运行

### 启动MCP服务器

```bash
cd src
python server.py
```

### 运行客户端测试

```bash
cd src
python client.py
```

## 功能

- `get_element`: 根据元素名称获取元素周期表信息
- `get_element_by_position`: 根据原子序数获取元素信息

## 示例

```python
# 获取硅元素信息
result = await session.call_tool("get_element", {"name": "硅"})

# 获取第14号元素信息
result = await session.call_tool("get_element_by_position", {"position": 14})
```

## 测试

```bash
pytest tests/
```
