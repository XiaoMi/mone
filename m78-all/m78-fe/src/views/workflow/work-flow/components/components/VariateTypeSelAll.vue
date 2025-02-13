<template>
  <el-select v-model="val" placeholder="类型" style="width: 190px" :size="size">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
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
  size: {
    type: String,
    default: 'small'
  }
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
const opsButImg = baseValueTypes.filter((it) => it.value != 'Image')
const options = ref(opsButImg)
</script>

<style lang="scss" scoped></style>
