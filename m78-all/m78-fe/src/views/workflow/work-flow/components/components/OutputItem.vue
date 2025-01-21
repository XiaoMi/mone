<template>
  <!-- 对象类型 -->
  <div v-if="objTypes.includes(resItem.valueType)" class="p-r">
    <EditOutBtn :item="resItem" :nodeData="nodeData" class="item-edit-btn" type="output" />
    <p class="name-i">{{ resItem.name }}</p>
    <div class="obj-box">
      <div
        v-for="objKeyItem in Object.keys(resItem.value)"
        :key="objKeyItem"
        class="obj-inner flex"
      >
        <i class="name-i">{{ objKeyItem }}：</i>
        <ResItemVal :item="resItem.value[objKeyItem]" />
      </div>
    </div>
  </div>
  <!-- 数组类型 -->
  <div v-if="resItem.valueType.startsWith('Array<')" class="p-r">
    <EditOutBtn :item="resItem" :nodeData="nodeData" class="item-edit-btn" type="output" />
    <p class="name-i">{{ resItem.name }}</p>
    <div v-for="(arrItem, index) in resItem.value" :key="index" class="arr-inner flex">
      <i class="name-i">{{ index + 1 }}：</i>
      <ResItemVal :item="arrItem" />
    </div>
  </div>
  <!-- 字符串类型 -->
  <div class="flex p-r" v-if="strArr.includes(resItem.valueType)">
    <EditOutBtn :item="resItem" :nodeData="nodeData" class="item-edit-btn" type="output" />
    <i class="name-i">{{ resItem.name }}：</i>
    <ResItemVal :item="resItem.value" />
  </div>
</template>

<script setup>
import EditOutBtn from './EditOutBtn.vue'
import ResItemVal from '@/views/workflow/work-flow/components/components/ResItemVal.vue'

const props = defineProps({
  resItem: {},
  nodeData: {}
})
const strArr = ['String', 'Code', 'Boolean', 'Integer', 'string']
const arrTypes = ['Array<String>', 'Array<Object>']
const objTypes = ['Object']
</script>

<style lang="scss" scoped>
.p-r {
  position: relative;
  &:hover {
    background: #eee;
    .item-edit-btn {
      visibility: visible;
    }
  }
}
.flex {
  display: flex;
  :deep(.out-text) {
    flex: 1;
  }
}

.item-edit-btn {
  position: absolute;
  top: 0;
  right: 0;
  visibility: hidden;
}
.obj-box,
.arr-inner {
  padding-left: 20px;
}
</style>
