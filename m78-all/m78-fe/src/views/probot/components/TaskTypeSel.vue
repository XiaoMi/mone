<template>
  <el-select v-model="val" placeholder="请选择">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed } from 'vue'
const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {}
})
const options = [
  {
    value: 0,
    label: 'cron'
  },
  {
    value: 1,
    label: '固定频率'
  },
  {
    value: 2,
    label: '单次执行'
  }
]
const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style lang="scss" scoped></style>
