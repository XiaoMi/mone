<!--
 * @Description:
 * @Date: 2024-03-13 14:31:08
 * @LastEditTime: 2024-12-06 18:23:48
-->
<template>
  <div class="panel">
    <div v-if="isShow" class="panel-header">
      <div class="panel-header-icon">
        <BaseIcon size="large" :index="props.data?.botInfo?.avatarUrl"></BaseIcon>
      </div>
      <div class="panel-header-name">
        <h3>{{ props.data?.botInfo?.name }}</h3>
      </div>
      <div class="panel-header-desc">
        <span>{{ props.data?.botInfo?.remark }}</span>
      </div>
    </div>
    <div class="panel-body">
      <div id="scrollRef" ref="scrollRef" style="overflow: auto; height: 100%">
        <Message
          v-for="(item, index) of conversions"
          :avatarUrl="props.data?.botInfo?.avatarUrl"
          :avatar="
            index === conversions.length - 1 && isMoonshot(item.aiModel)
              ? lastMoonshotAvatar
              : item.avatar
          "
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
          :voice-setting="voiceSetting"
          :class="[{ 'message-item-question': item.type === 'question' }]"
          @click="item.type === 'question' ? sendText(item.text) : null"
          @onTryAgain="tryAgain"
          :flowData="item.flowData"
          :language="props.data?.botSetting?.timbre"
          :multimodal="item.multimodal"
        />
      </div>
    </div>
    <div class="panel-footer">
      <div class="footer-opts">
        <el-popover width="90px" popper-class="footer-clear-popper">
          <div class="clear-tip">清空会话</div>
          <template #reference>
            <el-button
              style="margin-right: 10px"
              @click="handleClear"
              :icon="Delete"
              circle
            ></el-button>
          </template>
        </el-popover>
        <CommmonTextarea
          ref="inputRef"
          v-model="text"
          class="flex-1"
          :placeholder="placeholder"
          @enterFn="handleEnter"
          :disabled="loading"
          :showImg="showImg"
          size="large"
        ></CommmonTextarea>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onBeforeUnmount, onUnmounted, watch } from 'vue'
import { executeBot, getPresetQuestion, clearDebuggerMessage } from '@/api/probot'
import { getChatMessageList } from '@/api/chat'
import BaseIcon from '@/components/BaseIcon.vue'
import { Message } from '@/components/common-message'
import CommmonTextarea from '@/components/CommmonTextarea.vue'
import { useUserStore } from '@/stores'
import { Delete } from '@element-plus/icons-vue'
import SockJS from 'sockjs-client'
import CryptoJS from 'crypto-js'
import dateFormat from 'dateformat'
import { useRoute } from 'vue-router'
import mitt from '@/utils/bus'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Action } from 'element-plus'

const route = useRoute()
type ScrollElement = HTMLDivElement | null

const props = withDefaults(
  defineProps<{
    data: Object
    type: String
    topicId: String
    showImg: Boolean
    isShowHeader: boolean
  }>(),
  {
    isShowHeader: true
  }
)

const userStore = useUserStore()

let sock: SockJS | null = null
let params: any | null = null
let lastConversionRes: any | null = null

const _isBotStream = 'BOT_STREAM'
const _botStreamBegin = 'BOT_STREAM_BEGIN'
const _botStreamResult = 'BOT_STREAM_RESULT'
const _botStreamEvent = 'BOT_STREAM_EVENT'

const avatar = computed(() => {
  const model = aiModel.value
  if (isMoonshot(model)) {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/kimi.png`
  } else if (model === 'gpt4_1106_2') {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/chatgpt.png`
  } else if (model === 'glm4') {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/glm4.png`
  } else if (model === 'claude3') {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/claude3.png`
  }
  return ''
})

const voiceSetting = computed(() => {
  return {
    open: !!props.data?.botSetting?.timbreSwitch,
    language: props.data?.botSetting?.timbre
  }
})

