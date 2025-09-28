<!-- TokenUsage组件使用示例 -->

<template>
  <div class="demo-container">
    <!-- <h2>Token使用量组件示例</h2> -->

    <!-- 基础用法 -->
    <div class="demo-section">
      <!-- <h3>基础用法</h3> -->
      <TokenUsage :used-tokens="usedTokens1" :total-tokens="totalTokens1" />
    </div>

    <!-- 显示详细信息 -->
    <!-- <div class="demo-section">
      <h3>显示详细信息</h3>
      <TokenUsage
        :used-tokens="usedTokens2"
        :total-tokens="totalTokens2"
        :show-details="true"
      />
    </div> -->

    <!-- 不同主题 -->
    <!-- <div class="demo-section">
      <h3>不同状态主题</h3>
      <div class="theme-demos">
        <TokenUsage
          :used-tokens="30000"
          :total-tokens="100000"
          theme="success"
          :show-details="true"
        />
        <TokenUsage
          :used-tokens="75000"
          :total-tokens="100000"
          theme="warning"
          :show-details="true"
        />
        <TokenUsage
          :used-tokens="95000"
          :total-tokens="100000"
          theme="danger"
          :show-details="true"
        />
      </div>
    </div> -->

    <!-- 动态更新演示 -->
    <!-- <div class="demo-section"> -->
      <!-- <h3>动态更新演示</h3> -->
      <!-- <TokenUsage
        :used-tokens="dynamicUsedTokens"
        :total-tokens="dynamicTotalTokens"
        :show-details="true"
      /> -->
      <!-- <div class="controls">
        <el-button @click="increaseTokens">增加使用量</el-button>
        <el-button @click="resetTokens">重置</el-button>
        <el-button @click="simulateUsage">模拟使用</el-button>
      </div> -->
    <!-- </div> -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TokenUsage from './index.vue'
let interval: ReturnType<typeof setInterval> | undefined
// 示例数据
const usedTokens1 = ref(25000)
const totalTokens1 = ref(100000)

const usedTokens2 = ref(65000)
const totalTokens2 = ref(100000)

// 动态演示数据
const dynamicUsedTokens = ref(10000)
const dynamicTotalTokens = ref(100000)

// 方法
const increaseTokens = () => {
  if (dynamicUsedTokens.value < dynamicTotalTokens.value) {
    dynamicUsedTokens.value += Math.floor(Math.random() * 5000) + 1000
  }
}

const resetTokens = () => {
  if (interval) {
    clearInterval(interval)
    interval = undefined
  }
  dynamicUsedTokens.value = 0
}

const simulateUsage = () => {
  if (interval) {
    clearInterval(interval)
    interval = undefined
  }
  interval = setInterval(() => {
    if (dynamicUsedTokens.value >= dynamicTotalTokens.value) {
      clearInterval(interval)
      return
    }
    dynamicUsedTokens.value += Math.floor(Math.random() * 1000) + 100
  }, 200)
}

onMounted(() => {
  // 可以在这里初始化真实的token数据
  console.log('TokenUsage component demo mounted')
})
</script>

<style scoped lang="scss">
.demo-container {
  // position: relative;
  // z-index: 123123;
  // padding: 20px;
  // max-width: 800px;
  // margin: 0 auto;
}

.demo-section {
  margin-bottom: 20px;


  h3 {
    margin-bottom: 16px;
    color: var(--el-text-color-primary);
  }
}

.theme-demos {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.controls {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  .el-button {
    color: var(--el-color-chat-text-secondary);
  }
}
</style>
