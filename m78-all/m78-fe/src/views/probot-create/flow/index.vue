<!--
 * @Description: 
 * @Date: 2024-03-04 16:35:15
 * @LastEditTime: 2024-08-15 17:36:00
-->
<template>
  <div class="probot-flow-form">
    <BindLayout
      class="flow-plug"
      title="插件绑定"
      tooltip=" 插件允许机器人调用外部API，比如搜索信息、浏览网页、生成图像，从而拓展机器人的功能和使用场景。"
      :btn="{
        name: '绑定',
        icon: 'icon-plus1',
        click: addPlug,
        size: 'small',
        disabled: props.disabled
      }"
      :data="selectedPlugData"
      empty="给你的Probot绑定插件吧"
    >
      <template v-slot:right="{ item, index }">
        <div v-for="(v, i) in item.plugins" :key="i" class="child-container">
          <p>{{ v.name }}</p>
          <p>{{ v.describe }}</p>
          <div class="btn-container">
            <BaseLink
              name="参数列表"
              icon="icon-canshuliebiao"
              @click="plugParameterListClick(v)"
            ></BaseLink>
            <BaseLink name="卡片样式" icon="icon-danlanqiapianyangshi"></BaseLink>
            <BaseLink
              name="移除"
              icon="icon-yichu"
              @click="removePlug(index, i)"
              :disabled="props.disabled"
            ></BaseLink>
          </div>
        </div>
      </template>
    </BindLayout>
    <BindLayout
      class="flow-workflow"
      title="工作流绑定"
      tooltip="允许机器人调用工作流，将插件、大型语言模型、代码块等功能进行组合，以实现复杂且稳定的业务流程编排"
      :btn="{
        name: '绑定',
        icon: 'icon-plus1',
        click: addWorkflow,
        size: 'small',
        disabled: props.disabled
      }"
      :data="selectedWorkflowData"
      empty="给你的Probot绑定工作流吧"
    >
      <template v-slot:right="{ item, index }">
        <div class="btn-container">
          <BaseLink
            name="参数列表"
            icon="icon-canshuliebiao"
            @click="workParameterListClick(item)"
          ></BaseLink>
          <BaseLink name="卡片样式" icon="icon-danlanqiapianyangshi"></BaseLink>
          <BaseLink
            name="移除"
            icon="icon-yichu"
            @click="removeWorkflow(index)"
            :disabled="props.disabled"
          ></BaseLink>
        </div>
      </template>
    </BindLayout>
    <BindPlug v-model="bindPlugVisible" :data="plugData" @update="updatePlugData"></BindPlug>
    <BindWorkflow
      v-model="bindWorkflowVisible"
      :data="workflowData"
      @update="updateWorkflowData"
    ></BindWorkflow>
    <BindPlugParameterList
      v-model="plugParameterListVisible"
      :data="plugParameterListData"
    ></BindPlugParameterList>
    <BindWorkflowParameterList
      v-model="workflowParameterListVisible"
      :data="workflowParameterListData"
    ></BindWorkflowParameterList>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import BindPlug from './BindPlug.vue'
import BindWorkflow from './BindWorkflow.vue'
import { getBotplugin, getFlowList, getBotPluginParameter } from '@/api/probot'
import BindLayout from '../components/BindLayout.vue'
import BindPlugParameterList from './BindPlugParameterList.vue'
import BindWorkflowParameterList from './BindWorkflowParameterList.vue'
import mittBus from '@/utils/bus'

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const plugData = ref([])
const selectedPlugData = ref([])
const bindPlugVisible = ref(false)
const workflowData = ref([])
const selectedWorkflowData = ref([])
const bindWorkflowVisible = ref(false)
const plugParameterListVisible = ref(false)
const plugParameterListData = ref()
const workflowParameterListVisible = ref(false)
const workflowParameterListData = ref()

