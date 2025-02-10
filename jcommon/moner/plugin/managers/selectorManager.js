export class SelectorManager {
    constructor() {
        this.initializeMessageListener();
    }

    async moveToElement(selector) {
        try {
            // 获取当前活动标签页
            const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
            
            // 向内容脚本发送消息以获取元素位置
            const response = await chrome.tabs.sendMessage(tab.id, {
                action: 'getElementPosition',
                selector: selector
            });

            if (response.success) {
                // 移动鼠标到元素位置
                await chrome.tabs.sendMessage(tab.id, {
                    action: 'moveMouseTo',
                    x: response.position.x,
                    y: response.position.y
                });
                return { success: true };
            } else {
                return { 
                    success: false, 
                    error: '未找到匹配的元素' 
                };
            }
        } catch (error) {
            return { 
                success: false, 
                error: '操作失败: ' + error.message 
            };
        }
    }

    initializeMessageListener() {
        chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
            if (message.action === 'moveToSelector') {
                this.moveToElement(message.selector)
                    .then(sendResponse);
                return true; // 保持消息通道开放
            }
        });
    }
}

// 创建实例
const selectorManager = new SelectorManager();
export default selectorManager; 