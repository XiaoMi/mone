<template>
  <div :class="'input-box ' + props.size">
    <slot name="top"></slot>
    <div class="input-box-content">
      <el-input
        ref="textarea"
        v-model="inputV"
        :class="'common-input textarea-class ' + props.size"
        type="textarea"
        :autosize="{ minRows: 1, maxRows: 8 }"
        @keyup.enter="handleEnter"
        :placeholder="placeholder"
        :disabled="disabled"
      >
      </el-input>
      <div class="btn-box">
        <template v-if="props.type !== 'simple'">
          <el-tooltip effect="dark" :content="t('commonTextare.audio')" placement="top">
            <Recoder @submit="submitAudio"></Recoder>
          </el-tooltip>
          <!-- 图片 -->
          <el-tooltip
            effect="dark"
            content="支持上传一个PDF文本或者图片"
            placement="top"
            v-if="showImg"
          >
            <el-upload
              :limit="1"
              class="upload-container"
              ref="upload"
              action="/"
              :auto-upload="false"
              :on-change="beforeAvatarUpload"
              v-model:file-list="fileList"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              list-type="picture"
              :on-exceed="handleExceed"
            >
              <el-icon><Paperclip class="icon-paperclip" /></el-icon>
            </el-upload>
          </el-tooltip>
          <!-- shift -->
          <el-tooltip effect="dark" :content="t('commonTextare.enterWithShift')" placement="top">
            <el-button
              link
              :class="`dark-icon${enterWithShiftKey ? '' : ' dark-icon-disabled'}`"
              @click="toggleEnterWithShiftKey"
            >
              shift
            </el-button>
          </el-tooltip>
        </template>
        <!-- endter -->
        <el-tooltip effect="dark" :content="t('commonTextare.sendMsg')" placement="top">
          <el-button
            link
            class="send-btn"
            :class="[inputV.length > 0 || fileList.length ? 'dark-send-icon' : '']"
            @click="sendMsg"
          >
            <i class="iconfont icon-send"></i>
          </el-button>
        </el-tooltip>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { UploadProps, UploadUserFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'
import Recoder from '@/components/recorder/index.vue'
import { useCommonTextareaStore } from '@/stores/common-textarea'
import { speechToText } from '@/api/translate'
import { filePdfUpload } from '@/api/index'

const commonTextareaStore = useCommonTextareaStore()

const props = defineProps({
  // 大小尺寸
  size: {
    type: String,
    default: () => {
      return ''
    }
  },
  // 简版和正常版 simple/normal
  type: {
    type: String,
    default: 'normal'
  },
  // 是否展示图片上传
  showImg: {
    type: Boolean,
    default: false
  },
  modelValue: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    default: () => {
      return ''
    }
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
const emits = defineEmits(['update:modelValue', 'enterFn', 'stopReq'])
const inputV = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const fileList = ref<UploadUserFile[]>([])
const fileParams = ref({})
const upload = ref(null)

// 是否开启enter+shift是发送
const enterWithShiftKey = computed(() => {
  return commonTextareaStore.enterWithShiftKey
})

const toggleEnterWithShiftKey = () => {
  commonTextareaStore.setEnterWithEnterShiftKey(!commonTextareaStore.enterWithShiftKey)
}

// enter
const enterUpdate = () => {
  if (inputV.value?.trim()) {
    // 图片
    if (fileList.value.length) {
      emits('enterFn', fileParams.value)
      fileList.value = []
      upload.value?.clearFiles()
    } else {
      // 其他情况
      emits('enterFn')
    }
  }
}

function handleEnter(event: KeyboardEvent) {
  if (enterWithShiftKey.value) {
    // 按下enter键+shift键,进行发送
    if (event.key === 'Enter' && event.shiftKey) {
      event.preventDefault()
      enterUpdate()
    }
  } else {
    // enter键直接发送
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      enterUpdate()
    }
  }
}

function sendMsg() {
  enterUpdate()
}

async function submitAudio(url: string, base64Data: string) {
  const res = await speechToText({
    text: base64Data.split('base64,')[1],
    format: 'mp3'
  })
  if (res.code == 0) {
    if (res.data) {
      inputV.value = res.data
      emits('enterFn')
    } else {
      ElMessage.warning('语音转成文本，文本空')
    }
  }
}

const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (file) => {
  console.log(file)
}
const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  ElMessage.warning(`文件限制1个, 你已经选择 ${files.length} 文件`)
}

const beforeAvatarUpload = (file: any) => {
  if (file) {
    const reader = new FileReader()
    const { name, size } = file
    //   文件类型限制
    const fileTypeFlag = /^.png|.jpg|.jpeg|.gif|.webp|.pdf$/.test(
      name.substring(name.lastIndexOf('.')).toLowerCase()
    )
    if (!fileTypeFlag) {
      ElMessage.error('文件类型只能是.png|.jpg|.jpeg|.gif|.webp|.pdf')
      fileList.value = []
      return
    }
    //   文件大小进行校验
    if (size > 1024 * 1024 * 30) {
      ElMessage.error('文件大小不能超过30Mb')
      fileList.value = []
      return
    }

    reader?.readAsDataURL(file.raw)
    reader.onload = function (event) {
      // 获取Base64编码的字符串
      const base64String = event.target.result

      const arr = base64String?.split('base64,')
      const url = URL.createObjectURL(file.raw)
      const fileFlag = /^.pdf$/.test(name.substring(name.lastIndexOf('.')).toLowerCase())
      if (fileFlag) {
        fileList.value = [
          {
            name: file.raw.name,
            url: '/ai-plug/images/pdf.svg'
          }
        ]
        filePdfUpload({
          base64: arr[1],
          isInner: true
        }).then((res) => {
          fileParams.value = {
            multimodal: fileFlag ? 3 : 2,
            mediaType: file.raw.type,
            input: arr[1],
            url: res.data
          }
        })
      } else {
        fileList.value = [
          {
            name: '',
            url
          }
        ]
        fileParams.value = {
          multimodal: fileFlag ? 3 : 2,
          mediaType: file.raw.type,
          input: arr[1],
          url
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.input-box {
  padding-right: 3px;
  display: flex;
  flex-direction: column;
  border: 1px solid transparent;
  border-radius: 4px;
  background-clip: padding-box, border-box;
  background-origin: padding-box, border-box;
  background-image: linear-gradient(to right, #fff, #fff),
    linear-gradient(90deg, #2cd497, #56ccda 10%, #5eb5ff 35%, #2468f2 60% 87%);
  :deep(.oz-input__wrapper),
  :deep(.oz-textarea__inner) {
    box-shadow: none;
  }
  &-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .textarea-class {
    :deep(textarea) {
      resize: none;
    }
  }
  &.large {
    .textarea-class {
      :deep(.oz-textarea__inner) {
        min-height: 40px !important;
        height: 40px !important;
        line-height: 30px;
      }
    }
    .btn-box {
      margin-right: 8px;
    }
  }
}
.btn-box {
  display: flex;
  height: 100%;
  // 图片
  .upload-container {
    display: flex;
    align-items: center;
    margin-right: 12px;
    border-radius: 4px;
    color: #fff;
    padding: 2px 5px;
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #85d8ce, #c2e9fb);
    position: relative;
    :deep(.oz-upload-list) {
      position: absolute;
      bottom: 30px;
      right: 0px;
    }
    .oz-icon {
      font-size: 14px;
    }
    &:hover {
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(180px at -5% -20%, #85d8ce, #c2e9fb);
    }
    .icon-paperclip {
      transform: scaleX(-1) rotate(-45deg);
      display: inline-block; /* 确保变换生效 */
    }
  }
  // shift
  .dark-icon {
    color: #fff;
    padding: 2px 5px;
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
    opacity: 0.8;

    &:hover {
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
      opacity: 1;
    }
  }

  .dark-icon-disabled {
    background: #e8e8e8;
  }

  .oz-button.dark-icon.is-link:not(.is-disabled):focus {
    color: #fff;
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
  }

  .oz-button.dark-icon-disabled.is-link:not(.is-disabled):focus {
    background: #e8e8e8;
  }
  // 发送
  .send-btn,
  .send-btn:hover {
    padding: 2px 5px;
    background: #e8e8e8;
  }
  .icon-send {
    line-height: 20px;
    font-size: 14px;
    color: #fff;
  }
  .dark-send-icon,
  .dark-send-icon:hover {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
    color: #fff;
  }
}
</style>
