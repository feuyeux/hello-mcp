package main

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"time"
)

// Message 消息类
type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

// ToolCall 工具调用类
type ToolCall struct {
	Function struct {
		Name      string                 `json:"name"`
		Arguments map[string]interface{} `json:"arguments"`
	} `json:"function"`
}

// ChatResponse 聊天响应类
type ChatResponse struct {
	Message struct {
		Role      string     `json:"role"`
		Content   string     `json:"content"`
		ToolCalls []ToolCall `json:"tool_calls,omitempty"`
	} `json:"message"`
}

// HasToolCalls 检查是否有工具调用
func (r *ChatResponse) HasToolCalls() bool {
	return len(r.Message.ToolCalls) > 0
}

// OllamaClient Ollama 客户端
type OllamaClient struct {
	BaseURL    string
	Model      string
	HTTPClient *http.Client
}

// NewOllamaClient 创建新的 Ollama 客户端
func NewOllamaClient(baseURL, model string) *OllamaClient {
	if baseURL == "" {
		baseURL = "http://localhost:11434"
	}
	if model == "" {
		model = "qwen2.5:latest"
	}
	return &OllamaClient{
		BaseURL: baseURL,
		Model:   model,
		HTTPClient: &http.Client{
			Timeout: 300 * time.Second,
		},
	}
}

// Chat 发送聊天请求
func (c *OllamaClient) Chat(ctx context.Context, messages []Message, tools []map[string]interface{}) (*ChatResponse, error) {
	log.Printf("发送聊天请求到 Ollama: model=%s, messages=%d", c.Model, len(messages))

	// 构建请求体
	requestBody := map[string]interface{}{
		"model":    c.Model,
		"stream":   false,
		"messages": messages,
		"tools":    tools,
	}

	jsonData, err := json.Marshal(requestBody)
	if err != nil {
		return nil, fmt.Errorf("序列化请求失败: %w", err)
	}

	// 发送请求
	req, err := http.NewRequestWithContext(ctx, "POST", c.BaseURL+"/api/chat", bytes.NewBuffer(jsonData))
	if err != nil {
		return nil, fmt.Errorf("创建请求失败: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.HTTPClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("发送请求失败: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("Ollama API 请求失败: %d, %s", resp.StatusCode, string(body))
	}

	// 解析响应
	var chatResp ChatResponse
	if err := json.NewDecoder(resp.Body).Decode(&chatResp); err != nil {
		return nil, fmt.Errorf("解析响应失败: %w", err)
	}

	return &chatResp, nil
}

// ExecuteToolCall 执行工具调用
func (c *OllamaClient) ExecuteToolCall(ctx context.Context, toolCall ToolCall, helloClient *HelloClient) (string, error) {
	log.Printf("执行工具调用: %s, 参数: %v", toolCall.Function.Name, toolCall.Function.Arguments)

	var result string
	var err error

	switch toolCall.Function.Name {
	case "get_element":
		name, ok := toolCall.Function.Arguments["name"].(string)
		if !ok {
			return "", fmt.Errorf("参数 name 类型错误")
		}
		result, err = helloClient.GetElement(ctx, name)

	case "get_element_by_position":
		var position int
		switch v := toolCall.Function.Arguments["position"].(type) {
		case float64:
			position = int(v)
		case int:
			position = v
		default:
			return "", fmt.Errorf("参数 position 类型错误")
		}
		result, err = helloClient.GetElementByPosition(ctx, position)

	default:
		return "", fmt.Errorf("未知工具: %s", toolCall.Function.Name)
	}

	if err != nil {
		log.Printf("工具调用失败: %v", err)
		errorJSON, _ := json.Marshal(map[string]string{"error": err.Error()})
		return string(errorJSON), nil
	}

	log.Printf("工具调用结果: %s", result)
	return result, nil
}
