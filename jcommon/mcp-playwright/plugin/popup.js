import bookmarkManager from './managers/bookmarkManager.js';
import MoneyEffect from './moneyEffect.js';
import { MouseTracker } from './mouseTracker.js';
import historyManager from './managers/historyManager.js';
import errorManager from './errorManager.js';
import storageManager from './managers/storageManager.js';


// 等待DOM加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    console.log('Popup script loaded and DOM is ready');
    
    // 获取实时消息容器
    const realtimeMessages = document.getElementById('realtime-messages');

    // 监听来自 background script 的消息
    chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
        if (message.type === 'newWebSocketMessage') {
            // 创建新的消息元素
            const messageElement = document.createElement('div');
            messageElement.className = 'message-item';
            
            // 直接使用原始内容  
        let content = message?.message?.data || {};   

        if (content instanceof Object) {
            content = content['data']?.toString();
        }

        console.log(content);
        
    

        content = content  
                    .replace(/</g, '&lt;')  // 转义 < 符号  
                    .replace(/>/g, '&gt;')  // 转义 > 符号  
                    .replace(/\n/g, '<br>') // 保持换行  
                    .replace(/\s/g, '&nbsp;'); // 保持空格  
            
        

            messageElement.innerHTML = `  
            <div class="message-time">${message.message.timestamp}</div>  
            <pre class="message-content">${content}</pre>  
        `;  

            
            // 添加到消息容器的顶部
            realtimeMessages.insertBefore(messageElement, realtimeMessages.firstChild);
            
            // 限制显示的消息数量
            if (realtimeMessages.children.length > 100) {
                realtimeMessages.removeChild(realtimeMessages.lastChild);
            }
        }
    });

    // 添加清除按钮功能
    document.getElementById('clear-messages')?.addEventListener('click', () => {
        console.log('clear-messages');
        realtimeMessages.innerHTML = '';
        chrome.runtime.sendMessage({ type: 'clearMessageHistory' });
    });
    
    // 添加关闭按钮事件监听
    document.getElementById('close-sidebar').addEventListener('click', () => {
        chrome.sidePanel.close();
    });
    
    // 添加显示标签页按钮的事件监听
    document.getElementById('showTabs').addEventListener('click', async () => {
        try {
            const tabs = await getAllTabs({ currentWindow: true });
            const tabsList = document.getElementById('tabs-list');
            
            // 清空之前的列表
            tabsList.innerHTML = '';
            
            // 创建标签页列表
            const ul = document.createElement('ul');
            ul.style.listStyle = 'none';
            ul.style.padding = '10px';
            ul.style.margin = '10px 0';
            ul.style.maxHeight = '200px';
            ul.style.overflowY = 'auto';
            ul.style.border = '1px solid #ccc';
            ul.style.borderRadius = '4px';
            
            tabs.forEach((tab, index) => {
                const li = document.createElement('li');
                li.textContent = `${index + 1}. ${tab.title}`;
                li.style.padding = '5px 0';
                li.style.borderBottom = '1px solid #eee';
                li.style.fontSize = '12px';
                li.style.whiteSpace = 'nowrap';
                li.style.overflow = 'hidden';
                li.style.textOverflow = 'ellipsis';
                ul.appendChild(li);
            });
            
            tabsList.appendChild(ul);
        } catch (error) {
            console.error('Error showing tabs:', error);
            document.getElementById('tabs-list').innerHTML = 
                `<p style="color: red;">获取标签页失败: ${error.message}</p>`;
        }
    });

    // 修改下雪特效按钮事件监听
    document.getElementById('snowEffect').addEventListener('click', async () => {
        const button = document.getElementById('snowEffect');
        const isEffectOn = await MoneyEffect.toggleEffect();
        button.textContent = isEffectOn ? '❄️ 关闭下雪' : '❄️ 下雪特效';
    });

    // 添加边框按钮事件监听
    document.getElementById('addBorders').addEventListener('click', async () => {
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        
        chrome.scripting.executeScript({
            target: { tabId: tab.id },
            function: toggleBorders
        });
    });

    let isTracking = false;
    const trackerButton = document.getElementById('toggleMouseTracker');
    
    trackerButton.addEventListener('click', async () => {
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        isTracking = !isTracking;
        
        if (isTracking) {
            await MouseTracker.injectTracker(tab.id);
            trackerButton.textContent = '🔴 关闭虚拟鼠标';
            trackerButton.classList.add('active');
        } else {
            await MouseTracker.removeTracker(tab.id);
            trackerButton.textContent = '🔴 虚拟鼠标跟踪';
            trackerButton.classList.remove('active');
        }
    });

    // 在功能按钮区域添加移动到元素功能
    document.getElementById('move-to-selector').addEventListener('click', async () => {
        const selector = document.getElementById('selector-input').value;
        if (!selector) {
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '❌ 请输入选择器';
            statusText.style.color = 'red';
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);
            return;
        }

        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        const statusText = document.getElementById('status-text') || createStatusElement();
        const button = document.getElementById('move-to-selector');
        
        try {
            button.disabled = true;
            button.textContent = '移动中...';
            
            // 执行选择器查找和位置计算
            const [{result}] = await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                function: (sel) => {
                    const element = document.querySelector(sel);
                    if (!element) return null;
                    
                    const rect = element.getBoundingClientRect();
                    return {
                        x: Math.round(rect.left + rect.width / 2),
                        y: Math.round(rect.top + rect.height / 2)
                    };
                },
                args: [selector]
            });

            if (result) {
                await MouseTracker.moveToPosition(tab.id, result.x, result.y);
                
                // 获取存储的位置信息
                const storedPosition = await storageManager.get(`mousePosition_${tab.id}`);
                if (storedPosition) {
                    button.textContent = `🎯 移动到元素 (${storedPosition.x}, ${storedPosition.y})`;
                } else {
                    button.textContent = '🎯 移动到元素';
                }

                statusText.textContent = '✅ 已移动到元素位置';
                statusText.style.color = '#4CAF50';
                
                // 3秒后恢复跟踪
                setTimeout(async () => {
                    await MouseTracker.resumeTracking(tab.id);
                    statusText.textContent = '';
                    // 保持坐标显示，不重置按钮文本
                }, 3000);
            } else {
                statusText.textContent = '❌ 未找到匹配的元素';
                statusText.style.color = 'red';
                button.textContent = '🎯 移动到元素';
                setTimeout(() => {
                    statusText.textContent = '';
                }, 3000);
            }
        } catch (error) {
            statusText.textContent = '❌ 发生错误: ' + error.message;
            statusText.style.color = 'red';
            button.textContent = '🎯 移动到元素';
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);
        } finally {
            button.disabled = false;
        }
    });

    // 添加查看DOM树按钮事件监听
    document.getElementById('viewDomTree').addEventListener('click', () => {
        chrome.windows.create({
            url: 'tree-viewer.html',
            type: 'popup',
            width: 800,
            height: 600
        });
    });

    // Add this in your event listeners setup
    document.getElementById('testError').addEventListener('click', () => {
        console.log('testError');
        // Test different types of errors
        errorManager.info('This is an info message');
        errorManager.warning('This is a warning message');
        errorManager.error('This is an error message');
        
        // Simulate a runtime error
        try {
            throw new Error('This is a simulated error');
        } catch (e) {
            errorManager.error('Caught a simulated error', e);
        }
        
        // Test fatal error (this will open error page)
        errorManager.fatal('This is a fatal error message');
    });

    // 添加获取最近历史记录按钮事件监听
    document.getElementById('getRecentHistory').addEventListener('click', async () => {
        try {
            // 获取最近3条历史记录
            const recentHistory = await historyManager.getRecentHistory(3);
            
            // 使用 errorManager 记录信息
            recentHistory.forEach((item, index) => {
                const timestamp = new Date(item.lastVisitTime).toLocaleString();
                errorManager.info(`最近访问 ${index + 1}: ${item.title}\n链接: ${item.url}\n时间: ${timestamp}`);
            });

            // 更新状态文本
            const statusText = document.getElementById('status-text');
            statusText.textContent = '✅ 已获取最近历史记录';
            statusText.style.color = '#4CAF50';
            
            // 3秒后清除状态信息
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);

        } catch (error) {
            console.error('获取历史记录失败:', error);
            errorManager.error('获取历史记录失败', error);
        }
    });

    // 添加获取书签统计信息按钮事件监听
    document.getElementById('getBookmarkStats').addEventListener('click', async () => {
        try {
            const stats = await bookmarkManager.getBookmarkStats();
            
            // 使用errorManager记录信息
            errorManager.info('=== 书签统计信息 ===');
            errorManager.info(`总书签数: ${stats.totalBookmarks}`);
            errorManager.info(`总文件夹数: ${stats.totalFolders}`);
            
            if (stats.mostRecentBookmark) {
                const recentDate = new Date(stats.mostRecentBookmark.dateAdded).toLocaleString();
                errorManager.info(`最近添加的书签: ${stats.mostRecentBookmark.title}\n添加时间: ${recentDate}`);
            }
            
            if (stats.oldestBookmark) {
                const oldestDate = new Date(stats.oldestBookmark.dateAdded).toLocaleString();
                errorManager.info(`最早添加的书签: ${stats.oldestBookmark.title}\n添加时间: ${oldestDate}`);
            }
            
            errorManager.info(`平均文件夹深度: ${stats.averageDepth}`);

            // 更新状态文本
            const statusText = document.getElementById('status-text');
            statusText.textContent = '✅ 书签统计信息已生成';
            statusText.style.color = '#4CAF50';
            
            // 3秒后清除状态信息
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);

        } catch (error) {
            console.error('获取书签统计信息失败:', error);
            errorManager.error('获取书签统计信息失败', error);
            
            // 更新状态文本显示错误
            const statusText = document.getElementById('status-text');
            statusText.textContent = '❌ 获取书签统计失败';
            statusText.style.color = 'red';
        }
    });

    // 添加测试按钮事件监听
    document.getElementById('actionTest').addEventListener('click', async () => {
        try {
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

            errorManager.info('开始测试操作序列');
            
            // 先注入 actionManager
            await injectActionManager(tab.id);
            
            // 修改执行脚本部分
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: async () => {
                    try {
                        // 使用一个变量来控制使用哪个搜索引擎
                        const searchEngine = 'baidu'; // 或 'baidu'
                        if (searchEngine === 'baidu') {
                            await window.actionManager.fill('#kw', '大熊猫');
                            await window.actionManager.click('#su');
                        } else if (searchEngine === 'bing') {
                            await window.actionManager.fill('#sb_form_q', '大熊猫');
                            await window.actionManager.enter('#sb_form_q');
                        }

                        return { success: true, message: '操作序列执行完成' };
                    } catch (error) {
                        console.error('Error executing action sequence:', error);
                        return { success: false, error: error.message };
                    }
                }
            });

            // 更新状态文本
            const statusText = document.getElementById('status-text');
            statusText.textContent = '✅ 操作序列执行成功';
            statusText.style.color = '#4CAF50';
            
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);

        } catch (error) {
            console.error('Error:', error);
            const statusText = document.getElementById('status-text');
            statusText.textContent = '❌ 操作执行失败: ' + error.message;
            statusText.style.color = 'red';
        }
    });

    // 修改滚动一屏按钮的事件监听
    document.getElementById('scrollOneScreen').addEventListener('click', async () => {
        errorManager.info('开始滚动一屏');
        try {
            // 获取当前标签页
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
            
            // 先注入 scrollManager.js
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                files: ['managers/scrollManager.js']
            });

            // 执行滚动操作
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: async () => {
                    // 等待 scrollManager 模块加载完成
                    const scrollManager = await import(chrome.runtime.getURL('managers/scrollManager.js'))
                        .then(module => module.default);
                    
                    // 执行滚动一屏
                    await scrollManager.scrollOneScreen('down', { behavior: 'smooth' });
                }
            });

            // 更新状态文本
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '✅ 滚动完成';
            statusText.style.color = '#4CAF50';
            
            setTimeout(() => {
                statusText.textContent = '';
            }, 2000);

        } catch (error) {
            errorManager.error('滚动一屏失败：' + error.message, error);
            
            // 显示错误状态
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '❌ 滚动失败';
            statusText.style.color = 'red';
            
            setTimeout(() => {
                statusText.textContent = '';
            }, 2000);
        }
    });

    // 添加折叠功能
    const buttonContainerHeader = document.querySelector('.button-container-header');
    const buttonContainer = document.querySelector('.button-container');
    const collapseArrow = document.querySelector('.collapse-arrow');
    
    buttonContainerHeader.addEventListener('click', function() {
        buttonContainer.classList.toggle('collapsed');
        collapseArrow.classList.toggle('expanded');
    });

    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');

    // 处理发送按钮点击
    sendButton.addEventListener('click', sendMessage);

    // 处理回车键发送
    messageInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    function sendMessage() {
        const message = messageInput.value.trim();
        if (message) {
            chrome.runtime.sendMessage({
                type: 'sendWebSocketMessage',
                text: message
            }, response => {
                if (response.success) {
                    messageInput.value = ''; // 清空输入框
                }
            });
        }
    }

    // 修改获取元素引用部分
    const actionTypeSelect = document.getElementById('action-type');
    const actionSelector = document.getElementById('action-selector'); // 新的选择器输入框
    const fillContentInput = document.getElementById('fill-content');
    const executeActionButton = document.getElementById('execute-action');

    // 监听动作类型变化
    actionTypeSelect.addEventListener('change', () => {
        // 当选择 "fill" 时显示内容输入框
        fillContentInput.style.display = actionTypeSelect.value === 'fill' ? 'block' : 'none';
    });

    // 执行操作按钮点击事件
    executeActionButton.addEventListener('click', async () => {
        const actionType = actionTypeSelect.value;
        const selector = actionSelector.value.trim(); // 使用新的选择器输入框
        const content = fillContentInput.value.trim();
        
        if (!selector) {
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '❌ 请输入选择器';
            statusText.style.color = 'red';
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);
            return;
        }

        try {
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
            
            // 先注入 actionManager
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                files: ['managers/actionManager.js']
            });

            // 执行选定的操作
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: async (type, sel, cont) => {
                    try {
                        switch (type) {
                            case 'click':
                                await window.actionManager.click(sel);
                                break;
                            case 'enter':
                                await window.actionManager.enter(sel);
                                break;
                            case 'fill':
                                await window.actionManager.fill(sel, cont);
                                break;
                        }
                        return { success: true };
                    } catch (error) {
                        return { success: false, error: error.message };
                    }
                },
                args: [actionType, selector, content]
            });

            // 显示成功消息
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '✅ 操作执行成功';
            statusText.style.color = '#4CAF50';
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);

        } catch (error) {
            console.error('操作执行失败:', error);
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = `❌ 操作失败: ${error.message}`;
            statusText.style.color = 'red';
            setTimeout(() => {
                statusText.textContent = '';
            }, 3000);
        }
    });
});

