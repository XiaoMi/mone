# TokenUsage 组件使用指南

## 🚀 快速开始

### 1. 基础使用

在任意Vue组件中导入并使用：

```vue
<template>
  <div class="my-chat-app">
    <!-- 基础用法：显示Token使用情况 -->
    <TokenUsage 
      :used-tokens="25000" 
      :total-tokens="100000" 
    />
  </div>
</template>

<script setup>
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'
</script>
```

### 2. 与Store集成

结合Pinia Store使用，实现动态Token管理：

```vue
<template>
  <div class="chat-container">
    <!-- 显示实时Token使用量 -->
    <TokenUsage 
      :used-tokens="chatStore.tokenUsage.usedTokens" 
      :total-tokens="chatStore.tokenUsage.totalTokens" 
      :show-details="true"
      @click="handleTokenClick"
    />
    
    <!-- 聊天内容 -->
    <div class="messages">
      <!-- 消息列表 -->
    </div>
    
    <!-- 输入框 -->
    <div class="input-area">
      <input 
        v-model="userInput" 
        @keyup.enter="sendMessage"
        placeholder="请输入消息..."
      />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useChatContextStore } from '@/stores/chat-context'
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'

const chatStore = useChatContextStore()
const userInput = ref('')

// 发送消息并更新Token使用量
const sendMessage = async () => {
  if (!userInput.value.trim()) return
  
  const message = userInput.value
  userInput.value = ''
  
  // 估算输入Token（粗略计算：4个字符 ≈ 1个Token）
  const inputTokens = Math.ceil(message.length / 4)
  
  try {
    // 发送API请求（这里是示例）
    const response = await sendChatMessage(message)
    
    // 从API响应中获取实际Token使用量
    const outputTokens = response.usage?.completion_tokens || 0
    const actualInputTokens = response.usage?.prompt_tokens || inputTokens
    
    // 更新Store中的Token使用量
    chatStore.updateTokenUsage(actualInputTokens, outputTokens)
    
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

const handleTokenClick = () => {
  console.log('当前Token使用情况:', {
    已使用: chatStore.tokenUsage.usedTokens,
    总量: chatStore.tokenUsage.totalTokens,
    使用率: chatStore.getTokenUsagePercentage().toFixed(2) + '%'
  })
}

// 模拟API调用
const sendChatMessage = async (message) => {
  // 实际项目中这里应该是真实的API调用
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        usage: {
          prompt_tokens: Math.ceil(message.length / 4),
          completion_tokens: Math.floor(Math.random() * 200) + 50
        }
      })
    }, 1000)
  })
}
</script>
```

### 3. 在聊天窗口顶部显示

```vue
<template>
  <div class="chat-window">
    <!-- Token使用量显示区 -->
    <div class="token-header">
      <TokenUsage 
        :used-tokens="tokenData.used" 
        :total-tokens="tokenData.total"
        :show-details="isTokenDetailsVisible"
        theme="default"
      />
      <el-button 
        size="small" 
        @click="toggleTokenDetails"
        class="toggle-btn"
      >
        {{ isTokenDetailsVisible ? '隐藏详情' : '显示详情' }}
      </el-button>
    </div>
    
    <!-- 聊天内容区 -->
    <div class="chat-content">
      <!-- 消息列表组件 -->
      <MessageList :messages="messages" />
      
      <!-- 输入组件 -->
      <UserInput @submit="handleUserInput" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'

const isTokenDetailsVisible = ref(false)
const tokenData = reactive({
  used: 15000,
  total: 100000
})

const messages = ref([])

const toggleTokenDetails = () => {
  isTokenDetailsVisible.value = !isTokenDetailsVisible.value
}

const handleUserInput = async (input) => {
  // 处理用户输入
  messages.value.push({
    role: 'user',
    content: input,
    timestamp: new Date()
  })
  
  // 模拟AI回复并更新Token使用量
  const response = await simulateAIResponse(input)
  
  messages.value.push({
    role: 'assistant',
    content: response.content,
    timestamp: new Date()
  })
  
  // 更新Token使用量
  tokenData.used += response.tokenUsed
}

const simulateAIResponse = async (input) => {
  return new Promise(resolve => {
    setTimeout(() => {
      const responseLength = Math.floor(Math.random() * 200) + 50
      const content = 'AI回复内容...'
      const tokenUsed = Math.ceil((input.length + content.length) / 4)
      
      resolve({ content, tokenUsed })
    }, 1000)
  })
}

// 监听Token使用量变化，显示警告
watch(() => tokenData.used / tokenData.total, (ratio) => {
  if (ratio > 0.9) {
    ElMessage.warning('Token使用量已超过90%，请注意！')
  } else if (ratio > 0.7) {
    ElMessage.info('Token使用量已超过70%')
  }
})
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.token-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--el-bg-color-page);
  border-bottom: 1px solid var(--el-border-color);
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toggle-btn {
  margin-left: 12px;
}
</style>
```

## 🎯 高级用法

### 自定义主题和样式

```vue
<template>
  <!-- 危险状态的Token使用量 -->
  <TokenUsage 
    :used-tokens="95000" 
    :total-tokens="100000" 
    theme="danger"
    :show-details="true"
  />
  
  <!-- 自定义样式 -->
  <TokenUsage 
    :used-tokens="50000" 
    :total-tokens="100000" 
    class="custom-token-usage"
  />
</template>

<style>
.custom-token-usage {
  --progress-height: 12px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.custom-token-usage .progress-fill {
  background: linear-gradient(90deg, #667eea, #764ba2) !important;
}
</style>
```

### 响应式设计

组件已内置响应式支持，在移动设备上会自动调整布局：

```scss
// 组件内部已包含这些样式
@media (max-width: 768px) {
  .token-usage-content {
    flex-direction: column;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .token-details {
    flex-direction: column;
    gap: 8px;
  }
}
```

## 📚 API参考

### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| usedTokens | number | 0 | 已使用的Token数量 |
| totalTokens | number | 100000 | Token总量 |
| showDetails | boolean | false | 是否显示详细信息 |
| theme | string | 'default' | 主题：'default' \| 'success' \| 'warning' \| 'danger' |

### 事件

| 事件名 | 说明 | 参数 |
|--------|------|------|
| click | 点击组件时触发 | - |

### Store方法（配合useChatContextStore使用）

```javascript
// 更新Token使用量
chatStore.updateTokenUsage(inputTokens, outputTokens)

// 设置总Token量
chatStore.setTotalTokens(totalTokens)

// 重置Token使用量
chatStore.resetTokenUsage()

// 获取使用率百分比
const percentage = chatStore.getTokenUsagePercentage()
```

## 🔧 最佳实践

1. **实时更新**: 每次API调用后及时更新Token使用量
2. **用户提醒**: 当Token使用量超过阈值时给出提示
3. **数据持久化**: 考虑将Token使用量保存到localStorage
4. **错误处理**: 处理Token不足的情况
5. **性能优化**: 避免频繁更新，可以批量更新Token使用量

## 🎨 样式定制

组件使用CSS变量，便于主题定制：

```css
:root {
  --el-color-primary: #409eff;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
  --el-border-color: #dcdfe6;
  --el-bg-color: #ffffff;
}
```

支持暗色主题：

```css
@media (prefers-color-scheme: dark) {
  :root {
    --el-bg-color: #1d1e1f;
    --el-text-color-primary: #e5eaf3;
  }
}
```