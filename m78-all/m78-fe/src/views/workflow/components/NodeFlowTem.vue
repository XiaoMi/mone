<template>
  <div class="node-item" :draggable="draggable" @dragstart="onDragStartFn($event)" @click="addNode">
    <div class="top-p">
      <div class="top-l">
        <slot />
      </div>
      <el-button link class="add-btn">
        <el-icon color="#4d53e8"><Plus /></el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  draggable: {
    default: true,
    type: Boolean
  }
})

const emits = defineEmits(['onDragStart', 'addClick'])
const onDragStartFn = (e) => {
  emits('onDragStart', e)
}
const addNode = () => {
  emits('addClick')
}
</script>

<style lang="scss" scoped>
.node-item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 6px 8px 0 rgba(29, 28, 35, 0.06);
  cursor: -webkit-grab;
  cursor: grab;
  font-size: 12px;
  line-height: 16px;
  overflow: hidden;
  padding: 16px;
  margin-bottom: 10px;
}
.top-p {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-l {
  display: flex;
  align-items: center;
}
</style>
