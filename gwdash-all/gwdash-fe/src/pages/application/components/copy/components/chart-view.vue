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
import config from './chart-xconfig'

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    },
    id: {
      type: Number,
      required: true
    },
    envId: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      showData: [],
      chartDialogVisible: false
    }
  },
  watch: {
    show: {
      immediate: true,
      handler () {
        if (this.show) {
          this.getAllData()
        }
      }
    },
    envId () {
      this.getAllData()
    }
  },
  methods: {
    getAllData () {
      axios.all([this.getReplicates()]).then(axios.spread((data) => {
        this.initData(data)
        this.drawChart()
      }))
    },
    getReplicates () {
      let id = this.id
      let envId = this.envId
      if (envId === '' || !envId) {
        console.log('envId id null')
        return
      }
      return service({
        url: `/predict/replicates?projectId=${id}&envId=${envId}`
      })
    },
    getDate () {
      var date = new Date()
      var nowMonth = date.getMonth() + 1
      var strDate = date.getDate()
      // 日期分割符号
      var seperator = "-"
      var endDate = date.getFullYear() + seperator + nowMonth + seperator + strDate
      return endDate
    },
    initData (data) {
      this.showData = (data && data.replicates || []).map(
        (ele, index) => {
          let obj = {
            value: []
          }
          var time = this.getDate() + ' 00:00:00'
          time = time.replace(new RegExp("-", "gm"), "/")
          var startDateM = (new Date(time)).getTime() + index * data.period * 60000
          var startDateY = bizutil.timeFormat(startDateM)
          if (startDateM < new Date().getTime()) {
            obj.value[0] = startDateY
            obj.value[1] = ele
            return obj
          }
        }
      )
    },
    drawChart () {
      let chart = echarts.init(this.$refs.chart)
      chart.setOption(config(this, true))
    },
    dislogOpen () {
      this.$nextTick(() => {
        let newChart = echarts.init(this.$refs.newChart)
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
