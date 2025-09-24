// 存储管理类
export class StorageManager {
    constructor() {
        // 默认存储配置
        this.defaultConfig = {
            useCompression: false,
            storageType: 'local', // 'local' | 'sync' | 'session'
            expirationTime: null  // 过期时间（毫秒）
        };

        // 存储事件监听器集合
        this.listeners = new Map();
    }

    // 设置数据
    async set(key, value, options = {}) {
        try {
            const config = { ...this.defaultConfig, ...options };
            const data = {
                value,
                timestamp: Date.now(),
                expiration: config.expirationTime ? Date.now() + config.expirationTime : null
            };

            const serializedData = JSON.stringify(data);

            switch (config.storageType) {
                case 'local':
                    if (chrome?.storage?.local) {
                        await chrome.storage.local.set({ [key]: serializedData });
                    } else {
                        localStorage.setItem(key, serializedData);
                    }
                    break;
                case 'sync':
                    if (chrome?.storage?.sync) {
                        await chrome.storage.sync.set({ [key]: serializedData });
                    } else {
                        throw new Error('Chrome sync storage is not available');
                    }
                    break;
                case 'session':
                    sessionStorage.setItem(key, serializedData);
                    break;
                default:
                    throw new Error('Invalid storage type');
            }

            return true;
        } catch (error) {
            console.error('Error setting storage data:', error);
            throw error;
        }
    }

    // 获取数据
    async get(key, options = {}) {
        try {
            const config = { ...this.defaultConfig, ...options };
            let serializedData;

            switch (config.storageType) {
                case 'local':
                    if (chrome?.storage?.local) {
                        const result = await chrome.storage.local.get(key);
                        serializedData = result[key];
                    } else {
                        serializedData = localStorage.getItem(key);
                    }
                    break;
                case 'sync':
                    if (chrome?.storage?.sync) {
                        const result = await chrome.storage.sync.get(key);
                        serializedData = result[key];
                    } else {
                        throw new Error('Chrome sync storage is not available');
                    }
                    break;
                case 'session':
                    serializedData = sessionStorage.getItem(key);
                    break;
                default:
                    throw new Error('Invalid storage type');
            }

            if (!serializedData) {
                return null;
            }

            const data = JSON.parse(serializedData);

            // 检查是否过期
            if (data.expiration && Date.now() > data.expiration) {
                await this.remove(key, options);
                return null;
            }

            return data.value;
        } catch (error) {
            console.error('Error getting storage data:', error);
            throw error;
        }
    }

    // 删除数据
    async remove(key, options = {}) {
        try {
            const config = { ...this.defaultConfig, ...options };

            switch (config.storageType) {
                case 'local':
                    if (chrome?.storage?.local) {
                        await chrome.storage.local.remove(key);
                    } else {
                        localStorage.removeItem(key);
                    }
                    break;
                case 'sync':
                    if (chrome?.storage?.sync) {
                        await chrome.storage.sync.remove(key);
                    } else {
                        throw new Error('Chrome sync storage is not available');
                    }
                    break;
                case 'session':
                    sessionStorage.removeItem(key);
                    break;
                default:
                    throw new Error('Invalid storage type');
            }

            return true;
        } catch (error) {
            console.error('Error removing storage data:', error);
            throw error;
        }
    }

    // 清除所有数据
    async clear(options = {}) {
        try {
            const config = { ...this.defaultConfig, ...options };

            switch (config.storageType) {
                case 'local':
                    if (chrome?.storage?.local) {
                        await chrome.storage.local.clear();
                    } else {
                        localStorage.clear();
                    }
                    break;
                case 'sync':
                    if (chrome?.storage?.sync) {
                        await chrome.storage.sync.clear();
                    } else {
                        throw new Error('Chrome sync storage is not available');
                    }
                    break;
                case 'session':
                    sessionStorage.clear();
                    break;
                default:
                    throw new Error('Invalid storage type');
            }

            return true;
        } catch (error) {
            console.error('Error clearing storage:', error);
            throw error;
        }
    }

    // 获取存储使用情况
    async getStorageUsage(options = {}) {
        try {
            const config = { ...this.defaultConfig, ...options };
            let usage = {
                used: 0,
                total: 0,
                percentage: 0
            };

            switch (config.storageType) {
                case 'local':
                    if (chrome?.storage?.local) {
                        const info = await chrome.storage.local.getBytesInUse();
                        usage.used = info;
                        usage.total = chrome.storage.local.QUOTA_BYTES;
                    } else {
                        usage.used = new Blob(Object.values(localStorage)).size;
                        usage.total = 5 * 1024 * 1024; // 默认 5MB
                    }
                    break;
                case 'sync':
                    if (chrome?.storage?.sync) {
                        const info = await chrome.storage.sync.getBytesInUse();
                        usage.used = info;
                        usage.total = chrome.storage.sync.QUOTA_BYTES;
                    }
                    break;
                case 'session':
                    usage.used = new Blob(Object.values(sessionStorage)).size;
                    usage.total = 5 * 1024 * 1024; // 默认 5MB
                    break;
            }

            usage.percentage = (usage.used / usage.total) * 100;
            return usage;
        } catch (error) {
            console.error('Error getting storage usage:', error);
            throw error;
        }
    }

    // 添加存储变化监听器
    addStorageListener(callback, options = {}) {
        const config = { ...this.defaultConfig, ...options };
        
        const listener = (changes, areaName) => {
            const changedItems = {};
            for (const [key, change] of Object.entries(changes)) {
                if (change.newValue) {
                    try {
                        changedItems[key] = JSON.parse(change.newValue).value;
                    } catch (e) {
                        changedItems[key] = change.newValue;
                    }
                }
            }
            callback(changedItems, areaName);
        };

        if (chrome?.storage) {
            chrome.storage.onChanged.addListener(listener);
        } else {
            window.addEventListener('storage', (event) => {
                const changes = {
                    [event.key]: {
                        newValue: event.newValue,
                        oldValue: event.oldValue
                    }
                };
                listener(changes, 'local');
            });
        }

        this.listeners.set(callback, listener);
        return () => this.removeStorageListener(callback);
    }

    // 移除存储变化监听器
    removeStorageListener(callback) {
        const listener = this.listeners.get(callback);
        if (listener) {
            if (chrome?.storage) {
                chrome.storage.onChanged.removeListener(listener);
            } else {
                window.removeEventListener('storage', listener);
            }
            this.listeners.delete(callback);
        }
    }
}

// 创建单例实例
const storageManager = new StorageManager();
export default storageManager; 