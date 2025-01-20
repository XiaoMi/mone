<template>
  <FormItem title="模型选择" :prop="prop">
    <ModalLLMModelSel v-model="value" style="width: 100%" :apiType="apiType" />
  </FormItem>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import FormItem from './FormItem.vue'
import ModalLLMModelSel from '@/components/ModalLLMModelSel.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  prop: {
    type: String,
    default: ''
  },
  apiType: {}
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
const options = [
  {
    value: 'Option1',
    label: 'Option1'
  },
  {
    value: 'Option2',
    label: 'Option2'
  },
  {
    value: 'Option3',
    label: 'Option3'
  },
  {
    value: 'Option4',
    label: 'Option4'
  },
  {
    value: 'Option5',
    label: 'Option5'
  }
]
</script>

<style scoped></style>
