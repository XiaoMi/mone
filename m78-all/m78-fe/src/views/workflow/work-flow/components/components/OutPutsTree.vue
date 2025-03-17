<template>
  <div class="top-t">
    <div class="tree-t">
      <p class="var-label flex-1">变量名</p>
      <p class="var-label" style="width: 120px">变量类型</p>
      <p class="var-label" v-if="showDesc" style="width: 120px">描述</p>
      <p class="var-label" v-if="showRequired" style="width: 50px">必填</p>
    </div>
  </div>
  <el-tree :data="outputs" default-expand-all :expand-on-click-node="false" class="var-tree">
    <template #default="{ node, data }">
      <div class="custom-tree-node">
        <div class="input-box">
          <el-form-item
            :prop="'inputValue_' + node.id"
            :rules="{
              validator: (rule, value, callback) => validateTreeInput(node, data, value, callback),
              trigger: 'blur'
            }"
            label-position="top"
            class="input-item flex-1"
          >
            <RefInput
              v-model="data.name"
              placeholder="变量名"
              maxlength="30"
              size="small"
              :disabled="
                disabled ||
                (nodeType == 'llm' && batchType == 'batch' && node.level == 1) ||
                data.valueType === 'Pdf'
              "
              :node="flowNode"
              :check="check"
            />
          </el-form-item>
          <VariateTypeSelAll
            v-model="data.valueType"
            @change="
              (val) => {
                changeType(val, node, data)
              }
            "
            :disabled="nodeType == 'llm' && batchType == 'batch' && node.level == 1"
            v-if="nodeType == 'llm'"
            style="width: 120px"
          />
          <VariateTypeSel
            v-else
            v-model="data.valueType"
            @change="
              (val) => {
                changeType(val, node, data)
              }
            "
            :nodeType="nodeType"
            style="width: 120px"
            :disabled="disabled || (nodeType == 'llm' && batchType == 'batch' && node.level == 1)"
          />
          <el-input
            v-if="showDesc"
            v-model="data.desc"
            placeholder="请输入参数描述"
            size="small"
            class="out-desc"
            style="width: 120px"
            :disabled="disabled || (nodeType == 'llm' && batchType == 'batch' && node.level == 1)"
          />
          <el-switch v-model="data.required" size="small" style="width: 50px" v-if="showRequired" />
        </div>
        <div class="btns">
          <el-button
            link
            @click="append(data)"
            v-if="hasChildType.includes(data.valueType)"
            :icon="Plus"
            :disabled="disabled"
          >
          </el-button>
          <el-button
            link
            @click="remove(node, data)"
            :disabled="disabled || (!isCanEmpty && outputs.length == 1 && node.level == 1)"
            :icon="Minus"
          >
          </el-button>
        </div>
      </div>
    </template>
  </el-tree>
</template>

<script setup>
import { validateTreeInput } from '../../baseInfo'
import VariateTypeSel from './VariateTypeSel'
import VariateTypeSelAll from './VariateTypeSelAll'
import { computed, ref, inject } from 'vue'
import { Plus, Minus } from '@element-plus/icons-vue'
import { v4 as uuidv4 } from 'uuid'
import RefInput from '@/views/workflow/work-flow/components/components/RefInput.vue'
import { initOpsByNodeList, classifyNodes } from '@/views/workflow/work-flow/baseInfo.js'
import { useWfStore } from '@/stores/workflow1'
import { useVueFlow } from '@vue-flow/core'
import { areArraysEqual, extractNames } from '@/views/workflow/common/base.js'

const wfStore = useWfStore()
const { setNodesPreNodes } = wfStore
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)
const { toObject, updateNode } = useVueFlow()

const props = defineProps({
  modelValue: {},
  showRequired: {
    type: Boolean,
    default: false
  },
  showDesc: {},
  isCanEmpty: {
    default: false
  },
  // 默认是全部都可以改的，但是插件中不可以改
  disabled: {
    default: false
  },
  nodeType: {},
  batchType: {},
  flowNode: {},
  check: {
    type: Boolean,
    default: true
  }
})
const emit = defineEmits(['update:modelValue'])
const outputs = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const append = (data) => {
  // eslint-disable-next-line no-const-assign
  const newChild = { name: '', valueType: 'String', children: [], id: uuidv4() }
  if (!data.children) {
    data.children = []
  }
  data.children.push(newChild)
}
const hasChildType = ref([
  'Array<Object>',
  'Array<String>',
  'Array<Boolean>',
  'Array<Integer>',
  'Object'
])
const changeType = (val, node, data) => {
  console.log('val', val, node, data)
  if (!hasChildType.value.includes(val)) {
    node.data.children = []
  }
  refOpsChange(val, node, data)
  if (val === 'Pdf') {
    node.data.name = 'pdf'
  }
}

