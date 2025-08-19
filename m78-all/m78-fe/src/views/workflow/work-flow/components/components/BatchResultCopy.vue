<template>
  <div class="btns-box">
    <CopyBtn
      :arr="arr"
      :batchNum="batchNum"
      :isBatch="true"
      tips="复制当前批次"
      :nodeData="nodeData"
    />
    <CopyBtn :arr="arr" tips="复制全部批次" :nodeData="nodeData" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import CopyBtn from './CopyBtn'

const props = defineProps({
  arr: {
    default: []
  },
  batchNum: {},
  nodeData: {}
})
</script>

<style lang="scss" scoped>
.btns-box {
  width: calc(100% - 60px);
  padding-left: 5px;
  display: flex;
  justify-content: space-between;
}
</style>
