#!/bin/bash

echo "════════════════════════════════════════════"
echo "    Hive Manager TUI - 快速安装脚本"
echo "════════════════════════════════════════════"
echo ""

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 未安装 Node.js"
    echo "请从 https://nodejs.org 安装 Node.js 18+"
    exit 1
fi

echo "✓ Node.js $(node -v)"

# 安装依赖
echo ""
echo "正在安装依赖..."
npm install

# 配置环境
if [ ! -f .env ]; then
    echo ""
    echo "创建配置文件..."
    cp .env.example .env
    echo "✓ .env 文件已创建"
fi

# 类型检查
echo ""
echo "运行类型检查..."
npm run type-check

echo ""
echo "════════════════════════════════════════════"
echo "✓ 安装完成！"
echo "════════════════════════════════════════════"
echo ""
echo "启动应用:"
echo "  npm run dev     - 开发模式"
echo "  npm run build   - 构建生产版本"
echo "  npm start       - 运行生产版本"
echo ""
