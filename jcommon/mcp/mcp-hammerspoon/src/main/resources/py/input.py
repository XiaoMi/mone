from pywinauto.application import Application


def switchToNextUnreadMessage():
    """
    切换到下一个未读消息。
    使用Windows API直接向企业微信窗口发送按键消息，避免触发系统全局快捷键。
    """
    try:
        
        app = Application().connect(title_re="企业微信")
        for w in app.windows():
            w.send_keystrokes("{VK_LWIN down}%{DOWN}{VK_LWIN up}")  # Ctrl+S
    except Exception as e:
        print("切换未读消息时发生错误:", str(e))
        import traceback
        print("错误详情:", traceback.format_exc())
