<template>
  <transition name="notification-container-fade">
    <div v-if="notifications.length > 0" class="mcp-notification-container">
      <div class="notification-header">
        <div class="header-title">
          <el-icon class="title-icon"><Bell /></el-icon>
          <span>MCP Server 通知</span>
          <el-badge :value="unreadCount" :max="99" v-if="unreadCount > 0" class="notification-badge" />
        </div>
        <div class="header-actions">
          <el-button 
            v-if="unreadCount > 0"
            type="text" 
            size="small" 
            @click="markAllAsRead"
          >
            全部已读
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="clearAllNotifications"
            :icon="Delete"
          >
            清空
          </el-button>
        </div>
      </div>
      
      <div class="notification-list">
        <transition-group name="notification-item">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="notification-item"
            :class="[`notification-${notification.type}`, { 'notification-read': notification.read }]"
            @click="markAsRead(notification.id)"
          >
            <div class="notification-icon">
              <el-icon v-if="notification.type === 'info'" class="icon-info"><InfoFilled /></el-icon>
              <el-icon v-else-if="notification.type === 'success'" class="icon-success"><SuccessFilled /></el-icon>
              <el-icon v-else-if="notification.type === 'warning'" class="icon-warning"><WarningFilled /></el-icon>
              <el-icon v-else-if="notification.type === 'error'" class="icon-error"><CircleCloseFilled /></el-icon>
            </div>
            
            <div class="notification-content">
              <div class="notification-title">{{ notification.title }}</div>
              <div class="notification-message">{{ notification.message }}</div>
              <div class="notification-meta">
                <span v-if="notification.serverName" class="server-name">
                  <el-tag size="small" effect="plain">{{ notification.serverName }}</el-tag>
                </span>
                <span class="notification-time">{{ formatTime(notification.timestamp) }}</span>
              </div>
            </div>
            
            <div class="notification-actions">
              <el-button 
                type="text" 
                size="small" 
                @click.stop="removeNotification(notification.id)"
                :icon="Close"
              />
            </div>
          </div>
        </transition-group>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useMcpNotificationStore } from '@/stores/mcp-notification'
import { 
  Bell, 
  Delete, 
  Close,
  InfoFilled,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled
} from '@element-plus/icons-vue'

// Store
const notificationStore = useMcpNotificationStore()
const { notifications } = storeToRefs(notificationStore)
const { removeNotification, clearAllNotifications, markAsRead, markAllAsRead } = notificationStore

// 计算未读数量
const unreadCount = computed(() => {
  return notifications.value.filter((n: any) => !n.read).length
})

// 格式化时间
const formatTime = (timestamp: number) => {
  const now = Date.now()
  const diff = now - timestamp
  
  // 小于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  
  // 小于1小时
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分钟前`
  }
  
  // 小于24小时
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时前`
  }
  
  // 显示具体时间
  const date = new Date(timestamp)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  
  return `${month}-${day} ${hour}:${minute}`
}
</script>

<style lang="scss" scoped>
</style>
