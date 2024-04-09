<template>
  <el-select v-model="val" placeholder="类型" style="width: 190px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: 'String'
  }
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const options = ref([
  {
    value: 'String',
    label: 'String'
  },
  {
    value: 'Object',
    label: 'Object'
  },
  {
    value: 'Array<String>',
    label: 'Array<String>'
  },
  {
    value: 'Array<Object>',
    label: 'Array<Object>'
  }
])
</script>

<style lang="scss" scoped></style>
