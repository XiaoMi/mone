<template>
  <div class="content-wrap" :style="{ width: nodesBase[nodeData?.nodeType]?.width + 'px' }">
    <div class="com-box">
      <StatusDiv
        :resInputs="nodeData?.resInputs"
        :resOutputs="nodeData?.resOutputs"
        v-if="showStatus"
        :nodeData="nodeData"
      />
      <div class="inner-box">
        <div class="inner-top-container">
          <div class="top-box">
            <div class="top-p">
              <img :src="nodesBase[nodeData.nodeType]?.imgSrc" alt="" class="header" />
              <span class="title">{{
                nodeData.nodeType === 'plugin'
                  ? nodeData.nodeMetaInfo?.plugin?.name
                  : nodeData.nodeMetaInfo?.nodeName
              }}</span>
            </div>
            <NodeDrop :nodeData="nodeData" />
          </div>
          <Desc
            v-if="nodeData.nodeType === 'plugin'"
            :desc="nodeData.nodeMetaInfo?.plugin?.desc || '没有插件相关描述'"
          />
          <Desc v-else :desc="nodesBase[nodeData.nodeType]?.desc" />
        </div>
        <!-- 阻止子元素 mousedown 冒泡 @mousedown.stop -->
        <component
          :is="dom[nodeData?.nodeType]"
          v-model="nodeData"
          :nodes="nodes"
          :lines="lines"
          ref="comRef"
          @mousedown.stop
          @keydown.stop
          :disabled="disabled"
        />
      </div>
      <Handle
        type="target"
        :position="Position.Left"
        :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
        v-if="['begin'].indexOf(nodeData.nodeType) > -1 ? false : true"
      />
      <Handle
        type="source"
        :position="Position.Right"
        v-if="['end', 'precondition'].indexOf(nodeData.nodeType) > -1 ? false : true"
        :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
      />
    </div>
  </div>
</template>

<script setup>
import { shallowReactive, ref, onMounted, computed, defineExpose, watch } from 'vue'
import Desc from './components/Desc.vue'
import LLM from './components/LLM.vue'
import Start from './components/Start.vue'
import End from './components/End.vue'
import Code from './components/Code.vue'
import Knowledge from './components/Knowledge'
import Condition from './components/Condition'
import { nodesBase } from './baseInfo.js'
import Plugin from './components/Plugin.vue'
import StatusDiv from './components/StatusDiv'
import NodeDrop from './components/NodeDrop.vue'
import { useVueFlow, Position, useNode, Handle } from '@vue-flow/core'

const { updateNode, toObject } = useVueFlow()

const nodeData = ref({})
const comRef = ref(null)
const emits = defineEmits(['addLine', 'moveNodeFn', 'deleteNode', 'changeActive', 'deleteLine'])
const props = defineProps({
  lines: {
    default: []
  },
  activeNodeId: {},
  activeLineId: {},
  nodeItem: {},
  nodes: {},
  disabled: {}
})
const drawingLine = ref({})
const initNodeData = () => {
  const nodeD = useNode()
  nodeData.value = nodeD.node
}

const dom = shallowReactive({
  llm: LLM,
  code: Code,
  knowledge: Knowledge,
  precondition: Condition,
  begin: Start,
  end: End,
  plugin: Plugin
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
const editIfElseTop = (nodeType, isAdd) => {
  const { nodes } = toObject()
  const nodeI = nodes.find((it) => it.parentNode == nodeData.value.id && it.nodeType == nodeType)
  if (!nodeI) return
  const preP = nodeI.position
  const newY = isAdd ? preP.y + 46 : preP.y - 46
  updateNode(nodeI.id, {
    position: {
      x: preP.x,
      y: newY
    }
  })
}
watch(
  () => nodeData.value?.resOutputs?.status,
  (val, oldV) => {
    if (nodeData.value.nodeType == 'precondition') {
      if (!!val != !!oldV) {
        // 展示status
        editConditionH(!!val)
        editIfElseTop('nodeif', !!val)
        editIfElseTop('nodeelse', !!val)
      }
    }
  },
  {
    immediate: true
  }
)

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
    font-size: 14px;
    color: #1c1f23;
    font-weight: 500;
    vertical-align: top;
    margin-left: 4px;
  }
  .header {
    width: 16px;
    height: 16px;
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
</style>