const matchPlugData = () => {
  //匹配数据
  plugData.value?.forEach((item: any) => {
    item.plugins?.forEach((v: any) => {
      v.bind = false
    })
  })
  //匹配数据
  plugData.value?.forEach((item: any) => {
    item.plugins?.forEach((v: any) => {
      selectedPlugData.value?.forEach((selectedItem) => {
        selectedItem.plugins?.forEach((selectedV) => {
          if (v.id === selectedV.id) {
            v.bind = true
          }
        })
      })
    })
  })
}
const matchFlowData = () => {
  workflowData.value?.forEach((item: any) => {
    item.bind = false
  })
  workflowData.value?.forEach((item: any) => {
    selectedWorkflowData.value?.forEach((selectedItem) => {
      if (item.id === selectedItem.id) {
        item.bind = true
      }
    })
  })
}
const getBotpluginData = (params?: {}) => {
  getBotplugin({
    ...params,
    pageNum: 1,
    pageSize: 1000
  }).then((res) => {
    plugData.value = res?.data?.records?.map((item) => {
      return {
        ...item,
        name: item.pluginOrgName
      }
    })
    matchPlugData()
  })
}

const getFlowListData = (params?: {}) => {
  getFlowList({ ...params }).then((res) => {
    workflowData.value = res?.data?.records?.map((item) => {
      return {
        ...item.flowBaseInfo
      }
    })
    matchFlowData()
  })
}

mittBus.on('filterPlugData', (params) => {
  getBotpluginData(params)
})
mittBus.on('filterFlowListData', (params) => {
  getFlowListData(params)
})

const addPlug = () => {
  bindPlugVisible.value = true
  getBotpluginData()
}
const removePlug = (index: number, childIndex: number) => {
  selectedPlugData.value[index]?.plugins.splice(childIndex, 1)
  if (selectedPlugData.value[index]?.plugins.length === 0) {
    selectedPlugData.value.splice(index, 1)
  }
}
const updatePlugData = (data: any) => {
  selectedPlugData.value = data
}
const plugParameterListClick = (val: any) => {
  plugParameterListVisible.value = true
  getBotPluginParameter({
    id: val?.id
  }).then((res) => {
    if (res?.data && res?.data?.meta) {
      const data = JSON.parse(res?.data.meta)
      plugParameterListData.value = {
        http_headers: data?.http_headers,
        input: data?.input,
        output: data?.output
      }
    } else {
      plugParameterListData.value = ''
    }
  })
}
const addWorkflow = () => {
  bindWorkflowVisible.value = true
  getFlowListData()
}
const removeWorkflow = (index: number) => {
  selectedWorkflowData.value.splice(index, 1)
}
const updateWorkflowData = (data: any) => {
  selectedWorkflowData.value = data
}
const workParameterListClick = (item: any) => {
  workflowParameterListVisible.value = true
  workflowParameterListData.value = {
    input: item?.inputs ? JSON.parse(item?.inputs) : ''
  }
}

watch(
  () => props.formData,
  ({ botPluginList, botFlowBoList }) => {
    selectedPlugData.value = botPluginList?.map((item) => {
      return {
        ...item,
        name: item.orgName,
        plugins: item.pluginDetailList?.map((v) => {
          return {
            ...v,
            id: v.pluginId || v.id
          }
        })
      }
    })
    //工作流
    selectedWorkflowData.value = botFlowBoList
  }
)

defineExpose({
  submit: () => {
    const ids = selectedPlugData.value?.map((item) => item.plugins?.map((v: any) => v.id))
    return Promise.resolve({
      flowBaseId: selectedWorkflowData.value?.map((it) => it.id),
      pluginId: ids?.flat(Infinity)
    })
  }
})
</script>

<style scoped lang="scss">
.flow-workflow {
  padding-top: 20px;
}

.child-container {
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  padding: 10px 5px;
  font-size: 14px;
  line-break: 20px;
  color: rgba(0, 0, 0, 0.7);
  &:last-child {
    border: none;
  }
}
.btn-container {
  display: flex;
  justify-content: space-between;
  width: 300px;
  float: right;
}
</style>
