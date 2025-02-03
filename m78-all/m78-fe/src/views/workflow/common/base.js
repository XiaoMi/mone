import { getHandleId } from './edges-transform.js'
import { nodeIsLLM, addIdToNodes } from '../work-flow/baseInfo.js'

export const conditionTypes = ['precondition', 'newPrecondition']
// 遍历nodesBase对象的每一个属性的值val，如果val[keyName]是true,拿到val.type推入数组arr中,返回这个arr;
export const filterNodeTypes = (nodesObj, keyName) => {
  const arr = []
  for (const key in nodesObj) {
    if (nodesObj[key][keyName] === true) {
      arr.push(nodesObj[key].type)
    }
  }
  return arr
}

/**
 * 检查数组是否为key连续的
 *
 * @param {Array} arr 要检查的数组
 * @return {boolean} 如果数组是key连续的返回true，否则返回false
 */
function isKeyContinuous(arr) {
  // 检查相邻元素的key是否连续
  for (let i = 1; i < arr.length; i++) {
    if (arr[i].key !== arr[i - 1].key + 1) {
      return false
    }
  }
  return true
}

export const intentSaveBefore = (nodes, edges) => {
  const intentNodes = nodes.filter((it) => it.nodeType == 'intentRecognition')
  for (let i = 0; i < intentNodes.length; i++) {
    const { id, handles, coreSetting } = intentNodes[i]
    const { intentMatch } = coreSetting
    const filetrOther = intentMatch.filter((it) => it.key > 0)
    // 按照key的大小排序
    const sortArr = isKeyContinuous(filetrOther)
    if (!sortArr) {
      for (let k = 0; k < filetrOther.length; k++) {
        const item = filetrOther[k]
        if (item.key != k + 1) {
          const oldHandleId = getHandleId(item.key)
          // 说明前面缺项了
          const newKey = k + 1
          const newHandleId = getHandleId(newKey)
          // 1. 修改边
          const edge = edges.find((edge) => edge.sourceHandle === oldHandleId && edge.source == id)
          if (edge) {
            edge.sourceHandle = newHandleId
          }
          // 2. 修改handle的可以
          const handle = handles.find((it) => it.id == oldHandleId)
          if (handle) {
            handle.id = newHandleId
          }
          // 3. 修改当前的key
          item.key = newKey
        }
      }
    }
  }
  return {
    nodes: nodes.map((it) => {
      // 过滤掉resInputs，resOutputs
      const { resInputs, resOutputs, ...rst } = it
      return {
        ...rst,
        coreSetting: JSON.stringify(it.coreSetting)
      }
    }),
    edges
  }
}

// coreSetting保存
export const transformSetting = (item, nodes) => {
  if (item.nodeType == 'knowledge') {
    return { ...item.coreSetting, minMatch: item?.coreSetting?.minMatch / 100 || null }
  } else if (!conditionTypes.includes(item.nodeType)) {
    return item.coreSetting
  } else {
    // 找到孩子if的coreSetting;
    const children = nodes.filter((it) => {
      return it.parentNode == item.id && ['nodeif', 'nodeelse'].includes(it.nodeType)
    })
    if (children.length == 0) return []
    if (['precondition'].includes(item.nodeType)) {
      return returnIfArr(children, nodes)
    } else if (['newPrecondition'].includes(item.nodeType)) {
      return returnIfArrNew(children, nodes)
    }
  }
}
/**
 * 尝试将字符串转换为数字，如果无法转换则返回原字符串
 *
 * @param {string} str - 需要转换的字符串
 * @returns {number|string} - 如果可以转换为数字则返回数字，否则返回原字符串
 */
function convertToNumberIfPossible(str) {
  // 尝试将字符串转换为数字
  const num = Number(str)

  // 检查转换结果是否为有效数字
  if (!isNaN(num)) {
    return num
  }

  // 如果无法转换为数字，返回原字符串
  return str
}