// 监听来自contentscript的消息
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'mousePosition') {
        document.getElementById('current-pos').textContent = `X:${message.x}, Y:${message.y}`;
        document.getElementById('x-coord').value = message.x;
        document.getElementById('y-coord').value = message.y;
    } else if (message.type === 'mouseClick') {
        document.getElementById('last-click-pos').textContent = `X:${message.x}, Y:${message.y}`;
    } else if (message.type === 'selectorCopied') {
        const statusText = document.getElementById('status-text') || createStatusElement();
        statusText.textContent = '✅ 选择器已复制到剪贴板';
        statusText.style.color = '#4CAF50';
        
        setTimeout(() => {
            statusText.textContent = '';
        }, 2000);
    } else if (message.type === 'elementSelector') {
        // 更新两个选择器输入框的值
        const selectorInput = document.getElementById('selector-input');
        const actionSelector = document.getElementById('action-selector');
        if (selectorInput && actionSelector) {
            selectorInput.value = message.selector;
            actionSelector.value = message.selector;
            
            // 添加视觉反馈
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '✅ 已更新选择器: ' + message.selector;
            statusText.style.color = '#4CAF50';
            
            setTimeout(() => {
                statusText.textContent = '';
            }, 2000);
        }
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
    await MouseTracker.moveToPosition(tab.id, x, y);
});

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

