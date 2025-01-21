<template>
  <el-input v-model="val" @change="changeInput" />
</template>

<script setup>
import { defineProps, defineEmits, computed, inject } from 'vue'
import { useWfStore } from '@/stores/workflow1'
import { useVueFlow } from '@vue-flow/core'

import {
  getReferOpsByNodesLines,
  getBatchReferByNodesLine,
  initOpsByNodeList,
  classifyNodes
} from '@/views/workflow/work-flow/baseInfo.js'

const wfStore = useWfStore()
const { setNodesPreNodes } = wfStore
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)

const { toObject, updateNode } = useVueFlow()
const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {},
  node: {},
  // 关联的节点是否包含自己，只有大模型的批处理才包含自己
  relSelf: {
    default: false
  },
  check: {
    type: Boolean,
    default: true
  }
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const emtpyRefData = inject('emtpyRefData')

const changeInput = (val) => {
  if (!props.check) {
    return false
  }
  const { affectedNodes, unaffectedNodes } = classifyNodes(
    props.relSelf,
    nodesPreNodes.value,
    props.node.id
  )
  const newArr = [...initRelationNodesPreIds(affectedNodes), ...unaffectedNodes]
  setNodesPreNodes(newArr)
  // 置空绑定值
  emtpyRefData(affectedNodes, props.node.id)
}

const initRelationNodesPreIds = (relations) => {
  const obj = toObject()
  return initOpsByNodeList(relations, obj)
}
</script>

<style lang="scss" scoped></style>
