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
          @click="handleRefresh"
          size="mini"
          :disabled="refreshDisabled"
        >刷新</el-button>
        <el-button
          v-if="true"
          @click="showAddForm"
          size="mini"
          style="margin-left: 10px"
        >新增</el-button>
      </div>
    </d2-module>
    <d2-module>
      <div class="table-list">
        <el-table :data="list" style="width: 100%">
          <el-table-column prop="id" label="id" width="100"></el-table-column>
          <el-table-column prop="name" label="name" width="200"></el-table-column>
          <el-table-column prop="type" label="type" width="100"></el-table-column>
          <el-table-column prop="dataSourceUrl" label="url" width="400"></el-table-column>
          <el-table-column prop="poolSize" label="连接数" width="120"></el-table-column>
          <el-table-column prop="creator" label="作者" width="180"></el-table-column>
          <el-table-column prop="ctime" label="创建时间" width="230"></el-table-column>
          <el-table-column prop="utime" label="更新时间" width="230"></el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" class="el-button--blue" @click="fetchRecord(scope.row.id)">更新</el-button>
              <el-button
                size="mini"
                type="danger"
                class="el-button--orange"
                @click="handleDelete(scope.row.id)"
              >删除</el-button>
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
    <!-- 添加 & 更新 -->
    <el-dialog width="780px" :title="title" :visible.sync="formVisible">
      <el-form
        :model="form"
        :label-width="formLabelWidth"
        :rules="rules"
        status-icon
        ref="form"
        size="mini"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="类型">
            <el-option label="mysql" :value="0"></el-option>
            <el-option label="plugin" :value="1"></el-option>
            <el-option label="dubbo" :value="2"></el-option>
            <el-option label="redis" :value="3"></el-option>
            <el-option label="nacos" :value="4"></el-option>
            <el-option label="mongo" :value="5"></el-option>
          </el-select>
        </el-form-item>

        <template v-if="form.type == 0">
          <el-form-item label="驱动类" prop="driverClass">
            <el-input v-model="form.driverClass"></el-input>
          </el-form-item>

          <el-form-item label="数据源" prop="dataSourceUrl">
            <el-input v-model="form.dataSourceUrl"></el-input>
          </el-form-item>

          <el-form-item label="用户名" prop="userName">
            <el-input v-model="form.userName"></el-input>
          </el-form-item>

          <el-form-item label="密码" prop="passWd">
            <el-input v-model="form.passWd" type="password"></el-input>
          </el-form-item>

          <el-form-item label="连接池" prop="poolSize">
            <el-row :gutter="2" type="flex" justify="space-between">
              <el-col :span="7">
                <el-input-number v-model="form.poolSize" :min="1" :max="400" placeholder="连接池数量"></el-input-number>
              </el-col>

              <el-col :span="7" :offset="2">
                <el-form-item prop="minPoolSize">
                  <el-input-number
                    v-model="form.minPoolSize"
                    :min="1"
                    :max="400"
                    placeholder="最小连接池"
                  ></el-input-number>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="1">-</el-col>
              <el-col :span="7">
                <el-form-item prop="maxPoolSize">
                  <el-input-number
                    v-model="form.maxPoolSize"
                    :min="1"
                    :max="400"
                    placeholder="最大连接池"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form-item>
        </template>

        <template v-if="form.type == 1">
          <el-form-item label="插件路径" prop="jarPath">
            <el-input v-model="form.jarPath"></el-input>
          </el-form-item>
          <el-form-item label="包路径" prop="iocPackage">
            <el-input v-model="form.iocPackage"></el-input>
          </el-form-item>
        </template>

        <template v-if="form.type == 2">
          <el-form-item label="服务名称" prop="appName">
            <el-input v-model="form.appName"></el-input>
          </el-form-item>
          <el-form-item label="注册地址" prop="regAddress">
            <el-input v-model="form.regAddress"></el-input>
          </el-form-item>
          <el-form-item label="api包路径" prop="apiPackage">
            <el-input v-model="form.apiPackage"></el-input>
          </el-form-item>
          <el-form-item label="线程数" prop="threads">
            <el-input-number v-model="form.threads" :min="1" :max="400"></el-input-number>
          </el-form-item>
        </template>

        <template v-if="form.type == 3">
          <el-form-item label="redis 地址" prop="dataSourceUrl">
            <el-input v-model="form.dataSourceUrl"></el-input>
          </el-form-item>
          <el-form-item label="redis 类型" prop="redisType">
            <el-input v-model="form.redisType" placeholder="dev or cluster"></el-input>
          </el-form-item>
        </template>

        <template v-if="form.type == 4">
          <el-form-item label="地址" prop="dataSourceUrl">
            <el-input v-model="form.dataSourceUrl"></el-input>
          </el-form-item>
          <el-form-item label="data id" prop="nacosDataId">
            <el-input v-model="form.nacosDataId"></el-input>
          </el-form-item>
          <el-form-item label="group" prop="nacosGroup">
            <el-input v-model="form.nacosGroup"></el-input>
          </el-form-item>
        </template>

        <template v-if="form.type == 5">
          <el-form-item label="地址" prop="dataSourceUrl">
            <el-input v-model="form.dataSourceUrl"></el-input>
          </el-form-item>
          <el-form-item label="数据库" prop="redisType">
            <el-input v-model="form.redisType"></el-input>
          </el-form-item>
        </template>

        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleRowCancel" size="mini">取 消</el-button>
        <el-button type="primary" @click="handleRow('form')" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index";
