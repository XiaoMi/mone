<template>
  <i v-if="flowRecordIdStatus" class="record-id" @click="jumpFn">
    {{ keyName }}:{{ flowRecordIdStatus }}
    <el-icon class="copy-icon"><DArrowRight /></el-icon>
  </i>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  flowRecordIdStatus: {},
  keyName: {
    default: 'flowRecordId'
  },
  flowId: {},
  hasPermission: {}
})
const route = useRoute()
const router = useRouter()
const getNewUrlWithFlowRecordId = (id) => {
  // 获取当前路由的完整路径和查询参数
  const currentFullPath = window.location.origin + route.fullPath
  const { name, query, params } = route
  // 生成新的 URL
  const newUrl = new URL(
    router.resolve({
      name,
      query: {
        ...query,
        flowRecordId: id
      },
      params: {
        ...params,
        id: props.flowId
      }
    }).href,
    currentFullPath
  ).toString()

  return newUrl
}
const jumpFn = async () => {
  if (!props.hasPermission) {
    return
  }
  const url = getNewUrlWithFlowRecordId(props.flowRecordIdStatus)
  window.open(url, '_blank')
}
</script>

<style scoped>
.record-id {
  border: solid 1px var(--oz-color-primary-light-7);
  font-size: 12px;
  display: flex;
  align-items: center;
  border-radius: 4px;
  color: var(--oz-color-primary);
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
  background: #edecfc;
}
.copy-icon {
  margin-left: 2px;
}
</style>
