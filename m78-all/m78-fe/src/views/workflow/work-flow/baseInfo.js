import LLMImg from '../imgs/icon-LLM.png'
import CodeImg from '../imgs/icon-Code.png'
import KnowledgeImg from '../imgs/icon-Knowledge.png'
import ConditionImg from '../imgs/icon-Condition.png'
import StartImg from '../imgs/icon-Start.png'
import EndImg from '../imgs/icon-End.png'
import PluginImg from '../imgs/icon-plugin.png'
import DatabaseImg from '../imgs/icon-Database.png'
import ConfirmImg from '../imgs/confirm.png'
import ModelImg from '../imgs/mode-png.png'
import ModelFile from '../imgs/mode-file.png'
import IntentImg from '../imgs/icon-node-intent.png'
import { filterNoOutput, knowledgeOutputs, checkInput } from '../common/base.js'

const llmArr = ['llmImageUnderstand', 'llmFileUnderstand', 'llm']
const returnSpotPostion = (nodeId) => {
  const startNode = document.getElementById(nodeId)
  const startNodeH = startNode.offsetHeight
  const startNodeW = startNode.offsetWidth
  const startTransform = startNode.style.transform
  const numStr = startTransform
    .replace(/translate\(/g, '')
    .replace(/\)/g, '')
    .replace(/px/g, '')
  const startArr = numStr.split(',').map((item) => Number(item))
  const spotsY = startArr[1] + startNodeH / 2
  const spots = {
    left: {
      x: startArr[0],
      y: spotsY
    },
    right: {
      x: startArr[0] + startNodeW,
      y: spotsY
    }
  }
  return spots
}
const referTypes = ['imageReference', 'reference']
const validateRef = (rule, value, callback, curObj) => {
  const { referenceInfo, type } = curObj
  if (
    referTypes.includes(type) &&
    (!referenceInfo || (referenceInfo && referenceInfo.length == 0))
  ) {
    return callback(new Error('参数值不可为空'))
  } else if (type == 'value' && !checkInput(curObj.value)) {
    return callback(new Error('参数值不可为空'))
  } else {
    return callback()
  }
}

const validateRef2 = (rule, value, callback, curObj) => {
  const { referenceInfo2, type2 } = curObj
  if (
    referTypes.includes(type2) &&
    (!referenceInfo2 || (referenceInfo2 && referenceInfo2.length == 0))
  ) {
    return callback(new Error('参数值不可为空'))
  } else if (type2 == 'value' && !checkInput(curObj.value2)) {
    return callback(new Error('参数值不可为空'))
  } else {
    return callback()
  }
}

const validPName = (rule, value, callback, arr) => {
  const reg = /^[a-zA-Z_]([a-zA-Z0-9_]+)?$/
  if (!value) {
    return callback(new Error('参数名不可为空'))
  } else if (!reg.test(value)) {
    return callback(new Error('以字母或下划线开头且仅包含字母,数字,下划线'))
  } else {
    const newA = arr || []
    const names = newA.map((item) => item.name)
    const newL = new Set(names).size
    const preL = newA.length
    if (newL != preL) {
      return callback(new Error('参数不可重复!'))
    } else {
      return callback()
    }
  }
}
// 校验树形结构
const validateTreeInput = (node, data, value, callback) => {
  const reg = /^[a-zA-Z_$]([a-zA-Z0-9_$]+)?$/
  const { name } = data
  if (!name) {
    return callback(new Error('参数名不可为空'))
  } else if (!reg.test(name)) {
    return callback(new Error('以字母或下划线开头且仅包含字母,数字,下划线,$'))
  } else {
    const siblingNodes = (node?.parent?.childNodes || []).filter((item) => item.id != node.id)
    if (siblingNodes) {
      const duplicateValue = siblingNodes.some((sibling) => {
        return sibling.data.name === name
      })
      if (duplicateValue) {
        return callback(new Error('同级参数名不可重复'))
      } else {
        return callback()
      }
    } else {
      return callback()
    }
  }
}

const modelsBase = {
  llmImageUnderstand: {
    type: 'llmImageUnderstand',
    width: 500,
    imgSrc: ModelImg,
    title: '图片理解',
    desc: '图片理解',
    showInSidebar: false, // 这个是多模态的所有不在sidebar中展示
    showRun: true, // 是否展示试运行
    inputCanRef: true, // 输入可引用
    hasBatch: true,
    outputsTree: true,
    isModel: true
  },
  llmFileUnderstand: {
    type: 'llmFileUnderstand',
    width: 500,
    imgSrc: ModelFile,
    title: '文本理解',
    desc: '文本理解',
    showInSidebar: false, // 这个是多模态的所有不在sidebar中展示
    showRun: true, // 是否展示试运行
    inputCanRef: true, // 输入可引用
    hasBatch: true,
    outputsTree: true,
    isModel: true
  }
}

