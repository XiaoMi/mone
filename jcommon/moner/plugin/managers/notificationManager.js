/**
 * 通知管理类
 */
export class NotificationManager {
    constructor() {
        // 通知配置
        this.defaultConfig = {
            duration: 5000,           // 通知显示时长（毫秒）
            enabled: true,            // 是否启用通知
            position: 'bottom-right', // 通知位置
            maxCount: 5,              // 最大同时显示数量
            types: {                  // 不同类型通知的配置
                success: {
                    iconUrl: '/icons/success.png',
                    enabled: true
                },
                error: {
                    iconUrl: '/icons/error.png',
                    enabled: true
                },
                warning: {
                    iconUrl: '/icons/warning.png',
                    enabled: true
                },
                info: {
                    iconUrl: '/icons/info.png',
                    enabled: true
                }
            }
        };

        // 存储活动通知
        this.activeNotifications = new Map();
        
        // 初始化通知点击监听器
        if (chrome?.notifications) {
            this.initializeNotificationListeners();
        }
    }

    // 初始化通知监听器
    initializeNotificationListeners() {
        chrome.notifications.onClicked.addListener((notificationId) => {
            this.handleNotificationClick(notificationId);
        });

        chrome.notifications.onClosed.addListener((notificationId) => {
            this.handleNotificationClosed(notificationId);
        });
    }

    // 处理通知点击
    handleNotificationClick(notificationId) {
        const notification = this.activeNotifications.get(notificationId);
        if (notification?.onClick) {
            notification.onClick(notificationId);
        }
    }

    // 处理通知关闭
    handleNotificationClosed(notificationId) {
        const notification = this.activeNotifications.get(notificationId);
        if (notification?.onClose) {
            notification.onClose(notificationId);
        }
        this.activeNotifications.delete(notificationId);
    }

    // 创建通知
    async create(options) {
        if (!this.defaultConfig.enabled || !chrome?.notifications) return null;

        const {
            type = 'info',
            title,
            message,
            duration = this.defaultConfig.duration,
            onClick,
            onClose,
            contextMessage,
            buttons,
            items,
            progress
        } = options;

        // 生成唯一通知ID
        const notificationId = `notification_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;

        // 构建通知选项
        const notificationOptions = {
            type: items ? 'list' : buttons ? 'basic' : progress ? 'progress' : 'basic',
            iconUrl: this.defaultConfig.types[type]?.iconUrl,
            title,
            message,
            contextMessage,
            buttons,
            items,
            progress
        };

        try {
            await chrome.notifications.create(notificationId, notificationOptions);
            
            // 存储通知信息
            this.activeNotifications.set(notificationId, {
                ...options,
                createdAt: Date.now()
            });

            // 如果设置了显示时长，则自动关闭
            if (duration > 0) {
                setTimeout(() => {
                    this.clear(notificationId);
                }, duration);
            }

            return notificationId;
        } catch (error) {
            console.error('Error creating notification:', error);
            return null;
        }
    }

    // 更新通知
    async update(notificationId, options) {
        if (!chrome?.notifications) return false;

        try {
            const notification = this.activeNotifications.get(notificationId);
            if (!notification) return false;

            const updateOptions = {
                type: options.items ? 'list' : options.buttons ? 'basic' : options.progress ? 'progress' : 'basic',
                iconUrl: options.iconUrl || notification.iconUrl,
                title: options.title || notification.title,
                message: options.message || notification.message,
                contextMessage: options.contextMessage,
                buttons: options.buttons,
                items: options.items,
                progress: options.progress
            };

            const success = await chrome.notifications.update(notificationId, updateOptions);
            
            if (success) {
                this.activeNotifications.set(notificationId, {
                    ...notification,
                    ...options,
                    updatedAt: Date.now()
                });
            }

            return success;
        } catch (error) {
            console.error('Error updating notification:', error);
            return false;
        }
    }

    // 清除指定通知
    async clear(notificationId) {
        if (!chrome?.notifications) return;

        try {
            await chrome.notifications.clear(notificationId);
            this.activeNotifications.delete(notificationId);
        } catch (error) {
            console.error('Error clearing notification:', error);
        }
    }

    // 清除所有通知
    async clearAll() {
        if (!chrome?.notifications) return;

        try {
            const notifications = await chrome.notifications.getAll();
            await Promise.all(
                Object.keys(notifications).map(notificationId => 
                    this.clear(notificationId)
                )
            );
            this.activeNotifications.clear();
        } catch (error) {
            console.error('Error clearing all notifications:', error);
        }
    }

    // 获取活动通知列表
    getActiveNotifications() {
        return Array.from(this.activeNotifications.entries()).map(([id, notification]) => ({
            id,
            ...notification
        }));
    }

    // 更新通知配置
    updateConfig(config) {
        this.defaultConfig = {
            ...this.defaultConfig,
            ...config,
            types: {
                ...this.defaultConfig.types,
                ...(config.types || {})
            }
        };
    }

    // 快捷方法：显示成功通知
    async success(title, message, options = {}) {
        return this.create({
            type: 'success',
            title,
            message,
            ...options
        });
    }

    // 快捷方法：显示错误通知
    async error(title, message, options = {}) {
        return this.create({
            type: 'error',
            title,
            message,
            ...options
        });
    }

    // 快捷方法：显示警告通知
    async warning(title, message, options = {}) {
        return this.create({
            type: 'warning',
            title,
            message,
            ...options
        });
    }

    // 快捷方法：显示信息通知
    async info(title, message, options = {}) {
        return this.create({
            type: 'info',
            title,
            message,
            ...options
        });
    }
}

// 创建单例实例
const notificationManager = new NotificationManager();
export default notificationManager; 