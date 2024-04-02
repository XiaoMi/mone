<template>
  <Header :flowInfo="flowBaseInfo" @editNameSuc="editNameSuc" @wheel.stop="handleWheel">
    <template #rBtns>
      <div>
        <el-button @click="runFn" type="primary" class="top-btn" :disabled="saveing || running">{{
          saveing ? '保存中' : '试运行'
        }}</el-button>
        <el-button
          @click="saveFn"
          type="primary"
          class="top-btn save-btn"
          :disabled="saveing || running"
          >保存</el-button
        >
      </div>
    </template>
  </Header>
  <div class="dndflow" @drop="onDropFn" @dragenter.prevent @dragover.prevent>
    <Sidebar
      @onDragStart="onDragStart"
      @addClick="addClickFn"
      @addPlugin="addPlugin"
      @wheel.stop="handleWheel"
    />
    <div class="work-flow" @wheel.stop>
      <VueFlow
        :nodes="nodes"
        :edges="lines"
        fit-view-on-init
        elevate-edges-on-select
        :min-zoom="0.2"
      >
        <DropzoneBackground />
        <template #node-custom="scope">
          <NodeItemNew
            :nodeItem="scope"
            :nodes="nodes"
            :ref="
              (el) => {
                setItemRef(el, scope.id)
              }
            "
            :disabled="running"
          />
        </template>
        <template #node-nodeif="scope">
          <NodeItemSon
            :nodeItem="scope"
            :nodes="nodes"
            :ref="
              (el) => {
                setItemRef(el, scope.id)
              }
            "
            :disabled="running"
          />
        </template>
        <template #node-nodeelse="scope">
          <NodeItemSon :nodeItem="scope" :nodes="nodes" />
        </template>
        <Controls position="bottom-right"> </Controls>
      </VueFlow>
    </div>

    <RunDrawer :nodes="nodes" v-model="showDrwer" @runStart="testStart" />
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import Sidebar from './components/Sidebar.vue'
import NodeItemNew from './work-flow/NodeItemNew.vue'
import { nodesBase, knowledgeTree } from './work-flow/baseInfo.js'
import { ElMessage } from 'element-plus'
import { saveFlow, getFlowDetail, getFlowStatus } from '@/api/workflow'
import RunDrawer from './work-flow/components/RunDrawer'
import { useRoute } from 'vue-router'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { VueFlow, MarkerType, useVueFlow } from '@vue-flow/core'
import DropzoneBackground from './work-flow/DropzoneBackground'
import { Controls } from '@vue-flow/controls'
import NodeItemSon from './work-flow/NodeItemSon'
import Header from './components/Header'

const { onConnect, addEdges, toObject, addNodes, updateNode, zoomTo, screenToFlowCoordinate } =
  useVueFlow()
const flowBaseInfo = ref(null)
const running = ref(false)
const nodes = ref([])

const zoomFn = () => {
  zoomTo(1)
}

const draggedType = ref('')
const showDrwer = ref(false)
const draggedInfo = ref(null)
const workFlowRef = ref({})
const conditionHeaderH = 67
const ifFistItemH = 66
const ifItemH = 42
// if title 的高度
const IfTittleH = 27
const ifPb = 5
const ifElseMargin = 10
const elseHeight = 47
const conditionPb = 10

const setItemRef = (el, id) => {
  const { nodes } = toObject()
  const curNode = nodes.filter((item) => item.id === id)[0]
  if (curNode?.nodeType == 'precondition' || !el) {
    workFlowRef.value[id] = null
  } else {
    workFlowRef.value[id] = el
  }
}
const nodeIFElse = ['nodeif', 'nodeelse']
// 拖拽结束
const onDropFn = (e) => {
  const position = screenToFlowCoordinate({
    x: e.clientX,
    y: e.clientY
  })
  addNodeFn(position, draggedType.value)
}

