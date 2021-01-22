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
      <el-table stripe class='table-list' :data="list" @selection-change="handleSelectionChange">
        <el-table-column prop="projectName" label="项目名称"></el-table-column>
        <el-table-column prop="commitId" width="120" label="commit_id"></el-table-column>
        <el-table-column prop="url" label="url" width="220"></el-table-column>
        <el-table-column prop="submitter" label="提交人"></el-table-column>
        <el-table-column prop="ctime" label="提交时间" width="140px"></el-table-column>
        <el-table-column prop="operator" label="审核人"></el-table-column>
        <el-table-column prop="operateTime" label="审核时间" width="140px"></el-table-column>
        <el-table-column label="审核状态">
          <template slot-scope="scope">
                    <span v-if="scope.row.status == 1">通过</span>
                    <span v-else-if="scope.row.status == 2">驳回
                      <el-tooltip placement="bottom-end" effect="light" offset=''>
                        <div slot="content" class="reject-tooltip">驳回原因：<br/>{{scope.row.remarks}}</div>
                        <i class="el-icon-question"></i>
                      </el-tooltip>
                    </span>
                     <span v-if="scope.row.status == 4">紧急发布</span>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pager.pageNo'
        :pageSize='pager.pageSize'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
   </d2-module>
 </d2-container>
</template>

<script>
import projectSearch from "./../components/search"
import projectHeader from "./../components/header"
import service from "@/plugin/axios/index";
import bizutil from "@/common/bizutil"
const initParams = [
   {
    label: '相关人员',
    key: 'relevanter',
    comp: 'el-input',
    optComp: 'el-option',
    value: undefined,
    styles: 'margin-right:20px;',
    placeHolder: '请输入相关人员邮箱'
  },
  {
    label: '审核状态',
    key: 'status',
    comp: 'el-select',
    optComp: 'el-option',
    styles: 'margin-right:20px;',
    value: -1,
    options: [
      {
        label: '全部',
        value: -1
      },
      {
        label: '通过',
        value: 1
      },
      {
        label: '驳回',
        value: 2
      },
      {
        label: '紧急发布',
        value: 4
      },
    ]
  },
]
export default {
  name: "operationLog",
  data() {
    return {
      firstFloor:'项目审核',
      secondFloor:'操作日志',
      pager: {
        pageNo: 1,
        pageSize: 10,
        total: 1
      }, //分页
      paramsFiled: JSON.parse(JSON.stringify(initParams)),
      list: [],//列表
      dialogData: "",//提示框信息
      multipleSelection: [],
      pageDisabled: false,//分页控制
      input:'',//相关人员绑定值
      options: [ {
          num : -1,
          label: '全部'
        },{
          num:1,
          label: '通过'
        }, {
          num:2,
          label: '驳回'
        }, {
          num:4,
          label: '紧急发布'
        },],//审核状态配置项
      value: '',//审核状态绑定值
    };
  },
  components:{
    projectHeader,
    projectSearch
  },
  activated() {
      this.getList(this.$refs.search.searchParams);
  },
  methods: {
    handleCurrentChange(val,params) {
      this.pager.pageNo = val;
      this.getList(this.$refs.search.searchParams);
    },//分页变化
    getList(params,flag) {
      if(flag) {
          this.pager.pageNo = 1;
      }
      let { pageNo, pageSize } = this.pager;
      this.pageDisabled = true;;
      let url = `/test/review/log?page=${pageNo}&pageSize=${pageSize}`;
      if(params){
        for(let key in params){
           url = `${url}&${key}=${params[key]}` 
           console.log(key,'key',url)
        }
      }
      service({
        url: url,
        method: "GET"
      }).then(res => {
        this.list = this.fix(res.list);
        this.pager.total = res.total;
        this.pageDisabled = false
      });
      // setTimeout(() => {
      //   this.pageDisabled = false;
      // }, 2000);
    },//初始化表格
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    fix(data) {
      let ret = data.map(item => {
        let showItem = Object.assign({}, item);
        showItem.ctime = bizutil.timeFormat(item.ctime);
        showItem.operateTime = bizutil.timeFormat(item.operateTime);
        return showItem;
      });
      return ret;
    },
    handleClick(data) {
      this.dialogData = JSON.stringify(data.content, null, 4);
      this.dialogVisible = true;
    },
    handleClose() {
      this.dialogData = "";
      this.dialogVisible = false;
    }
  }
};
</script>

<style lang="scss" scoped>
#wrapper {
  .header-bar {
      .header-bar-right {
          justify-content: flex-end;
          display: flex;
              .header-bar-right-input {
                  width: 180px;
                  margin-right: 20px;
              } 
              .header-bar-right-title {
                  line-height: 28px;
                  height: 28px;
                  color: #333333;
                  font-family: PingFang SC;
                  font-weight: regular;
                  font-size: 13px;
                  letter-spacing: 0px;
              }
              .el-button--small {
                  height: 28px;
                  line-height: 4px;
                  font-size: 12px;
                  font-weight: 300;
                  border-radius: 3px;
              }
      }
  }
  .table-list {
    .cell {
      position: relative;
      .el-icon-question {
        position: absolute;
        top: 42%;
        color: #C1C1C1;
        // &:hover {
        //   color: #515151;
        // }
      }
    }
  }
}
.reject-wrapper {
  border: 0px;
  box-shadow: 0px 2px 7px 0 #4A90E2;
  .reject-tooltip {
          background: #FFFFFF;
          max-width: 128px;
  }
}
</style>