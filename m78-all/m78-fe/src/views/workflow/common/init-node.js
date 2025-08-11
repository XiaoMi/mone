// 初始化node数据
import { useVueFlow } from '@vue-flow/core'
import { v4 as uuidv4 } from 'uuid'
import { nodeIsLLM, nodesBase, baseCode } from '../work-flow/baseInfo.js'
import { initIntentHandle } from './edges-transform.js'
import { conditionTypes } from './base.js'
import { conditionH } from './if-else.js'

export const nodeIfElse = ['nodeif', 'nodeelse']
const { toObject } = useVueFlow()
export const createNodeName = (nodeType, draggedInfo) => {
  let name = ''
  switch (nodeType) {
    case 'plugin':
      name = draggedInfo?.name
      break
    case 'subFlow':
      name = draggedInfo?.name
      break

    default:
      name = nodesBase[nodeType].title
      break
  }
  const { nodes } = toObject()
  const exsitSameTypeNodes = nodes.filter((item) => item.nodeType === nodeType)
  const nodeName =
    exsitSameTypeNodes.length > 0 ? `${name}_${exsitSameTypeNodes.length}` : `${name}`
  return nodeName
}

export const inputItem = (nodeType) => {
  let name = ''
  if (nodeType == 'knowledge') {
    name = '$$TY_KNOWLEDGE_QUERY$$'
  } else if (nodeType == 'code') {
    name = 'count'
  }
  return {
    name,
    type: 'reference'
  }
}

export const initInputs = (nodeType, draggedInfo) => {
  if (nodeType == 'plugin' && draggedInfo?.originalType == 'http') {
    return {
      inputs: draggedInfo.meta?.input || []
    }
  } else if (nodeType == 'subFlow') {
    const inputs = draggedInfo.inputs || []
    return {
      inputs: inputs.map((it) => {
        return {
          ...it,
          type: 'reference'
        }
      })
    }
  } else if (conditionTypes.includes(nodeType)) {
    return { inputs: [] }
  } else if (nodeType == 'knowledge') {
    return {
      inputs: [inputItem(nodeType)],
      knowledgeInputs: [
        {
          name: '$$TY_KNOWLEDGE_REF_BASE_ID$$',
          type: 'reference',
          referenceInfo: []
        }
      ]
    }
  } else if (nodeType == 'intentRecognition') {
    return {
      inputs: [
        {
          name: '$$TY_INTENT_QUERY$$',
          type: 'reference'
        }
      ]
    }
  } else {
    return {
      inputs: [inputItem(nodeType)]
    }
  }
}

export const initKnowledgeOutPuts = () => {
  return [
    {
      value: 'outputList',
      label: 'outputList',
      desc: 'Array<Object>',
      children: [
        {
          value: 'output',
          label: 'output',
          desc: 'String'
        }
      ]
    }
  ]
}
export const initOutputBases = (nodeType) => {
  let name = ''
  if (nodeType == 'database') {
    name = 'data'
  } else if (conditionTypes.includes(nodeType)) {
    name = 'result'
  } else if (nodeType == 'code') {
    name = 'randomNumbers'
  }

  let valueType = 'String'
  if (nodeType == 'database') {
    valueType = 'Array<Object>'
  } else if (nodeType == 'code') {
    valueType = 'Array<String>'
  }
  return [
    {
      name,
      valueType,
      id: uuidv4()
    }
  ]
}

export const initIntentOutputs = () => {
  return [{ name: 'intentId', valueType: 'Integer' }]
}

export const initOutputs = (nodeType, draggedInfo) => {
  if (nodeType == 'plugin') {
    return { outputs: draggedInfo.meta?.output || [] }
  } else if (nodeType == 'intentRecognition') {
    // 意图识别
    return { outputs: initIntentOutputs() }
  } else if (nodeType == 'subFlow') {
    // subflow
    return { outputs: draggedInfo.outputs }
  } else if (nodeType == 'knowledge') {
    return { outputs: initKnowledgeOutPuts() }
    // 如果是条件选择器则没有outputs
  } else {
    const op = initOutputBases(nodeType)
    return { outputs: op }
  }
}

