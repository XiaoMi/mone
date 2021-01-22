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
    <div class="periodic-contain">
      <el-button size="mini" @click="handleAddRow" class="newly-added">新增</el-button>
      <el-button size="mini" @click="handleChangeState({},'delAll')" class="newly-added">全部删除</el-button>
      <el-table stripe :data="formData" style="width: 100%" class="table-list api-table-list">
          <el-table-column prop="scheduleId" label="scheduleId" width="80"></el-table-column>
          <el-table-column 
            prop="name" 
            label="名称" 
            width="100"
            header-align="center" 
          ></el-table-column>
          <el-table-column prop="capacity" label="capacity" width="100" show-overflow-tooltip></el-table-column>
          <el-table-column prop="cron" label="cron" width="90"></el-table-column>
          <el-table-column prop="domain" label="domain" width="120"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="150"></el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="150"></el-table-column>
          <el-table-column fixed="right" label="操作" width="220">
            <template slot-scope="scope">
                <el-button @click="handleChangeState(scope.row,'start')" type="text" class="el-button--blue" size="small" v-if="!scope.row.active">开始</el-button>
                <el-button @click="handleChangeState(scope.row,'stop')" type="text" class="el-button--blue" size="small" v-else>暂停</el-button>
                <el-button @click="handleEditorRow(scope.row)" type="text" class="el-button--blue" size="small">编辑</el-button>
                <el-button @click="handleChangeState(scope.row,'del')" type="danger" class="el-button--blue" size="small">删除</el-button>
            </template>
          </el-table-column>
      </el-table>
      <!-- 添加&编辑, v-if保证组件每次都创建新的组件-->
      <periodic-deployment-add
          v-if="apiInfoEditorVisible"
          :formApiInfo="formApiInfo"
          :title="apiInfoTitle"
          :projectId='projectId'
          :envId='envId'
          :api-info-editor-visible.sync="apiInfoEditorVisible"
          @submitSuccess="handleCurrentChange"
          @apiInfoEditorClose='handleDialogClose'
      />
    </div>
</template>

<script>
import service from '@/plugin/axios/index'
import PeriodicDeploymentAdd from './components/periodic-deployment-add'
import bizutil from '@/common/bizutil'

export default {
  props: {
    projectId: {
      type: [Number, String],
      required: true,
    },
    envId: {
      type: [Number, String],
      required: true,
    }
  },
  components: { 
    PeriodicDeploymentAdd, 
  },
  data () {
    return {
      formData: [],
      apiInfoEditorVisible : false,
      formApiInfo:{}
    }
  },
  created () {
    this.getAllSchedule();
  },
  methods: {
    //新增
    handleAddRow(){
      this.apiInfoTitle = '新建'
      this.apiInfoEditorVisible = true
      this.formApiInfo.flag = 1
    },
    //编辑
    handleEditorRow(row){
      this.apiInfoTitle = '编辑'
      this.apiInfoEditorVisible = true
      this.formApiInfo = row
      this.formApiInfo.flag = 0
    },
    //开始 暂停 删除  全部删除
    handleChangeState(row,flag) {
      const envId = this.envId;
      const projectId = this.projectId;
      let text , url , scheduleId
      if(row.scheduleId) {
        scheduleId = row.scheduleId;
      }
      if(flag === 'start'){
        text = '开始'
        url = `/schedule/start?scheduleId=${scheduleId}`
      }else if(flag === 'stop'){
        text = '暂停'
        url = `/schedule/pause?scheduleId=${scheduleId}`
      }else if(flag === 'del'){
        text = '删除'
        url = `/schedule/delete?scheduleId=${scheduleId}`
      }else{
        text = '全部删除'
        url = `/schedule/deleteAll?envId=${envId}&projectId=${projectId}`
      }
      this.$confirm(`确认${text}?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: url,
          method: 'post',
        }).then(res => {
            this.$message({
              message: `${text}成功`,
              type: 'success'
            })
            this.getAllSchedule()
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: `已取消${text}`
        })
      })
    },
    getAllSchedule () {
      const envId = this.envId;
      const projectId = this.projectId;
      service({
        url: `/schedule/getAll?envId=${envId}&projectId=${projectId}`,
        method: 'get',
      }).then( res => {
        if (res) {
          this.formData = res.map((ele)=>{
            ele.startTime = bizutil.timeFormat(ele.startTime)
            ele.endTime = bizutil.timeFormat(ele.endTime)
            return ele
          })     
        }
      })
    },
    handleCurrentChange (val) {
      // if(!!val){
      //   this.pageNo=val;
      // }
      this.getAllSchedule()
    },
    handleDialogClose(){
      this.formApiInfo = {};
    }
  }
}
</script>

<style lang="scss" scoped>
.periodic-contain{
  .newly-added {
    margin-bottom: 16px;
  }
}
</style>