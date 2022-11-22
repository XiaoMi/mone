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
    <el-dialog
      :title="title"
      :visible="serverLessEditorVisible"
      @close="dialogClose"
      width="800px"
      :close-on-click-modal=false
    >
      <el-form :model="form" :rules="rules" label-width="130px" ref="form" size="mini">
        <template>
          <el-form-item label="git地址" class='gitaddress'>
          <el-input value="https://" placeholder disabled style="width:25%"></el-input>
          <el-form-item style="display:inline-block; width:25%" prop="gitGroup">
            <el-input v-model="form.gitGroup" disabled placeholder="请输入组名"></el-input>
          </el-form-item>
          <el-input value="/" placeholder disabled style="width: 5%"></el-input>
          <el-form-item style="display:inline-block; width:25%" prop="gitName">
            <el-input v-model="form.gitName" disabled placeholder="请输入项目名"></el-input>
          </el-form-item>
        </el-form-item>
        </template>

         <template>
          <el-form-item label="jar包" class='gitaddress'>
          <el-form-item style="display:inline-block; width:80%" prop="jar">
            <el-select v-model="form.jarName" placeholder="请选择jar包">
              <el-option
                v-for="item in jarGroups"
                :key="item.jarName"
                :label="item.jarName"
                :value="item.jarName">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form-item>
        </template>
      <el-form-item>
        <el-button  @click="dialogClose" size="mini">取 消</el-button>
        <el-button type="primary" @click="onSubmit" size="mini">确 定</el-button>
      </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/plugin/axios'

export default {
  name: 'api-info-editor',
  data () {
    return {
      serverLessGroups: [],
      viewApiComposeDialogVisible: false,
      jarGroups: [],
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
      }
    }
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    form: {
      type: Object,
      default: () => ({})
    },
    serverLessEditorVisible: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '800px'
    }
  },
  computed: {
    gitaddress () {
      return 'https://' + this.form.gitGroup + '/' + this.form.gitName
    }
  },
  created () {
    console.log(this.form)
    service({
      url: `/cloud/build/jar/list?gitName=${this.form.gitName}&&gitGroup=${this.form.gitGroup}&&page=1&&pageSize=10`,
      method: 'GET',
      data: {
        gitName: this.form.gitName,
        gitGroup: this.form.gitGroup,
        page: 1,
        pageSize: 10
      }
    }).then(res => {
      this.jarGroups = res || []
    })
  },
  methods: {
    getApiGroups () {
      service({
        url: '/apiinfo/modify/jarscript',
        method: 'get',
        data: {}
      }).then(res => {
        this.serverLessGroups = res || []
      })
    },
    getDag (param) {
      this.dagData = param || ''
      this.viewApiComposeDialogVisible = true
    },
    onSubmit () {
      console.log(this.form)
      this.$refs['form'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        service({
          url: `apiinfo/modify/jarscript`,
          method: 'post',
          data: {
            ...this.form
          }
        }).then(res => {
          this.$emit('submitSuccess')
          this.$emit('serverLessEditorVisible')
        }).catch((res) => {
          this.$message.error(res)
        })
      })
    },
    onCancel () {
      const form = this.form
      this.$emit('serverLessEditorVisible')
      this.$emit('cancel', form)
    },
    dialogClose (...args) {
      this.$emit('serverLessEditorVisible', args)
    },
    updateApiInfoEditorVisible (newApiInfoEditorVisible) {
      this.$emit('update:apiInfoEditorVisible', newApiInfoEditorVisible)
    }
  }
}
</script>
