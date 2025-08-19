<!--
 * @Description: 
 * @Date: 2024-09-19 11:18:43
 * @LastEditTime: 2024-09-24 20:26:53
-->
<template>
  <FormItem title="内间距" prop="padding">
    <el-select v-model="value" placeholder="Select"  style="width: 100%">
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </FormItem>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import FormItem from './FormItem.vue'

const props = defineProps({
  modelValue: {
    type: Number
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
const options = ref([
  {
    value: 0,
    label: '0px'
  },
  {
    value: 4,
    label: '4px'
  },
  {
    value: 8,
    label: '8px'
  },
  {
    value: 12,
    label: '12px'
  },
  {
    value: 16,
    label: '16px'
  },
  {
    value: 20,
    label: '20px'
  },
  {
    value: 24,
    label: '24px'
  },
  {
    value: 28,
    label: '28px'
  },
  {
    value: 32,
    label: '32px'
  }
])
</script>

<style scoped lang="scss"></style>
