<template>
    <el-tag size="small" class="tag-btn" :type="type">
      <slot />
    </el-tag>
  </template>
  
  <script setup>
  const props = defineProps({
    type: {}
  })
  </script>
  
  <style lang="scss" scoped>
  .tag-btn {
    border: none;
    :deep(.oz-tag__content) {
      display: flex;
      align-items: center;
    }
  }
  .tag-btn {
    &.oz-tag--primary {
      color: #409eff;
      background: #d6e9ff;
    }
    &.oz-tag--success {
      background: #d2f3d5;
      color: #32a247;
    }
    &.oz-tag--error {
      color: #d50031;
      background: #f4c3c3;
    }
    &.oz-tag--warning {
      color: #e19800;
      background: #f8deaa;
    }
  }
  </style>
  