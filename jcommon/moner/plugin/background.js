import tabManager from './managers/tabManager.js';
import xmlManager from './managers/xmlManager.js';
import stateManager from './managers/stateManager.js';
import actionManager2 from './managers/actionManager2.js';
import screenshotManager from './managers/screenshotManager.js';

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

// 添加全局变量跟踪auto状态
let isAutoMode = false;

// ws地址
let wsUrl = 'ws://localhost:8181/ws';

// config地址
let configUrl = 'http://localhost:8181/config/list';

// 创建 WebSocket 连接函数
function connectWebSocket() {
    // 如果已经有连接，先关闭
    if (ws) {
        ws.close();
    }

    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log('WebSocket connected');
        // 连接成功后重置重连计数
        reconnectAttempts = 0;
        isReconnecting = false;
        // 连接成功后发送 ping
        ws.send('client_connected');
    };

    ws.onmessage = async (event) => {
        let messageWithTimestamp;

        try {
            console.log(event.data);
            const data = JSON.parse(event.data);
            console.log('Received message:', data);

            //当前的tab
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

            if (data && 'data' in data) {
                if (typeof data.data === 'string') {
                    const xmlString = data.data;
                    console.log('XML string:', xmlString);
                    // 这里可以继续处理 xmlString  
                    let actions = xmlManager.parseActions(xmlString);
                    console.log('actions:', actions);

                    //添加一个context(map),用来记录action的执行状态
                    let context = new Map();

                    for (const action of actions || []) {
                        console.log('action:', action);

                        // 处理chat类型的消息
                        if (action.type === 'chat') {
                            console.log('Processing chat message:', action);
                            addMessageToHistory(action.attributes.message);
                            continue;
                        }

                        if (action.type === 'end') {
                            isAutoMode = false;
                            addMessageToHistory('end');
                        }

                        // 处理普通action类型
                        if (action.type === 'action') {
                            console.log('Processing action:', action);
                            const selector = `[browser-user-highlight-id="playwright-highlight-${action.attributes.elementId}"]`;

                            if (action.attributes.name === 'fill') {
                                await chrome.scripting.executeScript({
                                    target: { tabId: tab.id },
                                    func: (selector, value) => {
                                        console.log('fill action:', selector, value);
                                        const element = document.querySelector(selector);
                                        if (element) {
                                            element.value = value;
                                            console.log('fill element:', element);
                                            // Trigger input event to simulate user input
                                            element.dispatchEvent(new Event('input', { bubbles: true }));
                                            element.dispatchEvent(new Event('change', { bubbles: true }));

                                            // 焦点处理  
                                            if (true) element.focus();
                                            if (true) element.blur();

                                        }
                                    },
                                    args: [selector, action.attributes.value]
                                });
                            } else if (action.attributes.name === 'click') {
                                console.log('Executing click action with selector:', selector);
                                await chrome.scripting.executeScript({
                                    target: { tabId: tab.id },
                                    func: (selector) => {
                                        const element = document.querySelector(selector);
                                        if (element) {
                                            element.focus();
                                            element.click();
                                        }
                                    },
                                    args: [selector]
                                });
                            }
                            await new Promise(resolve => setTimeout(resolve, 1999));
                        }

                        //停顿1000ms
                        if (action.type === 'pause') {
                            //停顿1000ms
                            await new Promise(resolve => setTimeout(resolve, 1000));
                        }

                        //滚动一屏
                        if (action.type === 'scrollOneScreen') {
                            console.log('scrollOneScreen action');
                            await chrome.scripting.executeScript({
                                target: { tabId: tab.id },
                                func: () => {
                                    // 获取视口高度
                                    const viewportHeight = window.innerHeight;
                                    // 平滑滚动一屏
                                    window.scrollBy({
                                        top: viewportHeight,
                                        behavior: 'smooth'
                                    });
                                }
                            });
                        }

                        //通知服务器
                        if (action.type === 'notification') {
                            console.log('notification:', action);
                            sendWebSocketMessage("notification", action.attributes.message);
                        }

                        //创建tab页面
                        if (action.type === 'createNewTab') {
                            console.log('click action:', action);
                            // 设置全局auto状态
                            isAutoMode = action.attributes.auto === 'true';
                            // 创建新标签页
                            await tabManager.createNewTab(action.attributes.url);
                        }
                        //截屏
                        if (action.type === 'screenshot') {
                            // 捕获当前视口的截图
                            console.log('takeScreenshot');
                            const screenshot = await chrome.tabs.captureVisibleTab(null, {
                                format: 'jpeg',
                                quality: 10
                            });

                            //提供下载选项
                            if (action.attributes.download === 'true') {
                                await chrome.tabs.sendMessage(tab.id, {
                                    type: 'takeScreenshot',
                                    data: screenshot
                                });
                            }

                            let code = '';
                            //提供发送选项  
                            if (action.attributes.send === 'true') {
                                if (context.has('domTreeData')) {
                                    // 获取domTreeData
                                    const domTreeData = context.get('domTreeData');
                                    code = generateHtmlString(domTreeData);
                                }
                                // 获取domTreeData
                                const domTreeData = context.get('domTreeData');
                                // 将截图数据放入context
                                const messageData = {
                                    code: code,
                                    img: [screenshot]
                                };
                                console.log('send messageData:', messageData);
                                await sendWebSocketMessage(JSON.stringify(messageData), "shopping");
                            }
                        }
                        // 滚动到页面顶部
                        if (action.type === 'scrollToTop') {
                            console.log('scrollToTop');
                            await chrome.scripting.executeScript({
                                target: { tabId: tab.id },
                                function: () => {
                                    window.scrollTo(0, 0);
                                }
                            });
                        }
                        // 滚动到底部
                        if (action.type === 'scrollToBottom') {
                            console.log('scrollToBottom');
                            await chrome.scripting.executeScript({
                                target: { tabId: tab.id },
                                function: () => {
                                    window.scrollTo(0, document.body.scrollHeight);
                                }
                            });
                        }
                        // 截全屏
                        if (action.type === 'screenshotFullPage') {
                            console.log('screenshotFullPage');
                            const screenshot = await screenshotManager.captureFullPage();
                            //提供发送选项  
                            if (action.attributes.send === 'true') {
                                if (context.has('domTreeData')) {
                                    // 获取domTreeData
                                    const domTreeData = context.get('domTreeData');
                                    code = generateHtmlString(domTreeData);
                                }
                                // 获取domTreeData
                                const domTreeData = context.get('domTreeData');
                                // 将截图数据放入context
                                const messageData = {
                                    code: code,
                                    img: [screenshot]
                                };
                                console.log('send messageData:', messageData);
                                await sendWebSocketMessage(JSON.stringify(messageData), "shopping");
                            }
                        }
                        // buildDomTree(从新生成domTree)
                        if (action.type === 'buildDomTree') {
                            console.log('buildDomTree');
                            // 判断action是否有fullPage属性, 且为true
                            let fullPage = false;
                            if (action.attributes.fullPage && action.attributes.fullPage === 'true') {
                                fullPage = true;
                            }

                            // 获取config配置内容，对于config中定义的select的元素，打上对应的kv标记
                            const configs = await getConfigs();
                            await markElements(configs);

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
                                args: [{ doHighlightElements: true, focusHighlightIndex: -1, viewportExpansion: 0 , onlyVisibleArea: !fullPage}]
                            });

                             //TODO$ add
                             new Promise((resolve, reject) => {
                                try {
                                    chrome.storage.local.set({ lastDomTreeData: domTreeData });
                                    resolve();
                                } catch (error) {
                                    reject(error);
                                }
                             });

                            context.set('domTreeData', domTreeData);
                        }
                        //取消重绘
                        if (action.type === 'cancelBuildDomTree') {
                            console.log('cancelBuildDomTree');
                            // 取消重绘
                            await chrome.scripting.executeScript({
                                target: { tabId: tab.id },
                                func: () => {
                                    const container = document.getElementById('playwright-highlight-container');
                                    if (container) {
                                        container.remove();
                                        // Remove highlight IDs from all elements
                                        const highlightedElements = document.querySelectorAll('[browser-user-highlight-id]');
                                        highlightedElements.forEach(element => {
                                            element.removeAttribute('browser-user-highlight-id');
                                        });
                                    }
                                }
                            });
                        }
                    };


                }
            }



            messageWithTimestamp = {
                timestamp: new Date().toLocaleTimeString(),
                data: data,
                type: 'json'
            };


            // 处理通知命令
            if (data.cmd === 'notification') {
                const notificationOptions = {
                    type: data.notificationType || 'info',
                    title: data.title || 'Notification',
                    message: data.message || '',
                    contextMessage: data.contextMessage,
                    buttons: data.buttons,
                    items: data.items,
                    progress: data.progress,
                    duration: data.duration,
                    onClick: data.onClick,
                    onClose: data.onClose
                };

                // 使用 NotificationManager 创建通知
                const notificationId = await notificationManager.create(notificationOptions);
                console.log('Notification created with ID:', notificationId);
            }

        } catch (e) {
            console.log('error Received non-JSON message:', event.data, e);
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



// 定期检查WebSocket连接并发送ping
function startPingCheck() {
    setInterval(() => {
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            console.log('WebSocket connection is not open, attempting to reconnect...');
            forceReconnect();
            return;
        }

        try {
            ws.send('ping');
            console.log('Sent ping to server');
        } catch (error) {
            console.error('Error sending ping:', error);
            forceReconnect();
        }
    }, 10000);
}

