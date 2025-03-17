<!--
 * @Description: 
 * @Date: 2024-08-08 14:10:22
 * @LastEditTime: 2024-08-08 15:26:18
-->

<template>
  <div class="fold-left-box">
    <div class="fold-left-box-left" :style="{ width: asideWidth + 'px' }" v-show="asideWidth > 0">
      <slot name="left"></slot>
    </div>
    <div
      class="fold-left-box-line"
      :style="{ cursor: asideWidth === 0 ? '' : 'col-resize' }"
      ref="dragRef"
    >
      <el-button  circle class="fold-left-box-line-button" @click="foldLeft"
        ><el-icon><ArrowRight v-if="asideWidth === 0" /><ArrowLeft v-else /></el-icon>
      </el-button>
    </div>
    <div class="fold-left-box-main">
      <slot name="main"></slot>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'

const asideWidth = ref(300)
const dragRef = ref()

onMounted(() => {
  bindDrop()
})
// 折叠事件
const foldLeft = () => {
  asideWidth.value = asideWidth.value === 0 ? 300 : 0
}

// 绑定鼠标点击事件
const bindDrop = () => {
  const drag = dragRef.value
  drag.onmousedown = function (e) {
    document.onmousemove = function (e) {
      asideWidth.value += e.movementX
      if (asideWidth.value < 20) {
        document.onmouseup()
        asideWidth.value = 0
      }
    }
    document.onmouseup = function () {
      document.onmousemove = null
      document.onmouseup = null
    }
    return false
  }
}
</script>

<style lang="less" scoped>
.fold-left-box {
  width: 100%;
  height: 100%;
  display: flex;
  &-left {
    height: 100%;
    overflow: hidden;
  }
  &-line {
    width: 4px;
    height: 100%;
    position: relative;
    border-left: 1px solid #e6e6e6;

    &-button {
      position: absolute;
      top: 50%;
      right: -12px;
    }
  }
  &-main {
    height: 100%;
    flex: 1;
    padding-left: 12px;
    overflow: hidden;
    min-width: 100px;
  }
}
</style>
