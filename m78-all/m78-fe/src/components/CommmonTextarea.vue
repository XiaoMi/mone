<template>
  <div :class="'input-box ' + props.size">
    <el-input
      ref="textarea"
      v-model="inputV"
      :class="'common-input textarea-class ' + props.size"
      type="textarea"
      :autosize="{ minRows: 1, maxRows: 8 }"
      @keyup.enter="handleEnter"
      :placeholder="placeholder"
    >
    </el-input>
    <div class="btn-box">
      <el-tooltip effect="dark" :content="t('commonTextare.enterWithShift')" placement="top">
        <el-button
          link
          :class="`dark-icon${enterWithShiftKey ? '' : ' dark-icon-disabled'}`"
          @click="toggleEnterWithShiftKey"
        >
          shift
        </el-button>
      </el-tooltip>
      <el-tooltip effect="dark" :content="t('commonTextare.sendMsg')" placement="top">
        <el-button
          link
          class="send-btn"
          :class="[inputV.length > 0 ? 'dark-send-icon' : '']"
          @click="sendMsg"
        >
          <i class="iconfont icon-send"></i>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useCommonTextareaStore } from '@/stores/common-textarea'
import { t } from '@/locales'

const commonTextareaStore = useCommonTextareaStore()

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
const enterWithShiftKey = computed(() => {
  return commonTextareaStore.enterWithShiftKey
})

const inputV = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

function handleEnter(event: KeyboardEvent) {
  if (enterWithShiftKey.value) {
    if (event.key === 'Enter' && event.shiftKey) {
      event.preventDefault()
      emits('enterFn', inputV.value)
    }
  } else {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      emits('enterFn', inputV.value)
    }
  }
}

const toggleEnterWithShiftKey = () => {
  commonTextareaStore.setEnterWithEnterShiftKey(!commonTextareaStore.enterWithShiftKey)
}

function sendMsg() {
  if (inputV.value?.trim()) {
    emits('enterFn', inputV.value)
  }
}
</script>

<style lang="scss" scoped>
.icon-send {
  line-height: 20px;
  font-size: 14px;
  color: #fff;
}
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

  :deep(.oz-input__wrapper),
  :deep(.oz-textarea__inner) {
    box-shadow: none;
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

  .send-btn,
  .send-btn:hover {
    padding: 2px 5px;
    background: #e8e8e8;
  }
  .dark-send-icon,
  .dark-send-icon:hover {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
    color: #fff;
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
}

.textarea-class {
  :deep(textarea) {
    resize: none;
  }
}
</style>
