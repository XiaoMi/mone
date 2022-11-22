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
        <div class='title'>压测CaseName：</div>
        <el-input
           size='mini'
           @keypress.native.enter='handleInput'
           placeholder='请输入'
           v-model='searchWorld'
           class='input_margin'/>
        <el-button
           size='mini'
           @click="handleInput">查询</el-button>
        <el-button
           size='mini'
           @click="addChainCase">新增链路Case</el-button>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column label="id" prop="id" width="80"></el-table-column>
        <el-table-column label="caseName" prop="chainAliasName" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column label="uuid" prop="uuid" width="220" show-overflow-tooltip></el-table-column>
        <el-table-column label="创建人" prop="creator" width="160"></el-table-column>
        <el-table-column label="更新人" prop="updater" width="160"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="160"></el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="editChainCase(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="showChainCase(scope.row)">源Case</el-button>
            <el-button type="text" size="mini" @click="executeChainCase(scope.row)">执行</el-button>
            <!-- <el-button type='text' size='mini' @click="resultChainCase(scope.row)">执行结果</el-button> -->
            <el-button type="danger" size="mini" class='danger' @click="deleteChainCase(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='page'
        :pageSize='pageSize'
        :total='total'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
    </d2-module>

    <flow-chart
      :show='show'
      :tag='chainCaseTag'
      :contentData='contentData'
      @doCloseDialog='flowCloseDialog'>
    </flow-chart>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios"
import bizutil from '@/common/bizutil'
import qs from 'qs'
import flowChart from './components/flow-chart'

export default {
  data () {
    return {
      searchWorld: '',
      show: false,
      chainCaseTag: '',
      tableData: [],
      page: 1,
      pageSize: 10,
      total: 0,
      contentData: {}
    }
  },
  components: {
    flowChart
  },
  created () {
    this.getChainCaseList()
  },
  methods: {
    getChainCaseList () {
      service({
        url: '/chain/page',
        method: 'POST',
        data: qs.stringify({
          page: this.page,
          pageSize: this.pageSize,
          chainAliasName: this.searchWorld
        })
      }).then(res => {
        this.total = res.count
        this.page = res.page
        this.pageSize = res.pageSize
        this.tableData = res.list.map(item => {
          return {
            ...item,
            createTime: bizutil.timeFormat(item.ctime)
          }
        })
      })
    },
    handleCurrentChange (val) {
      this.page = val
      this.getChainCaseList()
    },
    handleInput () {
      this.page = 1
      this.pageSize = 10
      this.getChainCaseList()
    },
    addChainCase () {
      this.chainCaseTag = 'add'
      this.show = true
    },
    editChainCase (row) {
      this.chainCaseTag = 'edit'
      this.contentData = {
        chainAliasName: row.chainAliasName,
        backParam: row.backParam,
        frontParam: row.frontParam,
        uuid: row.uuid
      }
      this.show = true
    },
    showChainCase (row) {
      this.chainCaseTag = 'show'
      this.contentData = {
        chainAliasName: row.chainAliasName,
        backParam: row.backParam,
        frontParam: row.frontParam
      }
      this.show = true
    },
    executeChainCase (row) {
      const msgTips = this.$message.warning('Case正在执行中... 请稍等...')
      service({
        url: '/chain/execute',
        method: 'POST',
        data: qs.stringify({
          uuid: row.uuid
        })
      }).then(res => {
        if (!Array.isArray(res)) return
        const data = res && res[0]
        const resultArr = res.map(item => ({
          result: item.result,
          msg: item.message
        }))
        this.contentData = {
          chainAliasName: data && data.chainAliasName,
          backParam: data && data.backParam,
          frontParam: data && data.frontParam,
          resultArr
        }
        msgTips.close()
        this.chainCaseTag = 'result'
        this.show = true
      }).catch(err => {
        console.log(err)
        msgTips.close()
        this.$message({
          type: 'error',
          showClose: true,
          duration: 0,
          message: 'Case执行失败'
        })
      })
    },
    deleteChainCase (row) {
      this.$confirm("此操作将永久删除此Case, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: '/chain/delete',
          method: 'POST',
          data: qs.stringify({
            uuid: row.uuid
          })
        }).then(res => {
          if (res === null) {
            this.$message.success('删除成功')
            this.getChainCaseList()
          }
        })
      }).catch(() => {
        this.$message({
          message: "已取消删除",
          type: "warning"
        })
      })
    },
    flowCloseDialog (param, tag) {
      if (tag === 'add' || tag === 'edit') {
        this.getChainCaseList()
      }
      this.show = param
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  .title {
    font-size: 13px;
    color: #333
  }
  .input_margin {
    width: 20%;
    margin-right: 10px
  }
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
</style>