// 添加自动滚动按钮事件监听
document.getElementById('autoScroll').addEventListener('click', async () => {
    const [tab] = await chrome.tabs.query({active: true, currentWindow: true});
    
    // 执行自动滚动脚本
    await chrome.scripting.executeScript({
        target: {tabId: tab.id},
        function: autoScrollPage
    });
});

// 自动滚动函数
function autoScrollPage() {
    let lastScrollTop = -1;
    const scrollInterval = setInterval(() => {
        // 每次滚动100像素
        window.scrollBy(0, 100);
        
        // 如果滚动位置没有变化，说明已经到底了
        if (lastScrollTop === window.scrollY) {
            clearInterval(scrollInterval);
            console.log('Reached bottom of page');
            return;
        }
        
        lastScrollTop = window.scrollY;
    }, 100); // 每100毫秒滚动一次
}

// 修改边框处理函数
function toggleBorders() {
    if (!window._borderManager) {
        class BorderManager {
            constructor() {
                this.borderedElements = new Set();
                this.isActive = false;
                // this.VALID_ELEMENTS = ['div', 'section', 'article', 'main', 'aside', 'header', 'footer', 'nav' , 'input'];
                this.VALID_ELEMENTS = ['input','button','textarea','div'];
                this.MIN_ELEMENT_SIZE = 30;
                this.MAX_ELEMENT_SIZE = 600; // 添加最大尺寸限制
                this.tooltipStyles = `
                    position: absolute;
                    background: rgba(0, 0, 0, 0.8);
                    color: white;
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    z-index: 10000;
                    pointer-events: none;
                    max-width: 300px;
                    word-break: break-all;
                `;
            }

            isValidElement(element) {
                if (!element || !element.getBoundingClientRect) {
                    return false;
                }

                const rect = element.getBoundingClientRect();
                
                // 检查元素是否可见
                if (rect.width === 0 || rect.height === 0) {
                    return false;
                }

                // 检查元素尺寸是否在合适范围内
                // 至少有一个维度在最小和最大尺寸之间
                const hasValidWidth = rect.width >= this.MIN_ELEMENT_SIZE && rect.width <= this.MAX_ELEMENT_SIZE;
                const hasValidHeight = rect.height >= this.MIN_ELEMENT_SIZE && rect.height <= this.MAX_ELEMENT_SIZE;
                
                if (!hasValidWidth && !hasValidHeight) {
                    return false;
                }

                if (!this.VALID_ELEMENTS.includes(element.tagName.toLowerCase())) {
                    return false;
                }

                // 检查父元素是否已有边框
                let parent = element.parentElement;
                while (parent) {
                    if (this.borderedElements.has(parent)) {
                        return false;
                    }
                    parent = parent.parentElement;
                }

                return true;
            }

            // 获取元素的唯一选择器
            getSelector(element) {
                // 1. 如果有 id，直接返回
                if (element.id) {
                    return '#' + element.id;
                }
                
                // 2. 尝试使用 class 组合
                if (element.className) {
                    const classes = Array.from(element.classList)
                        .filter(cls => cls && !cls.includes(' '))  // 过滤掉空类名和包含空格的类名
                        .join('.');
                        
                    if (classes) {
                        // 检查使用这些 class 是否能唯一定位到元素
                        const selector = '.' + classes;
                        const elements = document.querySelectorAll(selector);
                        if (elements.length === 1) {
                            return selector;
                        }
                    }
                }
                
                // 3. 尝试标签名 + class 组合
                if (element.className) {
                    const selector = `${element.tagName.toLowerCase()}.${Array.from(element.classList)
                        .filter(cls => cls && !cls.includes(' '))
                        .join('.')}`;
                    const elements = document.querySelectorAll(selector);
                    if (elements.length === 1) {
                        return selector;
                    }
                }
                
                // 4. 如果上述方法都不能唯一定位，则生成最短的层级选择器
                let current = element;
                let path = [];
                
                while (current && current.tagName) {
                    let selector = current.tagName.toLowerCase();
                    
                    // 添加 class（如果有）
                    if (current.className) {
                        const classes = Array.from(current.classList)
                            .filter(cls => cls && !cls.includes(' '))
                            .join('.');
                        if (classes) {
                            selector += '.' + classes;
                        }
                    }
                    
                    // 如果当前选择器可以唯一定位，就不需要继续往上层查找
                    const tempPath = [...path, selector].reverse().join(' > ');
                    if (document.querySelectorAll(tempPath).length === 1) {
                        return tempPath;
                    }
                    
                    // 如果不能唯一定位，添加 nth-child
                    let nth = 1;
                    let sibling = current;
                    while (sibling.previousElementSibling) {
                        sibling = sibling.previousElementSibling;
                        nth++;
                    }
                    selector += `:nth-child(${nth})`;
                    
                    path.push(selector);
                    
                    // 如果遇到有 id 的父元素，可以停止往上查找
                    if (current.parentElement && current.parentElement.id) {
                        path.push('#' + current.parentElement.id);
                        break;
                    }
                    
                    current = current.parentElement;
                }
                
                return path.reverse().join(' > ');
            }

            createTooltip(element) {
                const tooltip = document.createElement('div');
                tooltip.className = 'selector-tooltip';
                tooltip.textContent = this.getSelector(element);
                //TODO$
                tooltip.textContent = '';
                tooltip.style.cssText = this.tooltipStyles;
                document.body.appendChild(tooltip);
                
                // 定位提示框
                const updateTooltipPosition = () => {
                    const rect = element.getBoundingClientRect();
                    tooltip.style.left = rect.left + window.scrollX + 'px';
                    tooltip.style.top = (rect.top + window.scrollY - tooltip.offsetHeight - 5) + 'px';
                };
                
                updateTooltipPosition();
                element._tooltipUpdatePosition = updateTooltipPosition;
                element._tooltip = tooltip;
                
                // 添加滚动监听
                window.addEventListener('scroll', updateTooltipPosition);
                window.addEventListener('resize', updateTooltipPosition);
            }

            removeTooltip(element) {
                if (element._tooltip) {
                    element._tooltip.remove();
                    window.removeEventListener('scroll', element._tooltipUpdatePosition);
                    window.removeEventListener('resize', element._tooltipUpdatePosition);
                    delete element._tooltip;
                    delete element._tooltipUpdatePosition;
                }
            }

            addBorder(element) {
                // 检查子元素是否有可以添加边框的元素
                const children = element.querySelectorAll(window._borderManager.VALID_ELEMENTS.join(', '));
                let hasValidChild = false;
                
                for (const child of children) {
                    if (this.VALID_ELEMENTS.includes(child.tagName.toLowerCase()) &&
                        child.getBoundingClientRect().width >= this.MIN_ELEMENT_SIZE || 
                        child.getBoundingClientRect().height >= this.MIN_ELEMENT_SIZE) {
                        hasValidChild = true;
                        break;
                    }
                }

                // 如果没有合适的子元素，才添加边框
                if (!hasValidChild) {
                    const originalStyle = element.getAttribute('style') || '';
                    element.style.border = '2px solid red';
                    element.style.boxSizing = 'border-box';
                    element.dataset.originalStyle = originalStyle;
                    this.borderedElements.add(element);
                    this.createTooltip(element);
                }
            }

            removeBorder(element) {
                if (element.dataset.originalStyle) {
                    element.setAttribute('style', element.dataset.originalStyle);
                } else {
                    element.removeAttribute('style');
                }
                delete element.dataset.originalStyle;
                this.removeTooltip(element);
                this.borderedElements.delete(element);
            }

            toggle() {
                this.isActive = !this.isActive;
                return this.isActive;
            }

            clearAllBorders() {
                this.borderedElements.forEach(element => {
                    this.removeBorder(element);
                });
                this.borderedElements.clear();
            }
        }
        window._borderManager = new BorderManager();
    }

    const isActive = window._borderManager.toggle();

    if (isActive) {
        // 使用 VALID_ELEMENTS 构建选择器
        const elements = document.querySelectorAll(window._borderManager.VALID_ELEMENTS.join(', '));
        elements.forEach(element => {
            if (window._borderManager.isValidElement(element)) {
                window._borderManager.addBorder(element);
            }
        });

        const observer = new MutationObserver(mutations => {
            mutations.forEach(mutation => {
                mutation.addedNodes.forEach(node => {
                    // 使用 VALID_ELEMENTS 检查节点类型
                    if (node.nodeType === 1 && 
                        window._borderManager.VALID_ELEMENTS.includes(node.tagName.toLowerCase()) && 
                        window._borderManager.isValidElement(node)) {
                        window._borderManager.addBorder(node);
                    }
                });
            });
        });

        observer.observe(document.body, {
            childList: true,
            subtree: true
        });

        window._borderObserver = observer;
    } else {
        window._borderManager.clearAllBorders();
        if (window._borderObserver) {
            window._borderObserver.disconnect();
            delete window._borderObserver;
        }
    }
}

