import { MoneyEffect } from './moneyEffect.js';
import { injectActionManager } from './inject.js';

console.log("Background script started at:", new Date().toISOString());

// WebSocket 连接
let ws = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 10;
let reconnectTimeout = null;
let isReconnecting = false;

// 在文件开头添加消息存储数组
let messageHistory = [];
const MAX_MESSAGES = 100; // 最多存储100条消息

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

    ws.onmessage = async (event) => {
        let messageWithTimestamp;

        try {
            console.log(event.data);
            const data = JSON.parse(event.data);
            console.log('Received message:', data);

            messageWithTimestamp = {
                timestamp: new Date().toLocaleTimeString(),
                data: data,
                type: 'json'
            };

            // 处理特定命令
            if (data.cmd === 'captureVisibleArea') {
                console.log("captureVisibleArea!!!");
                // 获取当前活动标签页
                const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
                if (!tab) {
                    console.error('No active tab found');
                    return;
                }

                // 执行截屏
                const dataUrl = await chrome.tabs.captureVisibleTab(null, {
                    format: 'jpeg',
                    quality: 90
                });

                // 下载截图
                await chrome.downloads.download({
                    url: dataUrl,
                    filename: `screenshot_${new Date().toISOString().replace(/[:.]/g, '-')}.jpg`,
                    saveAs: false
                });

                console.log('Screenshot captured and saved');
            }

            if (data.cmd === 'buildDomTree') {
                const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
                console.log("a!!!" + tab.id);
                // 重新执行buildDomTree
                await chrome.scripting.executeScript({
                    target: { tabId: tab.id },
                    files: ['buildDomTree.js']
                });

                // 执行buildDomTree函数来重新渲染高亮并获取返回数据
                const [{ result: domTreeData }] = await chrome.scripting.executeScript({
                    target: { tabId: tab.id },
                    func: (args) => {
                        const buildDomTreeFunc = window['buildDomTree'];
                        if (buildDomTreeFunc) {
                            return buildDomTreeFunc(args);
                        } else {
                            throw new Error('buildDomTree函数未找到');
                        }
                    },
                    args: [{ doHighlightElements: true, focusHighlightIndex: -1, viewportExpansion: 0 }]
                });
            }


        } catch (e) {
            console.log('Received non-JSON message:', event.data);
            messageWithTimestamp = {
                timestamp: new Date().toLocaleTimeString(),
                data: event.data,
                type: 'text'
            };
        }

        // 统一的消息处理逻辑
        messageHistory.push(messageWithTimestamp);
        if (messageHistory.length > MAX_MESSAGES) {
            messageHistory.shift();
        }

        // 广播消息给所有打开的popup
        chrome.runtime.sendMessage({
            type: 'newWebSocketMessage',
            message: messageWithTimestamp
        }).catch(error => {
            // 处理接收者不存在的情况
            console.log('No receivers for message:', error);
        });

        // 如果是JSON消息且需要DOM操作
        if (messageWithTimestamp.type === 'json') {
            // 获取当前活动标签页
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
            if (!tab) return;

            // 先注入 actionManager.js
            // await chrome.scripting.executeScript({
            //     target: { tabId: tab.id },
            //     files: ['actionManager.js']
            // });

            //await injectActionManager(tab.id);




            // 然后执行操作
            // await chrome.scripting.executeScript({
            //     target: { tabId: tab.id },
            //     func: async (selector, text) => {
            //         console.log('Executing actions');
            //         // 在页面上下文中执行操作
            //         await window.actionManager.fill(selector, text);
            //         await window.actionManager.click('#su');
            //     },
            //     args: ['#kw', '大熊猫']
            // });
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

// 添加发送消息的函数
function sendWebSocketMessage(message) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        const jsonMessage = {  
            from: "chrome",  
            data: message  
        };  
        ws.send(JSON.stringify(jsonMessage));  
        console.log('Message sent:', jsonMessage); 
    } else {
        console.log('WebSocket is not connected');
    }
}

