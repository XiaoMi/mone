<!--
 * @Description:
 * @Date: 2024-03-13 14:31:08
 * @LastEditTime: 2024-03-14 11:39:34
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
          size="large"
        ></CommmonTextarea>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { executeBot, getPresetQuestion } from '@/api/probot'
import { ElMessage } from 'element-plus'
import BaseIcon from '@/components/BaseIcon.vue'
import { Message } from '@/components/common-message'
import CommmonTextarea from '@/components/CommmonTextarea.vue'
import { useUserStore } from '@/stores'
import { Delete } from '@element-plus/icons-vue'
import { clearDebuggerMessage } from '@/api/probot'

type ScrollElement = HTMLDivElement | null

const props = defineProps<{
  data: Object
  type: String
}>()

const userStore = useUserStore()

const avatar = computed(() => {
  const model = aiModel.value
  if (isMoonshot(model)) {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/kimi.png`
  } else if (model === 'gpt4_1106_2') {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/chatgpt.png`
  } else if (model === 'glm4') {
    return `${import.meta.env.VITE_APP_STATIC_PATH}images/logo/glm4.png`
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
    type?: string
    name?: string
    text: string
    dateTime?: string
    inversion: boolean
    aiModel?: string
    avatar?: string
    error?: boolean
    textType?: string
    loading?: boolean
    showCursor?: boolean
    isShowOperate?: boolean
  }[]
>([])
const isShow = ref(true)
const loading = ref(false)
const placeholder = ref('')
const text = ref('')
const scrollRef = ref<ScrollElement>(null)

const getUuid = () => {
  return `${props.data?.botId}-${userStore.userInfo.username}`
}

const sendText = (propmt: string) => {
  text.value = propmt
  onConversion()
}

const handleEnter = () => {
  onConversion()
}

const scrollToBottom = async () => {
  await nextTick()
  if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
}
const getQi = async (params: any) => {
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
    props.data?.botSetting?.openingQues.forEach((item) => {
      if (item) {
        conversions.value.push({
          type: 'question',
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
    onConversion(true, propmt.text)
  }
}

const onConversion = async (isTry: boolean = false, textStr?: string) => {
  conversions.value = conversions.value.filter((item) => item.type !== 'question')
  conversions.value = conversions.value.map((v) => {
    v.isShowOperate = false
    return v
  })
  const res = {
    text: '',
    textType: '',
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
    if (isTry) {
      propmt = textStr
    } else {
      conversions.value.push({
        text: propmt,
        inversion: true,
        loading: false,
        dateTime: new Date().toLocaleString()
      })
    }
    isShow.value = false
    text.value = ''
    loading.value = true
    conversions.value.push(res)
    await scrollToBottom()
    const params = {
      botId: props.data?.botId,
      input: propmt,
      topicId: getUuid()
    }
    const { code, data, message } = await executeBot(params)
    getQi(params)
    if (code == 0) {
      let resText = data
      let textType = ''
      try {
        const tryJson = JSON.parse(data)
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
        console.error(e)
      }
      if (textType === 'img') {
        res.loading = false
        res.text = resText
        res.textType = textType
      } else {
        res.loading = false
        res.showCursor = true
        for (const c of resText) {
          res.text += c
          conversions.value = [...conversions.value]
          await scrollToBottom()
          await new Promise((resolve) => {
            setTimeout(() => {
              resolve(1)
            }, 0)
          })
        }
        res.showCursor = false
      }
    } else {
      res.loading = false
      res.showCursor = false
      res.text = message || `code=${code}`
      ElMessage.error(message || `code=${code}`)
    }
  } catch (e) {
    console.error(e)
    res.loading = false
    res.showCursor = false
    res.text = `出错了: ${e}`
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
      init(detailData.value)
      ElMessage.success('清除成功')
    } else {
      ElMessage.error(message || '清除失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('清除失败')
  }
}

const init = (data) => {
  conversions.value = []
  isShow.value = true
  if (data?.botSetting?.openingRemarks) {
    conversions.value.push({
      text: data?.botSetting?.openingRemarks,
      avatar: avatar.value,
      aiModel: aiModel.value,
      inversion: false,
      loading: false,
      dateTime: new Date().toLocaleString()
    })
    getQi()
  }
}
onMounted(() => {
  init(props.data)
})

watch(
  () => props.data,
  (val) => {
    detailData.value = val
    init(val)
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.tip {
  margin-bottom: 10px;
  padding: 10px;

  cursor: pointer;
  color: #fff;
  background-color: #409eff;
  border-radius: 4px;
  opacity: 0.6;

  &:hover {
    opacity: 1;
  }
}

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
