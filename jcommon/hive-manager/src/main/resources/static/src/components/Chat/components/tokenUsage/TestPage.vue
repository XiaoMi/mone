<template>
  <div class="token-usage-test-page">
    <div class="page-header">
      <h1>Token使用量组件测试页面</h1>
      <p>展示Token使用量组件的各种使用场景和效果</p>
    </div>

    <div class="test-sections">
      <!-- 基础使用 -->
      <section class="test-section">
        <h2>1. 基础使用</h2>
        <div class="test-item">
          <h3>默认样式</h3>
          <TokenUsage
            :used-tokens="25000"
            :total-tokens="100000"
          />
        </div>
      </section>

      <!-- 不同状态 -->
      <section class="test-section">
        <h2>2. 不同使用状态</h2>
        <div class="test-grid">
          <div class="test-item">
            <h3>安全状态 (30%)</h3>
            <TokenUsage
              :used-tokens="30000"
              :total-tokens="100000"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>警告状态 (75%)</h3>
            <TokenUsage
              :used-tokens="75000"
              :total-tokens="100000"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>危险状态 (95%)</h3>
            <TokenUsage
              :used-tokens="95000"
              :total-tokens="100000"
              :show-details="true"
            />
          </div>
        </div>
      </section>

      <!-- 不同主题 -->
      <section class="test-section">
        <h2>3. 自定义主题</h2>
        <div class="test-grid">
          <div class="test-item">
            <h3>成功主题</h3>
            <TokenUsage
              :used-tokens="45000"
              :total-tokens="100000"
              theme="success"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>警告主题</h3>
            <TokenUsage
              :used-tokens="65000"
              :total-tokens="100000"
              theme="warning"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>危险主题</h3>
            <TokenUsage
              :used-tokens="85000"
              :total-tokens="100000"
              theme="danger"
              :show-details="true"
            />
          </div>
        </div>
      </section>

      <!-- 动态更新演示 -->
      <section class="test-section">
        <h2>4. 动态更新演示</h2>
        <div class="test-item">
          <h3>模拟聊天Token消耗</h3>
          <TokenUsage
            :used-tokens="dynamicTokens.used"
            :total-tokens="dynamicTokens.total"
            :show-details="true"
          />
          <div class="controls">
            <el-button @click="simulateUserInput" type="primary">
              模拟用户输入 (+{{ randomInputTokens }}tokens)
            </el-button>
            <el-button @click="simulateAIResponse" type="success">
              模拟AI回复 (+{{ randomOutputTokens }}tokens)
            </el-button>
            <el-button @click="simulateChatSession" type="info">
              模拟对话会话
            </el-button>
            <el-button @click="resetDynamicTokens" type="warning">
              重置计数
            </el-button>
          </div>
          <div class="token-info">
            <p><strong>使用率:</strong> {{ ((dynamicTokens.used / dynamicTokens.total) * 100).toFixed(2) }}%</p>
            <p><strong>剩余Token:</strong> {{ (dynamicTokens.total - dynamicTokens.used).toLocaleString() }}</p>
            <p><strong>预计剩余轮次:</strong> {{ Math.floor((dynamicTokens.total - dynamicTokens.used) / 500) }}</p>
          </div>
        </div>
      </section>

      <!-- 不同数值范围 -->
      <section class="test-section">
        <h2>5. 不同数值范围测试</h2>
        <div class="test-grid">
          <div class="test-item">
            <h3>小数值 (K级别)</h3>
            <TokenUsage
              :used-tokens="2500"
              :total-tokens="10000"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>大数值 (M级别)</h3>
            <TokenUsage
              :used-tokens="1500000"
              :total-tokens="5000000"
              :show-details="true"
            />
          </div>
          <div class="test-item">
            <h3>接近用完</h3>
            <TokenUsage
              :used-tokens="99800"
              :total-tokens="100000"
              :show-details="true"
            />
          </div>
        </div>
      </section>

      <!-- 集成示例 -->
      <section class="test-section">
        <h2>6. 聊天界面集成示例</h2>
        <div class="chat-demo">
          <div class="chat-header">
            <TokenUsage
              :used-tokens="chatDemo.usedTokens"
              :total-tokens="chatDemo.totalTokens"
              :show-details="true"
            />
          </div>
          <div class="chat-messages">
            <div v-for="(message, index) in chatDemo.messages" :key="index" class="message">
              <div class="message-role">{{ message.role }}:</div>
              <div class="message-content">{{ message.content }}</div>
              <div class="message-tokens">{{ message.tokens }} tokens</div>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="newMessage"
              placeholder="输入消息..."
              @keyup.enter="addChatMessage"
            />
            <el-button @click="addChatMessage" type="primary">发送</el-button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import TokenUsage from './index.vue'

