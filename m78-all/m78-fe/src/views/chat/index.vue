<script setup lang="ts">
import type { Ref } from 'vue'
import { computed, onMounted, onUnmounted, ref } from 'vue'
//@ts-ignore
import CryptoJS from 'crypto-js'
import html2canvas from 'html2canvas'
import { Delete, Download, Memo } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'element-plus/theme-chalk/src/message-box.scss'
import { Message } from '@/components/common-message'
import { useScroll } from './hooks/useScroll'
import { useChat } from './hooks/useChat'
import { useUsingContext } from './hooks/useUsingContext'
import { useChatStore } from '@/stores/chat'
import { useUserStore } from '@/stores/user'
import { fetchChatAPIProcess } from '@/api'
import CommmonTextarea from '@/components/CommmonTextarea.vue'
import { t } from '@/locales'

let controller = new AbortController()

const chatStore = useChatStore()
const userStore = useUserStore()

const isMobile = ref<boolean>(false)
const { addChat, updateChatMessage, updateChat, updateChatSome, getChatByUuidAndIndex } = useChat()
const { scrollRef, scrollToBottom, scrollToBottomIfAtBottom } = useScroll()
const { usingContext, toggleUsingContext } = useUsingContext()
const { updateHomeInput } = chatStore

const uuid = computed(() => chatStore.active || 0)
const history = computed(() => chatStore.history)

const dataSources = computed(() => chatStore.getChatByUuid(+uuid.value))
const conversationList = computed(() => dataSources.value.filter((item) => !item.inversion))

const prompt = ref<string>('')
const loading = ref<boolean>(false)
const inputRef = ref<Ref | null>(null)

// 未知原因刷新页面，loading 状态不会重置，手动重置
dataSources.value.forEach((item, index) => {
  if (item.loading) updateChatSome(+uuid.value, index, { loading: false })
})

function handleSubmit() {
  onConversation()
}

