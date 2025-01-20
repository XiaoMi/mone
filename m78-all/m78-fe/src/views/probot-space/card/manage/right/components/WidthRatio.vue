<!--
 * @Description: 
 * @Date: 2024-09-19 10:54:46
 * @LastEditTime: 2024-10-10 17:02:12
-->
<template>
  <FormItem title="宽度比例" prop="bg">
    <TabSelect :list="list" v-model="value"></TabSelect>
    <el-input-number v-model="num" controls-position="right" style="width: 100%; margin-top: 10px">
      <template #decrease-icon>
        <el-icon>
          <Minus />
        </el-icon>
      </template>
      <template #increase-icon>
        <el-icon>
          <Plus />
        </el-icon>
      </template>
    </el-input-number>
  </FormItem>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import FormItem from './FormItem.vue'
import TabSelect from './TabSelect.vue'

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

const list = [
  {
    name: '宽度按权重分配',
    value: '1'
  },
  {
    name: '宽度自适应内容',
    value: '2'
  }
]
const num = ref(1)
</script>

<style scoped lang="scss"></style>
