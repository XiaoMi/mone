import re
import pyperclip
import window_util
import pyautogui
import time
import win32gui
import win32con
import base64
from PIL import Image
import io

def has_unsupported_chars(text):
    pattern = r'[^\x00-\x7F]+'  # 匹配非 ASCII 字符
    return bool(re.search(pattern, text))

def keyboard_input_string(text):
    """
    模拟键盘输入一个字符串
    """
    # if has_unsupported_chars(text):
    pyperclip.copy(text)
    pyautogui.hotkey('ctrl', 'v')
    # else:
    #     pyautogui.typewrite(text)


def searchWeComContact(contactName):
    """
    在企业微信中搜索联系人并打开聊天窗口。
    """
    try:
        print("开始搜索联系人:", contactName)
        hwnds = window_util.find_windows_by_name("企业微信")
        print("找到的企业微信窗口数量:", len(hwnds) if hwnds else 0)
        if not hwnds:
            print("企业微信未运行")
            return

        hwnd = hwnds[0]
        print("选中窗口句柄:", hwnd)
        try:
            window_util.set_window_topmost("企业微信")
            print("设置窗口置顶成功")
        except Exception as e:
            print("设置窗口置顶失败:", str(e))

        time.sleep(0.5)
        print("准备按下 Ctrl+F 进行搜索")
        pyautogui.hotkey('ctrl', 'f')
        time.sleep(1)
        print("输入联系人名称:", contactName)
        keyboard_input_string(contactName)
        print("联系人名称输入完成:", contactName)
        time.sleep(1)
        print("按下回车键选择联系人")
        pyautogui.press('enter')
        print("回车键已按下，等待1.5秒")
        time.sleep(1.5)
        print("搜索联系人完成")
    except Exception as e:
        print("搜索联系人时发生错误:", str(e))
        import traceback
        print("错误详情:", traceback.format_exc())


def sendWeComMessage(message):
    """
    在当前企业微信聊天窗口中发送消息。
    如果消息中包含 :·: 分隔符，则会分割消息并逐条发送，每条消息之间间隔2秒。
    """
    if ":·:" in message:
        # 分割消息并逐条发送
        message_parts = message.split(":·:")
        for part in message_parts:
            if part.strip():  # 跳过空消息
                keyboard_input_string(part)
                time.sleep(0.5)
                pyautogui.press('enter')
                time.sleep(2)  # 每条消息之间间隔2秒
    else:
        # 发送单条消息
        keyboard_input_string(message)
        time.sleep(0.5)
        pyautogui.press('enter')


def searchAndSendWeComMessage(contactName, message):
    """
    搜索企业微信联系人并发送消息。
    """
    # searchWeComContact(contactName)
    sendWeComMessage(message)


def switchToNextUnreadMessage():
    try:
        window_util.set_window_topmost("企业微信")
        print("设置窗口置顶成功")
    except Exception as e:
        print("设置窗口置顶失败:", str(e))
    """
    模拟按下快捷键 "win+alt+下箭头" 切换到下一个未读消息。
    """
    findWorkWechatAndTopMost()
    pyautogui.hotkey('win', 'alt', 'down')

def viewChatContentUpward():
    """
    模拟pageUp快捷键，在聊天框里向上翻页
    """

    findWorkWechatAndTopMost()
    click_window_top_center()
    pyautogui.hotkey('pageup')

def viewChatContentBelow():
    """
    模拟pageDown快捷键，在聊天框里向下翻页
    """
    findWorkWechatAndTopMost()
    click_window_top_center()
    pyautogui.hotkey('pagedown')

def findWorkWechatAndTopMost():
    hwnds = window_util.find_windows_by_name("企业微信")
    print("找到的企业微信窗口数量:", len(hwnds) if hwnds else 0)
    if not hwnds:
        print("企业微信未运行")
        return

    hwnd = hwnds[0]
    print("选中窗口句柄:", hwnd)
    return hwnd
    try:
        window_util.set_window_topmost("企业微信")
        print("设置窗口置顶成功")
    except Exception as e:
        print("设置窗口置顶失败:", str(e))