// 用于获取newPrecondtion的引用值
export const getReferVal = (referenceInfo, nodes) => {
  if (!referenceInfo || referenceInfo?.length == 0)
    return {
      referenceName: '',
      referenceNodeId: ''
    }
  const [nodeId, ...other] = referenceInfo
  const referNode = nodes.find((allNodeItem) => allNodeItem.id == nodeId)
  const refereOutputs =
    referNode?.nodeType == 'begin' ? referNode?.inputs || [] : referNode?.outputs || []
  const referenceName = getReferName(refereOutputs, other)
  return {
    referenceName,
    referenceNodeId: nodeId
  }
}
// 获取newPrecondition的coreSetting
export const returnCoreSettingNew = (coreSetting, nodes) => {
  return coreSetting.map((coreItem) => {
    const { referenceInfo, referenceInfo2, type2, value2 } = coreItem
    // operator前面
    const beforeOP = getReferVal(referenceInfo, nodes)
    // operator后面
    const afterOP = getReferVal(referenceInfo2, nodes)

    return {
      ...coreItem,
      referenceName: beforeOP.referenceName,
      name: beforeOP.referenceName,
      flowId: convertToNumberIfPossible(beforeOP.referenceNodeId),

      referenceName2: type2 == 'reference' ? afterOP.referenceName : '',
      flowId2: convertToNumberIfPossible(type2 == 'reference' ? afterOP.referenceNodeId : ''),
      name2: type2 == 'reference' ? afterOP.referenceName : '',
      value2: convertToNumberIfPossible(value2)
    }
  })
}
// // 返回条件选择器的coreSetting
// export const returnCoreSetting = (coreSetting, nodes) => {
//   console.log('nodes', nodes)
//   return coreSetting.map((coreItem) => {
//     const { referenceInfo, referenceInfo2, type2, value2 } = coreItem
//     // operator前面
//     const beforeOP = getReferVal(referenceInfo, nodes)
//     // operator前面
//     const afterOP = getReferVal(referenceInfo2, nodes)

//     const name = beforeOP.referenceName
//     const name2 = type2 == 'reference' ? afterOP.referenceName : ''
//     const flowId2Obj = {}
//     if (type2 == 'reference') {
//       flowId2Obj.flowId2 = convertToNumberIfPossible((referenceInfo2 && referenceInfo2[0]) || '')
//     }
//     const res = {
//       ...coreItem,
//       flowId: convertToNumberIfPossible((referenceInfo && referenceInfo[0]) || ''),
//       referenceName: name,
//       name,
//       referenceName2: type2 == 'value' ? '' : name2,
//       name2,
//       value2: convertToNumberIfPossible(value2),
//       ...flowId2Obj
//     }
//     if (type2 == 'value') {
//       delete res.flowId2
//     }
//     return res
//   })
// }

export const returnIfArrNew = (children, nodes) => {
  const obj = {}
  children.map((child) => {
    const { coreSetting, id } = child
    // 拿到if的真正id
    const realId = id.split('_')[1]
    if (realId < 0) {
      obj[realId] = []
    } else {
      obj[realId] = returnCoreSettingNew(coreSetting, nodes)
    }
  })
  return { conditionExpress: obj }
}

export const returnIfArr = (children, nodes) => {
  const newChildren = children.filter((it) => {
    const { id } = it
    // 拿到if的真正id
    const realId = id.split('_')[1]
    return realId >= 0
  })
  const res = newChildren.map((child) => {
    const { coreSetting, id } = child
    // 拿到if的真正id
    const realId = id.split('_')[1]
    if (realId >= 0) {
      return returnCoreSettingNew(coreSetting, nodes)
    } else {
      return {}
    }
  })
  return res[0]
}

// coreSetting回显
export const reshowCoreSetting = (preVal, nodeType) => {
  if (nodeType == 'knowledge' && !preVal.knowledgeRefType) {
    return {
      ...preVal,
      knowledgeRefType: 'knowlegeList'
    }
  } else {
    return preVal
  }
}

// export const appendToLines = (allLines) => {
//   const step = 5
//   const appendLine = (start) => {
//     const end = start + step > allLines.length ? allLines.length : start + step
//     const toInsert = allLines.slice(start, end)
//     requestAnimationFrame(() => {
//       lines.value.push(...toInsert)
//       if (end < allLines.length) {
//         appendLine(end)
//       }
//     })
//   }
//   appendLine(0)
// }

// const appendToNodes = (allNodes) => {
//   const step = 3
//   const appendNode = (start) => {
//     const end = start + step > allNodes.length ? allNodes.length : start + step
//     const toInsert = allNodes.slice(start, end)
//     requestAnimationFrame(() => {
//       nodes.value.push(...toInsert)
//       if (end < allNodes.length) {
//         appendNode(end)
//       }
//     })
//   }
//   appendNode(0)
// }

