#!/bin/bash

# Hello MCP Kotlin构建脚本

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Hello MCP - Kotlin Build Script${NC}"
echo -e "${BLUE}===============================${NC}"

# 清理之前的构建
echo -e "${BLUE}清理之前的构建...${NC}"
gradle clean

# 构建项目
echo -e "${BLUE}构建项目...${NC}"
gradle build

if [ $? -eq 0 ]; then
    echo -e "${GREEN}构建成功！${NC}"
    echo -e "${GREEN}JAR文件位置: build/libs/hello-mcp-kotlin-1.0.0.jar${NC}"
    
    # 运行测试
    echo -e "${BLUE}运行测试...${NC}"
    gradle test
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}所有测试通过！${NC}"
        
        # 显示使用说明
        echo ""
        echo -e "${YELLOW}使用说明:${NC}"
        echo "  gradle run hello  - 测试Hello MCP服务器"
        echo "  gradle run fs     - 测试文件系统MCP"
        echo "  gradle run map    - 测试地图MCP"
        echo "  gradle run all    - 运行所有测试"
        echo "  gradle test       - 运行单元测试"
        echo "  gradle runServer  - 启动MCP服务器"
        
    else
        echo -e "${RED}测试失败${NC}"
        exit 1
    fi
else
    echo -e "${RED}构建失败${NC}"
    exit 1
fi
