<template>
  <div class="workflow-header flex-center">
    <div class="left flex-center">
      <el-icon class="ret-btn" @click="backFn"><ArrowLeft /></el-icon>
      <FlowImg :imgWidth="30" :imgUrl="flowInfo?.avatarUrl" />
      <div>
        <h5 class="name">
          {{ flowInfo?.name }}
          <el-button link :icon="Edit" @click="editClick" :disabled="!hasPermission"></el-button>
        </h5>
        <div class="detail-box">
          <el-tooltip effect="dark" :content="flowInfo?.desc" placement="top">
            <p class="desc">{{ flowInfo?.desc || '暂无描述' }}</p>
          </el-tooltip>
          <FlowRecordId :flowRecordIdStatus="flowRecordIdStatus" v-if="flowStatusObj[flowStatus]" />
          <TimerTag
            v-if="flowStatusObj[flowStatus]"
            :type="tagType"
            plain
            style="margin-left: 10px"
          >
            <el-icon class="is-loading loading-icon" v-if="[0, 5].includes(flowStatus)">
              <Loading />
            </el-icon>
            <i>{{ flowStatusObj[flowStatus] }} </i
            ><i v-if="props.streamTimer" class="status-icon">{{ timer }}</i></TimerTag
          >
        </div>
      </div>
    </div>
    <div class="right">
      <!-- <el-button @click="obFn" type="danger">object</el-button> -->
      <el-button
        @click="emits('reDraw')"
        type="primary"
        :loading="reDrawing"
        :disabled="saveing || runOrTest || !getDetailed"
        >整理节点 <i class="iconfont icon-youhuabuju"></i
      ></el-button>
      <el-button @click="emits('stopRun')" type="danger" v-if="runOrTest">取消</el-button>
      <el-button
        @click="shareFn"
        type="success"
        v-if="flowRecordIdStatus && testType != 'single' && flowStatusObj[flowStatus]"
        >分享</el-button
      >
      <el-button
        @click="emits('clickRun', hasPermission)"
        type="primary"
        class="top-btn"
        :disabled="saveing || runOrTest || !getDetailed"
        v-if="!runOrTest && hasPermission"
        >保存并试运行</el-button
      >
      <el-button
        @click="emits('clickRun', fasle)"
        type="primary"
        class="top-btn"
        :disabled="saveing || runOrTest || !getDetailed"
        v-if="!runOrTest"
        >试运行</el-button
      >
      <el-button
        @click="emits('saveFn')"
        type="primary"
        class="top-btn save-btn"
        :disabled="saveing || runOrTest || !hasPermission"
        >保存</el-button
      >
      <CopyFlowBtn
        type="icon"
        :originalId="route.params.id"
        :disabled="!hasPermission"
        @copySuc="copySuc"
      />
      <!-- <el-button @click="zoomFn" type="danger" class="top-btn">zoomto 1</el-button> -->
    </div>
    <CreateFlow v-model="showCreate" :preInfo="flowInfo" type="edit" @createSuc="editSuc" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HeaderImg from '../imgs/workflow.png'
import CreateFlow from '@/views/workflow-list/CreateFlow.vue'
import { Edit } from '@element-plus/icons-vue'
import { formatTime, flowStatusObjBase } from '../work-flow/baseInfo.js'
import TimerTag from './TimerTag.vue'
import { Loading } from '@element-plus/icons-vue'
import { flowStatusArr } from '../work-flow/baseInfo'
import FlowImg from './components/FlowImg.vue'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
import FlowRecordId from './components/FlowRecordId.vue'
import { useVueFlow } from '@vue-flow/core'

const { toObject, zoomTo } = useVueFlow()

const router = useRouter()
const route = useRoute()
const props = defineProps({
  flowInfo: {},
  streamTimer: {},
  flowStatus: {},
  hasPermission: {},
  saveing: {},
  runOrTest: {},
  getDetailed: {},
  flowRecordIdStatus: {},
  testType: {},
  reDrawing: {}
})
const tagType = computed(() => {
  const obj = flowStatusArr.find((item) => item.code === props.flowStatus)
  return obj?.tagType || ''
})
const flowStatusObj = ref(flowStatusObjBase)
const emits = defineEmits(['editNameSuc', 'reDraw', 'stopRun', 'clickRun', 'saveFn'])

const showCreate = ref(false)
const backFn = () => {
  router.push({
    name: 'AI Probot Space',
    params: {
      id: props.flowInfo.workSpaceId
    },
    query: {
      tab: 'workflow'
    }
  })
}
const editClick = () => {
  showCreate.value = true
}
const editSuc = () => {
  emits('editNameSuc')
}
const timer = computed(() => {
  return formatTime(props.streamTimer)
})
const copySuc = (flowId) => {
  const { href } = router.resolve({
    path: router.currentRoute.path,
    params: { id: flowId }
  })
  window.open(href, '_blank') //打开新的窗口
}
const obFn = () => {
  zoomTo(1)
  console.log('toObject', toObject())
}

const getNewUrlWithFlowRecordId = (id) => {
  // 获取当前路由的完整路径和查询参数
  const currentFullPath = window.location.origin + route.fullPath
  const currentQuery = { ...route.query }

  // 添加新的查询参数
  currentQuery.flowRecordId = id

  // 生成新的 URL
  const newUrl = new URL(
    router.resolve({
      path: route.path,
      query: currentQuery
    }).href,
    currentFullPath
  ).toString()

  return newUrl
}

const shareFn = async () => {
  const url = getNewUrlWithFlowRecordId(props.flowRecordIdStatus)
  const { toClipboard } = useClipboard()
  try {
    await toClipboard(url)
    ElMessage.success('链接已复制到剪贴板！')
  } catch (e) {
    ElMessage.warning('您的浏览器不支持复制：', e)
  }
}
</script>

<style lang="scss" scoped>
.workflow-header {
  position: relative;
  z-index: 10;
  background: #f7f7fa;
  border-bottom: 1px solid#1d1c2314;
  justify-content: space-between;
  padding: 16px 24px;
}
.flex-center {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ret-btn {
  font-size: 20px;
  margin-left: 4px;
  margin-right: 20px;
}

.name {
  color: #1d1c23;
  font-size: 14px;
  font-weight: 600;
  line-height: 24px;
}
.desc {
  display: inline-block;
  background-color: #f0f0f5;
  border-radius: 4px;
  color: #1d1c2399;
  padding: 0 6px;
  height: 20px;
  line-height: 20px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}
.edit-btn {
  font-size: 12px;
  vertical-align: middle;
  margin-left: 3px;
}
.loading-icon {
  font-size: 16px;
  margin-right: 3px;
}
.status-icon {
  margin-left: 3px;
}
.detail-box {
  display: flex;
}
</style>
