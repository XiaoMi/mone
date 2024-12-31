<template>
  <el-select v-model="val" style="width: 60px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
const props = defineProps({
  value: {
    type: String,
    default: 'or'
  }
})
const options = ref([
  {
    value: 'or',
    label: '或者'
  },
  {
    value: 'and',
    label: '并且'
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
