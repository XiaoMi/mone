<template>
  <el-cascader
    v-model="val"
    :options="options"
    :props="actions"
    :show-all-levels="false"
    clearable
    @change="change"
    @visible-change="changeVisible"
    ref="cascaderRef"
  />
</template>

<script setup>
import { ref, computed } from 'vue'
// import { isValueValid } from '@/views/workflow/work-flow/baseInfo.js'

const cascaderRef = ref(null)
const emits = defineEmits(['update:modelValue', 'change'])

const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const props = defineProps({
  modelValue: {},
  options: {}
})

const change = (val) => {
  emits('change', val)
  // hideFn()
  cascaderRef.value.togglePopperVisible()
}
const actions = {
  expandTrigger: 'click',
  checkStrictly: true
}
const isVisible = ref(false)
const changeVisible = (val) => {
  isVisible.value = val
}
const hideFn = () => {
  if (isVisible.value) {
    isVisible.value = false
    cascaderRef.value.togglePopperVisible()
  }
}
</script>

<style lang="scss" scoped></style>
