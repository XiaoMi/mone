<template>
  <Header
    :flowInfo="flowBaseInfo"
    @editNameSuc="editNameSuc"
    @wheel.stop="handleWheel"
    :streamTimer="streamTimer"
    :flowStatus="flowStatus"
    :hasPermission="hasPermission"
    :saveing="saveing"
    :runOrTest="runOrTest"
    :getDetailed="getDetailed"
    :reDrawing="reDrawing"
    @reDraw="reDraw"
    @clickRun="clickRun"
    @stopRun="stopRun"
    @saveFn="saveClick"
    :flowRecordIdStatus="flowRecordIdStatus"
    :testType="testType"
  >
  </Header>
  <div
    class="dndflow"
    @drop="onDropFn"
    @dragenter.prevent
    @dragover.prevent
    v-loading="flowTesting || reDrawing"
    element-loading-background="rgba(0, 0, 0, 0.1)"
    v-if="getDetailed"
  >
    <BaseFold @changeFlod="fitFn">
      <template #left>
        <Sidebar
          @onDragStart="onDragStart"
          @addClick="addClickFn"
          @addPluginFlow="addPluginFlow"
          @wheel.stop="handleWheel"
          v-if="hasPermission"
        />
      </template>
      <template #main>
        <div class="work-flow" @wheel.stop>
          <VueFlow
            :nodes="nodes"
            :edges="lines"
            fit-view-on-init
            elevate-edges-on-select
            :min-zoom="0.1"
            @viewportChange="viewportChangeFn"
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
                :disabled="runOrTest || !hasPermission"
                :workspaceId="flowBaseInfo?.workSpaceId"
                :getDetailed="getDetailed"
                @testNode="testNode"
                @retryNode="retryNode"
                :hasPermission="hasPermission"
                :isRetry="retryInfo?.isRetry == true ? true : false"
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
                :disabled="runOrTest"
                :getDetailed="getDetailed"
                :hasPermission="hasPermission"
              />
            </template>
            <template #node-nodeelse="scope">
              <NodeItemSon :nodeItem="scope" :nodes="nodes" :hasPermission="hasPermission" />
            </template>
            <template #edge-button="buttonEdgeProps">
              <EdgeWithButton
                :id="buttonEdgeProps.id"
                :source-x="buttonEdgeProps.sourceX"
                :source-y="buttonEdgeProps.sourceY"
                :target-x="buttonEdgeProps.targetX"
                :target-y="buttonEdgeProps.targetY"
                :source-position="buttonEdgeProps.sourcePosition"
                :target-position="buttonEdgeProps.targetPosition"
                :marker-end="buttonEdgeProps.markerEnd"
                :style="buttonEdgeProps.style"
                :showBtn="buttonEdgeProps.data.showBtn"
                @clickEdgeBtn="clickEdgeBtn"
              />
            </template>
            <Controls position="bottom-right" :showInteractive="false">
              <el-tooltip effect="dark" content="执行中的节点自动居中" placement="top">
                <ControlButton title="执行中的节点自动居中" @click="changeCenter">
                  <i class="iconfont icon-juzhong" :class="nodeCentering ? 'active-btn' : ''"></i>
                </ControlButton>
              </el-tooltip>
              <el-tooltip effect="dark" content="整理节点" placement="top">
                <ControlButton title="整理节点" @click="reDraw">
                  <i class="iconfont icon-youhuabuju"></i>
                </ControlButton>
              </el-tooltip>
            </Controls>
          </VueFlow>
        </div>
      </template>
    </BaseFold>

    <RunDrawer
      :nodes="nodes"
      v-model="showDrwer"
      @runStart="testStart"
      :toFillArr="toFillArr"
      :testType="testType"
      :testNodeInfo="testNodeInfo"
      :toRunNodeId="toRunNodeId"
    />
  </div>
  <EditOutputDialog />
  <PopSide
    :btnPosition="btnPosition"
    :clickAddEdgeId="clickAddEdgeId"
    v-model="showPopSide"
    @addEdgeNode="addEdgeNode"
  />
</template>
<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed, provide } from 'vue'
import Sidebar from './components/Sidebar.vue'
import NodeItemNew from './work-flow/NodeItemNew.vue'

import {
  getPreNodeIdsFn,
  getPreNodesByNodesLines,
  getReferOpsByNodesLines,
  getBatchReferByNodesLine,
  addIdToNodes,
  outputsTreeArr,
  inputCanRefTypes,
  retNodeReferArr,
  initOpsByNodeList,
  getRalNodes,
  getNotRalNodes,
  classifyNodes,
  nodeIsLLM,
  flowStatusArr,
  filterNodesByReference
} from './work-flow/baseInfo.js'
import { ElMessage, ElLoading } from 'element-plus'
import { saveFlow, getFlowDetail, getFlowStatus, getPluginList, stopRunApi } from '@/api/workflow'
import RunDrawer from './work-flow/components/RunDrawer'
import { useRoute, useRouter } from 'vue-router'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { VueFlow, MarkerType, useVueFlow } from '@vue-flow/core'
import DropzoneBackground from './work-flow/DropzoneBackground'
import { Controls, ControlButton } from '@vue-flow/controls'
import NodeItemSon from './work-flow/NodeItemSon'
import Header from './components/Header'
import { useWfStore } from '@/stores/workflow1'
import SockJS from 'sockjs-client'
import EditOutputDialog from './work-flow/components/EditOutputDialog.vue'
import BaseFold from '@/components/probot/BaseFold.vue'
import EdgeWithButton from './work-flow/EdgeWithButton.vue'
import PopSide from './work-flow/components/PopSide.vue'
import { useLayout } from './work-flow/useLayout.js'
import { batchUpdateNodesRAF } from './work-flow/batch.js'
import { addIfElseByNodes, createIFElse, reshowStyle } from './common/if-else.js'

