<template>
  <div class="content-wrap" :style="{ width: nodesBase[nodeData?.nodeType]?.width + 'px' }">
    <div class="com-box">
      <StatusDiv
        :resInputs="nodeData?.resInputs"
        :resOutputs="nodeData?.resOutputs"
        v-if="showStatus"
        :nodeData="nodeData"
        :nodes="nodes"
        :hasPermission="hasPermission"
        @retryFn="emits('retryNode', nodeData)"
      />
      <div class="inner-box">
        <div class="inner-top-container">
          <div class="top-box">
            <div class="top-p">
              <NodeHeaderImg :nodeData="nodeData" />
              <div class="title">
                <i>({{ nodeData.id }}){{ nodeData.nodeMetaInfo?.nodeName }}</i>
                <FlowId
                  :flowRecordIdStatus="`${nodeData.coreSetting.flowId}`"
                  v-if="nodeData.nodeType == 'subFlow'"
                  keyName="flowId"
                />
              </div>
            </div>
            <div class="top-r">
              <TestNodeBtn
                @click="testNode"
                v-if="showTestNodeTypes.includes(nodeData.nodeType)"
                :disabled="disabled"
              />
              <NodeDrop :nodeData="nodeData" :disabled="disabled" :nodes="nodes" />
            </div>
          </div>
          <DescAndUpdate v-model="nodeData">
            <Desc :desc="flowPluginDesc || nodesBase[nodeData.nodeType]?.desc">
              <PluginDetail
                :nodeData="nodeData"
                v-if="nodeData.nodeType == 'plugin' && nodeData.coreSetting.pluginType == 'dubbo'"
              />
            </Desc>
          </DescAndUpdate>
        </div>
        <!-- 阻止子元素 mousedown 冒泡 @mousedown.stop -->
        <!-- @mousedown.stop
          @keydown.stop -->
        <NodeBox :hasPermission="hasPermission" :nodeType="nodeData?.nodeType">
          <component
            :is="dom[nodeData?.nodeType]"
            v-model="nodeData"
            :nodes="nodes"
            :lines="lines"
            ref="comRef"
            :disabled="disabled"
            :workspaceId="workspaceId"
            :getDetailed="getDetailed"
            :referOps="referOpsObj?.ops || []"
            :referBatchOps="referOpsObj?.referBatchOps || []"
            v-bind="componentProps"
          />
        </NodeBox>
      </div>
      <Handle
        type="target"
        :position="Position.Left"
        :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
        v-if="hideLNodeTypes.includes(nodeData.nodeType) ? false : true"
      />
      <Handle
        type="source"
        :position="Position.Right"
        v-if="hideRNodeTypes.includes(nodeData.nodeType) ? false : true"
        :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
      />
      <template v-if="nodeData?.nodeType == 'intentRecognition'">
        <Handle
          type="source"
          v-for="(item, index) in nodeData.handles"
          :position="item.position"
          :key="item.key"
          :id="item.id"
          :style="{ top: `${302 + 42 * index}px` }"
          :class="showStatus ? 'show-status-class' : ''"
        />
      </template>
    </div>
  </div>
</template>

<script setup>
import { shallowReactive, ref, onMounted, computed, defineExpose, watch } from 'vue'
import Desc from './components/Desc.vue'
import LLM from './components/LLM.vue'
import LLMPdf from './components/LLMPdf.vue'
import Start from './components/Start.vue'
import End from './components/End.vue'
import Code from './components/Code.vue'
import Knowledge from './components/Knowledge'
import DataBase from './components/Database'
import Condition from './components/Condition'
import ConditionNew from './components/ConditionNew.vue'
import SubFlow from './components/SubFlow'
// 意图识别
import Intent from './components/Intent.vue'
import CustomConfirm from './components/CustomConfirm'
import {
  nodesBase,
  getRalNodes,
  getNotRalNodes,
  initOpsByNodeList,
  filterNodesByReference,
  retNodeReferArr
} from './baseInfo.js'
import { filterNodeTypes, conditionTypes } from '../common/base.js'
import Plugin from './components/Plugin.vue'
import StatusDiv from './components/StatusDiv'
import NodeDrop from './components/NodeDrop.vue'
import { useVueFlow, Position, useNode, Handle } from '@vue-flow/core'
import TestNodeBtn from './components/TestNodeBtn.vue'
import { ElMessage } from 'element-plus'
import { useWfStore } from '@/stores/workflow1'
import NodeBox from '@/views/workflow/work-flow/components/NodeBox.vue'
import PluginDetail from './components/components/PluginDetail.vue'
import NodeHeaderImg from './components/NodeHeaderImg.vue'
import { getFlowDetail } from '@/api/workflow'
import { getBotPluginParameter } from '@/api/probot'
import FlowId from '../components/components/FlowRecordId.vue'
import DescAndUpdate from './components/DescAndUpdate'

const { updateNode, toObject } = useVueFlow()

const nodeData = ref({})
const comRef = ref(null)
const emits = defineEmits([
  'addLine',
  'moveNodeFn',
  'deleteNode',
  'changeActive',
  'deleteLine',
  'testNode',
  'retryNode'
])
// 遍历nodesBase对象的每一个属性的值val，如果val.showRun是true,拿到val.type推入数组arr中,返回这个arr;
const showTestNodeTypes = computed(() => {
  return filterNodeTypes(nodesBase, 'showRun')
})

const hideLNodeTypes = computed(() => {
  return filterNodeTypes(nodesBase, 'hideLeft')
})

const hideRNodeTypes = computed(() => {
  return filterNodeTypes(nodesBase, 'hideRight')
})

const componentProps = computed(() => {
  if (nodeData.value.nodeType == 'manualConfirm') {
    return { isRetry: props.isRetry }
  } else {
    return {}
  }
})