export const initPluginFlowSetting = (nodeType, draggedInfo) => {
  switch (nodeType) {
    case 'plugin':
      return {
        pluginId: draggedInfo?.id,
        pluginType: draggedInfo?.originalType
      }
      break
    case 'subFlow':
      return {
        flowId: draggedInfo?.id
      }
      break

    default:
      return {}
      break
  }
}

// 初始化coreSetting
export const initKnowledgeSetting = (nodeType) => {
  if (nodeType != 'knowledge') {
    return {}
  }
  return {
    knowledgeRefType: 'knowlegeList',
    knowledgeBaseId: null,
    maxRecall: 3,
    minMatch: 50
  }
}

export const initLLMSetting = (nodeType) => {
  if (nodeType.startsWith('llm')) {
    return {
      temperature: 0.7
    }
  }
  return {}
}
export const initGptModal = (nodeType) => {
  if (['code', 'intentRecognition', 'llmImageUnderstand', 'llm'].includes(nodeType)) {
    return {
      gptModel: nodeType == 'llmImageUnderstand' ? '' : 'gpt4_1106'
    }
  }
  return {}
}

export const initCodeSetting = (nodeType) => {
  if (['code'].includes(nodeType)) {
    return {
      code: baseCode
    }
  }
  return {}
}

export const reshowMinMatch = (setting, nodeType) => {
  if (nodeType == 'knowledge') {
    return {
      minMatch: setting.minMatch * 100
    }
  }
  return {}
}

export const conditionsFn = (nodeType) => {
  if (nodeIfElse.includes(nodeType)) {
    return {
      coreSetting: [
        {
          type: 'reference',
          operator: ''
        }
      ]
    }
  } else {
    return {}
  }
}

const startEndNodeTypes = ['begin', 'end']

// 基础样式和配置
export const baseNodeInfo = (nodeType) => {
  const styleObj = conditionTypes.includes(nodeType)
    ? { style: { width: '600px', height: `${conditionH}px` } }
    : {}
  const isIfElse = nodeIfElse.includes(nodeType)
  return {
    type: isIfElse ? nodeType : 'custom',
    ...styleObj,
    draggable: isIfElse ? false : true,
    deletable: startEndNodeTypes.includes(nodeType) ? false : true
  }
}

export const initLLmBatch = (nodeType) => {
  if (nodeIsLLM(nodeType)) {
    const imgInputs =
      nodeType == 'llmImageUnderstand' ? { inputsImg: [{ type: 'imageReference' }] } : {}
    return {
      batchInfo: {
        arr: [
          {
            name: '',
            type: 'reference'
          }
        ],
        $$TY_BATCH_MAX_TIMES$$: '1',
        $$TY_BATCH_TIME_INTERVAL$$: 1500
      },
      batchType: 'single',
      ...imgInputs
    }
  } else {
    return {}
  }
}

export const initDubboPlugin = (pluginInfo, nodeType) => {
  if (nodeType != 'plugin') return {}
  // 初始化dubbo类型的插件的特殊配置
  if (pluginInfo.originalType == 'http') {
    return {}
  } else {
    return {
      inputsPlugin: {
        // $$TY_PLUGIN_DUBBO_PARAMS$$
        dubboParam: ''
      }
    }
  }
}
export const getNodeWrapStyle = () => {
  const wrap = document.querySelector('.work-flow')
  return {
    w: wrap?.offsetWidth,
    h: wrap?.offsetHeight
  }
}

