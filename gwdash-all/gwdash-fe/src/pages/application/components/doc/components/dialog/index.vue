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
  <el-dialog
    :title='dialogTitle'
    :visible='dialogVisible'
    :before-close='handleClose'
    destroy-on-close
    width='800px'>
    <el-form :model="form" ref="ruleForm" :rules="rules" label-width="110px" size='mini'>
      <div v-if='dialogTitle === "Issue"'>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" style="width:42%"></el-input>
        </el-form-item>
        <el-form-item label="优先级" prop="priority" class="priority">
          <el-rate v-model="form.priority" show-text :colors="priorityColors" :texts="priorityTexts"/>
        </el-form-item>
        <el-form-item label="解决者" prop="commentUserVos" >
          <el-select
            v-model="form.commentUserVos"
            multiple
            clearable
            filterable
            default-first-option
            placeholder="指定解决者"
            value-key="userId"
            style="width:42%">
            <el-option v-for="item in mOptions" :key="item.label" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="form.status" filterable default-first-option style="width:42%">
            <el-option v-for="item in issueOptions" :key="item.label" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="任务时间">
          <el-date-picker
            v-model="form.expEndTime"
            value-format="timestamp"
            type="datetime"
            placeholder="预期结束时间点"
            style="width:42%">
          </el-date-picker>
        </el-form-item>
      </div>
      <el-form-item
        label='详情'
        prop="content"
        style="margin-bottom:0px">
        <d2-mde v-model="form.content" placeholder="markdown描述" class="mde" style="width:95%"/>
      </el-form-item>
      <el-form-item style="text-align:right; margin-right:32px">
        <el-button type="primary" size="small" @click="submit('ruleForm')">提交</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import service from '@/plugin/axios/index'
import { options as issueOptions } from '../constants/issue_status'
export default {
  props: {
    dialogTitle: {
      type: String,
      required: true
    },
    dialogVisible: {
      type: Boolean,
      required: false
    }
  },

  data () {
    return {
      form: {},
      rules: {
        title: [
          { required: true, message: '必填字段', trigger: 'blur' },
          { min: 3, message: '不少于3个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '必填字段', trigger: 'blur' },
          { min: 6, message: '不少于6个字符', trigger: 'blur' }
        ],
        commentUserVos: [
          { required: true, message: '请选择解决者', trigger: 'blur' }
        ],
        priority: [
          { required: true, message: '请选择优先级', trigger: 'blur' }
        ]
      },
      priorityColors: {
        1: '#909399',
        2: '#67C23A',
        3: '#E6A23C',
        4: '#F56C6C',
        5: '#ff1919'
      },
      priorityTexts: ["低", "中", "高", "严重", "紧急"],
      mOptions: [],
      issueOptions
    }
  },
  created () {
    this.dialogTitle === 'Issue' && this.getMembers()
  },
  methods: {
    getMembers () {
      service({
        url: '/account/all/list'
      }).then(item => {
        if (!Array.isArray(item)) return
        this.mOptions = item.map(item => {
          return {
            label: `${item.name}[${item.userName}]`,
            value: {
              userId: item.id,
              userName: item.userName,
              type: 1
            }
          }
        })
      })
    },
    submit (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message.warning('请检查参数')
          return
        }
        this.$emit('submit', this.form)
      })
    },
    handleClose () {
      this.$emit('close')
    }
  }
}
</script>