import {
  baseNodeInfo,
  startEndNodes,
  createNodeId,
  createEdgeId,
  initKnowledgeOutPuts,
  initBaseNode,
  reshowMinMatch,
  nodeIfElse,
  initDubboPlugin
} from './common/init-node.js'

import {
  intentSaveBefore,
  reshowCoreSetting,
  transformSetting,
  returnBatchInfo,
  handleBatch,
  reshowInputs,
  saveTransInputs,
  conditionTypes,
  getReferName
} from './common/base.js'
import {
  getLineConfig,
  saveLineTransform,
  reshowHandles,
  checkSameEdge,
  reshowTransform
} from './common/edges-transform.js'

const wfStore = useWfStore()
const { setDragging, setFlowStatus, setNodesPreNodes, getCommonOps } = wfStore
let sock = null
const { layout } = useLayout()
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)
const router = useRouter()

const {
  onConnect,
  addEdges,
  updateEdge,
  removeEdges,
  toObject,
  addNodes,
  getNode,
  getNodes,
  viewport,
  updateNode,
  zoomTo,
  screenToFlowCoordinate,
  fitView,
  onEdgeMouseEnter,
  onEdgeMouseLeave,
  setCenter,
  getViewport,
  getEdges,
  onEdgesChange
} = useVueFlow()

const reDrawing = ref(false)
const reDraw = () => {
  reDrawing.value = true
  // 分批次更新节点
  nextTick(() => {
    const { nodes, edges } = toObject()
    // 过滤掉子节点 nodeif nodeelse
    const nodesButIFElse = nodes.filter((it) => !nodeIfElse.includes(it.nodeType))
    const newNodes = layout(nodesButIFElse, edges, 'LR')
    batchUpdateNodesRAF(newNodes, 7, updateNode).then((res) => {
      if (res) {
        nextTick(() => {
          fitView()
          reDrawing.value = false
        })
      }
    })
  })
}

onEdgeMouseEnter((e) => {
  e.edge.data.showBtn = true
})

onEdgeMouseLeave((e) => {
  e.edge.data.showBtn = false
})

const changeDraging = (val) => {
  if (val) {
    setDragging(val)
  } else {
    nextTick(() => {
      setDragging(val)
    })
  }
}

const flowBaseInfo = ref(null)
const nodes = ref([])

const zoomFn = () => {
  zoomTo(1)
}

const streamTimer = ref(null)
const draggedType = ref('')
const showDrwer = ref(false)
const draggedInfo = ref(null)
const workFlowRef = ref({})

const baseRefer = {
  referenceNodeId: 0,
  referenceName: ''
}

const setItemRef = (el, id) => {
  const { nodes } = toObject()
  const curNode = nodes.filter((item) => item.id === id)[0]
  if (conditionTypes.includes(curNode?.nodeType) || !el) {
    workFlowRef.value[id] = null
  } else {
    workFlowRef.value[id] = el
  }
}

// 拖拽结束
const onDropFn = (e) => {
  const position = screenToFlowCoordinate({
    x: e.clientX,
    y: e.clientY
  })
  addNodeFn(position, draggedType.value)
}

const initSockjs = (params) => {
  if (!sock) {
    sock = new SockJS(
      `${window.location.origin}${
        import.meta.env.VITE_GLOB_API_NEW_URL
      }ws/sockjs/flow/status/stream`
    )

    sock.onopen = function () {
      console.log('SockJS is open now.')
      sock.send(JSON.stringify(params))
    }

    sock.onmessage = function (event) {
      const json = event.data
      try {
        const resData = JSON.parse(json)
        handleRes(resData)
      } catch (error) {
        console.error(error)
      }
    }

    sock.onclose = function () {
      console.log('SockJS is closed now.')
      sock?.close()
      sock = null
    }

    sock.onerror = function (event) {
      console.error(event)
      sock = null
    }
  } else {
    sock.send(JSON.stringify(params))
  }
}

