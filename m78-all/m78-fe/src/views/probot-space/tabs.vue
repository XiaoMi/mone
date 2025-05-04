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
      <template #code>
        <Code />
      </template>
      <template #card>
        <Card />
      </template>
    </BaseTabs>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import BaseTabs from '@/components/probot/BaseTabs.vue'
import Probots from './probots/index.vue'
import Plugins from './plugins/index.vue'
import Knowledge from './knowledge/index.vue'
import Code from './code/index.vue'
import Card from './card/index.vue'
import WorkFlow from '@/views/workflow-list/index.vue'
import { useRoute } from 'vue-router'
import { useProbotStore } from '@/stores/probot'
const probotStore = useProbotStore()
const route = useRoute()

const workspaceList = computed(() => probotStore.workspaceList)
const activeName = ref('base')
const tabsInitData = [
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
  }
]
const knowledgeData = {
  name: 'knowledge',
  labelIcon: 'icon-zhishiku',
  label: '知识库'
}
const codeData = {
  name: 'code',
  labelIcon: 'icon-a-yanfaguankong_huaban1fuben80',
  label: '代码库'
}
const cardData = {
  name: 'card',
  labelIcon: 'icon-yinhangka-F',
  label: '卡片'
}
const tabsData = ref()

watch(
  () => [route, workspaceList.value],
  ([val, list]) => {
    activeName.value = (val?.query?.tab || 'base') as string
    if (list?.length && route.params.id == list[0]?.id) {
      tabsData.value = [...tabsInitData, knowledgeData, codeData]
    } else if (val.name === 'AI Probot My Collect') {
      tabsData.value = [...tabsInitData]
    } else {
      tabsData.value = [...tabsInitData, knowledgeData]
    }
    tabsData.value.push(cardData)
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.content {
  margin: 10px 10px 0;
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
