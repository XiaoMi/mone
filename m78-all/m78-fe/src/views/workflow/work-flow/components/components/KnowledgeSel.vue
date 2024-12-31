<template>
  <el-select v-model="val" placeholder="请选择知识库">
    <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id" />
  </el-select>
</template>

<script setup>
import { getKnowledgeMyList } from '@/api/chat'

import { computed, ref, onMounted } from 'vue'
const props = defineProps({
  modelValue: {}
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
const getApiList = () => {
  getKnowledgeMyList().then((res) => {
    options.value = res.data || []
  })
}
onMounted(() => {
  getApiList()
})
</script>

<style lang="scss" scoped></style>
