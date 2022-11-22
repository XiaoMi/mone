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
 <d2-container id="wrapper">
   <project-header
      :firstFloor='firstFloor'
      :secondFloor='secondFloor'
   />
   <d2-module>
      <el-table stripe class='table-list' id="table_id" :data="list" @selection-change="handleSelectionChange">
        <el-table-column prop="id" width="120" label="id"></el-table-column>
        <el-table-column prop="author" label="作者" width="200"></el-table-column>
        <el-table-column prop="log" label="日志">
          <template slot-scope="scope">{{scope.row.log }}</template>
        </el-table-column>
        <el-table-column prop="ctime" width="250" label="操作时间"></el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pager.offset / this.pager.limit + 1'
        :pageSize='pager.count'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
   </d2-module>
 </d2-container>
</template>

<script>
import projectHeader from './../../layout/header'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'

export default {
  name: 'operationRecord',
  data () {
    return {
      firstFloor: '降级系统',
      secondFloor: '操作日志',
      pager: {
        offset: 0,
        limit: 10,
        total: 4
      }, // 分页项
      list: [], // 列表
      multipleSelection: [],
      pageDisabled: false // 分页控制
    }
  },
  components: {
    projectHeader
  },
  activated () {
    this.getList()
  },
  methods: {
    handleCurrentChange (val) { // 分页变化
      this.pager.offset = (val - 1) * this.pager.limit
      this.getList()
    },
    getList (params, flag) { // 初始化表格
      this.pageDisabled = true
      let { offset, limit } = this.pager
      let url = `/scepter/log/list?limit=${limit}&offset=${offset}`
      service({
        url: url,
        method: 'GET'
      }).then(res => {
        this.list = res.list.map(it => {
          it.ctime = bizutil.timeFormat(it.ctime)
          return it
        })
        this.pager.total = res.total
        this.pageDisabled = false
      })
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/ .el-table--striped .el-table__body tr.el-table__row--striped td{
  background-color:  #F2F8FC;
}

/deep/ .el-table--striped .el-table__body tr.el-table__row--striped {
  &:hover {
    > td {
      background: #E3F1F9;
    }
    .is-hidden{
              background: #E3F1F9;

    }
  }
}
</style>
