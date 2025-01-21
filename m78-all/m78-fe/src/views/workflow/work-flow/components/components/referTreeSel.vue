<template>
  <el-tree-select v-model="value" :data="ops" clearable check-strictly />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useVueFlow } from '@vue-flow/core'
import { filterNoOutput } from '../../../common/base.js'

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
  const chilName = 'children'
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
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNode(props.nodeId)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.includes(item.id))
  // 将 if else 节点去掉
  const sourceNodeButIf = filterNoOutput(sourceNodes)
  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (item.nodeType == 'begin') {
      const inputs = item.inputs || []
      arr = inputs.filter((item) => item.name)
    } else if (['code', 'llm'].includes(item.nodeType)) {
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
      children: ['knowledge', 'code', 'llm'].includes(item.nodeType)
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
</script>

<style lang="scss" scoped></style>