// 所有类型的节点集合;
// type 用户后端传值
// outputsTree:输出是否是树形结构,涉及到给后端传参;
// inputCanRef: 输入部分可引用其他节点的node ;
// hasBatch 有批处理的节点
// showRun：是否展示单节点试运行
// hideLeft:隐藏左侧handle;
// hideRight:隐藏右侧handle
// showInSidebar:在sidebar中展示
const nodesBase = {
  llm: {
    type: 'llm',
    width: 500,
    imgSrc: LLMImg,
    title: '大模型',
    desc: '调用大语言模型，使用变量和提示词生成回复',
    showInSidebar: true,
    inputCanRef: true,
    hasBatch: true,
    showRun: true, // 是否展示试运行
    outputsTree: true
  },
  code: {
    type: 'code',
    width: 500,
    imgSrc: CodeImg,
    title: '代码',
    desc: '编写代码，处理输入变量来生成返回值',
    showInSidebar: true,
    showRun: true, // 是否展示试运行
    inputCanRef: true,
    outputsTree: true
  },
  plugin: {
    type: 'plugin',
    width: 500,
    imgSrc: PluginImg,
    title: '插件',
    desc: '填写输入输出参数',
    showRun: true, // 是否展示试运行
    inputCanRef: true,
    outputsTree: true
  },
  precondition: {
    type: 'precondition',
    width: 600,
    imgSrc: ConditionImg,
    showInSidebar: false,
    hideRight: true,
    title: '选择器',
    desc: '连接多个下游分支，如果设定的条件成立则只运行“如果”分支，不成立则只运行否定分支'
  },
  newPrecondition: {
    type: 'newPrecondition',
    width: 600,
    imgSrc: ConditionImg,
    showInSidebar: true,
    hideRight: true,
    title: '选择器',
    desc: '连接多个下游分支，如果设定的条件成立则只运行“如果”分支，不成立则只运行否定分支'
  },
  knowledge: {
    type: 'knowledge',
    width: 420,
    imgSrc: KnowledgeImg,
    showRun: true, // 是否展示试运行
    showInSidebar: true,
    title: '知识库',
    inputCanRef: true,
    desc: '在选定的知识中，根据输入变量召回最匹配的信息，并以列表形式返回'
  },
  database: {
    type: 'database',
    width: 500,
    imgSrc: DatabaseImg,
    showRun: true, // 是否展示试运行
    showInSidebar: true,
    title: '数据库',
    inputCanRef: true,
    desc: '可支持对 Database 放开读写控制，用户可读写其他用户提交的数据，由开发者控制。需要提前在 Bot 的 Database 中添加 Table。'
  },
  manualConfirm: {
    type: 'manualConfirm',
    width: 350,
    imgSrc: ConfirmImg,
    showInSidebar: true,
    title: '手动确认',
    desc: '用户手动确认才会继续执行后续节点。'
  },
  begin: {
    type: 'begin',
    width: 550,
    imgSrc: StartImg,
    title: '开始',
    desc: '工作流的起始节点，用于设定启动工作流需要的信息',
    outputsTree: true,
    hideLeft: true
  },
  end: {
    type: 'end',
    width: 470,
    imgSrc: EndImg,
    title: '结束',
    desc: '工作流的最终节点，用于返回工作流运行后的结果信息',
    hideRight: true
  },
  nodeif: {
    type: 'nodeif',
    width: 470,
    imgSrc: StartImg,
    title: 'IF',
    desc: '条件-if'
  },
  nodeelse: {
    type: 'nodeelse',
    width: 470,
    imgSrc: StartImg,
    title: 'ELSE',
    desc: '条件-else'
  },
  intentRecognition: {
    type: 'intentRecognition',
    width: 500,
    imgSrc: IntentImg,
    showInSidebar: true,
    showRun: true, // 是否展示试运行
    hideRight: true,
    inputCanRef: true, //
    title: '意图识别',
    desc: '用于用户输入的意图识别，并将其与预设意图选项进行匹配。'
  },
  subFlow: {
    type: 'subFlow',
    width: 500,
    showInSidebar: false,
    showRun: true, // 是否展示试运行
    hideRight: false,
    inputCanRef: true //
  },
  ...modelsBase
}