const addNodeFn = (position, type) => {
  const node = initBaseNode(position, type)
  addNodes(node)
  if (node.nodeType == 'precondition') {
    const ifElseNodes = createIFElse(node.id)
    addNodes(ifElseNodes)
  }
}
// 根据 parentId 创建if else节点 , 如果是新生成node不需要传elsePosition
const createIFElse = (nodeId, elseP = { x: 10, y: 180 }) => {
  const pNodeId = Number(nodeId)
  const ifId = `${pNodeId + 1}`
  const elseId = `${pNodeId + 2}`
  // 如果是条件选择器则需要在生成一个if的node 和一个else的node
  const ifNode = initBaseNode({ x: 10, y: conditionHeaderH }, 'nodeif')
  const elseNode = initBaseNode(elseP, 'nodeelse')
  const commonData = {
    // style: { zIndex: 0 },
    parentNode: `${pNodeId}`
  }
  const ifElseNodes = [
    { ...ifNode, id: ifId, elseId, ...commonData },
    { ...elseNode, id: elseId, ...commonData }
  ]
  return ifElseNodes
}
const startEndNodeTypes = ['begin', 'end']

// 基础样式和配置
const baseNodeInfo = (nodeType) => {
  const styleObj = nodeType == 'precondition' ? { style: { width: '600px', height: '240px' } } : {}
  return {
    type: nodeIFElse.indexOf(nodeType) > -1 ? nodeType : 'custom',
    ...styleObj,
    draggable: nodeIFElse.indexOf(nodeType) > -1 ? false : true,
    deletable: startEndNodeTypes.indexOf(nodeType) > -1 ? false : true
  }
}

const lines = ref([])

const initInputs = (nodeType) => {
  if (nodeType == 'plugin') {
    return {
      inputs: draggedInfo.value.meta?.input || []
    }
  } else if (nodeType == 'precondition') {
    return { inputs: [] }
  } else {
    return {
      inputs: [
        {
          name: nodeType == 'knowledge' ? '$$TY_KNOWLEDGE_QUERY$$' : '',
          type: 'reference'
        }
      ]
    }
  }
}
const initOutputs = (nodeType) => {
  if (nodeType == 'plugin') {
    return { outputs: draggedInfo.value.meta?.output || [] }
  } else if (nodeType == 'knowledge') {
    return { outputs: knowledgeTree }
  } else if (nodeType == 'precondition') {
    return {
      outputs: [
        {
          name: 'result',
          type: 'value',
          valueType: 'string'
        }
      ]
    }
  } else {
    return {
      outputs: [
        {
          name: '',
          type: 'string'
        }
      ]
    }
  }
}

// 点击插件
const addPlugin = (plugin) => {
  draggedInfo.value = plugin
  addClickFn('plugin')
}

const createNodeName = (nodeType) => {
  const { nodes } = toObject()
  const exsitSameTypeNodes = nodes.filter((item) => item.nodeType === nodeType)
  const isNodeP = nodeType === 'plugin' ? draggedInfo.value.name : nodesBase[nodeType].title
  const nodeName =
    exsitSameTypeNodes.length > 0 ? `${isNodeP}_${exsitSameTypeNodes.length}` : `${isNodeP}`
  return nodeName
}

const initLLmBatch = (nodeType) => {
  if (nodeType == 'llm') {
    return {
      batchInfo: {
        arr: [
          {
            name: '',
            type: 'reference'
          }
        ],
        $$TY_BATCH_MAX_TIMES$$: '1'
      },
      batchType: 'single'
    }
  } else {
    return {}
  }
}

const initBaseNode = (position, nodeType) => {
  // 将时间戳调小
  const timer = new Date().getTime() - mountedTimer.value
  const newNode = {
    id: `${timer}`,
    nodeType: nodeType,
    position,
    nodeMetaInfo: {
      nodeName: createNodeName(nodeType),
      plugin: draggedInfo.value
        ? {
            ...draggedInfo.value
          }
        : null
    },
    ...initInputs(nodeType),
    ...initOutputs(nodeType),
    coreSetting: {
      pluginId: draggedInfo.value?.id,
      temperature: 0.7,
      knowledgeBaseId: null,
      maxRecall: 3,
      minMatch: 50,
      gptModel: 'gpt4_1106_2',
      code: 'console.log("hello world")'
    },
    //  只有nodeif类型有conditions
    ...conditionsFn(nodeType),
    ...baseNodeInfo(nodeType),
    ...initLLmBatch(nodeType)
  }
  return newNode
}

