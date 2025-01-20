<template>
  <div class="condition-box">
    <div class="title">
      <h4 class="title-text">条件分支</h4>
      <el-button link @click="addElse" type="primary">+ 新增分支</el-button>
    </div>
  </div>
</template>

<script setup>
import { useVueFlow } from '@vue-flow/core'
import { computed } from 'vue'
import { addConditionFn } from '../../common/if-else.js'

const { toObject, addNodes, updateNode } = useVueFlow()
const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  disabled: {},
  workspaceId: {},
  referOps: {},
  referBatchOps: {}
})

const emits = defineEmits(['update:modelValue'])
const flowNode = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const addElse = () => {
  const { nodes } = toObject()
  addConditionFn({
    parentNode: flowNode.value,
    nodes,
    updateNode,
    addNodes
  })
}
</script>

<style lang="scss" scoped>
.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}
.title-text {
  font-size: 12px;
}
</style>
