<template>
  <div class="input-box">
    <el-input
      v-model="inputV"
      class="common-input"
      :type="loading ? 'textarea' : 'text'"
      :autosize="{ minRows: 1, maxRows: 3 }"
      @keyup.enter="inputHandle"
      :autofocus="true"
      :class="loading ? 'textarea-class' : ''"
      :placeholder="t('excle.MessageAIDocAssistant')"
    >
    </el-input>
    <div class="btn-box">
      <el-button link @click="stopFn" :disabled="props.disabled" v-if="loading">
        <i class="iconfont icon-tingzhi"></i>
      </el-button>
      <el-tooltip effect="dark" :content="t('excle.sendMsg')" placement="top" v-else>
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

<script setup>
import { computed } from 'vue'
import { t } from '@/locales'

const props = defineProps({
  modelValue: {},
  loading: {},
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
const inputHandle = () => {
  if (props.loading) {
    return
  } else {
    emits('enterFn', inputV)
  }
}
const sendMsg = () => {
  emits('enterFn', inputV)
}
const stopFn = () => {
  // 加载中 则是发出停止请求的信号
  emits('stopReq')
}
</script>

<style lang="scss" scoped>
.common-input {
  // :deep(.oz-input-group__append),
  // :deep(.oz-input__wrapper:hover) {
  //   background-color: #fff;
  // }
}
.send-icon {
  display: inline-block;
  width: 20px;
  height: 20px;
  margin-left: 0px;
  padding: 4px;
  border-radius: 4px;
}
.center-class {
  display: flex;
  justify-content: center;
  align-items: center;
}
.icon-send {
  line-height: 20px;
  font-size: 14px;
  color: #fff;
}

.icon-tingzhi {
  color: #000;
  cursor: pointer;
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
}
.btn-box {
  .send-btn,
  .send-btn:hover {
    padding: 2px 5px;
    background: #e8e8e8;
  }
  .dark-send-icon,
  .dark-send-icon:hover {
    background: #000;
    color: #fff;
  }
}

.textarea-class {
  :deep(textarea) {
    resize: none;
  }
}
</style>
