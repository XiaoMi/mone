<!--
 * @Description:
 * @Date: 2024-03-13 11:23:40
 * @LastEditTime: 2024-03-28 17:05:09
-->
<template>
  <div class="visit-container" v-loading="loading">
    <div :class="'visit-top ' + (moreVisible ? 'visit-bg' : '')">
      <BaseInfo
        :data="{
          name: props.detailData?.pluginOrgName || '----',
          avatarUrl: detailData?.avatarUrl
        }"
        size="small"
      >
        <template #bottom>
          <div class="visit-describe">
            <p>@{{ props.detailData?.creator || '***' }}</p>
            <p>|</p>
            <p>发布于{{ dateFormat(props.detailData?.createTime, 'yyyy-mm-dd HH:MM:ss') }}</p>
          </div>
        </template>
      </BaseInfo>
      <div class="visit-top-right">
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
        <More :data="props.detailData" :show-rate="refreshRate" v-model="moreVisible"></More>
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
import { useRoute } from 'vue-router'
import BaseStar from '@/components/BaseStar.vue'
import BaseInfo from '@/components/BaseInfo.vue'
import RateDialog from '@/components/RateDialog.vue'
import { isCollect, applyCollect, deleteCollect } from '@/api/probot-visit'
// @ts-ignore
import dateFormat from 'dateformat'
import More from './More.vue'
import { getCommentRates } from '@/api/probot'

const route = useRoute()

const props = defineProps({
  detailData: {
    type: Object,
    default() {
      return {}
    }
  }
})

const botId = computed(() => {
  return route.query.id as string
})
const refreshRate = ref(true)
const moreVisible = ref(false)
const averageScore = ref(0)
const collect = ref(false)
const rateDialogVisible = ref(false)
const loading = ref(false)
const type = ref(1)

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
      console.log()
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
    getCommentRatesFn()
    isCollect({
      type: '0',
      collectId: botId.value
    }).then((res) => {
      collect.value = res?.data
    })
  }
})
</script>

<style lang="scss" scoped>
.visit-container {
  height: calc(100% + 60px);
  display: flex;
  flex-direction: column;
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
}
</style>
