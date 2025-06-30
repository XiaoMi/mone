<template>
  <div class="probot-index">
    <div class="flex flex-col w-full h-full">
      <main class="flex-1 overflow-hidden">
        <div
          id="scrollRef"
          ref="scrollRef"
          class="h-full overflow-hidden overflow-y-auto probot-content"
        >
          <div
            id="image-wrapper"
            class="w-full max-w-screen-xl m-auto"
            :class="[isMobile ? 'p-2' : 'p-4']"
          >
            <Message :text="helloValue" :inversion="false" />
            <Message
              v-for="(item, index) of dataSources"
              :key="index"
              :date-time="item.dateTime"
              :text="item.text"
              :textType="item.textType"
              :inversion="item.inversion"
              :error="item.error"
              :loading="item.loading"
              :show-cursor="item.showCursor"
            />
            <div class="sticky bottom-0 left-0 flex justify-center">
              <el-button v-if="loading" type="warning" @click="handleStop"> 停止请求 </el-button>
            </div>
          </div>
        </div>
      </main>
      <footer :class="footerClass">
        <div class="w-full max-w-screen-xl m-auto chat-footer-container">
          <FootBtnInput
            v-if="showProbotCreate"
            v-model="showProbotCreate"
            @update="
              (val) => {
                handleSubmit(val, 'quick')
              }
            "
            :data="{
              title: '创建Bot',
              desc: '你想创建什么样的Bot？',
              tooltip: '描述你想创建 Bot 的功能、服务的用户',
              isClose: true
            }"
          ></FootBtnInput>
          <FootBtnInput
            v-else-if="showUseIssues"
            v-model="showUseIssues"
            @update="
              (val) => {
                handleSubmit(val, 'issues')
              }
            "
            :data="{
              title: '使用问题',
              desc: '你想咨询什么问题？',
              tooltip: '请描述你在使用M78过程中遇到的问题'
            }"
          ></FootBtnInput>
          <FootBtnInput
            v-else-if="showUseBot"
            v-model="showUseBot"
            @update="
              (val) => {
                handleSubmit(val, 'bot')
              }
            "
          >
            <template #top
              ><div>
                <BaseInfo
                  :data="{
                    name: '@' + currentBotInfo.botInfo.name || '----',
                    avatarUrl: currentBotInfo.botInfo.avatarUrl || '10'
                  }"
                  size="mini"
                >
                  <template #before>正在于</template>
                  <span>对话</span>
                </BaseInfo>
              </div></template
            ></FootBtnInput
          >
          <template v-else>
            <div class="btn-container">
              <el-button type="primary" size="large" @click="create"
                ><b>+</b><span>创建PROBOT</span></el-button
              >
              <el-button type="primary" size="large" @click="useAnswer" plain
                ><b></b><span>使用答疑</span></el-button
              >
            </div>
            <BotChat @botClick="botClick" v-if="showBotChat"></BotChat>
            <CommmonTextarea
              ref="inputRef"
              v-model="prompt"
              class="flex-1 probot-textarea"
              @enterFn="handleSubmit"
              :placeholder="placeholder"
            >
              <div class="flex items-center justify-between space-x-2 input-bottom-content">
                <ModeSelect v-model="modeSelectValue"></ModeSelect>
                <div>
                  <el-popover title="" :content="t('chat.clearChat')">
                    <template #reference>
                      <el-button @click="handleClear" :icon="Delete" link></el-button>
                    </template>
                  </el-popover>
                  <el-popover title="" :content="t('chat.exportImage')">
                    <template #reference>
                      <el-button @click="handleExport" :icon="Download" link />
                    </template>
                  </el-popover>
                  <el-popover
                    title=""
                    :content="usingContext ? t('chat.turnOnContext') : t('chat.turnOffContext')"
                  >
                    <template #reference>
                      <el-button
                        v-if="usingContext"
                        @click="toggleUsingContext"
                        :icon="Memo"
                        link
                      />
                      <el-button
                        v-else
                        type="danger"
                        @click="toggleUsingContext"
                        :icon="Memo"
                        link
                      />
                    </template>
                  </el-popover>
                </div>
              </div>
            </CommmonTextarea>
          </template>
        </div>
      </footer>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { Ref } from 'vue'
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
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
import { useProbotStore } from '@/stores/probot'
import FootBtnInput from './components/FootBtnInput.vue'
import ModeSelect from './components/ModeSelect.vue'
import BotChat from './components/BotChat.vue'
import { createByOneSentence } from '@/api/probot-index'
import { getBotDetail, executeBot, getTopicId } from '@/api/probot'
import BaseInfo from '@/components/BaseInfo.vue'