// 添加重绘DOM树的按钮事件监听
document.getElementById('redrawDomTree').addEventListener('click', async () => {
    const button = document.getElementById('redrawDomTree');
    const statusText = document.getElementById('status-text') || createStatusElement();
    
    // 检查当前是否正在重绘
    const isDrawing = button.classList.contains('drawing');
    
    try {
        // 获取当前活动标签页
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

        if (!isDrawing) {
            // 开始重绘
            button.disabled = true;
            button.textContent = '重绘中...';
            button.classList.add('drawing');
            statusText.textContent = '';

            // 清除之前的高亮
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: () => {
                    const container = document.getElementById('playwright-highlight-container');
                    if (container) {
                        container.remove();
                    }
                }
            });

            // 重新执行buildDomTree
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                files: ['buildDomTree.js']
            });

            // 执行buildDomTree函数来重新渲染高亮并获取返回数据
            const [{result: domTreeData}] = await chrome.scripting.executeScript({
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

            // 将数据存储到 chrome.storage
            await chrome.storage.local.set({ lastDomTreeData: domTreeData });
            console.log('DOM树数据已保存:', domTreeData);

            button.textContent = '取消重绘DOM树';
            statusText.textContent = '✅ 重绘成功';
            statusText.style.color = '#4CAF50';
        } else {
            // 取消重绘
            await chrome.scripting.executeScript({
                target: { tabId: tab.id },
                func: () => {
                    const container = document.getElementById('playwright-highlight-container');
                    if (container) {
                        container.remove();
                    }
                }
            });

            button.classList.remove('drawing');
            button.textContent = '🔄 重绘DOM树';
            statusText.textContent = '已取消重绘';
            statusText.style.color = '#666';
        }
        
    } catch (error) {
        console.error('重绘操作失败:', error);
        statusText.textContent = `❌ 操作失败: ${error.message}`;
        statusText.style.color = 'red';
        button.classList.remove('drawing');
        button.textContent = '🔄 重绘DOM树';
    } finally {
        button.disabled = false;
        
        // 3秒后清除状态信息
        setTimeout(() => {
            statusText.textContent = '';
        }, 3000);
    }
});

