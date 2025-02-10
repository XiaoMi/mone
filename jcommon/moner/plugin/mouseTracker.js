import storageManager from './managers/storageManager.js';

export class MouseTracker {
    static async injectTracker(tabId) {
        // 注入 CSS
        await chrome.scripting.insertCSS({
            target: { tabId },
            css: `
                #virtual-mouse {
                    position: fixed;
                    width: 10px;
                    height: 10px;
                    background-color: red;
                    border-radius: 50%;
                    pointer-events: none;
                    z-index: 999999;
                    transition: all 0.1s ease;
                    box-shadow: 0 0 5px rgba(255, 0, 0, 0.5);
                }
            `
        });

        // 注入跟踪脚本
        await chrome.scripting.executeScript({
            target: { tabId },
            function: () => {
                if (!document.getElementById('virtual-mouse')) {
                    const mouse = document.createElement('div');
                    mouse.id = 'virtual-mouse';
                    document.body.appendChild(mouse);

                    // 监听鼠标移动
                    document.addEventListener('mousemove', (e) => {
                        if (!window._virtualMousePaused) {
                            mouse.style.left = `${e.clientX - 5}px`;
                            mouse.style.top = `${e.clientY - 5}px`;
                        }
                    });

                    // 存储当前位置
                    window._currentMousePosition = { x: 0, y: 0 };
                }
            }
        });
    }

    static async removeTracker(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            function: () => {
                const mouse = document.getElementById('virtual-mouse');
                if (mouse) {
                    mouse.remove();
                }
                delete window._virtualMousePaused;
                delete window._currentMousePosition;
            }
        });
    }

    static async moveToPosition(tabId, x, y) {
        // 保存位置到 storage
        await storageManager.set(`mousePosition_${tabId}`, { x, y });

        await chrome.scripting.executeScript({
            target: { tabId },
            function: (targetX, targetY) => {
                const mouse = document.getElementById('virtual-mouse');
                if (mouse) {
                    window._virtualMousePaused = true;
                    mouse.style.left = `${targetX - 5}px`;
                    mouse.style.top = `${targetY - 5}px`;
                }
            },
            args: [x, y]
        });
    }

    static async resumeTracking(tabId) {
        await chrome.scripting.executeScript({
            target: { tabId },
            function: () => {
                window._virtualMousePaused = false;
            }
        });
    }
} 