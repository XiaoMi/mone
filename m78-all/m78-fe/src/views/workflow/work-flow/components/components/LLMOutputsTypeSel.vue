<template>
  <el-select v-model="val" placeholder="请选择类型">
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get: () => props.modelValue,
  set: (val) => {
    emits('update:modelValue', val)
  }
})
const options = ref([
  {
    label: 'String',
    value: 'String'
  },
  // {
  //   label: 'Integer',
  //   value: 'Integer'
  // },
  // {
  //   label: 'Boolean',
  //   value: 'Boolean'
  // },
  // {
  //   label: 'Number',
  //   value: 'Number'
  // },
  {
    label: 'Object',
    value: 'Object',
    disabled: true
  },
  {
    label: 'Array<String>',
    value: 'Array<String>'
  },
  // {
  //   label: 'Array<Integer>',
  //   value: 'Array<Integer>'
  // },
  // {
  //   label: 'Array<Boolean>',
  //   value: 'Array<Boolean>'
  // },
  // {
  //   label: 'Array<Number>',
  //   value: 'Array<Number>'
  // },
  {
    label: 'Array<Object>',
    value: 'Array<Object>',
    disabled: true
  }
])
</script>

<style lang="scss" scoped></style>
