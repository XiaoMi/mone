<!--
 * @Description: 
 * @Date: 2024-03-05 18:12:21
 * @LastEditTime: 2024-03-13 19:00:04
-->
<template>
  <el-tooltip effect="light" placement="bottom" ref="tooltipRef">
    <template #content><slot></slot> </template>
    <div class="probot-tooltip-menu">
      <span>{{ props.name }}</span>
      <el-icon class="probot-tooltip-menu-icon">
        <ArrowDown />
      </el-icon>
    </div>
  </el-tooltip>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const tooltipRef = ref()

const props = defineProps({
  name: {
    type: String,
    default: ''
  }
})

defineExpose({
  hide: () => {
    tooltipRef.value.hide()
  }
})
</script>

<style scoped lang="scss">
.probot-tooltip-menu {
  .probot-tooltip-menu-icon {
    font-size: 12px;
    margin-left: 4px;
    transition: transform 0.3s;
    transform: none;
    transform-origin: 50% 50%;
  }
  &:hover {
    background-color: transparent !important;
    .probot-tooltip-menu-icon {
      transform: rotateZ(180deg);
    }
  }
}
</style>
