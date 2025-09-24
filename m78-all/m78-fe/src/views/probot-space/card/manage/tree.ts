import { computed } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import { useProbotCardStore } from '@/stores/card'

export function generateUUID() {
  return uuidv4() + ''
}
/**
 * 将扁平对象结构转换为树形结构
 *
 * @param {Object} flatObj - 扁平的对象结构
 * @param {string} rootKey - 根节点的key
 * @returns {Object} 树形结构
 */
export function flatToTree(flatObj: any, rootKey: string): any {
  const cardStore = useProbotCardStore()
  const dataTypeMap = computed(() => cardStore.dataTypeMap)
  // 创建一个新对象来存储树形结构
  const treeObj: any = {}

  // 递归函数来构建树
  function buildTree(key: string): any {
    const flatObjMap = flatObj[key]
    if (flatObjMap) {
      const node = flatObjMap
      // 如果有子节点，递归构建子树
      const nodeSetting = dataTypeMap.value[node.type]
      const child = nodeSetting?.children || []
      if (node.property?.slots?.length && Array.isArray(node.property?.slots)) {
        node.children = node.property.slots.map((slot, index) => {
          return {
            ...JSON.parse(JSON.stringify(child[0])),
            ...slot,
            uniqueKey: slot.uniqueKey || generateUUID(),
            label: child[0].treeLabel + (index + 1),
            row: nodeSetting.row,
            children: slot.children.filter((childKey: string) => buildTree(childKey)),
            parentType: node.type
          }
        })
      }
      // label处理
      if (!node.label && nodeSetting?.customizeType === 'outer') {
        if (!nodeSetting?.treeLabelIndex) {
          dataTypeMap.value[node.type].treeLabelIndex = 1
        } else {
          dataTypeMap.value[node.type].treeLabelIndex++
        }
        node.label =
          dataTypeMap.value[node.type].treeLabel + dataTypeMap.value[node.type].treeLabelIndex
      }

      // 处理 children
      if (node.children) {
        node.children = node.children?.map((child) => {
          if (typeof child === 'string') {
            return buildTree(child)
          }
          return {
            ...child,
            children: child.children ? child.children?.map((childId) => buildTree(childId)) : []
          }
        })
      }
      return {
        ...JSON.parse(JSON.stringify(nodeSetting)),
        ...node
      }
    } else {
      return
    }
  }

  // 从根节点开始构建树
  treeObj[rootKey] = buildTree(rootKey)
  cardStore.setDataTypeMap(dataTypeMap.value)
  return [treeObj[rootKey]]
}

/**
 * 将树形结构扁平化为对象
 *
 * @param {Array} tree - 输入的树形结构
 * @param {string} [keyField='uniqueKey'] - 用作对象键的字段名
 * @returns {Object} 扁平化后的对象
 */
export function flattenTree(tree, keyField = 'uniqueKey') {
  const result = {}

  /**
   * 递归函数，用于遍历树并扁平化
   *
   * @param {Array} nodes - 当前处理的节点数组
   */
  function traverse(nodes) {
    nodes.forEach((node) => {
      const key = node[keyField]
      if (key) {
        const { children, ...rest } = node
        result[key] = {
          ...rest,
          property: {
            ...rest.property,
            slots: children?.map((child) => {
              if (child.type == 'container') {
                if (child.children && child.children.length > 0) {
                  traverse(child.children)
                }
                delete child?.customizeType
                delete child?.label
                delete child?.smallChildren
                delete child?.title
                delete child?.treeLabel
                delete child?.treeLabelIndex
                delete child?.column
                delete child?.row
                return {
                  ...child,
                  uniqueKey: '',
                  children: child.children ? child.children.map((v) => v[keyField]) : []
                }
              }
            })
          },
          children: []
        }
      }
    })
  }

  traverse(tree)
  return result
}
/**
 * 在指定 uniqueKey 的节点下添加新元素
 *
 * @param tree - 树结构或单个节点
 * @param uniqueKey - 指定节点的唯一标识符
 * @param newNode - 要添加的新节点
 * @param uniqueKeyName - 唯一标识符的属性名，默认为 'uniqueKey'
 * @returns 更新后的树结构
 */
