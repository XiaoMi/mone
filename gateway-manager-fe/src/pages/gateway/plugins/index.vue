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
        <el-button size="mini" @click="addPlugin">添加 Plugin</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table :data="pluginsList" style="width: 100%" class="table-list">
        <el-table-column prop="id" label="id" width="50"></el-table-column>
        <el-table-column prop="version" label="version" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              plain
              @click="handleChangeVersion(scope.row)"
            >{{pluginsList[scope.$index]['dataVersion']}}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="name" width="130"></el-table-column>
        <el-table-column prop="creator" label="作者" width="130"></el-table-column>
        <el-table-column prop="url" label="url" width="220"></el-table-column>
        <el-table-column label="项目" width="160">
          <template
            slot-scope="scope"
          >{{projectListMap[scope.row.projectId] || scope.row.projectId}}</template>
        </el-table-column>
        <el-table-column prop="ctime" label="创建时间"></el-table-column>
        <el-table-column prop="utime" label="更新时间"></el-table-column>
        <el-table-column prop="address" label="操作" width="420" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
            <template v-if="pluginsList[scope.$index]['status'] == 0">
              <el-button
                size="mini"
                type="primary"
                class="el-button--blue"
                :disabled="!scope.row.flowKey"
                @click="handleStart(scope.row)"
              >start</el-button>
            </template>

            <template v-if="pluginsList[scope.$index]['status'] == 1">
              <el-button
                size="mini"
                class="el-button--orange"
                @click="handleStop(
                pluginsList[scope.$index]['id'],
                pluginsList[scope.$index]['name']
              )"
              >stop</el-button>
            </template>

            <el-button size="mini" @click="handleDebug(scope.row)">DEBUG</el-button>
            <el-button
              size="mini"
              class="el-button--orange"
              @click="handleDelete(
              pluginsList[scope.$index]['id'],
              pluginsList[scope.$index]['name']
            )"
            >归档</el-button>
            <template v-if="pluginsList[scope.$index]['status'] == 2">
              <el-button
                size="mini"
                type="danger"
                class="el-button--blue"
                @click="handleStop(
                pluginsList[scope.$index]['id'],
                pluginsList[scope.$index]['name']
              )"
              >stop</el-button>
            </template>
            <el-button
              v-if="scope.row.flowKey"
              @click="showAuthorityDialog(scope.row.id, scope.row.projectId)"
              size="mini"
              class="el-button--blue"
              type="primary"
            >归还权限</el-button>
            <el-button
              v-else
              size="mini"
              @click="applyAuthority(scope.row.id, scope.row.projectId)"
              class="el-button--blue"
              type="primary"
            >申请权限</el-button>
            <el-button
              @click="returnAuthority(scope.row.id, scope.row.name)"
              size="mini"
              class="el-button--blue"
              type="primary"
            >收回权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </d2-module>

    <el-dialog title="添加plugin" :visible.sync="addPluginDialogVisible" width="800px" :close-on-click-modal=false>
      <el-form :model="formAdd" label-width="110px" ref="uploadForm" :rules="rules" size="mini">
        <el-form-item label="审批项目" prop="projectId">
          <el-select
            style="width:42%"
            v-model="formAdd.projectId"
            placeholder="请选择"
            default-first-option
            filterable
            remote
            :remote-method='remoteSearch'>
            <el-option
              v-for="item in projectListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="路由" prop="url">
          <el-select style="width:42%" v-model="formAdd.url" placeholder="请选择">
            <el-option
              v-for="item in routeUrlList"
              :key="item.url"
              :label="item.url"
              :value="item.url"
            />
          </el-select>
        </el-form-item>
        <d2-git-address :gitGroup.sync='formAdd.group' :gitName.sync='formAdd.name' :domain.sync='formAdd.domain' :disabled="false"/>
        <el-form-item label="branch" prop="branch">
          <el-input value="master" disabled style="width:42%"></el-input>
        </el-form-item>
        <el-form-item label="commit" prop="commitId">
          <el-select
            style="width:42%"
            placeholder="请选择【支持commit信息检索】"
            v-model="formAdd.commitId"
            filterable
          >
            <el-option
              v-for="item in commitGroup"
              :key="item.commitId"
              :label="item.message"
              :value="item.commitId"
              class="commitOption"
            >
              <span>{{item.sliceCommitId}}</span>
              <span>{{item.message}}</span>
            </el-option>
          </el-select>
          <span v-if="commitLoading" @click="getCommitList">
            <em class="el-icon-refresh" style="font-size:19px; margin-left: 6px"></em>
          </span>
          <span v-else>
            <em class="el-icon-loading" style="font-size:19px; margin-left:6px;"></em>
          </span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="addPluginDialogVisible = false">取 消</el-button>
        <el-button type="primary" size="mini" @click="submitAddFormUpload('uploadForm')">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="编辑plugin" :visible.sync="editPluginDialogVisible" width="800px" :close-on-click-modal=false>
      <el-form
        :model="formEdit"
        label-width="110px"
        ref="uploadEditForm"
        :rules="rules"
        size="mini"
      >
        <el-form-item label="路由" prop="url">
          <el-select style="width: 50%" v-model="formEdit.url" placeholder="请选择">
            <el-option
              v-for="item in routeUrlList"
              :key="item.url"
              :label="item.url"
              :value="item.url"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审批项目" prop="projectId">
          <el-select style="width: 50%" v-model="formEdit.projectId" placeholder="请选择">
            <el-option
              v-for="item in projectListOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <d2-git-address :gitGroup.sync='formEdit.group' :gitName.sync='formEdit.name' :domain.sync='formEdit.domain' :disabled="true"/>
        <el-form-item label="branch">
          <el-input value="master" disabled style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="commit">
          <el-select
            style="width: 50%"
            placeholder="请选择【支持commit信息检索】"
            v-model="formEdit.commitId"
            filterable
          >
            <el-option
              v-for="item in commitGroup"
              :key="item.commitId"
              :label="item.shortMessage"
              :value="item.commitId"
              class="commitOption"
            >
              <span>{{item.sliceCommitId}}</span>
              <span>{{item.message}}</span>
            </el-option>
          </el-select>
          <span v-if="commitLoading" @click="getCommitList">
            <em class="el-icon-refresh" style="font-size:19px; margin-left: 6px"></em>
          </span>
          <span v-else>
            <em class="el-icon-loading" style="font-size:19px; margin-left:6px;"></em>
          </span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="editPluginDialogVisible = false;">取 消</el-button>
        <el-button type="primary" size="mini" @click="submitEditFormUpload('uploadEditForm')">更 新</el-button>
      </span>
    </el-dialog>

    <el-dialog title="切换plugin版本" :visible.sync="versionPluginDialogVisible" width="30%" :close-on-click-modal=false>
      <el-form :inline="true" :model="formEdit">
        <el-form-item label="选择版本">
          <div :style="{width: '300px'}">
            <el-select size="medium" v-model="selectPlugin" placeholder="请选择">
              <el-option
                v-for="item in versionList"
                :key="item.id"
                :label="`${item.version} - ${time2Date(item.ctime)}`"
                :value="`${item.version}|${item.id}`"
              ></el-option>
            </el-select>
          </div>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="versionPluginDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitChangeVersion">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="选择 group" :visible.sync="selectPluginGroupDialogVisible" width="30%" :close-on-click-modal=false>
      <el-form :inline="true" :model="formEdit">
        <el-form-item label="group">
          <div :style="{width: '300px'}">
            <el-select size="medium" multiple v-model="selectPluginGroup" placeholder="请选择">
              <el-option
                v-for="item in pluginGroupList"
                :key="item.id"
                :label="item.name"
                :value="item.name"
              ></el-option>
            </el-select>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="selectPluginGroupDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitChangeGroup">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="DEBUG" :visible.sync="debugDialogVisible" width="30%" :close-on-click-modal=false>
      <el-form :model="formDebug" size="mini">
        <el-form-item label="机器">
          <el-col :span="12">
            <el-input v-model="ip" placeholder="ip" />
          </el-col>
          <el-col class="line" :span="1" :offset="1">:</el-col>
          <el-col :span="4">
            <el-input v-model="port" placeholder="port" />
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="debugDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" size="mini" @click="submitDebug('start')">START</el-button>
        <el-button type="primary" size="mini" @click="submitDebug('stop')">STOP</el-button>
      </span>
    </el-dialog>

    <el-dialog title="归还权限" :visible.sync="authorityFormVisible" :close-on-click-modal=false>
      <el-form :model="authForm">
        <el-form-item label="本次操作是否成功:">
          <el-radio-group v-model="authForm.status">
            <el-radio label="3">成功</el-radio>
            <el-radio label="4">失败</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="authorityFormVisible = false">暂不归还</el-button>
        <el-button type="primary" @click="withdrawAuthority">归还</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import time2Date from '@/libs/time2Date'