const conditionsFn = (nodeType) => {
  if (nodeIFElse.indexOf(nodeType) > -1) {
    return {
      coreSetting: [
        {
          type: 'reference',
          operator: 'EQUALS'
        }
      ]
    }
  } else {
    return {}
  }
}

const onDragStart = ({ type, info }) => {
  draggedType.value = type
  draggedInfo.value = info
  document.addEventListener('drop', onDragEnd)
}
const addClickFn = (type) => {
  const randomX = Math.floor(Math.random() * 500 + 1)
  const randomY = Math.floor(Math.random() * 200 + 1)
  const position = { x: randomX, y: randomY }
  addNodeFn(position, type)
}
const onDragEnd = () => {
  draggedType.value = null
  document.removeEventListener('drop', onDragEnd)
}

// 插入start end
const initNodes = () => {
  const initNum = 60
  const startT = new Date().getTime() - mountedTimer.value
  const endWidth = nodesBase['end'].width
  // 修正因为拖拽导致的坐标偏差
  if (!nodes.value.find((item) => item.nodeType == 'begin')) {
    const nodeType = 'begin'
    nodes.value.push({
      id: `${startT}`,
      nodeType,
      position: {
        x: initNum,
        y: initNum
      },
      nodeMetaInfo: {
        nodeName: '开始'
      },
      inputs: [{ name: '', type: 'String', desc: '', required: true, activeNames: '1' }],
      ...baseNodeInfo(nodeType)
    })
  }
  if (!nodes.value.find((item) => item.nodeType == 'end')) {
    const wrapWH = getNodeWrapStyle()
    const endX = wrapWH.w - endWidth - initNum
    const nodeType = 'end'

    nodes.value.push({
      id: `${startT + 1}`,
      nodeType,
      position: {
        x: endX,
        y: initNum
      },
      coreSetting: {
        type: 'var',
        answerContent: ''
      },
      nodeMetaInfo: {
        nodeName: '结束'
      },
      outputs: [{ type: 'value' }],
      ...baseNodeInfo(nodeType)
    })
  }
}
const getNodeWrapStyle = () => {
  const wrap = document.querySelector('.work-flow')
  return {
    w: wrap?.offsetWidth,
    h: wrap?.offsetHeight
  }
}

const validate = async () => {
  try {
    const res = []
    for (let item in workFlowRef.value) {
      if (workFlowRef.value[item]) {
        // 当删除节点时候将原记得节点删掉
        res.push(await workFlowRef.value[item].validate())
      }
    }
    if (res.filter((item) => !item).length > 0) {
      return false
    }
    return true
  } catch (error) {
    return false
  }
}
const mapArr = (num, other, arr, str = '') => {
  const curOther = other[num]
  const curObj = arr.find((item) => item.name == curOther)
  if (curObj) {
    str = str + (str.length == 0 ? '' : '.') + curOther
    // other还没循环完
    if (other.length > num + 1) {
      str = str + (curObj.valueType?.startsWith('Array') ? '.0' : '')
      return mapArr(num + 1, other, curObj.children, str)
    } else {
      return str
    }
  } else {
    // 这种情况不会发生
    return str + curOther
  }
}

