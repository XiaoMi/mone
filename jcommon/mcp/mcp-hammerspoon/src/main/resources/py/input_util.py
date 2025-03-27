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


if __name__ == "__main__":
    # 测试代码
    contact_name = "rikaaa0928"  # 请替换为你的企业微信联系人姓名
    message_text = "你好，测试消息"
    searchAndSendWeComMessage(contact_name, message_text)
