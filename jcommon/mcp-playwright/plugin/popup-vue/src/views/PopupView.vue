<template>
  <div class="chat-panel">
    <div class="chat-panel-body">
      <div id="scrollRef" ref="scrollRef" style="overflow: auto; height: 100%">
        <Message
          v-for="(item, index) of conversions"
          :avatarUrl="''"
          :avatar="item.avatar"
          :username="item.name"
          :key="index"
          :date-time="item.dateTime"
          :text="item.text"
          :inversion="item.inversion"
          :error="item.error"
          :textType="item.textType"
          :loading="item.loading"
          :show-cursor="item.showCursor"
          :type="item.type"
          :show-operate="item.isShowOperate"
          :voice-setting="{}"
          :class="[{ 'message-item-question': item.type === 'question' }]"
          @onTryAgain="tryAgain"
          :flowData="{}"
          language=""
          :multimodal="item.multimodal"
        />
      </div>
    </div>
    <div class="chat-panel-footer">
      <div class="footer-opts">
        <el-popover width="90px" popper-class="footer-clear-popper">
          <div class="clear-tip">清空会话</div>
          <template #reference>
            <el-button
              style="margin-right: 10px"
              @click="clearMessages"
              :icon="Delete"
              circle
            ></el-button>
          </template>
        </el-popover>
        <CommonTextarea
          ref="inputRef"
          v-model="messageInput"
          class="flex-1"
          :placeholder="placeholder"
          @enterFn="sendMessage"
          :disabled="loading"
          :showImg="showImg"
          size="large"
        ></CommonTextarea>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { Message } from '@/components/common-message'
import CommonTextarea from '@/components/common-textarea/CommonTextarea.vue'

interface ChatMessage {
  multimodal?: number
  type?: string
  name?: string
  text: string
  msgType: string // 标识该message的类别，history要用到。
  dateTime?: string
  inversion: boolean
  aiModel?: string
  avatar?: string
  error?: boolean
  textType?: string
  loading?: boolean
  showCursor?: boolean
  isShowOperate?: boolean
  flowData?: Object
  //  如果上一条是bot Message 则新发送时需要加一个Type
  isBotMessage?: Boolean
}

const placeholder = ref('请输入消息')
const showImg = ref(false)
const loading = ref(false)
const messageInput = ref('')
const isSending = ref(false)
const messageContainer = ref<HTMLElement | null>(null)
const conversions = ref<ChatMessage[]>([])

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString()
}

// 滚动到最新消息
const scrollToBottom = async () => {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

const tryAgain = () => {
  console.log('tryAgain')
}

// 方法定义
const closeSidebar = () => {
  chrome.sidePanel.close()
}

const clearMessages = () => {
  conversions.value = []
  chrome.runtime.sendMessage({ type: 'clearMessageHistory' })
}

const sendMessage = async () => {
  const message = messageInput.value.trim()
  if (!message || isSending.value) return

  // 添加用户消息
  conversions.value.push({
    type: 'user',
    text: message,
    dateTime: formatTime(Date.now()),
    msgType: 'chat',
    inversion: false
  })

  messageInput.value = ''
  isSending.value = true
  await scrollToBottom()

  try {
    // 发送消息到后台
    chrome.runtime.sendMessage({
      type: 'sendWebSocketMessage',
      text: message
    }, response => {
      if (response.success) {
        // 可以在这里处理成功响应
        conversions.value.push({
          type: 'assistant',
          text: response,
          dateTime: formatTime(Date.now()),
          msgType: 'md',
          inversion: true
        })
        scrollToBottom()
      } else {
        ElMessage.error('发送消息失败')
      }
      isSending.value = false
    })
  } catch (error) {
    ElMessage.error('发送消息失败')
    isSending.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  // 监听来自 background script 的消息
  chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'newWebSocketMessage') {
      conversions.value.push({
        type: 'assistant',
        text: message,
        dateTime: formatTime(Date.now()),
        msgType: 'md',
        inversion: true
      })
      scrollToBottom()
    }
  })

  // 获取历史消息
  chrome.runtime.sendMessage({ type: 'getMessageHistory' }, (response) => {
    if (response.messages) {
      conversions.value = response.messages
      scrollToBottom()
    }
  })
})
</script>

<style scoped lang="scss">
.chat-panel {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  padding: 20px;

  &-header {
    display: flex;
    flex-direction: column;
    justify-content: center;
    justify-items: center;
    flex-grow: 0;
    flex-shrink: 0;

    &-icon {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
      justify-items: center;
    }

    &-name {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
      justify-items: center;
    }

    &-desc {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
    }
  }

  &-body {
    overflow: hidden;
    flex: 1;
  }

  &-footer {
    display: flex;
    flex-direction: column;
    flex-shrink: 0;

    .footer-opts {
      display: flex;
      align-items: center;
    }
  }
}
.footer-clear-popper {
  min-width: 90px;
  .clear-tip {
    text-align: center !important;
  }
}
.message-item-question {
  margin-top: -1.5rem;
  cursor: pointer !important;
  opacity: 0.6;
}
</style>