const helloValue = `你好，欢迎来到AI PROBOT~\n
AI PROBOT是一个低门槛、快速便捷的智能agent创作平台。无需编写复杂代码或拥有专业技能，轻松编排各种组件，创造出个性化、多媒体互动的AI PROBOT，并一键发布到飞书、微信服务号等渠道。\n
• 输入“@”，直接与你收藏的 Bot 对话\n
• 选择对话框上的【创建Probot】，一句话快速创建Bot\n
• 选择对话框上的【使用答疑】，解决你在平台使用过程中遇到的问题\n
很高兴与你交流任何话题，欢迎随时来找我！`
const probotStore = useProbotStore()
const workspaceList = computed(() => probotStore.workspaceList)
const showProbotCreate = ref(false)
const showUseIssues = ref(false)
const showUseBot = ref(false)
const showBotChat = ref(false)
const currentBotInfo = ref({})
const modeSelectValue = ref('')

const create = () => {
  showProbotCreate.value = true
}
const useAnswer = () => {
  showUseIssues.value = true
}

const botClick = (item: {}) => {
  currentBotInfo.value = item
  showBotChat.value = false
  showUseBot.value = true
}

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

function handleSubmit(val, type) {
  onConversation(val, type)
}

let lastConversionRes: any | null = null
const updateLastConversion = async (
  {
    code,
    data,
    message
  }: {
    code: any
    data: any
    message: any
  },
  callback
) => {
  lastConversionRes = {
    text: '',
    textType: '',
    msgType: 'ASSISTANT',
    inversion: false,
    loading: true,
    dateTime: new Date().toLocaleString(),
    showCursor: false,
    isShowOperate: true
  }
  const res = lastConversionRes
  if (code == 0) {
    let resText = data
    let textType = ''
    try {
      const tryJson = JSON.parse(data)
      if (!tryJson) return
      const { type, display, call_plugin: callPlugin, data: src } = tryJson
      if (type === 'llm') {
        if (typeof tryJson.content !== 'string') {
          resText = JSON.stringify(tryJson.content)
        } else {
          resText = tryJson.content
        }
      } else if (type === 'plugin') {
        if (callPlugin === '图片生成' && src) {
          resText = src
          textType = 'img'
        } else if (display) {
          const tres = tryJson[display]
          if (typeof tres === 'string') {
            resText = tres
          } else {
            resText = JSON.stringify(tres)
          }
        }
      } else if (tryJson?.type === 'bar_chart') {
        textType = 'chartColumnar'
      }
    } catch (e) {
      console.log(e)
    }
    console.log('textType3333', textType)
    if (textType === 'img') {
      res.loading = false
      res.text = resText
      res.textType = textType
    } else if (textType.includes('chart')) {
      res.loading = false
      res.text = resText
      res.textType = textType
    } else {
      res.loading = false
      res.showCursor = true
      for (const c of resText) {
        res.text += c
        dataSources.value = [...dataSources.value]
        await scrollToBottom()
        await new Promise((resolve) => {
          setTimeout(() => {
            resolve(1)
          }, 0)
        })
      }
      res.showCursor = false
    }
    callback(res.text)
  } else {
    res.loading = false
    res.showCursor = false
    res.text = message || `code=${code}`
    ElMessage.error(message || `code=${code}`)
  }
}
// 如果有flow正在执行，则flowRecordId为true
const flowRecordId = ref(null)

