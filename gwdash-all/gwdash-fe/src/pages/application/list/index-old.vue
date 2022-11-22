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
    <d2-module>
    <div class="header">
      <el-button size="small" type="primary" @click="updateProject('添加', '/project/create')">添加项目</el-button>
    </div>
    <div class="table-list">
      <el-table
        :data="List"
        style="width: 100%"
      >
        <el-table-column prop="id" label="id" width="50"></el-table-column>
        <el-table-column prop="name" label="项目名" width="160"></el-table-column>
        <el-table-column prop="ctime" label="创建时间" width="160"></el-table-column>
        <el-table-column prop="utime" label="更新时间" width="160"></el-table-column>
        <el-table-column prop="desc" label="描述" min-width="200" />
        <el-table-column label="操作" width="210">
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="updateMembers(scope.row.id)"
            >更新成员</el-button>
            <el-button
              type="text"
              @click="updateProject('更新', '/project/update', scope.row)"
            >更新</el-button>
            <!--
            <el-dropdown v-if="!isOnline" class="el-button el-button--text">
              <span class="el-dropdown-link">
                文档相关<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <router-link
                    class="el-button el-button--text"
                    :to="{ path: `/document/detail/1/${docsPre}${scope.row.id}`}">
                    项目文档
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                <router-link
                  class="el-button el-button--text"
                  :to="{ path: `/document/detail/6/${docsPre}${scope.row.id}`}">
                  todo文档
                </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link
                    class="el-button el-button--text"
                    :to="{ path: `/document/detail/7/${docsPre}${scope.row.id}`}">
                    change log 文档
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link
                    class="el-button el-button--text"
                    :to="{ path: `/issue/feedback?projectId=${docsPre}${scope.row.id}`}">
                    反馈
                  </router-link>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            -->
          </template>
        </el-table-column>
        <!--
        <el-table-column label="CI&CD" width="260" fixed="right">
            <template slot-scope="scope">
              <el-button
              type="text"
              @click="generateCodeDialog(scope.row, true)"
            >
              生成代码
            </el-button>
              <el-button type="text" @click="showBpDialog(scope.row)">构建</el-button>
              <router-link
                  class="el-button el-button--text"
                  :to="{ path: `/paudit/project/build/list?id=${scope.row.id}&name=${scope.row.name}`}">
                    构建记录
              </router-link>
              <router-link
                class="el-button el-button--text"
                :to="{ path: '/paudit/project/deploy/list', query: {projectId: scope.row.id, name: scope.row.name} }">
                  部署记录
              </router-link>
            </template>
        </el-table-column>
        -->
      </el-table>
      <div class="table-footer">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :current-page.sync="page"
          :page-size="pageSize"
          @current-change="currentChange"
        ></el-pagination>
      </div>
    </div>

    <el-dialog
      title="生成代码"
      width="800px"
      :visible.sync="dialogGenerateCodeVisible"
    >
      <el-form ref="gForm" :model="gForm" label-width="120px">
        <el-form-item label="git 地址">
          <el-input v-model="gForm.gitAddress" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="projectName">
          <el-input v-model="gForm.projectName"></el-input>
        </el-form-item>
        <el-form-item label="groupId">
          <el-input v-model="gForm.groupId" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="packageName">
          <el-input v-model="gForm.packageName"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="gForm.author"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="generateCodeDialog(null, false)">取 消</el-button>
        <el-button type="primary" @click="generateCode(gForm)">生 成</el-button>
      </span>
    </el-dialog>

    <el-dialog title="成员更新" :visible.sync="dialogMemberVisible" width="800px">
      <el-form ref="mForm" :model="mForm" :rules="mRules" label-width="110px" size='mini'>
        <el-form-item label="添加ower" prop="owners">
          <el-select
            v-model="mForm.owners"
            multiple
            filterable
            placeholder="请输入姓名"
            style="width: 70%" >
            <el-option
              v-for="item in memberOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="添加成员"  prop="members">
          <el-select
            v-model="mForm.members"
            multiple
            filterable
            placeholder="请输入姓名"
            style="width: 70%" >
            <el-option
              v-for="item in memberOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelMemberDialog">取 消</el-button>
        <el-button type="primary" @click="submitMemberForm('mForm')">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="项目构建"
      :visible.sync="bpDialogVisible"
    >
      <el-form
        label-width="100px"
        size="mini"
      >
        <el-form-item
          label="项目名称"
        >
          <el-input
            disabled
            v-model="bpForm.name" />
        </el-form-item>
        <el-form-item
          label="git地址"
        >
          <el-input
            disabled
            v-model="bpForm.gitAddress" />
        </el-form-item>
        <el-form-item
          label="git分支"
        >
          <el-input v-model="bpForm.branch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="buildProject">构建</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-dialog
      :title="`${title}项目`"
      :visible.sync="dialogVisible"
      width="600px">
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="120px">
        <el-form-item
          label="名字"
          prop="name"
        >
            <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item
          label="git group"
          prop="gitGroup"
        >
          <el-input v-model="form.gitGroup" />
        </el-form-item>
        <el-form-item
          label="git name"
          prop="gitName"
        >
          <el-input v-model="form.gitName" />
        </el-form-item>
        <el-form-item
          label="git库"
          prop="gitAddress"
        >
            <el-input disabled :value="`xx_replace_xx`" />
        </el-form-item>
        <el-form-item
          label="描述"
          prop="desc"
        >
          <el-input type="textarea" v-model="form.desc" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelDialog">取 消</el-button>
        <el-button type="primary" @click="submitForm('form')">确 定</el-button>
      </span>
    </el-dialog>
    </d2-module>
  </d2-container>
