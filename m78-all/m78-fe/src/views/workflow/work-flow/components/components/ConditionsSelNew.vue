<template>
  <el-select v-model="val" style="width: 140px">
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { getOperators } from '@/api/workflow'
import { getValueType } from '@/views/workflow/work-flow/components/common.js'
import { getOutputTree } from '../../../common/base.js'
import { useVueFlow } from '@vue-flow/core'
import { opList } from '../../baseInfo'

const { toObject } = useVueFlow()

const props = defineProps({
  value: {
    type: String
  },
  // 根据引用类型的valueType获取接口
  referenceInfo: {},
  parentNodeType: {}
})
// <!-- 如果时候precondition则是固定死的选项 -->
const options = ref(props.parentNodeType == 'precondition' ? opList : [])
const emits = defineEmits(['update:modelValue'])
const val = computed({
  get() {
    return props.value
  },
  set(val) {
    emits('update:modelValue', val)
  }
})

watch(
  () => props.referenceInfo,
  async (val) => {
    if (props.parentNodeType == 'precondition') return
    if (!val || val?.length < 2) return
    const { nodes } = toObject()
    const referNodeInfo = nodes.find((nodeItem) => nodeItem.id == val[0])
    const { valueType } = getValueType(val, getOutputTree(referNodeInfo))
    const { data } = await getOperators({ valueType })
    options.value = data.map((it) => {
      return {
        label: it.desc,
        value: it.name
      }
    })
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped></style>
