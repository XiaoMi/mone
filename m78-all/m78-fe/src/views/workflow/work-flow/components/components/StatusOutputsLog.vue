<template>
  <el-collapse v-model="activeNames">
    <el-collapse-item name="1">
      <template #title>
        日志
        <el-tooltip effect="light" content="复制" placement="top">
          <el-button type="primary" :icon="DocumentCopy" link @click.stop="copyFn"></el-button>
        </el-tooltip>
      </template>
      <ProbotCodemirror
        v-model="log"
        height="300px"
        @editorMounted="editorMounted"
        :readOnly="true"
      />
    </el-collapse-item>
  </el-collapse>
</template>

<script setup>
import { computed, ref, watch, nextTick } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
import ProbotCodemirror from '@/components/ProbotCodemirror'

const strArr = ref(['string', 'String'])
const activeNames = ref(['1'])
const props = defineProps({
  nodeData: {
    type: Object,
    default: () => {}
  },
  codeLog: {}
})

const log = computed(() => {
  return props.codeLog
})
const { toClipboard } = useClipboard()

const copyFn = async () => {
  try {
    await toClipboard(log.value)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.warning('您的浏览器不支持复制：', e)
  }
}
</script>

<style lang="scss" scoped>
.p-l-15px {
  padding-left: 15px;
}
</style>
