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
  <div class="onsite-inspection">
     <el-date-picker
      v-model="period"
      size="mini"
      class="period-cls"
      type="datetimerange"
      value-format="timestamp"
      range-separator="至"
      start-placeholder="开始日期"
      @change="getData"
      end-placeholder="结束日期">
    </el-date-picker>
   <d2-module class="inspection-modules">
    <div class="graph" ref="canvas"> </div>
    <div class="inspection-tab-box">
        <el-tabs type="border-card" v-model="listTabActiveName" class="inspection">
         <el-tab-pane label="成功部署列表" name="success"  class="pane">
            <div class="table-box">
            <el-table
              :data="successList"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
               <el-table-column
                prop="pipelineId"
                label="pipelineId"
                >
              </el-table-column>
              <el-table-column
                prop="projectName"
                label="项目"
                >
              </el-table-column>
              <el-table-column
                prop="env"
                label="环境">
              </el-table-column>
              <el-table-column
                 prop="deployUser"
                label="部署人"
                >
              </el-table-column>
               <el-table-column
                prop="deployTime"
                label="部署时间"
                width="220"
                >
              </el-table-column>

            </el-table>
          </div>
      </el-tab-pane>
      <el-tab-pane label="失败部署列表" name="failure" >
          <div class="table-box">
            <el-table
              :data="failureList"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
               <el-table-column
                prop="pipelineId"
                label="pipelineId"
                >
              </el-table-column>
              <el-table-column
                prop="projectName"
                label="项目"
                >
              </el-table-column>
                  <el-table-column
                prop="env"
                label="环境">
              </el-table-column>
              <el-table-column
                 prop="deployUser"
                label="部署人"
                >
              </el-table-column>
               <el-table-column
                prop="deployTime"
                label="部署时间"
                width="220"
                >
              </el-table-column>
               <el-table-column
                prop="errorMessage"
                label="失败原因"
                width="220"
                >
              </el-table-column>

            </el-table>
          </div>
      </el-tab-pane>
    </el-tabs>
    </div>
   </d2-module>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import echarts from 'echarts'
export default {
  name: 'onSiteInspection',
  props: {
    actived: {
      type: Boolean,
      default: false

    }
  },
  data () {
    return {
      period: [new Date(), new Date()],
      successList: [],
      failureList: [],
      hasAddEventListener: false,
      listTabActiveName: 'success'
    }
  },
  mounted () {
    this.initPeriod()
    // this.getData();
  },
  methods: {
    initPeriod () {
      let period = this.getTimeStamps()
      this.period = [period.startTime, period.endTime]
    },
    getData () {
      let [startTime, endTime] = this.period
      service({
        url: `/onSiteInspection/getOnSiteInspection?startTime=${startTime}&endTime=${endTime}`,
        method: 'GET'
      })
        .then(res => {
          this.data2Show(res)
        })
    },
    data2Show (res) {
      this.failureList = this.fixData(res.fail)
      this.successList = this.fixData(res.success)
      let graphData = this.generateEchartsData()
      let echartsOptions = this.generateOptions(graphData)
      this.$nextTick(() => {
        setTimeout(() => {
          this.showGraph(echartsOptions)
        }, 500)
      })
    },
    showGraph (options) {
      let graph = echarts.init(this.$refs['canvas'])
      //  graph.clear();
      //  console.log(options);
      graph.setOption(options)
      if (!this.hasAddEventListener) {
        this.hasAddEventListener = true
        graph.on('click', params => {
          if (params.name === '部署成功') {
            this.listTabActiveName = 'success'
          } else if (params.name === '部署失败') {
            this.listTabActiveName = 'failure'
          }
        })
      }
    },
    fixData (list) {
      return list.map(item => {
        let showItem = {}
        showItem.pipelineId = item.pipelineId
        showItem.deployTime = bizutil.timeFormat(item.deployTime)
        showItem.projectName = item.project && item.project.name
        showItem.env = item.projectEnv && item.projectEnv.name
        showItem.deployUser = item.deployUser
        showItem.deploySucceed = item.deploySucceed
        if (item.errorMessage) {
          showItem.errorMessage = item.errorMessage.message
        }
        return showItem
      })
    },
    getTimeStamps () {
      let endTime = +new Date()
      let yesterdayZeroClick = +new Date().setHours(0, 0, 0, 0) - 3600 * 1000 * 24
      return { endTime, startTime: yesterdayZeroClick }
    },
    generateEchartsData () {
      return [
        {
          name: '部署成功',
          value: this.successList.length
        },
        {
          name: '部署失败',
          value: this.failureList.length
        }
      ]
    },
    generateOptions (data) {
      let option = {
        title: {
          text: '部署成功比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          formatter: (name) => {
            let kv = data.filter(item => item.name === name)
            return `${kv[0].name}  ${kv[0].value}`
          }
          // data: ['部署成功', '部署失败']
        },
        color: ['#409EFF', '#F56C6C'],
        series: [
          {
            name: '',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: data,
            emphasis: {
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }

            }
          }
        ]
      }
      return option
    }
  },
  watch: {
    actived: function (newVal, oldVal) {
      if (newVal) {
        this.getData()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.onsite-inspection{
  display: flex;
  flex-direction:column;
}
.period-cls{
  margin-bottom: 10px;
}
.graph{
  min-height: 250px;
}
.section-header{
  text-align: left;
}

.table-box{
  max-height:350px;
  overflow:scroll;
}
</style>
<style lang="scss">
.inspection-modules{
  flex:1;
  display: flex;
  flex-direction:column;

}
</style>