// 启动ping检查
startPingCheck();



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
        // 发送消息给content script，不再传递info.x和info.y
        chrome.tabs.sendMessage(tab.id, {
            type: 'toggleSelector',
            active: true
        });
    }
});

// 统一的tab监听处理函数
function setupTabListener(tabId, machineId) {
    let lastUrl = '';
    
    function tabUpdateListener(updatedTabId, changeInfo, updatedTab) {
        if (updatedTabId === tabId && isAutoMode) {
            // 检测URL变化
            if (updatedTab.url && updatedTab.url !== lastUrl) {
                console.log('URL changed from', lastUrl, 'to', updatedTab.url);
                lastUrl = updatedTab.url;
                stateManager.sendMessage(machineId, {
                    type: 'INFO_RECEIVED',
                    tabId: tabId,
                    oldUrl: lastUrl,
                    newUrl: updatedTab.url
                });
            }

            // 检测页面加载完成
            if (changeInfo.status === 'complete') {
                console.log('PAGE_LOADED', updatedTab.url);
                stateManager.sendMessage(machineId, {
                    type: 'PAGE_LOADED',
                    tabId: tabId,
                    url: updatedTab.url
                });
            }
        }
    }

    chrome.tabs.onUpdated.addListener(tabUpdateListener);
    
    // 可选：返回清理函数
    return () => chrome.tabs.onUpdated.removeListener(tabUpdateListener);
}

