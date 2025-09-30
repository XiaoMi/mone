<template>
  <div class="token-usage-container">
    <div class="token-usage-content">
      <!-- 左侧：已使用Token量 -->
      <div class="token-used">
        <span class="token-label">已使用</span>
        <span class="token-value">{{ formatNumber(usedTokens) }}</span>
      </div>

      <!-- 中间：进度条 -->
      <div class="progress-container">
        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: progressPercentage + '%' }"
            :class="getProgressClass()"
          ></div>
        </div>
        <div class="progress-text">{{ progressPercentage.toFixed(1) }}%</div>
      </div>

      <!-- 右侧：总Token量 -->
      <div class="token-total">
        <span class="token-label">总量</span>
        <span class="token-value">{{ formatNumber(totalTokens) }}</span>
      </div>
    </div>

    <!-- 详细信息（可选显示） -->
    <div v-if="showDetails" class="token-details">
      <div class="detail-item">
        <span>剩余Token：</span>
        <span class="remaining-tokens">{{ formatNumber(remainingTokens) }}</span>
      </div>
      <div class="detail-item">
        <span>使用率：</span>
        <span :class="getUsageRateClass()">{{ progressPercentage.toFixed(2) }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, withDefaults } from 'vue'

// 定义组件名称
defineOptions({
  name: 'TokenUsage',
})

// 定义Props
interface Props {
  usedTokens: number
  totalTokens: number
  showDetails?: boolean
  theme?: 'default' | 'success' | 'warning' | 'danger'
}

const props = withDefaults(defineProps<Props>(), {
  usedTokens: 0,
  totalTokens: 1000000,
  showDetails: false,
  theme: 'default',
})

// 计算属性
const progressPercentage = computed(() => {
  if (props.totalTokens === 0) return 0
  return Math.min((props.usedTokens / props.totalTokens) * 100, 100)
})

const remainingTokens = computed(() => {
  return Math.max(props.totalTokens - props.usedTokens, 0)
})

// 格式化数字显示
const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// 获取进度条样式类
const getProgressClass = () => {
  if (props.theme !== 'default') {
    return `progress-${props.theme}`
  }

  const percentage = progressPercentage.value
  if (percentage >= 90) return 'progress-danger'
  if (percentage >= 70) return 'progress-warning'
  return 'progress-success'
}

// 获取使用率样式类
const getUsageRateClass = () => {
  const percentage = progressPercentage.value
  if (percentage >= 90) return 'rate-danger'
  if (percentage >= 70) return 'rate-warning'
  return 'rate-success'
}
</script>

<style scoped lang="scss">
.token-usage-container {
  // border-radius: 8px;
  box-shadow: -4px 3px 17px 4px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  // padding: 6px;
  box-sizing: border-box;

  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  }
}

.token-usage-content {
  display: flex;
  align-items: center;
  gap: 20px;
  // margin-bottom: 12px;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 12px;
  }
}

.token-used,
.token-total {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;

  .token-label {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 4px;
  }

  .token-value {
    line-height: 100%;
    font-size: 12px;
    font-weight: bold;
    color: var(--el-text-color-primary);
  }
}

.progress-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 200px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  margin-top: 10px;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease, background-color 0.3s ease;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    // animation: shimmer 2s infinite;
  }

  &.progress-success {
    background: linear-gradient(90deg, #67c23a, #85ce61);
  }

  &.progress-warning {
    background: linear-gradient(90deg, #e6a23c, #f2c55c);
  }

  &.progress-danger {
    background: linear-gradient(90deg, #f56c6c, #f78989);
  }
}

.progress-text {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.token-details {
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 12px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;

  @media (max-width: 480px) {
    flex-direction: column;
    gap: 8px;
  }
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;

  span:first-child {
    color: var(--el-text-color-secondary);
  }
}

.remaining-tokens {
  color: var(--el-color-primary);
  font-weight: 500;
}

.rate-success {
  color: var(--el-color-success);
  font-weight: 500;
}

.rate-warning {
  color: var(--el-color-warning);
  font-weight: 500;
}

.rate-danger {
  color: var(--el-color-danger);
  font-weight: 500;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// 暗色主题支持
.dark {
  .token-usage-container {
    background: rgba(20, 20, 50, 0.5);
  }
}

.light {
  .token-usage-container {
    background: rgba(20, 20, 50, 0.5);
  }
  .token-label,
  .token-value,
  .progress-text,
  .token-value {
    color: #ffffff;
  }
}
.cyberpunk {
  .token-usage-container {
    background: rgba(20, 20, 50, 0.5);
  }
   .token-label,
  .token-value,
  .progress-text,
  .token-value {
    color: #ffffff;
  }
}
</style>
