<template>
  <el-button link @click.stop="updateOutIn" :loading="loading" class="update-btn"
    >更新配置</el-button
  >
</template>

<script setup>
import { ref } from 'vue'
const emits = defineEmits(['update'])
const props = defineProps({
  nodeType: {},
  id: {}
})
const loading = ref(false)
import { getBotPluginParameter } from '@/api/probot'
import { getFlowDetail } from '@/api/workflow'

const getDetail = async () => {
  const { code, data } =
    props.nodeType == 'plugin'
      ? await getBotPluginParameter({ id: props.id })
      : await getFlowDetail(props.id)
  if (code == 0) {
    return data
  }
  return
}
const updateOutIn = async () => {
  loading.value = true
  const detailInfo = await getDetail()
  if (detailInfo) {
    emits('update', detailInfo)
  }
  loading.value = false
}
</script>

<style lang="scss" scoped>
.update-btn {
  font-size: 12px;
}
</style>
