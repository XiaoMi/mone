<template>
  <el-select v-model="val" :placeholder="props.placeholder" :disabled="props.disabled">
    <el-option v-for="item in options" :key="item.cname" :label="item.cname" :value="item.cname" />
  </el-select>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

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
    cname: 'Claude-3.5-Sonnet-company'
  },
  {
    cname: 'claude-3-sonnet-bedrock'
  },
  {
    cname: 'Claude-3.5-Sonnet-company-raw'
  }
])
// const options = computed(() => probotStore.LLMModelSelList)
</script>

<style lang="scss" scoped></style>