// 过滤出nodesBase中outputsTree是true的元素，返回type组成的数组
function filterNodesWithOutputsTreeTrue(nodesBase, typeName) {
  return Object.values(nodesBase)
    .filter((node) => node[typeName])
    .map((node) => node.type)
}
const outputsTreeArr = filterNodesWithOutputsTreeTrue(nodesBase, 'outputsTree')
// 过滤出nodesBase中inputCanRef是true的元素，返回type组成的数组
const inputCanRefTypes = filterNodesWithOutputsTreeTrue(nodesBase, 'inputCanRef')

// 过滤出nodesBase中hasBatch是true的元素，返回type组成的数组
const hasBatchTypes = filterNodesWithOutputsTreeTrue(nodesBase, 'hasBatch')
// 不在sidebar展示的类型
const showSidebar = filterNodesWithOutputsTreeTrue(nodesBase, 'showInSidebar')

const opList = [
  {
    value: 'EQUALS',
    label: '等于'
  },
  {
    value: 'NOT_EQUALS',
    label: '不等于'
  },
  {
    value: 'GREATER_THAN',
    label: '大于'
  },
  {
    label: '小于',
    value: 'LESS_THAN'
  },
  {
    label: '包含',
    value: 'CONTAINS'
  }
]
const oneM = 1000 * 60
const oneH = oneM * 60
const oneDay = oneH * 24

const formatTime = (mss) => {
  let days = 0,
    hours = 0,
    minutes = 0,
    seconds = 0
  seconds = (mss % oneM) / 1000
  if (mss > oneM) {
    minutes = parseInt((mss % oneH) / oneM)
    if (mss > oneH) {
      hours = parseInt((mss % oneDay) / oneH)
      if (mss > oneDay) {
        days = parseInt(mss / oneDay)
      }
    }
  }
  return `${days ? days + 'd' : ''}${hours ? hours + 'h' : ''}${
    minutes ? minutes + 'm' : ''
  }${seconds}s`
}

const isValueValid = (value, options) => {
  if (value.length === 0) {
    return true // 如果选中值为空，直接返回true
  }
  for (let option of options) {
    if (option.value === value[0]) {
      if (option.children) {
        // 如果有子选项，递归检查子选项
        return isValueValid(value.slice(1), option.children)
      } else if (value.length === 1) {
        // 如果没有子选项，且已经是最后一级选中值，则匹配成功
        return true
      }
    }
  }
  return false // 如果没有匹配项，返回false
}
// 获取当前nodeId的所有上级节点 ID;
const getPreNode = (nodeId, allObj) => {
  // 缓存 allLines 和 allNodes 避免重复查询
  const allLines = allObj.edges || []
  const allNodes = allObj.nodes || []

  // 获取当前节点的上级节点
  const targetIsNodeIdLines = allLines.filter((item) => item.target === nodeId)
  const preNodeIds = targetIsNodeIdLines.map((item) => {
    if (item.conditionFlag) {
      const sourceId = item.source
      const nodeIfOrElse = allNodes.find((nodeItem) => nodeItem.id === sourceId)
      // 异常处理
      if (!nodeIfOrElse) {
        throw new Error(`Node with ID: ${sourceId} not found.`)
      }
      return nodeIfOrElse.parentNode
    } else {
      return item.source
    }
  })

  if (preNodeIds.length === 0) {
    return []
  }

  let allIds = preNodeIds
  const visitedNodeIds = new Set() // 避免循环引用
  const stack = [...preNodeIds] // 使用栈来迭代以保持 DFS 特性

  while (stack.length > 0) {
    const nodeId = stack.pop()
    if (visitedNodeIds.has(nodeId)) continue // 已访问过的节点跳过
    visitedNodeIds.add(nodeId)

    // 递归获取所有上级节点
    const parentIds = getPreNode(nodeId, allObj)
    parentIds.forEach((parentId) => {
      if (!visitedNodeIds.has(parentId)) {
        stack.push(parentId)
        allIds.push(parentId)
      }
    })
  }

  return allIds
}
const CHILDREN_KEY = 'children'

