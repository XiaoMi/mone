<template>
  <el-select v-model="val" placeholder="请选择知识库">
    <el-option
      v-for="item in options"
      :key="item.knowledgeBaseId"
      :label="item.knowledgeBaseName"
      :value="item.knowledgeBaseId"
    />
  </el-select>
</template>

<script setup>
import { getKnowledgeList } from '@/api/probot-knowledge'

import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
const props = defineProps({
  modelValue: {},
  workspaceId: {}
})
const options = ref()
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const route = useRoute()
const getApiList = () => {
  getKnowledgeList({ workSpaceId: props.workspaceId, pageSize: 100, pageNum: 1 }).then((res) => {
    options.value = res?.data?.records || []
  })
}
onMounted(() => {
  getApiList()
})
</script>

<style lang="scss" scoped></style>
