<!--
 * @Description: 
 * @Date: 2024-03-15 16:23:36
 * @LastEditTime: 2024-03-15 16:37:01
-->
<template>
  <el-dialog v-model="dialogFormVisible" title="AI 生成" width="500" @open="open">
    <el-form
      :model="form"
      v-loading="contentLoading"
      element-loading-text="正在为你生成中，请稍等..."
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
import { aiRecordNotes } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()
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
  aiRecordNotes({
    botId: probotStore.createdRobtId
  })
    .then((res) => {
      contentLoading.value = false
      form.prompt = res?.data
    })
    .finally(() => {
      loading.value = false
    })
}
const use = () => {
  emits('use', form.prompt)
  dialogFormVisible.value = false
}
</script>

<style scoped lang="scss"></style>
