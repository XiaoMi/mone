<template>
  <el-select v-model="val" placeholder="请选择知识库" value-key="id">
    <el-option v-for="item in options" :key="item.id" :label="item.tableName" :value="item" />
  </el-select>
</template>

<script setup>
import { getTables } from '@/api/workflow'
import { computed, ref, onMounted } from 'vue'

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
const getApiList = () => {
  getTables({ workspaceId: props.workspaceId }).then((res) => {
    options.value = res.data || []
  })
}
onMounted(() => {
  getApiList()
})
</script>

<style lang="scss" scoped></style>
