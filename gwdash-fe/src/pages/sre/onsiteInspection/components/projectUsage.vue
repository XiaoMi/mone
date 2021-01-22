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

    <div class="table-con cpu-useage">
        <el-table
        :data="list"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        >
        <el-table-column
          prop="projectId"
          label="projectId"
          width="100"
          align="center">
        </el-table-column>
        <el-table-column
          prop="showProjectName"
          align="left"
          header-align="center"
          label="项目名"
          width="180">
        </el-table-column>
        <!-- <el-table-column
          prop="envId"
          label="envId"
          width="180"
          align="center">
        </el-table-column> -->
          <el-table-column
          prop="envName"
          label="环境名"
          width="100"
          align="center">
        </el-table-column>
          <el-table-column
          prop="owner"
          label="负责人"
          width="100"
          align="center">
        </el-table-column>
         <el-table-column
          prop="dockerCount"
          label="副本数"
          width="100"
          align="center">
        </el-table-column>
         <el-table-column
          prop="cpuNum"
          label="cpu核"
          width="100"
          align="center">
        </el-table-column>
          <el-table-column
          label="cpu使用率"
          width="100"
          align="center">
           <template slot-scope="scope">
                {{Number2Percent(scope.row.showCpuUsage)}}
            </template>
        </el-table-column>
           <el-table-column
          prop="memory"
          label="memory使用率"
          width="180"
          align="center">
           <template slot-scope="scope">
                {{Number2Percent(scope.row.memoryUsage)}}
            </template>
        </el-table-column>

      </el-table>
    </div>
     <d2-pagination
        marginTop
        :currentPage='pager.page'
        :pageSize='pager.pageSize'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
const REQUEST_TIMES_MAX = 10
const QUENE_MAX = 3
const ALERT_MIN = 0.2
const ALERT_MAX = 0.8
export default {
  name: 'projectUsage',
  data () {
    return {
      requestTimes: 0,
      list: [],
      cpuMap: {},
      pager: {
        page: 1,
        pageSize: 10,
        total: 0,
        pageDisabled: false
      }
    }
  },
  props: {
    actived: {
      type: Boolean,
      default: false
    }
  },
  created () {
    // this.getList();
  },
  methods: {
    getList () {
      service({
        url: `/onSiteInspection/getEvnList?page=${this.pager.page}&pageSize=${this.pager.pageSize}`
      })
        .then(res => {
          this.pager.total = res.total
          let list = this.fixList(res.list)
          let envIds = res.list.map(item => item.envId).slice(0, 5)
          this.list = list
          // 重置次数
          this.requestTimes = 0
          // this.getCpuUsageByQuene(QUENE_MAX)
        })
    },
    getCpuUsage (ids) {
      //  console.log("获取以下id",ids);
      if (ids.length === 0) return
      if (this.requestTimes > REQUEST_TIMES_MAX) {
        console.log('已达到最大请求次数，不再查询')
        return
      }
      ++this.requestTimes
      service({
        url: `/onSiteInspection/getEvnUsage`,
        method: 'POST',
        data: ids
      })
        .then(res => {
          if (res.length > 0) {
            res.forEach(item => {
              this.coverCpuUsageByEnvId(item)
            })
            this.getCpuUsageByQuene(QUENE_MAX)
          }
        })
    },
    coverCpuUsageByEnvId (useAge) {
      let defaultInfo = ['0', '0']
      if (useAge.dockerInfo && useAge.dockerInfo.status.length === 2) {
        defaultInfo = useAge.dockerInfo.status
      }
      this.list.forEach(tableItem => {
        if (tableItem.envId === useAge.envId) {
          tableItem.cpu = this.getNumFromPercent(defaultInfo[0])
          tableItem.memory = this.getNumFromPercent(defaultInfo[1])
        }
      })
    },
    getNumFromPercent (num) {
      if (typeof num === 'string') {
        if (num.indexOf('%') > -1) {
          num = parseFloat(num.replace('%', '')) * 100
          return num
        } else {
          return +num
        }
      } else {
        return num
      }
    },
    getCpuUsageByQuene (size) {
      let cpuUsageEmpty = this.list.filter(item => item.cpu === '').map(it => it.envId)
      if (cpuUsageEmpty.length == 0) {
        console.log('cpu end')
        return
      }
      let callIds = cpuUsageEmpty.slice(0, size)
      this.getCpuUsage(callIds)
    },
    fixList (list) {
      return list.map(item => {
        item.showProjectName = item.projectName || ''
        let cpuNum = 1
        try {
          cpuNum = parseInt(item.cpuNum, 10)
          cpuNum = Math.max(1, cpuNum)
        } catch (error) {

        }
        item.showDockerCount = parseInt(item.dockerCount, 10)
        item.showCpuUsage = (item.cpuUsage).toFixed(4)
        if (item.owner && item.owner.length > 1) {
          item.owner = item.owner[0]
        }
        return item
      })
    },
    tableRowClassName ({ row, rowIndex }) {
      // (row.showCpuUsage/Math.max(1,row.cpuNum) 单核超过max才警告
      if ((row.showCpuUsage / Math.max(1, row.cpuNum)) > ALERT_MAX || row.memoryUsage > ALERT_MAX) {
        return 'warning-row'
      } else if ((row.showCpuUsage / Math.max(1, row.cpuNum)) <= ALERT_MIN || row.memoryUsage <= ALERT_MIN) {
        return 'success-row'
      }
      return ''
    },
    Number2Percent (num) {
      if (num === '') return ''
      if (isNaN(num)) return '0%'
      return (num * 100).toFixed(2) + '%'
    },
    handleCurrentChange (val) {
      this.pager.page = val
      this.getList()
    }

  },
  watch: {
    actived: function (newVal, oldVal) {
      if (newVal) {
        this.getList()
      }
    }
  }
}
</script>

<style lang="scss" >
.cpu-useage {
    .el-table .warning-row {
    background: #F56C6C;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }

}

</style>
