<!--
 * @Description: 
 * @Date: 2024-09-19 10:54:46
 * @LastEditTime: 2024-10-11 18:37:03
-->
<template>
  <el-form :model="form" ref="formRef">
    <FormItem title="背景" prop="backgroundType">
      <TabSelect :list="cardStore.getBackgroundOptions" v-model="form.backgroundType"></TabSelect>
    </FormItem>
    <Color v-model="form.backgroundColor" v-if="form.backgroundType === 'Color'"></Color>
    <div v-if="form.backgroundType === 'Picture'">
      <FormItemUpload
        v-model="form.backgroundImageUrl"
        prop="backgroundImageUrl"
        title="资源"
      ></FormItemUpload>
      <FormItem title="透明度" prop="backgroundImageTransparency">
        <el-slider
          v-model="form.backgroundImageTransparency"
          :format-tooltip="formatTooltip"
          style="margin-left: 5%; width: 90%"
          :min="0"
          :max="100"
        />
      </FormItem>
      <FormItem title="图片展示位置" >
        <TabSelect
          :list="cardStore.getVerticalOptions"
          v-model="form.backgroundImageVerticalPosition"
        ></TabSelect>
        <TabSelect
          :list="cardStore.getHorizontalOptions"
          v-model="form.backgroundImageHorizontalPosition"
        ></TabSelect>
      </FormItem>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import FormItem from './FormItem.vue'
import TabSelect from './TabSelect.vue'
import Color from './Color.vue'
import FormItemUpload from './FormItemUpload.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue'])

const form = computed({
  get() {
    props.modelValue.backgroundImageTransparency=Number(props.modelValue.backgroundImageTransparency)
    return  props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const formatTooltip = (val: number) => {
  return val / 100
}
</script>

<style scoped lang="scss"></style>
