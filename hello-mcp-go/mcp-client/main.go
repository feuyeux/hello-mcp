package main

import (
	"fmt"
	"os"
)

func main() {
	// 如果没有参数或第一个参数以 - 开头（是标志），默认使用 test 模式
	if len(os.Args) < 2 || os.Args[1][0] == '-' {
		// 默认运行基础测试
		os.Args = append([]string{os.Args[0], "test"}, os.Args[1:]...)
	}

	mode := os.Args[1]

	// 移除模式参数，保留程序名和其他参数
	// 这样 flag.Parse() 就能正确解析 -port 等参数
	os.Args = append([]string{os.Args[0]}, os.Args[2:]...)

	switch mode {
	case "test":
		testClientMain()
	case "ollama":
		mainOllama()
	default:
		fmt.Fprintf(os.Stderr, "未知模式: %s\n", mode)
		fmt.Fprintf(os.Stderr, "用法: %s <test|ollama> [选项]\n\n", os.Args[0])
		fmt.Fprintf(os.Stderr, "模式:\n")
		fmt.Fprintf(os.Stderr, "  test    - 运行基础 MCP 测试\n")
		fmt.Fprintf(os.Stderr, "  ollama  - 运行 Ollama 集成测试\n\n")
		fmt.Fprintf(os.Stderr, "选项:\n")
		fmt.Fprintf(os.Stderr, "  -port int\n")
		fmt.Fprintf(os.Stderr, "        连接到 MCP 服务器的端口 (默认: 9900)\n\n")
		fmt.Fprintf(os.Stderr, "示例:\n")
		fmt.Fprintf(os.Stderr, "  %s test -port 9900\n", os.Args[0])
		fmt.Fprintf(os.Stderr, "  %s ollama -port 9900\n", os.Args[0])
		fmt.Fprintf(os.Stderr, "  %s -port 9900  (默认为 test 模式)\n", os.Args[0])
		os.Exit(1)
	}
}

