// 注入 ActionManager 类和实例
export async function injectActionManager(tabId) {
    try {
        // 注入脚本
        await chrome.scripting.executeScript({
            target: { tabId },
            files: ['actionManager.js']
        });

        // 验证注入是否成功
        const [{ result }] = await chrome.scripting.executeScript({
            target: { tabId },
            func: () => {
                return !!window.actionManager;
            }
        });

        if (!result) {
            throw new Error('ActionManager injection failed');
        }

        return true;
    } catch (error) {
        console.error('Failed to inject ActionManager:', error);
        throw error;
    }
} 