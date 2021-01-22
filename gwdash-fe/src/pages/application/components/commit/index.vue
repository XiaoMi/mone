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
    <div class="project-commit">
      <div class="no-token" v-if="commitOptions === null">您未授权gitlab access token,
        <router-link style="color:#409EFF" :to="{path:'/account/gitlab'}">请授权</router-link>
      </div>
      <el-form :model="commitForm" label-width="58px" size="mini" v-else>
        <el-form-item label="Branch:">
          <el-select
            v-model="commitBranch"

            remote
            @change="selectedBranch"
            :remote-method="searchBranch"
            :loading="loading"
          >
            <el-option v-for="(item, index) in commitOptions" :key="index" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-timeline class="commit-timeline">
          <el-timeline-item
              v-for="item in commitTimeList"
              :key="item.id"
              :timestamp="item.timestamp"
              :color="item.color">
              {{ item.message }} - {{ item.sliceCommitId }}
          </el-timeline-item>
      </el-timeline>
    </div>
</template>

<script>
import { throttle } from 'lodash'
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import qs from 'qs'

export default {
  props: {
    id: {
      type: Number,
      required: true
    },
    projectForm: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      loading: false,
      commitForm: {},
      commitOptions: [],
      commitTimeList: [],
      commitBranch: '',
      noToken: ''
    }
  },
  created () {
    this.updateBranchAndCommits()
  },
  watch: {
    id: function () {
      this.updateBranchAndCommits()
    }
  },
  methods: {
    updateBranchAndCommits () {
      this.getCommitOptions('').then((branchList) => {
        this.commitTimeList = []
        if (Array.isArray(branchList) && branchList.length) {
          this.commitBranch = branchList.find(item => item == 'master') || branchList[0]
          this.getCommitTimeList(this.commitBranch)
        } else {
          this.commitBranch = ''
        }
      })
    },
    // 获取options列表
    getCommitOptions (search) {
      console.log('getCommitOptions', this.projectForm)
      return service({
        url: '/project/branch',
        method: 'POST',
        data: qs.stringify({
          group: this.projectForm.gitGroup,
          name: this.projectForm.gitName,
          search: search
        })
      }).then(branchList => {
        this.commitOptions = branchList
        return branchList
      })
    },
    // 获取时间线列表
    getCommitTimeList (val) {
      this.commitTimeList = []
      service({
        url: '/project/commits',
        method: 'POST',
        data: qs.stringify({
          group: this.projectForm.gitGroup,
          name: this.projectForm.gitName,
          branch: val
        })
      }).then(res => {
        res.forEach(item => {
          item.sliceCommitId = item.id.substr(0, 6)
          item.timestamp = item.committed_date
        })
        res[0].color = '#409EFF'
        this.commitTimeList = res
      })
    },
    searchBranch: throttle(function (query) {
      if (query != '') {
        this.loading = true
        this.getCommitOptions(query).then(() => {
          this.loading = false
        })
      }
    }),
    selectedBranch (branch) {
      this.getCommitTimeList(branch)
    }
  }
}
</script>

<style lang="scss" scoped>
.no-token {
  padding: 8px 16px;
  border-radius: 4px;
  background-color: #fdf6ec;
  color: #E6A23C;
  margin-right: 10px;
}
.project-commit {
  // margin-left: 18px;
  height: 90%;
  /deep/ .el-form .el-form-item .el-form-item__label {
    font-size: 13px;
  }
}
.commit-timeline {
  height: 90%;
  overflow-y: scroll;
  padding-top: 5px;
  padding-left: 0px;
  &::-webkit-scrollbar{
    display: none;
  }
  /deep/.el-timeline-item__tail{
    left: 5px;
  }
  /deep/.el-timeline-item__node--normal{
    left: 0px;
  }
}
</style>
<style>
.project-commit .el-form-item__label {
  font-size: 14px;
  color: #909399;
  font-weight: bold;
  margin-bottom: 0px;
}
</style>