// 添加截取当前屏幕按钮事件监听
document.getElementById('captureVisible').addEventListener('click', async () => {
    try {
        const button = document.getElementById('captureVisible');
        const statusText = document.getElementById('status-text') || createStatusElement();
        
        button.disabled = true;
        button.textContent = '截图中...';
        statusText.textContent = ''; // 清除之前的状态
        

        const screenshot = await window.screenshotManager.getVisibleAreaScreenshot();
        await window.screenshotManager.captureVisibleArea(true,screenshot);
        
        button.textContent = '截图成功！';
        statusText.textContent = '✅ 截图已保存';
        
        setTimeout(() => {
            button.disabled = false;
            button.textContent = '📷 截取当前屏幕';
            statusText.textContent = '';
        }, 2000);
    } catch (error) {
        console.error('Screenshot failed:', error);
        const button = document.getElementById('captureVisible');
        const statusText = document.getElementById('status-text') || createStatusElement();
        
        button.textContent = '截图失败';
        statusText.textContent = `❌ 错误: ${error.message}`;
        statusText.style.color = 'red';
        
        setTimeout(() => {
            button.disabled = false;
            button.textContent = '📷 截取当前屏幕';
        }, 2000);
    }
});

// 添加消息监听器来更新选择器输入框
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'updateSelectorInput') {
        const selectorInput = document.getElementById('selector-input');
        if (selectorInput) {
            selectorInput.value = message.selector;
            
            // 添加一个简单的动画效果
            selectorInput.style.backgroundColor = '#4CAF50';
            selectorInput.style.transition = 'background-color 0.3s';
            
            // 恢复原来的背景色
            setTimeout(() => {
                selectorInput.style.backgroundColor = '';
            }, 300);
        }
    }
});
