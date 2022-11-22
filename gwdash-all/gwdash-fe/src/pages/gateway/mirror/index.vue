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
          @click='refresh'>刷新</el-button>
        <el-button
          size='mini'
          @click='handleDialog("新增","/docker/image/new")'>新增</el-button>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data='mirrorList' class='table-list'>
        <el-table-column  label='id' prop='id' width="50"></el-table-column>
        <el-table-column label='镜像名' prop='imageName' width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-if='scope.row.buildType === "success"' class='jarName'>
              <span style='display:inline-block;width:100px;overflow:hidden'>{{'docker pull ' + scope.row.imageName}}</span>
              <span
                :data-value="`docker pull ${scope.row.imageName}`"
                class='icon'
                title='复制'>
                <i class="el-icon-document-copy" @click='copyJarName'></i>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label='path' prop='path' width="160"></el-table-column>
        <el-table-column label='commitId' prop='commitId' width="160" show-overflow-tooltip>
           <template slot-scope="scope">
            <div style='width:120px;overflow:hidden'>{{scope.row.commitId}}</div>
          </template>
        </el-table-column>
        <el-table-column label='创建者' prop='creator' width="80"></el-table-column>
        <el-table-column label='描述' prop='desc' width="160" show-overflow-tooltip>
          <template slot-scope="scope">
            <div style='width:120px;overflow:hidden'>{{scope.row.desc}}</div>
          </template>
        </el-table-column>
        <el-table-column label='创建时间' prop='ctime' width="160"></el-table-column>
        <el-table-column label='更新时间' prop='utime' width="160"></el-table-column>
        <el-table-column label="审核状态" width="90" fixed="right">
          <template slot-scope="scope">
            <el-tag
              size='mini'
              style="width:60px"
              :type="scope.row.auditType">{{scope.row.auditText}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="构建状态" width="80" fixed="right">
          <template slot-scope="scope">
            <el-tag
              size='mini'
              v-if='scope.row.auditType === "success"'
              style="width:60px"
              :type="scope.row.buildType">{{scope.row.buildText}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label='操作' width='220' fixed='right'>
          <template slot-scope='scope'>
            <div style='display:inline-block; width:40px; margin-right:10px'>
              <el-button
               v-if='scope.row.auditType === "success" && scope.row.buildType !== "success" && scope.row.buildType !== "warning"'
               size='mini'
               @click='buildDocker(scope.row.id)'>构建</el-button>
            </div>
            <div style='display:inline-block'>
              <el-button
                size='mini'
                @click='handleDialog("编辑","/docker/image/update",scope.row)'>编辑</el-button>
              <el-button
                v-if='isAdmin'
                size='mini'
                @click='deleteDocker(scope.row.id)'>删除</el-button>
              <el-popover
                v-if='isAdmin'
                placement="right"
                width="150"
                style="margin-left: 10px"
                :ref="refNamePopover + scope.row.id">
                <el-tag
                    style="width:60px; text-align:center; margin-left:10px; cursor:pointer"
                    type='success'
                    @click='auditDocker("/docker/image/effect",scope.row.id)'>通过</el-tag>
                <el-tag
                    style="width:60px; margin-left:10px; cursor:pointer"
                    type='danger'
                    @click='auditDocker("/docker/image/reject",scope.row.id)'>不通过</el-tag>
                <el-button slot="reference">审核</el-button>
              </el-popover>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='currentPage'
        :pageSize='pageSize'
        :total='total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
    </d2-module>

    <el-dialog :title='formTitle' :visible.sync='formVisible' width='800px' class='mirror-dialog'>
      <el-form :model='form' :rules='rules' ref='form' size='mini' label-width="120px">
          <el-form-item label="git地址" class='gitaddress'>
            <el-input value="xx_replace_xx" disabled style="width:30%"></el-input>
            <div style="display:inline-block; width:25%">
              <el-form-item prop="groupName">
                <el-input
                   v-model="form.groupName"
                   placeholder="请输入组名"
                   :disabled='formTitle === "编辑镜像"'></el-input>
              </el-form-item>
            </div>
            <el-input value="/" disabled style="width:6%"></el-input>
            <div style="display:inline-block; width:25%">
              <el-form-item prop="projectName">
                <el-input
                    v-model="form.projectName"
                    placeholder="请输入项目名"
                    :disabled='formTitle === "编辑镜像"'></el-input>
              </el-form-item>
            </div>
          </el-form-item>
          <el-form-item label='commitId' prop='commitId'>
            <el-select
                v-model='form.commitId'
                style='width:45%'
                :disabled='formTitle === "编辑镜像"'
                filterable>
              <el-option
                v-for='item in commitList'
                :key='item.value'
                :label='item.value'
                :value='item.value'
                class='commitOption'>
                <span>{{ item.value.slice(0,10) }}</span>
                <span>{{ item.label }}</span>
                <span>{{ item.time }}</span>
               </el-option>
            </el-select>
            <div v-if='formTitle !== "编辑镜像"' style='display:inline-block'>
               <span v-if="commitLoading" @click="getCommitList" title='拉取commitId'>
                <i class="el-icon-refresh" style="font-size:19px; margin-left: 6px; cursor:pointer"></i>
               </span>
               <span v-else>
                <i class="el-icon-loading" style="font-size:19px; margin-left:6px"></i>
               </span>
            </div>
          </el-form-item>
          <el-form-item label='描述' prop='desc'>
            <el-input
                style="width:86%"
                type="textarea"
                :rows="3"
                v-model="form.desc"
                placeholder="请输入相关描述"
                maxlength="100"
                show-word-limit></el-input>
          </el-form-item>
      </el-form>
      <span slot='footer' class='dialog-footer'>
        <el-button size='mini' @click="formVisible = false">取 消</el-button>
        <el-button size='mini' type="primary" @click="submitFormUpload('form')">确 定</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import time2Date from "@/libs/time2Date"
import bizutil from '@/common/bizutil'
import qs from "qs"
import auditMap from './audit_map'
import buildMap from './build_map'

const isAdmin = !!(userInfo && userInfo.role === 1)

export default {
  name: 'mirror',
  data () {
    return {
      isAdmin,
      mirrorList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      pageDisabled: false,
      formTitle: '',
      formVisible: false,
      uploadUrl: '',
      form: {},
      commitList: [],
      commitLoading: true,
      refNamePopover: 'popover-',
      rules: {
        groupName: [
          { required: true, message: "请输入groupName", trigger: "blur" }
        ],
        projectName: [
          { required: true, message: "请输入projectName", trigger: "blur" }
        ],
        commitId: [
          { required: true, message: "请选择commitId", trigger: "blur" }
        ],
        desc: [
          { required: true, message: "请输入描述", trigger: "blur" }
        ]
      }
    }
  },
  created () {
    this.getMirrorList()
  },
  methods: {
    getMirrorList () {
      this.pageDisabled = true
      service({
        url: `/docker/image/list?page=${this.currentPage}&pageSize=${this.pageSize}`,
        method: 'GET'
      }).then(res => {
        this.currentPage = res.page
        this.total = res.total
        this.pageDisabled = false
        this.mirrorList = res.dockerImageList.map(item => {
          return {
            ...item,
            ctime: time2Date(item.ctime),
            utime: time2Date(item.utime),
            path: `${item.groupName}/${item.projectName}`,
            imageName: item.buildRecord ? item.buildRecord.jarName : '',
            auditType: auditMap[item.status].type,
            auditText: auditMap[item.status].name,
            buildType: item.buildRecord ? buildMap[item.buildRecord.status].type : 'info',
            buildText: item.buildRecord ? buildMap[item.buildRecord.status].name : '待构建'
          }
        })
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    handleDialog (title, url, row) {
      this.formTitle = `${title}镜像`
      this.uploadUrl = url
      this.formVisible = true
      if (row) {
        this.form = {
          ...row
        }
      }
    },
    submitFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: "请检查参数",
            type: "warning"
          })
          return false
        }
        var form = this.form
        service({
          url: this.uploadUrl,
          method: 'POST',
          data: qs.stringify({
            gitAddress: `https://xx_replace_xx/${form.groupName}/${form.projectName}`,
            ...form
          })
        }).then(res => {
          this.formVisible = false
          this.form = {}
          this.$message({
            message: `${this.formTitle}成功`,
            type: "success"
          })
          this.getMirrorList()
        })
      })
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.getMirrorList()
    },
    getCommitList () {
      var form = this.form
      if (form.groupName && form.projectName) {
        this.commitLoading = false
        var gitAddress = `xx_replace_xx/${form.groupName}/${form.projectName}`
        service({
          url: `/docker/image/commits?gitAddress=${gitAddress}`,
          method: 'GET'
        }).then(res => {
          this.commitList = res.map(item => {
            return {
              label: item.message.length > 20 ? `${item.message.slice(0, 20)}...` : item.message,
              value: item.id,
              time: item.committed_date
            }
          })
          this.commitLoading = true
        })
      } else {
        this.$message({
          message: "请完成组名、项目名填写",
          type: "warning"
        })
      }
    },
    buildDocker (id) {
      service({
        url: `/docker/image/build`,
        method: 'POST',
        data: qs.stringify({ id })
      }).then(res => {
        if (res) {
          this.$message({
            message: "构建中... 稍后请点击刷新按钮~",
            type: "success"
          })
          this.getMirrorList()
        }
      })
    },
    auditDocker (url, id) {
      setTimeout(() => {
        let refName = this.refNamePopover + id
        this.$refs[refName].doClose()
      }, 500)
      service({
        url,
        method: 'POST',
        data: qs.stringify({ id })
      }).then(res => {
        if (res) {
          this.$message({
            message: "审核完成",
            type: "success"
          })
          this.getMirrorList()
        }
      })
    },
    deleteDocker (id) {
      this.$confirm("此操作将永久删除该镜像, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: "/docker/image/del",
          method: "post",
          data: qs.stringify({ id })
        }).then(res => {
          this.$message({
            message: "删除成功",
            type: "success"
          })
          this.getMirrorList()
        })
      }).catch(() => {
        this.$message({
          message: "已取消删除",
          type: "warning"
        })
      })
    },
    refresh () {
      this.getMirrorList()
    },
    copyJarName (e) {
      var text = e.target.parentElement.dataset.value
      if (bizutil.copyText(text)) {
        this.$message({
          type: 'success',
          message: '复制成功'
        })
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
.gitaddress .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
  margin-bottom: 0px;
}
.commitOption.el-select-dropdown__item {
  height: 66px;
  border-bottom: 0.5px solid #ccc;
  padding-top: 5px;
  span {
    display: block;
    line-height: 21px;
  }
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
.mirror-dialog .el-textarea .el-input__count {
  right: 22px;
  bottom: 6px;
  height: 18px;
  line-height: 18px;
}
</style>
