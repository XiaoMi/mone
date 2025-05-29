import pyautogui
import time
from datetime import datetime

# 生成带时间戳的文件名
timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
filename = f"screenshot_{timestamp}.png"

# 进行截屏
screenshot = pyautogui.screenshot()

# 保存截图
screenshot.save(filename)
print(f"截图已保存为: {filename}")