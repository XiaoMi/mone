<template>
  <el-cascader
    v-model="value"
    :options="ops"
    :props="actions"
    :show-all-levels="false"
    clearable
    @change="change"
    ref="cascadeLLMRef"
  />
  <!-- {{ ops }} -->
</template>

<script setup>
import { ref, computed } from 'vue'
import { useVueFlow } from '@vue-flow/core'

const cascadeLLMRef = ref(null)
const change = () => {
  cascadeLLMRef.value.togglePopperVisible()
}
const { toObject } = useVueFlow()
const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {},
  nodeId: {}
})
// 获取当前nodeId的所有上级节点 ID;
const getPreNode = (nodeId) => {
  const allObj = toObject()
  const allLines = allObj.edges || []
  const allNodes = allObj.nodes || []
  // 获取当前节点的上级节点;
  //  获取所有线条中target是nodeId的；
  const targetIsNodeIdLines = allLines.filter((item) => item.target === nodeId)
  const preNodeIds = targetIsNodeIdLines.map((item) => {
    if (item.conditionFlag) {
      // 从if 或者else 来的
      const sourceId = item.source
      const nodeIfOrElse = allNodes.find((nodeItem) => nodeItem.id === sourceId)
      return nodeIfOrElse.parentNode
    } else {
      return item.source
    }
  })
  if (preNodeIds.length === 0) {
    return []
  } else {
    for (let i = 0; i < preNodeIds.length; i++) {
      const ids = getPreNode(preNodeIds[i])
      return [...ids, ...preNodeIds]
    }
  }
}
const mapTree = (treeItm, nodeType) => {
  const chilName = nodeType == 'code' ? 'schema' : 'children'
  const haveChildren = Array.isArray(treeItm[chilName]) && treeItm[chilName].length > 0
  let children = []
  if (haveChildren) {
    const filterEmptyName = treeItm[chilName].filter((item) => item.name)
    children = filterEmptyName.map((i) => mapTree(i, nodeType))
  }
  return {
    ...treeItm,
    label: treeItm.name,
    value: treeItm.name,
    children
  }
}

const ops = computed(() => {
  const allObj = toObject()
  const nodes = allObj.nodes || []
  const curNode = nodes.find((item) => item.id == props.nodeId)
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNode(props.nodeId)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.indexOf(item.id) > -1)
  // 将 if else 节点去掉
  let sourceNodeButIf = sourceNodes.filter(
    (item) => ['nodeif', 'nodeelse', 'precondition'].indexOf(item.nodeType) < 0
  )
  if (curNode.batchType == 'batch') {
    sourceNodeButIf.push(curNode)
  }
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      arr = inputs.filter((item) => item.name)
    } else if (['code', 'llm'].indexOf(item.nodeType) >= 0) {
      //  如果引用的是llm自己的批处理
      if (item.id == curNode.id) {
        const arrTemp = item.batchInfo?.arr || []
        const batchArr = arrTemp.filter((item) => item.name)
        arr = batchArr.map((item) => {
          return {
            ...item,
            label: item.name,
            value: item.name
          }
        })
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
      children:
        ['knowledge', 'code', 'llm'].indexOf(item.nodeType) > -1
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
})

const actions = {
  expandTrigger: 'click',
  checkStrictly: true
}
</script>

<style lang="scss" scoped></style>
