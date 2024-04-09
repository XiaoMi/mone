<template>
  <el-select v-model="val" placeholder="类型" style="width: 120px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
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
    value: 'meaning',
    label: '语义检索'
  },
  {
    value: 'fix',
    label: '混合检索'
  },
  {
    value: 'all',
    label: '全文检索'
  }
])
</script>

<style lang="scss" scoped></style>
