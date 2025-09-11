import { MarkerType, Position } from '@vue-flow/core'
import { nodeIfElse } from './init-node.js'

// 获取line的其他配置， 意图识别、if、else 需要这些配置
export const getLineConfig = (connection, getNode) => {
  const filterTypes = [...nodeIfElse, 'intentRecognition']
  const sourceNode = getNode(connection.source)
  const { nodeType } = sourceNode
  if (!filterTypes.includes(sourceNode.nodeType)) return {}
  const newFlag = 'subSourceNodeToTargetNode'
  const ifElseTypes = ['nodeif', 'nodeelse']
  //  条件一.如果是意图识别
  if (nodeType == 'intentRecognition') {
    return { conditionFlag: newFlag }
  } else if (ifElseTypes.includes(nodeType)) {
    // 条件二.if else
    const parentNode = getNode(sourceNode.parentNode)
    if (parentNode.nodeType == 'newPrecondition') {
      return { conditionFlag: newFlag }
    } else {
      return {
        conditionFlag: nodeType == 'nodeif' ? 'if' : 'else'
      }
    }
  }
}

// 保存转换
export const saveLineTransform = (edges, getNode) => {
  // 保存转换
  const edgesVal = edges.map((item) => {
    let sourceNodeId = item.source
    // 过滤掉属性：sourceX, sourceY, targetX, targetY
    const { sourceX, sourceY, targetX, targetY, ...rest } = item
    if (!item.conditionFlag) {
      return {
        ...rest,
        sourceNodeId,
        targetNodeId: item.target
      }
    } else {
      const ifNode = getNode(item.source)
      const parentNode = getNode(ifNode.parentNode)
      // 如果是条件的节点
      if (nodeIfElse.includes(ifNode.nodeType)) {
        return {
          ...rest,
          sourceNodeId: ifNode.parentNode,
          targetNodeId: item.target,
          // 如果是newPreconcition要有sourceSubNodeId
          ...newIfHandle(ifNode, getNode)
        }
      } else {
        // 意图识别
        return {
          ...rest,
          sourceNodeId: item.source,
          targetNodeId: item.target,
          sourceSubNodeId: saveHandleId(item.sourceHandle)
        }
      }
    }
  })
  return edgesVal
}

// newPrecondition 要增加参数
export const newIfHandle = (ifNode, getNode) => {
  const parent = getNode(ifNode.parentNode)
  if (parent.nodeType == 'newPrecondition') {
    return {
      sourceSubNodeId: ifNode.id.split('_')[1]
    }
  } else {
    return {}
  }
}

//lines回显 转换
export const reshowTransform = (resLines, ifElseNodes, allNodes) => {
  // 取消转换
  return resLines.map((item, i) => {
    let source = item.sourceNodeId
    if (item.conditionFlag) {
      //
      const parentNode = allNodes.find((it) => it.id == source)
      console.log('parentNode', parentNode)
      // debugger
      const ifNodeId = `${source}_${item.sourceSubNodeId}`
      // 是if 或者else
      if (item.conditionFlag == 'if') {
        const ifNode = ifElseNodes.find((it) => {
          return it.parentNode == source && it.nodeType == 'nodeif'
        })
        source = ifNode?.id
        return {
          ...baseLine(item, i),
          source: `${source}`,
          target: `${item.targetNodeId}`
        }
      } else if (item.conditionFlag == 'else') {
        const elseNode = ifElseNodes.find(
          (ifElseItem) => ifElseItem.parentNode == source && ifElseItem.nodeType == 'nodeelse'
        )
        source = elseNode?.id
        return {
          ...baseLine(item, i),
          source: `${source}`,
          target: `${item.targetNodeId}`
        }
      } else if (item.conditionFlag == 'subSourceNodeToTargetNode') {
        return {
          type: 'source',
          position: Position.Right,
          ...baseLine(item, i),
          source:
            parentNode?.nodeType == 'newPrecondition'
              ? `${item.sourceNodeId}_${item.sourceSubNodeId}`
              : `${source}`,
          target: `${item.targetNodeId}`,
          ...returnSourceHandle(parentNode, item)
        }
      }
    } else {
      return {
        ...baseLine(item, i),
        source: `${item.sourceNodeId}`,
        target: `${item.targetNodeId}`
      }
    }
  })
}

const returnSourceHandle = (parentNode, item) => {
  if (parentNode?.nodeType == 'newPrecondition') {
    return {}
  } else {
    return {
      sourceHandle: getHandleId(item.sourceSubNodeId)
    }
  }
}

const baseLine = (item, i) => {
  return {
    ...item,
    id: `edge_${i}`,
    markerEnd: MarkerType.ArrowClosed,
    type: 'button',
    data: {
      showBtn: false
    }
  }
}

export const initIntentHandle = (nodeType, attachArr) => {
  if (nodeType != 'intentRecognition') {
    return {}
  }
  const { intentMatch } = attachArr
  const handles = intentMatch.map((item) => {
    return {
      id: getHandleId(item.key), // 这里将key拼接一下，保存的时候要获取key,编辑回显也要获取key
      type: 'source',
      position: Position.Right
    }
  })
  return { handles }
}

export const reshowHandles = (node, coreSetting) => {
  if (node.nodeType != 'intentRecognition') {
    return {}
  }
  return initIntentHandle(node.nodeType, coreSetting)
  // const {}
}

// 获取handleId，根据id拼接
export const getHandleId = (id) => {
  return `${id}_handle-right` // 这
}
// 获取handleId
export const saveHandleId = (str) => {
  const keyArr = str.split('_') // 从这里要拿到id
  return keyArr[0] || null
}

const sameTargetMMsg = '请确保每个条件分支指向不同的节点，以保持逻辑清晰和正确性。'
// 检查是否有同样的线存在,返回一个对象{hasSame,conditionSameTip}，如果conditionSameTip有值用户需弹窗提示
export const checkSameEdge = (allEdges, allNodes, connection) => {
  let conditionSameTip = ''
  // 是否有兄弟节点已经指向了同样的target
  let hasBrotherTargetTo = false
  // 是否有同样链接的edge
  const hasSame = allEdges.find((it) => {
    // 意图识别 不同的意图，可能会出现这种
    if (it.source == connection.source && it.target == connection.target && it.sourceHandle) {
      conditionSameTip = sameTargetMMsg
    }
    // 说明是从if节点出来的,查看其他兄弟节点是否有相同的target
    if (connection.source.includes('_')) {
      // 同一个父级下的所有节点中有没有target是connection.target的，如果有则要提示
      const { parentNode } = allNodes.find((item) => item.id == connection.source)
      const brotherNodes = allNodes.filter(
        (item) => item.parentNode == parentNode && item.id !== connection.source
      )
      const breotherNodeIds = brotherNodes.map((item) => item.id)
      // 所有兄弟节点连出的线条
      const allBrotherLines = allEdges.filter((item) => breotherNodeIds.includes(item.source))
      if (allBrotherLines.find((item) => item.target == connection.target)) {
        conditionSameTip = sameTargetMMsg
        hasBrotherTargetTo = true
      }
    }
    return (it.source == connection.source && it.target == connection.target) || hasBrotherTargetTo
  })
  return {
    hasSame: hasSame ? true : false,
    conditionSameTip
  }
}
