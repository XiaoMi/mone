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
  <el-form ref="ruleForm" :model="form" :rules="rules" label-width="110px">
    <el-form-item v-if="showTitle" prop="title" label="标题">
      <el-input v-model="form.title" placeholder="请输入标题"  style="width:42%"></el-input>
    </el-form-item>
    <el-form-item label="优先级" prop="priority" class="priority" v-if="showTitle">
      <el-rate v-model="form.priority" show-text :colors="priorityColors" :texts="priorityTexts"></el-rate>
    </el-form-item>
    <el-form-item v-if="showMember" prop="commentUserVos" label="解决者">
      <el-select v-model="form.commentUserVos" multiple clearable filterable default-first-option placeholder="指定解决者" value-key="userId" style="width:42%">
        <el-option v-for="item in mOptions" :key="item.label" :label="item.label" :value="item.value"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="showType" label="类型" prop="type">
      <el-select v-model="form.type" placeholder="请选择">
        <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </el-form-item>
    <!-- issue类型开启 -->
    <el-form-item v-if="form.type == 3"  prop="status" label="任务状态">
      <el-select v-model="form.status" style="width:42%" filterable default-first-option>
        <el-option v-for="item in issueOptions" :key="item.label" :label="item.label" :value="item.value"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="showProject" prop="projectId">
      <el-select v-model="form.projectId" filterable default-first-option>
        <el-option v-for="item of pOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="showExpTime" label="任务时间">
      <el-date-picker
        v-model="form.expEndTime"
        value-format="timestamp"
        type="datetime"
        placeholder="预期结束时间点"
        style="width:42%">
      </el-date-picker>
    </el-form-item>
    <el-form-item prop="content" :label="showTitle ? '任务详情' : '详情'" style="margin-bottom:0px">
       <d2-mde v-model="form.content" placeholder="markdown描述" class="mde" style="width:95%" v-if="reFresh"/>
    </el-form-item>
    <el-form-item style="text-align:right; margin-right:32px">
        <el-button type="primary" size="small" @click="submit('ruleForm')">提交</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
import request from '@/plugin/axios/index'
import { options as typeOptions, pjPre } from '../constants/type_info'
import { options as issueOptions } from '../constants/issue_status'

export default {
  props: {
    showTitle: {
      type: Boolean,
      default: false
    },
    showType: {
      type: Boolean,
      default: false
    },
    showMember: {
      type: Boolean,
      default: false
    },
    showExpTime: {
      type: Boolean,
      default: false
    },
    showProject: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      typeOptions,
      issueOptions,
      pOptions: [],
      mOptions: [],
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
      reFresh: true
    }
  },
  created () {
    this.getMembers()
    this.getProjects()
  },
  methods: {
    getMembers () {
      request({
        url: '/account/all/list',
        method: 'get'
      }).then(
        accounts => {
          if (!Array.isArray(accounts)) return
          this.mOptions = accounts.map(item => {
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
    getProjects () {
      const url = `/project/list`
      request({
        url,
        method: 'post',
        data: {
          showAll: true
        }
      }).then(({ list = [] }) => {
        if (!Array.isArray(list)) return
        this.pOptions = list.map(item => {
          return {
            value: Number(`${pjPre.project}${item.id}`),
            label: item.name
          }
        })
        this.pOptions.push({
          value: 0,
          label: '不指定项目'
        })
      }
      )
    },
    submit (formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false
        }
        const form = { ...this.form }
        const commentUserVos = form.commentUserVos || []
        let url = '/comment/create'
        if (form.id != null) {
          url = '/comment/modify'
        }
        request({
          url,
          method: 'POST',
          data: {
            ...form,
            commentUserVos: JSON.stringify(commentUserVos)
          }
        }).then(data => {
          if (data === true) {
            this.$message.success('操作成功')
            this.$emit('submit', form)
            // d2-mde编辑框数据置空
            this.form.content = ''
            this.reFresh = false
            this.$nextTick(() => {
              this.reFresh = true
            })
          }
        })
      })
    }
  }
}
</script>

<style>
.priority .el-form-item__content {
  height: 40px;
  display: flex;
  align-items: center;
}
</style>
