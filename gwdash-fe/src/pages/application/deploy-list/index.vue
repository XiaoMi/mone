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
    <d2-module>
      <div class="table-list">
        <el-table v-if="isRefresh" :data="list">
          <el-table-column prop="id" label="id" width="40px"></el-table-column>
          <el-table-column prop="jarName" label="部署jar包"></el-table-column>
          <el-table-column label="部署状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.statusType">{{scope.row.statusTxt}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operatio" label="部署动作" width="100px"></el-table-column>
          <el-table-column prop="ctimeFormat" label="创建时间"></el-table-column>
          <el-table-column prop="utimeFormat" label="更新时间"></el-table-column>
        </el-table>
        <d2-pagination
          marginTop
          :currentPage='page'
          :pageSize='pageSize'
          :total='total'
          @doCurrentChange='handleCurrentChange'>
        </d2-pagination>
      </div>
    </d2-module>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index";
import bizutil from "@/common/bizutil";
import statusMap from "./status-map";
import qs from "qs";

export default {
  data() {
    return {
      projectId: this.$route.query.projectId,
      jarId: this.$route.query.jarId || 0,
      needFresh: false,
      deployDialogVisible: false,
      isRefresh: true,
      deployForm: {},
      list: [],
      clientList: [],
      total: 0,
      page: 1,
      pageSize: 10
    }
  },
  created() {
    this.getListAndFresh();
  },
  methods: {
    getList() {
      this.needFresh = false;
      const projectId = this.projectId;
      const jarId = this.jarId;
      const page = this.page;
      const pageSize = this.pageSize;
      return request({
        url: `/project/deploylist?projectId=${projectId}&id=${jarId}&page=${page}&pageSize=${pageSize}`
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        this.total = res.total;
        this.list = res.list.map(item => {
          this.needFresh = item.status == 1;
          return {
            ...item,
            name: this.name,
            statusTxt: statusMap[item.status].name,
            statusType: statusMap[item.status].type,
            ctimeFormat: bizutil.timeFormat(item.ctime),
            utimeFormat: bizutil.timeFormat(item.utime)
          };
        });
        return this.needFresh;
      });
    },
    getListAndFresh() {
      clearTimeout(this.timer);
      this.getList().then(needFresh => {
        if (needFresh) this.refreshBuildList();
      });
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getListAndFresh();
    },
    refreshBuildList() {
      this.timer = setTimeout(() => {
        this.getList().then(needFresh => {
          if (needFresh) this.refreshBuildList();
        });
      }, 6000);
    }
  }
};
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.status-label {
  text-align: center;
}
.link-button {
  a {
    color: #409eff;
  }
}
</style>