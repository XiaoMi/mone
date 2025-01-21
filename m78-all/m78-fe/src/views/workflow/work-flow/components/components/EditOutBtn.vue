<template>
  <el-button :icon="Edit" link plain type="primary" @click="handleEdit" v-if="flowStatusNum == 5">
  </el-button>
</template>

<script setup>
import { computed } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import { useWfStore } from '@/stores/workflow1'

const wfStore = useWfStore()
const { openEditOutput } = wfStore
const flowStatusNum = computed(() => wfStore.flowStatusNum)
const props = defineProps({
  batch: {
    default: false
  },
  item: {
    type: Object,
    default: () => {}
  },
  // 如果是批量， 将会修改第几个
  index: {},
  nodeData: {},
  type: {}
})
const handleEdit = () => {
  if (props.batch) {
    console.log('批处理, 修改几个', props.index, props.type, props.item)
    openEditOutput({
      editInfo: props.item,
      nodeData: props.nodeData,
      type: props.type,
      index: props.index
    })
  } else {
    openEditOutput({ editInfo: props.item, nodeData: props.nodeData, type: props.type })
  }
}
</script>

<style lang="scss" scoped></style>
