<template>
  <el-select v-model="val" placeholder="请选择">
    <el-option
      v-for="item in daysOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
import { daysOps } from '@/views/probot/baseInfo.js'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {}
})

const daysOptions = ref(daysOps)

const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style lang="scss" scoped></style>