const mapTree = (treeItm, nodeType) => {
  if (typeof treeItm !== 'object' || treeItm === null) {
    throw new Error('Invalid input: treeItm must be an object')
  }

  const childrenName = CHILDREN_KEY
  const haveChildren = Array.isArray(treeItm[childrenName]) && treeItm[childrenName].length > 0

  let children = []
  if (haveChildren) {
    treeItm[childrenName].forEach((item) => {
      if (item.name) {
        children.push(mapTree(item, nodeType))
      }
    })
  }

  return {
    ...treeItm,
    label: treeItm.name,
    value: treeItm.name,
    children
  }
}

const getReferOps = (allObj, nodeId) => {
  const nodes = allObj.nodes || []
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNode(nodeId, allObj)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.includes(item.id))
  // 将 if else 节点去掉
  const sourceNodeButIf = filterNoOutput(sourceNodes)
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      arr = inputs.filter((item) => item.name)
    } else if (['code', ...llmArr, 'plugin'].includes(item.nodeType)) {
      const filterEmptyName = item.outputs.filter((item) => item.name)
      const opsArr = filterEmptyName.map((outputItem) => {
        const res = mapTree(outputItem, item.nodeType)
        return res
      })
      arr = opsArr
    }
    return {
      value: `${item.id}`,
      label: item.nodeMetaInfo.nodeName,
      disabled: true,
      nodeType: item.nodeType,
      children: ['knowledge', 'code', ...llmArr, 'plugin'].includes(item.nodeType)
        ? arr
        : arr.map((input) => {
            return {
              label: input.name,
              value: input.name
            }
          })
    }
  })
  return ops
}
const getChild = (ouputs, nameArr, i = 0) => {
  if (i + 1 == nameArr.length) {
    return ouputs?.find((item) => item.name == nameArr[i])?.children || []
  } else {
    return getChild(ouputs.find((item) => item.name == nameArr[i])?.children, nameArr, i + 1)
  }
}

const getBatchRefer = (allObj, nodeId) => {
  const nodes = allObj.nodes || []
  const curNode = nodes.find((item) => item.id == nodeId)
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNode(nodeId, allObj)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.includes(item.id))
  // 将 if else 节点去掉
  let sourceNodeButIf = filterNoOutput(sourceNodes)
  if (curNode.batchType == 'batch') {
    sourceNodeButIf.push(curNode)
  }
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      arr = inputs.filter((item) => item.name)
    } else if (['code', ...llmArr].includes(item.nodeType)) {
      //  如果引用的是llm自己的批处理
      if (item.id == curNode.id) {
        const arrTemp = item.batchInfo?.arr || []
        const refInfo = arrTemp
          .map((res) => {
            return {
              label: res.name,
              value: res.name + '.0',
              referenceInfo: res.referenceInfo || []
            }
          })
          .filter((fil) => fil?.referenceInfo?.length >= 2)
        refInfo.forEach((ele) => {
          const [nodeId, ...other] = ele.referenceInfo
          const node = nodes.find((nodeI) => nodeI.id == nodeId)
          const outputsArr = node?.outputs || []
          const child = getChild(outputsArr, other)
          const filterEmptyName = child.filter((childI) => childI.name)
          const opsArr = filterEmptyName.map((outputItem) => {
            const res = mapTree(outputItem, item.nodeType)
            return res
          })
          ele.children = opsArr
        })
        arr = refInfo
      } else {
        const filterEmptyName = item.outputs.filter((item) => item.name)
        const opsArr = filterEmptyName.map((outputItem) => {
          const res = mapTree(outputItem, item.nodeType)
          return res
        })
        arr = opsArr
      }
    }
    return {
      value: `${item.id}`,
      label: item.nodeMetaInfo.nodeName,
      disabled: true,
      children: ['knowledge', 'code', ...llmArr].includes(item.nodeType)
        ? arr
        : arr.map((input) => {
            return {
              label: input.name,
              value: input.name
            }
          })
    }
  })
  return ops
}
const flowStatusArr = [
  {
    code: 1,
    label: '运行开始',
    tagType: 'primary'
  },
  {
    code: 2,
    label: '运行成功',
    tagType: 'success',
    isEnd: true // 终态
  },
  {
    code: 3,
    label: '运行失败',
    tagType: 'danger',
    isEnd: true // 终态
  },
  {
    code: 4,
    label: '运行取消',
    tagType: 'danger',
    isEnd: true // 终态
  },
  {
    code: 5,
    label: '等待手动确认',
    tagType: ''
  }
]
const flowHeaderStatus = [
  {
    code: 0,
    text: '运行中',
    tagType: 'primary'
  },
  {
    code: 2,
    text: '运行成功',
    tagType: 'success'
  },
  {
    code: 3,
    text: '运行失败',
    tagType: 'danger'
  },
  {
    code: 4,
    text: '运行已取消',
    tagType: 'danger'
  }
]
// 每一个节点展示状态
const flowStatusObjBase = {
  0: '运行中',
  2: '运行成功',
  3: '运行失败',
  4: '运行已取消',
  5: '运行中'
}

