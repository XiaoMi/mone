<!--
 * @Description: 
 * @Date: 2024-10-17 15:30:29
 * @LastEditTime: 2024-10-17 15:30:41
-->
<!--
 * @Description: 
 * @Date: 2024-10-17 15:30:05
 * @LastEditTime: 2024-10-17 15:30:18
-->
<!--
 * @Description: 
 * @Date: 2024-10-17 15:29:26
 * @LastEditTime: 2024-10-17 15:29:52
-->
<!--
 * @Description: 
 * @Date: 2024-09-19 10:08:05
 * @LastEditTime: 2024-10-17 15:23:22
-->
<template>
  <Container>
    <template #data>
      <!-- todo：固定格数和动态格数 -->
      <div
        v-if="
          ['LAYOUT_ROW', 'LAYOUT_GRID', 'LAYOUT_SIDESLIP', 'LAYOUT_FLOAT'].includes(
            currentItem.type
          )
        "
      >
        <FormItem title="" prop="">
          <TabSelect :list="cardStore.FRAME_ENUM"></TabSelect>
        </FormItem>
        <FormItem prop="" title="绑定数组类型变量" :require="true">
          <el-select v-model="property.boundArrayVariable" placeholder="Select" style="width: 100%">
            <el-option
              v-for="(item, key) in variableList.filter((item) => item.classType === 'Array')"
              :key="item.id"
              :label="item.name"
              :value="key.id"
            />
          </el-select>
        </FormItem>
      </div>
      <FormItem title="插槽集合">
        <template #topRight>
          <el-button type="primary" link @click="add" size="small"
            ><el-icon><Plus /></el-icon>添加插槽</el-button
          >
        </template>
        <div class="slot-container">
          <VueDraggable ref="el" v-model="currentItem.children" :animation="150" ghostClass="ghost">
            <div v-for="(item, index) in currentItem.children" :key="index" class="slot-item">
              <div>插槽{{ index + 1 }}</div>
              <el-link
                :underline="false"
                @click="remove(index)"
                v-if="currentItem.children.length > 1"
              >
                <el-icon><Delete /></el-icon>
              </el-link>
            </div>
          </VueDraggable>
        </div>
      </FormItem>
    </template>
    <template #base>
      <!-- todo -->
      <FormItem title="每行展示数目" prop="" v-if="['LAYOUT_GRID'].includes(currentItem.type)">
        <el-input-number v-model="property.num" style="width: 100%"></el-input-number>
      </FormItem>
      <Background v-model="property.background"></Background>
      <LineSpace v-model="property.form.rowGap"></LineSpace>
      <Padding v-model="property.form.padding"></Padding>
      <Fillet v-model="property.form.roundedSize"></Fillet>
    </template>
    <template #operate>
      <Operate v-model="property.operate"></Operate>
    </template>
    <template v-if="property.visibilitySetting" #high>
      <LoopRendering v-model="property.loopRending"></LoopRendering>
      <Visible v-model="property.visibilitySetting"></Visible>
    </template>
  </Container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { VueDraggable } from 'vue-draggable-plus'
import Container from './Container.vue'
import TabSelect from './components/TabSelect.vue'
import FormItem from './components/FormItem.vue'
import Background from './components/Background.vue'
import LineSpace from './components/LineSpace.vue'
import Padding from './components/Padding.vue'
import Fillet from './components/Fillet.vue'
import Operate from './components/Operate.vue'
import Visible from './components/Visible.vue'
import LoopRendering from './components/LoopRendering.vue'
import { deepCloneTree } from '../tree'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()

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

const currentItem = computed(() => cardStore.currentItem)
const dataTypeMap = computed(() => cardStore.dataTypeMap)
const variableList = computed(() => cardStore.variableList)

const add = () => {
  const item = deepCloneTree(dataTypeMap.value[currentItem.value.type].children[0])
  currentItem.value.children.push(item)
  currentItem.value.children.forEach((element, index) => {
    element.label = element.treeLabel + (index + 1)
  })
}
const remove = (index) => {
  currentItem.value.children.splice(index, 1)
  currentItem.value.children.forEach((element, index) => {
    element.label = element.treeLabel + (index + 1)
  })
}
</script>
<style lang="scss" scoped>
.slot-container {
  background-color: #fff;
  width: 100%;
  border-radius: 5px;
  padding: 5px;
  .slot-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 5px;
    border-radius: 5px;
    &:hover {
      background-color: #f1f1f1;
      cursor: move;
    }
  }
}
</style>
