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
      <el-form class="inline-form" :inline="true" :model="qForm" size="mini">
        <el-form-item label="审核状态">
          <el-select v-model="qForm.status" @change="onQuery" placeholder="请选择">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核未通过" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
    </d2-module>
    <d2-module>
      <div class="table-list">
        <el-table :data="List" style="width: 100%">
          <el-table-column prop="id" label="id" width="50"></el-table-column>
          <el-table-column label="申请人" width="160">
            <template
              slot-scope="scope"
            >{{accounts[scope.row.applicantName] || scope.row.applicantName}}</template>
          </el-table-column>
          <el-table-column prop="ctime" label="创建时间" width="160"></el-table-column>
          <el-table-column prop="utime" label="更新时间" width="160"></el-table-column>
          <el-table-column label="状态" width="160" fixed="right">
            <template slot-scope="scope">
              <el-tag :type="scope.row.statusType">{{scope.row.statusTxt}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="原因" prop="reason" />
          <el-table-column label="操作" width="180" fixed="right">
            <template slot-scope="scope">
              <div v-if="scope.row.status == '0'">
                <el-button type="text" @click="updateProject('/flow/agree', scope.row.id)">通过</el-button>
                <el-button type="text" @click="updateProject('/flow/refuse', scope.row.id)">不通过</el-button>
              </div>
              <div v-else>-</div>
            </template>
          </el-table-column>
        </el-table>
        <d2-pagination
            marginTop
            :currentPage='page'
            :pageSize='pageSize'
            :total='total'
            :pageDisabled='pageDisabled'
            @doCurrentChange='handleCurrentChange'>
         </d2-pagination>
      </div>
    </d2-module>

    <el-dialog :title="`${title}项目`" :visible.sync="dialogVisible" width="30%" :close-on-click-modal=false>
      <el-form ref="form" :inline="true" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="名字" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="git库" prop="gitAddress">
          <el-input v-model="form.gitAddress" />
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input type="textarea" v-model="form.desc" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog">取 消</el-button>
        <el-button type="primary" @click="submitForm('form')">确 定</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import request from '@/plugin/axios'
import bizutil from '@/common/bizutil'
import statusMap from './status_map'

export default {
  data () {
    return {
      url: '/api/project/create',
      uploadData: {},
      title: '',
      accounts: {},
      qForm: {
        status: '0'
      },
      form: {
        name: '',
        desc: '',
        gitAddress: ''
      },
      rules: {
        name: [{ required: true, message: '必填字段', trigger: 'blur' }],
        gitAddress: [{ required: true, message: '必填字段', trigger: 'blur' }]
      },
      fileListError: '',
      dialogVisible: false,
      page: 1,
      pageSize: 10,
      total: 0,
      List: [],
      pageDisabled: false
    }
  },
  created () {
    this.init()
  },
  methods: {
    async init () {
      try {
        await this.getMembers()
      } catch (e) {}
      await this.getList()
    },
    getList () {
      const page = this.page
      const pageSize = this.pageSize
      const status = this.qForm.status
      const url = `/flow/list`
      this.pageDisabled = true
      return request({
        url,
        method: 'post',
        data: {
          page,
          pageSize,
          status
        }
      }).then(res => {
        if (!res || !Array.isArray(res.list)) return
        this.total = res.total
        const data = res.list
        const list = []
        for (const item of data) {
          if (item) list.push(item)
        }
        this.List = list.map(item => {
          return {
            ...item,
            ctime: bizutil.timeFormat(item.ctime),
            utime: bizutil.timeFormat(item.utime),
            statusTxt: statusMap[item.status].name,
            statusType: statusMap[item.status].type
          }
        })
        this.pageDisabled = false
      })
    },
    getMembers () {
      return request({
        url: '/account/all/list',
        method: 'get'
      }).then(accounts => {
        if (!Array.isArray(accounts)) return
        for (const item of accounts) {
          this.accounts[item.id] = item.username
        }
      })
    },
    handleCurrentChange (page) {
      this.page = page
      this.getList()
    },
    updateProject (url, id) {
      request({
        url,
        method: 'post',
        data: { id }
      }).then(data => {
        if (data) {
          this.getList()
          this.$message.success('操作成功')
        } else {
          this.$message.error('操作失败')
        }
      })
    },
    deleteProject (url, id) {
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          request({
            url,
            method: 'post',
            data: { id }
          }).then(data => {
            if (data) {
              this.getList()
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.delVisible = false
            } else {
              this.$message.error('操作失败')
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    submitForm (formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          request({
            url: this.url,
            method: 'post',
            data: { ...this.form }
          }).then(data => {
            if (data) {
              this.getList()
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.delVisible = false
            } else {
              this.$message.error('操作失败')
            }
          })
        } else {
          return false
        }
      })
    },
    cancelDialog () {
      this.dialogVisible = false
      this.fileListError = ''
      this.form.fileList = []
    },
    onQuery () {
      this.page = 1
      this.getList()
    }
  }
}
</script>
<style lang="scss" scoped>
.inline-form {
  display: flex;
  justify-content: space-between;
  height: 30px;
}
.status-label {
  text-align: center;
}
.link-button {
  a {
    color: #409eff;
  }
}
</style>