// 监听新标签页的创建
chrome.tabs.onCreated.addListener((tab) => {
    console.log('New tab created:', tab.id);
    
    // 创建新的状态机实例
    const machineId = stateManager.createMachine();
    const machine = stateManager.getMachine(machineId);
    machine.setTabId(tab.id);
    
    // 如果是auto模式，设置监听器
    if (isAutoMode) {
        setupTabListener(tab.id, machineId);
    }
});


// 修改 sendWebSocketMessage 函数为 Promise 形式
function sendWebSocketMessage(message, cmd = '') {
    return new Promise((resolve, reject) => {
        if (ws && ws.readyState === WebSocket.OPEN) {
            const jsonMessage = {
                from: "chrome",
                data: message,
                cmd: cmd
            };
            try {
                console.log('sendWebSocketMessage:', JSON.stringify(jsonMessage));
                ws.send(JSON.stringify(jsonMessage));
                console.log('Message sent:', jsonMessage);
                resolve();
            } catch (error) {
                console.error('Error sending WebSocket message:', error);
                reject(error);
            }
        } else {
            const error = new Error('WebSocket is not connected');
            console.error(error);
            reject(error);
        }
    });
}

// 修改消息监听器，添加发送消息的处理
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    // 使用 Promise.resolve().then() 来确保异步操作正确处理
    Promise.resolve().then(async () => {
        try {
            if (message.type === 'getMessageHistory') {
                sendResponse(messageHistory);
            } else if (message.type === 'sendWebSocketMessage') {
                await sendWebSocketMessage(message.text);
                sendResponse({ success: true });
            } else if (message.type === 'clearMessageHistory') {
                messageHistory = [];
                await sendWebSocketMessage('clear');
                sendResponse({ success: true });
            } else if (message.type === 'mousePosition') {
                // 存储移动位置
                lastPosition = { x: message.x, y: message.y };
                sendResponse({ success: true });
            } else if (message.type === 'mouseClick') {
                // 存储点击位置
                lastClickPosition = { x: message.x, y: message.y };
                console.log("Click recorded at:", lastClickPosition);
                sendResponse({ success: true });
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
                    quality: 1
                }, function (dataUrl) {
                    chrome.downloads.download({
                        url: dataUrl,
                        filename: 'abc.jpg',
                        saveAs: false
                    }, function (downloadId) {
                        console.log('Screenshot saved with download ID:', downloadId);
                        sendResponse({ success: true, downloadId });
                    });
                });
            } else if (message.type === 'elementSelector') {
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
                sendResponse({ success: true });
            } else if (message.action === 'moveToSelector') {
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
            } else if (message.type === 'captureFullPage') {
                const result = await screenshotManager.captureFullPage();
                sendResponse({ success: result });
            } else {
                sendResponse({ success: false, error: 'Unknown message type' });
            }
        } catch (error) {
            console.error('Error in message listener:', error);
            sendResponse({ success: false, error: error.message });
        }
    });

    // 关键修改：返回 true 表示我们将异步发送响应
    return true;
});

