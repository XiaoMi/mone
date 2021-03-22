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
      <div class="header-bar theme-separate">
          <div class="header-bar-left header">
              <el-dropdown type="primary" size="mini" plain @command="handleBatch">
                  <el-button size="mini">批量操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                  <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="1">批量删除</el-dropdown-item>
                  </el-dropdown-menu>
              </el-dropdown>
          </div>
      </div>
   </d2-module>
   <d2-module>
      <el-table stripe class='table-list' :data="list" @selection-change="handleSelectionChange">
        <el-table-column type="selection"></el-table-column>
        <el-table-column fixed  prop="id" label="id" width="100"></el-table-column>
        <el-table-column prop="serviceName" label="服务名称"></el-table-column>
        <el-table-column prop="ip" width="120" label="ip地址"></el-table-column>
        <el-table-column prop="group" label="group"></el-table-column>
        <el-table-column prop="type" label="type"></el-table-column>
        <el-table-column prop="status" width="100" label="status"></el-table-column>
        <el-table-column prop="count" label="count"></el-table-column>
        <el-table-column prop="version" label="version"></el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template slot-scope="scope">
            <el-button class="el-button--blue" @click="handleClick(scope.row)" type="primary" size="mini">查看</el-button>
            <el-button class="el-button--blue" @click="handleDel(scope.row)" type="primary" size="mini">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pager.page'
        :pageSize='pager.pageSize'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
   </d2-module>

   <el-dialog title="详情" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
      <codemirror v-model="dialogData" :options="common_cmOptions"></codemirror>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false" size="mini">确 定</el-button>
      </span>
   </el-dialog>
 </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"
import "codemirror/mode/javascript/javascript.js"
import "codemirror/theme/base16-dark.css"

export default {
  name: "mErrorList",
  data () {
    return {
      pager: {
        page: 1,
        pageSize: 10,
        total: 1
      },
      list: [],
      dialogData: "",
      dialogVisible: false,
      multipleSelection: [],
      pageDisabled: false,
      common_cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        mode: "text/javascript",
        theme: "base16-dark",
        lineNumbers: true,
        line: true,
        smartIndent: true
      }
    }
  },
  created () {
    this.getList()
  },
  methods: {
    handleCurrentChange (val) {
      this.pager.page = val
      this.getList()
    },
    getList () {
      this.pageDisabled = true
      let { page, pageSize } = this.pager
      service({
        url: `/mError/list?page=${page}&pageSize=${pageSize}`
      }).then(res => {
        this.list = this.fix(res.list)
        this.pager.total = res.total
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    handleBatch (command) {
      switch (command) {
        case "1":this.handleDel(this.multipleSelection)
      }
    },
    fixStatus (status) {
      let map = {
        0: "初始化",
        1: "系统处理成功 ",
        2: "系统处理失败"
      }
      return map[status]
    },
    fixType (type) {
      let map = {
        0: "重启"
      }
      return map[type] || type
    },
    fix (data) {
      let ret = data.map(item => {
        let showItem = Object.assign({}, item)
        showItem.status = this.fixStatus(item.status)
        showItem.type = this.fixType(item.type)
        return showItem
      })

      return ret
    },
    handleClick (data) {
      this.dialogData = JSON.stringify(data.content, null, 4)
      this.dialogVisible = true
    },
    submitDel (id) {
      return service({
        url: "/mError/del",
        method: "POST",
        data: { id }
      })
    },
    handleDel (data) {
      let id = []
      if (data.constructor === Array) {
        id = data.map(it => it.id)
      } else {
        id = [data.id]
      }
      if (id.length === 0) {
        this.$message({
          type: "warning",
          message: "请先勾选需要删除的记录"
        })
        return
      }
      this.$confirm(`此操作将永久删除[${id.join()}]记录, 是否继续?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(async () => {
          let success = await this.submitDel(id)
          if (success) {
            this.getList()
            this.$message({
              type: "success",
              message: "删除成功!"
            })
          } else {
            this.$message({
              type: "error",
              message: "删除失败!"
            })
          }
        })
        .catch(() => {})
    },
    handleClose () {
      this.dialogData = ""
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.page-con {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
.table-container {
  margin-top: 10px;
}
</style>
