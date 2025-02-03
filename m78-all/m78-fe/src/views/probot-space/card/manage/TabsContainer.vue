<!--
 * @Description: 
 * @Date: 2024-09-05 17:49:55
 * @LastEditTime: 2024-09-29 18:07:27
-->
<template>
  <el-tabs v-model="props.activeName" type="card" class="card-tab">
    <el-tab-pane v-for="(item, index) in props.tabsData" :key="index" :name="item.name" lazy>
      <template #label>
        <span class="txt">{{ item.label }}</span>
      </template>
      <slot :name="item.name"></slot>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
const props = defineProps({
  activeName: {
    type: String,
    default: ''
  },
  tabsData: {
    type: Array<{
      name: string
      label: string
    }>,
    default: []
  }
})
</script>

<style lang="scss">
.card-tab {
  width: 100%;
  height: 100%;

  &.oz-tabs--card>.oz-tabs__header {
    height: 34px;
    border: none;
  }

  &.oz-tabs--card>.oz-tabs__header .oz-tabs__nav {
    border: none !important;
    width: 100%;
    justify-content: center;
  }

  .oz-tabs__item {
    background-color: rgba(0, 0, 0, 0.04);
    border: none !important;
    padding: 0 10px !important;
    height: 34px !important;
    width: 100%;
  }

  .txt {
    display: inline-block;
    padding: 2px 6px;
    border-radius: 5px;
    color: #666;
    width: 100%;
    text-align: center;
  }

  .is-active {
    .txt {
      background-color: #fff;
      color: var(--oz-menu-active-color);
    }
  }

  .oz-tabs__content {
    padding: 0px !important;
    height: calc(100% - 49px);
  }

  .oz-tab-pane {
    height: 100%;
    overflow: auto;
  }
}
</style>
