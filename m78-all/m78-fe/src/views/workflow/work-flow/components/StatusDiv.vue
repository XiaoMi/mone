<template>
  <div class="status-box" :class="`status-${resOutputs?.status}`">
    <div class="align-center">
      <el-icon class="icon">
        <component :is="statusIcon['icon' + resOutputs?.status || 2]" />
      </el-icon>
      <span class="label">{{ statusMap.label }} </span>
      <TimerTag
        class="timer"
        :type="statusMap.tagType"
        v-if="[2, 3].includes(resOutputs?.status)"
        >{{ showTime }}</TimerTag
      >
      <FlowRecordIdJump
        :jump="true"
        :flowRecordIdStatus="flowRecordIdVal"
        v-if="flowRecordIdVal"
        :flowId="nodeData.coreSetting.flowId"
        :hasPermission="hasPermission"
      />
    </div>
    <ResultPop
      :title="`${nodeData.nodeMetaInfo.nodeName} 执行结果`"
      :width="nodesBase[nodeData.nodeType].popWidth"
      :visible="visible"
    >
      <BatchResult
        v-if="nodeData.nodeType.startsWith('llm') && nodeData.batchType == 'batch'"
        :batchRes="batchRes"
        :resInputs="resInputs"
        :nodeData="nodeData"
        :resOutputs="resOutputs"
      />
      <template v-else>
        <ResultNewCondition
          v-if="conditionTypes.includes(nodeData.nodeType)"
          :nodes="nodes"
          :resInputs="resInputs"
          :nodeData="nodeData"
          :resOutputs="resOutputs"
        />
        <StatusInputs v-else :resInputs="resInputs" :nodeData="nodeData" />
        <StatusOutputs :resOutputs="resOutputs" :nodeData="nodeData" />
        <StatusError :resOutputs="resOutputs" />
      </template>
      <!-- 如果时候code类型节点要展示日志 -->
      <template v-if="nodeData.nodeType == 'code'">
        <StatusOutputsLog :codeLog="codeLog" v-if="codeLog" />
      </template>

      <template #reference v-if="nodeData.nodeType !== 'manualConfirm'">
        <TestNodeRetry @click="emits('retryFn')" v-if="resOutputs?.status == 3" />
        <el-button link @click="switchShow">{{ visible ? '收起' : '展开' }}运行结果 </el-button>
      </template>
    </ResultPop>
  </div>
</template>

<script setup>
import { computed, ref, defineEmits } from 'vue'
import { opList, formatTime, flowStatusArr, nodesBase } from '../baseInfo'
import ResultPop from './components/ResultPop'
import BatchResult from './components/BatchResult'
import StatusInputs from './components/StatusInputs'
import StatusOutputs from './components/StatusOutputs'
import StatusOutputsLog from './components/StatusOutputsLog'
import StatusError from './components/StatusError'
import TimerTag from '../../components/TimerTag'
import { SuccessFilled, CircleCloseFilled, WarningFilled } from '@element-plus/icons-vue'
import FlowRecordIdJump from '../../components/components/FlowRecordIdJump.vue'
import ResultNewCondition from './components/ResultNewCondition.vue'
import { conditionTypes } from '@/views/workflow/common/base.js'
import TestNodeRetry from './TestNodeRetry.vue'

const props = defineProps({
  resInputs: {},
  resOutputs: {},
  nodes: {},
  nodeData: {},
  hasPermission: {}
})
const emits = defineEmits(['retryFn'])
const statusIcon = {
  icon2: SuccessFilled,
  icon1: WarningFilled,
  icon3: CircleCloseFilled,
  icon4: CircleCloseFilled,
  icon5: WarningFilled
}
const visible = ref(props?.nodeData?.nodeType == 'end')
const statusMap = computed(() => {
  const arr = flowStatusArr.filter((item) => item.code === props.resOutputs.status)
  return arr[0] || {}
})

const batchRes = ref([
  {
    key: 1,
    disabled: false
  },
  {
    key: 2,
    disabled: false
  },
  {
    key: 3,
    disabled: false
  },
  {
    key: 4,
    disabled: false
  },
  {
    key: 5,
    disabled: true
  }
])

const showTime = computed(() => {
  return formatTime(props.resOutputs?.durationTime)
})
const flowRecordIdVal = computed(() => {
  if (props.nodeData.nodeType != 'subFlow') return ''
  return props.resOutputs?.outputDetails?.find((it) => it.name == '$$TY_SUB_FLOW_RECORD_ID$$')
    ?.value
})

// code类型会展示log
const codeLog = computed(() => {
  if (props.nodeData.nodeType != 'code') return ''
  return props.resOutputs?.outputDetails?.find((it) => it.name == '$$TY_CODE_LOG$$')?.value
})
const switchShow = () => {
  visible.value = !visible.value
}
</script>

<style lang="scss" scoped>
.status-box {
  padding: 12px;
  display: flex;
  align-items: center;
  border-radius: 6px 6px 0 0;
  display: flex;
  justify-content: space-between;
  :deep(.oz-popover) {
    margin-right: -18px;
    margin-bottom: 0px;
  }
  :deep(.t) {
    margin-bottom: 5px;
  }
  :deep(.item-t) {
    margin: 8px 0;
  }
  .name-i,
  :deep(.name-i) {
    color: #535ce0;
    word-break: keep-all;
  }
  .text-p,
  :deep(.text-p) {
    font-size: 14px;
    font-weight: 400;
    display: flex;
  }
  .outputs,
  :deep(.outputs) {
    background: rgba(46, 46, 56, 0.04);
    padding: 8px;
    border-radius: 8px;
    border: 1px solid rgba(29, 28, 35, 0.08);
  }
  .outputs + .outputs,
  :deep(.outputs + .outputs) {
    margin-top: 10px;
  }
  .err,
  :deep(.err) {
    color: #f56c6c;
  }
  .out-text,
  :deep(.out-text) {
    word-break: break-word;
    word-wrap: break-word;
  }
  :deep(.oz-collapse-item .oz-collapse-item__header),
  :deep(.oz-collapse-item .oz-collapse-item__wrap) {
    background-color: #fff;
    padding: 10px;
  }
  :deep(.oz-collapse-item .oz-collapse-item__arrow) {
    display: block;
  }
  :deep(.jv-code) {
    padding: 0;
  }
}

.icon {
  font-size: 18px;
}
.timer {
  margin-left: 6px;
}
// 开始
.status-1,
.status-5 {
  background: #ecf5ff;
  .icon {
    color: #45a0ff;
  }
}
.status-2 {
  // 成功
  background: #edf9ee;
  .icon {
    color: #3ec355;
  }
}
// 失败
.status-3,
.status-4 {
  background: #fde2e2;
  .icon {
    color: #f56c6c;
  }
}

.label {
  color: #1d1c23;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: 22px;
  margin-left: 6px;
}
.icon {
  font-size: 16px;
}

.condition-show {
  border: solid 1px rgba(29, 28, 35, 0.16);
  padding: 10px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  position: relative;
  .tag-bian {
    flex: 1;
  }
  .input-center {
    margin: 0 5px;
  }
  &::before {
    position: absolute;
    content: '';
    width: 1px;
    height: 100%;
    background: rgba(29, 28, 35, 0.16);
    left: 50%;
    top: 0;
    z-index: -1;
  }
}
.input-tag {
  background: #f8f8f8;
  border: 1px solid rgba(29, 28, 35, 0.08);
  color: #000;
  font-weight: 500;
}
.align-center {
  display: flex;
  align-items: center;
}
</style>