// 场景：拖拽结束和点击增加node按钮、多模态选择结果
const addNodeFn = (position, type) => {
  const { nodes } = toObject()
  const nodeId = createNodeId(nodes)
  const node = initBaseNode(position, type, { draggedInfo: draggedInfo.value, nodeId })
  addNodes(node)
  if (conditionTypes.includes(node.nodeType)) {
    const ifElseNodes = createIFElse(node)
    addNodes(ifElseNodes)
  }
  return node
}
const addEdgesFn = (newNode, curEdge) => {
  const { target } = curEdge
  const { id } = newNode
  //  重新计算一下引用下拉选项
  const { nodes, edges } = toObject()
  const edgeId = createEdgeId(edges)
  const baseEdge = {
    markerEnd: MarkerType.ArrowClosed,
    type: 'button'
  }
  const sourceHandleObj = curEdge.sourceHandle ? { sourceHandle: curEdge.sourceHandle } : {}
  const newEdge = {
    ...baseEdge,
    source: curEdge.source,
    id: curEdge.id,
    target: `${id}`,
    ...sourceHandleObj
  }

  // 将旧的edges指向新的target
  updateEdge(curEdge, newEdge)
  if (!conditionTypes.includes(newNode.nodeType)) {
    addEdges({
      ...baseEdge,
      source: `${id}`,
      target: `${target}`,
      id: `edge_${edgeId}`
    })
  }

  initAllNodesPreIds(nodes, edges)
}

// 将后面的节点向后移动
const moveBackward = (nextNode) => {
  const { affectedNodes } = classifyNodes(true, nodesPreNodes.value, nextNode.id)
  const affectedNodeIds = affectedNodes.map((item) => item.nodeId)
  affectedNodeIds.forEach((nodeId) => {
    const node = getNode.value(nodeId)
    const { position } = node
    updateNode(nodeId, {
      ...node,
      position: {
        x: position.x + 800,
        y: position.y
      }
    })
  })
}

// 从边增加节点
const addEdgeNode = ({ curEdge, nextNode, type, pluginInfo }) => {
  if (pluginInfo) draggedInfo.value = pluginInfo
  const { position } = nextNode
  const newNode = addNodeFn(position, type)
  // 增加两条新的edges
  addEdgesFn(newNode, curEdge)
  // 去除旧的edges
  removeEdges(curEdge.id)
  fitFn()
  // 将后面的节点向后移动
  moveBackward(nextNode)
  // 重新设置引用options
  const { nodes, edges } = toObject()
  initAllNodesPreIds(nodes, edges)
}

const lines = ref([])

// 点击插件/flow
const addPluginFlow = (pluginFlow) => {
  draggedInfo.value = pluginFlow
  addClickFn(pluginFlow.type)
}

