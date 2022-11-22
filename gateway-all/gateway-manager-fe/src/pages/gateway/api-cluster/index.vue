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
          @click="addCluster">新增</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column label="id" prop="id" width="80" fixed="left"></el-table-column>
        <el-table-column label="name" prop="name"></el-table-column>
        <!-- <el-table-column label="domain" prop="domain" width="150">
          <template slot-scope='scope'>
            <el-tag
              size='mini'
              style='width: 60px'
              :type="`${scope.row.status === 1 ? 'success' : ''}`">
              {{scope.row.status === 1 ? '录制中' : '待录制'}}
            </el-tag>
          </template>
        </el-table-column> -->
        <!-- <el-table-column label="group" prop="group" width="150"></el-table-column> -->
        <el-table-column label="描述" prop="description" show-overflow-tooltip></el-table-column>
        <!-- <el-table-column label="创建人" prop="creator" width="120"></el-table-column>
        <el-table-column label="更新人" prop="updater" width="120"></el-table-column> -->
        <el-table-column label="创建时间" prop="createTime" width="170"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="170"></el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="dealDomain(scope.row)">配置domain</el-button>
            <el-button type="text" size="mini" @click="dealGroup(scope.row)">配置group</el-button>
            <el-button type="text" size="mini" @click="editCluster(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" class='danger' @click="deleteCluster(scope.row.id)">删除</el-button>
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
      :title="`${handleClusterTitle}配置`"
      :visible.sync='handleCluDialogVisible'
      width='800px'
      :close-on-click-modal=false>
      <el-form ref='clusterForm' :model='clusterForm' :rules='clusterFormRules' label-width='110px' size='mini'>
        <el-form-item label="名称" prop="name">
          <el-input v-model="clusterForm.name" placeholder="请输入名称" style="width:50%"/>
        </el-form-item>
        <el-form-item label='描述' prop='description'>
          <el-input
            style="width:70%"
            type="textarea"
            :rows="6"
            v-model="clusterForm.description"
            placeholder="请输入相关描述"
            maxlength="200"
            show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCluDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitCluFormUpload('clusterForm')" size="mini">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="`配置${dealTitle}`"
      :visible.sync='dealDialogVisible'
      width='800px'
      :close-on-click-modal=false>
      <el-form ref='dealForm' :model='dealForm' :rules='dealFormRules' label-width='110px' size='mini'>
        <el-form-item label="domain" prop="domain" v-if='dealTitle === "domain"'>
          <el-select
            size='mini'
            multiple
            filterable
            style="width:50%"
            v-model='dealForm.domain'
            placeholder="请选择">
            <el-option
              v-for='item in domainOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
        </el-form-item>
        <el-form-item label='group' prop='group' v-else>
          <el-select
            size='mini'
            multiple
            filterable
            style="width:50%"
            v-model='dealForm.group'
            placeholder="请选择">
            <el-option
              v-for='item in groupOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dealDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitdealFormUpload('dealForm')" size="mini">确 定</el-button>
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
      handleClusterTitle: '',
      handleCluDialogVisible: false,
      clusterForm: {},
      clusterFormRules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
      },
      dealTitle: '',
      dealId: '',
      dealDialogVisible: false,
      dealForm: {},
      dealSubmitUrl: '',
      domainOptions: [],
      groupOptions: [],
      dealFormRules: {
        domain: [{ required: false, message: '请选择domain', trigger: 'blur' }],
        group: [{ required: false, message: '请选择group', trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getInitList()
    this.getDomainOptions()
    this.getGroupOptions()
  },
  methods: {
    getInitList () {
      const { page, pageSize, searchObj } = this
      service({
        url: `/apigroupcluster/list?page=${page}&pageSize=${pageSize}&name=${searchObj.name}`
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
    addCluster () {
      this.handleClusterTitle = '新增'
      this.handleCluDialogVisible = true
      this.submitUrl = '/apigroupcluster/new'
    },
    editCluster (row) {
      this.handleClusterTitle = '编辑'
      this.clusterForm = {
        ...row
      }
      this.handleCluDialogVisible = true
      this.submitUrl = '/apigroupcluster/update'
    },
    submitCluFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        this.handleCluDialogVisible = false
        service({
          url: this.submitUrl,
          method: 'POST',
          data: {
            ...this.clusterForm
          }
        }).then(res => {
          this.$message({
            type: 'success',
            message: `${this.handleClusterTitle}成功`
          })
          this.clusterForm = {}
          this.getInitList()
        })
      })
    },
    deleteCluster (id) {
      this.$confirm('此操作将永久删除该配置, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/apigroupcluster/delete?id=${id}`
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
    },
    dealDomain (row) {
      const id = row && row.id
      this.dealForm = {}
      this.getDomainList(id)
      this.dealId = id
      this.dealTitle = 'domain'
      this.dealSubmitUrl = '/apigroupcluster/updatedomain'
      this.dealDialogVisible = true
    },
    dealGroup (row) {
      const id = row && row.id
      this.dealForm = {}
      this.getGroupList(id)
      this.dealTitle = 'group'
      this.dealId = id
      this.dealSubmitUrl = '/apigroupcluster/updategroup'
      this.dealDialogVisible = true
    },
    getDomainList (id) {
      service({
        url: `/apigroupcluster/listdomain?id=${id}`
      }).then(res => {
        const list = res && res.metaDataList
        if (list) {
          this.dealForm = {
            domain: list.map(item => item.id)
          }
        }
      })
    },
    getGroupList (id) {
      service({
        url: `/apigroupcluster/listgroup?id=${id}`
      }).then(res => {
        const list = res && res.metaDataList
        if (list) {
          this.dealForm = {
            group: list.map(item => item.id)
          }
        }
      })
    },
    submitdealFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        this.dealDialogVisible = false
        let list = []
        if (this.dealTitle === 'domain') {
          list = this.dealForm.domain
        } else {
          list = this.dealForm.group
        }
        service({
          url: this.dealSubmitUrl,
          method: 'POST',
          data: {
            id: this.dealId,
            list
          }
        }).then(res => {
          this.$message({
            type: 'success',
            message: '配置成功'
          })
          this.getInitList()
        })
      })
    },
    getDomainOptions () {
      service({
        url: '/domain/listall'
      }).then(res => {
        const list = res && res.metaDataList
        if (list) {
          this.domainOptions = list.map(item => {
            return {
              label: item.name,
              value: item.id
            }
          })
        }
      })
    },
    getGroupOptions () {
      service({
        url: '/apigroup/listall2'
      }).then(res => {
        const list = res && res.groupList
        if (list) {
          this.groupOptions = list.map(item => {
            return {
              label: item.name,
              value: item.id
            }
          })
        }
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
.el-dropdown-styled {
  margin-left: 10px
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
.form_headers {
  width: 75%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 8px;
  .label {
    width: 160px;
  }
  .content {
    width: 250px;
  }
  .icon {
    font-size: 22px;
    align-self: center;
    color: rgb(192, 196, 204);
    margin-left: 7px;
    cursor: pointer;
  }
  .seat {
    display: inline-block;
    width: 22px;
    height: 22px;
    margin-left: 18px
  }
}
</style>
