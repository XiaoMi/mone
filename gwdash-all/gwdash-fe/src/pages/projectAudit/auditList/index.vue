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
 <d2-container id="wrapper">
   <project-header
      :firstFloor='firstFloor'
      :secondFloor='secondFloor'
   />
  <project-search
    :fetch-data="getList"
    :params-filed="paramsFiled"
    ref="search"
  />
   <d2-module>
      <el-table stripe class='table-list' id="table_id" :data="list" @selection-change="handleSelectionChange">
        <el-table-column prop="projectName" label="项目名称"></el-table-column>
        <el-table-column prop="commitId" width="120" label="commit_id"></el-table-column>
        <el-table-column label="url">
          <template slot-scope="scope">
              <a :href="getURL(scope.row)" target="_blank" rel="noopener noreferrer">{{scope.row.url}}</a>
          </template>
        </el-table-column>
        <el-table-column prop="submitter" label="提交人" width="150"></el-table-column>
        <el-table-column prop="ctime" width="150" label="提交时间"></el-table-column>
        <el-table-column fixed="right" label="操作" width="160">
          <template slot-scope="scope">
              <el-button size="mini" class="el-button--blue" @click="handlePassClick(scope.row)">通过</el-button>
              <el-button
                size="mini"
                type="danger"
                class="el-button--orange"
                @click="handleRejectClick(scope.row)"
              >驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pager.page'
        :pageSize='pager.pageSize'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
   </d2-module>
   <el-dialog title="提示" :visible.sync="dialogVisible" width="400px" :before-close="handleClose">
        <span>确认该项目审核通过吗？</span>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="handleAdopt" size="mini" class="dialog-button-left">确认通过</el-button>
            <el-button @click="dialogVisible = false" size="mini" class="dialog-button-right">取消</el-button>
        </span>
   </el-dialog>
   <el-dialog title="驳回理由" :visible.sync="formVisible" width="400px" :before-close="handleClose1">
       <el-input
        type="textarea"
        :rows="4"
        maxlength="100"
        resize="none"
        placeholder="100字以内"
        v-model="remark">
        </el-input>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="handleReject"  class="dialog-button-left" size="mini">确认驳回</el-button>
            <el-button @click="formVisible = false" class="dialog-button-right" size="mini">取消</el-button>
        </span>
   </el-dialog>
   <el-dialog title="提示" :visible.sync="repeatVisible" width="400px" :before-close="handleClose2">
        <span>该项目已由 <span class='approved-people'>{{msgData}}</span> 审核完成</span>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="repeatVisible = false" class="dialog-button-left" size="mini">确认</el-button>
        </span>
   </el-dialog>

 </d2-container>
</template>

<script>

import projectHeader from "./../../../layout/header"
import projectSearch from "./../../../layout/search"
import service from "@/plugin/axios/index"
import bizutil from "@/common/bizutil"

const initParams = [
  {
    label: '提交人',
    key: 'submitter',
    comp: 'el-input',
    optComp: 'el-option',
    value: undefined,
    styles: 'margin-right:20px;',
    placeHolder: '请输入提交人邮箱'
  }
]

