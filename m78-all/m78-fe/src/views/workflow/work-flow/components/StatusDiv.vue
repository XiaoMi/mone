<template>
  <div class="status-box" :class="`status-${resOutputs?.status}`">
    <div class="align-center">
      <el-icon class="icon">
        <component :is="statusIcon['icon' + resOutputs?.status || 2]" />
      </el-icon>
      <span class="label">{{ statusMap.label }} </span>
    </div>
    <ResultPop
      :title="`${nodeData.nodeMetaInfo.nodeName} 执行结果`"
      :width="nodesBase[nodeData.nodeType].popWidth"
      :visible="visible"
    >
      <BatchResult
        v-if="nodeData.nodeType == 'llm' && nodeData.batchType == 'batch'"
        :batchRes="batchRes"
        :resInputs="resInputs"
        :nodeData="nodeData"
        :resOutputs="resOutputs"
      />
      <template v-else>
        <StatusInputs :resInputs="resInputs" :nodeData="nodeData" />
        <StatusError :resOutputs="resOutputs" v-if="resOutputs.status == 3" />
        <StatusOutputs v-else :resOutputs="resOutputs" :nodeData="nodeData" />
      </template>
      <template #reference>
        <el-button link @click="switchShow">{{ visible ? '收起' : '展开' }}运行结果</el-button>
      </template>
    </ResultPop>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { opList } from '../baseInfo'
import ResultPop from './components/ResultPop'
import { nodesBase } from '../baseInfo.js'
import BatchResult from './components/BatchResult'
import StatusInputs from './components/StatusInputs'
import StatusOutputs from './components/StatusOutputs'
import StatusError from './components/StatusError'
import { SuccessFilled, CircleCloseFilled, WarningFilled } from '@element-plus/icons-vue'

const props = defineProps({
  resInputs: {},
  resOutputs: {},
  nodeData: {}
})
const statusIcon = {
  icon2: SuccessFilled,
  icon1: WarningFilled,
  icon3: CircleCloseFilled
}
const visible = ref(props?.nodeData?.nodeType == 'end')
const statusMap = computed(() => {
  const arr = statusArr.filter((item) => item.code === props.resOutputs.status)
  return arr[0] || {}
})
const statusArr = [
  {
    code: 1,
    label: '运行开始'
  },
  {
    code: 2,
    label: '运行成功'
  },
  {
    code: 3,
    label: '运行失败'
  }
]
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
// 开始
.status-1 {
  background: #fdf6ec;
  .icon {
    color: #e6a23c;
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
.status-3 {
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