export const mapArr = (num, other, arr, str = '') => {
  const curOther = other[num]
  const curObj = arr.find((item) => {
    const val = item.name || item.value
    return val == curOther
  })
  if (curObj) {
    str = str + (str.length == 0 ? '' : '.') + curOther
    // other还没循环完
    if (other.length > num + 1) {
      const vT = curObj.valueType || curObj.desc
      str = str + (vT?.startsWith('Array') ? '.0' : '')
      return mapArr(num + 1, other, curObj.children || [], str)
    } else {
      return str
    }
  } else {
    // 这种情况不会发生
    return str + curOther
  }
}

const getBatchInfoSpecial = (batchInfo, keyName) => {
  return batchInfo.find((item) => item.name == keyName)
}

// batch 回显
export const returnBatchInfo = (node) => {
  const { nodeType, batchType } = node
  if (nodeIsLLM(nodeType)) {
    if (batchType == 'batch') {
      const { batchInfo } = node
      const batchInfoReal = batchInfo.filter((item) => !item.name.startsWith('$$'))
      return {
        batchInfo: {
          arr: batchInfoReal,
          $$TY_BATCH_MAX_TIMES$$: getBatchInfoSpecial(batchInfo, '$$TY_BATCH_MAX_TIMES$$')?.value,
          $$TY_BATCH_TIME_INTERVAL$$:
            getBatchInfoSpecial(batchInfo, '$$TY_BATCH_TIME_INTERVAL$$')?.value || null
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
          $$TY_BATCH_MAX_TIMES$$: '1',
          $$TY_BATCH_TIME_INTERVAL$$: 1500
        },
        batchType: 'single'
      }
    }
  } else {
    return {}
  }
}

export const rewShowLLMImgInputs = (allInputs) => {
  const imgType = 'imageReference'
  const inputsImgArr = allInputs.filter((item) => item.type.startsWith(imgType))
  const realInputs = allInputs.filter((item) => !item.type.startsWith(imgType))
  return {
    inputs: realInputs.map((item) => {
      return {
        ...item,
        type: item.type == 'batch' ? 'reference' : item.type
      }
    }),
    inputsImg: inputsImgArr.map((item) => {
      return {
        ...item,
        type: imgType
      }
    })
  }
}

