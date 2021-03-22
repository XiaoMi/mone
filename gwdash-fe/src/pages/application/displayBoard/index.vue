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
  <d2-container>
      <div class="content">
        <div class="total" v-loading='loading'>
          <div class="total-num" v-for="item in totalNum" :key="item.id">
            <div>
              <h1 v-if="item.id==1">
                {{pipelineInfo.projectCount?pipelineInfo.projectCount:'-'}}
              </h1>
              <h1 v-if="item.id==4">
                {{pipelineInfo.dockerCount?pipelineInfo.dockerCount:'-'}}</h1>
              <h1 v-if="item.id==2">
                {{pipelineInfo.releaseCount?pipelineInfo.releaseCount:'-'}}
                </h1>
              <h1 v-if="item.id==3">{{pipelineInfo.successRate?pipelineInfo.successRate+'%':'-'}}</h1>
              <h3>{{item.name}}</h3>
            </div>
          </div>
        </div>
        <chart-view
          v-if="showChart"
          :show='showChart'
          :resultMap='resultMap'
          :countMap='countMap'
          :mostPerDayData='mostPerDayData'
          :mostPerMonthData='mostPerMonthData'
          :mostPerMemberData='mostPerMemberData'
          :environment='environment'
          :projectType='projectType'
          :mostCost='mostCost'
          :mostQps='mostQps'
          :showQps='showQps'
          :deployTimesPerMonth='deployTimesPerMonth'
          :utilization='utilization'
        />
      </div>
  </d2-container>
</template>
<script>
import chartView from './components/chart-view'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import axios from 'axios'
import totalNum from './../totalNum_map'
import echarts from 'echarts'

export default {
  name: 'onSiteInspection',
  data () {
    return {
      activeName: '',
      showChart: false,
      mostPerDayData: [ ],
      pipelineInfo: { },
      mostPerMemberData: [ ],
      countMap: { },
      resultMap: { },
      environment: { },
      projectType: { },
      mostCost: [],
      mostQps: [],
      showQps: true,
      totalNum: totalNum,
      loading: true,
      deployTimesPerMonth: [],
      utilization: []
    }
  },
  created () {
    this.getPipelineInfo()
    this.getAllData()
  },
  methods: {
    getAllData () {
      axios.all([
        this.getInitPerDay(),
        this.getReleaseInfo(),
        this.getReleaseInfoPart(),
        this.getDeployTimesPerMonth(),
        this.getCostInfo(),
        this.getQpsInfo(),
        this.getDailyUtilizationStats()
      ]).then(axios.spread((day, release, part, deployTimesPerMonth, cost, qps, utilization) => {
        this.mostPerDayData = this.fix(day.project)
        this.mostPerMemberData = this.fixMember(day.member)
        this.countMap = release.countMap
        this.resultMap = release.resultMap
        this.environment = part.environment
        this.projectType = part.projectType
        this.mostCost = (cost && Object.keys(cost).map(it => {
          let obj = {}
          obj.projectName = it
          obj.cost = cost[it]
          return obj
        })) || []
        this.mostQps = (qps && Object.keys(qps).map(it => {
          let obj = {}
          obj.projectName = it
          obj.qps = qps[it]
          return obj
        })) || []
        this.showQps = this.mostQps.some(it => it.qps !== 0)

        this.deployTimesPerMonth = Object.keys(deployTimesPerMonth).map(it => {
          let obj = {}
          obj.time = it
          obj.deployTimes = deployTimesPerMonth[it]
          return obj
        })
        this.utilization = utilization.map(it => {
          it.timestamp = bizutil.timeFormat(it.timestamp)
          it.clusterUtilization = it.clusterUtilization * 100

          return it
        })
        this.showChart = true
      }))
    },
    getInitPerDay () {
      return service({
        url: `/project/statistics`,
        method: 'GET'
      })
    },
    getPipelineInfo () {
      service({
        url: `/pipeline/statistics`,
        method: 'GET'
      }).then((data) => {
        for (let key in data) {
          if (data[key] === undefined) {
            data[key] = '-'
          }
        }
        this.pipelineInfo = data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getReleaseInfo () {
      return service({
        url: `/pipeline/statistics7days`,
        method: 'GET'
      })
    },
    getReleaseInfoPart () {
      return service({
        url: `/pipeline/statisticsChart`,
        method: 'GET'
      })
    },
    getCostInfo () {
      return service({
        url: `/billing/detail/topten`,
        method: 'GET'
      })
    },
    getQpsInfo () {
      return service({
        url: `/project/qps`,
        method: 'GET'
      })
    },
    getDailyUtilizationStats () {
      return service({
        url: `/cluster/utilization/daily`,
        method: 'GET'
      })
    },
    getDeployTimesPerMonth () {
      return service({
        url: `/pipeline/deployMonth`,
        method: 'GET'
      })
    },
    fix (data) {
      let arr = []
      for (let key in data) {
        arr.push(data[key])
      }
      arr = arr.sort((a, b) => {
        return b['total'] - a['total']
      }).slice(0, 10)
      return arr
    },
    fixMember (data) {
      let arr = []
      for (let key in data) {
        let obj = {}
        obj[key] = data[key]
        arr.push(obj)
      }
      arr = arr.sort((a, b) => {
        return Object.values(b)[0] - Object.values(a)[0]
      }).slice(0, 10)
      return arr
    },
    handleClick (tab, event) {
      // console.log(this.activeName);
      // console.log(tab, event);
    }
  },
  components: {
    chartView
  }
}
</script>

<style lang="scss" scoped>
.total{
  margin-top: 20px;
  margin-left: 20px;
  width: 97%;
  height: 190px;
  &-num {
    float:left;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24%;
    height: 100%;
    background: white;
    box-shadow: 0 0 8px 0 rgba(232,237,250,.6), 0 2px 4px 0 rgba(232,237,250,.5);
    div {
      text-align: center;
      :hover{
        color: #509ee3;
      }
    }
  }
  &-num:nth-child(2){
    margin: 0 12px;
  }
  &-num:nth-child(3){
    margin: 0 12px 0 0;
  }
  h1 {
    font-size: 3em;
    font-weight: 500;
    margin: 0;
  }
  h3 {
    font-weight: 500;
  }
}
</style>
