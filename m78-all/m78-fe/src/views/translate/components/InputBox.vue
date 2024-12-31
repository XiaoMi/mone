<!--
 * @Description:
 * @Date: 2024-01-11 19:54:12
 * @LastEditTime: 2024-03-15 10:56:38
-->
<template>
  <div class="input-box">
    <div class="origin-wrap">
      <div class="origin-input">
        <el-input
          v-model="origin"
          type="textarea"
          :placeholder="
            isRecording
              ? t('translate.originPlaceholderRecording')
              : isRecordingLoading
                ? t('translate.originPlaceholderProcessing')
                : isTranslateLoadingImg
                  ? isTranslateLoadingImg
                  : isTranslateLoadingDoc
                    ? isTranslateLoadingDoc
                    : t('translate.originPlaceholderDefault')
          "
          maxlength="10000"
          show-word-limit
          @input="originInputEvent"
          class="input-element"
          :disabled="
            isRecording ||
            isRecordingLoading ||
            isTranslateLoading ||
            isTranslateProgress ||
            !!isTranslateLoadingImg ||
            !!isTranslateLoadingDoc
          "
        />
      </div>
      <div class="origin-btn">
        <div>
          <div v-if="translationType === 1">
            <!-- 语音 -->
            <el-tooltip :content="t('translate.sound')" v-if="!isRecording">
              <el-button
                class="origin-icon sound-icon"
                @click="startSound"
                :disabled="isRecordingLoading || isTranslateLoading || isTranslateProgress"
                ><Mic
              /></el-button>
            </el-tooltip>
            <!-- 停止语言 -->
            <el-tooltip :content="t('translate.stopSound')" v-else>
              <el-button class="origin-icon luyinzhong-icon" @click="stopSound"
                ><i class="iconfont icon-luyinzhong"></i
              ></el-button>
            </el-tooltip>
          </div>
        </div>
        <div v-if="!isRecording">
          <el-button
            type="primary"
            @click="toTranslate"
            :disabled="
              !origin ||
              isRecordingLoading ||
              isTranslateLoading ||
              isTranslateProgress ||
              !!isTranslateLoadingImg ||
              !!isTranslateLoadingDoc
            "
            >{{
              isRecordingLoading || isTranslateLoading || isTranslateProgress
                ? t('translate.translateBtnTxtLoading')
                : t('translate.translateBtnTxtDefault')
            }}</el-button
          >
        </div>
      </div>
    </div>
    <div class="result-wrap">
      <div class="result-input" v-if="showEdit">
        <el-input
          v-model="result"
          type="textarea"
          placeholder=""
          maxlength="10000"
          show-word-limit
          class="input-element"
        />
      </div>
      <div class="result-content" v-else>
        {{
          isRecording
            ? t('translate.recording')
            : isRecordingLoading
              ? t('translate.resultRecording')
              : isTranslateLoading
                ? t('translate.translateLoading')
                : result
        }}
      </div>
      <div class="result-btn" v-if="!isRecording">
        <BaseSounds
          :disabled="!result || isRecordingLoading || isTranslateLoading || isTranslateProgress"
          :content="result"
          ref="soundsRef"
        ></BaseSounds>
        <div>
          <el-tooltip :content="t('translate.edit')">
            <el-button
              class="result-icon"
              @click="edit"
              :disabled="!result || isRecordingLoading || isTranslateLoading || isTranslateProgress"
              text
              ><i class="iconfont icon-bianji"></i
            ></el-button>
          </el-tooltip>
          <el-tooltip :content="t('common.copy')">
            <el-button
              class="result-icon"
              @click="copy"
              :disabled="!result || isRecordingLoading || isTranslateLoading || isTranslateProgress"
              text
              ><i class="iconfont icon-copy2"></i
            ></el-button>
          </el-tooltip>
          <FavoriteBtn
            :favoriteData="{
              id: uuid,
              favorite: favorite
            }"
            @onSuccess="fetchFavoriteSuccess"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted, defineExpose, watch, nextTick, computed } from 'vue'
import _ from 'lodash'
import mitt from '@/utils/bus'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
import FavoriteBtn from './FavoriteBtn.vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import CryptoJS from 'crypto-js'
import { fetchAudioToText } from '@/api/translate'
import { t } from '@/locales'
import BaseSounds from '@/components/BaseSounds.vue'
import { ElLoading } from 'element-plus'

