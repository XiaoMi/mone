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
      :thirdFloor='thirdFloor'
      :secondRoute='secondRoute'
   />
  <d2-module margin-bottom>
    <div class="functionWrapper">
      <span><i>{{name}}</i></span>
      <el-button @click="handleAdd()" type="primary" size="mini">新增配置</el-button>
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
        label="项目名"
        width="200">
      </el-table-column>
      <!-- <el-table-column
        prop="downgradeConfig"
        label="降级配置"
        width="150">
     </el-table-column>
      <el-table-column
        prop="restroreConfig"
        label="恢复配置"
        width="150">
      </el-table-column> -->
      <el-table-column
        prop="ctime"
        label="创建时间"
        width="180"
        show-overflow-tooltip>
      </el-table-column>
        <el-table-column
        prop="utime"
        label="审核时间"
        width="180"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="passed"
        label="审核状态"
        width="150"
        show-overflow-tooltip>
        <template slot-scope="scope">
           <el-tag
              v-if='scope.row.passed===1'
              size='mini'
              style="width:60px"
              type="success">通过</el-tag>
           <el-tag
              v-if='scope.row.passed===2'
              size='mini'
              style="width:60px"
              type="info">未审核</el-tag>
           <el-tag
              v-if='scope.row.passed===0'
              size='mini'
              style="width:60px"
              type="warning">未通过</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="操作" min-width="280">
        <template slot-scope="scope">
          <el-button size="mini" v-if='scope.row.passed===2'  class="el-button--blue" @click="handleAdd(scope.row)">编辑</el-button>
          <el-button size="mini" class="el-button--orange" @click="handleDel(scope.row)">删除</el-button>
          <el-button size="mini" class="el-button--green"  v-if='scope.row.passed===2 && isSuperUser===true'  @click="handleExamine(1,scope.row.id)">通过</el-button>
          <el-button size="mini" class="el-button--orange"  v-if='scope.row.passed===2 && isSuperUser===true'  @click="handleExamine(2,scope.row.id)">驳回</el-button>
          <el-button size="mini" class="el-button--blue" @click="handleCheck(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <d2-pagination
      marginTop
      :currentPage='pager.offset/this.pager.limit +1 '
      :pageSize='pager.limit'
      :total='pager.total'
      :pageDisabled='pageDisabled'
      @doCurrentChange='handleCurrentChange'>
    </d2-pagination>
  </d2-module>

  <el-dialog :title='dialogTitle' :visible.sync="addDialogVisible" width="800px" :before-close='handleAddClose'>
    <el-form :model="formAdd" label-width ="110px" ref="uploadForm" :rules="rules" size="mini">
      <el-form-item prop="name" label="项目名">
        <el-input v-model="formAdd.name" placeholder="请输入项目名"></el-input>
      </el-form-item>
      <el-form-item prop="type" label="配置">
        <el-select v-model="formAdd.type" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <div v-if="formAdd.type===1">
        <el-form-item prop="group" label="group">
              <el-input v-model="formAdd.group" placeholder="请输入group"></el-input>
        </el-form-item>
        <el-form-item prop="dataId" label="dataId">
              <el-input v-model="formAdd.dataId" placeholder="请输入dataId"></el-input>
        </el-form-item>
        <el-form-item label="降级配置" prop="downgradeConfig">
          <template>
              <codemirror v-model="formAdd.downgradeConfig" :options="cmOptions"></codemirror>
          </template>
        </el-form-item>
        <el-form-item label="恢复配置" prop="restroreConfig">
          <template>
              <codemirror v-model="formAdd.restroreConfig" :options="cmOptions"></codemirror>
          </template>
        </el-form-item>
      </div>
      <div v-else>
        <el-form-item prop="apiId" label="apiId">
          <el-input v-model="formAdd.apiId" placeholder="请输入内容" size="mini"></el-input>
        </el-form-item>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="mini" @click="addDialogVisible = false">取 消</el-button>
      <el-button type="primary" size="mini" @click="submit()">确 定</el-button>
    </span>
  </el-dialog>

  <el-dialog title="驳回" :visible.sync="formVisible" width="400px" :before-close="handleClose1">
    <span>确认该项目审核驳回吗？</span>
    <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleAdopt"  class="dialog-button-left" size="mini">确认驳回</el-button>
        <el-button @click="formVisible = false" class="dialog-button-right" size="mini">取消</el-button>
    </span>
   </el-dialog>

    <el-dialog title="提示" :visible.sync="dialogVisible" width="400px" :before-close="handleClose">
      <span>确认该项目审核通过吗？</span>
      <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="handleAdopt" size="mini" class="dialog-button-left">确认通过</el-button>
          <el-button @click="dialogVisible = false" size="mini" class="dialog-button-right">取消</el-button>
      </span>
   </el-dialog>

  <el-dialog title="配置详情" :visible.sync="dialogVisibleCheck" width="700px" :before-close="handleClose">
    <el-form :model="checkList" label-width ="110px" ref="uploadForm" :rules="rules" size="mini">
       <el-form-item prop="name" label="项目名">
        <el-input v-model="checkList.name" placeholder="请输入项目名" disabled></el-input>
      </el-form-item>
      <el-form-item prop="type" label="配置">
        <el-select v-model="checkList.type" placeholder="请选择" disabled>
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <div v-if="checkList.type===1">
        <el-form-item prop="group" label="group">
              <el-input v-model="checkList.group" placeholder="请输入group" disabled></el-input>
        </el-form-item>
        <el-form-item prop="dataId" label="dataId">
              <el-input v-model="checkList.dataId" placeholder="请输入dataId" disabled></el-input>
        </el-form-item>
      <el-form-item label="降级配置" prop="downgradeConfig">
        <template>
            <codemirror v-model="checkList.downgradeConfig" :options="checkOptions"></codemirror>
        </template>
      </el-form-item>
      <el-form-item label="恢复配置" prop="restroreConfig">
        <template>
            <codemirror v-model="checkList.restroreConfig" :options="checkOptions"></codemirror>
        </template>
      </el-form-item>
      </div>
      <div v-else>
        <el-form-item prop="apiId" label="apiId">
          <el-input v-model="checkList.apiId" placeholder="请输入内容" size="mini" disabled></el-input>
        </el-form-item>
      </div>
    </el-form>
   </el-dialog>
 </d2-container>
