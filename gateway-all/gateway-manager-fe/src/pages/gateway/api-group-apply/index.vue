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
    <el-card class="box-card">
        <div slot="header" class="clearfix">
            <span>您已属分组</span>
            <el-button
                size='mini'
                style="float: right; padding: 3px 0"
                @click="showApplyDialog"
                type="text">申请分组</el-button>
        </div>
        <div v-for="item of currentGroup"
            :key="item.id"
            class="text item">
            {{ item.groupName }}
        </div>
    </el-card>

    <el-dialog
      title="申请分组"
      :visible.sync='dialogVisible'
      width='800px'>
      <el-form ref='form' :model='form' :rules='formRules' label-width='110px' size='mini'>
        <el-form-item label='' prop='groups'>
            <el-transfer
                :titles="['没有分组', '已有分组']"
                v-model="form.groups"
                :data="groupOptions">
            </el-transfer>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="submitFormUpload('form')" size="mini">确 定</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios'
import qs from 'qs'

export default {
  data () {
    return {
      dialogVisible: false,
      groupOptions: [],
      currentGroup: [],
      form: {
        groups: ''
      },
      formRules: {
        groups: [{ required: true, message: '分组不能为空', trigger: 'blur' }]
      }
    }
  },
  created () {
    this.getCurrentGroup()
    this.getGroupOptions()
  },
  methods: {
    getCurrentGroup () {
      service({
        url: '/apigroup/owngroups',
        method: 'GET'
      }).then(res => {
        if (Array.isArray(res)) {
          this.currentGroup = res.map(it => {
            return {
              id: +it.id,
              key: +it.id,
              groupName: it.groupName
            }
          })
        } else {
          this.currentGroup = []
        }
      })
    },
    showApplyDialog () {
      this.dialogVisible = true
      this.form.groups = []
      this.form.groups = this.currentGroup.map(it => {
        return it.id
      })
    },
    submitFormUpload (formName) {
      console.log(this.form.groups)
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        const groups = this.form.groups
        const groupNames = []
        groups.forEach(item => {
          const group = this.groupOptions.find(it => it.value === item)
          group && groupNames.push(group.label)
        })
        service({
          url: '/auditing/apply/api/group',
          method: 'POST',
          data: qs.stringify({
            groups: JSON.stringify(groups),
            groupNames: JSON.stringify(groupNames)
          })
        }).then(res => {
          this.$message.success('提交成功')
          this.dialogVisible = false
          this.getCurrentGroup()
        })
      })
    },
    getGroupOptions () {
      service({
        url: '/apigroup/list',
        method: 'POST',
        data: {
          pageNo: 1,
          pageSize: 100
        }
      }).then(res => {
        const list = res && res.groupList
        if (list) {
          this.groupOptions = list.map(item => {
            return {
              key: item.id,
              label: item.name,
              value: item.id
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .text {
    font-size: 12px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    margin: 20px;
    // width: 480px;
  }
</style>
