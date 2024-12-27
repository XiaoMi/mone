<template>
  <el-select v-model="val" :placeholder="props.placeholder" :disabled="props.disabled">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '类型'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const options = ref([
  {
    value: 'gpt4_1106_2',
    label: 'gpt4_1106_2'
  },
  {
    value: 'moonshot_128k',
    label: 'moonshot_128k'
  },
  {
    value: 'moonshot_32k',
    label: 'moonshot_32k'
  },
  {
    value: 'moonshot',
    label: 'moonshot'
  },
  {
    value: 'glm4',
    label: 'glm4'
  }
])
</script>

<style lang="scss" scoped></style>
