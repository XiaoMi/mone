<template>
  <div id="drag">
    <!-- 左 -->
    <div class="left" ref="leftRef"><slot name="left"></slot></div>
    <div class="resizeBar" ref="resizeLeftBar" />

    <!-- 中 -->
    <div class="center"><slot name="center"></slot></div>

    <!-- 右 -->
    <div class="resizeBar" ref="resizeRightBar" />
    <div class="right" ref="rightRef"><slot name="right"></slot></div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
const resizeLeftBar = ref()
const resizeRightBar = ref()
const leftRef = ref()
const rightRef = ref()
// 左-中之间的拖动，改变左的宽度，中设置flex:1;宽度自适应
const resizeLeftAndMedium = () => {
  const resizeElement = leftRef.value
  resizeLeftBar.value.onmousedown = (e) => {
    // left初始宽度
    const oldWidth = resizeElement.offsetWidth
    // 点击鼠标初始X值
    const startX = e.clientX
    document.onmousemove = (e) => {
      // 当前鼠标X值
      const endX = e.clientX
      // 当前鼠标在X轴移动的距离
      const distance = endX - startX
      // left-top当前高度
      const newWidth = oldWidth + distance
      // 设置dom样式
      resizeElement.style.flex = `0 0 ${newWidth}px`
    }
    document.onmouseup = () => {
      document.onmousemove = null
      document.onmouseup = null
    }
    return false
  }
}
// 中-右之间的拖动，改变右的宽度，中设置flex:1;宽度自适应
const resizeRightAndMedium = () => {
  const resizeElement = rightRef.value
  resizeRightBar.value.onmousedown = (e) => {
    // left初始宽度
    const oldWidth = resizeElement.offsetWidth
    // 点击鼠标初始X值
    const startX = e.clientX
    document.onmousemove = (e) => {
      // 当前鼠标X值
      const endX = e.clientX
      // 当前鼠标在X轴移动的距离
      const distance = startX - endX
      // left-top当前高度
      const newWidth = oldWidth + distance
      // 设置dom样式
      resizeElement.style.flex = `0 0 ${newWidth}px`
    }
    document.onmouseup = () => {
      document.onmousemove = null
      document.onmouseup = null
    }
    return false
  }
}

onMounted(() => {
  resizeLeftAndMedium()
  resizeRightAndMedium()
})
</script>

<style scoped lang="scss">
#drag {
  width: 100%;
  display: flex;
  flex: 1;
  overflow-y: auto;
}

.left {
  flex: 0 0 240px;
  padding: 10px;
}

.center {
  flex: 1;
  padding: 40px 0px;
}

.right {
  flex: 0 0 280px;
}

.resizeBar {
  width: 4px;
  height: 100%;
  &::before {
    display: inline-block;
    content: '';
    width: 2px;
    height: 100%;
    background-color: #e6e6e6;
    vertical-align: top;
  }
  &:hover {
    cursor: col-resize;
    background-color: var(--oz-menu-active-color) !important;
    &::before {
      background-color: var(--oz-menu-active-color) !important;
    }
  }
}
</style>
