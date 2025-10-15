<!-- 在ChatWindow中集成TokenUsage组件的示例 -->
<template>
  <div class="chat-window-with-token">
    <!-- Token使用量显示区域 -->
    <div class="token-usage-header">
      <TokenUsage
        :used-tokens="chatStore.tokenUsage.usedTokens"
        :total-tokens="chatStore.tokenUsage.totalTokens"
        :show-details="true"
        @click="showTokenDetails"
      />
    </div>

    <!-- 原有的聊天窗口内容 -->
    <div class="chat-content">
      <MessageList
        :messages="messages"
        :always-scroll-to-bottom="alwaysScrollToBottom"
        :onMessageCmd="onMessageCmd"
        :onMessageClick="onMessageClick"
        @scrollToTop="$emit('scrollToTop')"
        :onPlayAudio="onPlayAudio"
        @pidAction="handlePidAction"
      />

      <!-- 用户输入区域 -->
      <UserInput
        @userInputSubmit="onUserInputSubmit"
        @fileSubmit="onFileSubmit"
        :showEmoji="showEmoji"
        :suggestions="suggestions"
        :placeholder="placeholder"
        :disabled="disabled"
      />
    </div>

    <!-- Token详情对话框 -->
    <el-dialog
      v-model="tokenDetailsVisible"
      title="Token使用详情"
      width="500px"
      append-to-body
    >
      <div class="token-details-content">
        <div class="detail-row">
          <span class="label">输入Token：</span>
          <span class="value">{{ formatNumber(chatStore.tokenUsage.inputTokens) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">输出Token：</span>
          <span class="value">{{ formatNumber(chatStore.tokenUsage.outputTokens) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">总使用量：</span>
          <span class="value highlight">{{ formatNumber(chatStore.tokenUsage.usedTokens) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">剩余Token：</span>
          <span class="value remaining">{{ formatNumber(chatStore.tokenUsage.totalTokens - chatStore.tokenUsage.usedTokens) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">使用率：</span>
          <span class="value" :class="getUsageRateClass()">{{ chatStore.getTokenUsagePercentage().toFixed(2) }}%</span>
        </div>
        <div class="detail-row">
          <span class="label">最后更新：</span>
          <span class="value">{{ formatTime(chatStore.tokenUsage.lastUpdate) }}</span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="chatStore.resetTokenUsage()" type="warning">
            重置用量
          </el-button>
          <el-button @click="tokenDetailsVisible = false" type="primary">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useChatContextStore } from '@/stores/chat-context'
import TokenUsage from './components/tokenUsage/index.vue'
import MessageList from './MessageList.vue'
import UserInput from './UserInput.vue'

const chatStore = useChatContextStore()
const tokenDetailsVisible = ref(false)

// Props (保持原有的props)
interface Props {
  messages: any[]
  alwaysScrollToBottom: boolean
  onMessageCmd: Function
  onMessageClick: Function
  onPlayAudio: Function
  showEmoji: boolean
  suggestions: string[]
  placeholder: string
  disabled: boolean
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits(['scrollToTop', 'userInputSubmit', 'fileSubmit', 'pidAction'])

// 方法
const showTokenDetails = () => {
  tokenDetailsVisible.value = true
}

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const formatTime = (date: Date): string => {
  return new Date(date).toLocaleString('zh-CN')
}

const getUsageRateClass = () => {
  const percentage = chatStore.getTokenUsagePercentage()
  if (percentage >= 90) return 'rate-danger'
  if (percentage >= 70) return 'rate-warning'
  return 'rate-success'
}

// 处理用户输入
const onUserInputSubmit = (message: any) => {
  // 模拟Token使用量更新
  // 这里应该从API响应中获取实际的Token使用量
  const estimatedInputTokens = Math.floor(message.content.length / 4) // 粗略估算
  const estimatedOutputTokens = Math.floor(Math.random() * 500) + 100 // 模拟输出Token

  chatStore.updateTokenUsage(estimatedInputTokens, estimatedOutputTokens)

  emit('userInputSubmit', message)
}

const onFileSubmit = (file: any) => {
  emit('fileSubmit', file)
}

const handlePidAction = (action: any) => {
  emit('pidAction', action)
}

// 监听消息变化，更新Token使用量
watch(() => props.messages, (newMessages, oldMessages) => {
  if (newMessages.length > (oldMessages?.length || 0)) {
    // 有新消息时，可以根据消息内容估算Token使用量
    const newMessage = newMessages[newMessages.length - 1]
    if (newMessage.meta.role === 'ASSISTANT') {
      // 这是AI回复，估算输出Token
      const content = newMessage.data.content || ''
      const estimatedTokens = Math.floor(content.length / 4)
      chatStore.updateTokenUsage(0, estimatedTokens)
    }
  }
}, { deep: true })

onMounted(() => {
  // 初始化Token总量（可以从配置或API获取）
  chatStore.setTotalTokens(100000)
})
</script>

<style scoped lang="scss">
.chat-window-with-token {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.token-usage-header {
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color-page);
  flex-shrink: 0;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.token-details-content {
  .detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: var(--el-text-color-secondary);
      font-weight: 500;
    }

    .value {
      font-weight: bold;

      &.highlight {
        color: var(--el-color-primary);
      }

      &.remaining {
        color: var(--el-color-success);
      }

      &.rate-success {
        color: var(--el-color-success);
      }

      &.rate-warning {
        color: var(--el-color-warning);
      }

      &.rate-danger {
        color: var(--el-color-danger);
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
