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
      <el-dialog :visible.sync="chartDialogVisible" @open='dislogOpen' width='1000px'>
        <div class='newChart' ref='newChart'></div>
      </el-dialog>
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
// import macarons from './macarons'
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
    }
  },
  data() {
    return {
      realData: [],
      predictData: [],
      chartDialogVisible: false
    }
  },
  watch: {
    // watch监听不生效
    // show() {
    //   console.log(this.show,'TRU');
    //   if (this.show) {
    //     getAllData();
    //   }
    // }
    show: {
      immediate:true,
      handler() {
        if(this.show) {
          this.getAllData();
        }
      }
    }
  },
  methods: {
    getAllData() {
      axios.all([this.getRealData(), this.getPredictData()]).then( axios.spread((real,predict) => {
        this.dealRealData(real.data);
        this.dealPredictData(predict);
        this.drawChart();
       }))
    },
    dealRealData(real) {
      if (!Array.isArray(real)) return; 
      var realData = real.reduce((result,item) => {
          return result.concat(item.queriesPerMinute)
      },[])

      var now = new Date().setHours(0,0,0,0);
      
      this.realData = realData.map( item => {
          var time = bizutil.formatNow(now);
          now += 60 * 1000;
          return {
            value: [time,item]
          }
       })
    },
    dealPredictData(predict) {
        if (!Array.isArray(predict)) return; 
        var now = new Date().setHours(0,0,0,0);

        this.predictData = predict.map( item => {
          var time = bizutil.formatNow(now);
          now += 60 * 1000;
          return {
            value: [time,item]
          }
        })
    },
    getRealData() {
      let id = this.id;
      return service({
        url: `/predict/getRealData?projectId=${id}`,
      })
    },
    getPredictData() {
      let id = this.id;
      return service({
        url: `/predict/getPredictData?projectId=${id}`,
      })
    },
    drawChart() {
      // echarts.registerTheme('macarons', macarons);
      // let chart = echarts.init(this.$refs.chart,"macarons");
      let chart = echarts.init(this.$refs.chart);
      chart.setOption(config(this,true))
    },
    dislogOpen() {
      this.$nextTick( () => {
        let newChart = echarts.init(this.$refs.newChart);
        newChart.setOption(config(this))
      })
    }
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