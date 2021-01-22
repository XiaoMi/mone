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
        />
      </div>
  </d2-container>
</template>
<script>
import chartView from './components/chart-view'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import axios from 'axios'

import echarts from 'echarts'
export default {
  name: 'onSiteInspection',
  data () {
    return { 
      activeName: '',
      showChart: false,
      mostPerDayData:[ ],
      pipelineInfo:{ },
      mostPerMemberData: [ ],
      countMap: { },
      resultMap: { },
      environment: { },
      projectType: { },
      totalNum: [
        {
          id:1,
          name:'项目总数',
        },
        {     
          id:4,
          name:'docker容器数量',
        },
        {
          id:2,
          name:'总发布次数',
        },
        {
          id:3,
          name:'最近7天成功率',
        },
      ],
      loading: true
    }
  },
  created () {
    this.getPipelineInfo(),
    this.getAllData()
  },
  methods: {
    // initDate(){
    //   this.periodDay=[new Date().setHours(0, 0, 0, 0) , new Date().getTime()]
    //   this.periodMonth=[this.getMonthDate() , new Date().getTime()]
    // },
    // getMonthDate(){
    //   var data=new Date();
    //   data.setDate(1);
    //   data.setHours(0);
    //   data.setSeconds(0);
    //   data.setMinutes(0);
    //   return data.getTime()
    // },
    getAllData() {
      axios.all([
       this.getInitPerDay(),
       this.getReleaseInfo(),
       this.getReleaseInfoPart(),
      ]).then( axios.spread((day,release,part) => {
        this.mostPerDayData=this.fix(day.project);
        this.mostPerMemberData=this.fixMember(day.member);
        this.countMap= release.countMap
        this.resultMap= release.resultMap
        this.environment = part.environment
        this.projectType = part.projectType
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
        for(let key in data){
          if(data[key] === undefined){
            data[key] = '-'
          }
        }
        this.pipelineInfo = data
        this.loading = false
      }).catch(()=>{
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
    fix(data){
      let arr = []
      for(let key in data){
        arr.push(data[key])
      }
       arr = arr.sort((a,b)=>{
        return b['total'] - a['total']
      }).slice(0,10)
      return arr
    },
    fixMember(data){
      console.log()
      let arr = []
      for(let key in data){
        let obj = {}
        obj[key] = data[key]
        arr.push(obj)
      }
      arr = arr.sort((a,b)=>{
        return Object.values(b)[0] - Object.values(a)[0]
      }).slice(0,10)
      return arr
    },
    handleClick (tab, event) {
      // console.log(this.activeName);
      // console.log(tab, event);
    }
  },
  components: {
    chartView,
  },
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