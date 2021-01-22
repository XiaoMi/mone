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
        <div class='title'>服务名：</div>
        <el-select
          v-model="serviceName"
          @change="changeServiceName"
          placeholder="请选择"
          size='mini'
          class='input_margin'>
          <el-option
            v-for="item in serviceList"
            :key="item"
            :label="item"
            :value="item">
          </el-option>
        </el-select>
      </div>
    </d2-module>
    
    <d2-module margin-bottom>
      <div class='card'>
        <div class='card-left'>
          <div class='version'>
            <span class='version-title'>版本：</span>
            <el-tag size="mini" type="success">{{ version }}</el-tag>
          </div>
          <div v-if='caseVersion'>
            <span class='version-title'>版本：</span>
            <el-tag size="mini" type="success">{{ caseVersion }}</el-tag>
          </div>
        </div>
        <div class='card-right'>
          <el-button 
            size='mini'
            :disabled='tableData.length === 0'
            @click="handleRunAllCase">执行所有用例</el-button>
        </div>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column type='expand'>
          <template slot-scope='props'>
            <div class='result-title'>测试结果：</div>
            <div class='result-expand' v-if='props.row.type === "null"'>
              <div class='result-expand_middle' 
                :title='props.row.result'>
                {{ props.row.result }}</div>
              <el-button 
                type="primary" 
                plain
                @click='showCaseResult(props.row)'>查看详情</el-button>
            </div>
            <div class='result-expand' v-else-if='props.row.type === "Object"'>
              <div class='result-expand_middle' 
                :title='JSON.stringify(props.row.result)'>
                {{ props.row.result }}</div>
              <el-button 
                type="primary" 
                plain
                @click='showCaseResult(props.row)'>查看详情</el-button>
            </div>
            <div class='result-expand' v-else-if='props.row.type === "other"'>
              <div class='result-expand_middle' 
                :title='props.row.result'>
                {{ props.row.result }}</div>
              <el-button 
                type="primary" 
                plain
                @click='showCaseResult(props.row)'>查看详情</el-button>
            </div>
            <div class='result-expand' 
                 v-else
                 v-for='item in props.row.result'
                 :key='item'>
              <div class='result-expand_middle' 
                :title='JSON.stringify(item)'>
                {{ item }}</div>
              <el-button 
                type="primary" 
                plain
                @click='showCaseResult(item)'>查看详情</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label='测试用例名' prop='name' width='220'></el-table-column>
        <el-table-column label='描述' prop='desc' show-overflow-tooltip width='300'></el-table-column>
        <el-table-column label='状态' width='250'>
          <template slot-scope='scope'>
            <el-tag 
              v-if='scope.row.code === -2' 
              size='mini' 
              style='width:60px'>未执行</el-tag>
            <el-tag 
              v-else-if='scope.row.code === 0 || scope.row.code === 200'
              type='success' 
              size='mini' 
              style='width:60px'>成功</el-tag>
            <el-tag 
              v-else-if='scope.row.code === 500'
              type='danger' 
              size='mini' 
              style='width:60px'>失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label='操作' fixed='right' width='200'>
          <template slot-scope='scope'>
            <el-button @click='handleSetCase(scope.row)'>设置</el-button>
            <el-button @click='handleRunCase(scope.row)'>执行</el-button>
          </template>
        </el-table-column>
      </el-table>
    </d2-module>

    <el-dialog title='测试结果' :visible.sync="caseResultVisible" width='880px'>
      <codemirror v-model="caseResult" :options="codeMirrorOptions" class='codeMirror_content'/>
    </el-dialog>

    <el-dialog title="测试用例设置" :visible.sync="dialogFormVisible" width='880px'>
      <el-form :model="editCaseForm" size="mini" label-width="100px">
        <el-form-item label="测试类型">
          <el-radio v-model="editCaseForm.callType" label="dubbo">dubbo</el-radio>
          <el-radio v-model="editCaseForm.callType" label="http">http</el-radio>
        </el-form-item>
        <el-form-item label="provider">
          <el-radio v-model="editCaseForm.type" label="normal">随机</el-radio>
          <el-radio v-model="editCaseForm.type" label="ip">指定
            <el-select
              v-model="editCaseForm.providers"
              multiple
              value-key="ip"
              placeholder="请选择">
              <el-option
                v-for="item in providers"
                :key="item.ip"
                :label="`${item.ip}:${item.port}`"
                :value="item">
              </el-option>
            </el-select>
          </el-radio>
          <el-radio v-model="editCaseForm.type" label="all">全部</el-radio>
        </el-form-item>
        <el-form-item label="参数" label-width="100px">
          <el-input 
            style='width: 80%'
            :rows='5'
            type="textarea" 
            v-model="editCaseForm.args"></el-input>
        </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogFormVisible = false" size='mini'>取 消</el-button>
      <el-button type="primary" @click="setSetting" size='mini'>确 定</el-button>
    </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'

