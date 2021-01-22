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
        <div class='title'>名称：</div>
        <el-input
           size='mini'
           v-model='searchObj.name' 
           @keypress.native.enter='handleInput'
           placeholder='请输入'
           class='input_margin'/>
        <div class='title'>录制状态：</div>
        <el-select size='mini' v-model.number='searchObj.status' class='input_margin'>
          <el-option 
            v-for='item in recordStatus'
            :key='item.value'
            :label='item.label'
            :value='item.value'/>
        </el-select>
        <div class='title'>创建人：</div>
        <el-input
           size='mini'
           v-model='searchObj.creater' 
           @keypress.native.enter='handleInput'
           placeholder='请输入'
           class='input_margin'/>
        <el-button 
          size='mini'
          @click="handleInput">查询</el-button> 
        <el-button 
          size='mini'
          @click="addRecordConfig">新增</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column label="id" prop="id" width="80" fixed="left"></el-table-column>
        <el-table-column label="名称" prop="name" width="120"></el-table-column>
        <el-table-column label="录制途径" width="120">
          <template slot-scope='scope'>
            <div v-if='scope.row.sourceType === 1'>网关接口</div>
            <div v-if='scope.row.sourceType === 2'>dubbo接口</div>
          </template>
        </el-table-column>
        <el-table-column label="录制状态" width="120">
          <template slot-scope='scope'>
            <el-tag
              size='mini'
              style='width: 60px'
              :type="`${scope.row.status === 1 ? 'success' : ''}`">
              {{scope.row.status === 1 ? '录制中' : '待录制'}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label='网关环境' prop='envType' width="120">
          <template slot-scope='scope'>
            <div v-if='scope.row.envType === 1'>外网网关</div>
            <div v-if='scope.row.envType === 2'>内网网关</div>
            <div v-if='scope.row.envType === 3'>测试环境网关</div>
          </template>
        </el-table-column>
        <el-table-column label='API路径' prop='url' width="180" show-overflow-tooltip></el-table-column>
        <el-table-column label='service name' prop='serviceName' width="160"></el-table-column>
        <el-table-column label='method name' prop='methodName' width="160"></el-table-column>
        <el-table-column label='存储天数' width='120'>
           <template slot-scope='scope'>
            {{ `${scope.row.saveDays}天`}}
          </template>
        </el-table-column>
        <el-table-column label="创建人" prop="creator" width="120"></el-table-column>
        <el-table-column label="更新人" prop="updater" width="120"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="160"></el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="editRecordConfig(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="startRecord(scope.row.id)">开始录制</el-button>
            <el-button type='text' size='mini' @click="stopRecord(scope.row.id)">停止录制</el-button>
            <el-dropdown 
              class="el-dropdown-styled" 
              size='mini'
              @command='handleCommand($event,scope.row)'>
              <el-button class="el-button--blue">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="copy">复制</el-dropdown-item>
                <el-dropdown-item command="delete">删除</el-dropdown-item>
                <!-- <el-dropdown-item command="fixedTime">定时录制</el-dropdown-item> -->
                <el-dropdown-item command='result'>录制结果</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
    

    <el-dialog :title="`${handleTitle}配置`" :visible.sync='handleDialogVisible' width='800px' :before-close="handleClose" destroy-on-close>
      <el-form ref='form' :model='form' :rules='formRules' label-width='110px' size='mini' style='margin-left:30px'>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" style="width:50%"/>
        </el-form-item>
        <el-form-item label="录制途径" prop="sourceType">
          <el-select v-model="form.sourceType" style="width:50%" @change='sourceTypeChange'>
            <el-option label='网关接口' :value='1'/>
            <el-option label='dubbo接口' :value='2'/>
          </el-select>
        </el-form-item>
        <template v-if='form.sourceType === 1'>
          <el-form-item label="网关环境" prop='gatewaySource.envType'>
            <el-select v-model="form.gatewaySource.envType" style="width:50%">
              <el-option label='外网网关' :value='1'/>
              <el-option label='内网网关' :value='2'/>
              <el-option label='测试环境网关' :value='3'/>
            </el-select>
          </el-form-item>
          <el-form-item label="API路径" prop="gatewaySource.url">
            <el-autocomplete
              v-model="form.gatewaySource.url"
              :fetch-suggestions="getApiList"
              @select='fetchApiSelect'
              placeholder="请输入"
              style='width:50%'
            ></el-autocomplete>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="Service name" prop="dubboSource.serviceName">
            <el-input v-model="form.dubboSource.serviceName" placeholder="请输入服务名" style="width:50%"/>
          </el-form-item>
          <el-form-item label="Method name" prop="dubboSource.methods">
            <el-input v-model="form.dubboSource.methods" placeholder="请输入方法名" style="width:50%"/>
          </el-form-item>
          <el-form-item label="Group" prop="dubboSource.group">
            <el-input v-model="form.dubboSource.group" placeholder="请输入组" style="width:50%"/>
          </el-form-item>
          <el-form-item label="version" prop="dubboSource.version">
            <el-input v-model="form.dubboSource.version" placeholder="请输入版本" style="width:50%"/>
          </el-form-item>
        </template>
        <el-form-item label="存储天数" prop="saveDays">
          <el-input v-model.number="form.saveDays" placeholder="请输入 | 默认值7天 | 格式：7" style="width:50%"/>
        </el-form-item>
        <el-form-item label="录制策略" prop="recordingStrategy">
          <el-select v-model="form.recordingStrategy" style="width:50%">
            <el-option label='无选择' :value='0'/>
            <el-option label='按百分比录制' :value='1'/>
            <el-option label='按header参数录制' :value='3'/>
            <el-option label='按uid录制' :value='2'/>
          </el-select>
        </el-form-item>
        <template v-if='form.recordingStrategy === 1'>
          <el-form-item label="百分比" prop="percentage">
            <el-input v-model="form.percentage" placeholder="请输入 | 格式：99%" style="width:50%"/>
          </el-form-item>
        </template>
        <template v-else-if='form.recordingStrategy === 3'>
          <el-form-item label="header参数" prop="headers">
            <div v-for='(item,index) in form.headers' :key='index' class='form_headers'>
              <el-input value='key' disabled class='label'/>
              <el-input placeholder='请输入' v-model='item.key' class='content'/>
              <el-input value='value' disabled class='label'/>
              <el-input placeholder='请输入' v-model='item.value' class='content'/>
              <span 
                class="el-icon-remove-outline del-machineLabel icon" 
                title='删除'
                @click='delHeadersLabel(index)'></span>
              <span
                v-if='form.headers.length == index + 1'
                class="el-icon-circle-plus-outline add-machineLabel icon" 
                title='新增'
                @click='addHeadersLabel'></span>
              <span 
                v-else
                class='seat'
                ></span>
            </div>
            <!-- <el-input 
              type='textarea'
              :rows='4'
              autocomplete="off"
              v-model="form.headers"
              placeholder='{"Content-Type":"application/json}' 
              style="width:50%"/> -->
          </el-form-item>
        </template>
        <template v-else-if='form.recordingStrategy === 2'>
          <el-form-item label="uid" prop="uid">
            <el-input v-model.number="form.uid" placeholder="请输入uid" style="width:50%"/>
          </el-form-item>
        </template> 
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitFormUpload('form')" size="mini">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 2期上线 -->
    <el-dialog title='定时录制' :visible.sync='fixedTimeDialogVisible' width='800px'>
      <el-form ref='fixedTimeForm' :model='fixedTimeForm' :rules='fixedTimeFormRules' label-width='110px' size='mini'>
        <el-form-item label='日期' prop='date'>
          <el-date-picker
            v-model="fixedTimeForm.date"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label='时间' prop='time'>
          <el-time-picker
            is-range
            v-model="fixedTimeForm.time"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            placeholder="选择时间范围">
          </el-time-picker>
        </el-form-item>
        <el-form-item label='录制百分比' prop='percentage'>
          <el-input v-model="fixedTimeForm.percentage" placeholder="请输入百分比 | 格式：99%" style="width:54%"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fixedTimeDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitFixedTimeForm('fixedTimeForm')" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios"
import bizutil from '@/common/bizutil'

export default {
  data() {
    let NumberValidator = (rule,value,callback) => {
      if (value === '' || typeof value !== 'number') {
        callback(new Error('请输入 | Number类型'))
      } else {
        callback()
      }
    }
    return {
      page: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
      searchObj: {
        name: '',
        status: '',
        creater: ''
      },
      recordStatus: [
        {
          label: '待录制',
          value: 0
        },
        {
          label: '录制中',
          value: 1
        }
      ],
      handleTitle: '',
      handleDialogVisible: false,
      urlOptions: [],
      submitUrl: '',
      form: {
        name: '',
        sourceType: 1,
        gatewaySource: {
          envType: '',
          url: ''
        },
        dubboSource: {
          serviceName: '',
          methods: '',
          group: '',
          version: ''
        },
        recordingStrategy: '',
        percentage: '',
        headers: [{}],
        uid: '',
        saveDays: '',
      },
      formRules: {
        name: [{ required: true, message: "请输入名称", trigger: "blur" }],
        sourceType: [{ required: true, message: "请输入录制途径", trigger: "blur" }],
        'gatewaySource.envType': [{ required: true, message: "请输入网关环境", trigger: "blur" }],
        'gatewaySource.url': [{ required: true, message: "请输入API路径", trigger: "blur" }],
        'dubboSource.serviceName': [{ required: true, message: "请输入服务名", trigger: "blur" }],
        'dubboSource.methods': [{ required: true, message: "请输入方法名", trigger: "blur" }],
        'dubboSource.group': [{ required: false, message: "请输入组", trigger: "blur" }],
        'dubboSource.version': [{ required: false, message: "请输入版本", trigger: "blur" }],
        saveDays: [{ validator: NumberValidator, trigger: "blur" }],
        // saveDays: [{ required: false, message: "请输入存储天数 | Number类型", trigger: "blur" }],
        recordingStrategy: [{ required: false, message: "请输入录制策略", trigger: "blur" }],
        percentage: [{ required: true, message: "请输入百分比 | 格式：99", trigger: "blur" }],
        headers: [{ required: true, message: "请输入header参数", trigger: "blur" }], 
        uid: [{ validator: NumberValidator, trigger: "blur" }]
        // uid: [{ required: true, message: "请输入uid", trigger: "blur" }]
      },
      fixedTimeDialogVisible: false,
      fixedTimeForm: {
        date: '',
        time: '',
        percentage: ''
      },
      fixedTimeFormRules: {
        date: [{ required: true, message: "请选择日期", trigger: "blur" }],
        time: [{ required: true, message: "请选择时间", trigger: "blur" }],
        percentage: [{ required: true, message: "请输入录制百分比", trigger: "blur" }],
      }
    }
  },
  created() {
    this.getInitList();
  },
  methods: {
    getInitList(){
      service({
        url: '/traffic/recording/config/list',
        method: 'POST',
        data: {
          page: this.page,
          pageSize: this.pageSize,
          ...this.searchObj
        }
      }).then(res => {
        this.page = res.page;
        this.pageSize = res.pagesize;
        this.total = res.total;
        this.tableData = res.list.map(item => {
          return {
            ...item,
            envType: item.gatewaySource.envType,
            url: item.gatewaySource.url,
            serviceName: item.dubboSource.serviceName,
            methodName: item.dubboSource.methods,
            createTime: bizutil.timeFormat(item.createTime),
            updateTime: bizutil.timeFormat(item.updateTime)
          }
        })
      })
    },
    handleInput() {
      this.page = 1;
      this.pageSize = 10;
      this.getInitList()
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getInitList()
    },
    handleCommand(cmd,row) {
      let id = row && row.id;
      switch(cmd) {
        case 'copy':
          this.copyRecordConfig(row)
          break;
        case 'delete':
          this.deleteRecordConfig(id)
          break;
        case 'fixedTime':
          this.fixedTimeRecord(id)
          break;
        case 'result':
          this.goRecordResult(id)
      }
    },
    addRecordConfig() {
      this.handleTitle = '新增',
      this.handleDialogVisible = true;
      this.submitUrl = '/traffic/recording/config/new';
    },
    handleClose() {
      this.handleDialogVisible = false;
      this.initialize()
    },
    addHeadersLabel() {
      this.form && this.form.headers && this.form.headers.push({})
    },
    delHeadersLabel(index) {
      if (index == 0) {return}
      this.form && this.form.headers && this.form.headers.splice(index,1)
    },
    submitFormUpload(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          });
          return
        }
        this.handleDialogVisible = false;

        // 处理百分比
        let { percentage,headers } = this.form;
        percentage = +(percentage.replace('%',''));
        // 处理headers参数
        let finalHeaders = {};
        headers.forEach(item => {
          if (item.key && item.value) {
            finalHeaders[item.key] = item.value
          }
        })
        service({
          url: this.submitUrl,
          method: 'POST',
          data: {
            ...this.form,
            percentage,
            headers: finalHeaders,
            saveDays: this.form.saveDays || 7
          }
        }).then( res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.getInitList()
        })
      })
    },
    editRecordConfig(row) {
      this.handleTitle = '编辑',
      this.handleDialogVisible = true
      this.submitUrl = '/traffic/recording/config/update';
      this.dataBack(row)
    },
    copyRecordConfig(row) {
      this.handleTitle = '复制',
      this.handleDialogVisible = true;
      this.submitUrl = '/traffic/recording/config/new';
      this.dataBack(row)
    },
    // 回填数据
    dataBack(row) {
      let headers = this.dealHeaders(row);
      let percentage = `${row.percentage}%`;
      delete row.createTime;
      delete row.updateTime
      this.form = {
        ...row,
        headers,
        percentage
      }
    },
    // 处理headers
    dealHeaders(row) {
      let headers = row && row.headers;
      let finalHeaders = [];
      if (headers === null) {
        finalHeaders.push({})
      } else {
        for (const key in headers) {
          if (headers.hasOwnProperty(key)) {
            let newObj = {};
            newObj.key = key
            newObj.value = headers[key]
            finalHeaders.push(newObj)
          }
        }
      }
      return finalHeaders
    },
    deleteRecordConfig(id) {
      this.$confirm("此操作将永久删除该配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: "/traffic/recording/config/delete",
          method: 'POST',
          data: {id}
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
    startRecord(id) {
      service({
        url: "/traffic/recording/config/start",
        method: 'POST',
        data: {id}
      }).then(res => {
        this.$message({
          message: "开始录制",
          type: "success"
        })
        this.getInitList()
      })
    },
    stopRecord(id) {
      service({
        url: "/traffic/recording/config/stop",
        method: 'POST',
        data: {id}
      }).then(res => {
        this.$message({
          message: "停止录制",
          type: "success"
        })
        this.getInitList()
      })
    },
    goRecordResult(id) {
      this.$router.push({
        path: '/traffic/record/result',
        query: {id}
      })
    },
    sourceTypeChange(value) {
      if (this.handleTitle === '新增') {return}
      this.form.gatewaySource = {
        envType: '',
        url: ''
      }
      this.form.dubboSource = {
        serviceName: '',
        methods: '',
        group: '',
        version: ''
      }
    },
    fixedTimeRecord(id) {
      this.fixedTimeDialogVisible = true;
    },
    submitFixedTimeForm(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          });
          return
        }
        this.fixedTimeDialogVisible = false;
        let { percentage } = this.fixedTimeForm;
        percentage = +(percentage.replace('%',''))
        service({
          url: '/traffic/recording/config/fixedTime',
          method: 'POST',
          data: {
            ...this.fixedTimeForm,
            percentage
          }
        }).then( res => {
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.fixedTimeForm = {
            date: '',
            time: '',
            percentage: ''
          }
        })
      })
    },
    getApiList(query,cb) {
      service({
        url: '/apiinfo/list',
        method: 'POST',
        data: {
          serviceName: query,
          pathString: query,
          urlString: query,
          name: query
        }
      }).then(res => {
        const infoList = res && res.infoList;
        const list = infoList.map(item => {
          return {
            value: item.url
          }
        })
        cb(list)
      })
    },
    fetchApiSelect(param) {
      this.form.gatewaySource.url = param.value;
    },
    initialize() {
      this.form = {
        name: '',
        sourceType: 1,
        gatewaySource: {
          envType: '',
          url: ''
        },
        dubboSource: {
          serviceName: '',
          methods: '',
          group: '',
          version: ''
        },
        recordingStrategy: '',
        percentage: '',
        headers: [{}],
        uid: '',
        saveDays: ''
      };
      this.urlOptions = []
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
.el-dropdown-styled{
  margin-left: 10px;
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
.form_headers {
  width: 75%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 8px;
  .label {
    width: 160px;
  }
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
<style lang="scss">
.form_headers {
  .el-input.is-disabled .el-input__inner {
    text-align: center
  }
}
</style>