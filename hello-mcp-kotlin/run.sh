#!/bin/bash

# Hello MCP Kotlin运行脚本

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Hello MCP - Kotlin Implementation${NC}"
echo -e "${BLUE}=================================${NC}"

MODE=${1:-help}

# 显示用法
show_usage() {
    echo -e "${YELLOW}用法: $0 [server|client|test|build|clean|help]${NC}"
    echo ""
    echo "命令:"
    echo "  server  - 启动MCP服务器"
    echo "  client  - 运行MCP客户端（查询氢元素）"
    echo "  test    - 运行客户端测试"
    echo "  build   - 构建项目"
    echo "  clean   - 清理项目"
    echo "  help    - 显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 server"
    echo "  $0 client"
    echo "  $0 test"
}

# 构建项目
build_project() {
    echo -e "${BLUE}构建项目...${NC}"
    gradle build
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}构建成功${NC}"
    else
        echo -e "${RED}构建失败${NC}"
        exit 1
    fi
}

# 执行命令
case $MODE in
    "server")
        echo -e "${BLUE}启动MCP服务器...${NC}"
        build_project
        gradle runServer
        ;;
    "client")
        echo -e "${BLUE}运行MCP客户端测试...${NC}"
        build_project
        gradle runClient
        ;;
    "test")
        echo -e "${BLUE}运行客户端集成测试...${NC}"
        build_project
        ./gradlew runTest
        ;;
    "build")
        build_project
        ;;
    "clean")
        echo -e "${BLUE}清理项目...${NC}"
        ./gradlew clean
        echo -e "${GREEN}清理完成${NC}"
        ;;
    "help")
        show_usage
        ;;
    *)
        echo -e "${RED}未知命令: $MODE${NC}"
        echo ""
        show_usage
        exit 1
        ;;
esac
