<template>
  <el-select
    v-model="val"
    placeholder="类型"
    style="width: 190px"
    size="small"
    :disabled="disabled"
  >
    <el-option v-for="item in realOps" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
import { baseValueTypes } from '@/views/workflow/work-flow/baseInfo.js'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: 'String'
  },
  disabled: {
    default: false
  },
  nodeType: {}
})
const val = computed({
  get() {
    const val = props.modelValue
    if (!val) {
      emits('update:modelValue', 'String')
      return 'String'
    }
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
    emits('change', val)
  }
})
const ops = baseValueTypes.filter((it) => it.value != 'Code')
const options = ref(ops)

const realOps = computed(() => {
  return ['llmImageUnderstand', 'begin'].includes(props.nodeType)
    ? options.value
    : options.value.filter((item) => item.value != 'Image')
})
</script>

<style lang="scss" scoped></style>