export function addNodeToChildren<T extends Record<string, any>>(
  tree: T | T[],
  uniqueKey: string,
  newNode: T,
  uniqueKeyName: string = 'uniqueKey'
): T | T[] {
  function addNode(nodes: T[]): T[] {
    return nodes.map((node) => {
      if (node[uniqueKeyName] === uniqueKey) {
        return {
          ...node,
          children: [...(node.children || []), newNode]
        }
      }
      if (node.children && Array.isArray(node.children)) {
        return {
          ...node,
          children: addNode(node.children)
        }
      }
      return node
    })
  }

  if (Array.isArray(tree)) {
    return addNode(tree)
  } else if (typeof tree === 'object' && tree !== null) {
    const result = addNode([tree])
    return result[0]
  } else {
    console.warn('Invalid input: tree must be an array or an object')
    return tree
  }
}

/**
 * 在指定 uniqueKey 的节点后面添加新元素
 *
 * @param tree - 树结构或单个节点
 * @param uniqueKey - 指定节点的唯一标识符
 * @param newNode - 要添加的新节点
 * @param uniqueKeyName - 唯一标识符的属性名，默认为 'uniqueKey'
 * @returns 更新后的树结构
 */
export function addNodeAfter<T extends Record<string, any>>(
  tree: T | T[],
  uniqueKey: string,
  newNode: T,
  uniqueKeyName: string = 'uniqueKey'
): T | T[] {
  function addNode(nodes: T[]): T[] {
    const result: T[] = []
    for (const node of nodes) {
      result.push(node)
      if (node[uniqueKeyName] === uniqueKey) {
        result.push(newNode)
      }
      if (node.children && Array.isArray(node.children)) {
        node.children = addNode(node.children)
      }
    }
    return result
  }

  if (Array.isArray(tree)) {
    return addNode(tree)
  } else if (typeof tree === 'object' && tree !== null) {
    const result = addNode([tree])
    return result.length > 1 ? result : result[0]
  } else {
    console.warn('Invalid input: tree must be an array or an object')
    return tree
  }
}

/**
 * 深拷贝树结构，忽略父节点引用，并使用 uniqueKey 作为唯一标识符
 *
 * @param node - 要拷贝的树结构对象
 * @param uniqueKeyName - 用作唯一标识符的属性名，默认为 'uniqueKey'
 * @returns 深拷贝后的新树结构
 */
export function deepCloneTree<T extends Record<string, any>>(
  node: T,
  uniqueKeyName: string = 'uniqueKey'
): T {
  function clone(node: T): T {
    const clonedNode: T = { ...node }

    // 移除父节点引用
    delete clonedNode.parent

    // 设置新的唯一标识符
    clonedNode[uniqueKeyName] = generateUUID()

    // 递归克隆子节点
    if (Array.isArray(clonedNode.children)) {
      clonedNode.children = clonedNode.children?.map((child) => clone(child))
    }

    return clonedNode
  }

  return clone(node)
}
/**
 * 从树结构中删除指定 uniqueKey 的元素，如果父元素没有其他子元素且 customizeType 为 'outer' 则递归删除
 *
 * @param tree - 树结构或单个节点
 * @param uniqueKey - 要删除的元素的唯一标识符
 * @param uniqueKeyName - 唯一标识符的属性名，默认为 'uniqueKey'
 * @returns 更新后的树结构
 */
export function removeNodeByUniqueKey<T extends Record<string, any>>(
  tree: T | T[],
  uniqueKey: string,
  uniqueKeyName: string = 'uniqueKey'
): T | T[] | null {
  const cardStore = useProbotCardStore()
  const dataTypeMap = computed(() => cardStore.dataTypeMap)
  function removeNode(nodes: T[]): T[] {
    let result = nodes.filter((node) => node[uniqueKeyName] !== uniqueKey)

    result = result
      .map((node) => {
        if (node.children && Array.isArray(node.children)) {
          const updatedChildren = removeNode(node.children)
          if (
            updatedChildren.length === 0 &&
            dataTypeMap.value[node.type]?.customizeType === 'outer'
          ) {
            // If all children are removed and customizeType is 'outer', remove this node too
            return null
          } else {
            return { ...node, children: updatedChildren }
          }
        }
        return node
      })
      .filter((node): node is T => node !== null)

    return result
  }

  if (Array.isArray(tree)) {
    const result = removeNode(tree)
    return result.length > 0 ? result : null
  } else if (typeof tree === 'object' && tree !== null) {
    if (tree[uniqueKeyName] === uniqueKey) {
      return null
    }
    const result = removeNode([tree])
    return result.length > 0 ? result[0] : null
  } else {
    console.warn('Invalid input: tree must be an array or an object')
    return tree
  }
}
