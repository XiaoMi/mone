<template>
  <div class="if-box">
    <div class="condition-item">否则</div>
    <Handle
      type="source"
      :position="Position.Right"
      :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
    />
  </div>
</template>

<script setup>
import { Handle, Position } from '@vue-flow/core'
</script>

<style lang="scss" scoped>
.if-box {
  padding: 10px 20px;
  background: #f8f8f8;
  border-radius: 5px;
  width: 580px;
}
.condition-item {
  font-size: 12px;
  font-weight: 600;
}
</style>
