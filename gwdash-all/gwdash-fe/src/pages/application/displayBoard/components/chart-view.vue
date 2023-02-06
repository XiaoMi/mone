<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
   <div style='padding-right:5px;'>
      <div class='chart' ref='chartCost'></div>
      <div class='chart' ref='chartQps' v-show="showQps"></div>
      <div class='chart' ref='chartDeployTimesPerMonth'></div>
      <div class='chart' ref='chartPerDay'></div>
      <div class='chart' ref='chartPerMember'></div>
      <div class='chart chart-small' ref='chartResult'></div>
      <div class='chart chart-small chart-left' ref='chartTimes'></div>
      <div class='chart chart-small' ref='chartEnv'></div>
      <div class='chart chart-small chart-left' ref='chartProject'></div>
      <div class='chart chart-small chart-left' ref='chartUtilization'></div>
   </div>
</template>

<script>
// 按需引入
// 主模块
import echarts from 'echarts/lib/echarts'
// 折线图
import 'echarts/lib/chart/line'
// 提示框、配置项
import 'echarts/lib/component/legend'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/toolbox'
import 'echarts/lib/component/dataZoom'

import service from '@/plugin/axios/index'
import axios from 'axios'
import bizutil from '@/common/bizutil'

import configCost from './chart-xconfig-cost'
import configQps from './chart-xconfig-qps'
import configChartDeployTimesPerMonth from './chart-view-deply-times-per-month'
import configPerDay from './chart-xconfig-most-per-day'
import configPerMember from './chart-xconfig-most-per-member.js'
import configResult from './chart-xconfig-result.js'
import configTimes from './chart-xconfig-times.js'
import configEnv from './chart-xconfig-env.js'
import configProject from './chart-xconfig-project.js'
import configUtilization from './chart-xconfig-utilization.js'
import envType from './../../env_map'

const arrConfig = [configCost, configQps, configChartDeployTimesPerMonth, configPerDay, configPerMember, configResult, configTimes, configEnv, configProject, configUtilization]
const propsArr = ['show', 'mostPerDayData', 'mostPerMemberData', 'countMap', 'resultMap', 'environment', 'projectType', 'mostCost', 'mostQps', 'deployTimesPerMonth', 'showQps', 'utilization']
let propsObj = {}
propsArr.forEach(it => {
  ['show' || 'showQps'].includes(it)
    ? propsObj[it] = {
      type: Boolean,
      required: true
    }
    : propsObj[it] = {
      type: Array,
      required: true
    }
})
export default {
  props: propsObj,
  data () {
    return {
      envType: Object.values(envType).filter(it => {
        if (it instanceof Array) {
          return false
        } else {
          return true
        }
      }),
      realData: [],
      predictData: [],
      chartDialogVisible: false
    }
  },
  watch: {
    show: {
      handler () {
        if (this.show) {
          this.getRealData()
        }
      }
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.drawChart()
    })
  },
  methods: {
    drawChart () {
      Object.keys(this.$refs).forEach((it, i) => {
        echarts.init(this.$refs[it]).setOption(arrConfig[i](this, true))
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.chart {
  width: 97%;
  height: 300px;
  margin-top: 20px;
  margin-left: 20px;
  box-shadow: 0 0 8px 0 rgba(232,237,250,.6), 0 2px 4px 0 rgba(232,237,250,.5);
  &:hover {
    box-shadow: 0 0 8px 0 rgba(232,237,250,.6), 0 2px 4px 0 rgba(232,237,250,.5);
  }
  &-small {
    width: 47%;
    height: 250px;
    float: left;
  }
  &-left {
    margin-left: 31px;
  }
  &:last-child {
    margin-bottom: 20px;
  }
}
</style>
