<template>
  <el-drawer
    v-model="showEditor"
    title="代码"
    direction="rtl"
    append-to-body
    class="code-mirror-item"
    size="50%"
  >
    <template #header>
      <div>
        <div class="t-box">
          <div class="t-logo">
            <img :src="CodeImg" class="t-img" />
            <span class="t-text">代码</span>
          </div>
          <div class="t-logo">
            <span class="label">语言</span>
            <ProbotCodeLanguage v-model="cmOptions.mode" style="width: 150px"></ProbotCodeLanguage>
          </div>
        </div>
      </div>
    </template>
    <!-- <Codemirror v-model:value="codeVal" :options="cmOptions" border class="code-mirror-item" /> -->
    <ProbotCodemirror v-model="codeVal"></ProbotCodemirror>
  </el-drawer>
</template>

<script setup>
import Codemirror from 'codemirror-editor-vue3'
// import 'codemirror/theme/erlang-dark.css'
import 'codemirror/theme/blackboard.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/python/python.js'
import { computed, ref } from 'vue'
import CodeImg from '@/views/workflow/imgs/icon-Code.png'
import ProbotCodeLanguage from '@/components/ProbotCodeLanguage'
import ProbotCodemirror from '@/components/ProbotCodemirror'

const emits = defineEmits(['update:modelValue', 'update:code'])
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  code: {}
})
const showEditor = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})

const codeVal = computed({
  get() {
    return props.code
  },
  set(val) {
    emits('update:code', val)
  }
})

const cmOptions = ref({
  lineNumbers: false, // 显示行号
  theme: 'blackboard',
  mode: 'text/groovy',
  autofocus: true
})
</script>

<style lang="scss" scoped>
.t-img {
  width: 18px;
  height: 18px;
}
.t-box {
  width: 290px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.t-text {
  font-weight: 600;
  font-size: 15px;
  margin-left: 5px;
}
.t-logo {
  display: flex;
  align-items: center;
}
.label {
  margin-right: 5px;
}
.code-mirror-item {
  :deep(.CodeMirror) {
    font-size: 16px;
    line-height: 150%;
  }
  // :deep {
  //   .CodeMirror-foldgutter {
  //     display: none;
  //   }
  // }
}
</style>
