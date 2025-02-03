<template>
  <el-form
    :model="node"
    size="small"
    label-position="top"
    inline
    class="intent-form"
    ref="codeFormRef"
  >
    <CommonInputsValid
      v-model="node.inputs"
      :referOps="referOps"
      :showAdd="false"
      :showDelete="false"
      :nameDisabled="true"
      titleDes="输入需要做意图识别判断的参数"
      :showValueType="true"
    />

    <CommonOutputs titleDes="输出表述">
      <OutPutsTree v-model="node.outputs" :showDesc="true" :disabled="true" />
    </CommonOutputs>
  </el-form>
</template>

<script setup>
import { ref, computed, defineExpose } from 'vue'
import OutPutsTree from './components/OutPutsTree.vue'
import CommonInputsValid from './components/CommonInputsValid.vue'
import CommonOutputs from './components/CommonOutputs.vue'
import CommonCollapse from './components/CommonCollapse.vue'
import { initIntentHandle, getHandleId } from '../../common/edges-transform.js'
import { Position, useVueFlow } from '@vue-flow/core'

const { removeEdges, toObject } = useVueFlow()
const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  getDetailed: {},
  referOps: {}
})
const emits = defineEmits(['update:modelValue'])
const node = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const addMatch = () => {
  // 获取 node.coreSetting.intentMatch中每个对象的key
  const arr = node.value.coreSetting.intentMatch.map((item) => item.key)
  const maxKey = Math.max(...arr)
  const item = {
    key: maxKey + 1,
    value: ''
  }
  // 在最后一项前面增加一项
  node.value.coreSetting.intentMatch.splice(node.value.coreSetting.intentMatch.length - 1, 0, item)
  // 在最后一项前面增加一项
  node.value.handles.splice(node.value.handles.length - 1, 0, {
    id: getHandleId(maxKey + 1),
    type: 'source',
    position: Position.Right
  })
}

const delMatch = (index) => {
  const curVal = node.value.coreSetting.intentMatch[index]
  const { edges } = toObject()
  // 受影响的边
  const edgesAffect = edges.filter(
    (it) => it.sourceHandle == getHandleId(curVal.key) && it.source == node.value.id
  )
  const edgesAffectIds = edgesAffect.map((it) => it.id)
  // 删除影响的边
  removeEdges(edgesAffectIds)
  // 删除意图匹配
  node.value.coreSetting.intentMatch.splice(index, 1)
  // 删除handles
  node.value.handles.splice(index, 1)
}

const showArr = computed(() => {
  return node.value.coreSetting.intentMatch.filter((item) => item.key > 0)
})

const codeFormRef = ref(null)
const validate = async () => {
  try {
    return await codeFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>
<style lang="scss" scoped>
.model-box {
  width: 100%;
  background: #f8f8f8;
  padding: 10px;
  :deep(.oz-form-item__label) {
    font-size: 13px;
    color: var(--oz-collapse-header-text-color);
  }
}
.other-intent {
  padding: 0 0 10px 0;
  .other-item {
    padding-left: 10px;
    background: #eee;
    border-radius: 4px;
  }
}
.match-input {
  width: calc(100% - 20px);
}
.icon-del-match {
  font-size: 12px;
}
</style>
