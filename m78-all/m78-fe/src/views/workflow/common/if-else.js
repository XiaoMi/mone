import { conditionTypes } from './base.js'
import { initBaseNode } from './init-node.js'
// 第一条if的高度
export const ifFistItemH = 66
// 除了第一条之外的其余条的高度
export const ifItemH = 44
// newPrecondition title的高度 25
export const conditionHeaderH = 67 + 25
export const IfTittleH = 27
export const ifPb = 5
export const ifElseMargin = 10
export const elseHeight = 47
export const conditionPb = 10
// 条件选择器默认高度
export const conditionH = 265
// 一条分支的高度
export const branchH = 120
// 条件的padding top  +padding bottom + title
export const conditionOtherH = 10 + 10 + 22
export const IfElseMargin = 10

export const addIfElseByNodes = (nodes, createIFElseFn) => {
  let newNodes = []
  nodes.forEach((node) => {
    newNodes.push(node)
    if (['precondition'].includes(node.nodeType)) {
      const { coreSetting } = node
      const ifAllH =
        coreSetting.length > 1 ? ifFistItemH + (coreSetting.length - 1) * ifItemH : ifFistItemH
      const elseTop = conditionHeaderH + (ifAllH + IfTittleH + ifPb * 2) + ifElseMargin
      const ifElseNodes = createIFElseFn(node, {
        x: 10,
        y: elseTop
      })
      const res = ifElseNodes.map((item) => {
        if (item.nodeType == 'nodeif') {
          item.coreSetting = node?.coreSetting || []
          return item
        } else {
          return item
        }
      })
      newNodes = [...newNodes, ...res]
    } else if (['newPrecondition'].includes(node.nodeType)) {
      // coreSetting是个数组
      const { coreSetting } = node
      const { sonNodes, totalH } = calcItemPosition(coreSetting.conditionExpress, node)
      newNodes[newNodes.length - 1].style.height = `${totalH}px`
      // 循环算出各个条件的position;
      newNodes = [...newNodes, ...sonNodes]
    }
  })
  return newNodes
}

export const calcItemPosition = (obj, parentNode) => {
  const keys = Object.keys(obj)
  const arr = keys.map((k) => {
    const newObj = {}
    newObj[k] = obj[k]
    return newObj
  })
  const nodeArr = []

  // 将条件按照key对象中的key从小到达排序，复数的放在最后
  const conditions = sortConditions(arr)
  // 当前累计高度
  let curH = conditionHeaderH
  for (let i = 0; i < conditions.length; i++) {
    const item = conditions[i]
    const keys = Object.keys(item)
    // 该对象只有一个key 就是Id;
    const conditionId = keys[0]
    // 当前条件存了几组值 val
    const val = item[conditionId]
    // debugger
    const condPosition = {
      x: 10,
      y: curH
    }
    // 当前这一个条件的高度
    const curCondH =
      conditionId < 0 ? 37 : conditionOtherH + ifFistItemH + (val.length - 1) * ifItemH
    // 10 每一个条件的间距
    curH = curH + curCondH + 10
    const newNode =
      conditionId < 0
        ? createElseEnd(parentNode, condPosition)
        : createIf({ parentNode, ifId: conditionId, position: condPosition })
    // 将这个newNode添加到图表中
    newNode.coreSetting = val
    nodeArr.push(newNode)
  }
  return { sonNodes: nodeArr, totalH: curH }
}

/**
 * 对条件数组进行排序
 * @param {Array} conditions 条件数组
 * @returns {Array} 排序后的数组
 */
export const sortConditions = (conditions) => {
  // debugger
  return conditions.sort((a, b) => {
    const keyA = Object.keys(a)[0]
    const keyB = Object.keys(b)[0]

    // 将键转换为数字
    const numA = parseInt(keyA)
    const numB = parseInt(keyB)

    // 检查是否为有效数字
    const isValidA = !isNaN(numA)
    const isValidB = !isNaN(numB)

    // 处理特殊情况
    if (!isValidA && !isValidB) return 0 // 两者都不是数字，保持原顺序
    if (!isValidA) return 1 // A 不是数字，放到后面
    if (!isValidB) return -1 // B 不是数字，放到后面

    // 正数在前，负数在后
    if (numA >= 0 && numB < 0) return -1
    if (numA < 0 && numB >= 0) return 1

    // 同为正数或同为负数时，按绝对值大小排序
    return Math.abs(numA) - Math.abs(numB)
  })
}

// 如果node是precondition 则需要设置height
export const reshowStyle = (node) => {
  if (['precondition'].includes(node.nodeType)) {
    const cs = JSON.parse(node.coreSetting)
    const ifAllH = cs.length > 1 ? ifFistItemH + (cs.length - 1) * ifItemH : ifFistItemH
    const elseTop = conditionHeaderH + (ifAllH + IfTittleH + ifPb * 2) + ifElseMargin
    return {
      style: {
        height: elseTop + elseHeight + conditionPb + 'px'
      }
    }
  } else {
    return {}
  }
}

const returnElseId = (pNodeId) => {
  return `${pNodeId}_-1`
}

