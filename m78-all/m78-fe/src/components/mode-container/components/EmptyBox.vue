<template>
  <div class="img-box center-class">
    <div class="tips-inner">
      <el-empty description=" " />
      <p class="tips-item">等你好久了</p>
      <p class="tips-item">快去左侧输入你的创意吧！</p>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.img-box {
  width: 600px;
  height: 600px;
  border: dashed 1px #dcdfe6;
  overflow: hidden;
}
.center-class {
  display: flex;
  justify-content: center;
  align-items: center;
}
.tips-inner {
  text-align: center;
}
.tips-item {
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  margin-bottom: 5px;
}
</style>