// 添加一个定时器来持续输出，确认 service worker 活跃
setInterval(() => {
    console.log("Background script is still running:", new Date().toISOString());
}, 20000);  // 每20秒输出一次

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

// 修改 generateHtmlString 函数
function generateHtmlString(node, indent = 0) {
    if (!node) return '';

    const indentStr = '    '.repeat(indent);
    let html = '';

    // 处理文本节点
    if (node.type === 'TEXT_NODE') {
        if (node.isVisible && node.text) {
            html += `${indentStr}${node.text.trim()}\n`;
        }
        return html;
    }

    // 处理元素节点
    if (node.tagName) {
        // 只收集可交互且可见的顶层元素
        if (node.isInteractive && node.isVisible && node.isTopElement) {
            html += `${indentStr}<${node.tagName}`;
            
            // 添加 highlight-id 属性（如果存在）
            if (typeof node.highlightIndex === 'number') {
                html += ` browser-user-highlight-id="playwright-highlight-${node.highlightIndex}"`;
            }
            
            // 添加其他重要属性
            if (node.attributes) {
                // 添加 class 属性
                if (node.attributes.class) {
                    html += ` class="${node.attributes.class}"`;
                }
                // 添加 id 属性
                if (node.attributes.id) {
                    html += ` id="${node.attributes.id}"`;
                }
                // 添加 role 属性
                if (node.attributes.role) {
                    html += ` role="${node.attributes.role}"`;
                }
                // 添加 aria-* 属性
                Object.entries(node.attributes).forEach(([key, value]) => {
                    if (key.startsWith('aria-')) {
                        html += ` ${key}="${value}"`;
                    }
                });
            }
            
            html += '>\n';

            // 处理子节点
            if (node.children && Array.isArray(node.children)) {
                node.children.forEach(child => {
                    html += generateHtmlString(child, indent + 1);
                });
            }

            html += `${indentStr}</${node.tagName}>\n`;
        } else {
            // 即使当前节点不需要收集，也要处理其子节点
            if (node.children && Array.isArray(node.children)) {
                node.children.forEach(child => {
                    html += generateHtmlString(child, indent);
                });
            }
        }
    }

    return html;
}

