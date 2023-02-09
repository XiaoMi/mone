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
        <el-input
          style='width:20%; margin-right:10px'
          size='mini'
          v-model='searchWorld'
          placeholder='请输入taskId'/>
        <el-button
          size='mini'
          @click='searchHealthInfo'>查询</el-button>
        <el-button
          size='mini'
          type='danger'
          @click="closeHealthCheck">关闭健康监测</el-button>
      </div>
    </d2-module>

    <d2-module>
      <el-table stripe :data="tableData" class="table-list">
        <el-table-column label="projectId" prop="id" width="160" ></el-table-column>
        <el-table-column label="项目名" prop="name" width="220"></el-table-column>
        <el-table-column label="项目描述" prop="desc" width="220" show-overflow-tooltip></el-table-column>
         <el-table-column label="环境名" prop="envName" width="220"></el-table-column>
          <el-table-column label="taskId" prop="taskId" width=""></el-table-column>
      </el-table>
    </d2-module>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
export default {
  data () {
    return {
      tableData: [],
      searchWorld: ''
    }
  },
  methods: {
    searchHealthInfo () {
      const searchWorld = +(this.searchWorld)
      if (this.searchWorld === '') {
        this.$message.warning('taskId不能为空，请输入')
        return
      }
      if (isNaN(searchWorld)) {
        this.$message.warning('taskId仅支持Number类型')
        return
      }
      let info = this.tableData.find(item => item.taskId === searchWorld)
      if (info) { return }
      service({
        url: `/health/check/info?taskId=${this.searchWorld}`
      }).then(res => {
        if (Object.keys(res).length === 0) {
          this.$message.warning('此taskId暂无健康监测相关信息')
          return
        }
        let { project, projectEnv: { healthCheckTaskId: taskId, name: envName } } = res
        const healthInfo = {
          ...project,
          taskId,
          envName
        }
        this.tableData.push(healthInfo)
      })
    },
    closeHealthCheck () {
      let info = this.tableData.find(item => item.taskId === +(this.searchWorld))
      if (!info) { return }
      service({
        url: `/health/check/pause?taskId=${this.searchWorld}`
      }).then(res => {
        if (res) {
          this.$message.success(`已关闭taskId为${this.searchWorld}的健康监测`)
          this.tableData = this.tableData.filter(item => item.taskId !== +(this.searchWorld))
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.table-list-con{
  height: 100%;
}
</style>