export default {
  data () {
    return {
      serviceName: '',
      tableData: [],
      caseVersion: '',
      caseResultVisible: false,
      caseResult: '',
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        theme: 'base16-dark',
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      serviceList: [],
      version: '',
      dialogFormVisible: false,
      providers: [],
      editCaseForm: {
        callType: 'dubbo',
        type: 'normal',
        providers: [],
        args: ''
      }
    }
  },
  created () {
    this.getVersion()
    this.services()
  },
  methods: {
    services () {
      return service({
        url: '/test/case/services',
        method: 'GET'
      }).then(serviceList => {
        this.serviceList = serviceList
      })
    },
    getVersion () {
      return service({
        url: '/test/case/version',
        method: 'GET'
      }).then(version => {
        this.version = version
      })
    },
    setSetting () {
      const serviceName = this.serviceName
      const item = this.currentItem
      const editCaseForm = this.editCaseForm
      return service({
        url: `/test/case/set-setting`,
        method: 'POST',
        data: {
          serviceName: serviceName,
          method: item.name,
          testCaseParam: {
            ...editCaseForm
          }
        }
      }).then(boolean => {
        if (boolean) {
          this.dialogFormVisible = false
        }
      })
    },
    getSetting (item) {
      return service({
        url: `/test/case/get-setting?serviceName=${this.serviceName}&methodName=${item.name}`,
        method: 'GET'
      }).then(testCaseParam => {
        if (testCaseParam) {
          this.editCaseForm = {
            ...testCaseParam
          }
        } else {
          this.editCaseForm = {
            callType: 'dubbo',
            type: 'normal',
            providers: [],
            args: ''
          }
        }
      })
    },
    getProviders (item) {
      return service({
        url: `/test/case/provider-list?serviceName=${this.serviceName}&methodName=${item.name}`,
        method: 'GET'
      }).then(providers => {
        this.providers = providers
      })
    },
    changeServiceName() {
      service({
        url: `/test/case/methods?serviceName=${this.serviceName}`,
        method: 'GET'
      }).then(res => {
        const { version,list } = res;
        this.caseVersion = version;
        this.tableData = list.map( (item,index) => {
          return {
            ...item,
            index,
            code: -2
          }
        })
      })
    },
    handleSetCase(row) {
      this.dialogFormVisible = true;
      this.currentItem = row;
      this.providers = [];
      this.getProviders(row);
      this.getSetting(row)
    },
    handleRunCase(row) {
      service({
        url: `/test/case/test-method?serviceName=${this.serviceName}&methodName=${row.name}`,
        method: 'GET'
      }).then(res => {
        let code = '';
        let result = '';
        let type = '';
        let index = row && row.index;
        if (res === 'null' || res === null) {
          code = 500;
          type = 'null';
          result = res || 'null';
          this.$set(this.tableData[index],'code',code);
          this.$set(this.tableData[index],'type',type);
          this.$set(this.tableData[index],'result',result);
          return;
        }
        try {
          const newRes = JSON.parse(res);
          if (Array.isArray(newRes)) {
            code = newRes.findIndex(item => (item.code !== 200 || item.code !== 0)) === -1 ? 500 : 0;
            type = 'Array';
            result = newRes;
          } else {
            code = newRes && newRes.code;
            type = 'Object';
            result = newRes
          }
        } catch(e) {
          code = 500;
          type = 'other'
          result = res
        }
        this.$set(this.tableData[index],'code',code)
        this.$set(this.tableData[index],'type',type)
        this.$set(this.tableData[index],'result',result)
      })
    },
    showCaseResult(row) {
      let { type,result } = row;
      switch(type) {
        case 'Object':
          this.caseResult = JSON.stringify(result,null,2);
          break;
        case 'null':
          this.caseResult = result;
          break;
        case 'other':
          this.caseResult = result;
          break;
        default:
          this.caseResult = JSON.stringify(row,null,2)
      }
      this.caseResultVisible = true
    },
    handleRunAllCase() {
      (this.tableData || []).forEach(item => {
        this.handleRunCase(item)
      })
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
.version {
  margin-bottom: 10px;
  &-title {
    font-size: 14px;
    color:#99a9bf
  }
}
.card {
  padding: 0 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  &-left {
    height: 50px
  }
  &-right {
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: flex-end
  }
}
.result-title {
  font-size: 15px;
  text-align: left;
  color: #99a9bf;
  margin-bottom: 10px; 
  padding-left: 50px;
  padding-top: 10px;
}
.result-expand {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 8px 80px;
  &_left {
    color: #666;
    font-size: 14px;
  }
  &_middle {
    font-size: 14px;
    width: 550px;
    text-align: left;
    overflow: hidden;
    text-overflow:ellipsis;
    white-space: nowrap;
    color: #99a9bf;
    margin-right: 30px;
    cursor: pointer;
  }
}
</style>