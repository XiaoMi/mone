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
          size='mini'
          @click="handleTestMember">关联测试审核人员</el-button>
      </div>
    </d2-module>
    <el-dialog title="关联测试审核人员" :visible.sync='relationTestMemberDialog' width='800px'>
      <el-form label-width='110px' ref='relationForm' :model='relationForm' :rules='relationRules' size='mini'>
        <el-form-item prop='projectId' label='项目'>
          <el-select
            v-model='relationForm.projectId'
            filterable
            remote
            :remote-method='remoteSearch'
            @change='proChange'
            placeholder='支持搜索项目'
            style='width:70%'>
            <el-option
              v-for='item in projectList'
              :key='item.projectId'
              :label='item.label'
              :value='item.projectId'>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop='members' label='测试审核人员'>
          <el-select v-model="relationForm.members" multiple filterable placeholder="请选择相关人员" style="width:70%">
              <el-option v-for="item in selectOptions" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="relationFormUpload('relationForm')">确 定</el-button>
        <el-button size="mini" @click="relationTestMemberDialog = false">取 消</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"
export default {
  data () {
    return {
      relationTestMemberDialog: false,
      relationForm: {
        projectId: '',
        members: []
      },
      relationRules: {
        projectId: [{ required: true, message: "请选择", trigger: "blur" }],
        members: [{ required: false, message: '请选择测试人员', trigger: 'blur' }]
      },
      projectList: [],
      searchWord: '',
      selectOptions: []
    }
  },
  created () {
    this.getProList()
    this.getAllMembers()
  },
  methods: {
    handleTestMember () {
      this.relationTestMemberDialog = true
    },
    getAllMembers () {
      return service({
        url: '/account/all/list',
        method: 'GET'
      }).then(res => {
        if (!Array.isArray(res)) return
        this.selectOptions = res.map(item => {
          return {
            name: `${item.name}[${item.userName}]`,
            id: item.id
          }
        })
      })
    },
    remoteSearch (query) {
      this.searchWord = query
      this.getProList()
    },
    proChange (id) {
      this.getInitMember(id)
    },
    getInitMember (id) {
      service({
        url: '/project/members',
        method: 'POST',
        data: { id }
      }).then(res => {
        if (!Array.isArray(res)) return
        let ids = []
        for (const item of res) {
          item.roleType === 2 && ids.push(item.accountId)
        }
        this.relationForm.members = ids
      })
    },
    getProList () {
      service({
        url: '/project/list',
        method: 'POST',
        data: {
          search: this.searchWord,
          showAll: true
        }
      }).then(res => {
        if (!Array.isArray(res.list)) return
        this.projectList = res.list.map(item => {
          if (item.gitGroup == null && item.gitName == null && item.gitAddress) {
            const rGitAddress = /^https$/
            const match = item.gitAddress.match(rGitAddress)
            if (match && match[1] && match[2]) {
              item.gitGroup = match[1]
              item.gitName = match[2]
            }
          }
          return {
            label: `${item.id}: ${item.gitGroup}/${item.gitName}`,
            projectId: item.id
          }
        })
      })
    },
    relationFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.relationTestMemberDialog = false
        let { projectId, members } = this.relationForm
        service({
          url: '/project/addMembers',
          method: 'POST',
          data: {
            projectId,
            members,
            roleType: 2
          }
        }).then(res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.initData()
        })
      })
    },
    initData () {
      this.relationForm = {
        projectId: '',
        members: []
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
</style>