const onDragStart = ({ type, info }) => {
  draggedType.value = type
  draggedInfo.value = info
  document.addEventListener('drop', onDragEnd)
}
const addClickFn = (type) => {
  const randomX = Math.floor(Math.random() * 100 + 1)
  const randomY = Math.floor(Math.random() * 100 + 1)
  const position = { x: randomX, y: randomY }
  addNodeFn(position, type)
}
const onDragEnd = () => {
  draggedType.value = null
  document.removeEventListener('drop', onDragEnd)
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

const transOutputs = (node) => {
  const outputs = node.outputs || []
  if (node.nodeType == 'knowledge') {
    return [
      {
        type: 'value',
        name: outputs[0].value,
        subName: outputs[0].children[0].value,
        valueType: outputs[0].desc
      }
    ]
  } else if (node.nodeType == 'end') {
    // 只有node的输出需要引用外面的输入
    const res = outputs.map((item) => {
      const { referenceInfo } = item
      if (!referenceInfo) {
        return item
      } else {
        const nodeId = referenceInfo[0]
        const { nodes } = toObject()
        const referNode = nodes.find((allNodeItem) => allNodeItem.id == nodeId)
        const refereOutputs =
          referNode?.nodeType == 'begin' ? referNode?.inputs || [] : referNode?.outputs || []
        if (referNode && ouputsNodeTypes.includes(referNode.nodeType)) {
          // 如果引用的是code llm
          const [nodeId, ...other] = referenceInfo
          const referenceName = getReferName(refereOutputs, other)
          return {
            ...item,
            referenceName,
            referenceNodeId: nodeId
          }
        } else {
          //临时方案， 这种情况后面应该会有修改 SubName和referenceSubName对应
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
  } else if (ouputsNodeTypes.includes(node.nodeType)) {
    // 将outputs子集转为字符串
    return outputs.map((outputI) => {
      return { ...outputI, schema: JSON.stringify(outputI.children) }
    })
  } else {
    return outputs
  }
}

const batchInfoReq = (node) => {
  const { nodeType, batchType, batchInfo } = node
  if (nodeIsLLM(nodeType) && batchType == 'batch' && batchInfo) {
    const { $$TY_BATCH_MAX_TIMES$$, $$TY_BATCH_TIME_INTERVAL$$ } = batchInfo
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
          referenceNodeId: referNodeId,
          referenceName
        }
      } else {
        return {
          ...item,
          ...baseRefer
        }
      }
    })
    const batchArr = [
      ...btachMap,
      {
        name: '$$TY_BATCH_MAX_TIMES$$',
        type: 'value',
        value: $$TY_BATCH_MAX_TIMES$$ + ''
      },
      {
        name: '$$TY_BATCH_TIME_INTERVAL$$',
        type: 'value',
        value: $$TY_BATCH_TIME_INTERVAL$$
      }
    ]
    return {
      batchInfo: batchArr
    }
  } else {
    return {
      batchInfo: []
    }
  }
}

const transInputs = (nodeItem) => {
  if (inputCanRefTypes.includes(nodeItem.nodeType)) {
    const preInputs = nodeItem.inputs || []
    const inputs =
      nodeItem.nodeType == 'llmImageUnderstand' ? [...preInputs, ...nodeItem.inputsImg] : preInputs
    if (nodeItem.coreSetting.pluginType == 'dubbo') {
      inputs.push({
        name: '$$TY_PLUGIN_DUBBO_PARAMS$$',
        type: 'input',
        value: nodeItem.inputsPlugin.dubboParam
      })
    } else if (
      nodeItem.nodeType == 'knowledge' &&
      nodeItem.coreSetting.knowledgeRefType == 'reference'
    ) {
      inputs.push(nodeItem.knowledgeInputs[0])
    }
    return saveTransInputs(inputs, nodeItem, getNode.value)
  } else {
    const inputsArr = nodeItem.inputs || []
    const newInputs = inputsArr.map((it) => {
      const { children, ...rst } = it
      return {
        ...rst,
        schema: JSON.stringify(it.children || [])
      }
    })
    return newInputs
  }
}
// 保存节点时候转换数据
const saveNodeTrans = (item) => {
  item.nodeMetaInfo.extraInfo = ''
  item.nodeMetaInfo.nodePosition = item.position
  const newCoreSetting = transformSetting(item, getNodes.value)
  return {
    ...item,
    coreSetting: newCoreSetting,
    outputs: transOutputs(item),
    inputs: transInputs(item),
    ...batchInfoReq(item)
  }
}

const saveing = ref(false) // 正在保存
//  saveTransform
const saveTransform = ({ nodes, edges }) => {
  const nodesVal = nodes.map((item) => {
    return saveNodeTrans(item)
  })
  const filterIfElse = nodesVal.filter((item) => {
    return item.nodeType != 'nodeif' && item.nodeType != 'nodeelse'
  })
  const obj = intentSaveBefore(filterIfElse, edges)

  const edgesVal = saveLineTransform(obj.edges, getNode.value)
  return {
    flowBaseInfo: flowBaseInfo.value,
    flowSettingInfo: {
      nodes: obj.nodes,
      flowBaseId: flowBaseInfo.value.id,
      id: flowBaseInfo.value.id,
      edges: edgesVal
    }
  }
}

const saveClick = async () => {
  saveFn(false)
}
// 默认是needcheck 变化的，如果不需要check则需要保存
const saveFn = async (needCheck = true) => {
  const p = saveTransform(toObject())
  //  如果不需要修改则直接保存
  if (needCheck) {
    // 如果没有修改则不需要调用保存接口
    if (JSON.stringify(detailInfo.value) == JSON.stringify(p)) {
      console.log('您没有修改，不需要保存！')
      return
    } else {
      detailInfo.value = p
    }
  }

  const { code, message } = await saveFlow(p)
  ElMessage.success(code == 0 ? '保存成功!' : message)
  saveing.value = false
}
// 需要转换ouputs的节点类型,所有输出是树形结构的节点类型
const ouputsNodeTypes = outputsTreeArr
// const ouputsNodeTypes = ['code', 'llm', 'plugin']
// 编辑回显处理数据
const editReShowOutputs = (node) => {
  if (node.nodeType == 'knowledge') {
    let outputs = initKnowledgeOutPuts()
    return outputs
  } else if (ouputsNodeTypes.includes(node.nodeType)) {
    try {
      const arr = node.outputs.map((item) => {
        return {
          ...item,
          children: item.schema ? JSON.parse(item.schema) : []
        }
      })
      // 给树形结构数据每个节点增加Id, 防止删除树形结构节点错乱
      return addIdToNodes(arr)
    } catch (error) {
      console.log('error', error)
      return node.outputs
    }
  } else return node.outputs
}

const route = useRoute()

const editNameSuc = () => {
  // 更新基本信息
  const detailId = route.params.id
  getFlowDetail(detailId).then(({ code, data }) => {
    if (code === 0 && data) {
      flowBaseInfo.value = data.flowBaseInfo
    }
  })
}

let loading = null
const getDetailed = ref(false)

// 回显
const transformNodes = (resNodes) => {
  return resNodes.map((it) => {
    const sets = it.coreSetting ? JSON.parse(it.coreSetting) : {}
    const anserModel = it.nodeType == 'end' && !sets.type ? { type: 'var' } : {}
    const transSets = conditionTypes.includes(it.nodeType)
      ? sets
      : {
          ...sets,
          ...reshowMinMatch(sets, it.nodeType),
          ...anserModel
        }
    const coreSetting = reshowCoreSetting(transSets, it.nodeType)
    return {
      ...it,
      coreSetting,
      outputs: editReShowOutputs(it),
      ...reshowInputs(it),
      position: it.nodeMetaInfo.nodePosition,
      id: `${it.id}`,
      ...baseNodeInfo(it.nodeType),
      ...reshowStyle(it),
      ...returnBatchInfo(it),
      ...reshowHandles(it, coreSetting)
    }
  })
}

// 初始化全部的refre
const initAllNodesPreIds = (allNodes, resLinesArr) => {
  const arr = []
  allNodes.forEach((item) => {
    const nodeId = item.id
    const obj = {
      nodeType: item.nodeType,
      nodeId,
      preNodes: getPreNodesByNodesLines(nodeId, resLinesArr, allNodes),
      ops: getReferOpsByNodesLines(nodeId, allNodes, resLinesArr),
      referBatchOps: nodeIsLLM(item.nodeType)
        ? getBatchReferByNodesLine(nodeId, allNodes, resLinesArr)
        : []
    }
    arr.push(obj)
  })
  console.log('arr', arr)
  setNodesPreNodes(arr)
}

// 查看用户权限
const hasPermission = ref(false)
const detailInfo = ref({})

// 获取详情
const getDetailFn = async () => {
  changeDraging(true)
  // await getPlugs()
  loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.1)'
  })
  const detailId = route.params.id
  try {
    const { code, data = {}, message } = await getFlowDetail(detailId)
    if (code === 0) {
      flowBaseInfo.value = data.flowBaseInfo
      hasPermission.value = data.hasPermission
      const resNodes = data?.flowSettingInfo?.nodes || []
      const resLines = data?.flowSettingInfo?.edges || []

      const allNodes = addIfElseByNodes(transformNodes(resNodes), createIFElse)
      const ifElseNodes = allNodes.filter((it) => nodeIfElse.includes(it.nodeType))
      const resLinesArr = reshowTransform(resLines, ifElseNodes, allNodes)
      nodes.value = allNodes
      initAllNodesPreIds(allNodes, resLinesArr)
      nextTick(() => {
        changeDraging(false)
        if (resLinesArr.length > 0) {
          lines.value = resLinesArr
        }
        // 增加begin,end节点。需要根据元素的宽度设置初始值，所以需要放在nextTick中
        const toAdds = startEndNodes(allNodes)
        if (toAdds.length > 0) {
          nodes.value = nodes.value.concat(toAdds)
        }
        nextTick(() => {
          detailInfo.value = saveTransform(toObject())
        })
      })
      getDetailed.value = true
      loading.close()
      // 判断是否需要重新获取流
      judgeGetStream()
    } else {
      ElMessage({ message, type: 'error' })
      nextTick(() => {
        changeDraging(false)
        getDetailed.value = true
        loading.close()
      })
    }
  } catch (e) {
    console.log('e', e)
    nextTick(() => {
      changeDraging(false)
      getDetailed.value = true
      loading.close()
    })
  }
}