def is_window_maximized(hwnd):
    """
    检查窗口是否最大化
    :param hwnd: 窗口句柄
    :return: True 如果窗口最大化，False 否则
    """
    try:
        # 获取窗口位置信息
        placement = win32gui.GetWindowPlacement(hwnd)
        # 检查窗口状态
        # 1 表示正常状态，2 表示最小化，3 表示最大化
        is_max = placement[1] == win32con.SW_MAXIMIZE
        print(f"窗口状态: {'最大化' if is_max else '未最大化'}")
        return is_max
    except Exception as e:
        print(f"检查窗口状态失败: {str(e)}")
        return False

def maximize_window(hwnd):
    """
    最大化窗口
    :param hwnd: 窗口句柄
    """
    try:
        win32gui.ShowWindow(hwnd, win32con.SW_MAXIMIZE)
        print("窗口已最大化")
    except Exception as e:
        print(f"最大化窗口失败: {str(e)}")

def click_window_top_center():
    """
    点击置顶窗口顶部中间位置
    """
    try:
        hwnd = findWorkWechatAndTopMost()
        if not hwnd:
            print("未找到企业微信窗口")
            return False

        # 检查窗口是否最大化，如果没有则最大化
        if not is_window_maximized(hwnd):
            maximize_window(hwnd)
            time.sleep(0.5)  # 等待窗口最大化完成

        # 获取窗口位置和大小
        left, top, right, bottom = win32gui.GetWindowRect(hwnd)
        width = right - left
        height = bottom - top

        # 计算顶部中间位置（稍微往下一点，避免点到标题栏）
        center_x = left + width // 2
        center_y = top + 100  # 从顶部往下100像素

        print(f"窗口信息:")
        print(f"- 位置: 左={left}, 上={top}, 右={right}, 下={bottom}")
        print(f"- 大小: 宽={width}, 高={height}")
        print(f"- 点击位置: x={center_x}, y={center_y}")

        # 确保窗口在前台
        win32gui.SetForegroundWindow(hwnd)
        time.sleep(0.5)  # 等待窗口置顶

        # 移动鼠标并点击
        pyautogui.moveTo(center_x, center_y, duration=0.5)  # 增加移动时间
        time.sleep(0.5)  # 等待鼠标移动完成
        
        pyautogui.click()
        time.sleep(0.5)  # 等待点击完成

        # 验证点击是否成功
        current_x, current_y = pyautogui.position()
        print(f"点击后鼠标位置: x={current_x}, y={current_y}")
        
        # 检查窗口是否仍然在前台
        active_window = win32gui.GetForegroundWindow()
        print(f"当前活动窗口: {win32gui.GetWindowText(active_window)}")
        
        return True

    except Exception as e:
        print(f"点击窗口顶部中间位置失败: {str(e)}")
        import traceback
        print("错误详情:", traceback.format_exc())
        return False

def click_input_box():
    """
    点击企业微信聊天窗口的输入框
    """
    try:
        hwnd = findWorkWechatAndTopMost()
        if not hwnd:
            print("未找到企业微信窗口")
            return False

        # 获取窗口位置和大小
        left, top, right, bottom = win32gui.GetWindowRect(hwnd)
        width = right - left
        height = bottom - top

        # 计算输入框位置（在窗口底部，大约上方100像素的位置）
        input_x = left + width // 2
        input_y = bottom - 100

        print(f"输入框位置信息:")
        print(f"- 窗口位置: 左={left}, 上={top}, 右={right}, 下={bottom}")
        print(f"- 窗口大小: 宽={width}, 高={height}")
        print(f"- 点击位置: x={input_x}, y={input_y}")

        # 确保窗口在前台
        win32gui.SetForegroundWindow(hwnd)
        time.sleep(0.5)  # 等待窗口置顶

        # 移动鼠标并点击
        pyautogui.moveTo(input_x, input_y, duration=0.5)
        time.sleep(0.5)
        pyautogui.click()
        time.sleep(0.5)

        # 验证点击是否成功
        current_x, current_y = pyautogui.position()
        print(f"点击后鼠标位置: x={current_x}, y={current_y}")

        # 检查窗口是否仍然在前台
        active_window = win32gui.GetForegroundWindow()
        print(f"当前活动窗口: {win32gui.GetWindowText(active_window)}")

        return True

    except Exception as e:
        print(f"点击输入框失败: {str(e)}")
        import traceback
        print("错误详情:", traceback.format_exc())
        return False

