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
      <el-table stripe :data="list" class='table-list'>
        <el-table-column prop="id" label="id" width="100px"></el-table-column>
        <el-table-column prop="name" label="项目" width="120px"></el-table-column>
        <el-table-column prop="profile" label="profile" width="120px"></el-table-column>
        <el-table-column prop="stepTxt" label="编译阶段" width="120px"></el-table-column>
        <el-table-column label="状态" width="120px">
          <template slot-scope="scope">
            <el-tag :type="scope.row.statusType">{{scope.row.statusTxt}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="url" width="100px">
          <template slot-scope="scope">
            <a v-if="scope.row.url" class="el-button el-button--text" :href="scope.row.url">
              <span v-if="scope.row.step==4">jar包</span>
              <span v-else>构建日志</span>
            </a>
          </template>
        </el-table-column>
        <el-table-column prop="ctimeFormat" label="创建时间" width="160px"></el-table-column>
        <el-table-column prop="utimeFormat" label="更新时间" width="160px"></el-table-column>
        <el-table-column label="操作" width="180px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.deployStatus == 1" type="info">处理中...</el-tag>
            <el-button
              v-if="scope.row.deployStatus == 3"
              size="mini"
              type="primary"
              @click="showDeployDialog(scope.row)"
            >部署</el-button>
            <el-button
              v-if="scope.row.deployStatus == 2"
              size="mini"
              type="danger"
              @click="undeployProject(scope.row)"
            >下线</el-button>
            <el-button
              v-if="scope.row.deployStatus == 2"
              size="mini"
              type="primaey"
              @click="showProjectDeploymentInfo(scope.row)"
            >信息</el-button>
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
    </d2-module>

     <el-dialog title="部署项目" :visible.sync="deployDialogVisible">
        <el-form :form="deployForm" label-width="120px">
          <el-form-item label="部署路径">
            <el-input v-model="deployForm.servicePath" />
          </el-form-item>
          <el-form-item label="代理节点">
            <el-select v-model="deployForm.address">
              <el-option v-for="item in clientList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="deployProject">部署</el-button>
            <el-button @click="deployDialogVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
     </el-dialog>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index";
import bizutil from "@/common/bizutil";
import { taskStep, status as statusMap } from "./status-map";
import qs from "qs";
import SockJS from "sockjs-client";

export default {
  data() {
    return {
      projectId: this.$route.query.id,
      name: this.$route.query.name,
      deployDialogVisible: false,
      deployForm: {},
      list: [],
      clientList: [],
      total: 0,
      page: 1,
      pageSize: 10,
      pageDisabled: false
    };
  },
  created() {
    this.getList();
    this.getClientList();
    this.initWebSocket();
  },
  beforeDestroy() {
    this.socket.close();
  },
  methods: {
    initWebSocket() {
      let socket = (this.socket = new SockJS(
        `//${window.location.host}/ws/cicd/`
      ));
      socket.onopen = () => {
        console.log("Socket 已打开");
      };
      //获得消息事件
      socket.onmessage = msg => {
        const data = JSON.parse(msg.data || "{}");
        console.log(msg);
        if (data.msgType == "ProjectCompilationUpdate") {
          // 服务器通知更新
          this.getList();
        }
      };
      //关闭事件
      socket.onclose = () => {
        console.log("Socket已关闭");
      };
      //发生了错误事件
      socket.onerror = () => {
        console.log("Socket发生了错误");
      };
    },
    getList() {
      this.needFresh = false;
      this.pageDisabled = true;
      const projectId = this.projectId;
      const page = this.page;
      const pageSize = this.pageSize;
      return request({
        url: `/project/compilation/list?id=${projectId}&page=${page}&pageSize=${pageSize}`
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        this.total = res.total;
        this.pageDisabled = false;
        this.list = res.list.map(item => {
          this.needFresh = item.status == 1;
          return {
            ...item,
            name: this.name,
            stepTxt: taskStep[item.step].name,
            stepType: taskStep[item.step].type,
            statusTxt: statusMap[item.status].name,
            statusType: statusMap[item.status].type,
            ctimeFormat: bizutil.timeFormat(item.ctime),
            utimeFormat: bizutil.timeFormat(item.utime)
          };
        });
        return this.needFresh;
      });
      setTimeout(() => {
        this.pageDisabled = false;
      }, 2000);
    },
    getClientList() {
      request({
        url: "/dpagent/list"
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        this.clientList = res.list.map(item => item.slice(1));
      });
    },
    showDeployDialog(item) {
      const paramSetting = JSON.parse(item.paramSetting || "{}");
      paramSetting.servicePath =
        paramSetting.servicePath || `xxxx/${this.name}/`;
      this.deployForm = {
        ...item,
        ...paramSetting
      };
      this.deployDialogVisible = true;
    },
    deployProject(item) {
      const deployForm = this.deployForm;
      const url = this.url;
      const projectId = this.projectId;
      request({
        url: "/project/deploy",
        method: "post",
        data: qs.stringify({
          projectId,
          id: deployForm.id,
          address: deployForm.address,
          jarName: deployForm.jarName,
          downloadKey: deployForm.jarKey,
          servicePath: deployForm.servicePath,
          paramSetting: JSON.stringify(deployForm)
        })
      }).then(res => {
        this.$message.success("部署处理中");
        this.deployDialogVisible = false;
        this.getList();
      });
    },
    undeployProject(item) {
      this.$confirm("再次确认下线操作?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          const deployForm = JSON.parse(item.paramSetting || "{}");
          const projectId = this.projectId;
          request({
            url: "/project/undeploy",
            method: "post",
            data: qs.stringify({
              projectId,
              id: deployForm.id,
              address: deployForm.address,
              jarName: deployForm.jarName,
              downloadKey: deployForm.jarKey,
              servicePath: deployForm.servicePath
            })
          }).then(res => {
            this.$message.success("线下处理中");
            this.getList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作"
          });
        });
    },
    showProjectDeploymentInfo(item) {
      const deployForm = JSON.parse(item.paramSetting || "{}");
      const projectId = this.projectId;
      request({
        url: "/application/deploy/info",
        method: "post",
        data: qs.stringify({
          projectId,
          id: deployForm.id,
          address: deployForm.address,
          jarName: deployForm.jarName,
          downloadKey: deployForm.jarKey,
          servicePath: deployForm.servicePath
        })
      }).then(res => {
        console.log(res);
      });
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getList();
    }
  }
};
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
    color: #409eff;
  }
}
</style>
