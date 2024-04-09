<template>
  <el-select v-model="val" style="width: 140px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
import { opList } from '../../baseInfo'

const props = defineProps({
  value: {
    type: String
  }
})
const options = ref(opList)
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    return props.value
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
</script>

<style lang="scss" scoped></style>
