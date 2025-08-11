<template>
  <i class="refer-name">{{ referName }} : </i>
</template>

<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  nodes: {},
  name: {},
  nodeData: {}
})
const referName = computed(() => {
  // 如果是旧的条件选择器
  // if (props.nodeData.nodeType == 'precondition') {
  return props.name
  // }
  // const [nodeId, ...rst] = props.name.split('.')
  // const obj = props.nodes.find((it) => it.id == nodeId)
  // return `${obj?.nodeMetaInfo?.nodeName || '_'}.${rst.join('.')}`
})
</script>

<style lang="scss" scoped>
.refer-name {
  color: var(--oz-color-primary);
  // min-width: 100px;
  word-break: break-word;
}
</style>
