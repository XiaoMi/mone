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
  <el-form :model="form" :rules="rules" size="mini" label-width="120px" ref='form'>
    <el-form-item prop="path" label="部署路径">
      <el-input v-model="form.path" placeholder="xxxx/myproject/" style="width:50%"/>
    </el-form-item>
    <el-form-item prop="heapSize" label="java堆内存(M)" v-if="proType !== 'go' ">
        <el-input-number
          v-model="form.heapSize"
          controls-position="right"
          :min="512"
          placeholder="512"
        />
    </el-form-item>
    <el-form-item label="jvm参数" prop="jvmParams">
      <el-input v-model="form.jvmParams" placeholder="非必填, 长度0-1024" style="width: 50%"></el-input>
    </el-form-item>
    <el-form-item label="健康检查" prop="healthCheckUrl">
      <el-input v-model="form.healthCheckUrl" placeholder="非必填, 长度0-120" style="width: 50%"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button @click="saveSetting" type="primary">保存</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'
export default {
  data() {
    return {
      form: {
        path: '',
        heapSize: 512,
        jvmParams: '',
        healthCheckUrl: ''
      },
      rules: {
        path: [
          { required: true, message: '必填字段', trigger: 'blur' },
          {
            min: 1,
            max: 128,
            message: '长度在 1 到 128 个字符',
            trigger: 'blur'
          }
        ],
        healthCheckUrl: [
          {
            min: 1,
            max: 128,
            message: '长度在 1 到 128 个字符',
            trigger: 'blur'
          }
        ],
      },
    };
  },
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [Number, String],
      required: true
    },
    proType: {
      type: String,
      required: true
    }
  },
  created () {
    this.getSetting()
  },
  methods: {
    saveSetting() {
        const form = this.form;
        const envId = this.envId;
        request({
          url: `/project/env/setting/deployment/save`,
          method: "post",
          data: {
            ...form,
            envId
          }
        }).then(res => {
          this.$message.success("保存成功");
        });
    },
    getSetting () {
      const envId = this.envId
      request({
        url: `/project/env/setting/deployment/get`,
        method: 'post',
        data: qs.stringify({ envId })
      }).then(res => {
        this.form = res || {}
      })
    }
  }
}
</script>
