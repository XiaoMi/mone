<template>
  <el-popover placement="right-end" :width="700" trigger="click">
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
      <div class="flow-t">
        <h6 class="name">
          <i class="iconfont icon-flow"></i>工作流详情
          <TimerTag :type="flowStatusArr.find((it) => it.code === endFlowStatusVal)?.tagType">{{
            flowStatusArr.find((it) => it.code === endFlowStatusVal)?.label
          }}</TimerTag>
        </h6>
        <div>
          <!-- 非终态才有取消按钮 -->
          <el-button type="danger" link v-if="!isFlowEnd" @click="cancleFn">取消</el-button>
        </div>
      </div>
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
            [{{ item.nodeId }}] {{ item.nodeName }}
            <TimerTag
              class="timer"
              :type="flowStatusArr.find((it) => it.code === item.status)?.tagType"
              v-if="[0, 1, 2, 3, 4].includes(item.status)"
            >
              <i>{{ formatTime(item.durationTime) }}</i>
            </TimerTag>
          </template>
          <!-- 如果是非手动确认节点才需要展示inputs, outputs -->
          <template v-if="item.nodeType != 'manualConfirm'">
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
          </template>
          <!-- 否则展示手动确认 -->
          <template v-else>
            <div class="confirm-box">
              <CustomConfirm v-model="item.nodeInfo" :botFlowRecordId="curFlowRecordId" />
            </div>
          </template>
        </el-collapse-item>
      </el-collapse>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed, watch, provide } from 'vue'
import { Loading, ArrowRight } from '@element-plus/icons-vue'
import {
  flowHeaderStatus,
  formatTime,
  flowStatusArr,
  flowStatusObjBase
} from '@/views/workflow/work-flow/baseInfo.js'
import TimerTag from '@/views/workflow/components/TimerTag.vue'
import { getFlowDetail } from '@/api/workflow'
import CustomConfirm from '@/views/workflow/work-flow/components/CustomConfirm.vue'
import SockJS from 'sockjs-client'

const activeNames = ref([])
const props = defineProps({
  flowData: {}
})
const flowStatusObj = ref(flowStatusObjBase)
const endStatus = computed(() => flowStatusArr.filter((it) => it.isEnd))
const flowId = computed(() => props.flowData.flowId)
const allNodes = ref([])
const curFlowRecordId = ref(null) // flowRecordId
const curFlowId = ref(null) // flowId
const isFlowEnd = ref(false) // flow是否为终态
const endFlowStatusVal = ref(null)
const getNodes = async (flowData) => {
  const { nodeInputsMap, nodeOutputsMap, flowRecordId, endFlowStatus, flowId, endFlowOutput } =
    flowData
  const inKeys = Object.keys(nodeInputsMap)
  const outKeys = Object.keys(nodeOutputsMap)
  const allKeys = [...new Set([...inKeys, ...outKeys])]
  const allArr = []
  curFlowRecordId.value = flowRecordId
  curFlowId.value = flowId
  endFlowStatusVal.value = endFlowStatus
  isFlowEnd.value = endStatus.value.find((it) => it.code == endFlowStatus)
  allKeys.forEach((key) => {
    const nodeType = nodeInputsMap[key].nodeType
    const { durationTime, status, nodeName, outputDetails } = nodeOutputsMap[key]
    allArr.push({
      nodeId: key,
      nodeName,
      nodeType,
      status,
      inputs: nodeInputsMap[key]?.inputDetails,
      durationTime,
      outputs: nodeType == 'end' ? endFlowOutput?.endFlowOutputDetails || [] : outputDetails || [],
      key: new Date(),
      nodeInfo: {
        resOutputs: {
          status
        }
      }
    })
  })
  allNodes.value = allArr
}

watch(
  () => props.flowData,
  (val) => {
    getNodes(val)
  },
  {
    immediate: true,
    deep: true
  }
)
const statusObj = computed(() => {
  const obj = flowHeaderStatus.find((it) => it.code == props.flowData.endFlowStatus)
  return obj
})

let sock = null

const operateFlowFn = (val) => {
  initSockjs({ operateCmd: 'operateFlow', ...val })
}

const cancleFn = () => {
  const p = {
    flowId: curFlowId.value,
    flowRecordId: curFlowRecordId.value,
    cmd: 'cancelFlow'
  }
  operateFlowFn(p)
}

const initSockjs = (params) => {
  if (!sock) {
    sock = new SockJS(
      `${window.location.origin}${
        import.meta.env.VITE_GLOB_API_NEW_URL
      }ws/sockjs/flow/status/stream`
    )

    sock.onopen = function () {
      console.log('SockJS is open now.')
      sock.send(JSON.stringify(params))
    }

    sock.onmessage = function (event) {
      const json = event.data
      try {
        const resData = JSON.parse(json)
        // handleRes(resData)
      } catch (error) {
        console.error(error)
      }
    }

    sock.onclose = function () {
      console.log('SockJS is closed now.')
      sock?.close()
      sock = null
    }

    sock.onerror = function (event) {
      console.error(event)
      sock = null
    }
  } else {
    sock.send(JSON.stringify(params))
  }
}
provide('operateFlowFn', operateFlowFn)
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  .name {
    font-size: 16px;
    font-weight: 800;
    color: #000;
  }
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
.confirm-box {
  padding: 0 10px;
  :deep(.empty-p) {
    height: 0;
  }
  :deep(.oz-form .oz-form-item:last-of-type) {
    margin-bottom: 0;
  }
}
</style>
