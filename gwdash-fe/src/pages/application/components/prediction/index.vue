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
    <div class="header">
      <div class="title">流量预测</div>
      <div @click="handleEdit('更新')">
        <el-link icon="el-icon-edit-outline" :underline="false" style="font-size:18px"></el-link>
      </div>
    </div>
    <div class="content">
      <chart-view v-if='showChart' :show='showChart' :id='id'/>
      <el-alert
          v-if='dislogTitle !== "新增" && !showChart'
          type="warning"
          title="已关闭显示"
          :closable="false"/>
      <el-alert
          v-if='dislogTitle === "新增"'
          type="warning"
          title="请先添加CAT-domain"
          :closable="false"/>
    </div>

    <el-dialog :title='`${dislogTitle}数据`' :visible.sync='dislogVisible' width='800px'>
       <el-form ref="form" :model="form" :rules="rules" label-width="110px" size='mini'>
          <el-form-item label="domain" prop="domain">
             <el-input v-model='form.domain' placeholder='请输入domain' style='width:50%'/>
             <el-popover
                 trigger="hover"
                 placement="right">
                 <div style='text-align:center'>CAT的domain</div>
                 <i style='margin-left:10px' slot="reference" class="el-icon-question"></i>
            </el-popover>
          </el-form-item>
          <el-form-item label="type" prop="type">
             <el-input v-model='form.type' placeholder='请输入type' style='width:50%'/>
             <el-popover
                 trigger="hover"
                 placement="right">
                 <div style='text-align:center'>事务名</div>
                 <i style='margin-left:10px' slot="reference" class="el-icon-question"></i>
             </el-popover>
          </el-form-item>
          <el-form-item label='QPS' prop='qps'>
            <el-input-number v-model="form.qps" :min='0' style='width:30%'></el-input-number>
            <el-popover
                 trigger="hover"
                 placement="right">
                 <div style='text-align:center'>项目QPS阈值</div>
                 <i style='margin-left:10px' slot="reference" class="el-icon-question"></i>
              </el-popover>
          </el-form-item>
          <el-form-item label="status" prop="status">
             <el-select v-model='form.status' style='width:50%' placeholder='请选择'>
                <el-option label='开' :value='0'></el-option>
                <el-option label='关' :value='1'></el-option>
             </el-select>
             <el-popover
                 trigger="hover"
                 placement="right">
                 <div style='text-align:center'>是否展示流量预测图</div>
                 <i style='margin-left:10px' slot="reference" class="el-icon-question"></i>
              </el-popover>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="dislogVisible = false" size='mini'>取 消</el-button>
            <el-button type="primary" @click="submitForm" size='mini'>确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import chartView from './components/chart-view'

export default {
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      dislogVisible: false,
      dislogTitle: '新增',
      showChart: false,
      form: {},
      rules: {
        domain: [{ required: true, message: '请输入domain', trigger: 'blur' }],
        type: [{ required: true, message: '请输入type', trigger: 'blur' }],
        status: [{ required: true, message: '请选择', trigger: 'blur' }],
        qps: [{ required: true, message: '请输入qps | 必须为number', trigger: 'blur' }]
      },
      realData: [],
      predictData: []
    }
  },
  watch: {
    id () {
      this.dislogTitle = '新增'
      this.showChart = false
      this.form = {}
      this.getInitConfig()
    }
  },
  created () {
    this.getInitConfig()
  },
  components: {
    chartView
  },
  methods: {
    getInitConfig () {
      let id = this.id;
      if (id === '' || !id) {
        console.log('project id null')
        return
      }
      service({
        url: `/predict/getConfig?projectId=${id}`
      }).then(res => {
        if (res) {
          this.dislogTitle = '更改'
          this.showChart = res.status === 0
          this.form = res
        }
      })
    },
    handleEdit () {
      this.dislogVisible = true
    },
    submitForm () {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          this.message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.dislogVisible = false
        service({
          url: '/predict/editConfig',
          method: 'POST',
          data: {
            ...this.form,
            projectId: this.id
          }
        }).then(res => {
          if (res) {
            this.$message({
              message: '更新成功',
              type: 'success'
            })
            this.getInitConfig()
          }
        })
      })
    }

  }
}
</script>

<style lang='scss' scoped>
.header {
  display: flex;
  justify-content: space-between;
  padding: 0px 0px 10px 0px;
  .title {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
}
</style>