async function onConversation() {
  const message = prompt.value

  if (loading.value) return

  if (!message || message.trim() === '') return

  // 没有数据先创建
  if (!history.value || history.value.length == 0) {
    const {code} = await chatStore.addHistory({
      title: message
    })
    if (code != 0) {
      return
    }
  }

  controller = new AbortController()

  const userChat = {
    id: 0,
    dateTime: new Date().toLocaleString(),
    text: message,
    inversion: true,
    error: false,
    requestOptions: { prompt: message, chatContext: null }
  }
  addChat(+uuid.value, userChat)
  updateChatMessage(+uuid.value, dataSources.value.length - 1, userChat, 'USER')
  scrollToBottom()

  loading.value = true
  prompt.value = ''

  const chatContext: Chat.ChatContext[] = []
  // 使用context，ps: 加入10条对话内容
  if (usingContext.value) {
    const len = conversationList.value.length || 0
    for (let i = 1; len - i >= 0 && i <= 5; i++) {
      const conversation = conversationList.value[len - i]
      chatContext.unshift({
        content: conversation.text,
        role: 'ASSISTANT'
      })
      chatContext.unshift({
        content: conversation.requestOptions.prompt,
        role: 'USER'
      })
    }
  }
  let answerChat: Chat.Chat = {
    id: 0,
    dateTime: new Date().toLocaleString(),
    text: '',
    loading: true,
    inversion: false,
    error: false,
    requestOptions: { prompt: message, chatContext }
  }
  addChat(+uuid.value, answerChat)
  scrollToBottom()

  try {
    const fetchChatAPIOnce = async () => {
      const events: { target: any }[] = []
      let allText = ''
      let isHandle = false
      let isEnd = false
      const chatPosition = dataSources.value.length - 1
      return new Promise((resolve, reject) => {
        fetchChatAPIProcess<Chat.ConversationResponse>({
          chatContextMsg: chatContext,
          prompt: message,
          topicId: String(uuid.value),
          zToken: userStore.userInfo.ztoken,
          signal: controller.signal,
          onDownloadProgress: async ({ event }) => {
            events.push(event)
            if (isHandle) return
            isHandle = true
            for (;;) {
              const e = events.shift()
              if (!e) {
                isHandle = false
                return
              }
              const xhr = e.target
              const { responseText } = xhr
              const chunks = responseText.split('\n')
              let text = ''
              let rawText = ''
              for (let chunk of chunks) {
                chunk = chunk.substring(5)
                if (chunk) {
                  rawText += chunk
                  const index = rawText.indexOf(':end')
                  if (index == -1 && rawText.length > 42) {
                    try {
                      text += CryptoJS.enc.Base64.parse(chunk).toString(CryptoJS.enc.Utf8)
                      if (text.length > allText.length) {
                        const newText = text.replace(allText, '')
                        for (const char of newText) {
                          allText += char
                          answerChat = {
                            id: 0,
                            dateTime: new Date().toLocaleString(),
                            text: allText,
                            inversion: false,
                            error: false,
                            showCursor: true,
                            loading: false,
                            requestOptions: { prompt: message, chatContext }
                          }
                          updateChat(+uuid.value, chatPosition, answerChat)
                          scrollToBottomIfAtBottom()
                          await new Promise((resolve) => {
                            setTimeout(() => {
                              resolve(1)
                            }, 60)
                          })
                        }
                      }
                    } catch (error) {
                      //
                      console.error(error)
                    }
                  } else if (index > 0 && isEnd == false) {
                    isEnd = true
                    answerChat = {
                      id: 0,
                      dateTime: new Date().toLocaleString(),
                      text: allText,
                      inversion: false,
                      error: false,
                      loading: false,
                      requestOptions: { prompt: message, chatContext }
                    }
                    updateChat(+uuid.value, chatPosition, answerChat)
                    updateChatMessage(+uuid.value, chatPosition, answerChat, 'ASSISTANT')
                    updateChatSome(+uuid.value, chatPosition, { loading: false })
                    resolve(allText)
                  }
                } else {
                  continue
                }
              }
            }
          }
        }).then(
          () => {},
          (e) => {
            reject(e)
          }
        )
      })
    }
    await fetchChatAPIOnce()
  } catch (error: any) {
    const errorMessage = error?.message ?? t('common.wrong')

    if (error.message === 'canceled') {
      updateChatSome(+uuid.value, dataSources.value.length - 1, {
        loading: false
      })
      scrollToBottomIfAtBottom()
      return
    }

    const currentChat = getChatByUuidAndIndex(+uuid.value, dataSources.value.length - 1)

    if (currentChat?.text && currentChat.text !== '') {
      updateChatSome(+uuid.value, dataSources.value.length - 1, {
        text: `${currentChat.text}\n[${errorMessage}]`,
        error: false,
        loading: false
      })
      return
    }

    const answerChat = {
      id: 0,
      dateTime: new Date().toLocaleString(),
      text: errorMessage,
      inversion: false,
      error: true,
      loading: false,
      requestOptions: { prompt: message, chatContext }
    }
    updateChat(+uuid.value, dataSources.value.length - 1, answerChat)
    updateChatMessage(+uuid.value, dataSources.value.length - 1, answerChat, 'ASSISTANT')
    scrollToBottomIfAtBottom()
  } finally {
    loading.value = false
  }
}

