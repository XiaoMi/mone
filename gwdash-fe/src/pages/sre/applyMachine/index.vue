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
        <el-button size="mini" @click="showDialog">申请机器</el-button>
      </div>
    </d2-module>
    <d2-module>
      <el-table :data="records" stripe class='table-list'>
        <el-table-column fixed label="id" prop="id" width="120"></el-table-column>
        <el-table-column label="suit id" prop="suitId"></el-table-column>
        <el-table-column label="site id" prop="siteId"></el-table-column>
        <el-table-column label="环境" prop="env"></el-table-column>
        <el-table-column label="订单信息" width='220'>
          <template slot-scope="scope">
            {{scope.row.orderId}}
            <el-popover
              style='margin-left:8px;font-size:14px'
              v-if="scope.row.orderId != 0"
              placement="top-start"
              title="详情"
              width="200"
              trigger="hover"
              :content="scope.row.orderRes">
                <i class="el-icon-document" slot="reference"></i>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="操作时间" prop="showCtime" width='220'></el-table-column>
        <el-table-column label="申请人" prop="creator" width='220'></el-table-column>
        <el-table-column label="申请状态" width="80" fixed="right">
          <template slot-scope="scope">
            <el-button @click='showApplyDetail(scope.row.orderId)' type="text" size="small">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="机器初始化" width="160" fixed="right">
          <template slot-scope="scope">
            <el-button @click='initMachine(scope.row.orderId)' type="text" size="small">初始化</el-button>
            <el-button @click='showInitDetail(scope.row.orderId)' type="text" size="small">状态</el-button>
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

    <el-dialog title="申请机器" :visible.sync="dialogVisible" width='800px'>
      <el-form ref="applyForm" :model="form" :rules='applyRules' label-width="110px" size="mini">
        <el-form-item label="环境" prop="env">
          <el-select
            v-model="form.env"
            style='width:50%'
          >
            <el-option label="staging" value="staging"></el-option>
            <el-option label="线上" value="online"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="机型" prop='suit'>
          <el-select
            v-model="form.suit"
            value-key="suitId" style='width:50%'>
            <el-option
              v-for='item in machineList'
              :key='item.suitId'
              :label='item.machine_type'
              :value='item'
              class='commitOption'>
              <span>{{item.machine_type}}</span>
              <span>{{item.cpu}}</span>
              <span>{{item.mem}}</span>
              <span>{{item.disk}}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApplyForm('applyForm')" size='mini'>立即申请</el-button>
      </div>
    </el-dialog>

    <el-dialog title='状态信息' :visible.sync='dialogInfoVisible' width='800px' :before-close="dislogClose">
      <codemirror v-model="codeMirrorContent" :options="codeMirrorOptions"/>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios'
import bizutil from "@/common/bizutil"
import "codemirror/mode/javascript/javascript.js";
import "codemirror/theme/base16-dark.css";

import machineInfo from './machineInfo';

export default {
  data() {
    return {
      records: [],
      page: 1,
      pageSize: 10,
      total: 0,
      pageDisabled: false,
      dialogVisible: false,
      form: {
        env: 'staging',
        suit: ''
      },
      applyRules: {
        suit: [{required: true, message: "请选择机型", trigger: "blur"}]
      },
      dialogInfoVisible: false,
      codeMirrorContent: '',
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        mode: "text/javascript",
        theme: "base16-dark",
        readOnly: "nocursor",
        lineNumbers: true,
        line: true,
        smartIndent: true
      }
    }
  },
  computed: {
    machineList() {
      const machineList = machineInfo[this.form.env] || []
      this.form.suit = machineList[0]
      return machineList
    }
  },
  created(){
    this.getList()
  },
  methods:{
    getList() {
      const page = this.page;
      const pageSize = this.pageSize;
      this.pageDisabled=true;
      service({
        url:`/applyMachine/list?page=${page}&pageSize=${pageSize}`,
        method:"GET"
      })
      .then(res => {
        this.records=this.fix(res.list);
        this.total=res.total
        this.pageDisabled=false
      })
    },
    fix(list){
      return list.map(item=>{
          item.showCtime =bizutil.timeFormat(item.ctime)
          return item
      })
    },
    showApplyDetail(id) {
      this.dialogInfoVisible = true;
      this.codeMirrorContent = '加载中...';
      service({
        url:`/applyMachine/detail?id=${id}`,
        method:"GET"
      }).then(res => {
        this.codeMirrorContent = JSON.stringify(res,null,4);
      })
    },
    showInitDetail(id) {
      this.dialogInfoVisible = true;
      this.codeMirrorContent = '加载中...';
      service({
        url:`/applyMachine/info?id=${id}`,
        method:"GET"
      }).then(res => {
        this.codeMirrorContent = JSON.stringify(res,null,4);
      })
    },
    dislogClose() {
      this.codeMirrorContent = '';
      this.dialogInfoVisible = false;
    },
    submitApplyForm (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message.warning('请检查参数')
          return false
        }
        const form = this.form
        service({
          url: `/applyMachine/application`,
          method: "POST",
          data: {
            env: form.env,
            suitId: form.suit.suitId,
            siteId: form.suit.siteId
          }
        }).then(res => {
          if (res) {
            this.$message.success('申请成功');
            this.dialogVisible = false;
            this.getList()
          }
        })
      })
    },
    initMachine(id) {
      service({
        url:`/applyMachine/init?id=${id}`,
        method:"GET"
      }).then(res => {
        if (res) {
          this.$message.success("初始化成功，点击状态查看")
        } else {
          this.$message.error("初始化失败，稍后重试")
        }
      })
    },
    showDialog() {
      this.dialogVisible = true
      this.form.suit = this.machineList[0]
    },
    handleCurrentChange(val) {
      this.page=val;
      this.getList();
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: flex-end;
}
.commitOption.el-select-dropdown__item {
  height: 100px;
  border-bottom: 0.5px solid #ccc;
  padding-top: 5px;
  span {
    display: block;
    line-height: 21px;
  }
}
</style>