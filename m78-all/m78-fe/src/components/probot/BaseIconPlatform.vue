<!--
 * @Description: 
 * @Date: 2024-03-14 19:46:57
 * @LastEditTime: 2024-03-27 20:28:46
-->
<template>
  <div class="icon-platform-container">
    <i class="iconfont icon-logo-wechat" v-if="props.iconType === 2"></i>
    <i class="iconfont icon-iconfeishuLOGO" v-if="props.iconType === 1"></i>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  iconType: {
    type: Number,
    default: 0
  }
})
</script>

<style scoped lang="scss">
.icon-platform-container {
  padding: 0px 10px;
  display: flex;
  align-items: center;
}
.iconfont {
  font-size: 20px;

  &.icon-iconfeishuLOGO {
    color: #017eff;
  }
  &.icon-logo-wechat {
    color: #6cc839;
  }
}
</style>
