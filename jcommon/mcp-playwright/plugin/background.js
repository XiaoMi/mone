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
  }
});

// 添加一个定时器来持续输出，确认 service worker 活跃
setInterval(() => {
  console.log("Background script is still running:", new Date().toISOString());
}, 10000);  // 每10秒输出一次
