<template>
  <div :class="['input-box', props.size]">
    <slot name="top"></slot>
    <div class="input-box-content">
      <el-input
        ref="textarea"
        v-model="inputV"
        :class="['common-input', 'textarea-class', props.size]"
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
            :content="t('commonTextare.img')"
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
            >
              <el-icon><Picture /></el-icon>
            </el-upload>
          </el-tooltip>
          <!-- shift -->
          <el-tooltip effect="dark" :content="t('commonTextare.enterWithShift')" placement="top">
            <el-button
              link
              :class="['shift-btn', enterWithShiftKey ? 'active' : '']"
              @click="toggleEnterWithShiftKey"
            >
              shift
            </el-button>
          </el-tooltip>
        </template>
        <!-- enter -->
        <el-tooltip effect="dark" :content="t('commonTextare.sendMsg')" placement="top">
          <el-button
            link
            :class="[
              'send-btn',
              inputV.length > 0 || fileList.length ? 'active' : ''
            ]"
            @click="sendMsg"
          >
            <el-icon><Position /></el-icon>
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
import { Picture, Operation, Position } from '@element-plus/icons-vue'
import { t } from '@/locales/index'
import Recoder from '@/components/recorder/index.vue'
import { useCommonTextareaStore } from '@/stores/common-textarea/index'

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
  // 待定
  console.log(url, base64Data)
}

const handleRemove: UploadProps['onRemove'] = (uploadFile: any, uploadFiles: any) => {
  console.log(uploadFile, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (file: any) => {
  console.log(file)
}

const beforeAvatarUpload = (file: any) => {
  if (file) {
    const reader = new FileReader()
    const { name, size } = file
    //   文件类型限制
    const fileTypeFlag = /^.png|.jpg|.jpeg|.gif|.webp$/.test(
      name.substring(name.lastIndexOf('.')).toLowerCase()
    )
    if (!fileTypeFlag) {
      ElMessage.error('文件类型只能是.png|.jpg|.jpeg|.gif|.webp')
      return
    }
    //   文件大小进行校验
    if (size > 1024 * 1024 * 100) {
      ElMessage.error('文件大小不能超过100Mb')
      return
    }
    reader?.readAsDataURL(file.raw)
    reader.onload = function (event) {
      // 获取Base64编码的字符串
      const base64String = event.target.result

      const arr = base64String?.split('base64,')
      const url = URL.createObjectURL(file.raw)
      fileList.value = [
        {
          name: '',
          url
        }
      ]
      fileParams.value = {
        multimodal: 2,
        mediaType: file.raw.type,
        input: arr[1],
        url
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
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  background-color: var(--el-bg-color);
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--el-border-color-hover);
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    box-shadow: none;
    background-color: var(--el-bg-color);
    color: var(--el-text-color-primary);
  }

  flex: 1;

  &-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .textarea-class {
    :deep(textarea) {
      resize: none;
      background-color: var(--el-bg-color);
      color: var(--el-text-color-primary);

      &::placeholder {
        color: var(--el-text-color-placeholder);
      }
    }
  }

  &.large {
    .textarea-class {
      :deep(.el-textarea__inner) {
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
  gap: 8px;
  padding: 0 8px;

  // 图片上传
  .upload-container {
    display: flex;
    align-items: center;
    border-radius: 4px;
    padding: 4px 8px;
    color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
    transition: all 0.3s ease;

    :deep(.el-upload-list) {
      position: absolute;
      bottom: 30px;
      right: 0px;
    }

    .el-icon {
      font-size: 14px;
    }

    &:hover {
      background-color: var(--el-color-primary-light-8);
    }
  }

  // shift 按钮
  .shift-btn {
    padding: 4px 8px;
    color: var(--el-text-color-regular);
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      background-color: var(--el-fill-color);
    }

    &.active {
      color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);

      &:hover {
        background-color: var(--el-color-primary-light-8);
      }
    }
  }

  // 发送按钮
  .send-btn {
    padding: 4px 8px;
    color: var(--el-text-color-regular);
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      background-color: var(--el-fill-color);
    }

    &.active {
      color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);

      &:hover {
        background-color: var(--el-color-primary-light-8);
      }
    }
  }
}

:deep(.el-button.is-link:focus) {
  color: inherit;
}

// 暗色主题适配
html.dark {
  .input-box {
    border-color: var(--el-border-color-darker);
    background-color: var(--el-bg-color-overlay);

    &:hover {
      border-color: var(--el-border-color);
    }

    :deep(.el-input__wrapper),
    :deep(.el-textarea__inner) {
      background-color: var(--el-bg-color-overlay);
    }
  }

  .btn-box {
    .upload-container {
      background-color: var(--el-color-primary-dark-2);
      &:hover {
        background-color: var(--el-color-primary-dark-1);
      }
    }

    .shift-btn,
    .send-btn {
      background-color: var(--el-fill-color-darker);

      &:hover {
        background-color: var(--el-fill-color-dark);
      }

      &.active {
        color: var(--el-color-primary-light-3);
        background-color: var(--el-color-primary-dark-2);

        &:hover {
          background-color: var(--el-color-primary-dark-1);
        }
      }
    }
  }
}
</style>
