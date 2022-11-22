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
    <d2-module margin-bottom>
      <div class="header">
        <el-input
           style='width:20%; margin-right:10px'
           size='mini'
           v-model='searchWorld'
           @input='handleInput'
           placeholder='支持负责人查询'/>
        <el-button
            size='mini'
            :disabled='disabledRefresh'
            @click="handleRefresh">刷新</el-button>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data="newTableData" class="table-list">
        <el-table-column label="服务名" prop="serviceName" width="300" fixed="left">
          <template slot-scope="scope">
            <div style="text-align:left">{{scope.row.serviceName}}</div>
          </template>
        </el-table-column>
        <el-table-column label="总运行数" prop="total" width=""></el-table-column>
        <el-table-column label="失败数" prop="failure" width=""></el-table-column>
        <el-table-column label="失败百分比" prop="failPercentage" width=""></el-table-column>
        <el-table-column label="可用性" prop="availability" width=""></el-table-column>
        <el-table-column label="最快运行时长/ms" prop="min" width=""></el-table-column>
        <el-table-column label="最慢运行时长/ms" prop="max" width=""></el-table-column>
        <el-table-column label="平均运行时长/ms" prop="avg" width=""></el-table-column>
        <el-table-column label="95置信区间/ms" prop="line95" width=""></el-table-column>
        <el-table-column label="标准差/ms" prop="std" width=""></el-table-column>
        <el-table-column label="qps" prop="qps" width=""></el-table-column>
        <el-table-column label="占比" prop="percentage" width=""></el-table-column>
        <el-table-column label="状态" prop="ishealth" width="90"  fixed="right">
          <template slot-scope="scope">
            <el-tag
              size='mini'
              style="width:60px"
              :type="`${scope.row.ishealth ? 'success' : 'danger'}`">{{scope.row.ishealth ? '健康' : '不健康'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="负责人" prop="owner" width=""  fixed="right"></el-table-column>
        <el-table-column label="电话" width="100" prop="ownerPhone"  fixed="right"></el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        tuneUp
        :currentPage='currentPage'
        :pageSize='pageSize'
        :total='total'
        :pageDisabled='pageDisabled'
        @doSizeChange='handleSizeChange'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
    </d2-module>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
let initData = []
export default {
  name: "cat",
  data () {
    return {
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      searchWorld: '',
      disabledRefresh: false,
      pageDisabled: false
    }
  },
  computed: {
    newTableData () {
      const currentPage = this.currentPage
      const pageSize = this.pageSize
      return this.tableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)
    },
    total () {
      return this.tableData.length
    }
  },
  created () {
    this.getCatList()
  },
  methods: {
    getCatList () {
      service({
        url: '/cat/list',
        method: 'GET'
      }).then(res => {
        let catList = JSON.parse(res.content).data
        let shiftArr = []
        let newCatList = []
        shiftArr.push(catList.shift())
        catList.sort((a, b) => b.failPercentage - a.failPercentage)
        newCatList = shiftArr.concat(catList)
        this.tableData = initData = newCatList.map(item => {
          return {
            ...item,
            availability: bizutil.toPercent(item.availability),
            percentage: bizutil.toPercent(item.percentage),
            failPercentage: bizutil.toPercentage(item.failPercentage),
            avg: bizutil.toRetain(item.avg),
            line95: bizutil.toRetain(item.line95),
            std: bizutil.toRetain(item.std),
            qps: item.qps.toFixed(2),
            ishealth: item.failPercentage < 0.01
          }
        })
        this.disabledRefresh = false
      }).catch(e => {
        this.$message.error('获取cat信息列表错误')
      })
    },
    handleSizeChange (val) {
      this.pageSize = val
    },
    handleCurrentChange (val) {
      this.currentPage = val
    },
    handleInput (val) {
      this.currentPage = 1
      if (val === '') {
        this.tableData = initData
        return
      }
      var data = initData.slice()
      this.tableData = data.filter(item => {
        if (item.owner !== undefined) {
          return item.owner.indexOf(this.searchWorld) !== -1
        }
      })
    },
    handleRefresh () {
      this.disabledRefresh = true
      this.getCatList()
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.table-list-con{
  height: 100%;
}
</style>