// 动态Token演示数据
const dynamicTokens = reactive({
  used: 15000,
  total: 100000
})

const randomInputTokens = computed(() => Math.floor(Math.random() * 100) + 50)
const randomOutputTokens = computed(() => Math.floor(Math.random() * 300) + 100)

// 聊天演示数据
const chatDemo = reactive({
  usedTokens: 5000,
  totalTokens: 50000,
  messages: [
    { role: 'user', content: '你好，请介绍一下你自己', tokens: 120 },
    { role: 'assistant', content: '你好！我是一个AI助手，很高兴为您服务...', tokens: 280 }
  ]
})

const newMessage = ref('')

// 方法
const simulateUserInput = () => {
  const tokens = randomInputTokens.value
  dynamicTokens.used = Math.min(dynamicTokens.used + tokens, dynamicTokens.total)
}

const simulateAIResponse = () => {
  const tokens = randomOutputTokens.value
  dynamicTokens.used = Math.min(dynamicTokens.used + tokens, dynamicTokens.total)
}

const simulateChatSession = () => {
  let currentUsed = dynamicTokens.used
  const sessionInterval = setInterval(() => {
    const increment = Math.floor(Math.random() * 200) + 100
    currentUsed = Math.min(currentUsed + increment, dynamicTokens.total)
    dynamicTokens.used = currentUsed

    if (currentUsed >= dynamicTokens.total) {
      clearInterval(sessionInterval)
    }
  }, 500)

  // 5秒后停止模拟
  setTimeout(() => {
    clearInterval(sessionInterval)
  }, 5000)
}

const resetDynamicTokens = () => {
  dynamicTokens.used = 0
}

const addChatMessage = () => {
  if (!newMessage.value.trim()) return

  const userTokens = Math.ceil(newMessage.value.length / 4)

  // 添加用户消息
  chatDemo.messages.push({
    role: 'user',
    content: newMessage.value,
    tokens: userTokens
  })

  chatDemo.usedTokens += userTokens

  // 模拟AI回复
  setTimeout(() => {
    const responses = [
      '我理解了您的问题，让我来为您解答...',
      '这是一个很好的问题，根据我的了解...',
      '谢谢您的提问，我的建议是...',
      '关于这个话题，我想分享一些观点...'
    ]
    const aiResponse = responses[Math.floor(Math.random() * responses.length)]
    const aiTokens = Math.ceil(aiResponse.length / 4) + Math.floor(Math.random() * 200)

    chatDemo.messages.push({
      role: 'assistant',
      content: aiResponse,
      tokens: aiTokens
    })

    chatDemo.usedTokens = Math.min(chatDemo.usedTokens + aiTokens, chatDemo.totalTokens)
  }, 1000)

  newMessage.value = ''
}
</script>

<style scoped lang="scss">
.token-usage-test-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: var(--el-bg-color-page);
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;

  h1 {
    color: var(--el-text-color-primary);
    margin-bottom: 10px;
  }

  p {
    color: var(--el-text-color-secondary);
  }
}

.test-sections {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.test-section {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  h2 {
    color: var(--el-text-color-primary);
    margin-bottom: 20px;
    border-bottom: 2px solid var(--el-color-primary);
    padding-bottom: 8px;
  }
}

.test-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.test-item {
  h3 {
    color: var(--el-text-color-regular);
    margin-bottom: 12px;
    font-size: 16px;
  }
}

.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 16px 0;
}

.token-info {
  background: var(--el-fill-color-light);
  padding: 16px;
  border-radius: 8px;
  margin-top: 16px;

  p {
    margin: 8px 0;
    color: var(--el-text-color-regular);
  }
}

.chat-demo {
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  overflow: hidden;
  max-width: 600px;

  .chat-header {
    padding: 12px 16px;
    background: var(--el-fill-color-extra-light);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .chat-messages {
    height: 300px;
    overflow-y: auto;
    padding: 16px;

    .message {
      margin-bottom: 16px;
      padding: 12px;
      background: var(--el-fill-color-light);
      border-radius: 6px;

      .message-role {
        font-weight: bold;
        color: var(--el-color-primary);
        margin-bottom: 4px;
      }

      .message-content {
        color: var(--el-text-color-primary);
        margin-bottom: 4px;
      }

      .message-tokens {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .chat-input {
    display: flex;
    padding: 12px 16px;
    background: var(--el-fill-color-extra-light);
    border-top: 1px solid var(--el-border-color-lighter);
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .token-usage-test-page {
    padding: 10px;
  }

  .test-grid {
    grid-template-columns: 1fr;
  }

  .controls {
    justify-content: center;
  }
}
</style>