async function onRegenerate(index: number) {
  if (loading.value) return

  controller = new AbortController()

  const { requestOptions } = dataSources.value[index]

  const message = requestOptions?.prompt ?? ''

  let chatContext: Chat.ChatContext[] = []

  if (requestOptions.chatContext) chatContext = [...requestOptions.chatContext]

  loading.value = true

  let answerChat: Chat.Chat = {
    id: 0,
    dateTime: new Date().toLocaleString(),
    text: '',
    loading: true,
    inversion: false,
    error: false,
    requestOptions: { prompt: message, chatContext }
  }

  updateChat(+uuid.value, index, answerChat)

  try {
    const fetchChatAPIOnce = async () => {
      const events: { target: any }[] = []
      let allText = ''
      let isHandle = false
      let isEnd = false
      const chatPosition = index
      return new Promise((resolve, reject) => {
        fetchChatAPIProcess<Chat.ConversationResponse>({
          chatContextMsg: chatContext,
          prompt: message,
          topicId: String(uuid.value),
          zToken: userStore.userInfo.ztoken,
          signal: controller.signal,
          onDownloadProgress: async ({ event }) => {
            events.push(event)
            if (isHandle) return
            isHandle = true
            for (;;) {
              const e = events.shift()
              if (!e) {
                isHandle = false
                return
              }
              const xhr = e.target
              const { responseText } = xhr
              const chunks = responseText.split('\n')
              let text = ''
              let rawText = ''
              for (let chunk of chunks) {
                chunk = chunk.substring(5)
                if (chunk) {
                  rawText += chunk
                  const index = rawText.indexOf(':end')
                  if (index == -1 && rawText.length > 42) {
                    try {
                      text += CryptoJS.enc.Base64.parse(chunk).toString(CryptoJS.enc.Utf8)
                      if (text.length > allText.length) {
                        const newText = text.replace(allText, '')
                        for (const char of newText) {
                          allText += char
                          answerChat = {
                            id: 0,
                            dateTime: new Date().toLocaleString(),
                            text: allText,
                            inversion: false,
                            error: false,
                            showCursor: true,
                            loading: false,
                            requestOptions: { prompt: message, chatContext }
                          }
                          updateChat(+uuid.value, chatPosition, answerChat)
                          scrollToBottomIfAtBottom()
                          await new Promise((resolve) => {
                            setTimeout(() => {
                              resolve(1)
                            }, 60)
                          })
                        }
                      }
                    } catch (error) {
                      //
                      console.error(error)
                    }
                  } else if (index > 0 && isEnd == false) {
                    isEnd = true
                    answerChat = {
                      id: 0,
                      dateTime: new Date().toLocaleString(),
                      text: allText,
                      inversion: false,
                      error: false,
                      loading: false,
                      requestOptions: { prompt: message, chatContext }
                    }
                    updateChat(+uuid.value, chatPosition, answerChat)
                    updateChatMessage(+uuid.value, chatPosition, answerChat, 'ASSISTANT')
                    updateChatSome(+uuid.value, chatPosition, { loading: false })
                    resolve(allText)
                  }
                } else {
                  continue
                }
              }
            }
          }
        }).then(
          () => {},
          (e) => {
            reject(e)
          }
        )
      })
    }
    await fetchChatAPIOnce()
  } catch (error: any) {
    if (error.message === 'canceled') {
      updateChatSome(+uuid.value, index, {
        loading: false
      })
      return
    }

    const errorMessage = error?.message ?? t('common.wrong')

    updateChat(+uuid.value, index, {
      id: 0,
      dateTime: new Date().toLocaleString(),
      text: errorMessage,
      inversion: false,
      error: true,
      loading: false,
      conversationOptions: null,
      requestOptions: { prompt: message, chatContext }
    })
  } finally {
    loading.value = false
  }
}

function handleExport() {
  if (loading.value) return
  ElMessageBox.confirm(t('chat.exportImageConfirm'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('chat.exportImage')
  }).then(async () => {
    try {
      const ele = document.getElementById('image-wrapper')
      const canvas = await html2canvas(ele as HTMLDivElement, {
        useCORS: true
      })
      const imgUrl = canvas.toDataURL('image/png')
      const tempLink = document.createElement('a')
      tempLink.style.display = 'none'
      tempLink.href = imgUrl
      tempLink.setAttribute('download', 'chat-shot.png')
      if (typeof tempLink.download === 'undefined') tempLink.setAttribute('target', '_blank')

      document.body.appendChild(tempLink)
      tempLink.click()
      document.body.removeChild(tempLink)
      window.URL.revokeObjectURL(imgUrl)
      ElMessage.success(t('chat.exportSuccess'))
      Promise.resolve()
    } catch (error: any) {
      ElMessage.error(t('chat.exportFailed'))
    } finally {
      // d.loading = false
    }
  })
}

