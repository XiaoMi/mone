<!--
 * @Description:
 * @Date: 2024-03-13 11:23:40
 * @LastEditTime: 2024-09-18 14:46:51
-->
<template>
  <div class="visit-container" v-loading="loading">
    <div :class="'visit-top ' + (moreVisible ? 'visit-bg' : '')">
      <BaseInfo
        :data="{
          name: detailData?.botInfo?.name || '----',
          avatarUrl: detailData?.botInfo?.avatarUrl
        }"
        size="small"
      >
        <template #bottom>
          <div class="visit-describe">
            <p>@{{ detailData?.botInfo?.creator || '***' }}</p>
            <p>|</p>
            <p>发布于{{ dateFormat(detailData?.botInfo?.publishTime, 'yyyy-mm-dd HH:MM:ss') }}</p>
          </div>
        </template>
      </BaseInfo>
      <div class="visit-top-right">
        <div @click="maxWindow = !maxWindow">
          <span>{{ maxWindow ? '还原' : '窗口最大化' }}</span
          ><el-icon><FullScreen /></el-icon>
        </div>
        <div type="danger" @click="collectClick">
          <span>收藏</span>
          <div :class="'collect ' + (collect ? 'selected' : 'no-selected')">
            <i class="iconfont icon-w_aixin"></i>
          </div>
        </div>
        <div @click="showRateDialog" class="evaluate">
          <span>评价</span><BaseStar :num="averageScore"></BaseStar>
        </div>
        <div>
          <span>分享</span><el-icon><Share /></el-icon>
        </div>
        <div @click="moreClick">
          <span>更多</span><el-icon><MoreFilled /></el-icon>
        </div>
        <More :data="detailData" :show-rate="refreshRate" v-model="moreVisible"></More>
      </div>
    </div>
    <div class="visit-bottom" :class="maxWindow ? 'w-[90%]!' : 'w-[50%]!'">
      <DebuggerPage
        :data="detailData"
        type="visit"
        :topicId="topicId"
        :showImg="showImg"
        v-if="active == 'chat'"
      ></DebuggerPage>
      <History v-else :data="historyData" @updatedTopicId="updatedTopicId"></History>
    </div>
    <div class="visit-btn-container">
      <div class="btn-item btn-index">
        <el-tooltip class="box-item" effect="dark" content="回到聊天" placement="left">
          <el-button type="primary" @click="goIndex"
            ><i class="iconfont icon-APP-robot1"></i
          ></el-button>
        </el-tooltip>
      </div>
      <div class="btn-line"></div>
      <div class="btn-item btn-new">
        <el-tooltip class="box-item" effect="dark" content="新会话" placement="left">
          <el-button @click="goNew"><i class="iconfont icon-xinhuihua"></i></el-button>
        </el-tooltip>
      </div>

      <div class="btn-item btn-history">
        <el-tooltip class="box-item" effect="dark" content="会话历史" placement="left">
          <el-button @click="goHistory"><i class="iconfont icon-lishihuihua"></i></el-button>
        </el-tooltip>
      </div>
    </div>
    <RateDialog
      v-model="rateDialogVisible"
      :bot-id="botId"
      :type="type"
      @update-rate="updateRate"
    ></RateDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeMount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { getBotSimpleInfo, getCommentRates, getTopicId } from '@/api/probot'
import { isCollect, applyCollect, deleteCollect } from '@/api/probot-visit'
import dateFormat from 'dateformat'
import { useProbotStore } from '@/stores/probot'
import More from './More.vue'
import History from './History.vue'
import BaseStar from '@/components/probot/BaseStar.vue'
import BaseInfo from '@/components/BaseInfo.vue'
import DebuggerPage from '@/components/probot/DebuggerPage.vue'
import RateDialog from '@/components/RateDialog.vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores'
import { t } from '@/locales'

const chatStore = useChatStore()
const active = ref('chat')
const historyData = ref([])

const router = useRouter()

const probotStore = useProbotStore()

const LLMModelSelList = computed(() => probotStore.LLMModelSelList)

const route = useRoute()

const topicId = ref('')
const botId = computed(() => {
  return route.params.id as string
})

const detailData = ref({
  botInfo: {
    avatarUrl: '---'
  }
})
const moreVisible = ref(false)
const averageScore = ref(0)
const collect = ref(false)
const rateDialogVisible = ref(false)
const loading = ref(false)
const type = ref(0)
const refreshRate = ref(true)
const showImg = ref(false)

const collectClick = () => {
  if (collect.value) {
    deleteCollect({
      type: '0',
      collectId: botId.value
    }).then((res) => {
      collect.value = false
    })
  } else {
    applyCollect({
      type: '0',
      collectId: botId.value
    }).then((res) => {
      if (res.code === 0) {
        collect.value = true
      }
    })
  }
}
const moreClick = () => {
  moreVisible.value = true
}

const showRateDialog = () => {
  rateDialogVisible.value = true
}

