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
      :visible="apiInfoEditorVisible"
      @update:visible="updateApiInfoEditorVisible"
      @close="dialogClose"
      width="1000px">
        <el-form :model="form" :rules="rules" label-width="130px" ref="form" size="mini" class='add-form'>
            <el-form-item label="name" prop="name">
              <el-input v-model="form.name" placeholder="name" style="width: 70%"></el-input>
            </el-form-item>
            <el-form-item label="capacity" prop="capacity">
              <el-input v-model="form.capacity" placeholder="capacity" style="width: 70%"></el-input>
            </el-form-item>
            <el-form-item label="cron" prop="cron">
              <el-input v-model="form.cron" placeholder="cron" clearable='true' style="width: 24%;margin-right:10px;"></el-input>
              <el-button
                  type="primary"
                  icon="el-icon-edit"
                  circle
                  size='mini'
                  @click='showCron=!showCron'>
              </el-button>
            </el-form-item>
            <el-form-item label="startTime" prop="startTime">
              <el-date-picker
                  value-format='timestamp'
                  v-model="form.startTime"
                  type="datetime"
                  placeholder="选择开始时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="endTime" prop="endTime">
              <el-date-picker
                  value-format='timestamp'
                  v-model="form.endTime"
                  type="datetime"
                  placeholder="选择结束时间">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
               <el-button @click="onSubmit" type="primary" :disabled="isSave">保存</el-button>
            </el-form-item>
            <el-dialog  :visible.sync="showCron"  :modal='false' :show-close='false' top='0vh' width='538px'>
              <vcrontab @hide="showCron=false" @fill="crontabFill" :expression="expression"></vcrontab>
            </el-dialog>
        </el-form>
    </el-dialog>
  </div>
</template>

<script>
import bizutil from '@/common/bizutil'
import service from '@/plugin/axios/index'
import vcrontab from 'vcrontab'

export default {
  name: 'periodic-deployment-editor',
  components: {
    vcrontab
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    apiInfoEditorVisible: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '800px'
    },
    projectId: {
      type: Number,
      required: true
    },
    envId: {
      type: Number,
      required: true
    },
    formApiInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    var checkEndTime = (rule, value, callback) => {
      console.log(this.form.startTime, value)
      if (!value || value.length <= 0) {
        callback(new Error('请输入'))
      } else if (value <= this.form.startTime) {
        callback(new Error('结束时间必须大于开始时间'))
      } else if (this.form.cron && value <= (new Date()).getTime()) {
        callback(new Error('结束时间必须大于当前时间'))
      } else {
        callback()
      }
    }
    var checkStartTime = (rule, value, callback) => {
      console.log(this.form.startTime, value)
      if (!value || value.length <= 0) {
        callback(new Error('请输入'))
      } else if (!this.form.cron && value <= (new Date()).getTime()) {
        callback(new Error('开始时间必须大于当前时间'))
      } else {
        callback()
      }
    }
    return {
      form: {},
      domain: '',
      showCron: false,
      expression: '',
      input: "",
      rules: {
        name: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        capacity: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        endTime: [
          {
            required: true,
            validator: checkEndTime,
            trigger: "blur"
          }
        ],
        startTime: [
          {
            required: true,
            validator: checkStartTime,
            trigger: "blur"
          }
        ]
      }
    }
  },
  computed: {

  },
  created () {
    this.form = this.formApiInfo
    if (this.form.flag === 0) {
      this.form.startTime = (new Date(this.form.startTime)).getTime()
      this.form.endTime = (new Date(this.form.endTime)).getTime()
    }
    this.getApiGroups()
  },
  methods: {
    showDialog () {
      this.expression = this.form.cron// 传入的 cron 表达式，可以反解析到 UI 上
      this.showCron = true
    },
    crontabFill (value) {
      // 确定后回传的值
      this.form.cron = value
    },
    getApiGroups () {
      const projectId = this.projectId
      const envId = this.envId
      console.log(this.form, 'form')
      service({
        url: `/predict/getConfig?envId=${envId}&projectId=${projectId}`,
        method: 'get'
      }).then(res => {
        console.log(res, 'domain')
        this.domain = res.domain
      }).catch((res) => {
        this.$message.error(res)
      })
    },
    // 添加  编辑
    handleAdd () {
      const form = this.form
      form.startTime = (new Date(form.startTime)).getTime()
      form.endTime = (new Date(form.endTime)).getTime()
      let url, showMessage, envId, projectId, domain
      envId = this.envId
      projectId = this.projectId
      domain = this.domain
      if (this.formApiInfo.flag) {
        showMessage = '添加成功'
        url = '/schedule/create'
      } else {
        showMessage = '保存成功'
        url = '/schedule/update'
      }
      service({
        url: url,
        method: 'post',
        data: {
          ...form,
          projectId: projectId,
          envId: envId,
          domain: domain
        }
      }).then(res => {
        this.$message.success(showMessage)
        this.dialogClose()
        this.$emit('submitSuccess')
        this.$emit('update:apiInfoEditorVisible', false)
      }).catch((res) => {
        this.$message.error(res)
      })
    },
    onSubmit () {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        this.handleAdd()
      })
    },
    dialogClose (...args) {
      this.form.startTime = bizutil.timeFormat(this.form.startTime)
      this.form.endTime = bizutil.timeFormat(this.form.startTime)
      this.$emit('apiInfoEditorClose', args)
    },
    updateApiInfoEditorVisible (newApiInfoEditorVisible) {
      this.$emit('update:apiInfoEditorVisible', newApiInfoEditorVisible)
    }
  }
}
</script>

<style lang="scss" scoped>
 /deep/ .add-form {
    position: relative;
  .el-dialog__wrapper {
      .el-dialog {
        position : absolute;
        right: 520px;
        top : 120px;
        .el-dialog__header {
          display: none;
        }
        .el-dialog__body {
          height: 278px;
          overflow-y: scroll;
        }
      }
    }
  }
</style>