</template>

<script>
import request from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import { mapState } from 'vuex'
import statusMap from '../ststus_map'
// import { pjPre } from '../../docs/constants/type_info'

const getProjectNameByGitlabAddress = (gitlabAddress) => {
  const s = gitlabAddress.split('/')
  return s[s.length - 1]
}

export default {
  data () {
    return {
      isOnline: !!(serverEnv === 'c3' || serverEnv === 'c4' || serverEnv === 'intranet'),
      url: '/api/project/create',
      uploadData: {},
      title: '',
      // docsPre: pjPre.project, // 文档projectId前缀，不要轻易修改
      form: {
        name: '',
        desc: '',
        gitAddress: ''
      },
      mForm: {
        id: '',
        members: [],
        owners: []
      },
      gForm: {
        gitAddress: '',
        projectName: '',
        groupId: 'xx_replace_xx',
        packageName: '',
        author: ''
      },
      bpForm: {},
      rules: {
        name: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        // gitAddress: [
        //   { required: true, message: '必填字段', trigger: 'blur' },
        //   { type: 'url', message: 'url格式不对', trigger: 'blur' }
        // ],
        gitGroup: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        gitName: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ]
      },
      mRules: {
        // owners: [
        //   { required: true, message: '必填字段', trigger: 'blur' }
        // ]
      },
      memberOptions: [{ label: '1', value: 1 }],
      fileListError: '',
      projectId: 0,
      dialogVisible: false,
      dialogMemberVisible: false,
      dialogGenerateCodeVisible: false,
      bpDialogVisible: false,
      page: 1,
      pageSize: 10,
      total: 0,
      List: []
    }
  },
  computed: {
    ...mapState('d2admin/user', ['info'])
  },
  created () {
    this.getAllMembers()
    this.getList()
  },
  methods: {
    getList () {
      const page = this.page
      const pageSize = this.pageSize
      const url = `/project/list`
      request({
        url,
        method: 'post',
        data: {
          page: page,
          pageSize: pageSize,
          showAll: true
        }
      }).then(
        res => {
          if (!Array.isArray(res.list)) return
          this.total = res.total
          const list = []
          for (const item of res.list) {
            if (item) list.push(item)
          }
          this.List = list.map(item => {
            return {
              ...item,
              ctime: bizutil.timeFormat(item.ctime),
              utime: bizutil.timeFormat(item.utime)
            }
          })
        }
      )
    },
    getAllMembers () {
      request({
        url: '/account/all/list'
      }).then(res => {
        if (!Array.isArray(res)) return
        this.memberOptions = res.map(item => {
          return {
            name: `${item.name}[${item.userName}]`,
            id: item.id
          }
        })
      })
    },
    updateMembers (id) {
      this.mForm.id = id
      request({
        url: '/project/members',
        method: 'POST',
        data: { id }
      }).then(res => {
        let ownerIds = []
        let memberIds = []
        for (const item of res) {
          switch (item.roleType) {
            case 0:
              ownerIds.push(item.accountId)
              break
            case 1:
              memberIds.push(item.accountId)
          }
        }
        this.mForm.owners = ownerIds
        this.mForm.members = memberIds
        this.dialogMemberVisible = true
      }).catch(() => {
        this.$message.error('操作失败')
      })
    },
    currentChange (page) {
      this.page = page
      this.getList()
    },
    updateProject (title, url, item) {
      this.title = title
      this.dialogVisible = true
      this.url = url
      this.form = {
        name: '',
        desc: '',
        gitGroup: '',
        gitName: ''
      }
      if (item) {
        this.form = {
          id: item.id,
          name: item.name,
          gitGroup: item.gitGroup,
          gitName: item.gitName,
          desc: item.desc
        }
      }
      this.$nextTick(res => {
        this.$refs.form.clearValidate()
      })
    },
    generateCodeDialog (row, isOpen) {
      if (row) {
        this.gForm.gitAddress = row.gitAddress
        this.gForm.projectName = getProjectNameByGitlabAddress(row.gitAddress)
        this.gForm.packageName = this.gForm.groupId + '.' + this.gForm.projectName
      }
      this.dialogGenerateCodeVisible = isOpen
    },
    generateCode (form) {
      request({
        url: '/project/generateCode',
        method: 'post',
        data: {
          ...this.gForm
        }
      }).then(data => {
        if (data) {
          this.$message.success('操作成功')
          this.dialogGenerateCodeVisible = false
        } else {
          this.$message.success('操作失败')
        }
      })
    },
    deleteProject (url, id) {
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url,
          method: 'post',
          data: { id }
        }).then(
          data => {
            if (data) {
              this.getList()
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.delVisible = false
            } else {
              this.$message.error('操作失败')
            }
          }
        )
      }).catch(() => {
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
          }).then(
            data => {
              if (data) {
                this.getList()
                this.$message.success('操作成功')
                this.dialogVisible = false
                this.delVisible = false
              } else {
                this.$message.error('操作失败')
              }
            }
          )
        } else {
          return false
        }
      })
    },
    addMembers (roleType, members) {
      return request({
        url: '/project/addMembers',
        method: 'POST',
        data: {
          projectId: this.mForm.id,
          roleType,
          members
        }
      })
    },
    submitMemberForm (formName) {
      this.dialogMemberVisible = false
      Promise.all([this.addMembers(0, this.mForm.owners), this.addMembers(1, this.mForm.members)]).then(() => {
        this.$message.success('操作成功')
      })
    },
    cancelMemberDialog () {
      this.dialogMemberVisible = false
      this.mForm.id = ''
      this.mForm.members = []
    },
    cancelDialog () {
      this.dialogVisible = false
      this.fileListError = ''
      this.form.fileList = []
    },
    showBpDialog (item) {
      this.bpForm = {
        ...item,
        branch: 'master'
      }
      this.bpDialogVisible = true
    },
    buildProject () {
      const item = this.bpForm
      request({
        url: `/project/compile?id=${item.id}&gitUrl=${encodeURIComponent(item.gitAddress)}&branch=${item.branch}`
      }).then(res => {
        this.$router.push(`/paudit/project/build/list?id=${item.id}&name=${item.name}`)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
.table-footer {
  padding-top: 15px;
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
</style>