def click_chat_area():
    """
    点击聊天区域
    """
    try:
        hwnd = findWorkWechatAndTopMost()
        if not hwnd:
            print("未找到企业微信窗口")
            return False

        # 检查窗口是否最大化，如果没有则最大化
        if not is_window_maximized(hwnd):
            maximize_window(hwnd)
            time.sleep(0.5)  # 等待窗口最大化完成

        # 获取窗口位置和大小
        left, top, right, bottom = win32gui.GetWindowRect(hwnd)
        width = right - left
        height = bottom - top

        # 计算聊天区域的中心点位置（在窗口中间偏右的位置）
        x = left + int(width * 0.6)  # 水平位置：窗口左边缘 + 窗口宽度的 60%
        y = top + int(height * 0.5)   # 垂直位置：窗口中间

        print(f"窗口信息:")
        print(f"- 位置: 左={left}, 上={top}, 右={right}, 下={bottom}")
        print(f"- 大小: 宽={width}, 高={height}")
        print(f"- 点击位置: x={x}, y={y}")

        # 确保窗口在前台
        win32gui.SetForegroundWindow(hwnd)
        time.sleep(0.1)

        # 移动鼠标并点击
        pyautogui.moveTo(x, y)
        pyautogui.click()

        return True
    except Exception as e:
        print(f"点击聊天区域失败: {str(e)}")
        return False

def merge_images(image1_base64, image2_base64, mime_type1, mime_type2):
    """
    将两张图片垂直拼接成一张图片
    :param image1_base64: 第一张图片的base64编码
    :param image2_base64: 第二张图片的base64编码
    :param mime_type1: 第一张图片的MIME类型
    :param mime_type2: 第二张图片的MIME类型
    :return: 拼接后的图片的base64编码和MIME类型
    """
    try:
        # 解码base64图片
        image1_data = base64.b64decode(image1_base64)
        image2_data = base64.b64decode(image2_base64)
        
        # 将字节数据转换为PIL图像
        image1 = Image.open(io.BytesIO(image1_data))
        image2 = Image.open(io.BytesIO(image2_data))
        
        # 确保两张图片宽度相同
        if image1.width != image2.width:
            # 调整第二张图片的宽度以匹配第一张
            image2 = image2.resize((image1.width, int(image2.height * (image1.width / image2.width))))
        
        # 创建新图片，高度为两张图片高度之和
        merged_image = Image.new('RGB', (image1.width, image1.height + image2.height))
        
        # 粘贴图片
        merged_image.paste(image1, (0, 0))
        merged_image.paste(image2, (0, image1.height))
        
        # 将图片转换为字节
        img_byte_arr = io.BytesIO()
        merged_image.save(img_byte_arr, format='PNG')
        img_byte_arr = img_byte_arr.getvalue()
        
        # 编码为base64
        merged_base64 = base64.b64encode(img_byte_arr).decode('utf-8')
        
        return {
            "image": merged_base64,
            "mimetype": "image/png"
        }
    except Exception as e:
        print(f"图片拼接失败: {str(e)}")
        import traceback
        print("错误详情:", traceback.format_exc())
        raise e

if __name__ == "__main__":
    # 测试代码
    contact_name = "rikaaa0928"  # 请替换为你的企业微信联系人姓名
    message_text = "你好，测试消息"
    searchAndSendWeComMessage(contact_name, message_text)
