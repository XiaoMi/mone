<template>
  <div class="his">
    <div class="top">
      <h2 class="title">{{ props.historyData?.title }}</h2>
      <el-button type="primary" text @click="closeHistory">
        <el-icon class="close-btn"><CircleClose /></el-icon>
      </el-button>
    </div>
    <slot name="content"></slot>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  historyData: {}
})

const emits = defineEmits(['closeHistory'])
const closeHistory = () => {
  emits('closeHistory')
}
</script>

<style lang="scss" scoped>
.his {
  padding: 16px;
  height: 100%;
  overflow: auto;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}
.close-btn {
  font-size: 16px;
}
</style>
