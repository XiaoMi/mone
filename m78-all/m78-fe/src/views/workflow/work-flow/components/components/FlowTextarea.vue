<template>
  <el-input
    v-model="val"
    :placeholder="placeholder"
    type="textarea"
    :autosize="autosize"
    class="tips-text nowheel"
    @wheel="handleNoWheelFn"
  />
</template>

<script setup>
import { defineProps, computed, defineEmits, ref } from 'vue'
import { handleNoWheel } from '@/views/workflow/work-flow/baseInfo.js'

const handleNoWheelFn = ref(handleNoWheel)
const props = defineProps({
  modelValue: {},
  autosize: {
    default: {
      minRows: 2,
      maxRows: 5
    }
  },
  placeholder: {}
})
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style lang="scss" scoped>
.tips-text {
  :deep(.oz-textarea__inner) {
    color: #2c2c2c;
    border-radius: unset;
  }
}
</style>