const getPreNodeIdsFn = (nodeId, allLines, allNodes) => {
  const visitedNodeIds = new Set()
  return getPreNodesByNodesLines(nodeId, allLines, allNodes, visitedNodeIds)
}

const getParentNodeIds = (allLines, allNodes, nodeId) => {
  // 获取当前节点的上级节点
  const targetIsNodeIdLines = allLines.filter((item) => item.target === nodeId)
  return targetIsNodeIdLines.map((item) => {
    if (item.conditionFlag) {
      const sourceId = item.source
      const sourceNode = allNodes.find((nodeItem) => nodeItem.id === sourceId)
      // 异常处理
      if (!sourceNode) {
        throw new Error(`Node with ID: ${sourceId} not found.`)
      }
      return sourceNode.nodeType == 'intentRecognition' ? item.source : sourceNode.parentNode
    } else {
      return item.source
    }
  })
}

const getPreNodesByNodesLines = (nodeId, allLines, allNodes) => {
  const allIds = []
  const visitedNodeIds = new Set()
  const stack = [nodeId]

  while (stack.length > 0) {
    const currentNodeId = stack.pop()

    if (!visitedNodeIds.has(currentNodeId)) {
      visitedNodeIds.add(currentNodeId)

      const parentIds = getParentNodeIds(allLines, allNodes, currentNodeId)

      for (const parentId of parentIds) {
        if (!visitedNodeIds.has(parentId)) {
          stack.push(parentId)
          allIds.push(parentId)
        }
      }
    }
  }

  return allIds
}

const getReferOpsByNodesLines = (nodeId, nodes, lines) => {
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNodesByNodesLines(nodeId, lines, nodes)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.includes(item.id))
  const sourceNodeButIf = filterNoOutput(sourceNodes)
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    let filterEmptyName = []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      filterEmptyName = inputs.filter((item) => item.name)
    } else if (
      [
        'code',
        ...llmArr,
        'plugin',
        'subFlow',
        'knowledge',
        'database',
        'intentRecognition'
      ].includes(item.nodeType)
    ) {
      // 如果是知识库的类型的需要特殊处理一下
      const outputs = item.nodeType == 'knowledge' ? knowledgeOutputs(item.outputs) : item.outputs
      filterEmptyName = outputs.filter((item) => item.name)
    }
    const opsArr = filterEmptyName.map((outputItem) => {
      const res = mapTree(outputItem, item.nodeType)
      return res
    })
    arr = opsArr
    return {
      value: `${item.id}`,
      label: item.nodeMetaInfo.nodeName,
      disabled: true,
      nodeType: item.nodeType,
      children: ['knowledge', 'code', ...llmArr, 'plugin', 'begin', 'subFlow'].includes(
        item.nodeType
      )
        ? arr
        : arr.map((input) => {
            return {
              label: input.name,
              value: input.name,
              valueType: input.valueType
            }
          })
    }
  })
  return ops
}