async function onConversation(val, type) {
  let message = prompt.value
  if (type === 'quick' || type === 'issues' || type === 'bot') {
    message = val
  }

  if (loading.value) return

  if (!message || message.trim() === '') return

  // 没有数据先创建
  if (!history.value || history.value.length == 0) {
    const { code } = await chatStore.addHistory({
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
        if (type === 'quick') {
          return createByOneSentence({
            sentence: val,
            workspaceId: workspaceList.value[0]?.id
          }).then((res) => {
            if (res.data) {
              return getBotDetail({
                botId: res.data
              }).then(async (res) => {
                if (res.code === 0) {
                  isHandle = true
                  isEnd = true
                  const botTxt = res.data
                  answerChat = {
                    id: 0,
                    dateTime: new Date().toLocaleString(),
                    text: JSON.stringify(botTxt),
                    textType: 'Bot',
                    inversion: false,
                    error: false,
                    loading: false,
                    requestOptions: { prompt: message, chatContext }
                  }
                  updateChat(+uuid.value, chatPosition, answerChat)
                  updateChatMessage(+uuid.value, chatPosition, answerChat, 'ASSISTANT')
                  updateChatSome(+uuid.value, chatPosition, { loading: false })
                  await new Promise((resolve) => {
                    setTimeout(() => {
                      resolve(1)
                      loading.value = false
                    }, 60)
                  })
                } else {
                  ElMessage.error(res?.message)
                }
              })
            }
          })
        } else if (type === 'issues' || type === 'bot') {
          const botId = type === 'issues' ? import.meta.env.VITE_BOTID : currentBotInfo.value.botId
          return getTopicId({ botId: botId, topicType: '1', createIfNotExist: 'true' }).then(
            (res) => {
              // 上一条数据是否是机器人返回的提示信息， 如果是则需要增加参数
              const newObj = {
                message: message,
                flowRecordId: flowRecordId.value,
                msgType: 'answer'
              }
              const inputObj = flowRecordId.value
                ? { input: JSON.stringify(newObj) }
                : { input: message }
              const msgTypeObj = flowRecordId.value ? { msgType: 'answer' } : {}
              const params = {
                multimodal: 1, //multimodal，1是文本，2是图片
                botId: botId,
                topicId: res?.data ? res?.data[0]?.id : '',
                history: (dataSources.value || [])
                  .filter((it) => it.msgType === 'USER' || it.msgType === 'ASSISTANT')
                  .slice(0, -1)
                  .map((item) => {
                    return {
                      role: item.msgType, // item.inversion ? 'USER' : 'ASSISTANT',
                      content: item.text
                    }
                  }),
                ...inputObj,
                ...msgTypeObj
              }
              executeBot(params).then(({ code, data, message }) => {
                updateLastConversion({ code, data, message }, async (text) => {
                  isHandle = true
                  isEnd = true
                  answerChat = {
                    id: 0,
                    dateTime: new Date().toLocaleString(),
                    text: text,
                    inversion: false,
                    error: false,
                    loading: false,
                    requestOptions: { prompt: message, chatContext }
                  }
                  updateChat(+uuid.value, chatPosition, answerChat)
                  updateChatMessage(+uuid.value, chatPosition, answerChat, 'ASSISTANT')
                  updateChatSome(+uuid.value, chatPosition, { loading: false })
                  await new Promise((resolve) => {
                    setTimeout(() => {
                      resolve(1)
                      loading.value = false
                    }, 60)
                  })
                })
              })
            },
            (e) => {
              reject(e)
            }
          )
        } else {
          fetchChatAPIProcess<Chat.ConversationResponse>({
            chatContextMsg: chatContext,
            prompt: message,
            topicId: String(uuid.value),
            zToken: userStore.userInfo.ztoken,
            signal: controller.signal,
            model: modeSelectValue.value, //大模型
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
              console.log('e', e)
              reject(e)
            }
          )
        }
      })
    }
    await fetchChatAPIOnce()
  } catch (error: any) {
    console.log('error', error)
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

watch(
  () => prompt.value,
  (newVal) => {
    if (newVal === '@') {
      showBotChat.value = true
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.probot-index {
  height: 100%;
  overflow: hidden;
  width: 800px;
  margin: 0 auto;
  .probot-content {
    &::-webkit-scrollbar {
      display: none;
    }
  }
  .btn-container {
    padding: 10px 0px;
  }
  .probot-textarea {
    :deep(.oz-textarea__inner) {
      min-height: 50px !important;
    }
  }
  .input-bottom-content {
    border-top: 1px solid rgba(0, 0, 0, 0.05);
  }
  .chat-footer-container {
    position: relative;
    padding: 0 40px;
  }
}
</style>
