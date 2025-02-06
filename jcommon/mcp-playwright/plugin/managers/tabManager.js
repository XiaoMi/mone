/**
 * 标签页管理类
 */
export class TabManager {
    constructor() {
        // 可以在这里添加任何需要的初始化配置
        this.defaultOptions = {
            active: true,
            pinned: false
        };
    }

    /**
     * 创建新标签页
     * @param {string} url - 新标签页的URL
     * @param {boolean} active - 是否激活新标签页
     * @returns {Promise<chrome.tabs.Tab>} 创建的标签页对象
     */
    async createNewTab(url, active = this.defaultOptions.active) {
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

    /**
     * 关闭指定的标签页
     * @param {number} tabId - 要关闭的标签页ID
     */
    async closeTab(tabId) {
        try {
            await chrome.tabs.remove(tabId);
            console.log('Tab closed:', tabId);
        } catch (error) {
            console.error('Error closing tab:', error);
            throw error;
        }
    }

    /**
     * 获取所有标签页
     * @param {object} queryOptions - 查询选项
     * @param {number} [queryOptions.windowId] - 窗口ID
     * @param {boolean} [queryOptions.active] - 是否活动标签
     * @param {boolean} [queryOptions.pinned] - 是否固定标签
     * @param {string|string[]} [queryOptions.url] - URL匹配模式
     * @param {string} [queryOptions.status] - 标签状态
     * @returns {Promise<chrome.tabs.Tab[]>} 标签页数组
     */
    async getAllTabs(queryOptions = {}) {
        try {
            const tabs = await chrome.tabs.query(queryOptions);
            console.log('All tabs:', tabs);
            return tabs;
        } catch (error) {
            console.error('Error getting tabs:', error);
            throw error;
        }
    }

    /**
     * 获取当前窗口的所有标签页
     * @returns {Promise<chrome.tabs.Tab[]>} 当前窗口的标签页数组
     */
    async getCurrentWindowTabs() {
        return await this.getAllTabs({ currentWindow: true });
    }

    /**
     * 获取当前活动的标签页
     * @returns {Promise<chrome.tabs.Tab>} 活动标签页对象
     */
    async getActiveTab() {
        const tabs = await this.getAllTabs({ active: true, currentWindow: true });
        return tabs[0];
    }

    /**
     * 更新标签页
     * @param {number} tabId - 标签页ID
     * @param {object} updateProperties - 更新属性
     * @returns {Promise<chrome.tabs.Tab>} 更新后的标签页对象
     */
    async updateTab(tabId, updateProperties) {
        try {
            const tab = await chrome.tabs.update(tabId, updateProperties);
            console.log('Tab updated:', tab);
            return tab;
        } catch (error) {
            console.error('Error updating tab:', error);
            throw error;
        }
    }

    /**
     * 移动标签页
     * @param {number|number[]} tabIds - 标签页ID或ID数组
     * @param {object} moveProperties - 移动属性
     * @returns {Promise<chrome.tabs.Tab[]>} 移动后的标签页数组
     */
    async moveTabs(tabIds, moveProperties) {
        try {
            const tabs = await chrome.tabs.move(tabIds, moveProperties);
            console.log('Tabs moved:', tabs);
            return tabs;
        } catch (error) {
            console.error('Error moving tabs:', error);
            throw error;
        }
    }
}

// 创建单例实例
const tabManager = new TabManager();
export default tabManager; 