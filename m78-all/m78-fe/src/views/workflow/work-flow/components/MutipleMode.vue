<template>
  <el-dialog v-model="showDialog" title="多模态" width="500">
    <el-menu @select="selFn">
      <el-menu-item-group v-for="item in menu" :key="item.index" :title="item.label">
        <el-menu-item v-for="child in item.children" :index="child.index" :key="child.index">{{
          child.label
        }}</el-menu-item>
      </el-menu-item-group>
    </el-menu>
  </el-dialog>
</template>

<script setup>
import { defineEmits, computed, ref } from 'vue'

const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue', 'addModeNode'])
const showDialog = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const menu = ref([
  {
    index: 2,
    label: '图像',
    children: [
      {
        index: 'llmImageUnderstand',
        label: '图片理解'
      }
    ]
  },
  {
    index: 2,
    label: '声音',
    children: [
      {
        index: '1-1',
        label: '声音理解'
      }
    ]
  }
])
const selFn = (index, indexOath, item) => {
  console.log('index, indexOath, item', index, indexOath, item)
  // 图片理解
  if (index == 'llmImageUnderstand') {
    emits('addModeNode', index)
    // 增加一个图片理解的img;
  }
}
</script>

<style lang="scss" scoped></style>
