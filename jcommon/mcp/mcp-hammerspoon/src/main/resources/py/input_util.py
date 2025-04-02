import re
import pyperclip
import window_util
import pyautogui
import time
import win32gui

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
    """
    keyboard_input_string(message)
    time.sleep(0.5)
    pyautogui.press('enter')


def searchAndSendWeComMessage(contactName, message):
    """
    搜索企业微信联系人并发送消息。
    """
    searchWeComContact(contactName)
    sendWeComMessage(message)


def switchToNextUnreadMessage():
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


def click_window_top_center():
    """
    点击置顶窗口顶部中间位置
    """
    try:

        hwnd = findWorkWechatAndTopMost()
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

        # 移动鼠标并点击
        pyautogui.moveTo(center_x, center_y, duration=0.1)
        pyautogui.click()
        print(f"已点击窗口顶部中间位置")
        return True

    except Exception as e:
        print(f"点击窗口顶部中间位置失败: {str(e)}")
        import traceback
        print("错误详情:", traceback.format_exc())
        return False

if __name__ == "__main__":
    # 测试代码
    contact_name = "rikaaa0928"  # 请替换为你的企业微信联系人姓名
    message_text = "你好，测试消息"
    searchAndSendWeComMessage(contact_name, message_text)