const getBatchReferByNodesLine = (nodeId, nodes, lines) => {
  const curNode = nodes.find((item) => item.id == nodeId)
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNodesByNodesLines(nodeId, lines, nodes)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.includes(item.id))
  // 将 if else 节点去掉
  let sourceNodeButIf = filterNoOutput(sourceNodes)
  if (curNode.batchType == 'batch') {
    sourceNodeButIf.push(curNode)
  }
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      arr = inputs.filter((item) => item.name)
    } else if (['code', ...llmArr].includes(item.nodeType)) {
      //  如果引用的是llm自己的批处理
      if (item.id == curNode.id) {
        const arrTemp = item.batchInfo?.arr || []
        const refInfo = arrTemp
          .map((res) => {
            return {
              label: res.name,
              value: res.name + '.0',
              referenceInfo: res.referenceInfo || []
            }
          })
          .filter((fil) => fil?.referenceInfo?.length >= 2)
        refInfo.forEach((ele) => {
          const [nodeId, ...other] = ele.referenceInfo
          const node = nodes.find((nodeI) => nodeI.id == nodeId)
          //如果引用的是开始节点
          const outputsArr = node?.nodeType == 'begin' ? node?.inputs : node?.outputs || []
          const child = getChild(outputsArr, other)
          const filterEmptyName = child.filter((childI) => childI.name)
          const opsArr = filterEmptyName.map((outputItem) => {
            const res = mapTree(outputItem, item.nodeType)
            return res
          })
          ele.children = opsArr
        })
        arr = refInfo
      } else {
        const filterEmptyName = item.outputs.filter((item) => item.name)
        const opsArr = filterEmptyName.map((outputItem) => {
          const res = mapTree(outputItem, item.nodeType)
          return res
        })
        arr = opsArr
      }
    }
    return {
      value: `${item.id}`,
      label: item.nodeMetaInfo.nodeName,
      disabled: true,
      children: ['knowledge', 'code', ...llmArr].includes(item.nodeType)
        ? arr
        : arr.map((input) => {
            return {
              label: input.name,
              value: input.name
            }
          })
    }
  })
  return ops
}
const baseCode =
  "import com.google.gson.JsonObject;\nimport java.util.Random;\nimport com.google.gson.Gson;\n\n/**\n * 生成指定个数的随机数\n */\npublic JsonObject execute(JsonObject input, Object context) {\n    if (!input.has('count')) {\n        throw new IllegalArgumentException(\"JSON对象必须包含键'count'。\");\n    }\n    int count = input.get('count').getAsInt();\n    context.getLogger().info('count:'+count);\n    Random random = new Random();\n    int[] numbers = new int[count];\n    for (int i = 0; i < count; i++) {\n        numbers[i] = random.nextInt();\n    }\n    Gson gson = new Gson();\n    String numbersJson = gson.toJson(numbers);\n    JsonObject result = new JsonObject();\n    result.addProperty(\"randomNumbers\", numbersJson);\n    return result;\n}"

// 定义一个方法，方法有一个参数arr；arr是树形结构，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}， children也是个数组，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}；这个方法要将所有结点valueType不属于['Image','String']的节点的disabled设置为true,否则为false; 结果进行返回，返回结果还是树形结构。
function filterImgRefTree(arr) {
  const setDisabled = (node) => {
    // 使用解构赋值和短路运算符简化逻辑
    const { valueType, children = [] } = node
    return {
      ...node,
      disabled: !['Image', 'String'].includes(valueType),
      ...(children.length && { children: children.map(setDisabled) })
    }
  }
  return arr.map(setDisabled)
}

// 定义一个方法，方法有一个参数arr；arr是树形结构，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}， children也是个数组，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}；这个方法要将所有结点valueType不属于typesArr的节点的disabled设置为true,否则为false; 结果进行返回，返回结果还是树形结构。
function filterRefByTypes(arr, typesArr) {
  const setDisabled = (node) => {
    // 使用解构赋值和短路运算符简化逻辑
    const { valueType, children = [] } = node
    return {
      ...node,
      disabled: !typesArr.includes(valueType),
      ...(children.length && { children: children.map(setDisabled) })
    }
  }
  return arr.map(setDisabled)
}

function filterImgBatchRefTree(arr, nodeId) {
  const setDisabled = (node) => {
    // 使用解构赋值和短路运算符简化逻辑
    const { valueType, children = [] } = node
    return {
      ...node,
      disabled: !['Image', 'String'].includes(valueType),
      ...(children.length && { children: children.map(setDisabled) })
    }
  }
  console.log('arr', arr, nodeId)
  const batchArr = arr.filter((item) => item.value == nodeId)
  const otherArr = arr.filter((item) => item.value != nodeId)
  const otherDisabledArr = otherArr.map(setDisabled)
  const newBatch = processTree(batchArr)
  console.log('newBatch', newBatch)
  return [...otherDisabledArr, ...newBatch]
}

/**
 * 处理树形结构,设置节点的disabled属性
 * 定义一个方法，方法有一个参数arr；arr是树形结构，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}， children也是个数组，数组的每一项格式是{label:'ddd',value:'aaa', valueType:'Image',children:[]}；如果是第二层则disable是false, 否则要将所有结点valueType不属于['Image','String']的节点的disabled设置为true,否则为false; 结果进行返回，返回结果还是树形结构。
 * @param {Array} arr - 树形结构数组
 * @param {number} [depth=0] - 当前深度,用于内部递归
 * @returns {Array} - 处理后的树形结构数组
 */
