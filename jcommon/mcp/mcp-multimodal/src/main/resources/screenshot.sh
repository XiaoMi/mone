#!/bin/bash

# 设置截图保存路径和文件名（使用时间戳命名）
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
SCREENSHOT_DIR="$HOME/Desktop/screenshots"
FILENAME="screenshot_$TIMESTAMP.png"

# 创建保存目录（如果不存在）
mkdir -p "$SCREENSHOT_DIR"

# 使用screencapture命令截取全屏
screencapture -x "$SCREENSHOT_DIR/$FILENAME"

echo "截图已保存至: $SCREENSHOT_DIR/$FILENAME"