const getReferName = (outputs, other) => {
  const mapArrRes = mapArr(0, other, outputs)
  return mapArrRes
}
const transOutputs = (item) => {
  const outputs = item.outputs || []
  if (item.nodeType == 'knowledge') {
    return [
      {
        type: 'value',
        name: outputs[0].value,
        subName: outputs[0].children[0].value,
        valueType: outputs[0].desc
      }
    ]
  } else if (item.nodeType == 'end') {
    // 只有node的输出需要引用外面的输入
    const res = outputs.map((item) => {
      const { referenceInfo } = item
      if (!referenceInfo) {
        return item
      } else {
        const nodeId = referenceInfo[0]
        const { nodes } = toObject()
        const referNode = nodes.find((allNodeItem) => allNodeItem.id == nodeId)
        const refereOutputs = referNode?.outputs || []
        if (referNode && ['code', 'llm'].indexOf(referNode.nodeType) > -1) {
          // 如果引用的是code llm
          const [nodeId, ...other] = referenceInfo
          const referenceName = getReferName(refereOutputs, other)
          return {
            ...item,
            referenceName,
            referenceNodeId: nodeId
          }
        } else {
          const reference = referenceInfo
            ? {
                referenceNodeId: referenceInfo[0],
                referenceName: referenceInfo[1],
                referenceSubName: referenceInfo[2]
              }
            : {}
          return {
            ...item,
            ...reference
          }
        }
      }
    })
    return res
  } else if (item.nodeType == 'code') {
    // 将outputs子集转为字符串
    return outputs.map((item) => {
      return { ...item, schema: JSON.stringify(item.schema) }
    })
  } else if (item.nodeType == 'llm') {
    // 将outputs子集转为字符串
    return outputs.map((item) => {
      return { ...item, schema: JSON.stringify(item.children) }
    })
  } else {
    return outputs
  }
}
const returnCoreSetting = (item) => {
  if (['precondition'].indexOf(item.nodeType) < 0) {
    return { ...item.coreSetting, minMatch: item?.coreSetting?.minMatch / 100 || null }
  } else {
    const { nodes } = toObject()
    // 找到孩子if的coreSetting;
    const child = nodes.filter((fI) => {
      return fI.parentNode == item.id && fI.nodeType == 'nodeif'
    })
    if (child.length == 0) return []
    const { coreSetting } = child[0]
    const arr = coreSetting.map((coreItem) => {
      const { referenceInfo, referenceInfo2, type2 } = coreItem
      const name = (referenceInfo && referenceInfo[1]) || ''
      const name2 = type2 == 'reference' ? (referenceInfo2 && referenceInfo2[1]) || '' : ''
      const flowId2Obj = {}
      if (type2 == 'reference') {
        flowId2Obj.flowId2 = (referenceInfo2 && referenceInfo2[0]) || ''
      }
      const res = {
        ...coreItem,
        flowId: (referenceInfo && referenceInfo[0]) || '',
        referenceName: name,
        name,
        referenceName2: type2 == 'value' ? '' : name2,
        name2,
        ...flowId2Obj
      }
      if (type2 == 'value') {
        delete res.flowId2
      }
      return res
    })
    return arr
  }
}

let initNum = 0

watch(
  () => toObject(),
  (val, oldV) => {
    if (initNum == 0) {
      initNum = initNum + 1
      return
    } else {
      initNum = initNum + 1
    }
    nodesChangeFn()
  },
  {
    deep: true,
    immediate: false
  }
)

let changeTimer = ref(null)
const nodesChangeFn = () => {
  if (running.value) return // 如果实在运行则不需要保存
  if (!getDetailed.value) return
  saveing.value = true
  if (changeTimer.value) {
    clearTimeout(changeTimer.value)
    changeTimer.value = null
  }
  changeTimer.value = setTimeout(() => {
    saveFn()
    clearTimeout(changeTimer.value)
  }, 800)
}