const props = defineProps({
  lines: {
    default: []
  },
  activeNodeId: {},
  activeLineId: {},
  nodeItem: {},
  nodes: {},
  disabled: {},
  workspaceId: {},
  getDetailed: {},
  hasPermission: {},
  isRetry: {
    default: false
  }
})

const flowPluginDesc = ref('')
const initNodeData = async () => {
  const nodeD = useNode()
  nodeData.value = nodeD.node
  flowPluginDesc.value = nodeData.value.data?.desc
  // flow节点没有头像，从详情获取
  if (nodeData.value.nodeType == 'subFlow' && !nodeData.value.data.avatarUrl) {
    const { flowId } = nodeData.value.coreSetting
    const { data } = await getFlowDetail(flowId)
    const { avatarUrl, desc } = data.flowBaseInfo
    nodeData.value.data.avatarUrl = avatarUrl
    flowPluginDesc.value = desc
  }
  // 获取插件的描述
  if (nodeData.value.nodeType == 'plugin' && !nodeData.value.nodeMetaInfo?.plugin?.desc) {
    const { pluginId } = nodeData.value.coreSetting
    const { code, data } = await getBotPluginParameter({ id: pluginId })
    if (code == 0) {
      flowPluginDesc.value = data?.botPluginMeta?.desc
    }
  }
}

const wfStore = useWfStore()
const { setNodesPreNodes } = wfStore
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)
const referOpsObj = computed(() => {
  const cur = wfStore.nodesPreNodes.find((item) => item.nodeId == nodeData.value.id)
  return cur
})

const dom = shallowReactive({
  llm: LLM,
  code: Code,
  knowledge: Knowledge,
  precondition: Condition,
  newPrecondition: ConditionNew,
  begin: Start,
  end: End,
  plugin: Plugin,
  database: DataBase,
  manualConfirm: CustomConfirm,
  llmImageUnderstand: LLM,
  llmFileUnderstand: LLM,
  intentRecognition: Intent,
  subFlow: SubFlow
})

const showStatus = computed(() => {
  return nodeData.value?.resOutputs?.status
})
const editConditionH = (isAdd = true) => {
  const preH = nodeData.value.style.height
  const preStyleNum = preH.replace('px', '')
  const newHeight = isAdd ? Number(preStyleNum) + 46 : Number(preStyleNum) - 46
  updateNode(nodeData.value.id, {
    style: {
      height: `${newHeight}px`
    }
  })
}
const editIfElseTop = (isAdd) => {
  const { nodes } = toObject()
  const toChanges = nodes.filter((it) => it.parentNode == nodeData.value.id)
  if (toChanges.length == 0) return
  toChanges.forEach((nodeI) => {
    const preP = nodeI.position
    const newY = isAdd ? preP.y + 46 : preP.y - 46
    updateNode(nodeI.id, {
      position: {
        x: preP.x,
        y: newY
      }
    })
  })
}
// 修改if else的高度
watch(
  () => nodeData.value?.resOutputs?.status,
  (val, oldV) => {
    if (conditionTypes.includes(nodeData.value.nodeType)) {
      if (!!val != !!oldV) {
        // 展示status
        editConditionH(!!val)
        editIfElseTop(!!val)
      }
    }
  },
  {
    immediate: true
  }
)
const testNode = async () => {
  const valid = await validate()
  if (valid) {
    emits('testNode', nodeData.value)
  } else {
    ElMessage({
      message: '有校验失败的节点，请检查配置好后，再测试该节点',
      type: 'error'
    })
  }
}

onMounted(() => {
  initNodeData()
})
const validate = async () => {
  try {
    return await comRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.nodes-wrap {
  position: relative;
  .active-node {
    border-color: #4d53e8;
    z-index: 10;
  }
}
.top-box {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
}
.top-p {
  width: 100%;
  display: flex;
  align-items: center;
  .title {
    flex: 1;
    display: flex;
    font-size: 14px;
    color: #1c1f23;
    font-weight: 500;
    vertical-align: top;
    margin-left: 4px;
  }
}

.spot {
  position: absolute;
  background: #4d53e8;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: solid 1px #fff;
  cursor: crosshair;
  transition: all 500ms linear;
  &:hover {
    transform: scale(1.8);
    transition: all 300ms linear;
  }
}
.left-spot {
  left: -5px;
  top: calc(50% - 5px);
}
.right-spot {
  right: -5px;
  top: calc(50% - 5px);
}
.com-box {
  :deep(.oz-collapse) {
    border: none;
    border-radius: 8px;
    overflow: hidden;
  }
  :deep(.oz-collapse-item__wrap) {
    background-color: rgba(46, 46, 56, 0.04);
    padding: 0 5px;
  }
  :deep(.oz-collapse-item__content) {
    padding-bottom: 0;
  }
  :deep(.oz-collapse-item__header) {
    padding: 0 5px;
    height: 35px;
    background-color: rgba(46, 46, 56, 0.04);
    line-height: 35px;
  }
  :deep(.oz-form-item) {
    margin-right: 5px;
  }
  :deep(.m-r-0) {
    margin-right: 0;
  }
  :deep(.empty-item) {
    padding-top: 24px;
  }
  :deep(.empty-label) {
    padding-top: 0;
  }
  :deep(.oz-form-item__label) {
    font-weight: 600;
  }
  :deep(.oz-collapse-item__arrow) {
    display: none;
  }
  :deep(.t-box) {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  :deep(.oz-collapse + .oz-collapse) {
    margin-top: 10px;
  }
  :deep(.empty-form-item) {
    padding-top: 24px;
  }
}
.inner-box {
  padding: 0 10px 10px;
}
.top-r {
  display: flex;
  align-items: center;
}
.show-status-class {
  margin-top: 46px;
}
</style>
