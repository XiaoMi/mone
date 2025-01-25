console.log("Background script started at:", new Date().toISOString());

// WebSocket 连接
let ws = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 10;
let reconnectTimeout = null;
let isReconnecting = false;

// 创建 WebSocket 连接函数
function connectWebSocket() {
    // 如果已经有连接，先关闭
    if (ws) {
        ws.close();
    }

    ws = new WebSocket('ws://127.0.0.1:8181/ws');

    ws.onopen = () => {
        console.log('WebSocket connected');
        // 连接成功后重置重连计数
        reconnectAttempts = 0;
        isReconnecting = false;
        // 连接成功后发送 ping
        ws.send('ping');
    };

    ws.onmessage = (event) => {
        try {
            const data = JSON.parse(event.data);
            console.log('Received message:', data);
            // 根据接收到的数据类型进行处理
            if (data.type === 'some_type') {
                // 处理特定类型的消息
            }
        } catch (e) {
            console.log('Received non-JSON message:', event.data);
        }
    };

    ws.onclose = () => {
        console.log('WebSocket disconnected');
        
        // 如果不是正在重连中，则开始重连
        if (!isReconnecting) {
            reconnectWebSocket();
        }
    };

    ws.onerror = (error) => {
        console.error('WebSocket error:', error);
    };
}

// 重连函数
function reconnectWebSocket() {
    if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
        console.log('Max reconnection attempts reached');
        return;
    }

    if (isReconnecting) {
        return;
    }

    isReconnecting = true;
    reconnectAttempts++;

    // 使用指数退避算法计算延迟时间（1秒、2秒、4秒、8秒...）
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts - 1), 30000);
    
    console.log(`Attempting to reconnect in ${delay}ms (Attempt ${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})`);
    
    clearTimeout(reconnectTimeout);
    reconnectTimeout = setTimeout(() => {
        console.log('Reconnecting...');
        connectWebSocket();
    }, delay);
}

// 手动重连函数
function forceReconnect() {
    reconnectAttempts = 0;
    isReconnecting = false;
    clearTimeout(reconnectTimeout);
    connectWebSocket();
}

// 启动 WebSocket 连接
connectWebSocket();

// 存储最后的鼠标位置和点击位置
let lastPosition = { x: 0, y: 0 };
let lastClickPosition = { x: 0, y: 0 };

// 创建右键菜单
chrome.runtime.onInstalled.addListener(() => {
    chrome.contextMenus.create({
        id: "getSelectorMenu",
        title: "获取元素选择器",
        contexts: ["all"]  // 在所有内容上显示
    });
});

// 监听右键菜单点击事件
chrome.contextMenus.onClicked.addListener((info, tab) => {
    if (info.menuItemId === "getSelectorMenu") {
        // 发送消息给content script
        chrome.tabs.sendMessage(tab.id, {
            type: 'toggleSelector',
            active: true,
            x: info.x,  // 添加点击位置信息
            y: info.y
        });
    }
});

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'mousePosition') {
    // 存储移动位置
    lastPosition = { x: message.x, y: message.y };
  } else if (message.type === 'mouseClick') {
    // 存储点击位置
    lastClickPosition = { x: message.x, y: message.y };
    console.log("Click recorded at:", lastClickPosition);
  } else if (message.type === 'getLastPosition') {
    // popup 打开时可以获取最后存储的位置
    sendResponse({
      mousePosition: lastPosition,
      clickPosition: lastClickPosition
    });
  } else if (message.type === 'captureScreen') {
    console.log("captureScreen");
    chrome.tabs.captureVisibleTab(null, {
      format: 'jpeg',
      quality: 1  // 1表示最低质量
    }, function(dataUrl) {
      // 使用 chrome.downloads API 来下载图片
      chrome.downloads.download({
        url: dataUrl,
        filename: 'abc.jpg',  // 改为.jpg后缀
        saveAs: false
      }, function(downloadId) {
        console.log('Screenshot saved with download ID:', downloadId);
      });
    });
  } else if (message.type === 'elementSelector') {
    // 向当前标签页注入并执行显示对话框的脚本
    chrome.scripting.executeScript({
        target: { tabId: sender.tab.id },
        function: (selector) => {
            // 创建对话框元素
            const dialog = document.createElement('div');
            dialog.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 15px;
                background: white;
                border: 1px solid #ccc;
                border-radius: 5px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                z-index: 10000;
                max-width: 300px;
                word-break: break-all;
                font-family: Arial, sans-serif;
            `;
            
            // 添加标题
            const title = document.createElement('div');
            title.style.cssText = `
                font-weight: bold;
                margin-bottom: 10px;
                padding-bottom: 5px;
                border-bottom: 1px solid #eee;
            `;
            title.textContent = 'Element Selector';
            dialog.appendChild(title);
            
            // 添加选择器内容
            const content = document.createElement('div');
            content.style.cssText = `
                margin-bottom: 10px;
                color: #333;
            `;
            content.textContent = selector;
            dialog.appendChild(content);
            
            // 添加复制按钮
            const copyButton = document.createElement('button');
            copyButton.style.cssText = `
                padding: 5px 10px;
                background: #4CAF50;
                color: white;
                border: none;
                border-radius: 3px;
                cursor: pointer;
                margin-right: 5px;
            `;
            copyButton.textContent = 'Copy';
            copyButton.onclick = () => {
                navigator.clipboard.writeText(selector);
                copyButton.textContent = 'Copied!';
                setTimeout(() => copyButton.textContent = 'Copy', 1500);
            };
            dialog.appendChild(copyButton);
            
            // 添加关闭按钮
            const closeButton = document.createElement('button');
            closeButton.style.cssText = `
                padding: 5px 10px;
                background: #f44336;
                color: white;
                border: none;
                border-radius: 3px;
                cursor: pointer;
            `;
            closeButton.textContent = 'Close';
            closeButton.onclick = () => dialog.remove();
            dialog.appendChild(closeButton);
            
            // 添加到页面
            document.body.appendChild(dialog);
            
            // 5秒后自动关闭
            setTimeout(() => dialog.remove(), 5000);
        },
        args: [message.selector]
    });
  }
});

// 添加一个定时器来持续输出，确认 service worker 活跃
setInterval(() => {
  console.log("Background script is still running:", new Date().toISOString());
}, 10000);  // 每10秒输出一次
