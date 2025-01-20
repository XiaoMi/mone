<template>
  <div class="probot-log">
    <!-- 调用统计 -->
    <div class="chart-wrap">
      <h1>调用统计</h1>
      <div class="filter">
        <span>天数：</span>
        <el-select
          v-model="daysAgo"
          placeholder="Select"
          style="width: 240px"
          @change="daysAgoChange"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <div class="chart-box">
        <div class="chart-item">
          <h2>全部消息数</h2>
          <p>过去{{ daysAgo }}天</p>
          <StatisticsChart class="chart-content" :data="allMsgData"></StatisticsChart>
        </div>
        <div class="chart-item">
          <h2>活跃用户数</h2>
          <p>过去{{ daysAgo }}天</p>
          <StatisticsChart class="chart-content" :data="userChartData"></StatisticsChart>
        </div>
      </div>
    </div>
    <!-- 调用日志 -->
    <LogChart></LogChart>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import StatisticsChart from './StatisticsChart'
import LogChart from './LogChart'
import { useRoute } from 'vue-router'
import dateFormat from 'dateformat'

import { listPerdayInfoByBotId } from '@/api/probot-log'

const route = useRoute()
const daysAgo = ref(7)
const options = [
  {
    value: 7,
    label: '7'
  },
  {
    value: 15,
    label: '15'
  },
  {
    value: 30,
    label: '30'
  }
]
const allMsgData = ref({
  xSeries: [],
  ySeries: []
})
const userChartData = ref({
  xSeries: [],
  ySeries: []
})

const getChartData = () => {
  listPerdayInfoByBotId({
    relateId: route?.query?.botId,
    daysAgo: daysAgo.value
  }).then((res) => {
    allMsgData.value.xSeries=[]
    allMsgData.value.ySeries=[]
    userChartData.value.xSeries=[]
    userChartData.value.ySeries=[]
    res.data.list.reverse()
    res.data.list.forEach((item) => {
      allMsgData.value.xSeries.push(dateFormat(item.invokeDay, 'yyyy-mm-dd'))
      allMsgData.value.ySeries.push(item.invokeCounts)

      userChartData.value.xSeries.push(dateFormat(item.invokeDay, 'yyyy-mm-dd'))
      userChartData.value.ySeries.push(item.invokeUsers)
    })
  })
}
const daysAgoChange = () => {
  getChartData()
}
onMounted(() => {
  getChartData()
})
</script>

<style lang="scss" >
.probot-log {
  min-width: 1200px;
  margin :0 auto;
  padding: 10px 40px;
  .chart-wrap {
    padding-top: 30px;
    &:first-child{
      padding-top: 0px;
    }
    .chart-title {
      display: flex;
      p {
        line-height: 65px;
        padding-left: 10px;
      }
    }
    .filter {
      display: flex;
      align-items: center;
      font-size: 14px;
      color: #666;
      margin-bottom: 10px;
      span {
        padding-left: 6px;
      }
    }
    h1 {
      font-size: 20px;
      color: #666;
      line-height: 40px;
      margin-top: 10px;
      margin-bottom: 10px;
    }
    .chart-box {
      display: flex;
      justify-content: space-between;
      .chart-item {
        border-radius: 10px;
        width: 49%;
        padding: 10px;
        min-height: 300px;
        background: #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        h2 {
          font-size: 16px;
          color: #666;
          line-height: 30px;
        }
        p {
          font-size: 14px;
          color: #666;
          line-height: 20px;
        }
        .chart-content {
          width: 100%;
          height: 100%;
          overflow: auto;
        }
      }
    }
  }
}
</style>
