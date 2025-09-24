<!--
 * @Description: 
 * @Date: 2024-10-10 17:54:23
 * @LastEditTime: 2024-10-17 11:11:04
-->
<template>
  <el-form :model="form" ref="formRef">
    <FormItem
      title="循环渲染"
      tip="循环展示：给组件绑定 Array 类型的数据后，即可将 Array 中的每一项数据在画布中渲染出来。使用此功能可实现新闻、商品列表的效果"
    >
      <template #topRight>
        <FormItem prop="enableLoopRending">
          <el-switch
            v-model="form.enableLoopRending"
            :active-value="1"
            :inactive-value="0"
            size="small"
          />
        </FormItem>
      </template>
      <FormItem
        prop="boundArrayVariable"
        title="绑定数组类型变量"
        :require="true"
        style="width: 100%"
        v-if="form.enableLoopRending"
      >
        <el-select v-model="form.boundArrayVariable" placeholder="Select" style="width: 100%">
          <el-option
            v-for="(item, key) in variableList.filter((item) => item.classType === 'Array')"
            :key="item.id"
            :label="item.name"
            :value="key.id"
          />
        </el-select>
      </FormItem>
    </FormItem>
  </el-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import FormItem from './FormItem.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()
const variableList = computed(() => cardStore.variableList)

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue'])

const form = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style scoped lang="scss"></style>
