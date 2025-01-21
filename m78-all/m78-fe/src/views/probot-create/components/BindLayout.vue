<!--
 * @Description: 
 * @Date: 2024-03-08 15:57:41
 * @LastEditTime: 2024-08-15 15:56:20
-->
<template>
  <div class="">
    <BaseGroup
      :title="props.title"
      :tooltip="props.tooltip"
      :btn="props.btn"
      class="head-container"
    ></BaseGroup>
    <ul class="list-container" v-if="props.data?.length">
      <li v-for="(item, index) in props.data" :key="index">
        <div class="left">
          <BaseInfo :data="item" size="small"></BaseInfo>
        </div>
        <div class="right">
          <slot name="right" :item="item" :index="index"></slot>
        </div>
      </li>
    </ul>
    <el-empty v-else :description="props.empty" :image-size="80">
      <!-- <BaseLink name="现在去绑定" icon="icon-plus1" @click="add"></BaseLink> -->
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import BaseInfo from '@/components/BaseInfo.vue'
import BaseGroup from './BaseGroup.vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  tooltip: {
    type: String,
    default: ''
  },
  btn: {
    type: Object,
    default: () => ({})
  },
  data: {
    type: Object,
    default: () => ({})
  },
  empty: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.head-container {
  padding-bottom: 10px;
}
.list-container {
  border-top: 1px solid #ddd;
  li {
    border-bottom: 1px solid #ddd;
    display: flex;
    align-items: center;
    padding: 10px 0;

    .left {
      width: 20%;
    }
    .right {
      flex: 1;
    }
  }
}
</style>