const switchShowDrawer = () => {
  const newVal = !showDrwer.value
  // 弹出试运行时候不需要更新
  changeDraging(newVal)
  showDrwer.value = newVal
}
const resetNodeTestResult = () => {
  const { nodes } = toObject()
  const anyNodeHasRes = nodes.find((item) => item.resOutputs?.status)
  // const llmBatchs = nodes.filter((item) => item.nodeType == 'llm' && item.batchType == 'batch')
  // 保存上一次批处理最大次数
  // llmBatchs.forEach((item) => {
  //   const maxNum = item.batchInfo.$$TY_BATCH_MAX_TIMES$$ || 1
  //   updateNode(item.id, { resInputs: {}, resOutputs: {}, maxNum })
  // })
  const toChange = nodes.filter((item) => item.resOutputs || item.resInputs)
  if (!anyNodeHasRes) return
  // 如果有节点有结果就清空
  toChange.forEach((item) => {
    updateNode(item.id, { resInputs: {}, resOutputs: {} })
  })
}
const toRunNodeId = ref(null)
// 是否要修改inputs
const changeToFill = (newNodeVal, arr) => {
  toRunNodeId.value = newNodeVal
  toFillArr.value = arr
}

// 试运行需要填写项
const toFillArr = ref([])
const setRunTestValBegin = () => {
  const { nodes } = toObject()
  const startNodes = nodes.find((item) => item.nodeType === 'begin')
  if (!startNodes) return
  changeToFill(startNodes.id, startNodes.inputs)
}
// whole : 完整运行 single : 单个节点  后端返回SINGLE_NODE(1, "单节点测试执行"), NORMAL(0, "正常执行"),
const testType = ref('whole')
const changeTestType = (executeType) => {
  testType.value = executeType == 1 ? 'single' : 'whole'
}
const testNodeInfo = ref(null)
// test whole
const clickRun = async (siwtchSave) => {
  if (siwtchSave) await saveFn()
  const validateRes = await validate()
  if (!validateRes) {
    ElMessage({
      message: '有校验失败的节点，请检查配置好后，再试运行',
      type: 'error'
    })
    return
  }
  streamTimer.value = null
  changStatus(null, '')
  testNodeInfo.value = {}
  setRunTestValBegin()
  runFn('whole')
}

// 清空原始内容，展示run弹框
const runFn = (val) => {
  setRetryInfo(false)
  testType.value = val
  flowStatus.value = 1
  switchShowDrawer()
  resetNodeTestResult()
}