export default {
  name: "auditList",
  data () {
    return {
      firstFloor: '项目审核',
      secondFloor: '审核列表',
      member: '提交人',
      paramsFiled: JSON.parse(JSON.stringify(initParams)),
      pager: {
        page: 1,
        pageSize: 10,
        total: 1
      }, // 分页项
      list: [], // 列表
      dialogData: "",
      dialogData1: "",
      dialogData2: "",
      dialogVisible: false, // 通过
      formVisible: false, // 驳回
      repeatVisible: false, // 重复审核
      multipleSelection: [],
      pageDisabled: false, // 分页控制
      remark: '', // 驳回理由
      state2: '', // 提交人邮箱搜索
      msgData: ''
    }
  },
  components: {
    projectHeader,
    projectSearch
  },
  activated () {
    this.getList(this.$refs.search.searchParams)
  },
  methods: {
    handleCurrentChange (val) { // 分页变化
      this.pager.page = val
      this.getList(this.$refs.search.searchParams)
    },
    getList (params, flag) { // 初始化表格
      if (flag) {
        this.pager.page = 1
      }
      this.pageDisabled = true
      let { page, pageSize } = this.pager
      let url = `/test/review/page?page=${page}&pageSize=${pageSize}`
      if (params) {
        for (let key in params) {
          url = `${url}&${key}=${params[key]}`
        }
      }
      service({
        url: url,
        method: "GET"
      }).then(res => {
        this.list = res.list
        this.list = this.fix(res.list)
        this.pager.total = res.total
        this.pageDisabled = false
      })
    //   setTimeout(() => {
    //     this.pageDisabled = false;
    //   }, 2000);
    },
    fix (data) {
      let ret = data.map(item => {
        let showItem = Object.assign({}, item)
        showItem.ctime = bizutil.timeFormat(item.ctime)
        showItem.operateTime = bizutil.timeFormat(item.operateTime)
        return showItem
      })
      return ret
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    handleAdopt () {
      let id = this.dialogData.id
      service({
        url: `/test/review/operation?id=${id}&status=1`
      }).then((res) => {
        // this.repeatCheck(res);
        if (typeof res === "boolean") {
          this.dialogVisible = false
          this.getList()
        } else {
          this.msgData = res
          this.dialogVisible = false
          this.repeatVisible = true
          this.getList()
        }
      }).catch(function (error) {
        console.log(error)
      })
    }, // 点击确认通过
    handleReject () {
      console.log(this.dialogData1)
      let id = this.dialogData1.id
      let remark = this.remark
      if (!remark) remark = '无'
      let url = `/test/review/operation?id=${id}&remarks=${remark}&status=2`
      service({
        url: url,
        method: 'get'
      }).then(res => {
        if (typeof res === "boolean") {
          this.formVisible = false
          this.remark = ''
          this.getList()
        } else {
          this.msgData = res
          this.formVisible = false
          this.repeatVisible = true
          this.remark = ''
          this.getList()
        }
      })
    }, // 点击确认驳回
    repeatCheck (data) {
      // console.log(data)
      // if(data.code === 200014) {
      //     this.msgData = data.data;
      //     this.repeatVisible=true;
      // }
    }, // 是否重复校验
    handlePassClick (data) { // 点击通过按钮
    //   console.log(data)
      this.dialogData = data
      this.dialogVisible = true
    },
    handleRejectClick (data) { // 点击驳回按钮
      this.dialogData1 = data
      this.formVisible = true
    },
    handleClose (e) {
      this.dialogData = ""
      this.dialogVisible = false
    },
    handleClose1 (e) {
      this.dialogData1 = ""
      this.formVisible = false
    },
    handleClose2() {
      this.msgData = "";
      this.repeatVisible = false;
    },
    getURL (data) {
      return data.url
    }
  }
}
</script>

<style lang="scss" scoped>
#wrapper {
    .font-demo {
        letter-spacing: 0px;
        font-family: PingFang SC;
        font-weight: regular;
        text-align: center;
    }

    /deep/ .el-dialog__wrapper{
        .el-dialog {
            width: 400px;
            .el-dialog__header {
                border-left: 4px solid #457BFC;
                .el-dialog__title {
                    @extend .font-demo;
                    color: #333333;
                    font-weight: medium;
                    font-size: 14px;
                    line-height: normal;
                    text-align: left;
                }
            }
            .el-dialog__body {
                @extend .font-demo;
                border-top: 1px solid #EFF0F4;
                color: #333333;
                font-size: 13px;
                .approved-people {
                    font-weight: bolder;
                }
            }
            .el-dialog__footer {
                text-align: center;
                .el-button {
                    @extend .font-demo;
                    width: 76px;
                    font-size: 12px;
                    border-radius: 2px;
                }
                .dialog-button-left{
                    color: #FFFFFF;
                    background: #457BFC;
                }
                .dialog-button-right{
                    color: #333333;
                    background: #EEEEEE;
                }
            }
        }
    }

     /deep/ .el-table:not(.el-table--scrollable-x) {
        .el-table__fixed-right {
            height: 100% !important;
        }
    }
}
</style>
