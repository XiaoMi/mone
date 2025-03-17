<template>
  <el-dialog v-model="dialogVisible" title="修改输出内容" width="700">
    <p class="name-i">{{ nameText }}</p>
    <Vue3JsonEditor
      v-model="inputV"
      :show-btns="false"
      :expandedOnStart="false"
      @json-change="onJsonChange"
      mode="text"
      class="json-edit"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFn"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, watch, ref, inject } from 'vue'
import { useWfStore } from '@/stores/workflow1'
import { continueFlow } from '@/api/workflow.ts'
import { useVueFlow } from '@vue-flow/core'
import { Vue3JsonEditor } from 'vue3-json-editor'
import { useRoute } from 'vue-router'

const { updateNode } = useVueFlow()
const route = useRoute()
const wfStore = useWfStore()
const { setEditOutputShow } = wfStore

const dialogVisible = computed({
  get() {
    return wfStore.showEditOutput
  },
  set(val) {
    setEditOutputShow(val)
  }
})
const inputV = ref('')
const nameText = ref('')
const flowRecordId = computed(() => route.query.flowRecordId)
watch(
  () => dialogVisible.value,
  (val) => {
    if (val) {
      const { value, name, isBatch } = wfStore.editOutInfo.editInfo
      if (!isBatch) {
        inputV.value = value
      } else {
        const { index } = wfStore.editOutInfo
        inputV.value = value[index]
      }
      nameText.value = name
    }
  }
)
const updateOut = () => {
  const { type, nodeData, index } = wfStore.editOutInfo
  const arr = type == 'input' ? nodeData.resInputs.inputDetails : nodeData.resOutputs.outputDetails
  const indexNum = arr.findIndex((item) => item.name == nameText.value)
  if (typeof index == 'undefined') {
    arr[indexNum].value = inputV.value
  } else {
    // 修改的是批处理的
    const batchArr = JSON.parse(arr[indexNum].value)
    batchArr[index] = inputV.value
    arr[indexNum].value = JSON.stringify(batchArr)
  }
}
const onJsonChange = (val) => {
  inputV.value = val
}
const operateFlowFn = inject('operateFlowFn')
const confirmFn = () => {
  const { nodeData, editInfo, index } = wfStore.editOutInfo
  const { isBatch } = editInfo
  const { id } = nodeData
  let newV
  if (isBatch) {
    newV = JSON.parse(JSON.stringify(editInfo.value))
    newV[index] = inputV.value
  } else {
    newV = inputV.value
  }
  const meta = {
    targetNodeId: id,
    name: editInfo.name,
    value: typeof newV == 'string' ? newV : JSON.stringify(newV)
  }
  operateFlowFn({
    cmd: 'modifyParam',
    nodeId: id,
    flowRecordId: flowRecordId.value,
    meta
  })
  updateOut()
  dialogVisible.value = false
}
</script>

<style lang="scss" scoped>
.input-inner {
  flex: 1;
}
.name-i {
  font-weight: bold;
  font-size: 15px;
  height: 35px;
  line-height: 35px;
  padding: 0 10px;
  color: #3ca9ff;
  border: solid 1px #3ca9ff;
  border-bottom: none;
}
.json-edit {
  :deep(.jsoneditor-menu) {
    display: none;
  }
  :deep(.jsoneditor-text) {
    padding: 10px;
  }
  :deep(.jsoneditor) {
    border-color: #3ca9ff;
  }
}
</style>
