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
        <el-button
          size="mini"
          :disabled="refreshDisabled"
          @click="refresh">刷新</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table stripe :data="tableData" class="table-list">
        <el-table-column fixed prop="id" label="ID" width="100"></el-table-column>
        <el-table-column prop="name" label="名称" width="250"></el-table-column>
        <el-table-column prop="description" label="描述" width="300"></el-table-column>
        <el-table-column prop="baseUrl" label="Api前缀" style="minWidth:260px"></el-table-column>
        <el-table-column v-if="info.role === 1" label="操作" width="110" fixed="right">
          <template slot-scope="scope">
            <el-button @click="editRow(scope.row)" type="text" size="small">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pageNo'
        :pageSize='pageSize'
        :total='total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
    </d2-module>

    <!--编辑-->
    <el-dialog :title="editFormTitle" :visible.sync="editFormVisible" width="800px">
      <el-form :model="formEdit" ref="formEdit" :rules="rules" label-width="110px" size="mini">
        <el-form-item label="Api前缀" prop="baseUrl">
          <el-input value="/mtop/" placeholder disabled style="width:15%"></el-input>
          <el-input v-model="formEdit.subBaseUrl" autocomplete="off" style="width:25%"></el-input>
          <el-input value="/" placeholder disabled style="width: 7%"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formEdit.name" autocomplete="off" disabled style="width:80%"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="formEdit.description" disabled style="width:80%"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleRowEditCancel" size="mini">取 消</el-button>
        <el-button type="primary" @click="handleRowEdit" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"
import bizutil from "@/common/bizutil"
import "codemirror/mode/javascript/javascript.js"
import "codemirror/theme/base16-dark.css"
import { mapState } from "vuex"

export default {
  name: "apiGroup",
  data () {
    return {
      tableData: [],
      total: 0,
      pageNo: 1,
      pageSize: 10,
      curUser: "", // 是否当前用户的task
      running: 0, // 是否筛选running
      warningClosable: false,
      pageDisabled: false,
      editFormVisible: false,
      editFormTitle: "",
      refreshDisabled: false,
      formAdd: {},
      formEdit: {},
      rules: {
        name: [
          { required: true, message: "请输入分组名称", trigger: "blur" }
        ],
        description: [
          { required: true, message: "请选输入分组描述", trigger: "blur" }
        ]
      }
    }
  },
  computed: {
    ...mapState("d2admin/user", ["info"]),
    baseUrlCom () {
      return '/mtop/' + this.formEdit.subBaseUrl + '/'
    }
  },
  mounted: function () {
    this.getList()
  },
  watch: {
    editFormVisible: function (val) {
      if (val) {
        return
      }
      this.formEdit = {}
      this.$refs["formEdit"].clearValidate()
    },
    $route: "getList",
    baseUrlCom: function () {
      this.formEdit.baseUrl = this.baseUrlCom
    }
  },
  methods: {
    getList () {
      this.pageDisabled = true
      service({
        url: "/apigroup/list",
        method: "post",
        data: {
          pageNo: this.pageNo,
          pageSize: this.pageSize
        }
      }).then(res => {
        if (!Array.isArray(res.groupList)) return
        this.total = res.total
        this.tableData = bizutil.apiGroupListProcess(res.groupList)
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    handleCurrentChange (val) {
      this.pageNo = val
      this.getList()
    },
    // 刷新
    refresh () {
      this.refreshDisabled = true
      setTimeout(() => {
        this.refreshDisabled = false
      }, 2000)
      this.getList()
    },
    // 编辑
    editRow (row) {
      let rowTemp = { ...row }
      this.formEdit = {
        id: rowTemp.id,
        name: rowTemp.name,
        description: rowTemp.description,
        subBaseUrl: rowTemp.baseUrl ? rowTemp.baseUrl.slice(6, rowTemp.baseUrl.length - 1) : '',
        baseUrl: rowTemp.baseUrl
      }
      this.editFormTitle = "编辑API Group " + rowTemp.id
      this.editFormVisible = true
    },
    handleRowEdit () {
      this.$refs["formEdit"].validate(valid => {
        if (!valid) {
          this.$message({
            message: "请检查参数",
            type: "warning"
          })
          return false
        }
        this.editFormVisible = false
        this.formEdit.updatorId = this.info.uuid
        service({
          url: "/apigroup/update",
          method: "post",
          data: this.formEdit
        }).then(res => {
          this.formEdit = {}
          this.$message({
            message: "编辑成功",
            type: "success"
          })
          this.getList()
        })
          .catch(() => {
            this.formEdit = {}
          })
          .finally(() => {
            this.$refs["formEdit"].clearValidate()
          })
      })
    },
    handleRowEditCancel () {
      this.editFormVisible = false
      this.$message({
        message: "取消编辑",
        type: "warning"
      })
    },

    // 删除
    delRow (row) {
      this.$confirm("确认删除?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          service({
            url: "/apigroup/del",
            method: "post",
            data: {
              ids: [row.id]
            }
          }).then(res => {
            this.formEdit = {}
            this.$message({
              message: "删除成功",
              type: "success"
            })
            this.getList()
          })
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          })
        })
    },
    // 批量操作
    handleBatch (cmd) {
      switch (cmd) {
        case "1":
          this.handleBatchDel()
          break
        default:
          this.$message({
            message: "无效的批量操作",
            type: "warning"
          })
      }
    },
    // 批量停止服务
    handleBatchDel () {
      let ids = bizutil.getBatchIdsForOpt(this.batchOptRows)
      if (ids.length <= 0) {
        this.$message({
          message: "请首先选择要操作的记录",
          type: "warning"
        })
        return
      }

      service({
        url: "/apigroup/del",
        method: "post",
        data: {
          ids: ids,
          uid: this.info.uuid
        }
      }).then(res => {
        this.$message({
          message: "API Group删除成功",
          type: "success"
        })
        this.getList()
        setTimeout(() => {
          this.getList()
        }, 1000)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
</style>