const lastMoonshotAvatar = computed(() => {
  return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/kimi1.png`
})

const aiModel = computed(() => {
  const setting = props.data?.botSetting
  if (setting && setting?.aiModel) {
    return setting?.aiModel
  }
  return ''
})

const isMoonshot = (model?: string) => {
  if (model?.startsWith('moonshot')) {
    return true
  }
  return false
}

const detailData = ref(props.data)

const conversions = ref<
  {
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
  }[]
>([])
const isShow = ref(props.isShowHeader)
const loading = ref(false)
const placeholder = ref('')
const text = ref('')
const scrollRef = ref<ScrollElement>(null)
const flag = ref(false)

const getUuid = () => {
  return props.topicId
  // return `${props.data?.botId}-${userStore.userInfo.username}`
}

const sendText = (propmt: string) => {
  text.value = propmt
  onConversion()
}

const handleEnter = (params?: {
  multimodal: number
  mediaType?: string
  input?: string
  url?: string
}) => {
  onConversion(params)
}

const scrollToBottom = async () => {
  await nextTick()
  if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
}
const getQi = async (params?: any) => {
  if (params) {
    const data = {
      type: 'question',
      text: '',
      loading: true,
      inversion: false
    }
    conversions.value.push(data)
    getPresetQuestion(params)
      .then((res) => {
        conversions.value = conversions.value.filter((item) => item.type !== 'question')
        if (res.data.contents) {
          res.data.contents.forEach((item: { question: string }) => {
            if (item) {
              conversions.value.push({
                type: 'question',
                msgType: 'question',
                text: item.question,
                inversion: false
              })
            }
          })
        }
      })
      .catch(() => {
        conversions.value = conversions.value.filter((item) => item.type !== 'question')
      })
      .finally(() => {})
  } else {
    //init用预置问题
    props.data?.botSetting?.openingQues.forEach((item: any) => {
      if (item) {
        conversions.value.push({
          type: 'question',
          msgType: 'question',
          text: item,
          inversion: false
        })
      }
    })
  }
  await scrollToBottom()
}

const tryAgain = () => {
  let propmt = conversions.value[conversions.value.length - 2]
  if (propmt.inversion) {
    conversions.value = conversions.value.splice(0, conversions.value.length - 1)
    onConversion({
      isTry: true,
      textStr: propmt.text
    })
  }
}

const onConversion = async (opts?: {
  isTry?: boolean
  textStr?: string
  multimodal?: number
  mediaType?: string
  input?: string
  url?: string
}) => {
  conversions.value = conversions.value.filter((item) => item.type !== 'question')
  conversions.value = conversions.value.map((v) => {
    v.isShowOperate = false
    return v
  })
  lastConversionRes = {
    text: '',
    textType: '',
    msgType: 'ASSISTANT',
    inversion: false,
    avatar: avatar.value,
    aiModel: aiModel.value,
    loading: true,
    dateTime: new Date().toLocaleString(),
    showCursor: false,
    isShowOperate: true
  }
  try {
    if (loading.value) return
    let propmt = text.value
    if (opts?.isTry) {
      propmt = opts?.textStr || ''
    } else {
      if (opts?.multimodal === 2 || opts?.multimodal === 3) {
        conversions.value.push({
          textType: opts?.multimodal === 2 ? 'img' : 'pdf',
          text: opts.url || '',
          msgType: 'USER',
          inversion: true,
          loading: false,
          dateTime: new Date().toLocaleString()
        })
      }
      propmt &&
        conversions.value.push({
          text: propmt,
          msgType: 'USER',
          inversion: true,
          loading: false,
          dateTime: new Date().toLocaleString()
        })
    }
    isShow.value = false
    text.value = ''
    loading.value = true
    // 倒数第二项
    const lastItem = conversions.value[conversions.value.length - 2]
    // 上一条数据是否是机器人返回的提示信息， 如果是则需要增加参数
    const newObj = {
      message: propmt,
      flowRecordId: flowRecordId.value,
      msgType: 'answer'
    }
    const inputObj = flowRecordId.value ? { input: JSON.stringify(newObj) } : { input: propmt }
    const msgTypeObj = flowRecordId.value ? { msgType: 'answer' } : {}
    let imgObj = {}
    if (opts?.multimodal === 2 || opts?.multimodal === 3) {
      imgObj = {
        multimodal: opts.multimodal,
        mediaType: opts.mediaType,
        input: opts.input,
        postscript: inputObj.input
      }
      lastConversionRes = {
        ...lastConversionRes,
        ...imgObj
      }
    }
    params = {
      multimodal: 1,
      botId: props.data?.botId,
      topicId: getUuid(),
      history: (conversions.value || [])
        .filter((it) => it.msgType === 'USER' || it.msgType === 'ASSISTANT')
        .slice(0, -1)
        .map((item) => {
          return {
            role: item.msgType, // item.inversion ? 'USER' : 'ASSISTANT',
            content: item.text
          }
        }),
      ...inputObj,
      ...msgTypeObj,
      ...imgObj,
      token: userStore.userInfo.ztoken
    }
    if (userStore.userInfo.ztoken) {
      sendMessage(params)
      conversions.value.push(lastConversionRes)
      await scrollToBottom()
    } else {
      ElMessageBox.alert('没有TOKEN', '提示', {
        confirmButtonText: '去创建',
        callback: async (action: Action) => {
          window.open(import.meta.env.VITE_APP_Z_DOMAIN + '/z/info', '_blank')
          sendMessage(params)
          conversions.value.push(lastConversionRes)
          await scrollToBottom()
        }
      })
    }
  } catch (e) {
    console.log(e)
    lastConversionRes.loading = false
    lastConversionRes.showCursor = false
    lastConversionRes.text = `出错了: ${e}`
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const handleClear = async () => {
  try {
    const { code, message } = await clearDebuggerMessage({
      botId: props.data?.botId,
      topicId: getUuid()
    })
    if (code === 0) {
      init(detailData.value, '', true)
      ElMessage.success('清除成功')
    } else {
      ElMessage.error(message || '清除失败')
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('清除失败')
  }
}
const getMsgList = async (topicId: string) => {
  if (topicId) {
    const { code, data } = await getChatMessageList({
      topicId: Number(topicId)
    })
    if (code == 0 && Array.isArray(data)) {
      const chatList = data.map((it) => {
        let textType = ''
        if (it?.message?.includes('/download?')) {
          textType = 'img'
        } else {
          try {
            if (it?.message?.includes('bar_chart')) {
              textType = 'chartColumnar'
            }
          } catch (error) {}
        }
        let item = {
          textType,
          text: it?.message,
          inversion: it.messageRole === 'USER' ? true : false,
          loading: false,
          dateTime: dateFormat(it.ctime, 'yyyy-mm-dd HH:MM:ss')
        }
        if (it?.meta?.messageType === 'BOT_RESULT') {
          item = {
            ...item,
            ...msgUpdateLastConversion(JSON.parse(it?.message))
          }
        }
        return item
      })
      conversions.value.unshift(...chatList)
    }
  }
}
const init = async (data: Object, topicId?: string, noHandleEnter) => {
  conversions.value = []
  // isShow.value = true
  await getMsgList(topicId)
  if (data?.botSetting?.openingRemarks) {
    conversions.value.push({
      text: data?.botSetting?.openingRemarks,
      msgType: 'openingRemarks',
      avatar: avatar.value,
      aiModel: aiModel.value,
      inversion: false,
      loading: false,
      dateTime: new Date().toLocaleString()
    })
    await getQi()
  }
  flag.value = false
  if (route.query?.input && !noHandleEnter) {
    handleEnter()
  }
}
const msgUpdateLastConversion = ({ code, data }: { code: any; data: any }) => {
  let res = {
    text: ''
  }
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
      }
    } catch (e) {
      console.log(e)
    }
    if (textType === 'img') {
      res.loading = false
      res.text = resText
      res.textType = textType
    } else {
      res.loading = false
      res.showCursor = true
      if (resText) {
        for (const c of resText) {
          res.text += c
        }
      }
      res.showCursor = false
    }
  }
  return res
}
const updateLastConversion = async ({
  code,
  data,
  message
}: {
  code: any
  data: any
  message: any
}) => {
  getQi(params)
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
      let num = 0
      if (resText) {
        for (const c of resText) {
          res.text += c
          num++
          if (num == resText.length) {
            res.showCursor = false
          }
          conversions.value = [...conversions.value]
          await scrollToBottom()
          await new Promise((resolve) => {
            setTimeout(() => {
              resolve(1)
            }, 0)
          })
        }
      } else {
        res.text = ''
        res.showCursor = false
        conversions.value = [...conversions.value]
        await scrollToBottom()
        await new Promise((resolve) => {
          setTimeout(() => {
            resolve(1)
          }, 0)
        })
      }
    }
  } else {
    res.loading = false
    res.showCursor = false
    res.text = message || `code=${code}`
    ElMessage.error(message || `code=${code}`)
  }
}

const sendMessage = async (params) => {
  if (sock) {
    sock?.send(JSON.stringify(params))
  } else {
    const { code, data, message } = await executeBot(params)
    updateLastConversion({ code, data, message })
  }
}
// 如果有flow正在执行，则flowRecordId为true
const flowRecordId = ref(null)

const updateLastConversionFlow = (obj) => {
  const newFlowRecordId = obj.flowRecordId
  const hasUpdated = conversions.value.find(
    (item) => item?.flowData?.flowRecordId == newFlowRecordId
  )
  const res = hasUpdated ? hasUpdated : lastConversionRes
  res.flowData = obj
  // 已结束，则清空
  if (obj.end == true) {
    flowRecordId.value = null
    // 用户手动取消
    if (obj.endFlowStatus == 4) {
      const { flowId } = obj
      // 找到conversions.value中的flowData中flowId  == flowId的那一项
      const toChange = conversions.value.find((it) => it.flowData?.flowId == flowId)
      toChange.loading = false
      toChange.text = '当前回复已被手动取消'
    }
  } else {
    flowRecordId.value = obj.flowRecordId
  }
  conversions.value = [...conversions.value]
}

const updateLastConversionMsg = async (obj) => {
  const res = lastConversionRes
  if (obj.meta?.message.includes('type') && obj.meta?.message.includes('table')) {
    res.textType = 'table'
    res.text = res.text + '---table---' + obj.meta?.message
  } else if (obj.meta?.message.includes('<zxw开始>')) {
    res.textType = 'button'
    res.text = res.text + obj.meta?.message
  } else {
    res.text = res.text + obj.meta?.message
  }
  res.loading = false
  res.isBotMessage = true
  conversions.value = [...conversions.value]
  await scrollToBottom()
}

const updateStreamLastConversion = async (text: string) => {
  const res = lastConversionRes
  res.text = res.text + text
  res.loading = false
  res.isBotMessage = true
  conversions.value = [...conversions.value]
  await scrollToBottom()
}

const initWS = () => {
  sock = new SockJS(
    `${window.location.origin}${import.meta.env.VITE_GLOB_API_NEW_URL}ws/sockjs/bot/execute`
  )

  sock.onopen = function () {
    console.log('WebSocket is open now.')
  }

  sock.onmessage = function (event: { data: string }) {
    const message = event.data
    const msgObj = JSON.parse(message)
    //  工作流
    if ((msgObj.messageType ?? '').startsWith(_isBotStream)) {
      const messageType = msgObj.messageType
      if (_botStreamBegin == messageType) {
        // 开始目前不做处理
        const res = lastConversionRes
        res.text = ''
        res.loading = false
        res.isBotMessage = true
        conversions.value = [...conversions.value]
      } else if (_botStreamEvent == messageType) {
        // 连续处理
        const text = msgObj.message
          ? CryptoJS.enc.Base64.parse(msgObj.message).toString(CryptoJS.enc.Utf8)
          : ''
        updateStreamLastConversion(text)
      } else if (_botStreamResult == messageType) {
        // 结束, 如果有message，在末尾在展示
        if (msgObj.message) {
          const conversionRes = { ...lastConversionRes }
          if (typeof msgObj.message == 'string') {
            conversionRes.text = msgObj.message
          } else {
            let text = JSON.stringify(msgObj.message)
            if (msgObj.message?.display) {
              const key = msgObj.message?.display
              text =
                typeof msgObj.message[key] == 'string'
                  ? msgObj.message[key]
                  : JSON.stringify(msgObj.message[key])
            }
            conversionRes.text = text
          }
          conversions.value = [...conversions.value, conversionRes]
        } else {
          const res = lastConversionRes
          res.loading = false
          res.isBotMessage = true
          conversions.value = [...conversions.value]
        }
      }
    } else if (msgObj.messageType == 'FLOW_EXECUTE_STATUS') {
      updateLastConversionFlow(msgObj)
      // 机器人消息
    } else if (msgObj.messageType == 'FLOW_EXECUTE_MESSAGE') {
      updateLastConversionMsg(msgObj)
    } // messageType == BOT_RESULT
    else if (msgObj.messageType == 'BOT_RESULT') {
      updateLastConversion(msgObj)
    } else if (msgObj.messageType == 'BOT_STATE_RESULT') {
      const conversionRes = { ...lastConversionRes }
      conversionRes.text = msgObj.data
      conversionRes.loading = false
      conversionRes.isBotMessage = true
      conversions.value = [...conversions.value, conversionRes]
    }
    // ANSWER_RESULT 不处理
  }
  sock.onclose = function () {
    console.log('WebSocket is closed now.')
    sock?.close()
    sock = null
  }

  sock.onerror = function (event: any) {
    console.log(event)
    sock = null
  }
}

function releaseResource() {
  // 在这里执行一些清理工作
  console.log('在这里执行一些清理工作')
  sock?.close()
}

onMounted(() => {
  initWS()
  window.addEventListener('beforeunload', releaseResource)
  mitt.on('DebuggerPageDataClick', async (value?: string) => {
    text.value = value || ''
    handleEnter()
  })
})

onUnmounted(() => {
  mitt.off('DebuggerPageDataClick')
  window.removeEventListener('beforeunload', releaseResource)
})

onBeforeUnmount(() => {
  sock?.close()
})

watch(
  () => [props.data, props.topicId],
  ([val, topicIdValue]) => {
    if (val && topicIdValue && !flag.value) {
      flag.value = true
      detailData.value = val
      text.value = route.query?.input || ''
      init(val, topicIdValue)
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.panel {
  display: flex;
  flex-direction: column;
  height: 100%;

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
