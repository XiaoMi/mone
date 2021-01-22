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
    <d2-module v-if="addVisible" margin-bottom>
      <div class="header">
        <div>
          <el-button
            @click="handleToken('新增token','/gitlab/token/new')"
            size="mini"
            icon="el-icon-plus"
          >新增</el-button>
        </div>
      </div>
    </d2-module>
    <d2-module>
      <div class="token-guide" v-if="addVisible">
        <token-guide />
      </div>
      <div class="token" v-else>
        <div class="token-top"></div>
        <div class="token-head">gitlab token：</div>
        <div class="token-name strip">
          <div class="key">name：</div>
          <div class="value">{{gitLabTokenList[0].name}}</div>
        </div>
        <div class="token-value strip">
          <div class="key">token：</div>
          <div class="value">{{gitLabTokenList[0].token}}</div>
        </div>
        <div class="token-desc strip">
          <div class="key">描述：</div>
          <div class="value">{{gitLabTokenList[0].desc}}</div>
        </div>
        <div class="token-footer">
          <el-divider content-position="right">
            <span
              @click="handleToken('编辑token','/gitlab/token/edit', gitLabTokenList[0])"
              class="edit"
            >编辑</span>
            <span style="margin: 0 5px">|</span>
            <span @click="delFormRow(gitLabTokenList[0])" style="cursor:pointer; color:#ff3232">删除</span>
          </el-divider>
        </div>
      </div>
    </d2-module>

    <!-- 新增/编辑 -->
    <el-dialog
      :title="handleTokenTitle"
      :visible.sync="dialogTokenVisible"
      width="800px"
      class="gitlab-page"
    >
      <el-form :model="handleTokenForm" label-width="110px" ref="handleTokenForm" :rules="rules">
        <el-form-item label="name" prop="name">
          <el-input v-model="handleTokenForm.name" placeholder="请输入name" style="width:50%"></el-input>
        </el-form-item>
        <el-form-item label="token" prop="token">
          <el-input v-model="handleTokenForm.token" placeholder="请输入token" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input
            class="desc"
            type="textarea"
            :rows="5"
            v-model="handleTokenForm.desc"
            placeholder="请输入相关描述"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleTokenForm = false">取 消</el-button>
        <el-button type="primary" @click="submitFormUpload('handleTokenForm')">确 定</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index";
import qs from "qs";
import tokenGuide from "./components/token-guide";

export default {
  components: {
    tokenGuide
  },
  data() {
    return {
      handleTokenTitle: "",
      handleTokenUrl: "",
      dialogTokenVisible: false,
      handleTokenForm: {},
      gitLabTokenList: [],
      paginationDisable: false,
      total: 0,
      page: 1,
      pageSize: 10,
      rules: {
        name: [{ required: true, message: "请输入name", trigger: "blur" }],
        token: [{ required: true, message: "请输入token", trigger: "blur" }],
        desc: [{ required: false, message: "请输入相关描述", trigger: "blur" }]
      },
      addVisible: false
    };
  },
  created() {
    this.getTokenList();
  },
  methods: {
    getTokenList() {
      const page = this.page;
      const pageSize = this.pageSize;
      this.paginationDisable = true;
      this.addVisible = false;
      service({
        url: `/gitlab/token/list?page=${page}&pageSize=${pageSize}`,
        method: "GET"
      }).then(res => {
        this.total = res.total;
        this.gitLabTokenList = res.list;
        this.paginationDisable = false;
        if (res.list && res.list.length === 0) {
          this.addVisible = true;
        }
      });
    },
    // 新增/编辑
    handleToken(title, url, form) {
      this.handleTokenTitle = title;
      this.handleTokenUrl = url;
      this.handleTokenForm = { ...form };
      this.dialogTokenVisible = true;
    },
    // 新增/编辑 -> 提交
    submitFormUpload(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: "请检查参数",
            type: "warning"
          });
          return false;
        }
        this.dialogTokenVisible = false;
        service({
          url: this.handleTokenUrl,
          method: "POST",
          data: qs.stringify(this.handleTokenForm)
        }).then(res => {
          this.handleTokenForm = {};
          this.$message({
            message: `${this.handleTokenTitle}成功`,
            type: "success"
          });
          this.getTokenList();
        });
      });
    },
    // 删除
    delFormRow(row) {
      this.$confirm("确认删除?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          service({
            url: `/gitlab/token/del?id=${row.id}`,
            methos: "GET"
          }).then(res => {
            this.$message({
              message: "删除成功",
              type: "success"
            });
            this.getTokenList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    }
  }
};
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.token {
  border: 1px solid #eee;
  padding: 16px;
  box-shadow: rgba(0, 0, 0, 0.12) 0px 2px 4px, rgba(0, 0, 0, 0.04) 0px 0px 6px;
  margin: 80px auto;
  position: relative;
  background: #f8f8f8;
  .token-top {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background-color: #409eff;
    height: 3px;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
  }
  .token-head {
    font-size: 16px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 16px;
    margin-left: 3px;
  }
  .token-footer {
    .el-divider__text {
      background: #f8f8f8;
      padding: 0 8px;
      right: 25px;
      font-size: 14px;
      color: #909399;
      font-weight: bold;
    }
  }
  .strip {
    padding: 10px;
    background: #fff;
    margin-bottom: 10px;
    border-radius: 8px;
  }
  .key {
    font-size: 14px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 0px;
    display: inline-block;
    width: 60px;
    margin-right: 40px;
  }
  .value {
    font-size: 14px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 0px;
    display: inline-block;
  }
}
.edit {
  cursor: pointer;
}
.edit:hover {
  color: #409eff;
}

.token {
  width: 900px;
  border: 1px solid #eee;
  padding: 16px;
  box-shadow: rgba(0, 0, 0, 0.12) 0px 2px 4px, rgba(0, 0, 0, 0.04) 0px 0px 6px;
  margin: 80px auto;
  position: relative;
  background: #f8f8f8;
  // background-clip: content-box;
  .token-top {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background-color: #409eff;
    height: 3px;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
  }
  .token-head {
    font-size: 16px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 16px;
    margin-left: 3px;
  }
  .token-footer {
    .el-divider__text {
      background: #f8f8f8;
      padding: 0 8px;
      right: 25px;
      font-size: 14px;
      color: #909399;
      font-weight: bold;
    }
  }
  .strip {
    padding: 10px;
    background: #fff;
    margin-bottom: 10px;
    border-radius: 8px;
  }
  .key {
    font-size: 14px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 0px;
    display: inline-block;
    width: 60px;
    margin-right: 40px;
  }
  .value {
    font-size: 14px;
    color: #909399;
    font-weight: bold;
    margin-bottom: 0px;
    display: inline-block;
  }
}
</style>

<style lang="scss">
.gitlab-page .el-textarea .el-input__count {
  right: 22px;
  bottom: 6px;
  height: 18px;
  line-height: 18px;
}
</style>