const batchInfoReq = (node) => {
  const { nodeType, batchType, batchInfo } = node
  if (nodeType == 'llm' && batchType == 'batch' && batchInfo) {
    const { $$TY_BATCH_MAX_TIMES$$ } = batchInfo
    const batchTemp = batchInfo.arr || []
    const btachMap = batchTemp.map((item) => {
      if (item.type == 'reference' && item.referenceInfo?.length > 0) {
        const [referNodeId, ...other] = item.referenceInfo
        const { nodes } = toObject()
        const referNode = nodes.find((node) => node.id == referNodeId)
        const refereInputs = referNode?.outputs || []
        const referenceName = getReferName(refereInputs, other)
        return {
          ...item,
          referenceName
        }
      } else {
        return item
      }
    })
    const batchArr = [
      ...btachMap,
      {
        name: '$$TY_BATCH_MAX_TIMES$$',
        type: 'value',
        value: $$TY_BATCH_MAX_TIMES$$ + ''
      }
    ]
    return {
      batchInfo: batchArr
    }
  } else {
    return {
      batchInfo: [
        {},
        {
          name: '$$TY_BATCH_MAX_TIMES$$',
          type: 'value',
          value: '1'
        }
      ]
    }
  }
}
const transInputs = (nodeItem) => {
  if (['llm', 'code'].indexOf(nodeItem.nodeType) < 0) {
    return nodeItem.inputs
  } else {
    const inputs = nodeItem.inputs || []

    const newInputs = inputs.map((input) => {
      if (input.type == 'reference') {
        if (input.referenceInfo) {
          const { referenceInfo } = input
          const referNodeId = referenceInfo[0]
          const { nodes } = toObject()
          //  引用node;
          const referNode = nodes.find((allNodeItem) => allNodeItem.id == referNodeId)
          let refereInputs = referNode?.outputs || []
          if (referNode && ['code', 'llm'].indexOf(referNode.nodeType) > -1) {
            // 如果引用的是code llm
            const [nodeId, ...other] = referenceInfo
            let referenceName
            if (referNodeId == nodeItem.id) {
              referenceName = other[0]
            } else {
              referenceName = getReferName(refereInputs, other)
            }
            const typeObj = input.referenceInfo[0] == nodeItem.id ? { type: 'batch' } : {}
            return {
              ...input,
              referenceName,
              referenceNodeId: nodeId,
              ...typeObj
            }
          } else {
            return input
          }
        } else {
          return input
        }
      } else {
        return input
      }
    })
    return newInputs
  }
}

const saveing = ref(false) // 正在保存
const saveFn = async () => {
  const dataObj = toObject()
  const { edges, nodes } = dataObj
  // 除了 condition  if else 节点的数据
  const nodesVal = nodes.map((item) => {
    const p = {
      ...item.nodeMetaInfo,
      source: item.nodeMetaInfo.sourceNodeId,
      target: item.nodeMetaInfo.targetNodeId
    }
    item.nodeMetaInfo.extraInfo = JSON.stringify(p)
    item.nodeMetaInfo.nodePosition = item.position
    const newCoreSetting = returnCoreSetting(item)
    return {
      ...item,
      coreSetting: JSON.stringify(newCoreSetting),
      outputs: transOutputs(item),
      inputs: transInputs(item),
      ...batchInfoReq(item)
    }
  })
  const edgesVal = edges.map((item) => {
    let sourceNodeId = item.source
    if (item.conditionFlag) {
      const nodeIfId = item.source
      const nodeIf = nodes.find((n) => n.id === nodeIfId)
      sourceNodeId = nodeIf.parentNode
    }
    return {
      ...item,
      sourceNodeId,
      targetNodeId: item.target
    }
  })

  // 过滤掉 if else 节点
  const filterIfElse = nodesVal.filter((item) => {
    return item.nodeType != 'nodeif' && item.nodeType != 'nodeelse'
  })
  const p = {
    flowBaseInfo: flowBaseInfo.value,
    flowSettingInfo: {
      nodes: filterIfElse,
      flowBaseId: flowBaseInfo.value.id,
      id: flowBaseInfo.value.id,
      edges: edgesVal
    }
  }
  saveFlow(p)
    .then((res) => {
      const { code, message } = res
      if (code != 0) {
        ElMessage.error(message)
      }
    })
    .finally(() => {
      saveing.value = false
    })
}

// 编辑回显处理数据
const editReShowOutputs = (node) => {
  if (node.nodeType == 'knowledge') {
    let outputs = knowledgeTree
    return outputs
  } else if (['code', 'llm'].indexOf(node.nodeType) > -1) {
    try {
      return node.outputs.map((item) => {
        return {
          ...item,
          schema: item.schema ? JSON.parse(item.schema) : [],
          children: item.schema ? JSON.parse(item.schema) : []
        }
      })
    } catch (error) {
      console.log('error', error)
      return node.outputs
    }
  } else return node.outputs
}

const route = useRoute()