</template>

<script>
import projectHeader from './../../layout/header'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import qs from 'qs'
import 'codemirror/lib/codemirror.js'
import 'codemirror/theme/base16-dark.css'
import '@/common/jsonlint-z.js'
import 'codemirror/addon/lint/lint.js'
import 'codemirror/addon/lint/json-lint.js'
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/lint/lint.css'

export default {
  name: '',
  data () {
    return {
      name: '',
      firstFloor: '降级系统',
      secondFloor: '策略列表',
      thirdFloor: '项目配置',
      secondRoute: '/downgrade/service/list',
      isSuperUser: false,
      list: [ ], // 列表
      multipleSelection: [],
      addDialogVisible: false,
      formAdd: {
        type: 1
      },
      dialogTitle: '新增',
      dialogData: { },
      dialogVisible: false,
      formVisible: false,
      dialogVisibleCheck: false,
      checkList: { },
      rules: {
        name: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        group: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        dataId: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        downgradeConfig: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        restroreConfig: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        apiId: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ]
      },
      cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      checkOptions: {
        tabSize: 4,
        indentUnit: 4,
        theme: 'base16-dark',
        readOnly: true,
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      pager: {
        offset: 0,
        limit: 10,
        total: 1
      }, // 分页项
      options: [ ],
      pageDisabled: false
    }
  },
  components: {
    projectHeader
  },
  created () {
    this.name = this.$route.params.name
    this.getUser()
    this.getType()
    this.getList()
  },
  methods: {
    getType () {
      service({
        url: 'scepter/config/type',
        method: 'GET'
      }).then(res => {
        this.options = Object.keys(res).reverse().map(it => {
          let obj = {}
          obj.value = res[it]
          obj.label = it
          return obj
        })
      })
    },
    getUser () {
      let username = window.userInfo.username
      service({
        url: `scepter/user/isAdmin?username=${username}`,
        method: 'GET'
      }).then(res => {
        this.isSuperUser = res
      })
    },
    getList () { // 初始化表格
      this.pageDisabled = true
      let { offset, limit } = this.pager
      let url = `/scepter/config/listByGroupid?groupId=${this.$route.params.id}&limit=${limit}&offset=${offset}`
      service({
        url: url,
        method: 'GET'
      }).then(res => {
        this.list = res.configs.map(it => {
          it.ctime = bizutil.timeFormat(it.ctime)
          it.utime = bizutil.timeFormat(it.utime)
          return it
        })
        this.pager.total = res.total
        this.pageDisabled = false
      })
    },
    handleCurrentChange (val) { // 分页变化
      this.pager.offset = (val - 1) * this.pager.limit
      this.getList()
    },
    handleExamine (flag, id) {
      this.dialogData.id = id
      this.dialogData.flag = flag
      flag === 1
        ? this.dialogVisible = true
        : this.formVisible = true
    },
    handleAdopt () {
      let url, messageSuccess
      let id = this.dialogData.id
      if (this.dialogData.flag === 1) {
        messageSuccess = '审核已通过'
        url = '/scepter/config/pass'
      } else {
        messageSuccess = '审核已驳回'
        url = '/scepter/config/nopass'
      }
      service({
        url: url,
        method: 'post',
        data: {
          id: id
        }
      }).then((res) => {
        this.dialogVisible = false
        this.formVisible = false
        this.$message({
          message: messageSuccess,
          type: 'success'
        })
        this.getList()
      }).catch((error) => {
        this.dialogVisible = false
        this.formVisible = false
        console.log(error)
      })
    }, // 点击确认通过
    handleAdd (it) {
      let rowTemp = { ...it }
      if (it === undefined) {
        this.formAdd = {
          type: 1,
          downgradeConfig: '',
          restroreConfig: ''
        }
        this.dialogTitle = '新增'
      } else {
        if (rowTemp.type === 1) rowTemp.apiId = ''
        if (rowTemp.type === 2) {
          rowTemp.group = ''
          rowTemp.dataId = ''
          rowTemp.downgradeConfig = ''
          rowTemp.restroreConfig = ''
        }
        this.formAdd = rowTemp
        this.dialogTitle = '编辑'
      }
      this.addDialogVisible = true
    //   this.getList();
    }, // 点击新增或编辑
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
          url = '/scepter/config/add'
          messageSuccess = '添加成功'
          messageError = '添加失败'
          data = {
            gid: this.$route.params.id,
            ...this.formAdd
          }
        } else {
          url = '/scepter/config/edit'
          messageSuccess = '编辑成功'
          messageError = '添加失败'
          data = {
            id: this.formAdd.id,
            name: this.formAdd.name,
            gid: this.formAdd.gid,
            group: this.formAdd.group,
            dataId: this.formAdd.dataId,
            downgradeConfig: this.formAdd.downgradeConfig,
            restroreConfig: this.formAdd.restroreConfig,
            apiId: Number(this.formAdd.apiId),
            type: this.formAdd.type
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
          this.addDialogVisible = false
          this.getList()
        })
          .catch((error) => {
            console.log(error)
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
          url: `/scepter/config/del?id=${id}`
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getList()
        }).catch((error) => {
          console.log(error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleCheck (it) {
      this.dialogVisibleCheck = true
      this.checkList = it
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    handleAddClose (it) {
      this.addDialogVisible = false
      this.formAdd = {}
    }
  }
}
</script>

<style lang="scss" scoped>
    .functionWrapper{
      display: flex;
      justify-content: space-between;
      span {
        height: 28px;
        line-height: 28px;
      }
    }
   .font-demo {
        letter-spacing: 0px;
        font-family: PingFang SC;
        font-weight: regular;
        // text-align: center;
    }
    /deep/ .el-dialog__wrapper{
        .el-dialog {
            width: 400px;
            .el-dialog__header {
                border-left: 4px solid #457BFC;
                .el-dialog__title {
                    @extend .font-demo;
                    color: #333333;
                    font-weight: medium;
                    font-size: 14px;
                    line-height: normal;
                    text-align: left;
                }
            }
            .el-dialog__body {
                @extend .font-demo;
                border-top: 1px solid #EFF0F4;
                color: #333333;
                font-size: 13px;
                .approved-people {
                    font-weight: bolder;
                }
                .vue-codemirror {
                  text-align: left;
                }
            }
            .el-dialog__footer {
                text-align: center;
                .el-button {
                    @extend .font-demo;
                    width: 76px;
                    font-size: 12px;
                    border-radius: 2px;
                }
                .dialog-button-left{
                    color: #FFFFFF;
                    background: #457BFC;
                }
                .dialog-button-right{
                    color: #333333;
                    background: #EEEEEE;
                }
            }
        }
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
