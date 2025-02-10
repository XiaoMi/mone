/**
 * 历史记录管理类
 */
export class HistoryManager {
    constructor() {
        // 默认配置
        this.defaultOptions = {
            maxResults: 100,
            defaultDays: 7,
            maxDayResults: 1000
        };
    }

    // 获取浏览历史记录
    async getHistory(searchOptions = {}) {
        try {
            // searchOptions 可以包含：
            // - text: string - 要搜索的文本
            // - startTime: number - 开始时间戳
            // - endTime: number - 结束时间戳
            // - maxResults: number - 最大结果数量
            const historyItems = await chrome.history.search({
                text: searchOptions.text || '',
                startTime: searchOptions.startTime || 0,
                endTime: searchOptions.endTime || Date.now(),
                maxResults: searchOptions.maxResults || this.defaultOptions.maxResults
            });
            console.log('History items retrieved:', historyItems);
            return historyItems;
        } catch (error) {
            console.error('Error getting history:', error);
            throw error;
        }
    }

    // 获取最近的历史记录
    async getRecentHistory(maxResults = 10) {
        try {
            const recentHistory = await this.getHistory({
                maxResults: maxResults,
                startTime: Date.now() - (this.defaultOptions.defaultDays * 24 * 60 * 60 * 1000) // 最近7天
            });
            return recentHistory;
        } catch (error) {
            console.error('Error getting recent history:', error);
            throw error;
        }
    }

    // 搜索特定网址的历史记录
    async searchUrlHistory(url) {
        try {
            const visits = await chrome.history.getVisits({
                url: url
            });
            console.log('URL visits:', visits);
            return visits;
        } catch (error) {
            console.error('Error searching URL history:', error);
            throw error;
        }
    }

    // 添加历史记录（通常由浏览器自动处理，但有时可能需要手动添加）
    async addToHistory(url, title = '') {
        try {
            await chrome.history.addUrl({
                url: url,
                title: title
            });
            console.log('Added to history:', url);
        } catch (error) {
            console.error('Error adding to history:', error);
            throw error;
        }
    }

    // 删除特定URL的历史记录
    async deleteUrlFromHistory(url) {
        try {
            await chrome.history.deleteUrl({
                url: url
            });
            console.log('Deleted from history:', url);
        } catch (error) {
            console.error('Error deleting from history:', error);
            throw error;
        }
    }

    // 删除特定时间范围内的历史记录
    async deleteHistoryRange(startTime, endTime) {
        try {
            await chrome.history.deleteRange({
                startTime: startTime,
                endTime: endTime
            });
            console.log('Deleted history range:', { startTime, endTime });
        } catch (error) {
            console.error('Error deleting history range:', error);
            throw error;
        }
    }

    // 清除所有历史记录
    async clearAllHistory() {
        try {
            await chrome.history.deleteAll();
            console.log('All history cleared');
        } catch (error) {
            console.error('Error clearing history:', error);
            throw error;
        }
    }

    // 获取指定日期的历史记录
    async getDayHistory(date) {
        try {
            const startTime = new Date(date);
            startTime.setHours(0, 0, 0, 0);
            
            const endTime = new Date(date);
            endTime.setHours(23, 59, 59, 999);
            
            const dayHistory = await this.getHistory({
                startTime: startTime.getTime(),
                endTime: endTime.getTime(),
                maxResults: this.defaultOptions.maxDayResults
            });
            
            return dayHistory;
        } catch (error) {
            console.error('Error getting day history:', error);
            throw error;
        }
    }

    // 更新默认配置
    updateDefaultOptions(options) {
        this.defaultOptions = {
            ...this.defaultOptions,
            ...options
        };
    }
}

// 创建单例实例
const historyManager = new HistoryManager();
export default historyManager; 