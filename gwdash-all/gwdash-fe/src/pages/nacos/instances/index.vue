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
      <div class='header'>
        <el-form size='mini' @submit.native.prevent>
          <el-form-item>
            <el-input v-model='searchWord' placeholder="检索服务名 如 tesla" @change="search"/>
          </el-form-item>
        </el-form>
      </div>
    </d2-module>
    <d2-module>
      <el-table :data="serviceList" :row-class-name="rowStatus" class="table-list">
        <el-table-column prop="name" label="name"></el-table-column>
        <el-table-column prop="clusterCount" label="clusterCount" width="150"></el-table-column>
        <el-table-column prop="ipCount" label="ipCount" width="150"></el-table-column>
        <el-table-column prop="healthyInstanceCount" label="healthyInstanceCount" width="200"></el-table-column>
        <el-table-column width="200" label="operate">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.healthyInstanceCount != 0 && scope.row.ipCount != 0"
              @click="fetchDeatil(scope.row.name)"
              size="small"
            >detail</el-button>
          </template>
        </el-table-column>
      </el-table>
    </d2-module>
    <el-dialog
      title="detail"
      width="80%"
      :visible.sync="instanceDetail.length != 0"
      @close="instanceDetail.length = 0"
    >
      <el-table :data="instanceDetail" stripe class="table-list">
        <el-table-column prop="instanceId" label="instanceId"></el-table-column>
        <el-table-column prop="serviceName" label="serviceName"></el-table-column>
        <el-table-column prop="ip" label="ip"></el-table-column>
        <el-table-column prop="port" label="port"></el-table-column>
        <el-table-column prop="clusterName" label="clusterName"></el-table-column>
        <el-table-column label="metadata" width="300">
          <template slot-scope="scope">{{JSON.stringify(scope.row.metadata)}}</template>
        </el-table-column>
        <el-table-column prop="healthy" label="healthy">
          <template slot-scope="scope">{{scope.row.healthy ? 'true' : 'false'}}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"

export default {
  data () {
    return {
      searchWord: "",
      serviceList: [],
      instanceDetail: []
    }
  },

  methods: {
    search () {
      this.getServiceList()
    },
    getServiceList () {
      service({
        url: "/nacos/service/list",
        method: "GET",
        params: {
          keyword: this.searchWord
        }
      }).then(res => {
        const json = JSON.parse(res)
        this.serviceList = json.serviceList
      })
    },
    fetchDeatil (serviceName) {
      service({
        url: "/nacos/instances/detail",
        method: "POST",
        data: {
          serviceName
        }
      }).then(res => {
        if (res && res.length !== 0) {
          this.instanceDetail = res
        }
      })
    },
    rowStatus ({ row, rowIndex }) {
      if (row.healthyInstanceCount === 0 && row.ipCount === 0) {
        return "warning-row"
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
  height: 30px;
}
</style>
<style>
.el-table .warning-row {
  background: oldlace;
}
</style>
