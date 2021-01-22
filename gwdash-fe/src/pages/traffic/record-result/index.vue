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
  <d2-container class='record-result'>
    <d2-module margin-bottom>
      <div class="header">
        <div class='title'>录制配置ID：</div>
        <el-input
           size='mini'
           v-model.number='searchObj.recordingConfigId' 
           @keypress.native.enter='handleInput'
           placeholder='请输入'
           class='input_margin'/>
        <el-button 
           size='mini'
           @click="handleInput">查询</el-button> 
        <!-- <el-input
           style='width:20%; margin-right:10px'
           size='mini'
           v-model='searchObj.time' 
           @change='handleInput'
           placeholder='支持录制时间查询'/> -->
      </div>
    </d2-module>
    <d2-module>
      <el-table stripe :data='tableData' class='table-list'>
        <el-table-column type='expand'>
          <template slot-scope='props'>
            <el-form label-position="left" class="table-form-expand">
              <el-form-item label="录制配置ID">
                <span>{{ props.row.recordingConfigId }}</span>
              </el-form-item>
              <el-form-item label="invokeBeginTime">
                <span>{{ props.row.invokeBeginTime }}</span>
              </el-form-item>
              <el-form-item label="invokeEndTime">
                <span>{{ props.row.invokeEndTime }}</span>
              </el-form-item>
              <el-form-item label="response">
                <el-button @click='showResponseData(props.row)' type="primary" plain>查看详情</el-button>
              </el-form-item>
              <el-form-item :label="props.row.sourceType === 1 ? 'httpTraffic' : 'dubboTraffic'">
                <el-button @click='showSourceTypeData(props.row)' type="primary" plain>查看详情</el-button>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="id" prop="id" width="80"></el-table-column>
        <el-table-column label="录制途径" prop="sourceType" width="120">
           <template slot-scope='scope'>
            <div v-if='scope.row.sourceType === 1'>网关接口</div>
            <div v-if='scope.row.sourceType === 2'>dubbo接口</div>
          </template>
        </el-table-column>
        <el-table-column label="uid" prop="uid" width="120"></el-table-column>
        <el-table-column label="traceId" prop="traceId" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column label="创建人" prop="creator" width="160"></el-table-column>
        <el-table-column label="更新人" prop="updater" width="160"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160"></el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="160"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="editRecordResult(scope.row)">编辑</el-button>
            <el-button type='text' size='mini' @click="oneStepPlayBack(scope.row)">单步回放</el-button>
            <el-button type="text" size="mini" @click="playBackResult(scope.row.id)">回放结果</el-button>
            <el-button type="danger" size="mini" class='danger' @click="deleteRecordResult(scope.row.id)">删除</el-button>
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

    <el-dialog title='response' :visible.sync='responseDataVisible' width='880px'>
      <codemirror v-model="responseData" :options="codeMirrorOptions" class='codeMirror_content'/>
    </el-dialog>

    <el-dialog :title='`${sourceTypeTitle}信息`' :visible.sync='sourceTypeDataVisbile' width='880px'>
      <el-card class='card'>
        <div v-for='(value,key) in sourceTypeData' :key='key' class='card-row'>
          <span class='card-row_key'>{{ key }}:</span>
          <span class='card-row_value' :title='`${JSON.stringify(value)}`'>{{ value }}</span>
        </div>
      </el-card>
    </el-dialog>

    <!-- 单步回放 -->
    <one-step
     :content="oneStepContent"
     :show='oneStepShow'
     @doCloseDialog='oneStepCloseDialog'/>

    <el-dialog title='回放结果' :visible.sync='backResultDialogVisible' width='880px' :before-close='handleBackResultClose'>
      <codemirror v-model="codeMirrorResultData" :options="codeMirrorOptions" class='codeMirror_content'/>
    </el-dialog>


    <el-dialog title='编辑' :visible.sync='editDialogVisible' width='880px' :before-close='handleEditClose'>
      <div>
        <div class='codeMirror_title'>headers</div>
        <codemirror v-model="codeMirrorEditHeaders" :options="codeMirrorOptions" class='codeMirror_content'/>
      </div>
      <div>
        <div class='codeMirror_title'>body</div>
        <codemirror v-model="codeMirrorEditBody" :options="codeMirrorOptions" class='codeMirror_content'/>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitEditContent" size="mini">确 定</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios"
import bizutil from '@/common/bizutil'
import oneStep from './components/one-step'

