<template>
  <el-switch v-model="val" class="run-switch" />
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed } from 'vue'

const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    if (typeof props.modelValue == 'undefined') {
      const v = true
      emits('update:modelValue', v)
      return v
    }
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style scoped lang="scss">
.run-switch.oz-switch.is-checked {
  :deep(.oz-switch__core) {
    background-color: #13ce66;
    border-color: #13ce66;
  }
}
</style>
