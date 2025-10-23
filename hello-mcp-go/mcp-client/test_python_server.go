package main

import (
	"context"
	"fmt"
	"log"
)

// 测试连接 Python 服务器
func testPythonServer() {
	baseURL := "http://localhost:9902/mcp"
	log.Printf("连接到 Python MCP 服务器: %s", baseURL)

	ctx := context.Background()
	client := &HelloClient{
		baseURL:  baseURL,
		endpoint: baseURL,
	}

	// 测试列举工具
	log.Println("=== 测试: 列举工具 ===")
	tools, err := client.ListTools(ctx)
	if err != nil {
		log.Fatalf("列举工具失败: %v", err)
	}
	fmt.Printf("\n列举到的工具:\n%s\n", tools)

	// 测试查询元素
	log.Println("=== 测试: 查询氢元素 ===")
	result, err := client.GetElement(ctx, "氢")
	if err != nil {
		log.Fatalf("查询元素失败: %v", err)
	}
	fmt.Printf("查询结果: %s\n\n", result)

	// 测试按位置查询
	log.Println("=== 测试: 查询原子序数为6的元素 ===")
	result, err = client.GetElementByPosition(ctx, 6)
	if err != nil {
		log.Fatalf("查询位置元素失败: %v", err)
	}
	fmt.Printf("查询结果: %s\n\n", result)

	log.Println("✅ Python 服务器测试成功")
}
