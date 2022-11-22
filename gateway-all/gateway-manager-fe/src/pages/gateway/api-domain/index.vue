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
        <div class='title'>名称：</div>
        <el-input
          size='mini'
          v-model='searchObj.name'
          @keypress.native.enter='handleInput'
          placeholder='请输入'
          class='input_margin'/>
        <el-button
          size='mini'
          @click="handleInput">查询</el-button>
        <el-button
          size='mini'
          @click="addDomain">新增</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column label="id" prop="id" width="120" fixed="left"></el-table-column>
        <el-table-column label="name" prop="name"></el-table-column>
        <el-table-column label="描述" prop="description" show-overflow-tooltip></el-table-column>
        <!-- <el-table-column label="创建人" prop="creator" width="120"></el-table-column>
        <el-table-column label="更新人" prop="updater" width="120"></el-table-column> -->
        <el-table-column label="创建时间" prop="createTime" width="190"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="190"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="editDomain(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" class='danger' @click="deleteDomain(scope.row.id)">删除</el-button>
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

    <el-dialog
      :title="`${handleDomainTitle}域名`"
      :visible.sync='handleDomDialogVisible'
      width='800px'
      destroy-on-close
      :close-on-click-modal=false>
      <el-form ref='domainForm' :model='domainForm' :rules='domainFormRules' label-width='110px' size='mini'>
        <el-form-item label="名称" prop="name">
          <el-input v-model="domainForm.name" placeholder="请输入名称" style="width:50%"/>
        </el-form-item>
        <el-form-item label="refer-header">
          <el-input v-model="domainForm.referHeader" placeholder="请输入refer-header" style="width:50%"/>
          <span class="tips"><i class="el-icon-question" /> 多个refer-header请用逗号分割</span>
        </el-form-item>
        <el-form-item label='描述' prop='description'>
          <el-input
            style="width:70%"
            type="textarea"
            :rows="6"
            v-model="domainForm.description"
            placeholder="请输入相关描述"
            maxlength="200"
            show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDomDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitDomFormUpload('domainForm')" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios'
import bizutil from '@/common/bizutil'

export default {
  data () {
    return {
      page: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
      searchObj: {
        name: ''
      },
      handleDomainTitle: '',
      handleDomDialogVisible: false,
      domainForm: {},
      domainFormRules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getInitList()
  },
  methods: {
    getInitList () {
      const { page, pageSize, searchObj } = this
      service({
        url: `/domain/list?page=${page}&pageSize=${pageSize}&name=${searchObj.name}`
      }).then(res => {
        this.page = res.page
        this.pageSize = res.pageSize
        this.total = res.total
        this.tableData = res.metaDataList.map(item => {
          return {
            ...item,
            createTime: bizutil.timeFormat(item.ctime),
            updateTime: bizutil.timeFormat(item.utime)
          }
        })
      })
    },
    handleInput () {
      this.page = 1
      this.pageSize = 10
      this.getInitList()
    },
    handleCurrentChange (val) {
      this.page = val
      this.getInitList()
    },
    addDomain () {
      this.handleDomainTitle = '新增'
      this.handleDomDialogVisible = true
      this.submitUrl = '/domain/new'
    },
    editDomain (row) {
      this.handleDomainTitle = '编辑'
      this.domainForm = {
        ...row
      }
      this.handleDomDialogVisible = true
      this.submitUrl = '/domain/update'
    },
    submitDomFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        this.handleDomDialogVisible = false
        service({
          url: this.submitUrl,
          method: 'POST',
          data: {
            ...this.domainForm
          }
        }).then(res => {
          this.$message({
            type: 'success',
            message: `${this.handleDomainTitle}成功`
          })
          this.domainForm = {}
          this.getInitList()
        })
      })
    },
    deleteDomain (id) {
      this.$confirm('此操作将永久删除该域名, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/domain/delete?id=${id}`
        }).then(res => {
          this.$message({
            message: '删除成功',
            type: 'success'
          })
          this.getInitList()
        })
      }).catch(() => {
        this.$message({
          message: '已取消删除',
          type: 'warning'
        })
      })
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
.tips {
  color: #909399;
  font-size: 12px;
  padding-left: 10px;
  i {
    font-size: 13px;
  }
}

</style>
