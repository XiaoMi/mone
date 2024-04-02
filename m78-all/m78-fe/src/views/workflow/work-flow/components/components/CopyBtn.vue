<template>
  <el-tooltip effect="light" content="复制" placement="top">
    <el-button type="primary" :icon="DocumentCopy" link @click.stop="copyInputs"></el-button>
  </el-tooltip>
</template>

<script setup>
import { DocumentCopy } from '@element-plus/icons-vue'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'

const { toClipboard } = useClipboard()
const props = defineProps({
  arr: {
    type: Array,
    default: () => []
  },
  batchNum: {},
  isBatch: {
    default: false
  }
})

const copyInputs = async () => {
  const info = props.arr
  const obj = {}

  if (!props.isBatch) {
    info.forEach((ele) => {
      obj[ele.name] = ele.value
    })
  } else {
    info.forEach((ele) => {
      const val = typeof ele.value == 'string' ? ele.value : ele.value[props.batchNum - 1]
      obj[ele.name] = val
    })
  }

  const text = JSON.stringify(obj)
  try {
    await toClipboard(text)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.warning('您的浏览器不支持复制：', e)
  }
}
</script>

<style lang="scss" scoped></style>
