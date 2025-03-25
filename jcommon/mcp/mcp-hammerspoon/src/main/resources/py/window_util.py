import win32gui
import win32con
import win32ui
import base64
import io
import tempfile
import os
from PIL import Image
    
def get_window_info(hwnd):
    """获取窗口信息"""
    title = win32gui.GetWindowText(hwnd)
    class_name = win32gui.GetClassName(hwnd)
    rect = win32gui.GetWindowRect(hwnd)
    return {
        'hwnd': hwnd,
        'title': title,
        'class_name': class_name,
        'rect': rect
    }
    
def list_all_windows():
    """列出所有窗口的标题"""
    def enum_windows_callback(hwnd, window_list):
        title = win32gui.GetWindowText(hwnd)
        if title:
            window_list.append(title)

    window_list = []
    win32gui.EnumWindows(enum_windows_callback, window_list)
    return window_list


def find_windows_by_name(name):
    """查找并返回所有符合条件的窗口句柄"""
    def callback(hwnd, hwnds):
        if win32gui.IsWindowVisible(hwnd):
            info = get_window_info(hwnd)
            if name in info['title']:
                hwnds.append(hwnd)
                # 进一步检查窗口大小，排除一些不正常的窗口
                # rect = info['rect']
                # width = rect[2] - rect[0]
                # height = rect[3] - rect[1]
                # if width > 100 and height > 100:  # 假设正常窗口的最小尺寸
                #     hwnds.append(hwnd)
        return True

    hwnds = []
    win32gui.EnumWindows(callback, hwnds)
    if len(hwnds)>1:
        print("find more than one")
    return hwnds

def set_window_topmost(window_name):
    """将指定名称的窗口置顶"""
    hwnds = find_windows_by_name(window_name)
    if not hwnds:
        raise Exception(f"未找到窗口: {window_name}")
    
    # 默认置顶第一个找到的窗口
    hwnd = hwnds[0]
    win32gui.SetWindowPos(hwnd, win32con.HWND_TOPMOST, 0, 0, 0, 0, win32con.SWP_NOMOVE | win32con.SWP_NOSIZE)
    win32gui.SetForegroundWindow(hwnd)

def capture_window(window_name):
    """截取指定窗口的截图并返回base64编码的图片和mimetype"""
    hwnds = find_windows_by_name(window_name)
    if not hwnds:
        raise Exception(f"未找到窗口: {window_name}")

    # 默认捕获第一个找到的窗口
    hwnd = hwnds[0]
    # 获取窗口大小
    left, top, right, bottom = win32gui.GetWindowRect(hwnd)
    width = right - left
    height = bottom - top

    # 获取窗口设备上下文
    hwndDC = win32gui.GetWindowDC(hwnd)
    mfcDC = win32ui.CreateDCFromHandle(hwndDC)
    saveDC = mfcDC.CreateCompatibleDC()

    # 创建位图对象
    saveBitMap = win32ui.CreateBitmap()
    saveBitMap.CreateCompatibleBitmap(mfcDC, width, height)

    # 将位图选入内存设备上下文
    saveDC.SelectObject(saveBitMap)

    # 从窗口设备上下文复制位图到内存设备上下文
    saveDC.BitBlt((0, 0), (width, height), mfcDC, (0, 0), win32con.SRCCOPY)

    # 创建临时文件
    with tempfile.NamedTemporaryFile(suffix='.bmp', delete=False) as temp_file:
        temp_filename = temp_file.name
    
    try:
        # 保存位图到临时文件
        saveBitMap.SaveBitmapFile(saveDC, temp_filename)
        
        # 释放资源
        win32gui.DeleteObject(saveBitMap.GetHandle())
        saveDC.DeleteDC()
        mfcDC.DeleteDC()
        win32gui.ReleaseDC(hwnd, hwndDC)
        
        # 使用PIL打开BMP图像
        img = Image.open(temp_filename)
        
        # 创建内存文件对象用于保存PNG格式
        output = io.BytesIO()
        img.save(output, format='PNG')
        output.seek(0)
        
        # 将图像转换为base64编码
        img_base64 = base64.b64encode(output.getvalue()).decode('utf-8')
        
        # 返回base64编码的图片和mimetype
        return img_base64, 'image/png'
    finally:
        # 确保临时文件被删除
        if os.path.exists(temp_filename):
            os.unlink(temp_filename)

if __name__ == "__main__":
    try:
        # 查找企业微信窗口
        hwnds = find_windows_by_name("企业微信")
        if hwnds:
            print(f"找到企业微信窗口: {hwnds}")
            for hwnd in hwnds:
                info = get_window_info(hwnd)
                print(f"窗口信息：{info}")
        else:
            print("未找到企业微信窗口")
            
        # 将企业微信窗口置顶
        set_window_topmost("企业微信")  # 请确保企业微信已打开
        
        # 截取企业微信窗口并获取base64编码的图片
        img_base64, mimetype = capture_window("企业微信")
        
        # 将base64解码为二进制数据并保存到文件
        img_data = base64.b64decode(img_base64)
        with open("wecom.png", "wb") as f:
            f.write(img_data)
            
        print(f"测试成功：已将企业微信窗口置顶并截图保存至 wecom.png")
        print(f"图片类型: {mimetype}")
        print(f"Base64编码长度: {len(img_base64)}")
    except Exception as e:
        print(f"测试失败: {e}")
    #print(list_all_windows())