function processTree(arr, depth = 0) {
  return arr.map((node) => {
    // 创建节点的副本,避免修改原始数据
    const newNode = { ...node }

    // 设置disabled属性
    if (depth === 1) {
      // 第二层节点,disabled始终为false
      newNode.disabled = false
    } else {
      // 其他层级,根据valueType设置disabled
      newNode.disabled = !['Image', 'String'].includes(node.valueType)
    }

    // 如果有子节点,递归处理
    if (Array.isArray(node.children) && node.children.length > 0) {
      newNode.children = processTree(node.children, depth + 1)
    }

    return newNode
  })
}

// 当前节点中inputs是否引用变动的数据 ,type : inputs/outputs/inputsImg
function inputsRel(node, type, currentNodeId) {
  return node[type]?.some((input) => {
    return input.type === ['inputs', 'outputs'].includes(type)
      ? 'reference'
      : 'imageReference' &&
          input.referenceInfo?.length > 0 &&
          input.referenceInfo[0] === currentNodeId
  })
}
//写一个方法；从关联的节点(关联节点作为参数是个数组)中过滤出inputs中有元素的type=='reference'并且referecenInfo的第一个元素等于当前node的id的节点
function filterNodesByReference(nodes, currentNodeId) {
  return nodes.filter((node) => {
    const inputRefer = inputsRel(node, 'inputs', currentNodeId)
    const outputRefer = inputsRel(node, 'outputs', currentNodeId)
    const inputImgRefer = inputsRel(node, 'inputsImg', currentNodeId)
    return inputRefer || outputRefer || inputImgRefer
  })
}

// 检验绑定数据是够还存在
function isPathValid(cascaderData, pathArray) {
  function checkPath(data, path) {
    // 如果数据options为空
    if (!data) {
      // 如果数据结束，需要路径也为空才认为是有效路径
      return !path.length
      // 如果路径为空，需要数据不为空才认为是有效路径
    } else if (!path.length) {
      return Boolean(data)
    }
    // 判断是否匹配
    const current = path.shift() // 取路径数组的第一个元素进行匹配
    for (const item of data) {
      if (item.value === current) {
        // 找到匹配的项
        return checkPath(item.children, path) // 递归检查剩余路径
      }
    }
    return false // 本层未找到匹配，返回false
  }
  const res = checkPath(cascaderData, pathArray.slice()) // 使用slice避免修改原数组
  console.log('pathArray', res)
  return res
}

// 写一个方法，参数是一个数组；遍历数组的元素，如果元素的type是reference,并且元素的referenceInfo不匹配，则将referenceInfo设置为null,并返回新数组；
const retNodeReferArr = (arr, referObj, node) => {
  return arr.map((item) => {
    console.log('referObj', referObj)
    // 引用类型共有两种：imageReference reference
    if (['reference', 'imageReference'].includes(item.type)) {
      const opsArr = node.batchType == 'batch' ? referObj?.referBatchOps : referObj?.ops
      if (!isPathValid(opsArr || [], item.referenceInfo)) {
        item.referenceInfo = null
      }
    }
    return item
  })
}

// 定义一个方法getRalNodes; 根据nodeId和preNodesArr, 过滤出preNodesArr中元素preNodes包含nodeId的元素，返回这个过滤后的结果
function getRalNodes(nodeId, preNodesArr) {
  return preNodesArr.filter((node) => node.preNodes.includes(nodeId))
}
// 定义一个方法getNotRalNodes; 根据nodeId和preNodesArr, 过滤出preNodesArr中元素preNodes不包含nodeId的元素，返回这个过滤后的结果
function getNotRalNodes(nodeId, preNodesArr) {
  return preNodesArr.filter((node) => !node.preNodes.includes(nodeId))
}
// 根据关联的node集合，获取新的cas的下拉选项
const initOpsByNodeList = (relations, obj) => {
  const arr = []
  const { nodes, edges } = obj
  relations.forEach((item) => {
    const nodeId = item.nodeId
    const newVal = {
      ...item,
      ops: getReferOpsByNodesLines(nodeId, nodes, edges),
      referBatchOps: llmArr.includes(item.nodeType)
        ? getBatchReferByNodesLine(nodeId, nodes, edges)
        : []
    }
    arr.push(newVal)
  })
  return arr
}

