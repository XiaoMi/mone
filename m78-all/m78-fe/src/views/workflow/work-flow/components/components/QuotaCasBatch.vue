<template>
  <el-cascader
    v-model="value"
    :options="ops"
    :props="actions"
    :show-all-levels="false"
    clearable
    @change="change"
    ref="cascadeBatchRef"
  />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useVueFlow } from '@vue-flow/core'

const cascadeBatchRef = ref(null)
const change = () => {
  cascadeBatchRef.value.togglePopperVisible()
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
    const filterEmptyName = treeItm[chilName].filter((item) => {
      return item.label || item.name
    })
    const filterType = filterEmptyName.filter((item) => {
      const type = item.desc || item.valueType
      return ['Array<String>', 'Array<Object>'].indexOf(type) > -1
    })
    if (filterType.length != 0) {
      children = filterType.map((i) => mapTree(i, nodeType))
    }
  }
  return {
    ...treeItm,
    label: treeItm.label || treeItm.name,
    value: treeItm.label || treeItm.name,
    children
  }
}

const ops = computed(() => {
  const allObj = toObject()
  const nodes = allObj.nodes || []
  // 获取当前node的所有上一级
  const sourceNodesIds = getPreNode(props.nodeId)
  const sourceNodes = nodes.filter((item) => sourceNodesIds.indexOf(item.id) > -1)
  // 将 if else 节点去掉
  const sourceNodeButIf = sourceNodes.filter(
    (item) => ['nodeif', 'nodeelse', 'precondition', 'begin'].indexOf(item.nodeType) < 0
  )

  const ops = sourceNodeButIf.map((item) => {
    let arr = item.outputs || []
    if (['code', 'llm', 'knowledge'].indexOf(item.nodeType) >= 0) {
      const filterEmptyName = item.outputs.filter((emptyItem) => {
        if (item.nodeType == 'knowledge') {
          return emptyItem.label
        } else {
          return emptyItem.name
        }
      })
      const filterType = filterEmptyName.filter((filerName) => {
        const type = item.nodeType == 'knowledge' ? filerName.desc : filerName.valueType
        return ['Array<String>', 'Array<Object>'].indexOf(type) > -1
      })
      const opsArr = filterType.map((outputItem) => {
        const res = mapTree(outputItem, item.nodeType)
        return res
      })
      arr = opsArr
    }
    return {
      value: `${item.id}`,
      label: item.nodeMetaInfo.nodeName,
      disabled: true,
      children: arr
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
