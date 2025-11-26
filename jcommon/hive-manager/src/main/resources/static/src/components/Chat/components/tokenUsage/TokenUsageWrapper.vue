<!-- 在现有Chat组件中集成TokenUsage的简单示例 -->
<template>
  <div class="chat-with-token-usage">
    <!-- Token使用量显示在顶部 -->
    <div class="token-display-section">
      <TokenUsage
        :used-tokens="currentUsedTokens"
        :total-tokens="totalTokens"
        :show-details="showTokenDetails"
        @click="toggleTokenDetails"
      />
    </div>

    <!-- 聊天内容区域 -->
    <div class="chat-main-content">
      <!-- 这里放置原有的聊天组件内容 -->
      <slot name="chat-content"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TokenUsage from './index.vue'

// Props
interface Props {
  initialUsedTokens?: number
  initialTotalTokens?: number
  autoUpdate?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  initialUsedTokens: 0,
  initialTotalTokens: 100000,
  autoUpdate: true
})

// 状态
const currentUsedTokens = ref(props.initialUsedTokens)
const totalTokens = ref(props.initialTotalTokens)
const showTokenDetails = ref(false)

// 计算属性
const usagePercentage = computed(() => {
  if (totalTokens.value === 0) return 0
  return Math.min((currentUsedTokens.value / totalTokens.value) * 100, 100)
})

// 方法
const toggleTokenDetails = () => {
  showTokenDetails.value = !showTokenDetails.value
}

const updateTokenUsage = (inputTokens: number, outputTokens: number) => {
  currentUsedTokens.value += inputTokens + outputTokens
}

const resetTokenUsage = () => {
  currentUsedTokens.value = 0
}

const setTotalTokens = (total: number) => {
  totalTokens.value = total
}

// 暴露给父组件的方法
defineExpose({
  updateTokenUsage,
  resetTokenUsage,
  setTotalTokens,
  currentUsedTokens: computed(() => currentUsedTokens.value),
  totalTokens: computed(() => totalTokens.value),
  usagePercentage
})

onMounted(() => {
  console.log('TokenUsage wrapper mounted')
})
</script>

<style scoped lang="scss">
.chat-with-token-usage {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.token-display-section {
  flex-shrink: 0;
  padding: 8px 16px;
  background: var(--el-bg-color-page);
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.chat-main-content {
  flex: 1;
  overflow: hidden;
}
</style>
