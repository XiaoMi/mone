<template>
  <div class="tab-wrap">
    <el-tabs tab-position="left" v-if="props.list?.length">
      <el-tab-pane v-for="v in props.list" :key="v.id">
        <template #label>
          <span class="label-name">{{ v.name }}</span>
        </template>
        <CompItem :item="v"
      /></el-tab-pane>
    </el-tabs>
    <el-empty v-else description="暂无组件" />
  </div>
</template>
<script lang="ts" setup>
import CompItem from './CompItem'
const props = defineProps({
  list: {
    type: Array,
    default() {
      return []
    }
  }
})
</script>
<style lang="scss" scoped>
.tab-wrap {
  height: 100%;
  &:deep(.oz-tabs) {
    .oz-tabs__active-bar {
      display: none;
    }
    .oz-tabs__nav {
      margin-right: 20px;
    }
    .oz-tabs__header {
      margin-right: 0;
      .oz-tabs__nav-wrap {
        padding: 0;
        &.is-left::after {
          content: none;
        }
        .oz-tabs__nav-scroll {
          height: calc(100vh - 117px);
          overflow-y: auto;
          &::-webkit-scrollbar {
            display: none;
          }
        }
        .oz-tabs__nav-prev,
        .oz-tabs__nav-next {
          display: none;
        }
      }
    }
    .oz-tabs__content {
      padding: 0 0 10px 20px;
      position: relative;
      height: calc(100vh - 117px);
      &::before {
        background-color: #dedbdb;
        width: 1px;
        height: 100%;
        left: 0px;
        top: 0;
        position: absolute;
        content: '';
      }
      .oz-tab-pane {
        height: 100%;
      }
    }
    .oz-tabs__item {
      width: 240px;
      text-align: left;
      justify-content: flex-start;
      .label-name {
        display: inline-block;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      &.is-active {
        background-color:  var(--oz-color-primary);
        border-radius: 5px;
        color: #fff;
      }
    }
  }
}
</style>
