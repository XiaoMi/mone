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
    <el-card>
      <el-form :model="form" :rules="rulesBottom" size="mini" label-width="150px" ref='formBottom'>
        <el-form-item label="accessKeyEnvKey" prop="accessKeyEnvKey">
          <el-input
            v-model="form.accessKeyEnvKey"
            placeholder='请输入'
            show-password
            style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="accessSecretEnvKey" prop="accessSecretEnvKey">
          <el-input
            v-model="form.accessSecretEnvKey"
            placeholder='请输入'
            show-password
            style="width: 50%"></el-input>
        </el-form-item>

        <el-form-item label="发布命令" prop="dockerParamsStr">
          <el-input
            v-model="form.cmd"
            placeholder='可填写shell脚本'
            style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="自定义变量" prop="dockerParamsStr">
          <el-input
            type="textarea"
            v-model="form.paramStr"
            placeholder='需标准JSON对象格式, 如{"env": "staging"}'
            style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="updateSetting" type="primary" :disabled="isSave">更新</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import qs from 'qs'
export default {
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [Number, String],
      required: true
    }
  },
  data () {
    return {
      form: {},
      rulesTop: {

      },
      rulesBottom: {

      },
      dialogCheckVisible: false
    }
  },
  created () {
    this.getSetting()
  },
  methods: {
    getSetting () {
      service({
        url: `/web/deploy/setting/get?id=${this.envId}`,
        method: 'GET'
      }).then(res => {
        let paramStr = "{}"
        try {
          paramStr = JSON.stringify(res.params)
        } catch (e) {}
        this.form = {
          ...res,
          paramStr
        }
      })
    },
    updateSetting () {
      let params = {}
      try {
        params = JSON.parse(this.form.paramStr)
      } catch (e) {
        return
      }
      service({
        url: '/web/deploy/setting/update',
        method: 'POST',
        data: {
          ...this.form,
          params,
          envId: this.envId
        }
      }).then(res => {
        if (res) {
          this.$message.success('更新成功');
          this.getSetting()
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.card {
  width: 80%;
  transition: 0.5s;
  margin-bottom: 16px;
  .title {
    font-size: 16px;
    color: #909399;
    margin-bottom: 12px;
    font-weight: bold;
    color: #f56c6c
  }
  .card-row {
    display: flex;
    align-items: center;
    background: #fef0f0;
    margin-bottom: 16px;
    padding-left: 10px;
    border-radius: 8px;
    &_entry {
      display: flex;
      margin-right: 20px;
      padding: 6px 8px;
      display: flex;
      justify-content: center;
      width: 100px;
      color: #f56c6c;
      .key {
        font-size: 15px;
        font-weight: 700
      }
      .value {
        font-size: 14px
      }
    }
  }
}
.card:hover {
  box-shadow: 15px 15px 15px 0 rgba(0,0,0,.1)
}
.v-enter,
.v-leave-to {
  opacity: 0;
  transform: translateX(150px)
}
.v-enter-active,
.v-leave-active {
  transition: all 0.8s
}
.v-enter-to  {
  opacity: 1;
  transform: translateX(0px)
}
</style>