const emtpyRefData = inject('emtpyRefData')
const refOpsChange = (val, node, data) => {
  console.log('data>>>>', val, node, data)
  const { affectedNodes, unaffectedNodes } = classifyNodes(
    false,
    nodesPreNodes.value,
    props.flowNode?.id
  )
  const newArr = [...initRelationNodesPreIds(affectedNodes), ...unaffectedNodes]
  setNodesPreNodes(newArr)
  // 如果这种类型被newPrecondition引用了，则当前这一条的operator需要置空
  emptyIfItemOperator(affectedNodes, props.flowNode?.id, node)
  // 置空绑定值
  emtpyRefData && emtpyRefData(affectedNodes, props.flowNode?.id)
}

// 如果这种类型被newPrecondition引用了，则当前这一条的operator需要置空
const emptyIfItemOperator = (affectedNode, curFlowId, node) => {
  const ifNodes = getAllAffectIf(affectedNode)
  const arr = extractNames(node)
  // 从以上if节点中过滤出真的引用了这个值的那一条的operator置空
  ifNodes.forEach((nodeIf) => {
    const { coreSetting } = nodeIf
    // 过滤出引用了当前node的coreSetting
    coreSetting?.forEach((it, i) => {
      const { referenceInfo } = it
      if (referenceInfo.length == 0) return false
      const [nodeId, ...rst] = referenceInfo
      if (nodeId == curFlowId && areArraysEqual(rst, arr)) {
        nodeIf.coreSetting[i].operator = ''
        updateNode(nodeIf.id, nodeIf)
      }
    })
  })
}

const getAllAffectIf = (affectedNode) => {
  // 受到影响的”条件“节点
  const affectConditionNodes = affectedNode.filter((it) => it.nodeType == 'newPrecondition')
  const affectConditionsNodeIds = affectConditionNodes.map((it) => it.nodeId)
  const { nodes } = toObject()
  // 所有可能影响if节点
  return nodes.filter(
    (it) => it.nodeType == 'nodeif' && affectConditionsNodeIds.includes(it.parentNode)
  )
}

const initRelationNodesPreIds = (relations) => {
  const obj = toObject()
  return initOpsByNodeList(relations, obj)
}

const delInPutOutput = inject('delInPutOutput')
const remove = (node, data) => {
  const parent = node.parent
  const children = parent.data.children || parent.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  emit('update:modelValue', outputs.value)
  // 调用NodeItemNew组件中的方法
  delInPutOutput && delInPutOutput(props.flowNode)
}
</script>

<style lang="scss" scoped>
.top-t {
  width: 100%;
  padding: 0 58px 0 25px;
}
.tree-t {
  flex: 1;
  display: flex;
  padding: 5px 0px;
  .var-label {
    font-size: 12px;
    font-weight: 600;
  }
  .var-name {
    flex-basis: 119px;
  }
}
//  tree 样式
.custom-tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 14px;
  padding-right: 8px;
  .input-box {
    flex: 1;
    display: flex;
  }
  :deep(.oz-form-item--small) {
    margin-bottom: 14px;
  }
}
.var-tree {
  background-color: transparent;
  margin-bottom: 18px;
  &:last-child {
    margin-bottom: 0px;
  }
  :deep(.oz-tree-node__content) {
    padding-top: 5px;
    padding-bottom: 5px;
    height: auto;
    align-items: flex-start;
  }
  :deep(.oz-form-item__error) {
    position: static;
  }
}
.btns {
  height: 24px;
  width: 58px;
  flex-basis: 48px;
  padding-left: 4px;
  display: flex;
  justify-content: space-between;
  .oz-button + .oz-button {
    margin-left: 5px;
  }
  .icon-btn {
    font-size: 14px;
  }
}
.out-desc {
  height: 25px;
}
.custom-tree-node .input-box .input-item {
  margin-right: 0;
  margin-bottom: 0px;
}
.input-item {
  :deep(.oz-form-item__content) {
    margin-left: 0 !important;
  }
}
.flex-1 {
  flex: 1;
}
.flex-06 {
  flex: 0.6;
}
.flex-03 {
  flex: 0.9;
}
.flex-02 {
  flex: 0.2;
  padding-left: 2px;
}
</style>
