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
    <template>
      <d2-module margin-bottom :padding-bottom="false">
        <div class="header">
          <div>
             <el-form class="inline-form" :inline="true" :model="form" size="mini">
                <el-form-item label="审核状态">
                  <el-select v-model="form.status" placeholder="请选择">
                    <el-option label="全部" value="0" />
                    <el-option label="待审核" value="1"  />
                    <el-option label="审核通过" value="2" />
                    <el-option label="审核未通过" value="3" />
                  </el-select>
                </el-form-item>
             </el-form>
          </div>
          <div>
            <el-button @click="onQuery" size='mini'>查询</el-button>
            <el-button @click="onTongbu" size='mini' style='margin-left:10px'>同步redis</el-button>
          </div>
        </div>
      </d2-module>
      <d2-module>
        <div class="table-list">
          <el-table :data="pluginList" class="table-list">
            <el-table-column prop="id" label="id" width="50"></el-table-column>
            <el-table-column label="name" width="150">
              <template slot-scope="scope">
                <span>{{scope.row.cname || scope.row.name}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="version" label="版本" width="100"></el-table-column>
            <el-table-column prop="author" label="作者" width="180"></el-table-column>
            <el-table-column prop="creator" label="上传者" width="100"></el-table-column>
            <el-table-column prop="ctime" label="创建时间" width="180"></el-table-column>
            <el-table-column prop="utime" label="更新时间" width="180"></el-table-column>
            <el-table-column prop="desc" label="描述" width="160" show-overflow-tooltip></el-table-column>
            <el-table-column label="构建状态" fixed="right" width='100'>
              <template slot-scope="scope">
                <el-tag
                  v-if="!scope.row.projectCompileRecord"
                  size="small"
                  type="info">
                    待构建
                    <el-link
                      :underline="false"
                      @click="bulidCode(scope.row.id)"
                      type="info"
                      class="el-icon-video-play"></el-link>
                </el-tag>
                <el-tag
                  v-else-if="scope.row.projectCompileRecord.status === 2"
                  size="small"
                  type="danger">
                    失败
                    <el-link
                      :underline="false"
                      @click="bulidCode(scope.row.id)"
                      type="danger"
                      class="el-icon-refresh-right"></el-link>
                    <el-link
                      :underline="false"
                      @click="showBuildLog(scope.row)"
                      type="danger"
                      class="el-icon-tickets"></el-link>
                  </el-tag>
                <el-tag
                  v-else-if="scope.row.projectCompileRecord.status === 1"
                  size="small"
                  type="success">成功</el-tag>
                <el-tag
                  v-else
                  size="small"
                  type="warning">
                    构建中
                    <el-link
                      :underline="false"
                      @click="showBuildLog(scope.row)"
                      type="warning"
                      class="el-icon-tickets"></el-link>
                  </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template slot-scope="scope">
                <div style='display:inline-block'>
                  <el-button
                    class='operate audit'
                    @click="showAudit(scope.row)"
                    :type="scope.row.statusType">{{scope.row.statusTxt}}</el-button>
                  <el-button
                    class='operate'
                    @click="updateOnlineStatus(scope.row.id, '/filter/offline', '关闭')"
                    v-if="scope.row.onlineStatus == 1"
                    type="success">启用</el-button>
                  <el-button
                    class='operate'
                    v-else
                    @click="updateOnlineStatus(scope.row.id, '/filter/online', '开启')"
                    type="danger">未启用</el-button>
                </div>
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

      <el-dialog title="审核详情" :visible="dialogVisible" @close="cancelDialog" width="800px">
        <el-form label-width="80px" size="mini">
          <el-form-item label="name">
            <el-input disabled v-model="auditInfo.name" />
          </el-form-item>
          <el-form-item label="作者">
            <el-input disabled v-model="auditInfo.author" />
          </el-form-item>
          <el-form-item label="版本">
            <el-input disabled v-model="auditInfo.version" />
          </el-form-item>
          <el-form-item label="git地址">
            <a :href="`${auditInfo.gitAddress}/tree/${auditInfo.commitId || 'master'}`" target="_blank">
              <el-button type="text">查看源码</el-button>
            </a>
            <el-input disabled v-model="auditInfo.gitAddress" />
          </el-form-item>
          <el-form-item label="参数描述">
            <el-input type="textarea" rows="3" disabled v-model="auditInfo.params" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" rows="3" disabled v-model="auditInfo.desc" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <div v-if="auditInfo.projectCompileRecord
            && auditInfo.projectCompileRecord.status === 1
            && auditInfo.projectCompileRecord.step === 4">
          <el-popover placement="top" v-model="delVisible">
            <el-alert
              title="该操作将删除这条记录，且不可恢复，请三思呀～"
              type="warning"
              effect="dark"
              :closable="false"/>
              <div style="text-align: right; margin: 0; margin-top: 10px;">
                <el-button size="mini" type="text" @click="delVisible = false">我再想想～</el-button>
                <el-button type="danger" size="mini" @click="audit(auditInfo.id, '/filter/real/delete')">残忍删除</el-button>
              </div>
            <el-button slot="reference" type="danger" size="mini">删除</el-button>
          </el-popover>
          <el-button
             v-if="auditInfo.status != 3"
             @click="audit(auditInfo.id, '/filter/reject')"
             size="mini"
             style="margin-left:10px">审核不通过</el-button>
          <el-button
             v-if="auditInfo.status != 2"
             @click="audit(auditInfo.id, '/filter/effect')"
             size="mini"
             type="primary"
             style="margin-left:10px">审核通过且启用</el-button>
          </div>
          <div v-else>
            <el-button
              style="margin-right:10px"
              type="primary"
              @click="bulidCode(auditInfo.id)">构建</el-button>
            </div>
          </div>
      </el-dialog>

      <el-dialog title='构建日志' :visible.sync='dialogBuildLogVisible' width='800px' :before-close="dislogClose">
          <codemirror v-model="codeMirrorBuildLog" :options="codeMirrorOptions" class='codeMirror_buildlog'/>
      </el-dialog>
    </template>
  </d2-container>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'
import { mapState } from 'vuex'
import bizutil from '@/common/bizutil'
import statusMap from '../status_map'
import { pjPre } from '../../../wiki/constants/type_info'
import "codemirror/mode/javascript/javascript.js"
import "codemirror/theme/base16-dark.css"

export default {
  data () {
    return {
      isOnline: !!(serverEnv === 'c3' || serverEnv === 'c4' || serverEnv === 'intranet'),
      filterPre: pjPre.filter,
      total: 0,
      page: 0,
      pageSize: 20,
      dialogVisible: false,
      auditInfo: {},
      delVisible: false,
      form: {
        status: '1'
      },
      pluginList: [],
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
  computed: {
    ...mapState('d2admin/user', ['info'])
  },
  created () {
    if (this.info.role === 1) {
      this.getPluginList()
    }
  },
  methods: {
    // 构建
    bulidCode (id) {
      request({
        url: '/filter/build',
        method: 'POST',
        data: qs.stringify({ id })
      }).then(res => {
        this.$message({
          message: '构建过程中~ 请耐心等待~',
          type: 'success'
        })
        this.dialogVisible = false
        this.getPluginList()
      })
    },
    getPluginList () {
      const page = this.page
      const pageSize = this.pageSize
      const status = this.form.status
      this.pageDisabled = true
      const url = `/filter/list?page=${page}&pageSize=${pageSize}&status=${status}`
      request({ url }).then(
        res => {
          this.page = res.page
          this.total = res.total
          this.pluginList = res.pluginList.map(item => {
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
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    onQuery () {
      this.page = 0
      this.getPluginList()
    },
    onTongbu () {
      request({
        url: '/filter/redis/flesh',
        method: 'post'
      }).then(
        data => {
          if (data) {
            this.getPluginList()
            this.$message.success('操作成功')
            this.dialogVisible = false
          } else {
            this.$message.error('操作失败')
          }
        }
      )
    },
    showAudit (item) {
      this.auditInfo = { ...item }
      this.dialogVisible = true
    },
    updateOnlineStatus (id, url, msg) {
      this.$confirm(`将${msg}过滤器, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url,
          method: 'post',
          data: qs.stringify({
            id
          })
        }).then(
          data => {
            if (data) {
              this.getPluginList()
              this.$message.success('操作成功')
            } else {
              this.$message.error('操作失败')
            }
          }
        )
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    audit (id, url) {
      request({
        url,
        method: 'post',
        data: qs.stringify({
          id
        })
      }).then(
        data => {
          if (data) {
            this.getPluginList()
            this.$message.success('操作成功')
            this.dialogVisible = false
          } else {
            this.$message.error('操作失败')
          }
        }
      )
    },
    handleCurrentChange (val) {
      this.page = val
      this.getPluginList()
    },
    cancelDialog () {
      this.dialogVisible = false
    },
    showBuildLog (item) {
      const id = item.projectCompileRecord && item.projectCompileRecord.id
      if (!id) return
      request({
        url: `/log/compile?id=${id}`,
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
.header {
  display: flex;
  justify-content: space-between;
}
.inline-form {
  display: flex;
  justify-content: space-between;
}
.link-button {
  a {
    color: #409EFF
  }
}
.d2-layout-header-aside-group .table-list .el-button {
  &.info {
    width: 70px;
    background: #a6a9ad;
    border-color: #a6a9ad;
  }
  &.success {
    width: 70px;
    background-color: #67C23A;
    border-color: #67C23A;
  }
  &.danger {
    width: 70px;
    background-color: #F56C6C;
    border-color: #F56C6C;
  }
}
.operate {
    width: 56px;
    color: #606266
}
.log {
  width: 65px
}
.audit {
  width: 75px
}
.codeMirror_buildlog {
  margin-left: 16px;
  margin-bottom: 20px
}
</style>
<style lang="scss">
.codeMirror_buildlog {
  .CodeMirror {
    height: 500px
  }
}
</style>
