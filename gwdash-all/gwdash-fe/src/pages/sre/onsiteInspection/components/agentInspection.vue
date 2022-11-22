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
  <div>
    <div class="fb">
       <div class="graph-1 fi" ref="graph-1">
    </div>
    <div class="graph-2 fi" ref="graph-2">
    </div>
    </div>

    <div class="tab-table">
         <el-tabs type="border-card" v-model="listTabActiveName" class="inspection">
         <el-tab-pane label="异常agent" name="errorAgent"  class="pane">
            <el-table
              :data="errorAgent"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="port"
                label="port"
                >
              </el-table-column>
              <el-table-column
                prop="displayMetaData"
                label="meta"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>
       <el-tab-pane label="agent 总览" name="agent"  class="pane">
            <el-table
              :data="agent"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="port"
                label="port"
                >
              </el-table-column>
             <el-table-column
                prop="displayMetaData"
                label="meta"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>

      <el-tab-pane label="不活跃agent" name="errorMachine" >
         <el-table
              :data="errorMachine"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
                <el-table-column
                prop="id"
                label="id"
                >
              </el-table-column>
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="hostname"
                label="hostname"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>
       <el-tab-pane label="活跃agent" name="aliveMachine" >
         <el-table
              :data="aliveMachine"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
                <el-table-column
                prop="id"
                label="id"
                >
              </el-table-column>
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="hostname"
                label="hostname"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>
      <el-tab-pane label="docker机" name="dockerMachine" >
         <el-table
              :data="dockerMachine"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
                <el-table-column
                prop="id"
                label="id"
                >
              </el-table-column>
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="hostname"
                label="hostname"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>
       <el-tab-pane label="物理机" name="physicalMachine" >
         <el-table
              :data="physicalMachine"
              size="mini"
              height="300"
              show-overflow-tooltip
              style="width: 100%">
                <el-table-column
                prop="id"
                label="id"
                >
              </el-table-column>
              <el-table-column
                prop="ip"
                label="ip"
                >
              </el-table-column>
                <el-table-column
                prop="hostname"
                label="hostname"
                >
              </el-table-column>

            </el-table>
      </el-tab-pane>
    </el-tabs>
    </div>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import bizutil from "@/common/bizutil"
import echarts from 'echarts'

export default {
  name: "agentInspection",
  props: {
    actived: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      agent: [],
      aliveMachine: [],
      errorMachine: [],
      dockerMachine: [],
      physicalMachine: [],
      errorAgent: [],
      listTabActiveName: "errorAgent",
      hasAddEventListener: false
    }
  },
  mounted () {
    // this.getData();
  },
  methods: {
    getData () {
      service({
        url: "/onSiteInspection/getAgentInfo"
      })
        .then(res => {
          this.agent = this.fixAgent(res.agent)
          this.aliveMachine = res.aliveMachine
          this.errorMachine = res.deadMachine
          this.dockerMachine = res.dockerMachine
          this.physicalMachine = res.physicalMachine
          this.errorAgent = this.getErrorAgent()
          this.drawGraph()
        })
    },
    fixAgent (agentList) {
      if (serverEnv === "local" || serverEnv === "staging") {
        agentList = agentList
          .filter(it => {
            return it.metadata && it.metadata.version.indexOf("tesla_server_staging") > 0
          })
          .map(it => {
            it.displayMetaData = JSON.stringify(it.metadata)
            return it
          })
      }
      return agentList
    },
    getErrorAgent () {
      if (this.agent.length <= this.aliveMachine.length) return []
      let ips = this.aliveMachine.map(it => it.ip)
      return this.agent
        .filter(it => {
          return !(ips.indexOf(it.ip) >= 0)
        })
    },
    drawGraph () {
      let leftGraphData = [
        {
          value: this.agent.length,
          name: "nacos记录的agent"
        },
        {
          value: this.aliveMachine.length,
          name: "活跃agent"
        },
        {
          value: this.errorMachine.length,
          name: "不活跃agent"
        }
      ]
      let rightGrapgData = [
        {
          value: this.dockerMachine.length,
          name: "docker机"
        },
        {
          value: this.physicalMachine.length,
          name: "物理机"
        }
      ]
      let graph1 = echarts.init(this.$refs['graph-1'])
      let graph2 = echarts.init(this.$refs['graph-2'])

      let option1 = this.getOptions(leftGraphData)
      let option2 = this.getOptions(rightGrapgData)
      graph1.setOption(option1)
      graph2.setOption(option2)
      if (!this.hasAddEventListener) {
        this.hasAddEventListener = true

        graph1.on("click", params => {
          this.listTabActiveName = this.getKeyMap(params.name).value
        })

        graph2.on("click", params => {
          this.listTabActiveName = this.getKeyMap(params.name).value
        })
      }
    },
    getKeyMap (key) {
      let list = [
        {
          key: "不活跃agent",
          value: "errorMachine"
        },
        {
          key: "活跃agent",
          value: "aliveMachine"
        },
        {
          key: "nacos记录的agent",
          value: "agent"
        },
        {
          key: "docker机",
          value: "dockerMachine"
        },
        {
          key: "物理机",
          value: "physicalMachine"
        }
      ]
      return list.filter(item => item.key === key)[0]
    },
    getOptions (data) {
      let names = data.map(it => it.name)
      let option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          formatter: (name) => {
            let kv = data.filter(item => item.name === name)
            return `${kv[0].name}  ${kv[0].value}`
          }
        },
        color: ['#409EFF', '#00bfff', "#F56C6C"],
        series: [
          {
            name: '',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }
      return option
    }
  },
  watch: {
    actived: function (newVal, oldVal) {
      newVal && this.getData()
    }
  }
}
</script>

<style lang="scss" scoped>
.fb{
  display: flex;
  min-width: 300px;

}
.fi{
  flex: 1;
  height: 300px;
}

</style>
