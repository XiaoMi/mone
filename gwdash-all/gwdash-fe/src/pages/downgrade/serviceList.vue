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
  <d2-module margin-bottom>
    <div class="functionWrapper">
      <el-button @click="handleAddOrEdit()" type="primary" size="mini">新增策略</el-button>
    </div>
  </d2-module>
  <d2-module>
    <el-table
      ref="multipleTable"
      :data="list"
      tooltip-effect="dark"
      stripe
      class='table-list'
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column
        prop="name"
        label="策略名"
        width="130">
      </el-table-column>
      <el-table-column
        prop="description"
        label="描述"
        >
      </el-table-column>
      <el-table-column
        prop="ctime"
        label="创建时间"
        width="150"
        show-overflow-tooltip>
      </el-table-column>
        <el-table-column
        prop="utime"
        label="降级时间"
        width="150"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="isDowngrade"
        label="降级状态"
        width="70"
        show-overflow-tooltip>
        <template slot-scope="scope">
           <el-tag
              v-if='scope.row.status===0 '
              size='mini'
              style="width:60px"
              type="info">未降级</el-tag>
           <el-tag
              v-else
              size='mini'
              style="width:60px"
              type="success">已降级</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="操作" width="280">
        <template slot-scope="scope">
          <el-button size="mini" class="el-button--blue" @click="handleAddOrEdit(scope.row)">编辑</el-button>
          <el-button size="mini" class="el-button--orange" @click="handleDel(scope.row)">删除</el-button>
          <el-button size="mini" class="el-button--blue" @click="handleJump(scope.row)">配置详情</el-button>
          <el-button
            v-if='scope.row.status===0'
            @click="handleDownGrade(scope.row)"
            size="mini"
            class="el-button--green"
            type="primary"
          >降级</el-button>
          <el-button
            v-else
            size="mini"
            @click="handleReset(scope.row)"
            class="el-button--blue"
            type="primary"
          >恢复</el-button>
        </template>
      </el-table-column>
    </el-table>
    <d2-pagination
      marginTop
      :currentPage='pager.offset/this.pager.limit+1'
      :pageSize='pager.limit'
      :total='pager.total'
      :pageDisabled='pageDisabled'
      @doCurrentChange='handleCurrentChange'>
    </d2-pagination>
  </d2-module>

  <el-dialog :title=dialogTitle :visible.sync="addStrategyDialogVisible" width="800px">
    <el-form :model="formAdd" label-width="110px" ref="uploadForm" :rules="rules" size="mini">
      <el-form-item prop="name" label="策略名">
            <el-input v-model="formAdd.name" placeholder="请输入策略名"></el-input>
          </el-form-item>
      <el-form-item prop="description" label="描述">
            <el-input v-model="formAdd.description"  type="textarea"   :rows="5" placeholder="请添加描述"></el-input>
          </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="mini" @click="addStrategyDialogVisible = false">取 消</el-button>
      <el-button type="primary" size="mini" @click="submit()">确 定</el-button>
    </span>
  </el-dialog>
 </d2-container>
</template>

<script>
import projectHeader from './../../layout/header'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'

export default {
  name: 'serviceList',
  data () {
    return {
      firstFloor: '降级系统',
      secondFloor: '策略列表',
      pager: {
        offset: 0,
        limit: 10,
        total: 1
      }, // 分页项
      list: [], // 列表
      pageDisabled: false, // 分页控制
      multipleSelection: [],
      addStrategyDialogVisible: false,
      formAdd: {},
      dialogTitle: '新增',
      rules: {
        name: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ]
      }
    }
  },
  components: {
    projectHeader
  },
  activated () {
    this.getList()
  },
  methods: {
    getList () { // 初始化表格
      this.pageDisabled = true
      let { offset, limit } = this.pager
      let url = `/scepter/group/list?limit=${limit}&offset=${offset}`
      service({
        url: url,
        method: 'GET'
      }).then(res => {
        this.list = res.list.map(it => {
          it.ctime = bizutil.timeFormat(it.ctime)
          it.utime = bizutil.timeFormat(it.utime)
          return it
        })
        this.pager.total = res.total
        this.pageDisabled = false
      })
    },
    handleJump (it) {
      this.$router.push(`configure/${it.id}/${it.name}`)
    },
    handleCurrentChange (val) { // 分页变化
      this.pager.offset = (val - 1) * this.pager.limit
      this.getList()
    },
    handleAddOrEdit (it) {
      let rowTemp = { ...it }
      if (it === undefined) {
        this.formAdd = {}
        this.dialogTitle = '新增'
      } else {
        this.formAdd = rowTemp
        this.dialogTitle = '编辑'
      }
      this.addStrategyDialogVisible = true
      // this.getList();
    },
    submit () {
      this.$refs['uploadForm'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        let url, messageSuccess, messageError, data
        if (this.dialogTitle === '新增') {
          url = '/scepter/group/add'
          messageSuccess = '添加成功'
          messageError = '添加失败'
          data = this.formAdd
        } else {
          url = '/scepter/group/edit'
          messageSuccess = '编辑成功'
          messageError = '添加失败'
          data = {
            id: this.formAdd.id,
            name: this.formAdd.name,
            description: this.formAdd.description
          }
        }
        service({
          url: url,
          method: 'POST',
          data: data
        }).then(res => {
          this.formAdd = {}
          this.$message({
            message: messageSuccess,
            type: 'success'
          })
          this.addStrategyDialogVisible = false
          this.getList()
        })
          .catch(() => {
            this.$message({
              type: 'warning',
              message: messageError
            })
          })
      })
    },
    handleDel (it) {
      const { id } = it
      this.$confirm('是否确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/scepter/group/del?id=${id}`
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        }).catch(function (error) {
          console.log(error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleDownGrade (it) {
      let { id } = it
      this.$confirm('是否确定降级?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/scepter/group/down?groupId=${id}`
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '降级成功!'
          })
          this.getList()
        }).catch((error) => {
          console.log(error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消降级'
        })
      })
    },
    handleReset (it) {
      let { id } = it
      this.$confirm('是否确定恢复?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/scepter/group/restore?groupId=${id}`
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '恢复成功!'
          })
          this.getList()
        }).catch((error) => {
          console.log(error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消恢复'
        })
      })
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    }
  }
}
</script>

<style lang="scss" scoped>
.functionWrapper {
  display: flex;
  justify-content: space-between;
}
.reject-tooltip {
  background: #FFFFFF;
  max-width: 128px;
}
.el-icon-question {
  color:#C1C1C1
}
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
