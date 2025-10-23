package main

import (
	"context"
	"fmt"
	"log"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

// HelloClient MCP 客户端
type HelloClient struct {
	baseURL  string
	endpoint string
}

// NewHelloClient 创建新的客户端
func NewHelloClient(baseURL string) *HelloClient {
	if baseURL == "" {
		baseURL = "http://localhost:9900"
	}
	return &HelloClient{
		baseURL:  baseURL,
		endpoint: baseURL,
	}
}

// ListTools 列举所有可用工具
func (c *HelloClient) ListTools(ctx context.Context) (string, error) {
	// 创建 MCP 客户端
	client := mcp.NewClient(&mcp.Implementation{
		Name:    "mcp-client",
		Version: "1.0.0",
	}, nil)

	// 连接到服务器
	session, err := client.Connect(ctx, &mcp.StreamableClientTransport{Endpoint: c.endpoint}, nil)
	if err != nil {
		return "", fmt.Errorf("连接失败: %w", err)
	}
	defer session.Close()

	// 列举工具
	result, err := session.ListTools(ctx, nil)
	if err != nil {
		return "", fmt.Errorf("列举工具失败: %w", err)
	}

	var toolsList string
	for _, tool := range result.Tools {
		toolsList += fmt.Sprintf("工具名称: %s, 描述: %s\n", tool.Name, tool.Description)
	}

	log.Printf("列举工具成功:\n%s", toolsList)
	return toolsList, nil
}

// GetElement 根据元素名称查询元素信息
func (c *HelloClient) GetElement(ctx context.Context, name string) (string, error) {
	log.Printf("查询元素: %s", name)

	// 创建 MCP 客户端
	client := mcp.NewClient(&mcp.Implementation{
		Name:    "mcp-client",
		Version: "1.0.0",
	}, nil)

	// 连接到服务器
	session, err := client.Connect(ctx, &mcp.StreamableClientTransport{Endpoint: c.endpoint}, nil)
	if err != nil {
		return "", fmt.Errorf("连接失败: %w", err)
	}
	defer session.Close()

	// 调用工具
	result, err := session.CallTool(ctx, &mcp.CallToolParams{
		Name: "get_element",
		Arguments: map[string]any{
			"name": name,
		},
	})
	if err != nil {
		return "", fmt.Errorf("调用工具失败: %w", err)
	}

	if len(result.Content) == 0 {
		return "", fmt.Errorf("未返回内容")
	}

	var content string
	if textContent, ok := result.Content[0].(*mcp.TextContent); ok {
		content = textContent.Text
	}

	log.Printf("查询元素 %s 成功: %s", name, content)
	return content, nil
}

// GetElementByPosition 根据原子序数查询元素信息
func (c *HelloClient) GetElementByPosition(ctx context.Context, position int) (string, error) {
	log.Printf("查询位置元素: %d", position)

	// 创建 MCP 客户端
	client := mcp.NewClient(&mcp.Implementation{
		Name:    "mcp-client",
		Version: "1.0.0",
	}, nil)

	// 连接到服务器
	session, err := client.Connect(ctx, &mcp.StreamableClientTransport{Endpoint: c.endpoint}, nil)
	if err != nil {
		return "", fmt.Errorf("连接失败: %w", err)
	}
	defer session.Close()

	// 调用工具
	result, err := session.CallTool(ctx, &mcp.CallToolParams{
		Name: "get_element_by_position",
		Arguments: map[string]any{
			"position": position,
		},
	})
	if err != nil {
		return "", fmt.Errorf("调用工具失败: %w", err)
	}

	if len(result.Content) == 0 {
		return "", fmt.Errorf("未返回内容")
	}

	var content string
	if textContent, ok := result.Content[0].(*mcp.TextContent); ok {
		content = textContent.Text
	}

	log.Printf("查询位置元素 %d 成功: %s", position, content)
	return content, nil
}
