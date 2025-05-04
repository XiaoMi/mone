<!--
 * @Description: 
 * @Date: 2024-04-15 15:59:16
 * @LastEditTime: 2024-04-15 16:12:02
-->
<template>
  <el-select v-model="langValue" clearable disabled>
    <el-option label="JavaScript" value="text/javascript"></el-option>
    <el-option label="Python" value="text/python"></el-option>
    <el-option label="Groovy" value="text/groovy"></el-option>
  </el-select>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: 'text/groovy'
  }
})
const langValue = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
</script>

<style scoped></style>