export const createIf = ({ parentNode, ifId = 1, position = { x: 10, y: conditionHeaderH } }) => {
  const { id } = parentNode
  const elseId = returnElseId(id)
  const ifNode = initBaseNode(position, 'nodeif', {
    draggedInfo: null,
    nodeId: `${id}_${ifId}`
  })
  // 子节点需要增加属性 parentNode
  const commonData = {
    parentNode: `${id}`
  }
  return { ...ifNode, elseId, ...commonData }
}

// 创建else节点
export const createElseEnd = ({ id }, elseP) => {
  const elseId = returnElseId(id)

  // 如果是条件选择器则需要在生成一个if的node 和一个else的node
  const elseNode = initBaseNode(elseP, 'nodeelse', {
    draggedInfo: null,
    nodeId: elseId
  })
  // 子节点需要增加属性 parentNode
  const commonData = {
    parentNode: `${id}`
  }
  return { ...elseNode, ...commonData }
}

// 根据 parentId 创建if else节点 , 如果是新生成node不需要传elsePosition
export const createIFElse = (node, elseP = { x: 10, y: 210 }) => {
  const ifElseNodes = [createIf({ parentNode: node }), createElseEnd(node, elseP)]
  return ifElseNodes
}

// 获取新增if的id
export const getNewIfIdPosition = (nodes, parentId) => {
  const sons = nodes.filter((it) => it.parentNode == parentId)
  const filterIf = sons.filter((it) => it.nodeType == 'nodeif')
  const elseNode = sons.find((it) => it.nodeType == 'nodeelse')
  const ifIds = filterIf.map((it) => {
    const arr = it.id.split('_')
    return arr[1]
  })
  console.log('elseNode', elseNode)
  const curMax = Math.max(...ifIds)
  return {
    id: curMax + 1,
    position: elseNode.position
  }
}

/**
 * 获取指定 data-id 的元素的实际高度
 * @param {string} id 要查找的元素的 data-id 值
 * @returns {number} 元素的实际高度（像素），如果没找到元素则返回 null
 */
export const getNodeHeight = (id) => {
  // 查找具有指定 data-id 的元素
  const element = document.querySelector(`[data-id="${id}"]`)

  if (element) {
    // 获取元素的实际高度（包括内边距和边框，但不包括外边距）
    return element.offsetHeight
  } else {
    console.warn(`未找到 data-id 为 "${id}" 的元素`)
    return null
  }
}

export const getToChangeNodes = ({ nodes, parentNode, ifNode }) => {
  const ifRealId = ifNode.id.split('_')[1]
  return nodes.filter((it) => {
    const itRealId = it.id.split('_')[1]
    return (
      //  同一个父节点，并且不是自己，并且id<0 或者 id>ifRealId
      it.parentNode == parentNode && it.id != ifNode.id && (itRealId < 0 || itRealId > ifRealId)
    )
  })
}

/**
 * 从包含数字和单位的字符串中提取数字
 * @param {string} str 包含数字和单位的字符串
 * @returns {number} 提取出的数字
 */
export const extractNumber = (str) => {
  return Number(str.replace(/[^\d]/g, ''))
}

// 增加条件
export const addConditionFn = ({ parentNode, nodes, updateNode, addNodes }) => {
  // 1,增加父节点的高度
  const { style } = parentNode
  const newStyle = {
    ...style,
    height: `${extractNumber(style.height) + branchH}px`
  }
  updateNode(parentNode.id, {
    ...parentNode,
    style: newStyle
  })
  // 2, 增加一个If的node,
  const { id, position } = getNewIfIdPosition(nodes, parentNode.id)
  console.log('position', position, nodes)
  const newIf = createIf({ parentNode: parentNode, ifId: id, position })
  console.log('newIf', newIf)
  addNodes(newIf)
  //  3 把最后那个else向下移动；
  const elseNode = nodes.find((it) => it.parentNode == parentNode.id && it.nodeType == 'nodeelse')
  console.log('elseNode', elseNode)
  const newPosition = {
    ...elseNode?.position,
    y: elseNode?.position.y + branchH
  }
  updateNode(elseNode.id, {
    ...elseNode,
    position: newPosition
  })
}

export const removeConditionFn = ({ nodes, nodeIf, removeNodes, updateNode, getNode }) => {
  console.log('node.value', nodeIf.value, removeNodes)
  // 1.删除当前node
  const { id, parentNode } = nodeIf
  removeNodes(nodeIf)
  // 2.获取data-id == id的node的元素的实际高度
  const nodeH = getNodeHeight(id)
  // 3.获取他后面的所有子节点，都向上移动当前节点的高度
  console.log('nodeH', nodeH)
  const toChangeNode = getToChangeNodes({
    nodes,
    parentNode,
    ifNode: nodeIf
  })
  const toReduceH = nodeH + IfElseMargin
  toChangeNode.forEach((ele) => {
    ele.position = {
      ...ele.position,
      y: ele.position.y - toReduceH
    }
    updateNode(ele.id, ele)
  })
  // 4.减少父节点的高度
  const pNode = getNode.value(nodeIf.parentNode)
  const { style } = pNode
  const newStyle = {
    ...style,
    height: `${extractNumber(style.height) - toReduceH}px`
  }
  updateNode(parentNode, {
    ...pNode,
    style: newStyle
  })
  console.log('pNode', pNode)
}