export default {
  data() {
    return {
      page: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
      searchObj: {
        recordingConfigId: '',
        time: ''
      },
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        // mode: 'text/javascript',
        theme: 'base16-dark',
        // readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      backResultDialogVisible: false,
      codeMirrorResultData: '',
      editDialogVisible: false,
      codeMirrorEditHeaders: '',
      codeMirrorEditBody: '',
      oneStepShow: false,
      oneStepContent: '',
      responseDataVisible: false,
      sourceTypeDataVisbile: false,
      sourceTypeTitle: '',
      sourceTypeData: {}
    }
  },
  components: {
    oneStep
  },
  beforeRouteEnter(to,from,next){
    if(to.query && to.query.id) {
      next(vm => {
        vm.searchObj.recordingConfigId = +(to.query.id);
        vm.getInitList()
      })
    } else {
      next(vm => {
        vm.getInitList()
      })
    }
  },
  methods: {
    getInitList() {
      service({
        url: '/traffic/list',
        method: 'POST',
        data: {
          page: this.page,
          pageSize: this.pageSize,
          recordingConfigId: this.searchObj.recordingConfigId
        }
      }).then(res => {
        this.page = res.page;
        this.pageSize = res.pagesize;
        this.total = res.total;
        this.tableData = res.list.map(item => {
          return {
            ...item,
            createTime: bizutil.timeFormat(item.createTime),
            updateTime: bizutil.timeFormat(item.updateTime),
            invokeBeginTime: bizutil.timeFormat(item.invokeBeginTime),
            invokeEndTime: bizutil.timeFormat(item.invokeEndTime)
          }
        })
      })
    },
    editRecordResult(row){
      const { id,sourceType,httpTraffic,dubboTraffic } = row;
      this.editId = id;
      const data = sourceType === 1 ? httpTraffic : dubboTraffic;
      const { originHeaders, orginBody } = data;
      if (originHeaders && orginBody) {
        this.codeMirrorEditHeaders = JSON.stringify(originHeaders,null,2);
        this.codeMirrorEditBody = orginBody
      }
      this.editDialogVisible = true 
    },
    submitEditContent() {
      let headers = this.codeMirrorEditHeaders || '{}';
      let newHeaders = JSON.parse(headers);
      let newBody = this.codeMirrorEditBody;
      service({
        url: '/traffic/recording/traffic/update',
        method: 'POST',
        data: {
          newHeaders,
          newBody,
          id: this.editId
        }
      }).then(res => {
        this.$message.success('编辑成功');
        this.editDialogVisible = false
      })
    },
    oneStepPlayBack(row){
      this.oneStepContent = row;
      this.oneStepShow = true
    },
    playBackResult(id){
      this.backResultDialogVisible = true;
      service({
        url: '/traffic/recording/traffic/last/result',
        method: 'POST',
        data: {id}
      }).then(res => {
        this.codeMirrorResultData = JSON.stringify(res.data,null,2)
      })
    },
    deleteRecordResult(id){
      this.$confirm("此操作将永久删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: "/traffic/recording/traffic/del",
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
    handleInput() {
      this.page = 1;
      this.pageSize = 10;
      this.getInitList()
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getInitList()
    },
    handleBackResultClose() {
      this.backResultDialogVisible = false;
      this.codeMirrorResultData = ''
    },
    handleEditClose() {
      this.editDialogVisible = false;
    },
    oneStepCloseDialog(val) {
      this.oneStepShow = val
    },
    showResponseData(row) {
      const { response } = row;
      this.responseData = JSON.stringify(JSON.parse(response),null,2);
      this.responseDataVisible = true
    },
    showSourceTypeData(row) {
      const { sourceType, httpTraffic, dubboTraffic } = row;
      this.sourceTypeTitle = sourceType === 1 ? '网关接口' : 'dubbo接口';
      this.sourceTypeData = sourceType === 1 ? httpTraffic : dubboTraffic;
      this.sourceTypeDataVisbile = true
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
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
.codeMirror_title {
  font-size: 16px;
  color: #909399;
  font-weight: 700;
  margin-bottom: 10px;
}
.codeMirror_content {
  margin-left: 16px;
  margin-bottom: 20px;
}
.card {
  &-row {
    height: 22px;
    display: flex;
    &_key {
      display: inline-block;
      height: 22px;
      line-height: 22px;
      font-size: 15px;
      color: #909399;
      font-weight: 700;
      margin-right: 10px;
    }
    &_value {
      display: inline-block;
      cursor: pointer;
      font-size: 14px;
      width: 600px;
      height: 22px;
      line-height: 22px;
      overflow: hidden;
      color: rgb(102, 102, 102);
    }
  }
}
</style>
<style lang="scss">
.table-form-expand {
  padding-left: 75px;
  .el-form-item {
    width: 50%;
    margin-bottom: 0;
    label {
      width: 90px;
      color: #99a9bf
    }
    div {
      color: #99a9bf
    }
  }
}
.record-result {
  .codeMirror_content, 
  .CodeMirror {
    height: 380px;
  }
}
</style>