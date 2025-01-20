<!--
 * @Description: 
 * @Date: 2024-03-06 15:05:37
 * @LastEditTime: 2024-09-05 17:56:12
-->
<template>
  <div class="base-tabs">
    <el-tabs v-model="props.activeName" @tab-click="handleClick">
      <el-tab-pane v-for="(item, index) in props.tabsData" :key="index" :name="item.name">
        <template #label>
          <span class="custom-tabs-label">
            <i :class="'iconfont ' + item.labelIcon"></i>
            <span class="txt">{{ item.label }}</span>
          </span>
        </template>
        <slot :name="item.name"></slot>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import type { TabsPaneContext } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({
  activeName: {
    type: String,
    default: ''
  },
  tabsData: {
    type: Array,
    default: []
  }
})

const router = useRouter()

const handleClick = (tab: TabsPaneContext, event: Event) => {
  router.push({
    query: {
      tab: tab.props.name
    }
  })
}
</script>

<style lang="scss">
.base-tabs {
  height: 100%;
  .oz-tabs {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .oz-tabs__content {
    flex: 1;
    overflow: auto;
    padding: 0 40px;
  }
  .custom-tabs-label {
    padding: 8px 24px;
    font-size: 16px;
    display: flex;
    align-items: center;
    color: rgb(71, 85, 105);
    margin-bottom: 10px;
    .iconfont {
      font-size: 20px;
    }
    .txt {
      padding-left: 8px;
    }
  }
  .oz-tabs__item.is-active,
  .oz-tabs__item:hover {
    .custom-tabs-label {
      color: var(--oz-menu-active-color);
      border-color: var(--oz-menu-active-color);
    }
  }
  .oz-tabs__active-bar {
    height: 3px;
    background-color: var(--oz-menu-active-color);
  }
  .oz-tab-pane {
    height: 100%;
  }
}
</style>
