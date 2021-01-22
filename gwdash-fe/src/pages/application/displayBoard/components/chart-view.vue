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
      <div class='chart' ref='chartPerDay'></div>
      <div class='chart' ref='chartPerMember'></div>
      <div class='chart chart-small' ref='chartResult'></div>
      <div class='chart chart-small chart-left' ref='chartTimes'></div>
      <div class='chart chart-small' ref='chartEnv'></div>
      <div class='chart chart-small chart-left' ref='chartProject'></div>
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

import configPerDay from './chart-xconfig-most-per-day'
import configPerMember from './chart-xconfig-most-per-member.js'
import configResult from './chart-xconfig-result.js'
import configTimes from './chart-xconfig-times.js'
import configEnv from './chart-xconfig-env.js'
import configProject from './chart-xconfig-project.js'

export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    mostPerDayData: {
      type: Array,
      required: true
    },
    mostPerMemberData: {
      type: Array,
      required: true
    },
    countMap: {
      type: Object,
      required: true
      },
    resultMap: {
      type: Object,
      required: true
    },
        environment: {
      type: Object,
      required: true
    },
        projectType: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      realData: [],
      predictData: [],
      envType:[
        {
            name :'日常',
            icon :'circle',
            profile: 'dev'
        } ,
        {
            name :'staging',
            icon :'circle',
            profile: 'staging'
        }, {
            name :'预发',
            icon :'circle',
            profile: 'preview'
        } , {
            name :'内网',
            icon :'circle',
            profile: 'intranet'
        } , {
            name :'线上集群',
            icon :'circle',
            profile: 'production'
        } ,{
            name :'线上c3集群',
            icon :'circle',
            profile: 'c3'
        } ,{
            name :'线上c4集群',
            icon :'circle',
            profile: 'c4'
        } ],
      chartDialogVisible: false
    }
  },
  watch: {
    show: {
      handler() {
        if(this.show) {
         this.getRealData()
        }
      }
    }
  },
   mounted() {
    this.$nextTick(() => {
      this.drawChart()
    });
  },
  methods: {
    drawChart() {
      let chartPerDay = echarts.init(this.$refs.chartPerDay),
          chartPerMember = echarts.init(this.$refs.chartPerMember),
          chartResult = echarts.init(this.$refs.chartResult),
          chartTimes = echarts.init(this.$refs.chartTimes),
          chartEnv = echarts.init(this.$refs.chartEnv),
          chartProject = echarts.init(this.$refs.chartProject);
      chartPerDay.setOption(configPerDay(this,true))
      chartPerMember.setOption(configPerMember(this,true))
      chartResult.setOption(configResult(this,true))
      chartTimes.setOption(configTimes(this,true))
      chartEnv.setOption(configEnv(this,true))
      chartProject.setOption(configProject(this,true))
    },
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