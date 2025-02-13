<!--
 * @Description: 
 * @Date: 2024-09-19 10:54:46
 * @LastEditTime: 2024-09-23 17:51:54
-->
<template>
  <FormItem title="选择颜色" prop="backgroundColor">
    <el-color-picker v-model="value" show-alpha :predefine="predefineColors" />
  </FormItem>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import FormItem from './FormItem.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emits = defineEmits(['update:modelValue'])

const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const predefineColors = ref([
  '#ff4500',
  '#ff8c00',
  '#ffd700',
  '#90ee90',
  '#00ced1',
  '#1e90ff',
  '#c71585',
  'rgba(255, 69, 0, 0.68)',
  'rgb(255, 120, 0)',
  'hsv(51, 100, 98)',
  'hsva(120, 40, 94, 0.5)',
  'hsl(181, 100%, 37%)',
  'hsla(209, 100%, 56%, 0.73)',
  '#c7158577'
])
</script>

<style scoped lang="scss"></style>
