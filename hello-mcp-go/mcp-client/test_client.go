package main

import (
	"context"
	"flag"
	"fmt"
	"log"
)

func testClientMain() {
	// 命令行参数
	port := flag.Int("port", 9900, "Port to connect to MCP server")
	flag.Parse()

	baseURL := fmt.Sprintf("http://localhost:%d", *port)
	log.Printf("连接到 MCP 服务器: %s", baseURL)

	ctx := context.Background()

	// 运行所有测试
	if err := runAllTests(ctx, baseURL); err != nil {
		log.Fatalf("测试失败: %v", err)
	}

	log.Println("Client completed successfully")
}

func runAllTests(ctx context.Context, baseURL string) error {
	if err := testListTools(ctx, baseURL); err != nil {
		return err
	}

	if err := testGetElementByName(ctx, baseURL); err != nil {
		return err
	}

	if err := testGetElementByPosition(ctx, baseURL); err != nil {
		return err
	}

	return nil
}

// testListTools 测试1: 列举Hello MCP工具
func testListTools(ctx context.Context, baseURL string) error {
	log.Println("=== 测试1: 列举Hello MCP工具 ===")
	client := NewHelloClient(baseURL)
	tools, err := client.ListTools(ctx)
	if err != nil {
		return fmt.Errorf("列举工具失败: %w", err)
	}
	fmt.Printf("\n列举到的工具:\n%s\n", tools)
	return nil
}

// testGetElementByName 测试2: 测试Hello MCP - 按名称查询
func testGetElementByName(ctx context.Context, baseURL string) error {
	log.Println("=== 测试2: 测试Hello MCP - 按名称查询 ===")
	client := NewHelloClient(baseURL)
	result, err := client.GetElement(ctx, "氢")
	if err != nil {
		return fmt.Errorf("查询元素失败: %w", err)
	}
	fmt.Printf("查询氢元素结果: %s\n\n", result)
	return nil
}

// testGetElementByPosition 测试3: 测试MCP工具调用 - 按位置查询
func testGetElementByPosition(ctx context.Context, baseURL string) error {
	log.Println("=== 测试3: 测试MCP工具调用 - 按位置查询 ===")
	client := NewHelloClient(baseURL)
	result, err := client.GetElementByPosition(ctx, 6)
	if err != nil {
		return fmt.Errorf("查询位置元素失败: %w", err)
	}
	fmt.Printf("查询原子序数为6的元素结果: %s\n\n", result)
	return nil
}
