<!--
 * @Description: 
 * @Date: 2024-07-25 14:36:14
 * @LastEditTime: 2024-07-25 14:40:45
-->
<template>
  <FormItem title="数量" :prop="prop">
    <div class="num-container">
      <el-slider v-model="value" :step="1" show-stops :max="3" :marks="marks" />
    </div>
  </FormItem>
</template>

<script setup lang="ts">
import { reactive, onMounted, computed, watch, nextTick } from 'vue'
import FormItem from './FormItem.vue'
import type { CSSProperties } from 'vue'

interface Mark {
  style: CSSProperties
  label: string
}
const marks = reactive<Mark>({
  0: '1',
  1: '2',
  2: '3',
  3: '4'
})

const props = defineProps({
  modelValue: {
    type: Number,
    default: ''
  },
  prop: {
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
</script>

<style scoped>
.num-container {
  padding-left: 10px;
  padding-right: 10px;
  width: 100%;
}
</style>
