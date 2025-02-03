<template>
  <template v-if="props.hasPermission"> <slot /> </template>
  <template v-else>
    <div class="com-inner-box">
      <slot />
      <div
        class="disable-style cover-div"
        v-if="!['begin', 'end', 'manualConfirm'].includes(nodeType)"
      ></div>
    </div>
  </template>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps({
  hasPermission: {},
  nodeType: {}
})
</script>

<style scoped lang="scss">
.com-inner-box {
  position: relative;
  .cover-div {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }
}
.disable-style {
  /* 使用backdrop-filter属性创建模糊效果 */
  backdrop-filter: blur(3px);
  /* 为不支持backdrop-filter的浏览器提供后备方案 */
  -webkit-backdrop-filter: blur(3px);
}
</style>
