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
        <el-form :model="qForm" size="mini">
          <el-form-item>
            <el-input v-model="qForm.query" size="mini" placeholder='请输入关键字'/>
          </el-form-item>
        </el-form>
        <el-button @click="query" size="mini" style='margin-left:10px'>查询</el-button>
      </div>
    </d2-module>
    <d2-module>
    <div class="table-list">
      <el-table :data="tableList" style="width: 100%">
        <el-table-column prop="id" label="id" width="80"></el-table-column>
        <el-table-column prop="title" label="title" width="120"></el-table-column>
        <el-table-column prop="authorName" label="创建者" width="120"></el-table-column>
        <el-table-column prop="updaterName" label="更新者" width="120"></el-table-column>
        <el-table-column label="类型" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.type == 1">文档</span>
            <span v-else-if="scope.row.type == 3">issue</span>
          </template>
        </el-table-column>
        <el-table-column prop="ctime" label="创建时间" width="160"></el-table-column>
        <el-table-column prop="utime" label="更新时间" width="160"></el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template slot-scope="scope">
            <!-- 文档 -->
            <router-link
              v-if="scope.row.type == 1"
              class="el-button el-button--text"
              :to="{ path: `/wiki/doc/detail/1/${scope.row.projectId}`}">
              详情
            </router-link>
            <!-- issue -->
            <router-link
              v-else-if="scope.row.type == 3"
              class="el-button el-button--text"
              :to="{ path: `/wiki/issue/${scope.row.projectId}/${scope.row.id}`}">
              详情
            </router-link>
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
  </d2-container>
</template>
<script>
import request from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'

export default {
  data () {
    return {
      total: 10,
      page: 1,
      pageSize: 10,
      qForm: {
        query: ''
      },
      tableList: [],
      pageDisabled: false
    }
  },
  created () {
    this.query();
  },
  methods: {
    query () {
      this.pageDisabled = true;
      const query = (this.qForm.query || '').trim()
      request({
        url: '/comment/fuzzy/query',
        method: 'post',
        data: {
          title: this.qForm.query,
          page: this.page,
          pageSize: this.pageSize,
          type: 1
        }
      }).then(data => {
        if (!Array.isArray(data && data.list)) return
        this.total = data.total
        this.tableList = data.list.map(item => {
          return {
            ...item,
            ctime: bizutil.timeFormat(item.ctime),
            utime: bizutil.timeFormat(item.utime)
          }
        })
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false;
      }, 2000)
    },
    handleCurrentChange (val) {
      this.page = val;
      this.query()
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