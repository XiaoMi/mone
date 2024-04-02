<template>
  <div>
    <component
      :is="dom[nodeData?.nodeType]"
      v-model="nodeData"
      :nodes="nodes"
      ref="sonRef"
      :disabled="disabled"
    />
  </div>
</template>

<script setup>
import { useNode } from '@vue-flow/core'
import { onMounted, ref, shallowReactive } from 'vue'
import NodeIf from './components/NodeIf'
import NodeElse from './components/NodeElse'

const props = defineProps({
  nodes: {},
  disabled: {}
})
const dom = shallowReactive({
  nodeif: NodeIf,
  nodeelse: NodeElse
})
const nodeData = ref({})
const initNodeData = () => {
  const nodeD = useNode()
  nodeData.value = nodeD.node
}
onMounted(() => {
  initNodeData()
})
const sonRef = ref(null)
const validate = async () => {
  try {
    return await sonRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped></style>
