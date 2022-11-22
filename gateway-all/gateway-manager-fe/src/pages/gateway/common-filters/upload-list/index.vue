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
        <el-button size="mini" @click="changeFilter('添加', '/filter/new')">添加通用 Filter</el-button>
      </div>
    </d2-module>
    <d2-module>
      <div class="table-list">
        <el-table :data="pluginList" style="width: 100%">
          <el-table-column prop="id" label="id" width="50"></el-table-column>
          <el-table-column label="name" width="160">
            <template slot-scope="scope">
              <span>{{scope.row.cname || scope.row.name}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="version" label="版本" width="80"></el-table-column>
          <el-table-column label="评分" width="160">
            <template slot-scope="scope">
              <el-popover
                  trigger="hover">
                <div>
                  <span>我的评分</span>
                  <el-rate
                      allow-half
                      v-model="scope.row.rate"
                      text-color="#ff9900"
                      @change="changeRate(scope.row)">
                  </el-rate>
                </div>
                <el-rate
                    allow-half
                    v-model="scope.row.avgRate"
                    text-color="#ff9900"
                    disabled
                    slot="reference">
                </el-rate>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="作者" width="120"></el-table-column>
          <el-table-column prop="creator" label="上传者" width="120"></el-table-column>
          <el-table-column show-overflow-tooltip prop="commitId" label="commitId" width="180"></el-table-column>
          <el-table-column prop="ctime" label="创建时间" width="160"></el-table-column>
          <el-table-column prop="utime" label="更新时间" width="160"></el-table-column>
          <el-table-column show-overflow-tooltip prop="desc" min-width="160" label="描述" />
          <el-table-column label="状态" width="100" fixed="right">
            <template slot-scope="scope">
              <el-tag :type="scope.row.statusType">{{scope.row.statusTxt}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="170" fixed="right">
            <template slot-scope="scope">
              <el-button
                  type="text"
                  @click="changeFilter('更新', '/filter/update', scope.row)"
              >更新</el-button>
              <!-- <el-button
                type="text"
                class="link-button"
                v-if="scope.row.projectCompileRecord
                  && scope.row.projectCompileRecord.url"
              >
                <a :href="scope.row.projectCompileRecord.url" target="_blank">下载</a>
              </el-button> -->
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

    <el-dialog :title="`${title}通用filter`" :visible.sync="dialogVisible" width="800px" :close-on-click-modal=false>
      <el-form :model="formAdd" label-width="110px" ref="uploadForm" :rules="rules" size="mini">
        <d2-git-address  :gitGroup.sync='formAdd.group' :gitName.sync='formAdd.name' :domain.sync='formAdd.domain' />
        <el-form-item label="branch">
          <el-input value="master" disabled style="width:42%"></el-input>
        </el-form-item>
        <el-form-item label="commit" prop="commitId">
          <el-select style="width:42%" placeholder="请选择【支持commit信息检索】" v-model="formAdd.commitId" filterable>
            <el-option v-for="item in commitGroup" :key="item.commitId" :label="item.message" :value="item.commitId" class="commitOption">
              <span>{{item.sliceCommitId}}</span>
              <span>{{item.message}}</span>
            </el-option>
          </el-select>
          <span v-if="commitLoading" @click="getCommitList"><em class="el-icon-refresh" style="font-size:19px; margin-left: 6px"></em></span>
          <span v-else><em class="el-icon-loading" style="font-size:19px; margin-left:6px;"></em></span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
          <el-button type="primary" @click="submitUploadForm('uploadForm')" size="mini">确 定</el-button>
       </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import qs from 'qs'
import bizutil from '@/common/bizutil'
import { mapState } from 'vuex'
import statusMap from '../status_map'
import d2GitAddress from '@/components/d2-git-address'

export default {
  data () {
    return {
      commitGroup: [],
      tokenGroup: [],
      formAdd: {},
      page: 1,
      pageSize: 10,
      total: 0,
      pluginList: [],
      uploadId: 0,
      commitLoading: true,
      url: '',
      title: '',
      rules: {
        // gitGroup: [
        //   { required: true, message: '请输入组名', trigger: 'blur' }
        // ],
        // gitName: [
        //   { required: true, message: '请输入项目名', trigger: 'blur' }
        // ],
        commitId: [
          { required: true, message: '请输入commit', trigger: 'blur' }
        ]
      },
      fileListError: '',
      dialogVisible: false,
      pageDisabled: false
    }
  },
  computed: {
    ...mapState('d2admin/dealUserInfo', ['userInfo', 'isOnline'])
  },
  created () {
    this.getPluginList()
  },
  methods: {
    getPluginList () {
      this.pageDisabled = true
      const page = this.page
      const pageSize = this.pageSize
      const url = `/filter/list?page=${page}&pageSize=${pageSize}`
      service({ url }).then(res => {
        if (!Object.prototype.toString.call(res) === '[object Object]') return
        this.page = res.page
        this.total = res.total
        this.pluginList = res.pluginList.map(item => {
          const userRateBos = item.userRateBos || []
          const len = userRateBos.length || 0
          let rate = 0
          const totalRate = userRateBos.reduce((prev, cur) => {
            if (cur.accountId === this.userInfo.uuid) rate = cur.rate
            return prev + cur.rate
          }, 0)
          if (item.gitGroup == null && item.gitName == null && item.gitAddress) {
            const rGitAddress = /^https?:\/\/(?:v9\.)?git\.n\.xiaomi\.com\/([0-9a-zA-Z_-]+)\/([0-9a-zA-Z_-]+)(?:\.git)?$/
            const match = item.gitAddress.match(rGitAddress)
            if (match && match[1] && match[2]) {
              item.gitGroup = match[1]
              item.gitName = match[2]
            }
          }
          item.domain = item.gitAddress.startsWith('https') ? item.gitAddress.slice(8, item.gitAddress.indexOf('/', 8)) : item.gitAddress.slice(7, item.gitAddress.indexOf('/', 8))
          return {
            ...item,
            ctime: bizutil.timeFormat(item.ctime),
            utime: bizutil.timeFormat(item.utime),
            statusTxt: statusMap[item.status].name,
            statusType: statusMap[item.status].type,
            rate,
            avgRate: len ? (totalRate / len) : 0
          }
        })
        this.pageDisabled = false
      },
      () => {
        this.$message.error('获取插件列表失败')
      }
      )
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    handleCurrentChange (val) {
      this.page = val
      this.getPluginList()
    },
    changeFilter (title, url, row) {
      if (title === '更新') {
        this.formAdd = { ...row, group: row.gitGroup, name: row.gitName || '' }// 表单数据回填
        this.uploadId = row.id
      }
      this.dialogVisible = true
      this.title = title
      this.url = url
    },
    // 获取commit列表
    getCommitList () {
      this.commitLoading = false
      if (this.formAdd.group && this.formAdd.name) {
        service({
          url: `/filter/commits?group=${this.formAdd.group}&name=${this.formAdd.name}&domain=${this.formAdd.domain}`,
          method: 'GET'
        }).then(res => {
          res.forEach(item => {
            item.commitId = item.id
            item.sliceCommitId = item.commitId.substr(0, 6)
          })
          this.commitGroup = res
          this.commitLoading = true
        }).catch(() => {
          this.commitLoading = true
        })
      } else {
        this.$message({
          message: '请完成组名、项目名填写',
          type: 'warning'
        })
        this.commitLoading = true
      }
    },
    // 添加/更新 -> 提交
    submitUploadForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.dialogVisible = false
        this.url !== '/filter/new' && (this.formAdd.id = this.uploadId)
        service({
          url: this.url,
          method: 'POST',
          data: qs.stringify(this.formAdd)
        }).then(res => {
          this.formAdd = {}
          this.$message({
            message: '添加成功',
            type: 'success'
          })
          this.getPluginList()
        })
      })
    },
    beforeFileUpload (file) {
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isLt5M) {
        this.$message.error('上传jar包大小不能超过 5MB!')
      }
      return isLt5M
    },
    onFileChange (file, fileList) {
      this.form.fileList = fileList
      this.$refs.uploadForm.validateField('fileList') // 手动交验更新
    },
    onFileRemove (file, fileList) {
      this.fileListError = ''
      this.form.fileList = fileList
    },
    onSuccess (res, file, fileList) {
      this.$refs.uploadPlugin.clearFiles()
      if (res.code === 0) {
        this.form.fileList = []
        this.page = 1
        this.getPluginList()
        this.cancelDialog()
      } else {
        this.fileListError = res.message || '解析失败，请修改后上传'
      }
    },
    onError (errInfo, file, fileList) {
      this.form.fileList = [file]
      this.fileListError = '上传操作失败'
    },
    onExceed (file, fileList) {
      this.fileListError = '最多只能传一个jar包'
    },
    cancelDialog () {
      this.dialogVisible = false
      this.fileListError = ''
      this.form.fileList = []
    },
    // 评分
    changeRate (item) {
      service({
        url: '/filter/rate',
        method: 'post',
        data: qs.stringify({
          id: item.id,
          rate: item.rate
        })
      })
    }
  },
  components: {
    d2GitAddress
  }
}
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.status-label {
  text-align: center;
}
.link-button {
  a {
    color: #409EFF
  }
}
.commitOption.el-select-dropdown__item {
  height: 42px;
  border-bottom: 0.5px solid #ccc;
  span{
    display: block;
    line-height: 20px;
  }
}
</style>