function handleDelete(index: number) {
  if (loading.value) return
  ElMessageBox.confirm(t('chat.deleteMessageConfirm'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('chat.deleteMessage')
  }).then(() => {
    chatStore.deleteChatByUuid(+uuid.value, index)
  })
}

function handleClear() {
  if (loading.value) return
  ElMessageBox.confirm(t('chat.clearChatConfirm'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('chat.clearChat')
  }).then(() => {
    chatStore.clearChatByUuid(+uuid.value)
  })
}

function handleStop() {
  if (loading.value) {
    controller.abort()
    loading.value = false
  }
}

const placeholder = computed(() => {
  if (isMobile.value) return t('chat.placeholderMobile')
  return t('chat.placeholder')
})

const footerClass = computed(() => {
  let classes = ['p-4']
  if (isMobile.value)
    classes = ['sticky', 'left-0', 'bottom-0', 'right-0', 'p-2', 'pr-3', 'overflow-hidden']
  return classes
})

onMounted(() => {
  chatStore.initHistory().then(async () => {
    const homeInput = chatStore.homeInput
    if (homeInput) {
      prompt.value = decodeURIComponent(homeInput)
      await chatStore.addHistory({ title: homeInput })
      updateHomeInput('')
      handleSubmit()
    } else {
      scrollToBottom()
      inputRef.value?.$refs?.textarea?.focus()
    }
  })
})

onUnmounted(() => {
  if (loading.value) controller.abort()
})
</script>

<template>
  <div style="overflow: hidden">
    <div class="flex flex-col w-full h-full">
      <main class="flex-1 overflow-hidden">
        <div id="scrollRef" ref="scrollRef" class="h-full overflow-hidden overflow-y-auto">
          <div
            id="image-wrapper"
            class="w-full max-w-screen-xl m-auto"
            :class="[isMobile ? 'p-2' : 'p-4']"
          >
            <template v-if="!dataSources.length">
              <div class="flex items-center justify-center mt-4 text-center text-neutral-300">
                <el-empty :description="t('chat.placeholderMobile')" />
              </div>
            </template>
            <template v-else>
              <div>
                <Message
                  v-for="(item, index) of dataSources"
                  :key="index"
                  :date-time="item.dateTime"
                  :text="item.text"
                  :inversion="item.inversion"
                  :error="item.error"
                  :loading="item.loading"
                  :show-cursor="item.showCursor"
                  @regenerate="onRegenerate(index)"
                  @delete="handleDelete(index)"
                />
                <div class="sticky bottom-0 left-0 flex justify-center">
                  <el-button v-if="loading" type="warning" @click="handleStop">
                    停止请求
                  </el-button>
                </div>
              </div>
            </template>
          </div>
        </div>
      </main>
      <footer :class="footerClass">
        <div class="w-full max-w-screen-xl m-auto">
          <div class="flex items-center justify-between space-x-2">
            <el-popover title="" :content="t('chat.clearChat')">
              <template #reference>
                <el-button @click="handleClear" :icon="Delete" circle></el-button>
              </template>
            </el-popover>
            <el-popover title="" :content="t('chat.exportImage')">
              <template #reference>
                <el-button @click="handleExport" :icon="Download" circle />
              </template>
            </el-popover>
            <el-popover
              title=""
              :content="usingContext ? t('chat.turnOnContext') : t('chat.turnOffContext')"
            >
              <template #reference>
                <el-button v-if="usingContext" @click="toggleUsingContext" :icon="Memo" circle />
                <el-button v-else type="danger" @click="toggleUsingContext" :icon="Memo" circle />
              </template>
            </el-popover>
            <CommmonTextarea
              ref="inputRef"
              v-model="prompt"
              class="flex-1"
              @enterFn="handleSubmit"
              :placeholder="placeholder"
            ></CommmonTextarea>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>
