// 获取浏览历史记录
async function getHistory(searchOptions = {}) {
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
            maxResults: searchOptions.maxResults || 100
        });
        console.log('History items retrieved:', historyItems);
        return historyItems;
    } catch (error) {
        console.error('Error getting history:', error);
        throw error;
    }
}

// 获取最近的历史记录
async function getRecentHistory(maxResults = 10) {
    try {
        const recentHistory = await getHistory({
            maxResults: maxResults,
            startTime: Date.now() - (7 * 24 * 60 * 60 * 1000) // 最近7天
        });
        return recentHistory;
    } catch (error) {
        console.error('Error getting recent history:', error);
        throw error;
    }
}

// 搜索特定网址的历史记录
async function searchUrlHistory(url) {
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
async function addToHistory(url, title = '') {
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
async function deleteUrlFromHistory(url) {
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
async function deleteHistoryRange(startTime, endTime) {
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
async function clearAllHistory() {
    try {
        await chrome.history.deleteAll();
        console.log('All history cleared');
    } catch (error) {
        console.error('Error clearing history:', error);
        throw error;
    }
}

// 获取指定日期的历史记录
async function getDayHistory(date) {
    try {
        const startTime = new Date(date);
        startTime.setHours(0, 0, 0, 0);
        
        const endTime = new Date(date);
        endTime.setHours(23, 59, 59, 999);
        
        const dayHistory = await getHistory({
            startTime: startTime.getTime(),
            endTime: endTime.getTime(),
            maxResults: 1000
        });
        
        return dayHistory;
    } catch (error) {
        console.error('Error getting day history:', error);
        throw error;
    }
}

// 导出函数供其他文件使用
export {
    getHistory,
    getRecentHistory,
    searchUrlHistory,
    addToHistory,
    deleteUrlFromHistory,
    deleteHistoryRange,
    clearAllHistory,
    getDayHistory
}; 