const getDetail = (botId: string) => {
  loading.value = true
  getBotSimpleInfo({
    botId
  })
    .then((res) => {
      if (res.code === 0) {
        detailData.value = res.data
        getTopicId({ botId: res.data?.botId, topicType: '1', createIfNotExist: 'true' }).then(
          (res) => {
            topicId.value = res?.data[0]?.id
          }
        )
        showImgFn()
      } else {
        ElMessage.error(res?.message)
      }
    })
    .finally(() => {
      loading.value = false
    })
}
const showImgFn = () => {
  showImg.value = false
  const { info = '' } = LLMModelSelList.value.find(
    (item) => item.cname === detailData.value.botSetting.aiModel
  )
  if (info) {
    try {
      const infoObj = JSON.parse(info)
      // 判断是否显示上传图片
      if (infoObj && infoObj['ability.vision'] == 'true') {
        showImg.value = true
      }
    } catch (error) {
      console.log('error', error)
    }
  }
}

const updateRate = async () => {
  await getCommentRatesFn()
  refreshRate.value = false
  nextTick(() => {
    refreshRate.value = true
  })
}

const getCommentRatesFn = async () => {
  try {
    const { code, data, message } = await getCommentRates({ itemId: botId.value, type: type.value })
    if (code == 0 && data) {
      averageScore.value = data.averageScore
    } else {
      console.error(message)
      // ElMessage.error(message || '出错了')
    }
  } catch (e) {
    console.error(e)
  }
}

onBeforeMount(() => {
  if (botId.value) {
    getDetail(botId.value)
    getCommentRatesFn()
    isCollect({
      type: '0',
      collectId: botId.value
    }).then((res) => {
      collect.value = res?.data
    })
  }
})

const goIndex = () => {
  active.value = 'chat'
}
const goNew = async () => {
  const { data } = await chatStore.addHistory({
    title: t('chat.newChat')
  })
  topicId.value = data.id
  active.value = 'chat'
}
const updatedTopicId = (data) => {
  topicId.value = data
  active.value = 'chat'
}
const goHistory = async () => {
  active.value = 'history'
}
const maxWindow = ref(false)
</script>

<style lang="scss" scoped>
.visit-container {
  height: calc(100% + 60px);
  display: flex;
  flex-direction: column;
  position: relative;
  .visit-top {
    padding: 20px;
    display: flex;
    align-items: center;
    &.visit-bg {
      background-color: rgb(219 239 255);
    }
    .visit-describe {
      display: flex;
      font-size: 12px;
      line-height: 20px;
      color: rgb(107, 114, 128);
      p:nth-child(1) {
        color: rgba(29, 28, 35, 0.6);
        padding-right: 6px;
        font-weight: 400;
        line-height: 22px;
      }
      p:nth-child(2) {
        color: rgba(56, 55, 67, 0.08);
      }
      p:nth-child(3) {
        padding-left: 6px;
        color: rgba(29, 28, 35, 0.6);
        font-size: 12px;
        font-weight: 400;
        line-height: 22px;
      }
    }
    .visit-top-right {
      display: flex;
      align-items: center;
      line-height: 20px;
      font-size: 14px;
      color: rgb(71, 85, 105);
      font-weight: 500;
      & > div {
        padding: 10px 15px;
        cursor: pointer;
        display: flex;
        align-items: center;
        span {
          padding-right: 6px;
        }
        &:hover {
          color: #333;
        }
      }
      .collect {
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        &.no-selected {
          background-image: linear-gradient(to left, #bdbbbe 0%, #9d9ea3 100%),
            radial-gradient(
              88% 271%,
              rgba(255, 255, 255, 0.25) 0%,
              rgba(254, 254, 254, 0.25) 1%,
              rgba(0, 0, 0, 0.25) 100%
            ),
            radial-gradient(50% 100%, rgba(255, 255, 255, 0.3) 0%, rgba(0, 0, 0, 0.3) 100%);
        }
        &.selected {
          background-image: linear-gradient(
            to right,
            #ff8177 0%,
            #ff867a 0%,
            #ff8c7f 21%,
            #f99185 52%,
            #cf556c 100%
          );
        }
      }
      .evaluate {
        display: flex;
        align-items: center;
      }
    }
  }

  .visit-bottom {
    flex: 1;
    background: rgba(255, 255, 255, 1);
    padding: 20px;
    margin: 0 auto;
    overflow: auto;
    transition: width 0.5s ease;
  }
  .visit-btn-container {
    position: absolute;
    right: 10px;
    top: calc(50% - 87px);
    background-color: #fff;
    padding: 15px 10px;
    border-radius: 10px;
    .btn-item {
      &.btn-index {
        padding-bottom: 15px;
      }
      &.btn-new {
        padding-top: 15px;
        padding-bottom: 15px;
      }
      &.btn-history {
      }
    }
    .btn-line {
      width: 80%;
      height: 1px;
      background-color: #ddd;
      margin: 0 auto;
    }
  }
}
</style>