// 根据详情生成if else节点
const getIfElseNodes = (allNodes) => {
  const conditions = allNodes.filter((item) => item.nodeType == 'precondition')
  let resNodes = []
  conditions.forEach((item) => {
    const { id, coreSetting } = item
    const cs = JSON.parse(coreSetting)
    const ifAllH = cs.length > 1 ? ifFistItemH + (cs.length - 1) * ifItemH : ifFistItemH
    const elseTop = conditionHeaderH + (ifAllH + IfTittleH + ifPb * 2) + ifElseMargin
    const ifElseNodes = createIFElse(id, {
      x: 10,
      y: elseTop
    })
    resNodes = [...resNodes, ...ifElseNodes]
  })
  const res = resNodes.map((item) => {
    if (item.nodeType == 'nodeif') {
      const parentNode = allNodes.find((node) => node.id == item.parentNode)
      const coreSetting = parentNode?.coreSetting && JSON.parse(parentNode?.coreSetting)
      item.coreSetting = coreSetting
      return item
    } else {
      return item
    }
  })
  return res
}
// 如果node是precondition 则需要设置height
const returnStyle = (node) => {
  if (node.nodeType != 'precondition') {
    return {}
  } else {
    const cs = JSON.parse(node.coreSetting)
    const ifAllH = cs.length > 1 ? ifFistItemH + (cs.length - 1) * ifItemH : ifFistItemH
    const elseTop = conditionHeaderH + (ifAllH + IfTittleH + ifPb * 2) + ifElseMargin
    return {
      style: {
        height: elseTop + elseHeight + conditionPb + 'px'
      }
    }
  }
}
const editNameSuc = () => {
  // 更新基本信息
  const detailId = route.params.id
  getFlowDetail(detailId).then(({ code, data }) => {
    if (code === 0 && data) {
      flowBaseInfo.value = data.flowBaseInfo
    }
  })
}

// batch 回显
const returnBatchInfo = (node) => {
  const { nodeType, batchType } = node
  if (nodeType == 'llm') {
    if (batchType == 'batch') {
      const { batchInfo } = node
      const l = batchInfo.length
      return {
        batchInfo: {
          arr: batchInfo.slice(0, l - 1),
          $$TY_BATCH_MAX_TIMES$$: batchInfo[l - 1].value
        }
      }
    } else {
      return {
        batchInfo: {
          arr: [
            {
              name: '',
              type: 'reference'
            }
          ],
          $$TY_BATCH_MAX_TIMES$$: '1'
        },
        batchType: 'single'
      }
    }
  } else {
    return {}
  }
}
const editReInputs = (node) => {
  const { inputs } = node
  if (node.nodeType == 'llm' && node.batchType == 'batch') {
    const newRes = inputs.map((item) => {
      const { referenceInfo } = item
      if (referenceInfo[0] == node.id) {
        return {
          ...item,
          type: 'reference'
        }
      }
      return item
    })
    return newRes
  } else {
    return inputs
  }
}

const getDetailed = ref(false)
// 获取详情
const getDetail = () => {
  const detailId = route.params.id
  getFlowDetail(detailId)
    .then(({ code, data }) => {
      if (code === 0 && data) {
        flowBaseInfo.value = data.flowBaseInfo
        const resNodes = data.flowSettingInfo?.nodes || []
        const ifElseNodes = getIfElseNodes(resNodes)
        const nodesV = resNodes.map((it) => {
          const sets = it.coreSetting ? JSON.parse(it.coreSetting) : {}
          const anserModel = it.nodeType == 'end' && !sets.type ? { type: 'var' } : {}
          const transSets = {
            ...sets,
            minMatch: sets.minMatch * 100,
            ...anserModel
          }
          return {
            ...it,
            coreSetting: transSets,
            nodeMetaInfo: JSON.parse(it.nodeMetaInfo.extraInfo),
            outputs: editReShowOutputs(it),
            inputs: editReInputs(it),
            position: it.nodeMetaInfo.nodePosition,
            id: `${it.id}`,
            ...baseNodeInfo(it.nodeType),
            ...returnStyle(it),
            ...returnBatchInfo(it)
          }
        })
        const resLines = data.flowSettingInfo?.edges || []
        const resLinesArrr = resLines.map((item, i) => {
          let source = item.sourceNodeId

          // 是if 或者else
          if (item.conditionFlag == 'if') {
            const ifNode = ifElseNodes.find(
              (ifElseItem) => ifElseItem.parentNode == source && ifElseItem.nodeType == 'nodeif'
            )
            source = ifNode?.id
          } else if (item.conditionFlag == 'else') {
            const elseNode = ifElseNodes.find(
              (ifElseItem) => ifElseItem.parentNode == source && ifElseItem.nodeType == 'nodeelse'
            )
            source = elseNode?.id
          }
          // console.log('source', source)
          return {
            ...item,
            target: `${item.targetNodeId}`,
            source: `${source}`,
            id: `edge_${i}`,
            markerEnd: MarkerType.ArrowClosed
          }
        })
        nodes.value = [...nodesV, ...ifElseNodes]
        nextTick(() => {
          lines.value = resLinesArrr
        })
      } else {
        nodes.value = []
        lines.value = []
      }
      initNodes()
      nextTick(() => {
        getDetailed.value = true
      })
    })
    .finally(() => {
      nextTick(() => {
        getDetailed.value = true
      })
    })
}

