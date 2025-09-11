<template>
  <div class="op-box">
    <div class="lines"></div>
    <div class="text">{{ conditionOps.find((it) => it.name == opVal)?.desc || opVal }}</div>
    <div class="lines"></div>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'
import { useWfStore } from '@/stores/workflow1'

const wfStore = useWfStore()
console.log('wfStore', wfStore)
const conditionOps = computed(() => wfStore.conditionOps)
const props = defineProps({
  opVal: {}
})
</script>

<style lang="scss" scoped>
.op-box {
  height: inherit;
  width: 83px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.text {
  display: inline-block;
  background: #fff;
  border: 1px solid rgba(29, 28, 35, 0.16);
  border-radius: 8px;
  color: #1d1c23;
  font-size: 14px;
  line-height: 20px;
  padding: 6px;
  text-align: center;
}
.lines {
  background-color: rgba(29, 28, 35, 0.16);
  flex: 1 1;
  min-height: 8px;
  width: 1px;
}
</style>
