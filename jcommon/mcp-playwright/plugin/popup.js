import { captureFullPage } from './screenshotManager.js';

// 等待DOM加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    console.log('Popup script loaded and DOM is ready');
});

// 监听来自contentscript的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'mousePosition') {
        document.getElementById('current-pos').textContent = `X:${message.x}, Y:${message.y}`;
        document.getElementById('x-coord').value = message.x;
        document.getElementById('y-coord').value = message.y;
    } else if (message.type === 'mouseClick') {
        document.getElementById('last-click-pos').textContent = `X:${message.x}, Y:${message.y}`;
    }
});

document.getElementById('click-btn').addEventListener('click', async () => {
    const x = parseInt(document.getElementById('x-coord').value);
    const y = parseInt(document.getElementById('y-coord').value);

    if (isNaN(x) || isNaN(y)) {
        alert('请输入有效的坐标');
        return;
    }

    const [tab] = await chrome.tabs.query({active: true, currentWindow: true});

    await chrome.scripting.executeScript({
        target: {tabId: tab.id},
        func: simulateClick,
        args: [x, y]
    });
});

function simulateClick(x, y) {
    const clickEvent = new MouseEvent('click', {
        view: window,
        bubbles: true,
        cancelable: true,
        clientX: x,
        clientY: y
    });

    document.elementFromPoint(x, y)?.dispatchEvent(clickEvent);
}

// popup 打开时，获取最后存储的位置
chrome.runtime.sendMessage({ type: 'getLastPosition' }, (response) => {
    if (response) {
        // 更新鼠标位置
        document.getElementById('current-pos').textContent = `X:${response.mousePosition.x}, Y:${response.mousePosition.y}`;
        document.getElementById('x-coord').value = response.mousePosition.x;
        document.getElementById('y-coord').value = response.mousePosition.y;
        
        // 更新最后点击位置
        document.getElementById('last-click-pos').textContent = `X:${response.clickPosition.x}, Y:${response.clickPosition.y}`;
    }
});

// 继续监听实时更新
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'mousePosition') {
    // ... 更新UI的代码 ...
  }
});

// 添加截屏按钮事件监听
document.getElementById('captureFullPage').addEventListener('click', async () => {
    try {
        const button = document.getElementById('captureFullPage');
        const statusText = document.getElementById('status-text') || createStatusElement();
        
        button.disabled = true;
        button.textContent = '截图中...';
        statusText.textContent = ''; // 清除之前的状态
        
        await captureFullPage();
        
        button.textContent = '截图成功！';
        statusText.textContent = '✅ 截图已保存';
        
        setTimeout(() => {
            button.disabled = false;
            button.textContent = '📸 截取整页';
            statusText.textContent = '';
        }, 2000);
    } catch (error) {
        console.error('Screenshot failed:', error);
        const button = document.getElementById('captureFullPage');
        const statusText = document.getElementById('status-text') || createStatusElement();
        
        button.textContent = '截图失败';
        statusText.textContent = `❌ 错误: ${error.message}`;
        statusText.style.color = 'red';
        
        setTimeout(() => {
            button.disabled = false;
            button.textContent = '📸 截取整页';
        }, 2000);
    }
});

// 创建状态文本元素的辅助函数
function createStatusElement() {
    const statusText = document.createElement('div');
    statusText.id = 'status-text';
    statusText.style.marginTop = '10px';
    statusText.style.textAlign = 'center';
    document.getElementById('captureFullPage').parentNode.appendChild(statusText);
    return statusText;
}
