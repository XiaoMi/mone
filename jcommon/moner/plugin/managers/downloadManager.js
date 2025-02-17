// 下载管理类
export class DownloadManager {
    constructor() {
        // 下载配置
        this.defaultConfig = {
            saveAs: false,          // 是否弹出另存为对话框
            conflictAction: 'uniquify', // 文件名冲突处理方式：'uniquify'|'overwrite'|'prompt'
            maxRetries: 3,          // 下载失败重试次数
            notifications: {        // 通知配置
                enabled: true,      // 是否启用通知
                success: true,      // 下载成功时通知
                error: true,        // 下载失败时通知
                progress: false     // 下载进度通知
            }
        };

        // 下载历史记录
        this.downloadHistory = new Map();
        // 下载监听器集合
        this.listeners = new Map();

        // 初始化下载监听器
        if (chrome?.downloads) {
            this.initializeDownloadListeners();
        }
    }

    // 初始化下载监听器
    initializeDownloadListeners() {
        chrome.downloads.onChanged.addListener((delta) => {
            this.handleDownloadChange(delta);
        });
    }

    // 处理下载状态变化
    handleDownloadChange(delta) {
        const downloadId = delta.id;
        const download = this.downloadHistory.get(downloadId);
        
        if (download) {
            if (delta.state) {
                download.state = delta.state.current;
                
                // 处理下载状态变化通知
                if (this.defaultConfig.notifications.enabled) {
                    this.handleDownloadNotification(download, delta);
                }
            }
            if (delta.error) {
                download.error = delta.error.current;
            }
            
            this.notifyListeners('change', { downloadId, delta });
        }
    }

    // 添加新方法：处理下载通知
    async handleDownloadNotification(download, delta) {
        if (!chrome?.notifications) return;

        const notificationId = `download_${download.id}`;
        
        if (delta.state.current === 'complete' && this.defaultConfig.notifications.success) {
            await chrome.notifications.create(notificationId, {
                type: 'basic',
                iconUrl: '/icons/success.png', // 确保你的扩展中有这个图标
                title: '下载完成',
                message: `文件 ${download.filename} 已下载完成`
            });
            
            // 5秒后自动关闭通知
            setTimeout(() => {
                chrome.notifications.clear(notificationId);
            }, 5000);
        }
        
        if (delta.state.current === 'interrupted' && this.defaultConfig.notifications.error) {
            await chrome.notifications.create(notificationId, {
                type: 'basic',
                iconUrl: '/icons/error.png', // 确保你的扩展中有这个图标
                title: '下载失败',
                message: `文件 ${download.filename} 下载失败: ${download.error || '未知错误'}`
            });
        }
    }

    // 添加新方法：更新通知配置
    updateNotificationConfig(config) {
        this.defaultConfig.notifications = {
            ...this.defaultConfig.notifications,
            ...config
        };
    }

    // 添加新方法：清除所有下载通知
    async clearAllNotifications() {
        if (!chrome?.notifications) return;
        
        const notifications = await chrome.notifications.getAll();
        Object.keys(notifications).forEach(notificationId => {
            if (notificationId.startsWith('download_')) {
                chrome.notifications.clear(notificationId);
            }
        });
    }

    // 开始下载
    async startDownload(options) {
        try {
            const config = { ...this.defaultConfig, ...options };
            
            if (!options.url) {
                throw new Error('Download URL is required');
            }

            const downloadOptions = {
                url: options.url,
                filename: options.filename,
                saveAs: config.saveAs,
                conflictAction: config.conflictAction
            };

            let downloadId;
            if (chrome?.downloads) {
                // Chrome 扩展环境
                const downloadItem = await chrome.downloads.download(downloadOptions);
                downloadId = downloadItem;
            } else {
                // 普通网页环境
                downloadId = this.fallbackDownload(options.url, options.filename);
            }

            const downloadInfo = {
                id: downloadId,
                url: options.url,
                filename: options.filename,
                startTime: Date.now(),
                state: 'in_progress',
                error: null
            };

            this.downloadHistory.set(downloadId, downloadInfo);
            this.notifyListeners('start', downloadInfo);

            return downloadId;
        } catch (error) {
            console.error('Error starting download:', error);
            throw error;
        }
    }

    // 普通网页环境下的下载处理
    fallbackDownload(url, filename) {
        const a = document.createElement('a');
        a.href = url;
        if (filename) {
            a.download = filename;
        }
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        
        return Date.now(); // 使用时间戳作为下载ID
    }

    // 取消下载
    async cancelDownload(downloadId) {
        try {
            if (chrome?.downloads) {
                await chrome.downloads.cancel(downloadId);
            }
            
            const download = this.downloadHistory.get(downloadId);
            if (download) {
                download.state = 'cancelled';
                this.notifyListeners('cancel', { downloadId });
            }
            
            return true;
        } catch (error) {
            console.error('Error cancelling download:', error);
            throw error;
        }
    }

    // 暂停下载
    async pauseDownload(downloadId) {
        try {
            if (chrome?.downloads) {
                await chrome.downloads.pause(downloadId);
                this.notifyListeners('pause', { downloadId });
                return true;
            }
            return false;
        } catch (error) {
            console.error('Error pausing download:', error);
            throw error;
        }
    }

    // 恢复下载
    async resumeDownload(downloadId) {
        try {
            if (chrome?.downloads) {
                await chrome.downloads.resume(downloadId);
                this.notifyListeners('resume', { downloadId });
                return true;
            }
            return false;
        } catch (error) {
            console.error('Error resuming download:', error);
            throw error;
        }
    }

    // 获取下载信息
    async getDownloadInfo(downloadId) {
        try {
            if (chrome?.downloads) {
                const [downloadItem] = await chrome.downloads.search({ id: downloadId });
                return downloadItem || null;
            }
            return this.downloadHistory.get(downloadId) || null;
        } catch (error) {
            console.error('Error getting download info:', error);
            throw error;
        }
    }

    // 获取下载历史
    async getDownloadHistory(query = {}) {
        try {
            if (chrome?.downloads) {
                return await chrome.downloads.search(query);
            }
            return Array.from(this.downloadHistory.values());
        } catch (error) {
            console.error('Error getting download history:', error);
            throw error;
        }
    }

    // 清除下载历史
    async clearDownloadHistory(query = {}) {
        try {
            if (chrome?.downloads) {
                await chrome.downloads.erase(query);
            }
            this.downloadHistory.clear();
            this.notifyListeners('clear', {});
            return true;
        } catch (error) {
            console.error('Error clearing download history:', error);
            throw error;
        }
    }

    // 添加下载事件监听器
    addDownloadListener(event, callback) {
        if (!this.listeners.has(event)) {
            this.listeners.set(event, new Set());
        }
        this.listeners.get(event).add(callback);
        
        return () => this.removeDownloadListener(event, callback);
    }

    // 移除下载事件监听器
    removeDownloadListener(event, callback) {
        const eventListeners = this.listeners.get(event);
        if (eventListeners) {
            eventListeners.delete(callback);
        }
    }

    // 通知所有监听器
    notifyListeners(event, data) {
        const eventListeners = this.listeners.get(event);
        if (eventListeners) {
            eventListeners.forEach(callback => {
                try {
                    callback(data);
                } catch (error) {
                    console.error('Error in download listener:', error);
                }
            });
        }
    }
}

// 创建单例实例
const downloadManager = new DownloadManager();
export default downloadManager; 