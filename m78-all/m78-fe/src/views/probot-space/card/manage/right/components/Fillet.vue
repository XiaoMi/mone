<!--
 * @Description: 
 * @Date: 2024-09-26 20:10:45
 * @LastEditTime: 2024-10-10 17:07:38
-->
<template>
  <FormItem title="圆角" prop="padding">
    <el-select v-model="value" placeholder="Select" style="width: 100%">
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
  }
])
</script>

<style scoped lang="scss"></style>
