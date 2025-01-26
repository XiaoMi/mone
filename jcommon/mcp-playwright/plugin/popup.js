import { captureFullPage } from './screenshotManager.js';
import { getAllTabs } from './tabManager.js';
import { toggleEffect } from './effectsManager.js';
import { BorderManager } from './borderManager.js';
import { MouseTracker } from './mouseTracker.js';

// 等待DOM加载完成后执行
document.addEventListener('DOMContentLoaded', () => {
    console.log('Popup script loaded and DOM is ready');
    
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

    // 添加下雨特效按钮事件监听
    document.getElementById('rainEffect').addEventListener('click', async () => {
        const button = document.getElementById('rainEffect');
        const isEffectOn = await toggleEffect('rain');
        button.textContent = isEffectOn ? '🌧️ 关闭下雨' : '🌧️ 下雨特效';
    });

    // 添加下雪特效按钮事件监听
    document.getElementById('snowEffect').addEventListener('click', async () => {
        const button = document.getElementById('snowEffect');
        const isEffectOn = await toggleEffect('snow');
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

    // 修改现有的坐标移动功能
    document.getElementById('move-to-selector').addEventListener('click', async () => {
        const selector = document.getElementById('selector-input').value;
        if (!selector) {
            alert('请输入选择器');
            return;
        }

        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        const statusText = document.getElementById('status-text');

        try {
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
                statusText.textContent = '已移动到元素位置';
                
                // 3秒后恢复跟踪
                setTimeout(async () => {
                    await MouseTracker.resumeTracking(tab.id);
                }, 3000);
            } else {
                statusText.textContent = '未找到匹配的元素';
            }
        } catch (error) {
            statusText.textContent = '发生错误: ' + error.message;
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
        // 复制选择器到剪贴板
        navigator.clipboard.writeText(message.selector).then(() => {
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '✅ 选择器已复制: ' + message.selector;
            statusText.style.color = '#4CAF50';
            
            setTimeout(() => {
                statusText.textContent = '';
            }, 2000);
        }).catch(err => {
            console.error('Failed to copy selector:', err);
            const statusText = document.getElementById('status-text') || createStatusElement();
            statusText.textContent = '❌ 复制失败';
            statusText.style.color = 'red';
        });
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
    
    try {
        button.disabled = true;
        button.textContent = '重绘中...';
        statusText.textContent = '';

        // 获取当前活动标签页
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
        
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

        // 执行buildDomTree函数来重新渲染高亮
        await chrome.scripting.executeScript({
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

        statusText.textContent = '✅ 重绘成功';
        statusText.style.color = '#4CAF50';
        
    } catch (error) {
        console.error('重绘失败:', error);
        statusText.textContent = `❌ 重绘失败: ${error.message}`;
        statusText.style.color = 'red';
    } finally {
        button.disabled = false;
        button.textContent = '🔄 重绘DOM树';
        
        // 3秒后清除状态信息
        setTimeout(() => {
            statusText.textContent = '';
        }, 3000);
    }
});
