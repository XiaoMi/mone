import { ref } from 'vue'
import { defineStore } from 'pinia'

export type NotificationType = 'info' | 'success' | 'warning' | 'error'

export interface McpNotification {
  id: string
  type: NotificationType
  title: string
  message: string
  serverName?: string
  timestamp: number
  read: boolean
}

export const useMcpNotificationStore = defineStore('mcp-notification', () => {
  const notifications = ref<McpNotification[]>([])
  const maxNotifications = 100 // 最多保存100条通知

  // 添加通知
  const addNotification = (
    type: NotificationType,
    title: string,
    message: string,
    serverName?: string
  ) => {
    const notification: McpNotification = {
      id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      type,
      title,
      message,
      serverName,
      timestamp: Date.now(),
      read: false,
    }

    notifications.value.unshift(notification)

    // 限制通知数量
    if (notifications.value.length > maxNotifications) {
      notifications.value = notifications.value.slice(0, maxNotifications)
    }

    return notification.id
  }

  // 移除通知
  const removeNotification = (id: string) => {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index !== -1) {
      notifications.value.splice(index, 1)
    }
  }

  // 清空所有通知
  const clearAllNotifications = () => {
    notifications.value = []
  }

  // 标记通知为已读
  const markAsRead = (id: string) => {
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.read = true
    }
  }

  // 标记所有通知为已读
  const markAllAsRead = () => {
    notifications.value.forEach(n => {
      n.read = true
    })
  }

  // 获取未读通知数量
  const unreadCount = () => {
    return notifications.value.filter(n => !n.read).length
  }

  // 根据服务器名称获取通知
  const getNotificationsByServer = (serverName: string) => {
    return notifications.value.filter(n => n.serverName === serverName)
  }

  // 添加快捷方法
  const addInfoNotification = (title: string, message: string, serverName?: string) => {
    return addNotification('info', title, message, serverName)
  }

  const addSuccessNotification = (title: string, message: string, serverName?: string) => {
    return addNotification('success', title, message, serverName)
  }

  const addWarningNotification = (title: string, message: string, serverName?: string) => {
    return addNotification('warning', title, message, serverName)
  }

  const addErrorNotification = (title: string, message: string, serverName?: string) => {
    return addNotification('error', title, message, serverName)
  }

  return {
    notifications,
    addNotification,
    removeNotification,
    clearAllNotifications,
    markAsRead,
    markAllAsRead,
    unreadCount,
    getNotificationsByServer,
    addInfoNotification,
    addSuccessNotification,
    addWarningNotification,
    addErrorNotification,
  }
})
