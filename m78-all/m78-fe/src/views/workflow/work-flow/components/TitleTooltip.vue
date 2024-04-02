<template>
  <div class="t-wrap">
    <div class="title-box">
      <span class="t">{{ props.title }}</span>
      <el-tooltip :content="props.content" placement="top" raw-content>
        <el-icon class="warning-icon"><Warning /></el-icon>
      </el-tooltip>
    </div>
    <el-button @click="addFn" link v-if="showAdd">
      <i class="iconfont icon-plus1"></i>
    </el-button>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  content: {
    type: String,
    default: '解释'
  },
  showAdd: {
    type: Boolean,
    default: true
  }
})
const emits = defineEmits(['add'])
const addFn = () => {
  emits('add')
}
</script>

<style lang="scss" scoped>
.t {
  font-weight: 600;
  margin-right: 2px;
  vertical-align: middle;
  font-size: 13px;
}
.warning-icon {
  color: rgba(28, 29, 35, 0.6);
  vertical-align: middle;
}
.t-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}
.icon-plus1 {
  font-size: 12px;
}
</style>
