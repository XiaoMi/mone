<template>
  <el-tooltip effect="dark" content="测试该节点" placement="top">
    <el-button link @click="clickFn" class="test-node-btn" :disabled="disabled">
      <i class="iconfont icon-play"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
const props = defineProps({
  disabled: {}
})
const emits = defineEmits(['click'])
const clickFn = () => {
  emits('click')
}
</script>

<style lang="scss" scoped>
.test-node-btn {
  margin-right: 5px;
}
</style>