const switchShowDrawer = () => {
  showDrwer.value = !showDrwer.value
}
const resetNodeTestResult = () => {
  const { nodes } = toObject()
  const anyNodeHasRes = nodes.find((item) => item.resOutputs?.status)
  const llmBatchs = nodes.filter((item) => item.nodeType == 'llm' && item.batchType == 'batch')
  // 保存上一次批处理最大次数
  llmBatchs.forEach((item) => {
    const maxNum = item.batchInfo.$$TY_BATCH_MAX_TIMES$$ || 1
    updateNode(item.id, { resInputs: {}, resOutputs: {}, maxNum })
  })
  if (!anyNodeHasRes) return
  // 如果有节点有结果就清空
  nodes.forEach((item) => {
    updateNode(item.id, { resInputs: {}, resOutputs: {} })
  })
}
const runFn = async () => {
  const validateRes = await validate()
  if (!validateRes) return
  resetNodeTestResult()
  switchShowDrawer()
}
// node状态还是非终态
const isUnFinished = (nodeId) => {
  // 2 '运行成功' 3 '运行失败'
  const endsCode = [2, 3]
  const { nodes } = toObject()
  const node = nodes.find((item) => item.id == nodeId)
  return !(node.resOutputs?.status && endsCode.indexOf(node.resOutputs?.status) > -1)
}
const getStream = (flowRecordId) => {
  running.value = true
  const formData = new FormData()
  formData.append('flowRecordId', flowRecordId)
  fetchEventSource(
    `//${window.location.host + import.meta.env.VITE_GLOB_API_NEW_URL}v1/flow/flowStatus/stream`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      //请求体，用于给后台的数据
      body: JSON.stringify({
        flowRecordId: flowRecordId
      }),
      openWhenHidden: true,
      onmessage(res) {
        try {
          console.log('res>>>>>', res)
          if (res.event == 'end') {
            running.value = false
          }
          const resData = JSON.parse(res.data)
          console.log('resData>>>>>>>>>', resData)
          let { nodeInputsMap, nodeOutputsMap, endFlowOutput } = resData
          const { nodes } = toObject()
          const endNodesId = nodes.filter((item) => {
            return item.nodeType == 'end'
          })[0]?.id
          const inputsArr = Object.keys(nodeInputsMap)
          inputsArr.map((item) => {
            // 非终态的才需要更新
            if (isUnFinished(item)) {
              updateNode(item, { resInputs: nodeInputsMap[item] })
            }
          })

          const outputsArr = Object.keys(nodeOutputsMap)
          outputsArr.map((item) => {
            let obj = {}
            if (endNodesId == item && endFlowOutput) {
              obj = {
                ...nodeOutputsMap[item],
                outputDetails: endFlowOutput.endFlowOutputDetails,
                answerContent: endFlowOutput.answerContent
              }
            } else {
              obj = nodeOutputsMap[item]
            }
            // 非终态的才需要更新
            if (isUnFinished(item)) {
              updateNode(item, {
                resOutputs: obj
              })
            }
          })
        } catch (err) {
          console.log('err', err)
        }
      },
      onerror(event) {
        running.value = false

        // 服务异常
        console.log('服务异常', event)
      },
      onclose(e) {
        // 服务关闭
        running.value = false
        console.log('服务关闭', e)
      }
    }
  )
}
const testStart = (flowRecordId) => {
  switchShowDrawer()
  getStream(flowRecordId)
}

