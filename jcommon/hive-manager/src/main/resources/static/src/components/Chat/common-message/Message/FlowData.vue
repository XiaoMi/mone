<template>
  <el-popover placement="right" :width="700" trigger="click">
    <template #reference>
      <el-button size="small" :type="statusObj?.tagType" plain>
        <el-icon class="is-loading loading-icon" v-if="flowData.endFlowStatus == 0">
          <Loading />
        </el-icon>
        <i>工作流</i>
        <el-icon>
          <ArrowRight />
        </el-icon>
      </el-button>
    </template>
    <div class="flow-data">
      <p class="flow-t"><i class="iconfont icon-flow"></i>工作流详情</p>
      <el-collapse v-model="activeNames" size="small">
        <el-collapse-item
          v-for="item in allNodes"
          :key="item.nodeId"
          :name="item.nodeId"
          :title="item.nodeName"
          class="flow-data-item"
          :class="`status-${item.outputs?.status}`"
        >
          <template #title>
            {{ item.nodeName }}
            <TimerTag
              class="timer"
              :type="flowStatusArr.find((it) => it.code === item.outputs?.status)?.tagType"
              v-if="[2, 3].includes(item.outputs?.status)"
              >{{ formatTime(item.outputs?.durationTime) }}</TimerTag
            >
          </template>
          <template v-if="item.inputs?.length > 0">
            <p class="title title-input">INPUT</p>
            <JsonViewer
              :value="item.inputs || []"
              copyable
              boxed
              sort
              theme="dark"
              :expand-depth="1"
              :key="item.key"
            />
          </template>
          <template v-if="item.outputs?.length > 0">
            <p class="title ouput-t">OUTPUT</p>
            <JsonViewer
              :value="item.outputs || []"
              copyable
              boxed
              sort
              theme="dark"
              :expand-depth="1"
              :key="item.key"
            />
          </template>
        </el-collapse-item>
      </el-collapse>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Loading, ArrowRight } from '@element-plus/icons-vue'
import { flowHeaderStatus, formatTime, flowStatusArr } from '@/views/workflow/work-flow/baseInfo.js'
import TimerTag from '@/views/workflow/components/TimerTag.vue'

const activeNames = ref([])
const props = defineProps({
  flowData: {}
})
const allNodes = computed(() => {
  const { nodeInputsMap, nodeOutputsMap } = props.flowData
  const inKeys = Object.keys(nodeInputsMap)
  const outKeys = Object.keys(nodeOutputsMap)
  const allKeys = [...new Set([...inKeys, ...outKeys])]
  const allArr = []
  allKeys.forEach((key) => {
    allArr.push({
      nodeId: key,
      nodeName: nodeOutputsMap[key].nodeName,
      inputs: nodeInputsMap[key]?.inputDetails,
      outputs: nodeOutputsMap[key],
      key: new Date()
    })
  })
  return allArr
})
const statusObj = computed(() => {
  const obj = flowHeaderStatus.find((it) => it.code == props.flowData.endFlowStatus)
  return obj
})
</script>

<style lang="scss" scoped>
.flow-data {
  max-height: 400px;
  overflow: auto;
  :deep(.jv-container) {
    border: none;
    border-radius: 0 0 5px 5px;
  }
}
.title {
  padding: 5px 10px;
  background: #282c34;
  color: #fff;
  border-radius: 5px 5px 0 0;
  border-bottom: 1px solid #fff;
}
.ouput-t {
  margin-top: 10px;
}
.btn-text {
  margin-left: 3px;
}
.flow-t {
  font-size: 16px;
  padding-bottom: 10px;
  font-weight: 800;
  color: #000;
}
.loading-icon {
  margin-right: 3px;
}
.icon-flow {
  font-size: 18px;
  margin-right: 2px;
}
.timer {
  margin-left: 6px;
}
// 开始
.status-1 :deep(.oz-collapse-item__header) {
  background: #ecf5ff;
  .icon {
    color: #45a0ff;
  }
}
.status-2 :deep(.oz-collapse-item__header) {
  // 成功
  background: #edf9ee;
  .icon {
    color: #3ec355;
  }
}
// 失败
.status-3 :deep(.oz-collapse-item__header) {
  background: #fde2e2;
  .icon {
    color: #f56c6c;
  }
}
.title-input {
  margin-top: 20px;
}
.flow-data-item :deep(.oz-collapse-item__header) {
  padding-left: 10px;
}
</style>
