// 本地存储管理类
export class LocalStorageManager {
    constructor() {
        // 默认配置
        this.defaultOptions = {
            prefix: 'app_',           // 键名前缀
            expire: 24 * 60 * 60,     // 默认过期时间（秒）
            maxSize: 5 * 1024 * 1024  // 默认最大存储空间（5MB）
        };

        // 存储事件监听器集合
        this.listeners = new Map();
        
        // 初始化存储变化监听
        this.initStorageListener();
    }

    // 设置带前缀的键名
    getKeyWithPrefix(key) {
        return `${this.defaultOptions.prefix}${key}`;
    }

    // 设置数据
    set(key, value, options = {}) {
        try {
            const prefixedKey = this.getKeyWithPrefix(key);
            const expire = options.expire || this.defaultOptions.expire;
            
            // 构建存储对象
            const storageObject = {
                value: value,
                timestamp: Date.now(),
                expire: expire * 1000, // 转换为毫秒
                meta: options.meta || {}
            };

            // 检查存储大小
            const valueSize = this.getValueSize(JSON.stringify(storageObject));
            if (valueSize > this.defaultOptions.maxSize) {
                throw new Error('Storage value exceeds size limit');
            }

            // 存储数据
            localStorage.setItem(prefixedKey, JSON.stringify(storageObject));
            
            // 触发变更事件
            this.triggerListeners(key, 'set', value);
            
            return true;
        } catch (error) {
            console.error('Error setting localStorage item:', error);
            throw error;
        }
    }

    // 获取数据
    get(key, defaultValue = null) {
        try {
            const prefixedKey = this.getKeyWithPrefix(key);
            const item = localStorage.getItem(prefixedKey);

            if (!item) {
                return defaultValue;
            }

            const storageObject = JSON.parse(item);
            
            // 检查是否过期
            if (this.isExpired(storageObject)) {
                this.remove(key);
                return defaultValue;
            }

            return storageObject.value;
        } catch (error) {
            console.error('Error getting localStorage item:', error);
            return defaultValue;
        }
    }

    // 移除数据
    remove(key) {
        try {
            const prefixedKey = this.getKeyWithPrefix(key);
            const value = this.get(key);
            
            localStorage.removeItem(prefixedKey);
            
            // 触发变更事件
            this.triggerListeners(key, 'remove', value);
            
            return true;
        } catch (error) {
            console.error('Error removing localStorage item:', error);
            throw error;
        }
    }

    // 清除所有数据
    clear(onlyPrefixed = true) {
        try {
            if (onlyPrefixed) {
                // 只清除带前缀的项
                Object.keys(localStorage).forEach(key => {
                    if (key.startsWith(this.defaultOptions.prefix)) {
                        localStorage.removeItem(key);
                    }
                });
            } else {
                // 清除所有项
                localStorage.clear();
            }
            
            // 触发变更事件
            this.triggerListeners(null, 'clear');
            
            return true;
        } catch (error) {
            console.error('Error clearing localStorage:', error);
            throw error;
        }
    }

    // 获取所有键
    keys(withoutPrefix = true) {
        try {
            const keys = Object.keys(localStorage)
                .filter(key => key.startsWith(this.defaultOptions.prefix));
            
            return withoutPrefix 
                ? keys.map(key => key.replace(this.defaultOptions.prefix, ''))
                : keys;
        } catch (error) {
            console.error('Error getting localStorage keys:', error);
            return [];
        }
    }

    // 检查键是否存在
    has(key) {
        const prefixedKey = this.getKeyWithPrefix(key);
        return localStorage.getItem(prefixedKey) !== null;
    }

    // 获取存储使用情况
    getStorageInfo() {
        try {
            let totalSize = 0;
            let itemCount = 0;

            Object.keys(localStorage).forEach(key => {
                if (key.startsWith(this.defaultOptions.prefix)) {
                    totalSize += this.getValueSize(localStorage.getItem(key));
                    itemCount++;
                }
            });

            return {
                totalSize: totalSize,
                itemCount: itemCount,
                maxSize: this.defaultOptions.maxSize,
                usagePercentage: (totalSize / this.defaultOptions.maxSize) * 100
            };
        } catch (error) {
            console.error('Error getting storage info:', error);
            throw error;
        }
    }

    // 添加存储变化监听器
    addListener(key, callback) {
        if (!this.listeners.has(key)) {
            this.listeners.set(key, new Set());
        }
        this.listeners.get(key).add(callback);
        
        return () => this.removeListener(key, callback);
    }

    // 移除存储变化监听器
    removeListener(key, callback) {
        if (this.listeners.has(key)) {
            this.listeners.get(key).delete(callback);
        }
    }

    // 检查数据是否过期
    isExpired(storageObject) {
        return Date.now() - storageObject.timestamp > storageObject.expire;
    }

    // 获取值的大小（字节）
    getValueSize(value) {
        return new Blob([value]).size;
    }

    // 初始化存储变化监听
    initStorageListener() {
        window.addEventListener('storage', (event) => {
            if (event.key && event.key.startsWith(this.defaultOptions.prefix)) {
                const key = event.key.replace(this.defaultOptions.prefix, '');
                this.triggerListeners(key, 'external', event.newValue);
            }
        });
    }

    // 触发监听器
    triggerListeners(key, action, value) {
        if (this.listeners.has(key)) {
            this.listeners.get(key).forEach(callback => {
                callback({ key, action, value });
            });
        }
        // 触发通用监听器（key为null的监听器）
        if (this.listeners.has(null)) {
            this.listeners.get(null).forEach(callback => {
                callback({ key, action, value });
            });
        }
    }

    // 清理过期数据
    cleanExpired() {
        try {
            const keys = this.keys(false);
            let cleanedCount = 0;

            keys.forEach(key => {
                const item = localStorage.getItem(key);
                if (item) {
                    const storageObject = JSON.parse(item);
                    if (this.isExpired(storageObject)) {
                        localStorage.removeItem(key);
                        cleanedCount++;
                    }
                }
            });

            return cleanedCount;
        } catch (error) {
            console.error('Error cleaning expired items:', error);
            throw error;
        }
    }
}

// 创建单例实例
const localStorageManager = new LocalStorageManager();
export default localStorageManager; 