// 帮我定义一个方法，方法有一个参数；这个参数数据结构：[{name: "randomNumbers", subName: "", valueType: "Array<String>", value: "", desc: "", type: "",children:[{name: "randomNumbers", subName: "", valueType: "Array<String>", value: "", desc: "", type: ""}]}]，这个方法就是要给这个参数的每一项增加一个id属性，最外层数据的id可以是index,自己id，配置成父级的id和下划线，和子集的index； 最终结果返回新的数据
function addIdToNodes(nodes) {
  const addId = (node, parentId = '') => {
    const newNode = {
      ...node,
      id: parentId ? `${parentId}_${node.name}` : node.name
    }
    if (node.children && node.children.length > 0) {
      newNode.children = node.children.map((child, index) => addId(child, `${newNode.id}_${index}`))
    }
    return newNode
  }
  return nodes.map((node, index) => addId(node, index.toString()))
}
// 给我生成一个方法；方法有三个参数(isRelSel, arr，nodeId);isRelSel是个布尔值，表示更改是否会造成所在节点更新；如果isRelSel是true,过滤出item.preNodes.includes(arr，nodeId) || item.nodeId == nodeId得到影响的节点，其余为未受到影响的节点；如果isRelSel是false,咋要过滤出item.preNodes.includes(arr，nodeId) 得到影响的节点，其余为未受到影响的节点;方法返回一个对象，对象有两个属性一个是未受影响的节点集合，另一个是受到影响的点的集合
function classifyNodes(isRelSel, arr, nodeId) {
  const affectedNodes = isRelSel
    ? arr.filter((item) => item.preNodes.includes(nodeId) || item.nodeId === nodeId)
    : arr.filter((item) => item.preNodes.includes(nodeId))
  const unaffectedNodes = arr.filter((item) => !affectedNodes.includes(item))
  return {
    affectedNodes,
    unaffectedNodes
  }
}

// 是否是大模型类型节点
function nodeIsLLM(nodeType) {
  return hasBatchTypes.includes(nodeType)
}

const baseValueTypes = [
  {
    value: 'Code',
    label: 'Code'
  },
  {
    value: 'Pdf',
    label: 'Pdf'
  },
  {
    value: 'Image',
    label: 'Image'
  },
  {
    value: 'String',
    label: 'String'
  },
  {
    value: 'Object',
    label: 'Object'
  },
  {
    value: 'Integer',
    label: 'Integer'
  },
  {
    value: 'Boolean',
    label: 'Boolean'
  },
  {
    value: 'Array<String>',
    label: 'Array<String>'
  },
  {
    value: 'Array<Object>',
    label: 'Array<Object>'
  },
  {
    value: 'Array<Integer>',
    label: 'Array<Integer>'
  },
  {
    value: 'Array<Boolean>',
    label: 'Array<Boolean>'
  }
]
//  阻止双手缩放，但支持滚动
const handleNoWheel = (event) => {
  // 检查是否按下了 Ctrl 键（Windows）或 Command 键（Mac）
  if (event.ctrlKey || event.metaKey) {
    // 如果按下了这些键，很可能是在尝试缩放
    event.preventDefault()
    return false
  }

  // 检查是否是双指触摸板操作
  if (event.deltaY && Math.abs(event.deltaY) === 0) {
    // 可能是双指缩放操作
    event.preventDefault()
    return false
  }

  // 允许正常的滚动
  return true
}

const specialNames = {
  $$TY_KNOWLEDGE_QUERY$$: 'Query',
  $$TY_PLUGIN_DUBBO_PARAMS$$: 'dubbo入参',
  $$TY_KNOWLEDGE_REF_BASE_ID$$: '知识库ID',
  $$TY_INTENT_QUERY$$: 'query'
}

export {
  returnSpotPostion,
  nodesBase,
  modelsBase,
  showSidebar,
  validateRef,
  validateRef2,
  validPName,
  opList,
  formatTime,
  validateTreeInput,
  isValueValid,
  getReferOps,
  getBatchRefer,
  flowStatusArr,
  flowHeaderStatus,
  getPreNodeIdsFn,
  getPreNodesByNodesLines,
  getReferOpsByNodesLines,
  getBatchReferByNodesLine,
  filterImgRefTree,
  filterImgBatchRefTree,
  filterNodesByReference,
  retNodeReferArr,
  baseCode,
  getRalNodes,
  getNotRalNodes,
  initOpsByNodeList,
  addIdToNodes,
  classifyNodes,
  outputsTreeArr,
  inputCanRefTypes,
  hasBatchTypes,
  nodeIsLLM,
  handleNoWheel,
  filterRefByTypes,
  specialNames,
  flowStatusObjBase,
  baseValueTypes
}
