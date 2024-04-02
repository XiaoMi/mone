<template>
  <div class="if-box">
    <div class="condition-item">
      <div class="top">
        <span class="t">否则</span>
      </div>
    </div>
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
  padding: 5px 10px;
  background: #f8f8f8;
  border-radius: 5px;
  width: 580px;
}
.condition-item {
  padding: 5px 10px;
  border-radius: 5px;
}
.condition-item + .condition-item {
  margin-top: 10px;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.t {
  font-size: 12px;
  font-weight: 600;
  padding: 5px 0;
}
.condition-form .if-item {
  display: flex;
  .empty-label {
    padding-top: 0;
  }
}
.icon-plus1 {
  font-size: 12px;
}
.label-span {
  display: inline-block;
  width: 60px;
  padding: 0 5px;
  color: #939395;
}
.empty-form-item {
  padding-top: 23px;
}
</style>
