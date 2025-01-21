<!--
 * @Description: 
 * @Date: 2024-08-15 10:42:28
 * @LastEditTime: 2024-08-15 11:01:59
-->
<template>
  <div class="base-fold">
    <div class="left" v-if="arrowLeft">
      <slot name="left"></slot>
    </div>
    <div class="arrow-container">
      <el-icon :class="['arrow-content', arrowLeft ? 'left-arrow' : '']" @click="arrowClick">
        <i :class="['iconfont  icon-shouqi']"></i>
      </el-icon>
    </div>
    <div :class="['main', arrowLeft ? 'left-main' : '']">
      <slot name="main"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue'
const emits = defineEmits(['changeFlod'])
const arrowLeft = ref(localStorage.getItem('arrowLeft') === 'false' ? false : true)

const arrowClick = () => {
  arrowLeft.value = !arrowLeft.value
  localStorage.setItem('arrowLeft', String(arrowLeft.value))
  emits('changeFlod', arrowLeft.value)
}
</script>

<style scoped lang="scss">
.base-fold {
  display: flex;
  height: 100%;
  width: 100%;
  overflow: hidden;
  .left {
    width: 240px;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.7);
    padding: 20px 10px;
    display: flex;
    flex-direction: column;
  }
  .arrow-container {
    position: relative;
    .arrow-content {
      position: absolute;
      width: 30px;
      height: 30px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      top: 50%;
      left: 5px;
      transform: translateY(-50%);
      cursor: pointer;
      z-index: 100;
      font-size: 20px;
      transition: all 0.3s;
      transform: translateY(-50%);

      &:hover {
        transform: translateY(-50%) scale(1.2);
      }
      &.left-arrow {
        left: -25px;
        transform: translateY(-50%) scaleX(-1);
        &:hover {
          transform: translateY(-50%) scaleX(-1) scale(1.2);
        }
      }
      .iconfont {
        font-size: 25px;
        color: #666;
      }
    }
  }
  .main {
    flex: 1;
    height: 100%;
    overflow: auto;
    padding: 0 40px;
    &.left-main {
      padding: 0 0px;
    }
  }
}
</style>
