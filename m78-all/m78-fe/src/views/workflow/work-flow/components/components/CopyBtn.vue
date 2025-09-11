<template>
  <el-tooltip effect="light" :content="tips" placement="top">
    <el-button
      type="primary"
      :icon="isBatch ? CopyDocument : DocumentCopy"
      link
      @click.stop="copyFn"
    ></el-button>
  </el-tooltip>
</template>

<script setup>
import { CopyDocument, DocumentCopy } from '@element-plus/icons-vue'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
// 过滤掉日志、flowRecordId, 如果是批处理过滤掉耗时

const { toClipboard } = useClipboard()
const props = defineProps({
  arr: {
    type: Array,
    default: () => []
  },
  batchNum: {},
  isBatch: {
    default: false
  },
  tips: {
    default: '复制'
  },
  nodeData: {}
})

const getKeyName = (keyName) => {
  if (props.nodeData) {
    const { nodeType, batchType, inputs, id } = props.nodeData
    if (batchType == 'batch') {
      // 在inputs中找到name == keyName 的，如果他引用的是自己则修改keyName
      const curInput = inputs.find((it) => it.name == keyName)
      if (curInput?.referenceInfo[0] == id) {
        keyName = curInput.referenceInfo[1].split('.')[0]
      }
      return keyName
    }
  }
  return keyName
}

const copyFn = async () => {
  const info = props.arr
  const obj = {}
  // 复制整条输入、输出
  if (!props.isBatch) {
    info.forEach((ele) => {
      const keyName = getKeyName(ele.name)
      obj[keyName] = ele.value
    })
  } else {
    // 复制批处理的某一条
    info.forEach((ele) => {
      const val = typeof ele.value == 'string' ? ele.value : ele.value[props.batchNum - 1]
      // 如果是批处理的
      if (ele.isBatch && ele.name == 'outputList') {
        // 过滤掉$$TY_ELAPSED_TIME$$
        const objTemp = {}
        for (let key in val) {
          if (!['$$TY_ELAPSED_TIME$$'].includes(key)) {
            objTemp[key] = val[key]
          }
        }
        obj[ele.name] = objTemp
      } else {
        obj[ele.name] = val
      }
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