// 修改状态变更监听器，直接使用stateUpdate中的tabId
async function getConfigs() {
  try {
    // 从API获取配置列表
    const response = await fetch(configUrl);
    const configs = await response.json();
    return configs;
  } catch (error) {
    console.error('获取配置失败:', error);
    return [];
  }
}

// 为元素添加标记
async function markElements(tabId, configs) {
  try {
    // 注入markElement.js脚本
    console.log('markElements:', configs);
    await chrome.scripting.executeScript({
      target: { tabId },
      files: ['markElement.js']
    });

    // 执行标记函数
    await chrome.scripting.executeScript({
      target: { tabId },
      func: (configs) => {
        window.markElements(configs);
      },
      args: [configs]
    });
  } catch (error) {
    console.error('标记元素失败:', error);
  }
}

// 在状态变更监听器中添加配置处理
stateManager.addGlobalStateChangeListener(async (stateUpdate) => {
    try {
        // 使用stateUpdate中的tabId，不再需要查询当前tab
        if (!stateUpdate.tabId) {
            console.warn('No tabId in state update:', stateUpdate);
            return;
        }

        // 获取config配置内容，对于config中定义的select的元素，打上对应的kv标记
        const configs = await getConfigs();
        await markElements(stateUpdate.tabId, configs);

        // 执行所需的操作（buildDomTree、截图等）
        await chrome.scripting.executeScript({
            target: { tabId: stateUpdate.tabId },
            files: ['buildDomTree.js']
        });        

        // 从新渲染页面
        const [{ result: domTreeData }] = await chrome.scripting.executeScript({
            target: { tabId: stateUpdate.tabId },
            func: (args) => {
                const buildDomTreeFunc = window['buildDomTree'];
                if (buildDomTreeFunc) {
                    return buildDomTreeFunc(args);
                }
                throw new Error('buildDomTree function not found');
            },
            args: [{ doHighlightElements: true, focusHighlightIndex: -1, viewportExpansion: 0 , onlyVisibleArea: true }]
        });

        // 添加延迟确保页面重绘完成
        await new Promise(resolve => setTimeout(resolve, 500)); // 500ms 延迟

        //截屏的数据
        const screenshot = await chrome.tabs.captureVisibleTab(null, {
            format: 'jpeg',
            quality: 10
        });

        // 取消重绘效果
        await chrome.scripting.executeScript({
            target: { tabId: stateUpdate.tabId },
            func: () => {
                const container = document.getElementById('playwright-highlight-container');
                if (container) {
                    //container.remove();
                }
            }
        });

        // 将 domTreeData 转换为字符串
        const domTreeString = generateHtmlString(domTreeData);

        //TODO$ add
        new Promise((resolve, reject) => {
            try {
                chrome.storage.local.set({ lastDomTreeData: domTreeData });
                resolve();
            } catch (error) {
                reject(error);
            }
        });

        const messageData = {
            code: domTreeString,
            img: [screenshot],
            tabs: await getAllTabsInfo()
        };
        console.log('messageData:', messageData);
        //通知服务器,这个tab发生了变化
        await sendWebSocketMessage(JSON.stringify(messageData), "shopping");
    } catch (error) {
        console.error('Error handling state change:', error);
    }
});


// 获取所有标签页信息的函数
async function getAllTabsInfo() {
    try {
        // 获取当前窗口的所有标签页
        const tabs = await chrome.tabs.query({});
        
        // 映射需要的标签页信息
        return tabs.map(tab => ({
            id: tab.id,
            title: tab.title,
            active: tab.active
        }));
    } catch (error) {
        console.error('Error getting tabs info:', error);
        return [];
    }
}


// 添加新的辅助方法来处理消息历史
function addMessageToHistory(message) {
    messageHistory.push({
        message: message,
        timestamp: new Date().toISOString()
    });
    
    // 如果消息数量超过最大限制，删除最早的消息
    if (messageHistory.length > MAX_MESSAGES) {
        messageHistory.shift();
    }
}