// 修改消息监听器，添加发送消息的处理
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'getMessageHistory') {
        sendResponse(messageHistory);
        return true;
    } else if (message.type === 'sendWebSocketMessage') {
        sendWebSocketMessage(message.text);
        sendResponse({ success: true });
        return true;
    } else if (message.type === 'clearMessageHistory') {
        messageHistory = [];
        sendResponse({ success: true });
        return true;
    } else if (message.type === 'mousePosition') {
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
        }, function (dataUrl) {
            // 使用 chrome.downloads API 来下载图片
            chrome.downloads.download({
                url: dataUrl,
                filename: 'abc.jpg',  // 改为.jpg后缀
                saveAs: false
            }, function (downloadId) {
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
    } else if (message.action === 'moveToSelector') {
        try {
            chrome.tabs.query({ active: true, currentWindow: true }, async function (tabs) {
                if (!tabs[0]) {
                    sendResponse({ success: false, error: '未找到活动标签页' });
                    return;
                }

                const result = await chrome.scripting.executeScript({
                    target: { tabId: tabs[0].id },
                    func: (selector) => {
                        const element = document.querySelector(selector);
                        if (!element) {
                            return { success: false, error: '未找到指定元素' };
                        }

                        // 滚动到元素位置
                        element.scrollIntoView({ behavior: 'smooth', block: 'center' });

                        // 获取元素的位置信息
                        const rect = element.getBoundingClientRect();
                        const centerX = Math.round(rect.left + rect.width / 2);
                        const centerY = Math.round(rect.top + rect.height / 2);

                        // 创建模拟鼠标指针
                        let fakePointer = document.getElementById('fake-mouse-pointer');
                        if (!fakePointer) {
                            fakePointer = document.createElement('div');
                            fakePointer.id = 'fake-mouse-pointer';
                            fakePointer.style.cssText = `
                                position: fixed;
                                width: 20px;
                                height: 20px;
                                background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20"><path d="M 0 0 L 12 12 L 8 16 L 0 0" fill="black"/></svg>');
                                pointer-events: none;
                                z-index: 999999;
                                transition: all 0.5s ease;
                                opacity: 0;
                            `;
                            document.body.appendChild(fakePointer);
                        }

                        // 设置初始位置（左上角）并显示
                        fakePointer.style.left = '0px';
                        fakePointer.style.top = '0px';
                        fakePointer.style.opacity = '1';

                        // 延迟一帧后移动到目标位置（这样可以看到动画效果）
                        requestAnimationFrame(() => {
                            fakePointer.style.left = `${centerX}px`;
                            fakePointer.style.top = `${centerY}px`;
                        });

                        // 添加元素高亮效果
                        const originalBackground = element.style.backgroundColor;
                        const originalTransition = element.style.transition;
                        element.style.transition = 'background-color 0.3s';
                        element.style.backgroundColor = 'yellow';

                        // 2秒后移除高亮和指针
                        setTimeout(() => {
                            element.style.backgroundColor = originalBackground;
                            element.style.transition = originalTransition;
                            fakePointer.style.opacity = '0';
                            setTimeout(() => fakePointer.remove(), 500);
                        }, 2000);

                        return {
                            success: true,
                            position: {
                                x: centerX,
                                y: centerY
                            }
                        };
                    },
                    args: [message.selector]
                });

                if (result[0].result.success) {
                    lastPosition = result[0].result.position;
                }

                sendResponse(result[0].result);
            });

            return true;
        } catch (error) {
            sendResponse({ success: false, error: error.message });
        }
    }
});

// 添加一个定时器来持续输出，确认 service worker 活跃
setInterval(() => {
    console.log("Background script is still running:", new Date().toISOString());
}, 10000);  // 每10秒输出一次

// 监听扩展图标点击事件
chrome.action.onClicked.addListener((tab) => {
    // 打开侧边栏
    chrome.sidePanel.open({ windowId: tab.windowId });
});

// 确保侧边栏在所有页面都可用
chrome.sidePanel.setPanelBehavior({ openPanelOnActionClick: true });

// 示例：在background script中执行页面操作
chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
    chrome.scripting.executeScript({
        target: { tabId: tabs[0].id },
        function: async () => {
            // 这里的代码将在页面上下文中执行
            const actionManager = new ActionManager();
            await actionManager.click('#some-selector');
        }
    });
});
