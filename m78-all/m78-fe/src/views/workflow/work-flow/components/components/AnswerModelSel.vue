<template>
  <el-select v-model="val" placeholder="请选择回答模式" style="width: 180px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
const props = defineProps({
  value: {
    type: String,
    default: ''
  }
})
const options = ref([
  {
    value: 'var',
    label: '返回变量，由Bot生成回答'
  },
  {
    value: 'content',
    label: '使用设定的内容直接回答'
  }
])
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    return props.value
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
</script>

<style lang="scss" scoped></style>
