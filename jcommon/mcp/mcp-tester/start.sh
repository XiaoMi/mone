#!/bin/bash

# MCP Tester 启动脚本

# 设置环境变量（根据实际情况修改）
export agent_name=${AGENT_NAME:-"mcp-tester"}
export hive_manager_base_url=${HIVE_MANAGER_BASE_URL:-"http://localhost:8080"}
export hive_manager_token=${HIVE_MANAGER_TOKEN:-"your-token"}

# 检查 JAR 文件是否存在
JAR_FILE="target/app.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 找不到 $JAR_FILE"
    echo "请先运行: mvn clean package"
    exit 1
fi

echo "========================================="
echo "MCP Tester 启动中..."
echo "========================================="
echo "Agent Name: $agent_name"
echo "Hive Manager URL: $hive_manager_base_url"
echo "========================================="

# 启动应用
java -Dagent_name="$agent_name" \
     -Dhive_manager_base_url="$hive_manager_base_url" \
     -Dhive_manager_token="$hive_manager_token" \
     -jar "$JAR_FILE"
