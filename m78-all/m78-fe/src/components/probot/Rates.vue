<template>
  <div class="py-[20px]">
    <div>
      <div class="pb-[10px]">
        <h2>评分与评价</h2>
      </div>
      <div class="flex">
        <div class="w-[190px] flex justify-center justify-items-center shrink-0">
          <div class="flex flex-col justify-center justify-items-center">
            <div class="text-8xl font-semibold">{{ score.averageScore.toFixed(1) }}</div>
            <div class="pt-[10px] flex justify-center"><span>综合评分</span></div>
          </div>
        </div>
        <div class="grow">
          <div class="flex py-[6px] justify-items-center" v-for="item in 5" :key="item">
            <div class="flex">{{ item }}星</div>
            <div class="flex px-[8px]"><BaseStar :num="item" /></div>
            <div class="grow">
              <el-progress
                text-inside
                :stroke-width="18"
                :percentage="score[`scorePercent${item}`]"
              >
                <span>{{ score[`score${item}`] }}</span></el-progress
              >
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="py-[20px]">
      <div class="py-[10px]">
        <el-radio-group v-model="radio" @change="handleClick" size="large">
          <el-radio-button label="total" value="total"
            >全部（{{ score.totalCount }}）</el-radio-button
          >
          <el-radio-button label="good" value="good">好评（{{ score.goodCount }}）</el-radio-button>
          <el-radio-button label="medium" value="medium"
            >中评（{{ score.mediumCount }}）</el-radio-button
          >
          <el-radio-button label="bad" value="bad">差评（{{ score.badCount }}）</el-radio-button>
        </el-radio-group>
      </div>
      <div>
        <template v-if="rateInfo[radio].data.length > 0">
          <div v-infinite-scroll="loadMore">
            <el-divider />
            <div v-for="item of rateInfo[radio].data" :key="item.id">
              <div class="flex">
                <div class="w-[100px] shrink-0">
                  <div class="font-semibold text-ellipsis overflow-hidden">{{ item.createBy }}</div>
                </div>
                <div class="pl-[10px] grow">
                  <div><BaseStar :num="item.score"></BaseStar></div>
                  <div class="py-[10px] text-ellipsis max-h-[80px]">{{ item.commentContent }}</div>
                  <div>{{ item.createTime }}</div>
                </div>
              </div>
              <el-divider />
            </div>
          </div>
        </template>
        <template v-else>
          <el-empty description="暂无评论" />
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getCommentList, getCommentRates } from '@/api/probot'
import BaseStar from '@/components/probot/BaseStar.vue'
import { ElMessage } from 'element-plus'
// @ts-ignore
import dateFormat from 'dateformat'

type Rate = {
  id: string
  botId: string
  commentContent: string
  createBy: string
  createTime: string
  updateTime: string
  score: number
}

type TableRate = {
  pageNum: number
  pageSize: number
  total: number
  data: Rate[]
}

type CommontTypeEmue = 'total' | 'good' | 'medium' | 'bad'

const props = defineProps<{
  botId: string
  type: number
}>()
const score = ref({
  averageScore: 0,
  badCount: 0,
  goodCount: 0,
  mediumCount: 0,
  score1: 0,
  score2: 0,
  score3: 0,
  score4: 0,
  score5: 0,
  totalCount: 1
})
const radio = ref<CommontTypeEmue>('total')
const rateInfo = ref<{
  total: TableRate
  good: TableRate
  medium: TableRate
  bad: TableRate
}>({
  total: {
    pageNum: 1,
    pageSize: 20,
    total: 0,
    data: []
  },
  medium: {
    pageNum: 1,
    pageSize: 20,
    total: 0,
    data: []
  },
  good: {
    pageNum: 1,
    pageSize: 20,
    total: 0,
    data: []
  },
  bad: {
    pageNum: 1,
    pageSize: 20,
    total: 0,
    data: []
  }
})

const handleClick = async (type: string) => {
  // @ts-ignore
  if (score.value[`${type}Count`] && rateInfo.value[type as CommontTypeEmue].data.length == 0) {
    await fetchList(type as CommontTypeEmue)
  }
}

const loadMore = () => {
  console.log('load-more')
}

const fetchList = async (type: CommontTypeEmue) => {
  try {
    const { code, data, message } = await getCommentList({
      itemId: props.botId,
      type: props.type,
      commentType: type.replace('total', '').trim(),
      pageNum: 1,
      pageSize: 30
    })
    if (code === 0) {
      rateInfo.value[type] = {
        pageNum: data.pageNumber,
        pageSize: data.pageSize,
        total: data.totalRow,
        data: (data.records || []).map((it: Rate) => {
          return {
            ...it,
            createTime: dateFormat(it.createTime, 'yyyy-mm-dd HH:MM:ss')
          }
        })
      }
    } else {
      ElMessage.error(message || '获取评论列表失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取评论列表失败')
  }
}

const fetchRates = async () => {
  try {
    const { code, data, message } = await getCommentRates({
      itemId: props.botId,
      type: props.type
    })
    if (code == 0 && data) {
      // console.log(data)
      const total = data.totalCount || 1
      score.value = {
        ...data,
        scorePercent1: (data.score1 / total) * 100,
        scorePercent2: (data.score2 / total) * 100,
        scorePercent3: (data.score3 / total) * 100,
        scorePercent4: (data.score4 / total) * 100,
        scorePercent5: (data.score5 / total) * 100
      }
    } else {
      ElMessage.error(message || '获取评价失败')
    }
  } catch (e) {
    ElMessage.error('获取评价失败')
    console.error(e)
  }
}

const init = async () => {
  await fetchRates()
  await fetchList('total')
}

init()
</script>
