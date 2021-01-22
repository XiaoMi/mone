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
        <d2-module>
            <h3>功能开启列表</h3>
            <el-form :model="form" :rules="rules" size="mini" ref="ruleForm" label-width="100px">
                <el-form-item label="是否开启发布" prop="release">
                    <el-switch v-model="form.release"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('ruleForm')">保存</el-button>
                </el-form-item>
            </el-form>
        </d2-module>
    </d2-container>
</template>
<script>
import service from '@/plugin/axios/index'

export default {
  data () {
    return {
      form: {
        release: false
      },
      rules: {
      }
    }
  },
  created () {
    this.getEntity()
  },
  methods: {
    getEntity () {
      service({
        url: '/switch/config',
        method: 'GET'
      }).then(form => {
        this.form = {
          ...form
        }
      })
    },
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const form = this.form
          service({
            url: '/switch/release',
            method: 'POST',
            data: {
              ...form
            }
          }).then(boolean => {
            this.getEntity()
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>
