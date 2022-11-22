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
    <div>
      <el-table :data="list" stripe class='table-list'>
        <el-table-column prop="id" label="id" width="80"></el-table-column>
        <el-table-column :label="deployType == 3 ? '镜像地址' : '下载'" width="210" show-overflow-tooltip>
            <template slot-scope="scope">
              <div v-if='deployType == 3' class='jarName'>
                <span style='display:inline-block;width:160px;overflow:hidden'>
                  {{'docker pull ' + scope.row.projectCompileRecord.jarName}}
                </span>
                <span
                  :data-value="`docker pull ${scope.row.projectCompileRecord.jarName}`"
                  class='icon'
                  title='复制'>
                  <i class="el-icon-document-copy" @click='copyJarName'></i>
                </span>
              </div>
              <div v-else>
                <a v-if="scope.row.projectCompileRecord && scope.row.projectCompileRecord.url"
                  :href="scope.row.projectCompileRecord.url">
                  {{scope.row.projectCompileRecord.jarName}}</a>
              </div>
            </template>
        </el-table-column>
        <el-table-column label="查看源码" width="130">
            <template slot-scope="scope">
              <a v-if="scope.row.deploySetting"
                :href="`xx_replace_xx`"
              >{{scope.row.deploySetting.commitId.substr(0, 7)}}</a>
            </template>
        </el-table-column>
        <el-table-column prop="username" label="部署人" width="130"></el-table-column>
        <el-table-column label="部署机器" min-width="160">
          <template slot-scope="scope">
            <div v-if="scope.row.deploySetting">
              <span
                class="deploy-machine"
                v-for="(item, index) in scope.row.deploySetting.envMachineBo || []"
                :key="index"
              >{{item.ip || item.machineBo && item.machineBo.ip}}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="deploySetting.branch" label="部署分支" width="160"></el-table-column>
        <el-table-column label="部署方式" width="160">
          <template slot-scope="scope">
            <div v-if="scope.row.deploySetting">
              <span v-if="scope.row.deploySetting.deployType == 1">物理机</span>
              <span v-else-if="scope.row.deploySetting.deployType == 2">docker</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="utimeFormat" label="更新时间" width="160"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
            <template slot-scope="scope">
              <div style='width:40px;display:inline-block;margin-right:10px'>
                <el-button
                  v-if="scope.row.deployInfo && scope.row.deployInfo.status == 1"
                  size="mini"
                  @click="startRollback(scope.row)">回滚
                </el-button>
              </div>
              <el-button
                v-if='scope.row.projectCompileRecord && scope.row.projectCompileRecord.id'
                size='mini'
                @click="showBuildLog(scope.row)">构建日志</el-button>
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

      <el-dialog
        title='构建日志'
        :visible.sync='dialogBuildLogVisible'
        width='800px'
        class='codeMirror_container'>
          <codemirror v-model="codeMirrorBuildLog" :options="codeMirrorOptions" class='codeMirror_buildlog'/>
      </el-dialog>
    </div>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'
import bizutil from '@/common/bizutil'

import "codemirror/mode/javascript/javascript.js"
import "codemirror/theme/base16-dark.css"
export default {
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [Number, String],
      required: true
    },
    deployType: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      total: 0,
      page: 1,
      pageSize: 20,
      list: [],
      pageDisabled: false,
      dialogBuildLogVisible: false,
      codeMirrorBuildLog: '',
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        mode: "text/javascript",
        theme: "base16-dark",
        readOnly: "nocursor",
        lineNumbers: true,
        lineWrapping: true,
        line: true,
        smartIndent: true
      }
    }
  },
  created () {
    this.getList()
  },
  methods: {
    getList () {
      this.pageDisabled = true
      const page = this.page
      const envId = this.envId
      const pageSize = this.pageSize
      request({
        url: '/project/env/deployment/list',
        method: 'post',
        data: qs.stringify({
          envId,
          page,
          pageSize
        })
      }).then(res => {
        if (Array.isArray(res.list)) {
          this.total = res.total
          this.list = res.list.map(it => {
            it.utimeFormat = bizutil.timeFormat(it.utime)
            return it
          })
        }
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    handleCurrentChange (val) {
      this.page = val
      this.getList()
    },
    startRollback (item) {
      this.$confirm('此操作将回滚到指定版本, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: "/pipeline/present",
          method: "post",
          data: qs.stringify({
            projectId: this.projectId,
            envId: this.envId
          })
        }).then(projectPipeline => {
          if (projectPipeline) {
            this.$message.warning("部署中,稍后尝试")
            return
          }
          this.$router.push(`/application/rollback?id=${this.projectId}&envId=${this.envId}&pipelineId=${item.id}`)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '操作取消'
        })
      })
    },
    copyJarName (e) {
      var text = e.target.parentElement.dataset.value
      if (bizutil.copyText(text)) {
        this.$message({
          type: 'success',
          message: '复制成功'
        })
      }
    },
    showBuildLog (item) {
      const id = item.projectCompileRecord && item.projectCompileRecord.id
      const url = this.deployType === 3 ? '/log/dockerbuild' : '/log/compile'
      if (!id) return
      request({
        url: `${url}?id=${id}`,
        method: 'GET'
      }).then(res => {
        this.codeMirrorBuildLog = res.split('/n').toString()
        this.dialogBuildLogVisible = true
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.deploy-machine {
  padding-right: 10px;
  display: inline-block;
}
.jarName {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .icon {
    cursor: pointer;
  }
}
.icon:hover {
  color: #409EFF
}
</style>
<style lang="scss">
.codeMirror_container {
  .CodeMirror {
    height: 600px
  }
}
</style>
