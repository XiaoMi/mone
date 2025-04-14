<template>
  <div :class="'input-box ' + props.size">
    <el-input ref="textarea" v-model="inputV" :class="'common-input textarea-class ' + props.size" type="textarea"
      :autosize="{ minRows: 2, maxRows: 8 }" @keyup.enter="handleEnter" :placeholder="placeholder">
    </el-input>
    <div class="btn-box">
      <!-- endter -->
      <el-tooltip effect="dark" content="录音" placement="top">
        <Recoder @submit="submitAudio"></Recoder>
      </el-tooltip>
      <el-tooltip effect="dark" content="发送信息" placement="top">
        <el-button link class="send-btn dark-send-icon" @click="sendMsg">
          <el-icon>
            <Position />
          </el-icon>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Recoder from '@/components/recorder/index.vue'
import { ElMessage } from 'element-plus'
import { speechToText } from '@/api/probot'

const props = defineProps({
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
  },
  size: {
    type: String,
    default: () => {
      return ''
    }
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

function handleEnter(event: KeyboardEvent) {
  if (event.key === 'Enter' && event.shiftKey) {
    event.preventDefault()
    enterUpdate()
  }
}
const enterUpdate = () => {
  if (inputV.value?.trim()) {
    emits('enterFn')
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

</script>

<style lang="scss">
.btn-box {
  .upload-container {
    .oz-upload-list {
      position: absolute;
      bottom: 30px;
      right: 0px;
    }
  }
}
</style>
<style lang="scss" scoped>
.input-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 3px;
  border: 1px solid transparent;
  border-radius: 4px;
  background-clip: padding-box, border-box;
  background-origin: padding-box, border-box;
  background-image: linear-gradient(to right, #fff, #fff),
    linear-gradient(90deg, #2cd497, #56ccda 10%, #5eb5ff 35%, #2468f2 60% 87%);

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    box-shadow: none;

    &:focus {
      border: none;
      box-shadow: none;
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

    .oz-icon {
      font-size: 14px;
    }

    &:hover {
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(180px at -5% -20%, #85d8ce, #c2e9fb);
    }
  }

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

.textarea-class {
  :deep(textarea) {
    resize: none;
  }
}
</style>