// 单节点测试
const testNode = (nodeInfo) => {
  // 防止造成数据污染，所以转一下
  const preVal = JSON.parse(JSON.stringify(nodeInfo))
  const nodeData = saveNodeTrans(preVal)
  testNodeInfo.value = {
    ...nodeData,
    coreSetting: JSON.stringify(nodeData.coreSetting)
  }
  const isLLMBatch = nodeIsLLM(nodeInfo.nodeType) && nodeInfo.batchType == 'batch'
  const { inputs = [], inputsImg = [] } = nodeInfo
  // 如果是图片类型的大模型要将图片的输入加入inputs
  const inputsArr = nodeInfo.nodeType == 'llmImageUnderstand' ? [...inputsImg, ...inputs] : inputs
  const allInputs = inputsArr?.filter((it) => {
    if (isLLMBatch) {
      // 过滤掉引用自己的
      return it.type == 'reference' && it.referenceInfo[0] != nodeInfo.id
    } else {
      return ['reference', 'imageReference'].includes(it.type)
    }
  })
  const arr = isLLMBatch ? [...allInputs, ...nodeInfo.batchInfo.arr] : allInputs
  const newFill = arr.map((it) => {
    return {
      ...it,
      required: nodeInfo.nodeType != 'plugin' ? true : it.required
    }
  })
  changeToFill(nodeInfo.id, newFill)
  runFn('single')
}
const cancleSingleRecordIds = ref([])
const stopRun = async () => {
  // 取消单节点的试运行
  if (testType.value == 'single') {
    // 如果能获取到flowRecordId ，则要将这个flowRecordId，塞入已取消的recordIds 中，在handleRes中就不渲染了
    cancleSingleRecordIds.value.push(flowRecordIdStatus.value)
    // 将状态设置为空
    flowRunning.value = false
    flowTesting.value = false
    // 如果已结束，要给一个状态1，这相当于一个空状态
    changStatus(1, null)
    updataRecordId(null)
    // 将所有的node状态置为空
    resetNodeTestResult()
  } else {
    // 取消整条工作流
    cancleWhole()
  }
}

// 取消整条流水线
const cancleWhole = () => {
  const p = {
    flowId: route.params.id,
    flowRecordId: route.query.flowRecordId,
    cmd: 'cancelFlow'
  }
  operateFlowFn(p)
  ElMessage.success('取消中，请稍等!')
}
const endCodes = flowStatusArr.filter((it) => it.isEnd).map((item) => item.code)
// node状态还是非终态
const isUnFinished = (nodeId) => {
  const node = getNode.value(nodeId)
  if (!node) return false
  return !(node?.resOutputs?.status && endCodes.includes(node.resOutputs?.status))
}
// 正在测试
const flowTesting = ref(false)
// 正在运行
const flowRunning = ref(false)

//  正在运行或者测试
const runOrTest = computed(() => {
  return flowRunning.value || flowTesting.value
})

// 用来控制头部的展示状态
const flowStatus = ref(1)
// flowRecordId 状态值
const flowRecordIdStatus = ref('')
// 修改工作流的整体状态,changStatus
const changStatus = (status, flowRecordId) => {
  flowStatus.value = status
  flowRecordIdStatus.value = flowRecordId
  setFlowStatus(status)
}
const mergeRetryAndTry = (pre, newVal) => {
  if (pre.flowRecordId == newVal.flowRecordId) {
    const newInputs = { ...pre.nodeInputsMap, ...newVal.nodeInputsMap }
    const newOutputs = { ...pre.nodeOutputsMap, ...newVal.nodeOutputsMap }
    return {
      ...newVal,
      nodeInputsMap: newInputs,
      nodeOutputsMap: newOutputs
    }
  } else {
    return newVal
  }
}
// 存储上一次状态数据，用于节点重试
const preFlowStatus = ref({})

const handleRes = (resData) => {
  flowTesting.value = false

  // 如果当前这一次是重试，则要将之前的数据和现在的数据合并
  preFlowStatus.value = retryInfo.value?.isRetry
    ? mergeRetryAndTry(preFlowStatus.value, resData)
    : resData
  let {
    nodeInputsMap = {},
    nodeOutputsMap,
    endFlowOutput,
    endFlowStatus,
    end,
    messageType,
    message,
    durationTime,
    flowRecordId,
    executeType
  } = preFlowStatus.value
  // 已取消
  if (cancleSingleRecordIds.value.includes(flowRecordId)) return
  // 当前这条流水线flowRecordId操作过跳转过某个节点，要清空
  if (hasGoto.value.flowRecordId == flowRecordId) {
    resetNodeTestResult()
  }
  if (!flowRecordId && messageType == 'FLOW_EXECUTE_FAILURE') {
    // 检验未通过
    ElMessage.error(message)
    return
  }
  changeTestType(executeType)
  streamTimer.value = durationTime
  // 将一个字符串转成一个布尔值最简单的做法怎么写

  flowRunning.value = end ? false : !!flowRecordId
  // 如果已结束，要给一个状态1，这相当于一个空状态
  changStatus(endFlowStatus, flowRecordId)
  flowRecordId && updataRecordId(flowRecordId)
  const { nodes } = toObject()
  const endNodesId = nodes.filter((item) => {
    return item.nodeType == 'end'
  })[0]?.id
  const inputsArr = Object.keys(nodeInputsMap || {})
  const outputsArr = Object.keys(nodeOutputsMap || {})
  const allArr = [...new Set([...inputsArr, ...outputsArr])]
  // 只有非终态的才需要更新。
  const toUpdates = allArr.filter((item) => isUnFinished(item))
  // 如果是重试则要把重试的那个id加进去
  if (retryInfo.value?.isRetry) {
    toUpdates.push(retryInfo.value?.retryNodeId)
  }
  toUpdates.forEach((item) => {
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

    const node = nodes.find((nodeI) => nodeI.id === item)
    const batchObj = handleBatch(node, obj, nodeInputsMap[item]?.inputDetails)
    const maxNum = batchObj?.maxNum
    const minNum = batchObj?.minNum
    const filterArr = batchObj?.filterArr || []
    const myObj = maxNum != undefined ? { maxNum } : {}
    const myObjMin = minNum != undefined ? { minNum } : {}
    const newInputs = nodeInputsMap[item]?.inputDetails?.map((item) => {
      return {
        ...item,
        isBatch: filterArr.find((itemI) => itemI.name == item.name) ? true : false
      }
    })
    let ouputs = obj.outputDetails || []
    if (nodeIsLLM(node.nodeType) && node.batchType == 'batch') {
      ouputs = ouputs.map((item) => {
        return {
          ...item,
          isBatch: true
        }
      })
    }
    if (nodeCentering.value) {
      setCenterFn(obj)
    }
    const newVal = {
      resInputs: {
        ...nodeInputsMap[item],
        inputDetails: newInputs
      },
      resOutputs: {
        ...obj,
        outputDetails: ouputs
      },
      ...myObj,
      ...myObjMin
    }
    updateNode(item, newVal)
  })
}

