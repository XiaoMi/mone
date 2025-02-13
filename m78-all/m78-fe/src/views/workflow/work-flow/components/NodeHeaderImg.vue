<template>
  <FlowImg
    v-if="nodeData.nodeType == 'subFlow'"
    :imgUrl="nodeData.data.avatarUrl"
    :imgWidth="16"
    class="flow-node-header"
  />
  <img v-else :src="nodesBase[nodeData.nodeType]?.imgSrc" alt="" class="header" />
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { nodesBase } from '../baseInfo.js'
import FlowImg from '../../components/components/FlowImg.vue'

const props = defineProps({
  nodeData: {}
})
</script>

<style scoped lang="scss">
.header {
  width: 16px;
  height: 16px;
}
.flow-node-header {
  margin-right: 0;
  border-radius: 2px;
  :deep(.iconfont) {
    font-size: 12px;
  }
}
</style>
