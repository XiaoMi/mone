<template>
  <el-select v-model="val" style="width: 80px" @change="emits('change')">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup lang="ts">
import { ref, defineEmits, defineProps, computed } from 'vue'
const options = ref([
  {
    label: '知识库',
    value: 'knowlegeList'
  },
  {
    label: '引用',
    value: 'reference'
  }
])
const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue', 'change'])
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
</script>

<style scoped></style>
