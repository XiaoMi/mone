<template>
  <el-form :model="node" label-position="top" size="small" ref="startFormRef" :disabled="disabled">
    <CommonCollapse title="输入" :content="collapseTip" @add="addInput" :showAdd="true">
      <OutPutsTree
        v-model="node.inputs"
        :showDesc="true"
        :nodeType="node.nodeType"
        batchType="single"
        :flowNode="node"
        :showRequired="true"
      />
    </CommonCollapse>
  </el-form>
</template>

<script setup>
import { ref, onMounted, computed, defineExpose, nextTick, inject } from 'vue'
import { ElMessage } from 'element-plus'
import CommonCollapse from './components/CommonCollapse.vue'
import OutPutsTree from './components/OutPutsTree'
import { v4 as uuidv4 } from 'uuid'

const props = defineProps({
  modelValue: {},
  disabled: {}
})
const collapseTip = ref(
  '工作流需要的输入参数，这些内容将在bot对话的过程</br>中被LLM阅读，使LLM可以在合适的时候启动工作流</br>并填入合适的信息'
)
const startFormRef = ref(null)
const emits = defineEmits(['update:modelValue'])
const node = computed({
  get() {
    const modelValue = props.modelValue
    modelValue.inputs?.forEach((it) => (it.activeNames = ['1']))
    return modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const validate = async () => {
  try {
    return await startFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })

const addInput = () => {
  node.value.inputs.push({ name: '', valueType: 'String', desc: '', required: false, id: uuidv4() })
}
</script>

<style lang="scss" scoped>
.llm {
  width: 340px;
}

.icon-jian {
  font-size: 15px;
  color: rgba(28, 29, 35, 0.6);
}
.col-item-name {
  color: #1d1c23;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  line-height: 22px;
  margin-left: 4px;
}
.left-col-t {
  display: flex;
  align-items: center;
}
</style>