const testFn = () => {
  const obj = toObject()
  console.log('obj', obj)
}

const getLineCondtionF = (connection) => {
  const { nodes } = toObject()
  const sourceNode = nodes.find((item) => item.id == connection.source)
  if (sourceNode.nodeType == 'nodeif') {
    return { conditionFlag: 'if' }
  } else if (sourceNode.nodeType == 'nodeelse') {
    return { conditionFlag: 'else' }
  }
  return {}
}
onConnect((connection) => {
  const otherP = getLineCondtionF(connection)
  const timer = new Date().getTime() - mountedTimer.value
  addEdges({ ...connection, ...otherP, markerEnd: MarkerType.ArrowClosed, id: `edge_${timer}` })
})

const handleWheel = (event) => {
  if (event.ctrlKey) {
    event.preventDefault()
    return
  }
}

const mountedTimer = ref()
onMounted(() => {
  mountedTimer.value = new Date().getTime()
  getDetail()
})
onUnmounted(() => {})
</script>
<style>
/* import the necessary styles for Vue Flow to work */
@import '@vue-flow/core/dist/style.css';

/* import the default theme, this is optional but generally recommended */
@import '@vue-flow/core/dist/theme-default.css';
/*  import default controls styles */
@import '@vue-flow/controls/dist/style.css';
</style>
<style lang="scss" scoped>
.vue-flow__minimap {
  transform: scale(75%);
  transform-origin: bottom right;
}

.dndflow {
  position: relative;
  flex-direction: column;
  display: flex;
  height: calc(100vh - 72px);
  background: #f1f1f4;
  :deep(.vue-flow__node-default) {
    width: auto;
  }
  :deep(.vue-flow__node-custom) {
    background: #fff;
    border-radius: 7px;
    border: solid 2px #d8d8d8;
  }
  :deep(.vue-flow__node-nodeif),
  :deep(.vue-flow__node-custom),
  :deep(.vue-flow__node-nodeelse) {
    .vue-flow__handle {
      background: #4d53e8 !important;
      filter: none !important;
      border: solid 1px #fff;
      width: 10px;
      height: 10px;
      &:hover {
        width: 16px;
        height: 16px;
      }
    }
  }

  :deep(.vue-flow__node-custom.selected) {
    border: solid 2px #4d53e8;
  }
  :deep(.vue-flow__arrowhead polyline) {
    fill: #4d53e8 !important;
    stroke: #4d53e8 !important;
  }
  :deep(.vue-flow__edge-path) {
    stroke: #4d53e8;
    stroke-width: 1.3;
  }
  :deep(.vue-flow__edge.selected .vue-flow__edge-path) {
    stroke: #37d0ff;
  }
  :deep(.vue-flow__controls) {
    display: flex;
    padding: 3px 8px;
    border-radius: 8px;
    background: #fff;
    .vue-flow__controls-button {
      border-bottom: none;
      position: relative;
    }
    .vue-flow__controls-button + .vue-flow__controls-button {
      margin-left: 5px;
      &::before {
        content: '';
        position: absolute;
        left: -3px;
        top: 4px;
        width: 1px;
        height: 18px;
        background: #eee;
      }
    }
  }
}
.dndflow aside .description {
  margin-bottom: 10px;
}
.dndflow .work-flow {
  flex-grow: 1;
  height: 100%;
  overflow: scroll;
}
@media screen and (min-width: 640px) {
  .dndflow {
    flex-direction: row;
  }
  .dndflow aside {
    min-width: 25%;
  }
}
.top-btn {
  padding: 4px 20px;
  font-size: 14px;
}
</style>
