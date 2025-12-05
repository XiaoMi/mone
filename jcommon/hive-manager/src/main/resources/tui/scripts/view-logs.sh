#!/bin/bash

# 日志查看脚本

cd "$(dirname "$0")/.."

LOG_DIR="logs"
LATEST_LOG=$(ls -t $LOG_DIR/*.log 2>/dev/null | head -n1)

echo "════════════════════════════════════════════"
echo "    Hive Manager TUI - 日志查看器"
echo "════════════════════════════════════════════"
echo ""

if [ -z "$LATEST_LOG" ]; then
    echo "❌ 没有找到日志文件"
    echo ""
    echo "日志文件会在应用运行时自动创建在 logs/ 目录下"
    exit 1
fi

echo "📋 日志文件: $LATEST_LOG"
echo ""

# 提供选项
echo "请选择操作:"
echo "  1) 查看最新 50 行"
echo "  2) 查看完整日志"
echo "  3) 实时跟踪日志 (tail -f)"
echo "  4) 搜索日志"
echo "  5) 清理日志"
echo ""
read -p "选择 (1-5): " choice

case $choice in
    1)
        echo ""
        echo "最新 50 行日志:"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        tail -n 50 "$LATEST_LOG"
        ;;
    2)
        echo ""
        echo "完整日志 (使用 less 查看，按 q 退出):"
        less "$LATEST_LOG"
        ;;
    3)
        echo ""
        echo "实时跟踪日志 (按 Ctrl+C 退出):"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        tail -f "$LATEST_LOG"
        ;;
    4)
        echo ""
        read -p "输入搜索关键词: " keyword
        echo ""
        echo "搜索结果:"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        grep -i "$keyword" "$LATEST_LOG" --color=always
        ;;
    5)
        echo ""
        read -p "确定要删除所有日志文件吗? (y/N): " confirm
        if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
            rm -f $LOG_DIR/*.log
            echo "✓ 日志已清理"
        else
            echo "取消操作"
        fi
        ;;
    *)
        echo "无效选择"
        exit 1
        ;;
esac

echo ""
