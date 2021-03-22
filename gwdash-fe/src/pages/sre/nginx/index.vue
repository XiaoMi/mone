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
      <div class='header'>
          <el-form size="mini">
            <el-form-item>
              <el-input v-model="query.serviceName" placeholder="服务名" />
            </el-form-item>
          </el-form>
          <el-button size='mini' @click="getList" style='margin-left:10px'>查询</el-button>
          <el-button size='mini' @click="showDialog({}, '新增')">新增</el-button>
      </div>
    </d2-module>
    <d2-module>
      <div class="table-list">
        <el-table :data="list" style="width:100%">
          <el-table-column prop="id" label="id" width="80"></el-table-column>
          <el-table-column prop="serviceName" label="服务名" min-width="160"></el-table-column>
          <el-table-column prop="upstreamName" label="upstream name" min-width="160"></el-table-column>
          <el-table-column prop="group" label="group" min-width="160"></el-table-column>
          <el-table-column prop="configPath" label="配置路径" min-width="160"></el-table-column>
          <el-table-column prop="ctimeFormat" label="创建时间" width="160"></el-table-column>
          <el-table-column prop="utimeFormat" label="更新时间" width="160"></el-table-column>
          <el-table-column label="操作" width="420" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" @click="deployNacos(scope.row.id)">发布nacos</el-button>
              <el-button type="text" @click="deployNginx(scope.row.id)">发布nginx</el-button>
              <el-button type="text" @click="showDialog(scope.row, '编辑')">编辑</el-button>
              <el-button type="text" @click="detailMachine(scope.row)">查看机器</el-button>
              <el-button type="text" @click="del(scope.row.id)">删除</el-button>
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
    <el-dialog :title="title" :visible.sync="dialogVisible" width="800px">
      <el-form label-width="120px" :model="form" :rules="rules" ref="ruleForm" size="mini">
        <el-form-item label="服务名" prop="serviceName">
          <el-input v-model="form.serviceName" placeholder="服务名" />
        </el-form-item>
        <el-form-item label="upstream name" prop="upstreamName">
          <el-input v-model="form.upstreamName" placeholder="upstream name" />
        </el-form-item>
        <el-form-item label="nginx配置路径" prop="configPath">
          <el-input v-model="form.configPath" placeholder="nginx配置路径" />
        </el-form-item>
        <el-form-item label="group" prop="group">
          <el-input v-model="form.group" placeholder="group" />
        </el-form-item>
        <el-form-item class="form-footer">
          <el-button @click="edit" type="primary">{{title}}</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog
      title="查看机器列表"
      :visible.sync="dialogMachineVisible"
      width="1200px"
      @close="machineList=[]; innerMachineVis=false"
    >
      <el-dialog width="30%" title="upstream 列表" :visible.sync="innerMachineVis" append-to-body>
        <li v-for="(upstream, index) in upstreamList" :key="index">{{upstream}}</li>
      </el-dialog>
      <el-table :data="machineList" class="table-list">
        <el-table-column prop="id" label="id" width="70"></el-table-column>
        <el-table-column prop="name" label="name" width="100"></el-table-column>
        <el-table-column prop="desc" label="desc" width="100"></el-table-column>
        <el-table-column prop="ip" label="ip" width="140"></el-table-column>
        <el-table-column prop="hostname" label="hostname" width="100"></el-table-column>
        <el-table-column prop="order" label="order" width="100"></el-table-column>
        <el-table-column label="labels">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right">
              <div v-if="scope.row.labels" class="label-wrapper">
                <li v-for="(v, k) in scope.row.labels" :key="k">{{k}}: {{v}}</li>
              </div>
              <div v-else>暂无数据</div>
              <span slot="reference" style="margin-right: 10px;">
                <el-tag>label</el-tag>
              </span>
            </el-popover>

            <el-popover trigger="hover" placement="right">
              <div v-if="scope.row.prepareLabels" class="label-wrapper">
                <li v-for="(v, k) in scope.row.prepareLabels" :key="k">{{k}}: {{v}}</li>
              </div>
              <div v-else>暂无数据</div>
              <span slot="reference">
                <el-tag>prepareLabels</el-tag>
              </span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button size="mini" @click="getUpStreamList(scope.row)">upStreamList ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index"
import qs from "qs"
import bizutil from "@/common/bizutil"

