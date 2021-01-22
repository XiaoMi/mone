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
    <!--主表格-->
    <template>
      <d2-module margin-bottom>
        <div class="header">
          <div>
            <el-dropdown type="primary" size="mini" plain @command="handleBatch">
              <el-button size="mini">
                批量操作
                <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="1">批量删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div>
            <el-button
              @click="refresh"
              size="mini"
              :disabled="refreshDisabled"
              style="margin-left: 2px"
            >刷新</el-button>
          </div>
        </div>
      </d2-module>
      <d2-module>
        <el-table
          :data="tableData"
          style="width: 100%"
          :row-class-name="tableRowClassName"
          @selection-change="handleSelectionChange"
          class="table-list"
        >
          <el-table-column type="selection" width="45"></el-table-column>

          <el-table-column prop="serverName" label="服务器名称" width="320"></el-table-column>

          <el-table-column prop="ip" label="IP地址" width="200"></el-table-column>

          <el-table-column prop="port" label="端口号" width="160"></el-table-column>

          <el-table-column prop="utime" label="更新时间" width="200"></el-table-column>

          <el-table-column prop="group" label="分组"></el-table-column>

          <el-table-column fixed="right" label="操作" width="220">
            <template slot-scope="scope">
              <el-button @click="editRow(scope.row)" type="text" size="small">编辑</el-button>
              <el-button @click="delRow(scope.row)" type="text" size="small">删除</el-button>
              <el-button @click="showInfo(scope.row)" type="text" size="small">节点详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </d2-module>
    </template>

    <!--编辑-->
    <template>
      <el-dialog :title="editFormTitle" :visible.sync="editFormVisible" width="800px">
        <el-form :model="formEdit" status-icon :rules="rules" ref="formEdit" size="mini">
          <el-form-item label="ID" :label-width="formLabelWidth" prop="id">
            <el-input v-model="formEdit.id" autocomplete="off" readonly="readonly"></el-input>
          </el-form-item>
          <el-form-item label="名称" :label-width="formLabelWidth" prop="serverName">
            <el-input v-model="formEdit.serverName" autocomplete="off" readonly="readonly"></el-input>
          </el-form-item>
          <el-form-item label="IP地址" :label-width="formLabelWidth" prop="ip">
            <el-input v-model="formEdit.ip" autocomplete="off" readonly="readonly"></el-input>
          </el-form-item>
          <el-form-item label="端口号" :label-width="formLabelWidth" prop="port">
            <el-input v-model="formEdit.port" autocomplete="off" readonly="readonly"></el-input>
          </el-form-item>
          <el-form-item label="分组" :label-width="formLabelWidth" prop="group">
            <el-input v-model="formEdit.group" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="handleRowEditCancel" size="mini">取 消</el-button>
          <el-button type="primary" @click="handleRowEdit" size="mini">确 定</el-button>
        </div>
      </el-dialog>
    </template>

    <!-- 节点信息展示 -->
    <template>
      <el-dialog title="节点详情信息" :visible.sync="nodeInfoVisible" fullscreen>
        <el-form size="mini" disabled label-width="120px">
          <el-form-item label="pid">
            <el-input :value="nodeInfo.pid" />
          </el-form-item>
          <el-form-item label="version">
            <el-input :value="nodeInfo.version" />
          </el-form-item>
          <el-form-item label="key">
            <el-input :value="nodeInfo.key" />
          </el-form-item>
          <el-form-item label="更新时间">
            <el-input :value="nodeInfo.updateTime" />
          </el-form-item>
          <el-form-item label="插件">
            <el-input
              style="margin-bottom: 15px;"
              v-for="item of nodeInfo.gatewayPluginInfoList && nodeInfo.gatewayPluginInfoList.list || []"
              :key="item.name"
              :value="item.name"
            />
          </el-form-item>
          <el-form-item label="过滤器">
            <el-input
              style="margin-bottom: 15px;"
              v-for="item of nodeInfo.gatewayFilterInfoList"
              :key="item.name"
              :value="item.name"
            />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button size="mini" type="primary" @click="nodeInfoVisible = false">关闭</el-button>
        </div>
      </el-dialog>
    </template>
  </d2-container>
</template>


<script>
import service from "@/plugin/axios/index";
import bizutil from "@/common/bizutil";
import "codemirror/mode/javascript/javascript.js";
import "codemirror/theme/base16-dark.css";
import { mapState } from "vuex";

