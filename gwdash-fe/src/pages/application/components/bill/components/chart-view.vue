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
   <div style='padding-right:5px'>
      <div class='chart' ref='chart'></div>
   </div>
</template>

<script>
// 按需引入
// 主模块
import echarts from 'echarts/lib/echarts'
// 柱状图
import 'echarts/lib/chart/bar'
// 提示框、配置项
import 'echarts/lib/component/legend'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/toolbox'
import 'echarts/lib/component/dataZoom'

import service from '@/plugin/axios/index'
import axios from 'axios'
import bizutil from '@/common/bizutil'
import config from './chart-xconfig'

export default {
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    id: {
      type: Number,
      required: true
    },
    envId:{
      type: Number,
      required: true,
    },
    year : {
      type: Number,
      required: true,
    }
  },
  data() {
    return {
      showData:[],
    }
  },
  watch: {
    show: {
      immediate:true,
      handler() {
        if(this.show) {
          this.getAllData();
        }
      }
    },
    year () {
        this.getAllData();
    },
    envId () {
        this.getAllData();
    }
  },
  methods: {
    getAllData() {
      axios.all([this.getRealData()]).then( axios.spread((data) => {
        this.initData(data);
        this.drawChart();
       }))
    },
    getRealData() {
      let id = this.id;
      let envId = this.envId;
      let year = this.year;
      if (envId === '' || !envId) {
        console.log('envId id null')
        return
      }
       return service({
        url: `/billing/project/year?projectId=${id}&envId=${envId}&year=${year}`,
      })
    },
    initData(data) {
        if(data){
          this.showData = data.list.map((item) => { 
              return item.price
          })
        }
    },
    drawChart() {
      let chart = echarts.init(this.$refs.chart);
      chart.setOption(config(this))
    },
  }
}
</script>

<style lang="scss" scoped>
.chart {
  width: 100%;
  height: 450px;
  margin-top: 20px;
}
.newChart {
  width: 100%;
  height: 600px;
}
</style>