export default {
  data () {
    return {
      total: 0,
      page: 1,
      pageSize: 20,
      list: [],
      title: "",
      dialogVisible: false,
      dialogMachineVisible: false,
      innerMachineVis: false,
      machineList: [],
      upstreamList: [],
      form: {},
      rules: {
        serviceName: [
          { required: true, message: "请输入服务名", trigger: "blur" },
          { min: 1, max: 64, message: "长度在 1 到 64 个字符", trigger: "blur" }
        ],
        upstreamName: [
          { required: true, message: "请输入upstream name", trigger: "blur" },
          { min: 1, max: 64, message: "长度在 1 到 64 个字符", trigger: "blur" }
        ],
        configPath: [
          { required: true, message: "请输入nginx配置路径", trigger: "blur" },
          { min: 1, max: 128, message: "长度在 1 到 128 个字符", trigger: "blur" }
        ],
        group: [
          { required: true, message: "请输入 group name", trigger: "blur" },
          { min: 1, max: 64, message: "长度在 1 到 64 个字符", trigger: "blur" }
        ]
      },
      query: {
        serviceName: ""
      },
      pageDisabled: false
    }
  },
  created () {
    this.getList()
  },
  methods: {
    getList () {
      this.pageDisabled = true
      const page = this.page
      const pageSize = this.pageSize
      const serviceName = this.query.serviceName
      let url = `/nginx/list?page=${page}&pageSize=${pageSize}`
      if (serviceName) url = `${url}&serviceName=${serviceName}`
      request({
        url
      }).then(res => {
        if (!Array.isArray(res.list)) return
        this.total = res.total
        this.list = res.list.map(it => {
          return {
            ...it,
            ctimeFormat: bizutil.timeFormat(it.ctime),
            utimeFormat: bizutil.timeFormat(it.utime)
          }
        })
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    showDialog (form, title) {
      this.form = { ...form }
      this.title = title
      this.dialogVisible = true
    },
    edit () {
      const form = { ...this.form }
      let url = "/nginx/new"
      if (form.id != null) url = "/nginx/edit"
      this.$refs.ruleForm.validate(valid => {
        if (!valid) {
          return false
        }
        request({
          url,
          method: "post",
          data: qs.stringify(form)
        }).then(res => {
          this.$message.success("操作成功")
          this.getList()
          this.dialogVisible = false
        })
      })
    },
    detailMachine (row) {
      request({
        url: `/nginx/machine?group=${row.group}`
      }).then(res => {
        this.dialogMachineVisible = true
        if (res.length > 0) {
          this.machineList = res.map(item => {
            item.configPath = row.configPath
            item.upstreamName = row.upstreamName
            return item
          })
        }
      })
    },
    getUpStreamList (row) {
      const { id, ip } = row

      const machine = this.machineList.filter(item => item.id === id)

      const { configPath, upstreamName } = machine[0]

      console.log(this.machineList)
      console.log(machine)
      console.log(row)

      request({
        url: `/nginx/upstreamNameDetail?ip=${row.ip}&upstreamName=${upstreamName}&configPath=${configPath}`
      }).then(res => {
        this.innerMachineVis = true
        this.upstreamList = res
      })
    },
    del (id) {
      this.$confirm("此操作将永久删除该机器, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: `/nginx/del?id=${id}`
          }).then(res => {
            if (res) {
              this.$message.success("删除成功")
              this.getList()
            }
          })
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          })
        })
    },
    deployNacos (id) {
      this.$confirm("确定发布到nacos?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: `/nginx/deploy/nacos?id=${id}`
          }).then(res => {
            if (res) {
              this.$message.success("发布成功")
            }
          })
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消"
          })
        })
    },
    deployNginx (id) {
      this.$confirm("确定发布到nginx?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: `/nginx/deploy/nginx?id=${id}`
          }).then(res => {
            if (res) {
              this.$message.success("发布成功")
            }
          })
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消"
          })
        })
    },
    handleCurrentChange (val) {
      this.page = val
      this.getList()
    },
    // 新增机器标签
    addMachineLabel () {
      this.form.machineLabels.push({})
    },
    // 删除机器标签
    delMachineLabel (index) {
      this.form.machineLabels.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
  height: 30px;
}
.form-footer {
  text-align: right;
}
.label-wrapper {
  width: 400px;
}
</style>
