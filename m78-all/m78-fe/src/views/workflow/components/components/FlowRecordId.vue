<template>
  <i v-if="flowRecordIdStatus" class="record-id" @click="copyFn">
    {{ keyName }}:{{ flowRecordIdStatus }}
    <el-icon class="copy-icon"><CopyDocument /></el-icon>
  </i>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'

const props = defineProps({
  flowRecordIdStatus: {},
  keyName: {
    default: 'flowRecordId'
  }
})
const copyFn = async () => {
  const { toClipboard } = useClipboard()
  try {
    await toClipboard(props.flowRecordIdStatus)
    ElMessage.success('已复制到剪贴板！')
  } catch (e) {
    ElMessage.warning('您的浏览器不支持复制：', e)
  }
}
</script>

<style scoped>
.record-id {
  font-size: 12px;
  display: flex;
  align-items: center;
  background-color: #f0f0f5;
  border-radius: 4px;
  color: rgba(29, 28, 35, 0.6);
  padding: 0 6px;
  height: 20px;
  line-height: 20px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
  margin-left: 10px;
  cursor: pointer;
}
.copy-icon {
  margin-left: 2px;
}
</style>
