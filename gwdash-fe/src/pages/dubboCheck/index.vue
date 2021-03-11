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
        <div class='title'>服务提供方：</div>
        <el-input
          size='mini'
          v-model='searchWord'
          @keypress.native.enter='handleInput'
          placeholder='请输入'
          class='input_margin'/>
        <el-button
          size='mini'
          @click="handleInput">查询</el-button>
        <el-button
          size='mini'
          @click="handleConfig('add',null)">新增</el-button>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data="tableData" class="table-list">
        <el-table-column label="id" prop="id" width="100" fixed="left"></el-table-column>
        <el-table-column label="服务提供方" prop="appNameKey" show-overflow-tooltip width="180"></el-table-column>
        <el-table-column label="服务消费方" width="260">
          <template slot-scope="scope">
            <div :class="{'deal-names': scope.row.appNameValue.length > 5}">
              <el-tag
                size='mini'
                style='width: 160px'
                v-for='item in scope.row.appNameValue' :key='item'>{{item}}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建者" prop="creator" width="160"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="160"></el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleConfig('edit',scope.row)">编辑</el-button>
            <el-button type="text" size="mini"  @click="showNacos(scope.row)">查看Nacos</el-button>
            <el-button type="danger" size="mini" class='danger' @click="deleteConfig(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='page'
        :pageSize='pageSize'
        :total='total'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
    </d2-module>

    <el-dialog 
      :title="`${submitUrl  === 'new' ? '新增' : '编辑'}配置`" 
      :visible.sync='configDialogVisible' 
      width='800px'>
      <el-form ref='configForm' :model='configForm' :rules='configFormRules' label-width='110px' size='mini'>
        <el-form-item label='服务提供方' prop='appNameKey'>
          <el-input v-model="configForm.appNameKey" placeholder="请输入服务提供方" style="width:50%"/>
        </el-form-item>
        <el-form-item label='服务消费方' prop='appNameValue'>
          <div v-for='(item,index) in configForm.appNameValue' :key='index' class='configForm_value'>
            <el-input
              v-model='configForm.appNameValue[index]'
              placeholder='请输入服务消费方' 
              style="width:50%"
              class='content'/>
            <span
              class="el-icon-remove-outline del-machineLabel icon"
              title='删除'
              @click='delAppNameValue(index)'></span>
            <span
              v-if='configForm.appNameValue.length == index + 1'
              class="el-icon-circle-plus-outline add-machineLabel icon"
              title='新增'
              @click='addAppNameValue'></span>
            <span
              v-else
              class='seat'></span>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitConfigForm('configForm')" size="mini">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title='Nacos' :visible.sync='nacosDialogVisible' width='880px'>
      <codemirror v-model="nacosData" :options="codeMirrorOptions" class='codeMirror_content'/>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios'
import bizutil from '@/common/bizutil'

export default {
  data () {
    return {
      tableData: [],
      searchWord: '',
      configDialogVisible: false,
      configForm: {
        appNameKey: '',
        appNameValue: ['']
      },
      configFormRules: {
        appNameKey: [{ required: true, message: "请输入服务提供方", trigger: "blur" }],
        appNameValue: [{ required: true, message: "请输入服务消费方", trigger: "blur" }]
      },
      submitUrl: '',
      page: 1,
      pageSize: 10,
      total: 0,
      nacosDialogVisible: false,
      nacosData: '',
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      }
    }
  },
  created () {
    this.getInitList()
  },
  methods: {
    getInitList () {
      service({
        url: '/rpc/auth/config/getList',
        method: 'POST',
        data: {
          page: this.page,
          pageSize: this.pageSize,
          appNameKey: this.searchWord
        }
      }).then(res => {
        if(res === null) return;
        this.page = res.page
        this.pageSize = res.pagesize
        this.total = res.total
        this.tableData = res.list && res.list.map(item => {
          return {
            ...item,
            appNameKey: item.providerAppName,
            appNameValue: JSON.parse(item.consumerAppNames),
            createTime: bizutil.timeFormat(item.createTime),
            updateTime: bizutil.timeFormat(item.updateTime)
          }
        })
      })
    },
    handleInput () {
      this.getInitList()
    },
    handleConfig(tag,param) {
      if (tag === 'add') {
        this.submitUrl = 'new';
        this.configForm = {
          appNameKey: '',
          appNameValue: ['']
        }
      } else {
        this.submitUrl = 'update';
        this.configForm = {
          appNameKey: param.appNameKey,
          appNameValue: JSON.parse(JSON.stringify(param.appNameValue))
        }
      }
      this.configDialogVisible = true
    },
    addAppNameValue() {
      this.configForm && this.configForm.appNameValue && this.configForm.appNameValue.push('')
    },
    delAppNameValue(index) {
      if (index === 0) { return }
      this.configForm && this.configForm.appNameValue && this.configForm.appNameValue.splice(index, 1)
    },
    submitConfigForm(formName) {
      this.$refs[formName].validate(valid => {
        let { appNameKey, appNameValue } = this.configForm;
        if (!valid || appNameValue.includes('')) {
          this.$message({
            message: '请检查参数[每个服务消费方都不能为空]',
            type: 'warning'
          })
          return
        }
        service({
          url: `/rpc/auth/config/${this.submitUrl}`,
          method: 'POST',
          data: {
            appNameKey,
            appNameValue
          }
        }).then(res => {
          if(res) {
            this.$message.success(`${this.submitUrl  === 'new' ? '新增' : '编辑'} 成功`);
            this.configDialogVisible = false;
            this.getInitList()
          }
        })
      })
    },
    deleteConfig(param) {
      let { appNameKey, appNameValue } = param;
      this.$confirm("此操作将永久删除该配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: '/rpc/auth/config/del',
          method: 'POST',
          data: { 
            appNameKey
          }
        }).then(res => {
          this.$message({
            message: "删除成功",
            type: "success"
          })
          this.getInitList()
        })
      }).catch(() => {
        this.$message({
          message: "已取消删除",
          type: "warning"
        })
      })
    },
    showNacos(param) {
      const appNameKey = param && param.appNameKey;
      service({
        url: '/rpc/auth/config/get',
        method: 'POST',
        data: { 
          appNameKey
        }
      }).then(res => {
        this.nacosData = JSON.stringify(res);
        this.nacosDialogVisible = true
      })
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getInitList()
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  .title {
    font-size: 13px;
    color: #333
  }
  .input_margin {
    width: 20%;
    margin-right: 10px
  }
}
.deal-names {
  height: 92px;
  overflow-y: scroll
}
.deal-names::-webkit-scrollbar {
  display: none
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C
}
.configForm_value {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 8px;
  .content {
    width: 250px;
  }
  .icon {
    font-size: 22px;
    align-self: center;
    color: rgb(192, 196, 204);
    margin-left: 7px;
    cursor: pointer;
  }
  .seat {
    display: inline-block;
    width: 22px;
    height: 22px;
    margin-left: 18px
  }
}
</style>