const setCenterFn = (obj) => {
  // 1运行开始 5 等待手动确认
  if ([1, 5].includes(obj.status)) {
    const node = getNode.value(`${obj.nodeId}`)
    const { x, y } = node.position
    const { zoom } = getViewport()
    const options = { zoom }
    setCenter(x, y, options)
  }
}
//判断是否需要获取stream数据
const judgeGetStream = () => {
  const { flowRecordId } = route.query
  if (flowRecordId) {
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>这里需要更新一下testType
    // testType.value = preObj[flowId].testType
    // 获取流状态
    initSockjs({ flowRecordId: flowRecordId, operateCmd: 'getStatus' })
  }
}

const testStart = (newP) => {
  switchShowDrawer()
  // 展示测试loading
  flowTesting.value = true
  initSockjs({ ...newP, operateCmd: 'testFlow' })
}

const retryInfo = ref(null)
// 设置重试信息，用户handleRes时候区分情况
const setRetryInfo = (isRetryParam, nodeId = null) => {
  retryInfo.value = {
    isRetry: isRetryParam,
    retryNodeId: nodeId,
    // 点击重试之前上一次的重试状态值
    flowStatusBeforeRetry: preFlowStatus.value
  }
}

const retryNode = (nodeInfo) => {
  setRetryInfo(true, nodeInfo.id)

  const { flowRecordId } = route.query
  const { id } = route.params
  const params = {
    flowId: id,
    operateCmd: 'testFlow',
    meta: {
      specifiedStartNodeId: nodeInfo.id,
      flowRecordId
    }
  }
  initSockjs(params)
}

const preRecordId = ref(null)
// 更新recordId
const updataRecordId = (flowRecordId) => {
  // 只有变化了才会更新
  if (preRecordId.value == flowRecordId) return
  preRecordId.value = flowRecordId
  updateRouterQuery({ flowRecordId })
}

const updateRouterQuery = (newQuery) => {
  const { query } = route
  router.push({
    query: {
      ...query,
      ...newQuery
    }
  })
}

onConnect((connection) => {
  const otherP = getLineConfig(connection, getNode.value)
  const timer = new Date().getTime() - mountedTimer.value

  const allEdges = getEdges.value
  const allNodes = getNodes.value
  const { hasSame, conditionSameTip } = checkSameEdge(allEdges, allNodes, connection)
  // 是否有同样链接的edge
  if (hasSame) {
    conditionSameTip && ElMessage.warning(conditionSameTip)
    return
  }
  addEdges({
    ...connection,
    ...otherP,
    markerEnd: MarkerType.ArrowClosed,
    id: `edge_${timer}`,
    type: 'button'
  })
  const { nodes, edges } = toObject()
  initAllNodesPreIds(nodes, edges)
})

onEdgesChange((changes) => {
  for (const change of changes) {
    if (change.type === 'remove') {
      const { nodes, edges } = toObject()
      initAllNodesPreIds(nodes, edges)
    }
  }
})

// 阻止滚轮的默认行为
const handleWheel = (event) => {
  if (event.ctrlKey) {
    event.preventDefault()
    return
  }
}

/**
 * 清除或设置特定的 query 参数
 *
 * @param {string} paramName - 要清除或设置的参数名
 * @param {string|null} value - 要设置的值，如果为 null 则清除该参数
 */
const updateQueryParam = (paramName, value) => {
  // 获取当前路由的查询参数
  const currentQuery = { ...route.query }
  if (value === null) {
    // 如果值为 null，则删除该参数
    delete currentQuery[paramName]
  } else {
    // 否则，设置或更新该参数
    currentQuery[paramName] = value
  }

  // 使用 router.push 更新路由，保持其他路由信息不变
  router.push({
    path: route.path,
    query: currentQuery
  })
}

