<!--
 * @Description: 
 * @Date: 2024-03-05 15:02:30
 * @LastEditTime: 2024-03-15 16:36:36
-->
<template>
  <el-dialog
    v-model="dialogFormVisible"
    title="AI 优化"
    width="500"
    :show-close="false"
    @open="open"
  >
    <template #header="{ close, titleId, titleClass }">
      <div class="optimize-header">
        <h4 :id="titleId" :class="titleClass">AI 优化</h4>
        <el-link
          :underline="false"
          type="primary"
          color="#40a3ff"
          class="retry-btn"
          @click="retry"
          :disabled="loading"
          ><el-icon><Refresh /></el-icon><span>重试</span></el-link
        >
      </div>
    </template>
    <el-form
      :model="form"
      v-loading="contentLoading"
      element-loading-text="正在为你优化中，请稍等..."
    >
      <el-form-item label="" :label-width="formLabelWidth">
        <el-input v-model="form.prompt" type="textarea" :autosize="{ minRows: 10, maxRows: 20 }" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="use" :disabled="loading"> 使用 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { ElButton, ElDialog } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { promptTurbo } from '@/api/probot'
import CryptoJS from 'crypto-js'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: String,
    default: ''
  }
})
const emits = defineEmits(['update:modelValue', 'use'])

const dialogFormVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const formLabelWidth = '0px'

const form = reactive({
  prompt: ''
})
const loading = ref(false)
const contentLoading = ref(false)

const open = () => {
  form.prompt = ''
  loading.value = true
  contentLoading.value = true
  const controller = new AbortController()
  const signal = controller.signal
  promptTurbo(
    {
      prompt: props.data
    },
    signal,
    ({ event }) => {
      const xhr = event.target
      const { responseText } = xhr
      const chunks = responseText.split('\n')
      let text = ''
      const start = chunks.indexOf('event:start')
      if (start > -1) {
        chunks.splice(start, 2)
      }
      const end = chunks.indexOf('event:end')
      if (end > -1) {
        chunks.splice(end, 2)
        loading.value = false
      }
      for (let chunk of chunks) {
        if (chunk.includes('data:')) {
          chunk = chunk.substring(5)
          if (chunk) {
            try {
              contentLoading.value = false
              text += CryptoJS.enc.Base64.parse(chunk).toString(CryptoJS.enc.Utf8)
              form.prompt = text
            } catch (error) {
              console.error(error)
            }
          }
        } else {
          continue
        }
      }
    }
  ).finally(() => {
    loading.value = false
  })
}
const retry = () => {
  open()
}
const use = () => {
  emits('use', form.prompt)
  dialogFormVisible.value = false
}
</script>

<style scoped lang="scss">
.optimize-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  .retry-btn {
    span {
      padding-left: 4px;
    }
  }
}
</style>
