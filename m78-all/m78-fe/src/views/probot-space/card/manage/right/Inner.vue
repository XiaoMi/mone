<!--
 * @Description: 
 * @Date: 2024-09-19 10:08:05
 * @LastEditTime: 2024-10-21 17:41:08
-->
<template>
  <Container>
    <template #base>
      <!-- 宽度比例 -->
      <div
        v-if="
          ['LAYOUT_SINGLE_ROW_1', 'LAYOUT_MULTI_ROW_1_1_1'].includes(currentItem.parentType || '')
        "
      >
        <FormItem title="宽度比例" prop="">
          <TabSelect :list="cardStore.WIDTH_RATIO_ENUM" v-model="property.form.width"></TabSelect>
        </FormItem>
        <FormItem title="" prop="" v-if="property.form.width === 'weight'">
          <el-input-number v-model="property.form.weight" style="width: 100%"></el-input-number>
        </FormItem>
        <FormItem
          title="内容过长时，挤压当前列宽"
          tip="当多列内容总宽度超出容器宽度时，允许压缩当前列宽"
          prop=""
          v-else-if="property.form.width === 'auto'"
        >
          <template #topRight>
            <el-switch
              v-model="property.form.enableShrink"
              :active-value="1"
              :inactive-value="0"
              size="small"
            />
          </template>
        </FormItem>
      </div>
      <!-- 宽度 -->
      <div v-if="['LAYOUT_SIDESLIP', 'LAYOUT_FLOAT'].includes(currentItem.parentType || '')">
        <FormItem title="宽度" prop="">
          <TabSelect :list="cardStore.WIDTH_ENUM" v-model="property.form.width"></TabSelect>
        </FormItem>
        <FormItem title="" prop="" v-if="property.form.width === 'weight'">
          <el-input-number v-model="property.form.weight" style="width: 100%"></el-input-number>
        </FormItem>
      </div>
      <!-- 垂直对齐 -->
      <FormItem title="垂直对齐" prop="">
        <TabSelect :list="cardStore.vertical_arr" v-model="property.form.vertical"></TabSelect>
      </FormItem>
      <!-- 水平对齐 -->
      <FormItem title="水平对齐" prop="">
        <TabSelect :list="cardStore.horizontal_arr" v-model="property.form.horizontal"></TabSelect>
      </FormItem>
      <FormItem title="显示边框" v-if="['LAYOUT_SIDESLIP'].includes(currentItem.parentType || '')">
        <template #topRight>
          <el-switch
            v-model="property.form.enableShowBorder"
            :active-value="1"
            :inactive-value="0"
            size="small"
          />
        </template>
      </FormItem>
      <LineSpace v-model="property.form.rowGap"></LineSpace>
      <Padding v-model="property.form.padding"></Padding>
    </template>
    <template #operate>
      <Operate v-model="property.operate"></Operate>
    </template>
  </Container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Container from './Container.vue'
import TabSelect from './components/TabSelect.vue'
import FormItem from './components/FormItem.vue'
import LineSpace from './components/LineSpace.vue'
import Padding from './components/Padding.vue'
import Operate from './components/Operate.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()
const currentItem = computed(() => cardStore.currentItem)

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['update:modelValue'])
const property = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>
<style lang="scss" scoped></style>
