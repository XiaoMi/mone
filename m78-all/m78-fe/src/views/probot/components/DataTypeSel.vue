<template>
  <el-select v-model="val" placeholder="请选择类型" style="width: 210px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { ref, computed } from 'vue'
const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue'])
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
    value: 'Integer',
    label: 'Integer'
  },
  {
    value: 'Number',
    label: 'Number'
  },
  {
    value: 'Time',
    label: 'Time'
  },
  {
    value: 'Boolean',
    label: 'Boolean'
  }
])
</script>

<style lang="scss" scoped></style>