export default {
  name: "agents",
  data() {
    return {
      rules: [],
      tableData: [],
      total: 0,
      pageNo: 1,
      pageSize: 10,
      paginationDisabled: false,
      batchOptRows: [],

      formEdit: {},
      editFormVisible: false,
      editFormTitle: "",
      formLabelWidth: "60px",
      nodeInfo: {},
      nodeInfoVisible: false,

      refreshDisabled: false
    };
  },
  computed: {
    ...mapState("d2admin/user", ["info"])
  },
  mounted: function() {
    this.getList();
  },
  methods: {
    getList() {
      this.paginationDisabled = true;
      service({
        url: "/agent/list",
        method: "post",
        data: {
          pageNo: this.pageNo,
          pageSize: this.pageSize
        }
      }).then( res => {
        this.total = res.total;
        const agentList = bizutil.agentListProcess(res.agentList);
        this.tableData = agentList.map(item => {
           item.expendNodeInfo = "";
           return item
        });
        setTimeout(() => {
          this.paginationDisabled = false;
        }, 1000);
        return agentList
      }).then( async res => {
       for (const item of res) {
          try {
            item.expendNodeInfo = await this.getExpendNodeInfo(item);
          } catch (e) {
            // 异常节点
            item.expendNodeInfo = "";
          }
        }
      });
      setTimeout(() => {
        this.paginationDisabled = false;
      }, 5000);
    },
    async getExpendNodeInfo(row) {
      return service({
        url: "/agent/datail/info",
        method: "post",
        data: {
          ip: row.ip,
          port: row.port
        }
      }).then(expendNodeInfo => {
        return expendNodeInfo;
      });
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.expendNodeInfo) {
        return "success-row";
      }
      return "error-row";
    },

    handleCurrentChange() {
      this.getList();
    },

    handlePrevClick() {},

    handleNextClick() {},

    // 新增按钮
    editRow(row) {
      this.formEdit = { ...row };
      this.editFormTitle = "编辑节点分组" + this.formEdit.id;
      this.editFormVisible = true;
    },
    handleRowEdit() {
      this.$refs["formEdit"].validate(valid => {
        if (!valid) {
          this.$message({
            message: "请检查参数",
            type: "warning"
          });
          return false;
        }

        this.editFormVisible = false;
        this.formEdit.updatorId = this.info.uuid;
        this.formEdit.utime = 0; // disable utime field
        service({
          url: "/agent/update",
          method: "post",
          data: this.formEdit
        })
          .then(res => {
            this.formEdit = {};
            this.$message({
              message: "编辑成功",
              type: "success"
            });
            this.getList();
          })
          .catch(() => {
            this.formEdit = {};
          })
          .finally(() => {
            this.$refs["formEdit"].clearValidate();
          });
      });
    },
    handleRowEditCancel() {
      this.editFormVisible = false;
      this.$message({
        message: "取消编辑",
        type: "warning"
      });
    },

    // 删除
    delRow(row) {
      this.$confirm("确认删除?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          service({
            url: "/agent/del",
            method: "post",
            data: {
              ids: [row.id],
              uid: this.info.uuid
            }
          }).then(res => {
            this.$message({
              message: "删除成功",
              type: "success"
            });
            this.getList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },

    // 批量操作
    handleBatch(cmd) {
      console.log("cmd:", cmd);
      switch (cmd) {
        case "1":
          this.handleBatchDel();
          break;
        default:
          this.$message({
            message: "无效的批量操作",
            type: "warning"
          });
          return;
      }
    },

    // 批量选择按钮处理
    handleSelectionChange(val) {
      this.batchOptRows = val;
    },

    // 批量删除
    handleBatchDel() {
      let ids = bizutil.getBatchIdsForOpt(this.batchOptRows);
      if (ids.length <= 0) {
        this.$message({
          message: "请首先选择要操作的记录",
          type: "warning"
        });
        return;
      }

      service({
        url: "/agent/del",
        method: "post",
        data: {
          ids: ids,
          uid: this.info.uuid
        }
      }).then(res => {
        this.$message({
          message: "API Group删除成功",
          type: "success"
        });
        this.getList();
        setTimeout(() => {
          this.getList();
        }, 1000);
      });
    },

    // 刷新
    refresh() {
      this.refreshDisabled = true;
      setTimeout(() => {
        this.refreshDisabled = false;
      }, 2000);
      this.getList();
    },

    showInfo(row) {
      this.nodeInfo = row.expendNodeInfo || {};
      this.nodeInfoVisible = true;
    }
  }
};
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
}
</style>
<style>
.el-table .success-row {
  background: #f0f9eb;
}
.el-table .error-row {
  background: #fef0f0 !important;
}
</style>
