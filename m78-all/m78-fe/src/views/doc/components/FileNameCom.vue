<template>
  <div class="doc-name">
    <i class="iconfont icon-baiwangyun-EXCELwenjian font"></i>
    <i class="font text">{{ props.name }}</i>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  name: {
    type: String,
    default: '文件名'
  }
})
</script>

<style lang="scss" scoped>
.doc-name {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  .text {
    font-weight: 700;
    font-size: 22px;
    margin-left: 5px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
