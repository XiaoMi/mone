import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getOperators } from '@/api/workflow'

export const useWfStore = defineStore('workflow', () => {
  const nodeDragging = ref(false)
  const showEditOutput = ref(false)
  const editOutInfo = ref(null)
  const flowStatusNum = ref(1)
  const nodesPreNodes = ref([])
  // 条件选择器操作符
  const conditionOps = ref([])

  const setNodesPreNodes = (val: any) => {
    nodesPreNodes.value = val
  }
  const setFlowStatus = (val: any) => {
    flowStatusNum.value = val
  }

  const setDragging = (val: any) => {
    nodeDragging.value = val
  }
  const setEditOutputShow = (val: any) => {
    showEditOutput.value = val
  }
  const setEditOutInfo = (val: any) => {
    editOutInfo.value = val
  }
  const openEditOutput = (info: any) => {
    setEditOutputShow(true)
    setEditOutInfo(info)
  }
  const getCommonOps = async () => {
    const { data } = await getOperators({ valueType: '' })
    conditionOps.value = data || []
  }
  return {
    nodeDragging,
    setDragging,
    showEditOutput,
    setEditOutputShow,
    openEditOutput,
    editOutInfo,
    setFlowStatus,
    flowStatusNum,
    setNodesPreNodes,
    getCommonOps,
    conditionOps,
    nodesPreNodes
  }
})
