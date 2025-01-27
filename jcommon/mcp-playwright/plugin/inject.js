// 注入 ActionManager 类和实例
export async function injectActionManager(tabId) {
    try {
        // 先注入 errorManager
        await chrome.scripting.executeScript({
            target: { tabId },
            files: ['errorManager.js']
        });

        // 再注入 actionManager
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