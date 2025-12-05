#!/bin/bash

# Hive Manager TUI 启动脚本

cd "$(dirname "$0")"

echo "════════════════════════════════════════════"
echo "    启动 Hive Manager TUI"
echo "════════════════════════════════════════════"
echo ""

# 检查依赖是否已安装
if [ ! -d "node_modules" ]; then
    echo "依赖未安装，正在安装..."
    npm install
    echo ""
fi

# 启动应用
echo "正在启动应用..."
echo ""
npm run dev
