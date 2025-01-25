// 创建新标签页
async function createNewTab(url, active = true) {
    try {
        const tab = await chrome.tabs.create({
            url: url,
            active: active
        });
        console.log('New tab created:', tab);
        return tab;
    } catch (error) {
        console.error('Error creating new tab:', error);
        throw error;
    }
}

// 关闭指定的标签页
async function closeTab(tabId) {
    try {
        await chrome.tabs.remove(tabId);
        console.log('Tab closed:', tabId);
    } catch (error) {
        console.error('Error closing tab:', error);
        throw error;
    }
}

// 获取所有标签页
async function getAllTabs(queryOptions = {}) {
    try {
        // queryOptions 可以包含：
        // - windowId: number
        // - active: boolean
        // - pinned: boolean
        // - url: string or array of string
        // - status: "unloaded" | "loading" | "complete"
        const tabs = await chrome.tabs.query(queryOptions);
        console.log('All tabs:', tabs);
        return tabs;
    } catch (error) {
        console.error('Error getting tabs:', error);
        throw error;
    }
}

// 示例用法：
// 获取当前窗口的所有标签页
async function getCurrentWindowTabs() {
    const tabs = await getAllTabs({ currentWindow: true });
    return tabs;
}

// 获取活动标签页
async function getActiveTab() {
    const tabs = await getAllTabs({ active: true, currentWindow: true });
    return tabs[0];
}

// 导出函数供其他文件使用
export {
    createNewTab,
    closeTab,
    getAllTabs,
    getCurrentWindowTabs,
    getActiveTab
}; 