import time2Date from "@/libs/time2Date";

export default {
  data() {
    return {
      title: "",
      list: [],
      formLabelWidth: "120px",
      formVisible: false,
      refreshDisabled: false,
      form: {},
      page: 1,
      pageSize: 10,
      total: 0,
      pageDisabled: false,
      rules: {
        name: [{ required: true, message: "请输入名称", trigger: "blur" }],
        type: [{ required: true, message: "必填字段", trigger: "blur" }],
        driverClass: [{ required: true, message: "必填字段", trigger: "blur" }],
        dataSourceUrl: [
          { required: true, message: "必填字段", trigger: "blur" }
        ],
        userName: [{ required: true, message: "必填字段", trigger: "blur" }],
        passWd: [{ required: true, message: "必填字段", trigger: "blur" }],
        poolSize: [{ required: true, message: "必填字段", trigger: "blur" }],
        minPoolSize: [{ required: true, message: "必填字段", trigger: "blur" }],
        maxPoolSize: [{ required: true, message: "必填字段", trigger: "blur" }],
        jarPath: [{ required: true, message: "必填字段", trigger: "blur" }],
        iocPackage: [{ required: true, message: "必填字段", trigger: "blur" }],
        appName: [{ required: true, message: "必填字段", trigger: "blur" }],
        regAddress: [{ required: true, message: "必填字段", trigger: "blur" }],
        apiPackage: [{ required: true, message: "必填字段", trigger: "blur" }],
        threads: [{ required: true, message: "必填字段", trigger: "blur" }],
        redisType: [{ required: true, message: "必填字段", trigger: "blur" }],
        nacosDataId: [{ required: true, message: "必填字段", trigger: "blur" }],
        nacosGroup: [{ required: true, message: "必填字段", trigger: "blur" }]
      }
    }
  },
  mounted: function() {
    this.handleList();
  },
  methods: {
    showAddForm() {
      this.form = {
        threads: 150
      };
      this.title = "新增数据源";
      this.formVisible = true;
    },
    handleRow(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          return false;
        }
        let url = "/ds/insert";
        const form = this.form;
        if (form.id != null) {
          url = "/ds/update";
        }
        service({
          url,
          method: "post",
          data: form
        }).then(e => {
          this.formVisible = false;
          this.$message({
            message: "操作成功",
            type: "success"
          });
          setTimeout(() => {
            this.handleList();
          }, 1000);
        });
      });
    },
    // 获取信息
    fetchRecord(id) {
      this.title = "更新数据源";
      service({
        url: `/ds/fetch?id=${id}`,
        method: "get"
      }).then(e => {
        this.form = e;
        this.formVisible = true;
      });
    },
    handleRowCancel() {
      this.formVisible = false;
    },
    handleRefresh() {
      this.refreshDisabled = true;
      setTimeout(() => {
        this.refreshDisabled = false;
      }, 2000);
      this.handleList();
    },
    handleDelete(id, name) {
      this.$confirm("此操作将永久删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          service({
            url: "/ds/delete",
            method: "post",
            data: {
              id
            }
          }).then(() => {
            this.handleList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    handleList() {
      this.pageDisabled = true;
      service({
        url: `/ds/list?page=${this.page}&pageSize=${this.pageSize}`,
        method: "get"
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        const list = res.list;
        this.total = res.total;
        list.map(item => {
          item.ctime = time2Date(item.ctime);
          item.utime = time2Date(item.utime);
          return item;
        });
        this.list = list;
        this.pageDisabled = false
      });
      setTimeout(() => {
        this.pageDisabled = false;
      }, 2000);
    },
    handleCurrentChange(val) {
      this.page = val;
      this.handleList();
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.line {
  text-align: center;
}
</style>