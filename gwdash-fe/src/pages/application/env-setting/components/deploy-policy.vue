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
  <el-form :model="policy" size="mini" label-width="120px">
    <el-form-item prop="deployment" label="发布方式">
      <el-select v-model="policy.deployment">
        <el-option label="分批发布" value="oneByone" />
      </el-select>
    </el-form-item>
    <el-form-item prop="stop" label="发布暂停方式">
      <el-select v-model="policy.stop">
        <!--
        <el-option label="第一批暂停" value="first" />
        <el-option label="不暂停" value="none" />
        -->
        <el-option label="每批暂停" value="each" />
      </el-select>
    </el-form-item>
    <el-form-item prop="time" label="发布批次">
      <el-input-number v-model="policy.batchNum" :min="2" />
    </el-form-item>
    <el-form-item>
      <el-button @click="savePolicy" type="primary">保存</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'
export default {
    data () {
        return {
            policy: {}
        }
    },
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
    created () {
        this.getPolicy()
    },
    methods: {
        savePolicy () {
            const form = this.policy
            const envId = this.envId
            request({
                url: `/project/env/setting/policy/save`,
                method: 'post',
                data: {
                    ...form,
                    envId
                }
            }).then(res => {
                this.$message.success("保存成功")
            })
        },
        getPolicy () {
            const envId = this.envId
            request({
                url: `/project/env/setting/policy/get`,
                method: 'post',
                data: qs.stringify({envId})
            }).then(policy => {
                this.policy = policy || { batchNum: 2 }
            })
        },
    }
}
</script>