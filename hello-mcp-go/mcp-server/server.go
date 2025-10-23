package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

func main() {
	// 命令行参数
	port := flag.Int("port", 9900, "Port to listen on for HTTP")
	flag.Parse()

	addr := fmt.Sprintf("127.0.0.1:%d", *port)
	runServer(addr)
}

// GetElementParams 定义 get_element 工具的参数
type GetElementParams struct {
	Name string `json:"name" jsonschema:"元素的中文名称，如'氢'、'氦'等"`
}

// GetElementByPositionParams 定义 get_element_by_position 工具的参数
type GetElementByPositionParams struct {
	Position int `json:"position" jsonschema:"元素的原子序数，范围从1到118"`
}

// getElement 实现根据名称查询元素的工具
func getElement(ctx context.Context, req *mcp.CallToolRequest, params *GetElementParams) (*mcp.CallToolResult, any, error) {
	log.Printf("处理 tools/call 请求: get_element, name=%s", params.Name)

	if params.Name == "" {
		return &mcp.CallToolResult{
			Content: []mcp.Content{
				&mcp.TextContent{Text: "元素名称不能为空"},
			},
			IsError: true,
		}, nil, nil
	}

	element := GetElementByName(params.Name)
	if element == nil {
		return &mcp.CallToolResult{
			Content: []mcp.Content{
				&mcp.TextContent{Text: "元素不存在"},
			},
		}, nil, nil
	}

	resultText := fmt.Sprintf(
		"元素名称: %s (%s, %s), 原子序数: %d, 符号: %s, 相对原子质量: %.3f, 周期: %d, 族: %s",
		element.Name,
		element.Pronunciation,
		element.EnglishName,
		element.AtomicNumber,
		element.Symbol,
		element.AtomicWeight,
		element.Period,
		element.Group,
	)

	return &mcp.CallToolResult{
		Content: []mcp.Content{
			&mcp.TextContent{Text: resultText},
		},
	}, nil, nil
}

// getElementByPosition 实现根据原子序数查询元素的工具
func getElementByPosition(ctx context.Context, req *mcp.CallToolRequest, params *GetElementByPositionParams) (*mcp.CallToolResult, any, error) {
	log.Printf("处理 tools/call 请求: get_element_by_position, position=%d", params.Position)

	if params.Position < 1 || params.Position > 118 {
		return &mcp.CallToolResult{
			Content: []mcp.Content{
				&mcp.TextContent{Text: "原子序数必须在1-118之间"},
			},
			IsError: true,
		}, nil, nil
	}

	element := GetElementByPosition(params.Position)
	if element == nil {
		return &mcp.CallToolResult{
			Content: []mcp.Content{
				&mcp.TextContent{Text: "元素不存在"},
			},
		}, nil, nil
	}

	resultText := fmt.Sprintf(
		"元素名称: %s (%s, %s), 原子序数: %d, 符号: %s, 相对原子质量: %.3f, 周期: %d, 族: %s",
		element.Name,
		element.Pronunciation,
		element.EnglishName,
		element.AtomicNumber,
		element.Symbol,
		element.AtomicWeight,
		element.Period,
		element.Group,
	)

	return &mcp.CallToolResult{
		Content: []mcp.Content{
			&mcp.TextContent{Text: resultText},
		},
	}, nil, nil
}

func runServer(addr string) {
	// 创建 MCP 服务器
	server := mcp.NewServer(&mcp.Implementation{
		Name:    "mcp-server",
		Version: "1.0.0",
	}, nil)

	// 添加工具
	mcp.AddTool(server, &mcp.Tool{
		Name:        "get_element",
		Description: "根据元素名称获取元素周期表元素信息",
	}, getElement)

	mcp.AddTool(server, &mcp.Tool{
		Name:        "get_element_by_position",
		Description: "根据元素在周期表中的位置（原子序数）查询元素信息",
	}, getElementByPosition)

	// 创建 StreamableHTTP 处理器
	handler := mcp.NewStreamableHTTPHandler(func(req *http.Request) *mcp.Server {
		return server
	}, nil)

	// 添加 CORS 中间件
	handlerWithCORS := corsMiddleware(handler)

	log.Printf("Application started with StreamableHTTP session manager on http://%s/mcp", addr)
	log.Printf("Available tools: get_element, get_element_by_position")

	// 启动 HTTP 服务器
	if err := http.ListenAndServe(addr, handlerWithCORS); err != nil {
		log.Fatalf("Server failed: %v", err)
	}
}

// corsMiddleware 添加 CORS 支持
func corsMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Mcp-Session-Id")
		w.Header().Set("Access-Control-Expose-Headers", "Mcp-Session-Id")

		if r.Method == "OPTIONS" {
			w.WriteHeader(http.StatusOK)
			return
		}

		next.ServeHTTP(w, r)
	})
}
