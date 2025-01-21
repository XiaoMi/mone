<template>
  <div class="desc-update">
    <slot />
    <UpPluginConfig v-if="showUpdateType.includes(nodeData.nodeType)" :id="nodeData.nodeType == 'plugin'
      ? nodeData?.coreSetting?.pluginId
      : nodeData.coreSetting?.flowId
      " :nodeType="nodeData.nodeType" @update="updateFn" />
  </div>
</template>

<script setup>
import UpPluginConfig from './components/UpPluginConfig.vue'
import {defineProps, ref, defineEmits, computed, inject} from 'vue'
import {useWfStore} from '@/stores/workflow1'
import {classifyNodes, initOpsByNodeList} from '../../work-flow/baseInfo.js'
import {useVueFlow} from '@vue-flow/core'

const {toObject} = useVueFlow()
const wfStore = useWfStore()
const {setNodesPreNodes} = wfStore
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)

const showUpdateType = ref(['plugin', 'subFlow'])
const props = defineProps({
  modelValue: {}
})
const nodeData = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const emits = defineEmits(['update'])
const updateFn = (detail) => {
  if (nodeData.value.nodeType == 'subFlow') {
    const {inputs, outputs} = detail.flowBaseInfo
    // 遍历新的inputs，如果已经存在则用更新旧的就行
    const newInputs = []
    inputs.forEach((ele) => {
      const hasIn = nodeData.value.inputs.find((it) => it.name == ele.name)
      hasIn ? newInputs.push({...hasIn, ...ele}) : newInputs.push(ele)
    })
    nodeData.value.inputs = newInputs
    nodeData.value.outputs = outputs
  } else {
    const {input, output} = detail.botPluginMeta
    if (detail.type !== 'dubbo') {
      const newInputs = []
      input.forEach((ele) => {
        const hasIn = nodeData.value.inputs.find((it) => it.name == ele.name)
        hasIn ? newInputs.push(hasIn) : newInputs.push(ele)
      })
      nodeData.value.inputs = newInputs
    }
    nodeData.value.outputs = output
  }
  // 更新配置之后，重新计算影响到的节点,如果引用了则置空
  emptyFn()
}

const emtpyRefData = inject('emtpyRefData')
const emptyFn = () => {
  const {affectedNodes, unaffectedNodes} = classifyNodes(
    false,
    nodesPreNodes.value,
    nodeData.value.id
  )
  const newArr = [...initRelationNodesPreIds(affectedNodes), ...unaffectedNodes]
  setNodesPreNodes(newArr)
  // 置空绑定值
  emtpyRefData(affectedNodes, nodeData.value.id)
}

const initRelationNodesPreIds = (relations) => {
  const obj = toObject()
  return initOpsByNodeList(relations, obj)
}
</script>

<style lang="scss" scoped>
.desc-update {
  display: flex;
  justify-content: space-between;
}
</style>