// 插入start end
export const startEndNodes = (nodes) => {
  const toAddNodes = []
  const initNum = 60
  const endWidth = nodesBase['end'].width
  // 修正因为拖拽导致的坐标偏差
  if (!nodes.find((item) => item.nodeType == 'begin')) {
    const startNode = createStartEnd('begin', {
      x: initNum,
      y: initNum
    })
    toAddNodes.push(startNode)
  }
  if (!nodes.find((item) => item.nodeType == 'end')) {
    const wrapWH = getNodeWrapStyle()
    const endX = wrapWH.w - endWidth - initNum
    const endNode = createStartEnd('end', {
      x: endX,
      y: initNum
    })
    toAddNodes.push(endNode)
  }
  return toAddNodes
}

export const createStartEnd = (nodeType, position) => {
  return {
    id: nodeType == 'begin' ? '1' : '2',
    nodeType,
    position,
    nodeMetaInfo: {
      nodeName: nodeType == 'begin' ? '开始' : '结束'
    },
    ...(nodeType == 'begin'
      ? { inputs: [{ name: '', valueType: 'String', desc: '', required: true }] }
      : {}),
    ...(nodeType == 'begin' ? {} : { outputs: [{ name: '', type: 'reference' }] }),
    ...(nodeType == 'end'
      ? {
          coreSetting: {
            type: 'var',
            answerContent: ''
          }
        }
      : {}),
    ...baseNodeInfo(nodeType)
  }
}

// 取得所有nodes的id,拿到最大的Id ，加1返回。作为新建node的id
export const createNodeId = (nodes) => {
  // 过滤掉有下划线的
  const filterNodes = nodes.filter((it) => !it.id.includes('_'))
  const nodesIds = filterNodes.map((it) => Number(it.id))
  const maxId = Math.max(...nodesIds)
  return maxId + 1
}

/**
 * 从字符串中提取数字
 *
 * @param str 包含数字的字符串
 * @returns 提取出的数字
 */
export const extractNumber = (str) => {
  const match = str.match(/\d+/)
  return match ? parseInt(match[0]) : null
}

// 取得所有nodes的id,拿到最大的Id ，加1返回。作为新建node的id
export const createEdgeId = (edges) => {
  // 过滤掉有下划线的
  const edgesIds = edges.map((it) => extractNumber(it.id))
  const maxId = Math.max(...edgesIds)
  return maxId + 1
}

// 初始化intent 的coreSetting
export const initIntentSetting = (nodeType) => {
  if (nodeType != 'intentRecognition') return {}
  const promptContent = ''
  const intentMatch = [
    {
      key: 1,
      value: ''
    },
    {
      key: 2,
      value: ''
    },
    { key: -1, value: '其他意图' }
  ]
  return { promptContent, intentMatch }
}

export const initData = (nodeType, nodeInfo) => {
  if (!['subFlow', 'plugin'].includes(nodeType)) return {}
  return {
    data: { avatarUrl: nodeInfo.avatarUrl, desc: nodeInfo.desc }
  }
}

export const initBaseNode = (position, nodeType, { draggedInfo = {}, nodeId }) => {
  const intentSetting = initIntentSetting(nodeType)
  const newNode = {
    id: nodeId,
    nodeType,
    position,
    ...initData(nodeType, draggedInfo),
    nodeMetaInfo: {
      nodeName: createNodeName(nodeType, draggedInfo),
      plugin: nodeType == 'plugin' ? draggedInfo : null
    },
    ...initInputs(nodeType, draggedInfo),
    ...initOutputs(nodeType, draggedInfo),
    coreSetting: {
      ...initPluginFlowSetting(nodeType, draggedInfo),
      ...initKnowledgeSetting(nodeType),
      ...initLLMSetting(nodeType),
      ...initGptModal(nodeType),
      ...initCodeSetting(nodeType),
      ...intentSetting
    },
    //  只有nodeif类型有conditions
    ...conditionsFn(nodeType),
    ...baseNodeInfo(nodeType),
    ...initLLmBatch(nodeType),
    ...initIntentHandle(nodeType, intentSetting),
    ...initDubboPlugin(draggedInfo, nodeType)
  }
  return newNode
}
