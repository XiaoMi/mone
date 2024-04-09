<template>
  <div class="content">
    <BaseTabs :activeName="activeName" :tabsData="tabsData">
      <template #base>
        <Probots />
      </template>
      <template #plug>
        <Plugins />
      </template>
      <template #workflow>
        <WorkFlow />
      </template>
      <template #knowledge>
        <Knowledge />
      </template>
    </BaseTabs>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import BaseTabs from '../components/BaseTabs.vue'
import Probots from './probots/index.vue'
import Plugins from './plugins/index.vue'
import Knowledge from './knowledge/index.vue'
import WorkFlow from '@/views/workflow-list/index.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeName = ref('base')
const tabsData = [
  {
    name: 'base',
    labelIcon: 'icon-APP-robot1',
    label: 'Probots'
  },
  {
    name: 'plug',
    labelIcon: 'icon-chajianku-chajianku',
    label: '插件'
  },
  {
    name: 'workflow',
    labelIcon: 'icon-gongzuoliu',
    label: '工作流'
  },
  {
    name: 'knowledge',
    labelIcon: 'icon-zhishiku',
    label: '知识库'
  }
]

watch(
  () => route,
  (val) => {
    activeName.value = (val?.query?.tab || 'base') as string
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.content {
  margin: 0px 50px;
  padding: 10px;
  background-color: #fff;
  box-shadow:
    (0 0 #0000, 0 0 #0000),
    (0 0 #0000, 0 0 #0000),
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -4px rgba(0, 0, 0, 0.1);
  background-color: hsl(0 0% 100%);
  color: hsl(224 71.4% 4.1%);
  border-radius: 10px;
  flex: 1;
  overflow: auto;
}
</style>
