// 截图管理类
class ScreenshotManager {
    constructor() {
        // 可以在这里添加配置项
        this.config = {
            format: 'jpeg',
            quality: 5,
            defaultFilename: 'screenshot.jpeg'
        };
    }

    // 在页面注入辅助工具（固定元素处理与状态提示）
    async injectHelper(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            func: () => {
                if (window.__imgshotHelper) return;
                window.__imgshotHelper = (() => {
                    const helper = {
                        fixed: [],
                        ensureStatusHost() {
                            let host = document.getElementById('longScreenshotStatus');
                            if (!host) {
                                host = document.createElement('div');
                                host.id = 'longScreenshotStatus';
                                host.style.cssText = [
                                    'position: fixed',
                                    'top: 20px',
                                    'right: 20px',
                                    'background: rgba(0,0,0,0.8)',
                                    'color: #fff',
                                    'padding: 12px 16px',
                                    'border-radius: 6px',
                                    'font: 14px/1.5 Arial, sans-serif',
                                    'z-index: 2147483647',
                                    'box-shadow: 0 4px 12px rgba(0,0,0,0.3)'
                                ].join(';');
                                document.documentElement.appendChild(host);
                            }
                            return host;
                        },
                        showStatus(msg, duration = 0) {
                            const host = helper.ensureStatusHost();
                            host.textContent = msg;
                            if (duration > 0) {
                                clearTimeout(host.__hideTimer);
                                host.__hideTimer = setTimeout(() => helper.hideStatus(), duration);
                            }
                        },
                        hideStatus() {
                            const host = document.getElementById('longScreenshotStatus');
                            if (host && host.parentNode) host.parentNode.removeChild(host);
                        },
                        collectFixed() {
                            helper.fixed = [];
                            const nodes = document.querySelectorAll('*:not(#longScreenshotStatus)');
                            nodes.forEach(el => {
                                const style = getComputedStyle(el);
                                if (style.position === 'fixed' || style.position === 'sticky') {
                                    helper.fixed.push({ el, style: el.getAttribute('style') || '', rect: el.getBoundingClientRect() });
                                }
                            });
                        },
                        hideFixed() {
                            if (!helper.fixed.length) helper.collectFixed();
                            helper.fixed.forEach(({ el, rect }) => {
                                el.style.visibility = 'hidden';
                                // 保留占位，减少重排引起的抖动
                                el.style.height = rect.height + 'px';
                            });
                        },
                        restoreFixed() {
                            helper.fixed.forEach(({ el, style }) => {
                                if (style) el.setAttribute('style', style); else el.removeAttribute('style');
                            });
                            helper.fixed = [];
                        }
                    };
                    return helper;
                })();
            }
        });
    }

    async pageShowStatus(tabId, msg, duration = 0) {
        await chrome.scripting.executeScript({
            target: { tabId },
            func: (m, d) => window.__imgshotHelper && window.__imgshotHelper.showStatus(m, d),
            args: [msg, duration]
        });
    }

    async pageHideStatus(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            func: () => window.__imgshotHelper && window.__imgshotHelper.hideStatus()
        });
    }

    async pageHideFixed(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            func: () => window.__imgshotHelper && window.__imgshotHelper.hideFixed()
        });
    }

    async pageRestoreFixed(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            func: () => window.__imgshotHelper && window.__imgshotHelper.restoreFixed()
        });
    }

    // 捕获完整页面的截图
    async captureFullPage(download = true) {
        try {
            // 首先尝试获取当前窗口的活动标签页
            let tabs = await chrome.tabs.query({
                active: true,
                lastFocusedWindow: true
            });

            console.log("Found tabs:", tabs);

            // 如果没有找到，尝试获取所有窗口的活动标签页
            if (!tabs || tabs.length === 0) {
                const allTabs = await chrome.tabs.query({
                    active: true
                });
                
                console.log("Found all window tabs:", allTabs);
                
                if (!allTabs || allTabs.length === 0) {
                    throw new Error('No active tab found in any window');
                }
                
                // 使用第一个找到的活动标签页
                tabs = [allTabs[0]];
            }

            const activeTab = tabs[0];

            // 注入帮助器与初始状态提示
            await this.injectHelper(activeTab.id);
            await this.pageShowStatus(activeTab.id, '正在准备长截图...');

            // 注入测量页面尺寸和滚动位置的脚本
            const [{result}] = await chrome.scripting.executeScript({
                target: { tabId: activeTab.id },
                function: () => {
                    return {
                        width: Math.max(
                            document.documentElement.scrollWidth,
                            document.body.scrollWidth,
                            document.documentElement.clientWidth
                        ),
                        height: Math.max(
                            document.documentElement.scrollHeight,
                            document.body.scrollHeight,
                            document.documentElement.clientHeight
                        ),
                        viewportHeight: window.innerHeight,
                        originalScrollTop: window.scrollY
                    };
                }
            });

            const { height, viewportHeight, originalScrollTop } = result;
            const totalHeight = height;
            const screenshots = [];
            
            // 分段截图
            for (let currentPosition = 0; currentPosition < totalHeight; currentPosition += viewportHeight) {
                await this.pageShowStatus(activeTab.id, `正在截图... ${Math.min(currentPosition + viewportHeight, totalHeight)}/${totalHeight}`);
                await this.pageHideFixed(activeTab.id);
                // 添加重试逻辑
                let retryCount = 0;
                const maxRetries = 3;
                
                while (retryCount < maxRetries) {
                    try {
                        // 滚动到指定位置
                        await chrome.scripting.executeScript({
                            target: { tabId: activeTab.id },
                            function: (scrollTo) => {
                                window.scrollTo(0, scrollTo);
                            },
                            args: [currentPosition]
                        });

                        // 增加等待时间到500ms
                        await new Promise(resolve => setTimeout(resolve, 500));

                        // 捕获当前视口的截图
                        const screenshot = await chrome.tabs.captureVisibleTab(activeTab.windowId, {
                            format: this.config.format,
                            quality: this.config.quality
                        });
                        screenshots.push(screenshot);
                        break; // 成功后跳出重试循环
                    } catch (error) {
                        retryCount++;
                        if (error.message.includes('MAX_CAPTURE_VISIBLE_TAB_CALLS_PER_SECOND')) {
                            // 如果是配额错误，等待更长时间后重试
                            await new Promise(resolve => setTimeout(resolve, 1000 * retryCount));
                            continue;
                        }
                        throw error; // 其他错误直接抛出
                    }
                }
                
                if (retryCount >= maxRetries) {
                    throw new Error('截图失败：已达到最大重试次数');
                }
            }

            // 清理状态与固定元素
            await this.pageShowStatus(activeTab.id, '正在合并图片...');
            await this.pageRestoreFixed(activeTab.id);

            // 恢复原始滚动位置
            await chrome.scripting.executeScript({
                target: { tabId: activeTab.id },
                function: (scrollTo) => {
                    window.scrollTo(0, scrollTo);
                },
                args: [originalScrollTop]
            });

            // 检查是否已存在offscreen文档
            const existingContexts = await chrome.runtime.getContexts({
                contextTypes: ['OFFSCREEN_DOCUMENT']
            });

            // 如果没有offscreen文档，创建一个
            if (existingContexts.length === 0) {
                await chrome.offscreen.createDocument({
                    url: 'offscreen.html',
                    reasons: ['BLOBS', 'DOM_PARSER'],
                    justification: 'Merging screenshots requires DOM manipulation and blob processing'
                });
            }

            // 发送消息给offscreen文档处理图片合并
            const response = await chrome.runtime.sendMessage({
                type: 'MERGE_SCREENSHOTS',
                data: {
                    screenshots,
                    width: result.width,
                    height: totalHeight,
                    viewportHeight
                }
            });

            // 下载合并后的图片
            if (download && response.success) {
                await this.pageShowStatus(activeTab.id, '长截图完成，正在下载...', 1500);
                await chrome.downloads.download({
                    url: response.dataUrl,
                    filename: 't.jpeg',
                    saveAs: false
                });
                console.log('Full page screenshot saved successfully');
            }

            // 关闭offscreen文档
            await chrome.offscreen.closeDocument();

            // 隐藏状态提示
            await this.pageHideStatus(activeTab.id);

            // 根据download参数返回不同的值
            return download ? true : response.dataUrl;

        } catch (error) {
            console.error('Error capturing full page screenshot:', error);
            throw error;
        }
    }

    // 捕获当前可视区域的截图
    async captureVisibleArea(allowDownload = false, screenshot) {
        try {
            // 创建一个 canvas 来处理图片
            const img = await this.loadImage(screenshot);
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            let scale = 1.0/window.devicePixelRatio;
            canvas.width = img.width * scale;
            canvas.height = img.height * scale; 
            
            // 绘制原始图片
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            
            // 设置网格线样式
            ctx.strokeStyle = 'rgba(200, 200, 200, 0.2)'; // 更透明的网格线
            ctx.lineWidth = 0.5;
            ctx.font = '10px Arial';
            ctx.fillStyle = 'rgba(100, 100, 100, 0.7)';
            
            const GRID_COUNT = 20; // 网格数量
            const rowHeight = canvas.height / GRID_COUNT;
            const columnWidth = canvas.width / GRID_COUNT;
            
            // 绘制水平线
            for (let i = 1; i < GRID_COUNT; i++) {
                const y = Math.round(rowHeight * i);
                ctx.beginPath();
                ctx.moveTo(0, y);
                ctx.lineTo(canvas.width, y);
                ctx.stroke();
            }
            
            // 绘制垂直线
            for (let i = 1; i < GRID_COUNT; i++) {
                const x = Math.round(columnWidth * i);
                ctx.beginPath();
                ctx.moveTo(x, 0);
                ctx.lineTo(x, canvas.height);
                ctx.stroke();
            }
            
            // 在交叉点绘制坐标
            ctx.font = '9px Arial';
            for (let i = 1; i < GRID_COUNT; i++) {
                for (let j = 1; j < GRID_COUNT; j++) {
                    const x = Math.round(columnWidth * i);
                    const y = Math.round(rowHeight * j);
                    
                    // 计算实际坐标值
                    const coordX = Math.round(canvas.width * (i / GRID_COUNT));
                    const coordY = Math.round(canvas.height * (j / GRID_COUNT));
                    
                    // 绘制小点
                    ctx.fillStyle = 'rgba(100, 100, 100, 0.5)';
                    ctx.beginPath();
                    ctx.arc(x, y, 1.5, 0, 2 * Math.PI);
                    ctx.fill();
                    
                    // 绘制坐标文本
                    ctx.fillStyle = 'rgba(100, 100, 100, 0.7)';
                    const coordText = `(${coordX},${coordY})`;
                    
                    // 根据位置调整文本显示方向，避免超出边界
                    const textX = x + 3;
                    const textY = y - 3;
                    
                    // 每隔一个交叉点显示坐标，避免文字重叠
                    if ((i % 2 === 1) && (j % 2 === 1)) {
                        ctx.fillText(coordText, textX, textY);
                    }
                }
            }

            // 将图片复制到剪贴板
            try {
                canvas.toBlob(async (blob) => {
                    const clipboardItem = new ClipboardItem({
                        'image/png': blob
                    });
                    await navigator.clipboard.write([clipboardItem]);
                    console.log('Screenshot copied to clipboard successfully');
                }, 'image/png');
            } catch (clipboardError) {
                console.error('Error copying to clipboard:', clipboardError);
            }

            // 只在 allowDownload 为 true 时下载截图
            if (allowDownload) {
                await chrome.downloads.download({
                    url: screenshot,
                    filename: this.config.defaultFilename,
                    saveAs: false
                });
                console.log('Visible area screenshot saved successfully');
            }

            return true;

        } catch (error) {
            console.error('Error capturing visible area screenshot:', error);
            throw error;
        }
    }

    // 获取当前可视区域的截图
    async getVisibleAreaScreenshot() {
        try {
            // 获取当前活动标签页的截图
            const screenshot = await chrome.tabs.captureVisibleTab(null, {
                format: this.config.format,
                quality: this.config.quality
            });
            
            return screenshot;
        } catch (error) {
            console.error('Error getting visible area screenshot:', error);
            throw error;
        }
    }

    // 辅助函数：将图片 URL 加载为 Image 对象
    loadImage(url) {
        return new Promise((resolve, reject) => {
            const img = new Image();
            img.onload = () => resolve(img);
            img.onerror = reject;
            img.src = url;
        });
    }
}

// 创建单例实例
const screenshotManager = new ScreenshotManager(); 
export default screenshotManager;