<template>
  <NodeBox :hasPermission="props.hasPermission" :nodeType="nodeData?.nodeType">
    <component
      :is="dom[nodeData?.nodeType]"
      v-model="nodeData"
      ref="sonRef"
      :disabled="disabled"
      :getDetailed="getDetailed"
      :referOps="referOpsObj?.ops || []"
      :nodes="nodes"
    />
  </NodeBox>
</template>

<script setup>
import { useNode } from '@vue-flow/core'
import { onMounted, ref, shallowReactive, computed } from 'vue'
import NodeIf from './components/NodeIf'
import NodeElse from './components/NodeElse'
import { useWfStore } from '@/stores/workflow1'
import NodeBox from '@/views/workflow/work-flow/components/NodeBox.vue'

const props = defineProps({
  nodes: {},
  disabled: {},
  getDetailed: {},
  hasPermission: {}
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

const wfStore = useWfStore()
const referOpsObj = computed(() => {
  const cur = wfStore.nodesPreNodes.find((item) => item.nodeId == nodeData.value.parentNode)
  return cur
})
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