const props = defineProps({
  modelValue: {},
  rightActive: {
    type: Number,
    required: true
  },
  translationType: {
    type: Number,
    required: true
  },
  currentStyle: {
    type: Array,
    required: true,
    default: []
  },
  currentLanguage: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['update:modelValue', 'updateHistory', 'updateFavorite'])

const origin = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const { toClipboard } = useClipboard()

const iscurrent = ref(true)
// 输入框变化触发检测语言
const originInputEvent = _.debounce(() => {
  mitt.emit('translateType', origin.value)
}, 1000)

const isRecording = ref(false) //是否在录音
const isRecordingLoading = ref(false)
const mediaRecorder = ref()
const chunks = ref([])
const originAudio = ref()
const originAudioSrc = ref()

const isTranslateLoading = ref(false)
const isTranslateProgress = ref(false)
const isTranslateLoadingImg = ref('')
const isTranslateLoadingDoc = ref('')

const showEdit = ref<boolean>(false)
const result = ref<string>('')

const uuid = ref<string>('')
const favorite = ref<boolean>(false)
const flag = ref(false) //停止录音标识
const controller = new AbortController()
const signal = controller.signal
const soundsRef = ref()

//更新table数据
const updateTable = () => {
  if (props.rightActive === 2) {
    emits('updateHistory')
  } else if (props.rightActive === 3) {
    emits('updateFavorite')
  }
}
// 录音
const processRecordedAudio = () => {
  const blob = new Blob(chunks.value, { type: 'audio/wav' })
  originAudioSrc.value = URL.createObjectURL(blob)
}
const handleDataAvailable = (event: any) => {
  chunks.value.push(event?.data)
  if (mediaRecorder.value.state === 'inactive') {
    processRecordedAudio()
  }
}
const handleStop = () => {
  // 如果不需要停止录音操作了，直接返回
  if (flag.value) return
  // const loadingInstance = ElLoading.service({
  //   fullscreen: true,
  //   text: t('translate.originPlaceholderProcessing')
  // })
  isRecordingLoading.value = true
  const blob = new Blob(chunks.value, { type: 'audio/wav' })
  const formData = new FormData()
  formData.append('audioFile', blob, 'recording.wav')
  // todo:requirements会自动转成字符串拼接
  formData.append('requirements', props.currentStyle)
  formData.append('fromLanguage', props.currentLanguage.fromLanguage)
  formData.append('toLanguage', props.currentLanguage.toLanguage)
  formData.append('type', props.translationType)
  fetchAudioToText(formData)
    .then((res) => {
      if (res?.data?.fromText) {
        origin.value = res?.data?.fromText
      }
      if (res?.data?.toText) {
        result.value = res?.data?.toText
      }
      if (res?.data?.uuid) {
        uuid.value = res?.data?.uuid
      }
    })
    .catch(() => {
      result.value = ''
      origin.value = ''
      ElMessage.error(t('translate.recordingError'))
    })
    .finally(() => {
      mitt.emit('translateType', origin.value)
      mitt.emit('translationUuid', uuid.value)
      isRecordingLoading.value = false
      // loadingInstance.close()
      updateTable()
    })
}
// 开始录音
const startSound = () => {
  chunks.value = []
  flag.value = false
  isRecording.value = true
  navigator.mediaDevices
    .getUserMedia({ audio: true })
    .then((stream) => {
      origin.value = ''
      mitt.emit('translateType', origin.value)
      showEdit.value = false
      //停止朗读
      soundsRef.value.stopRead()
      uuid.value = ''
      favorite.value = false
      mediaRecorder.value = new MediaRecorder(stream)
      mediaRecorder.value.addEventListener('dataavailable', handleDataAvailable)
      mediaRecorder.value.addEventListener('stop', handleStop)
      mediaRecorder.value.start()
    })
    .catch((error) => {
      console.error('无法访问麦克风：', error)
    })
}
// 停止录音
const stopSound = () => {
  isRecording.value = false
  mediaRecorder?.value?.stop()
}

// 点击翻译
const toTranslate = () => {
  iscurrent.value = true
  if (origin.value) {
    isTranslateLoading.value = true
    result.value = ''
    //停止朗读
    soundsRef.value.stopRead()
    uuid.value = ''
    favorite.value = false
    fetchEventSource(
      `//${window.location.host + import.meta.env.VITE_GLOB_API_NEW_URL}v1/translate/text/stream`,
      {
        method: 'POST',
        signal: signal,
        headers: {
          'Content-Type': 'application/json'
        },
        //请求体，用于给后台的数据
        body: JSON.stringify({
          requirements: props.currentStyle,
          fromLanguage: props.currentLanguage.fromLanguage,
          toLanguage: props.currentLanguage.toLanguage,
          fromText: origin.value,
          type: props.translationType
        }),
        onmessage(res) {
          // 是否在翻译当前类别的东西
          if (iscurrent.value) {
            console.log('res-----翻译结果', res)
            if (res.event === 'start') {
              result.value = ''
              uuid.value = res?.data
              mitt.emit('translationUuid', uuid.value)
            } else if (res.event === 'end') {
              controller.abort()
              updateTable()
              isTranslateProgress.value = false
            } else {
              result.value += CryptoJS.enc.Base64.parse(res.data).toString(CryptoJS.enc.Utf8)
              console.log('res-----result', result.value)
              isTranslateLoading.value = !result.value
              isTranslateProgress.value = true
            }
          } else {
            controller.abort()
            isTranslateLoading.value = false
            isTranslateProgress.value = false
          }
        },
        onerror(event) {
          // 服务异常
          console.log('服务异常', event)
        },
        onclose() {
          // 服务关闭
          console.log('服务关闭')
        }
      }
    )
  } else {
    ElMessage.error(t('translate.originNoData'))
  }
}

onUnmounted(() => {
  soundsRef.value.stopRead()
  stopSound()
  controller.abort()
})

// 编辑
const edit = () => {
  showEdit.value = !showEdit.value
  soundsRef.value.stopRead()
}

// 复制
const copy = () => {
  if (result.value) {
    toClipboard(result.value)
      .then(() => {
        ElMessage.success(t('common.copySuccess'))
      })
      .catch(() => {
        ElMessage.error(t('common.copyError'))
      })
  } else {
    ElMessage.error(t('translate.copyNoData'))
  }
}

// 收藏
const fetchFavoriteSuccess = () => {
  favorite.value = !favorite.value
  updateTable()
}

defineExpose({
  getOriginValue: () => {
    return origin.value
  },
  getResultValue() {
    return result.value
  },
  changeResult(text) {
    result.value = text
  }
})

watch(
  () => props.translationType,
  (val) => {
    nextTick(() => {
      iscurrent.value = false

      origin.value = ''
      mitt.emit('translateType', origin.value)

      isRecordingLoading.value = false
      isTranslateLoading.value = false
      isTranslateLoadingImg.value = ''
      isTranslateLoadingDoc.value = ''
      //切换类型后停止录音之后origin的操作
      flag.value = true
      stopSound()

      showEdit.value = false
      result.value = ''
      //停止朗读
      soundsRef.value.stopRead()

      uuid.value = ''
      favorite.value = false
    })
  },
  {
    immediate: true,
    deep: true
  }
)

// 监听原始文本框数据
mitt.on('translateData', (data) => {
  if (data.origin) {
    origin.value = data.origin
    if (data.type !== 'process') mitt.emit('translateType', data.origin)
  }
  if (data.result) result.value = data.result
  if (data.uuid) {
    uuid.value = data.uuid
    mitt.emit('translationUuid', uuid.value)
  }
  favorite.value = data.favorite

  isTranslateLoadingImg.value = data.isTranslateLoadingImg
  isTranslateLoadingDoc.value = data.isTranslateLoadingDoc
})
</script>

<style scoped lang="scss">
.input-box {
  display: flex;
  border: 1px solid var(--oz-menu-border-color);
  flex: 1;
  overflow: auto;
}
.origin-wrap {
  width: 50%;
  border-right: 1px solid var(--oz-menu-border-color);
  display: flex;
  flex-direction: column;
  .origin-input {
    flex: 1;
  }
  .origin-btn {
    padding: 6px 20px 10px;
    display: flex;
    justify-content: space-between;
    height: 48px;
    .origin-icon {
      border: none;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      &.sound-icon {
        svg {
          width: 22px;
        }
        .iconfont {
          font-size: 16px;
        }
      }
      &.luyinzhong-icon {
        .iconfont {
          font-size: 16px;
        }
      }
    }
  }
}
.result-wrap {
  width: 50%;
  display: flex;
  flex-direction: column;
  .result-content {
    flex: 1;
    padding: 7px 11px;
    font-size: 14px;
    line-height: 20px;
    overflow: auto;
    white-space: pre-line;
  }
  .result-input {
    flex: 1;
  }
  .result-btn {
    padding: 6px 20px 10px;
    display: flex;
    justify-content: space-between;
    height: 48px;
    .result-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
    .iconfont {
      font-size: 16px;
    }
  }
}
</style>
<style lang="scss">
.origin-wrap .origin-input,
.result-wrap .result-input {
  .oz-textarea {
    height: 100%;
  }
  .oz-textarea__inner {
    height: 100%;
    resize: none;
    box-shadow: none;
  }
}
.origin-wrap .origin-input {
  .oz-input__count {
    bottom: -30px;
    right: 100px;
  }
}
.result-wrap .result-input {
  .oz-input__count {
    bottom: -33px;
    right: 180px;
    width: 100px;
  }
}
</style>
