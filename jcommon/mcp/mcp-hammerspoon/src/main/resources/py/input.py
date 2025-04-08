from pywinauto.application import Application
import win32gui
import win32con


def switchToNextUnreadMessage():
    """
    切换到下一个未读消息。
    使用Windows API直接向企业微信窗口发送按键消息，避免触发系统全局快捷键。
    """
    try:
        # 获取所有企业微信窗口
        def callback(hwnd, windows):
            if win32gui.IsWindowVisible(hwnd) and "企业微信" in win32gui.GetWindowText(hwnd):
                windows.append(hwnd)
            return True
        
        windows = []
        win32gui.EnumWindows(callback, windows)
        
        if not windows:
            raise Exception("未找到企业微信窗口")
            
        # 选择第一个可见的企业微信窗口
        main_window = windows[0]
        
        # 将窗口置顶
        win32gui.SetWindowPos(main_window, win32con.HWND_TOPMOST, 0, 0, 0, 0, 
                             win32con.SWP_NOMOVE | win32con.SWP_NOSIZE)
        
        # 发送快捷键
        app = Application().connect(handle=main_window)
        app.window(handle=main_window).send_keystrokes("{VK_LWIN down}%{DOWN}{VK_LWIN up}")
        
    except Exception as e:
        print("切换未读消息时发生错误:", str(e))
        import traceback
        print("错误详情:", traceback.format_exc())
