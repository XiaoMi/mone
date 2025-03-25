import re
import pyperclip
import window_util
import pyautogui
import time

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
    print("search")
    hwnds = window_util.find_windows_by_name("企业微信")
    if not hwnds:
        print("企业微信未运行")
        return

    hwnd = hwnds[0]
    window_util.set_window_topmost("企业微信")
    time.sleep(0.5)
    print("ctrl f")
    pyautogui.hotkey('ctrl', 'f')
    time.sleep(1)
    keyboard_input_string(contactName)
    print(contactName)
    time.sleep(1)
    print('enter')
    pyautogui.press('enter')
    time.sleep(1.5)


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


if __name__ == "__main__":
    # 测试代码
    contact_name = "rikaaa0928"  # 请替换为你的企业微信联系人姓名
    message_text = "你好，测试消息"
    searchAndSendWeComMessage(contact_name, message_text)