import isValidPluginName from '@/libs/isValidPluginName'
import qs from 'qs'
import d2GitAddress from '@/components/d2-git-address'

export default {
  data () {
    return {
      formAdd: {},
      tokenGroup: [],
      commitGroup: [],
      routeUrlList: [],
      commitLoading: true,
      rules: {
        projectId: [
          { required: true, message: '请选择审批项目', trigger: 'blur' }
        ],
        url: [{ required: true, message: '请选择路由', trigger: 'blur' }],
        commitId: [{ required: true, message: '请选择commit', trigger: 'blur' }]
      },
      router: '',
      projectId: '',
      pluginsList: [],
      versionList: [],
      pluginGroupList: [],
      projectListOptions: [],
      projectListMap: {},
      selectRow: [],
      selectPluginGroup: '',
      selectVersion: '',
      selectDataId: '',
      formEdit: {},
      formDebug: {},
      authForm: {
        status: '3'
      },
      ip: '',
      port: '',
      editPluginId: 0,
      addPluginDialogVisible: false,
      editPluginDialogVisible: false,
      debugDialogVisible: false,
      versionPluginDialogVisible: false,
      selectPluginGroupDialogVisible: false,
      authorityFormVisible: false,
      searchWord: ''
    }
  },
  components: {
    d2GitAddress
  },
  created () {
    this.init()
  },
  methods: {
    async init () {
      try {
        await this.getAllProjectList()
      } catch (e) {}
      await this.getPluginsList()
    },
    time2Date (time) {
      return time2Date(time)
    },
    returnAuthority (id, name) {
      service({
        url: '/plugin/clear/authority',
        method: 'post',
        data: {
          id,
          name
        }
      }).then(e => {
        if (e === true) {
          this.getPluginsList()
          this.$message.success('收回成功')
        } else {
          this.$message.success('收回失败')
        }
      })
    },
    // 新增plugin
    addPlugin () {
      this.addPluginDialogVisible = true
      this.getProjectList()
      this.getRouteList()
    },
    // 获取路由列表
    getRouteList () {
      service({
        url: '/apiinfo/list',
        method: 'POST',
        data: { groupType: 2 }
      })
        .then(res => {
          this.routeUrlList = res.infoList
        })
        .catch(() => {
          this.$message({
            type: 'warning',
            message: '请求出错: /apiinfo/list'
          })
        })
    },
    // 获取commit列表
    getCommitList () {
      if (this.editPluginDialogVisible === true) {
        this.formAdd.group = this.formEdit.group
        this.formAdd.name = this.formEdit.name
        this.formAdd.domain = this.formEdit.domain
      }
      this.commitLoading = false
      if ((this.formAdd.group && this.formAdd.name && this.formAdd.domain) || (this.formAdd.gitName && this.formAdd.gitGroup)) {
        service({
          url: `/plugin/commits?group=${this.formAdd.group}&name=${this.formAdd.name}&domain=${this.formAdd.domain}`,
          method: 'GET'
        })
          .then(res => {
            res.forEach(item => {
              item.commitId = item.id
              item.sliceCommitId = item.id.substr(0, 6)
            })
            this.commitGroup = res
            this.commitLoading = true
          })
          .catch(() => {
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
    // 添加 -> 提交
    submitAddFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.addPluginDialogVisible = false
        service({
          url: '/plugin/create',
          method: 'POST',
          data: qs.stringify(this.formAdd) // 表单提交
        })
          .then(res => {
            this.formAdd = {}
            this.$message({
              message: '添加成功',
              type: 'success'
            })
            this.getPluginsList()
          })
          .catch(() => {
            this.$message({
              type: 'warning',
              message: '请求错误: /plugin/create'
            })
          })
      })
    },
    // 编辑
    handleUpdate (row) {
      this.formEdit = { ...row, name: row.gitName, group: row.gitGroup, domain: row.gitDomain }
      this.editPluginDialogVisible = true
      this.editPluginId = row.id
      this.getProjectList()
      this.getRouteList()
    },
    // 编辑 -> 提交
    submitEditFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.editPluginDialogVisible = false
        var data = {
          pluginId: this.editPluginId,
          url: this.formEdit.url,
          projectId: this.formEdit.projectId,
          commitId: this.formEdit.commitId
        }
        // var data = { ...this.formEdit, pluginId: this.editPluginId};
        service({
          url: '/plugin/edit',
          method: 'POST',
          data: qs.stringify(data)
        })
          .then(res => {
            this.formEdit = {}
            this.$message({
              type: 'success',
              message: '更新成功'
            })
            this.getPluginsList()
          })
          .catch(() => {
            this.$message({
              type: 'warning',
              message: '请求错误： /plugin/edit'
            })
          })
      })
    },
    submitChangeVersion () {
      service({
        url: `/plugin/update/version`,
        method: 'post',
        data: {
          id: this.formEdit.id,
          version: this.selectVersion,
          dataId: this.selectDataId
        }
      }).then(res => {
        this.versionPluginDialogVisible = false
        this.editPluginDialogVisible = false

        this.getPluginsList()

        this.$message({
          message: `切换版本为${this.selectVersion}`,
          type: 'success'
        })
      })
    },
    createPluginUpload (file) {
      if (!this.projectId) {
        this.$message.error('审批项目必填')
        return false
      }
      this.beforeUpload(
        file,
        `/plugin/create/upload?url=${this.router}&projectId=${this.projectId}`,
        res => {
          this.addPluginDialogVisible = false
        },
        err => {
          console.log(err)
          this.getPluginsList()
        }
      )
    },
    beforeUpload (file, url, cb, errCb) {
      const { name } = file
      const isValid = isValidPluginName(name)

      if (!isValid) {
        this.$message({
          type: 'error',
          message: '名字不符合规范'
        })
        return
      }

      let fd = new FormData()
      fd.append('file', file)
      service({
        url,
        method: 'POST',
        data: fd
      })
        .then(res => {
          cb()
        })
        .catch(ee => {
          errCb()
        })
    },
    handleChangeVersion (row) {
      this.formEdit = { ...row }
      this.versionPluginDialogVisible = true
      this.selectVersion = row.dataVersion
      this.selectDataId = row.dataId

      const pluginId = row.id

      service({
        url: '/plugin/version/list',
        method: 'post',
        data: {
          pluginId
        }
      }).then(res => {
        this.versionList = res
      })
    },
    submitDebug (type) {
      this.debugDialogVisible = false

      service({
        url: `/plugin/${type}`,
        method: 'post',
        data: {
          id: this.selectRow.id,
          name: this.selectRow.name,
          addressList: [`${this.ip}:${this.port}`]
        }
      }).then(() => {
        this.$message({
          type: 'success',
          message: 'success'
        })
      })
    },
    handleDebug (row) {
      this.selectRow = row
      this.debugDialogVisible = true
    },

    handleStart (row) {
      this.selectPluginGroupDialogVisible = true
      this.selectRow = row

      service({
        url: '/plugin/group/list'
      }).then(res => {
        this.pluginGroupList = res
      })
    },
    submitChangeGroup () {
      const group = this.selectPluginGroup

      service({
        url: '/plugin/start',
        method: 'post',
        data: {
          id: this.selectRow.id,
          name: this.selectRow.name,
          groupList: group
        }
      })
        .then(res => {
          if (res === 0) {
            this.$message.success('执行成功')
          }
          this.selectPluginGroupDialogVisible = false
          this.getPluginsList()
        })
        .catch(() => {
          this.selectPluginGroupDialogVisible = false
          this.getPluginsList()
        })
    },
    handleStop (id, name) {
      service({
        url: '/plugin/stop',
        method: 'post',
        data: {
          id,
          name
        }
      }).then(() => {
        this.getPluginsList()
      })
    },
    handleDelete (id, name) {
      service({
        url: '/plugin/delete',
        method: 'post',
        data: {
          id,
          name
        }
      }).then(() => {
        this.getPluginsList()
      })
    },
    getPluginsList () {
      return service({
        url: '/plugin/list',
        method: 'get'
      }).then(e => {
        const pluginList = e
        pluginList.map(item => {
          if (
            item.gitGroup == null &&
            item.gitName == null &&
            item.gitAddress
          ) {
            const rGitAddress = /^https?:\/\/([0-9a-zA-Z_-]+)\/([0-9a-zA-Z_-]+)(?:\.git)?$/
            const match = item.gitAddress.match(rGitAddress)
            if (match && match[1] && match[2]) {
              item.gitGroup = match[1]
              item.gitName = match[2]
            }
          }
          item.ctime = time2Date(item.ctime)
          item.utime = time2Date(item.utime)
          return item
        })
        this.pluginsList = pluginList
      })
    },
    remoteSearch (query) {
      this.searchWord = query
      this.getProjectList()
    },
    getProjectList () {
      return service({
        url: '/project/list',
        method: 'post',
        data: {
          search: this.searchWord
        }
      }).then(({ list = [] }) => {
        if (!Array.isArray(list)) return
        this.projectListOptions = list.map(item => {
          return {
            value: item.id,
            label: item.name
          }
        })
      }).catch(() => {
        this.$message({
          type: 'warning',
          message: '请求出错: /project/list'
        })
      })
    },
    getAllProjectList () {
      return service({
        url: '/project/list',
        method: 'post',
        data: {
          showAll: true
        }
      }).then(({ list = [] }) => {
        if (!Array.isArray(list)) return
        list.forEach(item => {
          this.projectListMap[item.id] = item.name
        })
      })
    },
    showAuthorityDialog (bizId) {
      this.authorityFormVisible = true
      this.bizId = bizId
    },
    withdrawAuthority () {
      service({
        url: '/flow/over',
        method: 'post',
        data: {
          bizId: this.bizId,
          status: this.authForm.status
        }
      }).then(e => {
        if (e === true) {
          this.$message.success('归还成功')
          this.authorityFormVisible = false
          this.getPluginsList()
        } else {
          this.$message.success('归还失败')
        }
      })
    },
    applyAuthority (bizId) {
      service({
        url: '/flow/create',
        method: 'post',
        data: {
          bizId
        }
      }).then(e => {
        if (e === true) {
          this.getPluginsList()
          this.$message.success('申请已经提交')
        } else {
          this.$message.success('申请提交失败')
        }
      })
    }
  },
  computed: {
    selectPlugin: {
      get: function () {
        return this.selectVersion + '|' + this.selectDataId
      },
      set: function (newValue) {
        this.selectVersion = newValue.split('|')[0]
        this.selectDataId = newValue.split('|')[1]
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.el-select {
  width: 100%;
}
.gitaddress .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
  margin-bottom: 0px;
}
.commitOption.el-select-dropdown__item {
  height: 42px;
  border-bottom: 1px solid #ccc;
  span {
    display: block;
    line-height: 20px;
  }
}
</style>
