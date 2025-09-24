<template>
  <el-dropdown @command="commandFn" v-if="!beginEnd.includes(nodeData.nodeType)">
    <span class="el-dropdown-link">
      <el-icon><More /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="del" :disabled="disabled">删除</el-dropdown-item>
        <el-dropdown-item command="rename" :disabled="disabled">重命名</el-dropdown-item>
        <el-dropdown-item
          command="copy"
          :disabled="disabled"
          v-if="showCopyTypes.includes(nodeData.nodeType)"
          >复制节点</el-dropdown-item
        >
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <el-dialog title="重命名" v-model="showRename" append-to-body width="400">
    <el-input v-model="name" :maxlength="20"></el-input>
    <p class="err" v-if="showTips">不能为空</p>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showRename = false" size="small">取消</el-button>
        <el-button type="primary" @click="confirm" size="small"> 确认 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { useVueFlow } from '@vue-flow/core'
import { ref, computed } from 'vue'
import { useWfStore } from '@/stores/workflow1'
import { classifyNodes, initOpsByNodeList } from '../baseInfo.js'
import { createNodeId, createEdgeId, initBaseNode } from '../../common/init-node.js'

const showCopyTypes = ref(['llm', 'code', 'database'])
const wfStore = useWfStore()
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)
const { setNodesPreNodes } = wfStore

const {
  removeNodes,
  updateNode,
  toObject,
  addNodes,
  getNodes,
  getEdges,
  addEdges,
  removeSelectedElements
} = useVueFlow()
const props = defineProps({
  nodeData: {},
  nodes: {},
  disabled: {}
})
const name = ref('')
const beginEnd = ref(['begin', 'end'])
const showRename = ref(false)
const showTips = ref(false)

const renameFn = () => {
  showRename.value = true
  name.value = props.nodeData.nodeMetaInfo.nodeName
}

const copyFn = () => {
  const moveLen = 50
  // 1.复制节点
  const nodeId = createNodeId(getNodes.value)
  const { position, nodeType, coreSetting, batchInfo, inputs, outputs, batchType, nodeMetaInfo } =
    props.nodeData
  const node = initBaseNode(
    {
      x: position.x + moveLen,
      y: position.y + moveLen
    },
    nodeType,
    { draggedInfo: null, nodeId }
  )
  if (nodeType.startsWith('llm')) {
    node.batchInfo = batchInfo
    node.batchType = batchType
  }
  console.log('>>>')
  // 清空被选中元素unselect selected elements
  removeSelectedElements()
  const newNode = JSON.parse(
    JSON.stringify({ ...node, coreSetting, inputs, outputs, nodeMetaInfo, selected: true })
  )
  addNodes(newNode)
  // 2.复制线
  const allEdges = getEdges.value
  // 相关edges
  const affectLine = allEdges.filter((it) => it.target == props.nodeData.id)
  const newEdgeId = createEdgeId(allEdges)
  const newEdges = affectLine.map((it, index) => {
    const { source, sourceHandle, conditionFlag, type, ...rst } = it
    return {
      id: `edge_${newEdgeId + index}`,
      target: `${nodeId}`,
      source: source,
      sourceHandle,
      type,
      conditionFlag
    }
  })
  addEdges(newEdges)
}
const commandFn = (command) => {
  switch (command) {
    case 'del':
      removeNodes(props.nodeData, true, true)
      break
    case 'rename':
      renameFn()
      break
    case 'copy':
      copyFn()
      break
    default:
      break
  }
}
const confirm = () => {
  showTips.value = !name.value
  if (!name.value) return
  updateNode(props.nodeData.id, {
    nodeMetaInfo: {
      nodeName: name.value
    }
  })
  showRename.value = false
  resetOps()
}

// 名字如果改变了，需要重置引用下拉选项选项
const resetOps = () => {
  const { affectedNodes, unaffectedNodes } = classifyNodes(
    true,
    nodesPreNodes.value,
    props.nodeData.id
  )
  const newArr = [...initRelationNodesPreIds(affectedNodes), ...unaffectedNodes]
  setNodesPreNodes(newArr)
}

const initRelationNodesPreIds = (affNodes) => {
  const obj = toObject()
  return initOpsByNodeList(affNodes, obj)
}
</script>

<style lang="scss" scoped>
.err {
  font-size: 12px;
  color: #f56c6c;
}
</style>
