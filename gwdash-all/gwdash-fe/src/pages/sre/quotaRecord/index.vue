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
<div>
   <h3>quota记录</h3>
  <d2-module>
     <el-table
    :data="records"
    size="mini"
    border
    style="width: 100%">
    <el-table-column
      fixed
      prop="id"
      label="id"
      width="100"
      align="center"

      >
    </el-table-column>
     <el-table-column

      prop="ip"
      label="ip"
      align="center"
      width="100"
      >
    </el-table-column>
     <el-table-column

      prop="type"
      label="type"
      align="center"
      width="140"
      >
    </el-table-column>

     <el-table-column
      label="status"
      width="80"
      align="center"
      >
         <template slot-scope="scope">
           <el-tag :type="scope.row.status==1?'danger':'success'">{{scope.row.status==1?'失败':'成功'}}</el-tag>
         </template>
    </el-table-column>
      <el-table-column

      prop="showCtime"
      label="操作时间"
      align="center"
      width="180"
      >
    </el-table-column>
      <el-table-column
      prop="operator"
      label="operator"
      >
    </el-table-column>
    <el-table-column
      fixed="right"
      label="操作"
      width="180">
      <template slot-scope="scope">
        <el-button @click="showProjectDiff(scope.row)" type="text" size="small">Project变更</el-button>
        <el-button @click="showresourceDiff(scope.row)" type="text" size="small">Resource变更</el-button>
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
  </d2-module>
  <el-dialog
    :title="`${showProjectDialog?'Project':'Resource'}变更信息`"
    :visible.sync="dialogProjectVisible"
    width="70%"
    >
      <div class="diff-box">
       <div class="code-mirror-left code-mirror-box">
         <p>更新前</p>
         <codemirror class="codeMirror" v-model="dialogData.before" :options="cmOptions" ></codemirror>
       </div>
       <div class="code-mirror-right code-mirror-box">
         <p>更新后</p>
         <codemirror class="codeMirror" v-model="dialogData.after" :options="cmOptions" ></codemirror>
       </div>
     </div>

    <span slot="footer" class="dialog-footer">
      <!-- <el-button @click="dialogProjectVisible = false">取 消</el-button> -->
      <el-button type="primary" @click="dialogProjectVisible = false">确 定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
import service from '@/plugin/axios'
// import mockData from "./mockData.js"
import bizutil from "@/common/bizutil"

export default {
  data () {
    return {
      records: [],
      page: 1,
      pageSize: 10,
      total: 10,
      pageDisabled: false,
      dialogProjectVisible: false,
      dialogData: {},
      showProjectDialog: true,
      cmOptions: {
        tabSize: 2,
        indentUnit: 2,
        theme: 'base16-dark',
        lineNumbers: true,
        smartIndent: true,
        height: 100,
        cursorHeight: 0.85,
        readOnly: true,
        // json校验
        mode: 'application/json',
        gutters: ['CodeMirror-lint-markers']
      }
    }
  },
  created () {
    this.getList()
  },
  methods: {
    getList () {
      this.pageDisabled = true
      service({
        url: "/quota/record",
        method: "POST",
        data: {
          page: this.page,
          pageSize: this.pageSize
        }
      })
        .then(res => {
          this.records = this.fix(res.records)
          // 蛋疼 许峥开发接口从来不规范
          // this.page=res.page
          // this.pageSize=res.pageSize
          this.total = res.total
          this.pageDisabled = false
        })
    },
    showProjectDiff (data) {
      this.showProjectDialog = true
      this.dialogData.before = JSON.stringify(data.projectBefore, null, 2)
      this.dialogData.after = JSON.stringify(data.projectAfter, null, 2)
      this.dialogProjectVisible = true
    },
    showresourceDiff (data) {
      this.showProjectDialog = false
      this.dialogData.before = JSON.stringify(data.resourceBefore, null, 2)
      this.dialogData.after = JSON.stringify(data.resourceAfter, null, 2)
      this.dialogProjectVisible = true
    },
    fix (list) {
      return list.map(item => {
        item.showCtime = bizutil.timeFormat(item.ctime)
        return item
      })
    },
    handleCurrentChange (e) {
      this.page = e
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.diff-box{
  display: flex;
  justify-content: space-around;
}
.code-mirror-box{
  width: 48%;
}

</style>