// 点击的元素如果class不是edgebutton，并且不在class为edgebutton内，则认为是空白处
const handleClick = (e) => {
  // 获取点击的目标元素
  const target = e.target
  // 检查点击的元素是否有 'edgebutton' 类
  const isEdgeButton = target.classList.contains('edgebutton')
  // 检查点击的元素是否在 'edgebutton' 类的元素内
  const isInsideEdgeButton = target.closest('.edgebutton') !== null

  const isPopSide = target.classList.contains('pop-side')
  const isInsidePopSize = target.closest('.pop-side') !== null
  // 如果既不是 edgebutton，也不在 edgebutton 内，则认为点击了空白处
  if (!isEdgeButton && !isInsideEdgeButton && !isPopSide && !isInsidePopSize) {
    // 在这里添加点击空白处的逻辑，例如：
    showPopSide.value = false
  } else {
    console.log('点击了 edgebutton 或其内部')
  }
}

const hasGoto = ref({})
// 操作flow(取消、goto)
const operateFlowFn = (p) => {
  const newP = { ...p, operateCmd: 'operateFlow' }
  const { cmd, meta, flowRecordId } = newP
  // 如果用户想要跳转到某个节点，则要将上一次的结果清空一下
  if (cmd == 'gotoFlow' && typeof meta?.nodeId == 'number') {
    hasGoto.value = {
      flowRecordId
    }
  }
  initSockjs(newP)
}
provide('operateFlowFn', operateFlowFn)

const mountedTimer = ref()
onMounted(() => {
  window.addEventListener('click', handleClick)
  mountedTimer.value = new Date().getTime()
  getCentering()
  getDetailFn()
  // 获取条件选择器的操作符options
  getCommonOps()
})
onUnmounted(() => {
  sock?.close()
  window.removeEventListener('click', handleClick)
})

const showPopSide = ref(false)
const clickAddEdgeId = ref(null)
const siwtchShoPop = () => {
  showPopSide.value = !showPopSide.value
}
const clickEdgeBtn = ({ edgeId }) => {
  // 点击的是不同的edge
  if (edgeId != clickAddEdgeId.value) {
    showPopSide.value = true
  } else {
    siwtchShoPop()
  }
  clickAddEdgeId.value = edgeId
  viewportChangeFn()
}

const btnPosition = ref(null)
const viewportChangeFn = (val) => {
  // 获取id为edge-btn-active的元素
  const activeBtn = document.getElementById('edge-btn-active')
  if (activeBtn) {
    const rect = activeBtn.getBoundingClientRect()
    // 实际显示宽度
    const width = rect.width
    const leftPosition = rect.left // 元素左边缘相对于视口左边缘的距离
    const topPosition = rect.top // 元素右边缘相对于视口左边缘的距离
    const newP = {
      x: leftPosition + width + 5 * viewport.value.zoom,
      y: topPosition
    }
    btnPosition.value = newP
  }
}
const fitFn = () => {
  nextTick(() => {
    const timer = setTimeout(() => {
      // 需要写在setTimeout中否则有时会失效
      fitView()
      clearTimeout(timer)
    }, 0)
  })
}
// 置空从引用值
const emtpyRefData = (relationIds, flowNodeId) => {
  const { nodes } = toObject()
  // 过滤出所有相关node的Id
  const relIdsArr = relationIds.map((item) => item.nodeId)
  // 过滤出有引用了该节点的node
  const relationNodes = nodes.filter((item) => relIdsArr.includes(item.id))
  // 过滤出引用了该节点的节点
  const referNodes = filterNodesByReference(relationNodes, flowNodeId)
  referNodes.forEach((node) => {
    const referOps = nodesPreNodes.value.find((item) => item.nodeId == node.id)
    // 重新算出节点的inputs和outputs
    const referArrObj = {
      inputs: retNodeReferArr(node.inputs, referOps, node),
      outputs: node.nodeType == 'end' ? retNodeReferArr(node.outputs, referOps, node) : node.outputs
    }
    const inImgObj =
      node.nodeType != 'llmImageUnderstand'
        ? {}
        : {
            inputsImg: retNodeReferArr(node.inputsImg, referOps, node)
          }
    const newItem = {
      ...node,
      ...referArrObj,
      ...inImgObj
    }
    updateNode(newItem.id, newItem)
  })
}
provide('emtpyRefData', emtpyRefData)

const initRelationNodesPreIds = (relations) => {
  const obj = toObject()
  return initOpsByNodeList(relations, obj)
}

// 删除开始节点的输入或者其他节点(除了结束节点)的输入
const delInOut = (nodeId) => {
  //  会影响的nodes
  const relationNodes = getRalNodes(nodeId, nodesPreNodes.value)
  const notRelationNodes = getNotRalNodes(nodeId, nodesPreNodes.value)
  const newArr = [...initRelationNodesPreIds(relationNodes), ...notRelationNodes]
  setNodesPreNodes(newArr)
  emtpyRefData(relationNodes, nodeId)
}
// 当OutPutsTree组件删除tree Item时调用
const delInPutOutput = (flowNode) => {
  delInOut(flowNode.id)
}
provide('delInPutOutput', delInPutOutput)

const nodeCentering = ref(false)
const changeCenter = () => {
  nodeCentering.value = !nodeCentering.value
  localStorage.setItem('nodeCentering', nodeCentering.value)
}
const getCentering = () => {
  const preVal = localStorage.getItem('nodeCentering')
  nodeCentering.value = preVal == 'true' ? true : false
}
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
@import './common/index.scss';
</style>
