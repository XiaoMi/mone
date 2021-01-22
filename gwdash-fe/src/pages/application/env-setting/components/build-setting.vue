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
  <el-form :model="form" :rules="rules" size="mini" label-width="120px">
    <el-form-item prop="buildDir" label="构建目录">
      <el-input v-model="form.buildDir" style="width:50%"></el-input>
    </el-form-item>
    <el-form-item prop="jarDir" label="构建jar目录">
      <el-input v-model="form.jarDir" style="width:50%"></el-input>
    </el-form-item>
    <el-form-item prop="customParams" label="mvn参数">
      <el-input placeholder="默认已有-U clean package -Dmaven.test.skip=true" v-model="form.customParams" style="width:50%"></el-input>
    </el-form-item>
    <el-form-item prop="xmlSetting" label="mvn库">
      <el-select v-model='form.xmlSetting' style="width:50%">
        <el-option
           v-for='item in xmlSettings'
           :label='item.name'
           :value='item.value'
           :key='item.value'/>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button @click="saveSetting" type="primary">保存</el-button>
    </el-form-item>
  </el-form>
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
      form: {
        buildDir: '',
        jarDir: '',
        customParams: '',
        xmlSetting: ''
      },
      xmlSettings: [
        // {
        //   name: 'nexus.d.xiaomi.net',
        //   value: 0
        // },
        {
          name: 'pkgs.d.xiaomi.net',
          value: 1
        }
      ],
      rules: {
        buildDir: [
          { min: 0, max: 128, message: "长度在 1 到 128 个字符", trigger: "blur"}
        ],
        jarDir: [
          { min: 0, max: 128, message: "长度在 1 到 128 个字符", trigger: "blur"}
        ],
        customParams: [
          { min: 0, max: 128, message: "长度在 1 到 128 个字符", trigger: "blur"}
        ],
        xmlSetting: [
          { required: false, message: "请选择", trigger: "blur" }
        ]
      }
    }
  },
  created () {
    this.getBuildSetting();
  },
  methods: {
     getBuildSetting () {
       service({
         url: `/project/env/setting/build/get?envId=${this.envId}`,
         method: 'GET'
       }).then(res => {
         this.form = {
             buildDir: res.buildDir,
             jarDir: res.jarDir,
             customParams: res.customParams,
             xmlSetting: res.xmlSetting || 0
         }
       })
     },
     saveSetting () {
        const form = this.form;
        const envId = this.envId;
        service({
            url: `/project/env/setting/build/save`,
            method: "POST",
            data: {
              buildDir: form.buildDir && form.buildDir.trim() || '',
              jarDir: form.jarDir && form.jarDir.trim() || '',
              customParams: form.customParams && form.customParams.trim() || '',
              xmlSetting: form.xmlSetting || 0,
              envId              
            }
        }).then(res => {
            this.$message.success("保存成功");
        });
     }, 
  }
}
</script>

<style lang="scss" scoped>

</style>