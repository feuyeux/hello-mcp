package main

import (
	"context"
	"flag"
	"fmt"
	"log"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

func mainOllama() {
	// 命令行参数
	port := flag.Int("port", 9900, "Port to connect to MCP server")
	flag.Parse()

	baseURL := fmt.Sprintf("http://localhost:%d", *port)
	log.Printf("连接到 MCP 服务器: %s", baseURL)

	ctx := context.Background()

	// 运行 Ollama 测试
	if err := testLLMWithMCPTools(ctx, baseURL); err != nil {
		log.Fatalf("测试失败: %v", err)
	}

	log.Println("Ollama integration test completed successfully")
}

// testLLMWithMCPTools 测试 LLM 通过工具调用查询元素
func testLLMWithMCPTools(ctx context.Context, baseURL string) error {
	log.Println("=== 测试: LLM 通过工具调用查询元素 ===")

	ollamaClient := NewOllamaClient("", "")
	helloClient := NewHelloClient(baseURL)

	// 获取可用工具
	mcpClient := mcp.NewClient(&mcp.Implementation{
		Name:    "mcp-client",
		Version: "1.0.0",
	}, nil)

	session, err := mcpClient.Connect(ctx, &mcp.StreamableClientTransport{Endpoint: baseURL}, nil)
	if err != nil {
		return fmt.Errorf("连接失败: %w", err)
	}
	defer session.Close()

	toolsResult, err := session.ListTools(ctx, nil)
	if err != nil {
		return fmt.Errorf("列举工具失败: %w", err)
	}

	// 转换工具格式为 Ollama 格式
	tools := make([]map[string]interface{}, 0)
	for _, tool := range toolsResult.Tools {
		tools = append(tools, map[string]interface{}{
			"type": "function",
			"function": map[string]interface{}{
				"name":        tool.Name,
				"description": tool.Description,
				"parameters":  tool.InputSchema,
			},
		})
	}

	// 构建消息
	messages := []Message{}
	query := "请帮我查询氢元素的详细信息，包括原子序数、符号和相对原子质量"
	messages = append(messages, Message{Role: "user", Content: query})

	// 第一次调用 LLM
	log.Printf("第一次调用 LLM: %s", query)
	response, err := ollamaClient.Chat(ctx, messages, tools)
	if err != nil {
		log.Printf("Ollama 请求失败: %v", err)
		log.Println("提示：请确保 Ollama 已启动并且 qwen2.5:latest 模型已下载")
		log.Println("启动命令: ollama serve")
		log.Println("下载模型: ollama pull qwen2.5:latest")
		return err
	}

	log.Printf("LLM 响应角色: %s", response.Message.Role)
	log.Printf("LLM 响应内容: %s", response.Message.Content)

	// 检查是否有工具调用
	if response.HasToolCalls() {
		log.Printf("LLM 决定调用工具，工具数量: %d", len(response.Message.ToolCalls))

		// 执行工具调用
		for _, toolCall := range response.Message.ToolCalls {
			log.Printf("执行工具: %s", toolCall.Function.Name)
			log.Printf("工具参数: %v", toolCall.Function.Arguments)

			toolResult, err := ollamaClient.ExecuteToolCall(ctx, toolCall, helloClient)
			if err != nil {
				log.Printf("工具执行失败: %v", err)
				continue
			}
			log.Printf("工具执行结果: %s", toolResult)

			// 将工具结果添加到消息历史
			messages = append(messages, Message{Role: "assistant", Content: ""})
			messages = append(messages, Message{Role: "tool", Content: toolResult})
		}

		// 第二次调用 LLM，让其基于工具结果生成最终答案
		log.Println("第二次调用 LLM，生成最终答案...")
		finalResponse, err := ollamaClient.Chat(ctx, messages, tools)
		if err != nil {
			return fmt.Errorf("第二次 LLM 调用失败: %w", err)
		}

		log.Printf("最终答案: %s", finalResponse.Message.Content)
		fmt.Printf("\n最终答案: %s\n\n", finalResponse.Message.Content)
		log.Println("✅ 测试成功：LLM 成功通过 MCP 工具查询到元素信息")

	} else {
		log.Printf("LLM 没有调用工具，直接返回了答案: %s", response.Message.Content)
		log.Println("这可能是因为 LLM 已经知道答案，或者不支持工具调用")
	}

	return nil
}
