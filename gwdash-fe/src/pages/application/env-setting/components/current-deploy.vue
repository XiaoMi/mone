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
    <div class="header">
      <el-button
        v-if="healthStatus == 3 || healthStatus == 1"
        @click="closeHealthCheck"
        type="danger"
        size="mini">关闭监测</el-button>
      <el-button
        v-else-if="healthStatus != 0"
        @click="openHealthCheck"
        type="success"
        size="mini">开启监测</el-button>
      <el-button
        @click="openExtendMachineDialog"
        type="primary"
        size="mini">扩展机器</el-button>
      <el-button @click="getCurrenRelease" size="mini">刷新部署列表</el-button>
    </div>

    <el-table stripe :data='tableList' class='table-list'>
      <el-table-column label='机器名' prop='name' width='160' show-overflow-tooltip></el-table-column>
      <el-table-column label='ip' prop='ip' width='160'></el-table-column>
      <el-table-column label='hostname' prop='hostname' width='160' show-overflow-tooltip></el-table-column>
      <el-table-column label='状态' width='100'>
        <template slot-scope="scope">
          <el-tag 
            size='mini'
            style="width:60px"
            :type="`${scope.row.isOnline ? 'success' : 'warning'}`">{{scope.row.isOnline ? '在线' : '不在线'}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label='信息' width='200' show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if='scope.row.info.data.length > 0'>{{scope.row.info.data}}</span>
        </template>
      </el-table-column>
      <el-table-column label='操作' fixed='right' width='270'>
        <template slot-scope="scope">
          <el-button 
              class='danger'
              size='mini'
              type='danger'
              @click="machineOnline(scope.row)">上线</el-button>
          <el-button
              class='danger'
              size='mini'
              type='danger'
              @click="machineOffine(scope.row)">下线</el-button>
          <el-button
              class='danger'
              size='mini'
              type='danger'
              @click="machineNuke(scope.row)">nuke</el-button>
            <el-dropdown class="el-dropdown-styled"  size="mini"  @command="commandHandler($event,scope.row)">
                <el-button class="el-button--blue" >
                  更多<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="showResource">查看资源</el-dropdown-item>
                  <el-dropdown-item command="checkLog">查看日志</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
          <!-- <el-button
              size='mini'
              @click='checkLog(scope.row)'>查看日志</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title='日志信息' :visible.sync='dialogInfoVisible' width='800px'>
      <div class="logpath">
        <el-input
          class="logpath_input"
          size="small"
          v-model="logPath"  
          placeholder="xxxx/log/filename.log"></el-input>
        <el-button
          type="primary"
          size="mini"
          @click="showSystemLog">查看</el-button>
      </div>
      <div class="systme_pannel">
        <SystemLog :ip="ip" :logPath="logPath" v-if="showLogPannel"></SystemLog>
      </div>
    </el-dialog>
    <el-dialog title="选择扩展机器" :visible.sync="dialogFormVisible">
      <el-table :data="extensionMachines" style="width: 100%" @select-all="handleMachineSelect" @select="handleMachineSelect">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="name" label="机器名" width="160" />
        <el-table-column prop="hostname" label="hostname" width="160" />
        <el-table-column prop="ip" label="ip" width="160" />
        <el-table-column prop="desc" label="描述" />
      </el-table>
      <div class="footer" style="margin-top:10px;text-align:right">
        <el-button type="primary" size="mini" :disabled="!selectedMachines.length" @click="extendMachine">立即扩展</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import request from "@/plugin/axios/index";
import SystemLog from "./system-log.vue";
import qs from "qs";
import bizutil from '@/common/bizutil'

export default {
  data() {
    return {
      tableList: [],
      tableCostList:[],
      dialogInfoVisible: false,
      logInfo: {},
      healthStatus: "",
      extensionMachines: [],
      selectedMachines: [],
      dialogFormVisible: false,
      ip:"",
      logPath:"",
      showLogPannel : false,
    };
  },
  created() {
    this.getCurrenRelease();
    this.getHealthStatus();
  },
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [String, Number],
      required: true
    }
  },
  methods: {
    getCurrenRelease() {
      request({
        url: "/project/env/current/release",
        method: "post",
        data: qs.stringify({
          envId: this.envId
        })
      }).then(res => {
        if (Array.isArray(res)) {
          this.tableList = res.map( item => {
            let info = JSON.parse(item.info || '{"data": []}');
            let isOnline = info && info.data && info.data.length;
            return {
              ...item,
              info,
              isOnline
            }
          })
        }
      })
    },
    commandHandler(cmd,row){
     switch(cmd){
       case "showResource":
         this.showResource(row)
         break;
      case "checkLog":
        this.checkLog(row)
        break;
      default:break;
     }
    },
    showResource(row){
     request({
       url:`/resource/getByIp?ip=${row.ip}`,
       method:"POST"
     })
     .then(res=>{
       try {
         this.codeMirrorResource=JSON.stringify(res,null,2)
       } catch (error) {
         this.$message.warn("数据解析失败")
       }
     })
    this.dialogResourceVisiable=true;
    },
    checkLog(info) {
      this.logInfo = info;
      this.showLogPannel=false      
      this.dialogInfoVisible = true;
    },
    getHealthStatus () {
      request({
        url: "/project/env/health/status",
        method: "post",
        data: qs.stringify({
          envId: this.envId
        })
      }).then(status => {
        this.healthStatus = status
      });
    },
    machineOnline(item) {
      this.$confirm("此操作将触发上线动作, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: "/project/env/current/online",
            method: "post",
            data: qs.stringify({
              envId: this.envId,
              ip: item.ip
            })
          }).then(isSuccess => {
            if (isSuccess) {
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作"
          });
        });
    },
    machineOffine(item) {
      this.$confirm("此操作将触发下线动作, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: "/project/env/current/offline",
            method: "post",
            data: qs.stringify({
              envId: this.envId,
              ip: item.ip
            })
          }).then(isSuccess => {
            if (isSuccess) {
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作"
          });
        });
    },
    machineNuke(item) {
      this.$confirm("此操作将下线及删除应用, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: "/project/env/current/nuke",
            method: "post",
            data: qs.stringify({
              envId: this.envId,
              ip: item.ip
            })
          }).then(isSuccess => {
            if (isSuccess) {
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作"
          });
        });
    },
    openExtendMachineDialog() {
      this.extensionMachines = [];
      request({
        url: "/project/env/obtain/extension/machines",
        method: "post",
        data: qs.stringify({
          envId: this.envId
        })
      }).then(list => {
        if (Array.isArray(list)) {
          this.extensionMachines = list;
          this.dialogFormVisible = true;
        }
      });
    },
    handleMachineSelect(selectedMachines) {
      this.selectedMachines = selectedMachines;
    },
    extendMachine() {
      const selectedMachines = this.selectedMachines;
      if (selectedMachines && selectedMachines.length) {
        request({
          url: "/project/env/extend/machines",
          method: "post",
          data: {
            envId: this.envId,
            machineBoList: selectedMachines
          }
        }).then(success => {
          if (success) {
            this.dialogFormVisible = false;
            this.getCurrenRelease();
          } else {
            this.$message({
              type: "error",
              message: "可扩展机失败，稍后重试"
            });
          }
        });
      } else {
        this.$message({
          type: "error",
          message: "可扩展机器空"
        });
      }
    },
    openHealthCheck () {
      request({
        url: "/project/env/health/check/open",
        method: "post",
        data: qs.stringify({
          envId: this.envId
        })
      }).then(isSuccess => {
        if (isSuccess) {
          this.getHealthStatus()
          this.$message({
            type: 'success',
            message: '打开监控成功'
          })
        }
      });
    },
    closeHealthCheck () {
      request({
        url: "/project/env/health/check/close",
        method: "post",
        data: qs.stringify({
          envId: this.envId
        })
      }).then(isSuccess => {
        if (isSuccess) {
          this.getHealthStatus()
          this.$message({
            type: 'success',
            message: '关闭监控成功'
          })
        }
      });
    },
    checkLogPath(){
     return this.logPath.startsWith("xxxx/log/")
    },
    showSystemLog(item){
      if(!this.checkLogPath()){
        this.$message.error("只能查看xxxx/log/的文件")
        return
      }
      let info = this.logInfo;
      this.ip =`${info.ip}:${info.agentPort}`
      if(this.showLogPannel){
        this.showLogPannel=false;
        setTimeout(_=>{
          this.showLogPannel=true;
        },500)
      }else{
      this.showLogPannel=true;
        
      }
    }
  },
  components:{
    SystemLog
  }
};
</script>
<style lang="scss" scoped>
.header {
  margin-bottom: 20px;
  text-align: right;
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
.logpath {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  &_input {
    width: 200px;
    margin-right: 20px;
  }
}
.systme_pannel {
  height: 500px;
  margin-top: 20px
}
.el-dropdown-styled{
  margin-left: 10px;
}
</style>
<style lang='css'>
  .el-tooltip__popper.is-dark {
    max-width: 800px;
    word-break: break-all;
  }
</style>