//  inputs 编辑回显
export const editReInputs = (node) => {
  const { inputs, nodeType } = node
  if (nodeType == 'llm' && node.batchType == 'batch') {
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

export const handleBatch = (node, obj, inputDetails) => {
  if (nodeIsLLM(node.nodeType) && node.batchType == 'batch') {
    const { nodeType } = node
    let referBatchArr = []
    // 引用的是批处理中的数据
    if (nodeType == 'llm') {
      referBatchArr = node.inputs.filter((input) => {
        return input.type == 'reference' && input?.referenceInfo[0] == node.id
      })
    } else {
      // llmImage
      const inputs = [...node.inputs, ...node.inputsImg]
      referBatchArr = inputs.filter((input) => {
        return input?.referenceInfo[0] == node.id
      })
    }
    try {
      if (inputDetails?.length > 0) {
        const inputsDetailNew = inputDetails.filter((output) => {
          return referBatchArr.find((batchArrI) => batchArrI.name == output.name)
        })
        const outputsArr = inputsDetailNew?.map((item) => {
          return {
            ...item,
            value: JSON.parse(item.value || [])
          }
        })
        const filterArr = outputsArr.filter((output) => {
          return referBatchArr.find((batchArrI) => batchArrI.name == output.name)
        })
        if (filterArr.length > 0) {
          const ouputsDetailsNum = filterArr.map((item) => item.value.length)
          const maxNum = Math.max(...ouputsDetailsNum)
          const minNum = Math.min(...ouputsDetailsNum)
          return {
            maxNum,
            minNum,
            filterArr // 返回需要批处理的 nodeId
          }
        }
      }
    } catch (err) {
      console.log('err', err)
    }
  }
}

export const filterNoOutput = (nodes) => {
  return nodes.filter(
    (item) => !['nodeif', 'nodeelse', ...conditionTypes, 'manualConfirm'].includes(item.nodeType)
  )
}

// inputs编辑回显
export const reshowInputs = (it) => {
  const { inputs, ...rst } = retInputObj(it)
  if (it.nodeType == 'begin') {
    return {
      inputs: addIdToNodes(inputs),
      ...rst
    }
  }
  return { inputs, ...rst }
}

export const retInputObj = (it) => {
  const allInputs = editReInputs(it)
  if (it.nodeType == 'llmImageUnderstand') {
    return rewShowLLMImgInputs(allInputs)
  } else if (it.nodeType == 'plugin') {
    const { pluginType } = JSON.parse(it.coreSetting)
    // dubbo类型的插件进行特殊处理 $$TY_PLUGIN_DUBBO_PARAMS$$
    if (pluginType == 'dubbo') {
      return {
        inputs: allInputs.filter((item) => !item.name.startsWith('$$')),
        inputsPlugin: {
          dubboParam: allInputs.find((item) => item.name == '$$TY_PLUGIN_DUBBO_PARAMS$$')?.value
        }
      }
    } else {
      return { inputs: allInputs }
    }
  } else if (it.nodeType == 'knowledge') {
    const coreSetting = it.coreSetting || '{}'
    const resCoreSetting = JSON.parse(coreSetting)
    let referenceInfo = []
    if (resCoreSetting.knowledgeRefType == 'reference') {
      const knowledgeArr = it.inputs.filter((item) => item.name == '$$TY_KNOWLEDGE_REF_BASE_ID$$')
      referenceInfo = knowledgeArr[0]?.referenceInfo
    }
    return {
      inputs: allInputs.filter((item) => item.name == '$$TY_KNOWLEDGE_QUERY$$'),
      knowledgeInputs: [
        {
          name: '$$TY_KNOWLEDGE_REF_BASE_ID$$',
          type: 'reference',
          referenceInfo
        }
      ]
    }
  } else {
    return {
      inputs: allInputs.map((it) => {
        const { schema, ...rst } = it
        return {
          ...rst,
          ...(schema ? { children: JSON.parse(schema) } : {})
        }
      })
    }
  }
}

//截取掉字符串的.0，返回新字符串
export const removeZero = (str) => {
  return str.replace(/\.0$/, '')
}

export const getReferName = (outputs, other) => {
  const mapArrRes = mapArr(0, other, outputs)
  return mapArrRes
}

const referTypes = ['reference', 'imageReference']
export const saveTransInputs = (inputs, nodeItem, getNodeFn) => {
  return inputs.map((input) => {
    // 引用的类型的
    if (referTypes.includes(input.type)) {
      if (input.referenceInfo) {
        const { referenceInfo } = input
        const referNodeId = referenceInfo[0]
        //  获取引用node;
        const referNode = getNodeFn(referNodeId)
        //  如果引用的是开始节点，引用的是开始节点的inputs, 其他类型的是outputs
        let refereInputs = referNode?.[referNode.nodeType == 'begin' ? 'inputs' : 'outputs'] || []
        if (referNode) {
          // 如果引用的是code llm
          const [nodeId, ...other] = referenceInfo
          let referenceName
          // 如果是大模型/图片大模型且批量处理
          if (
            nodeIsLLM(nodeItem.nodeType) &&
            nodeItem.batchType == 'batch' &&
            referNodeId == nodeItem.id
          ) {
            // 引用的是自己；
            const newOther = other.map((item, otherIndex) => {
              if (otherIndex == 0 && item.endsWith('.0')) {
                return removeZero(item)
              }
              return item
            })
            let newStr = newOther.join('.')
            if (newStr.endsWith('.0')) {
              newStr = newStr.substring(0, newStr.length - 2)
            }
            referenceName = newStr
          } else {
            referenceName = getReferName(refereInputs, other)
          }
          //  llm 和llmImg 传的type不一样
          const typeObj =
            input.referenceInfo[0] == nodeItem.id
              ? { type: input.type == 'reference' ? 'batch' : 'imageReferenceBatch' }
              : {}
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
}

/**
 * 将树形数据转换为schema
 * @param {Array} treeDataItem - 树形数据
 * @returns {Object} - schema对象
 */
export const loopTreeToSchema = (treeDataItem) => {
  function processNode(node) {
    let property = {}

    if (node.valueType === 'Object') {
      property = {
        type: 'object',
        properties: {},
        required: [],
        additionalProperties: true // 允许额外的属性
      }
      if (node.children && node.children.length > 0) {
        node.children.forEach((child) => {
          property.properties[child.name] = processNode(child)
          if (child.required) {
            property.required.push(child.name)
          }
        })
      }
    } else if (node.valueType && node.valueType.startsWith('Array<')) {
      const itemType = node.valueType.match(/Array<(.+)>/)[1] // 提取 Array<...> 中的类型
      console.log('itemType', itemType)
      const typeVal = mapValueTypeToSchemaType(itemType)
      console.log('typeVal', typeVal)
      property = {
        type: 'array',
        items: { type: typeVal }
      }
      console.log('property', property)
      if (node.children) {
        property.items = processNode({
          type: typeVal,
          children: node.children
        })
      }
    } else {
      property.type = mapValueTypeToSchemaType(node.valueType || node.type)
    }

    return property
  }

  function mapValueTypeToSchemaType(valueType) {
    if (valueType.startsWith('Array<')) {
      return 'array'
    }
    switch (valueType) {
      case 'Code':
      case 'Image':
      case 'String':
      case 'string':
        return 'string'
      case 'Integer':
      case 'integer':
        return 'integer'
      case 'Boolean':
      case 'boolean':
        return 'boolean'
      case 'Object':
      case 'object':
        return 'object'

      default:
        return 'string' // 默认为string类型
    }
  }
  const schema = {
    type: mapValueTypeToSchemaType(treeDataItem.valueType),
    properties: {},
    required: []
  }
  console.log('treeDataItem', treeDataItem)
  const children = treeDataItem.children || []
  children.forEach((node) => {
    schema.properties[node.name] = processNode(node)
    if (node.required) {
      schema.required.push(node.name)
    }
  })
  console.log('schema', schema)
  return schema
}

// 处理知识库的output为正常的结构
export const knowledgeOutputs = (arr) => {
  const res = arr.map((it) => {
    return {
      ...it,
      name: it.value,
      valueType: it.desc,
      children: it.children ? knowledgeOutputs(it.children) : []
    }
  })
  return res
}

// 根据引用的节点类型返回不同的outputs
export const getOutputTree = (referNode) => {
  const { nodeType } = referNode
  if (nodeType == 'begin') {
    return referNode.inputs
  } else if (nodeType == 'knowledge') {
    return knowledgeOutputs(referNode.outputs)
  } else {
    return referNode.outputs
  }
}

/**
 * 判断两个字符串数组是否每一项一一对应相等
 *
 * @param arr1 第一个字符串数组
 * @param arr2 第二个字符串数组
 * @returns 如果两个数组完全相等则返回true，否则返回false
 */
export const areArraysEqual = (arr1, arr2) => {
  // 首先检查数组长度是否相等
  if (arr1.length !== arr2.length) {
    return false
  }

  // 逐一比较每个元素
  for (let i = 0; i < arr1.length; i++) {
    if (arr1[i] !== arr2[i]) {
      return false
    }
  }
  // 如果所有元素都相等，返回true
  return true
}

/**
 * 从嵌套对象结构中提取名称并返回数组
 *
 * @param obj 嵌套的对象结构
 * @returns 包含所有name属性值的数组
 */
export const extractNames = (obj) => {
  const result = []
  // 递归函数来遍历对象结构
  function traverse(currentObj) {
    if (currentObj && currentObj.data && currentObj.data.name) {
      result.unshift(currentObj.data.name)
    }
    if (currentObj && currentObj.parent) {
      traverse(currentObj.parent)
    }
  }

  traverse(obj)
  return result
}

// typesArr 如果树形结构的valueType 属于typesArr，则不可选择.最外层的元素保持原样
export const typesDisabled = (arr, typesArr) => {
  const setDisabled = (node, isRoot = false) => {
    const { valueType, children = [], disabled } = node

    return {
      ...node,
      // 如果是根节点，保持原有的 disabled 状态
      disabled: isRoot ? disabled : typesArr.includes(valueType),
      ...(children.length && {
        children: children.map((child) => setDisabled(child))
      })
    }
  }

  return arr.map((node) => setDisabled(node, true))
}

export const switchEmptyOps = ['IS_EMPTY', 'IS_NOT_EMPTY', 'IS_TRUE', 'IS_FALSE']

export const checkInput = (input) => {
  // 如果输入是 undefined 或 null，返回 false
  if (input === undefined || input === null) {
    return false
  }
  // 如果输入是字符串类型
  if (typeof input === 'string') {
    // 去除首尾空格后，如果是空字符串，返回 false
    return input.trim() == '' ? false : true
  }
  return true
}
