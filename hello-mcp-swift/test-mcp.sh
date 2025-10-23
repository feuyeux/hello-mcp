#!/bin/bash

echo "=== 测试1: 健康检查 ==="
curl -s http://localhost:9900/health | jq .
echo ""

echo "=== 测试2: 初始化连接 ==="
curl -s -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"1.0"}}}' | jq .
echo ""

echo "=== 测试3: 列举工具 ==="
curl -s -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}' | jq .
echo ""

echo "=== 测试4: 查询氢元素 ==="
curl -s -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"get_element","arguments":{"name":"氢"}}}' | jq .
echo ""

echo "=== 测试5: 按位置查询碳元素 ==="
curl -s -X POST http://localhost:9900/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"get_element_by_position","arguments":{"position":6}}}' | jq .
echo ""

echo "✅ 所有测试完成！"
