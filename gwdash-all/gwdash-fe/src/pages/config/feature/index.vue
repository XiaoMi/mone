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
            <el-form :rules="rules" size="mini" ref="ruleForm" label-width="140px">
                <el-form-item label="是否开启发布" prop="release">
                    <el-switch v-model="release"></el-switch>
                </el-form-item>
                <el-form-item label="是否启用自动化扩容" prop="openCategory">
                    <el-switch v-model="openCategory"></el-switch>
                </el-form-item>
                <el-form-item label="是否启用ScaleDown" prop="quotaserverScaleDown">
                    <el-switch v-model="quotaserverScaleDown"></el-switch>
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
      release: false,
      rules: {
      },
      openCategory: false,
      quotaserverScaleDown: false
    }
  },
  watch: {
    release () {
      const release = this.release
      service({
        url: '/switch/release',
        method: 'POST',
        data: {
          id: 1,
          release: release
        }
      }).then(boolean => {
        this.getEntity()
      })
    },
    openCategory () {
      const openCategory = this.openCategory
      let url
      openCategory ? url = '/autoscaling/turnon' : url = '/autoscaling/turnoff'
      service({
        url: url,
        method: 'get'
      }).then(boolean => {
        this.getOpenCategory()
      })
    },
    quotaserverScaleDown () {
      const quotaserverScaleDown = this.quotaserverScaleDown
      let url
      quotaserverScaleDown ? url = '/scaledown/enable' : url = '/scaledown/disable'
      service({
        url: url,
        method: 'get'
      }).then(boolean => {
        this.getQuotaserverScaleDown()
      })
    }
  },
  created () {
    this.getEntity()
    this.getOpenCategory()
    this.getQuotaserverScaleDown()
  },
  methods: {
    getEntity () {
      service({
        url: '/switch/config',
        method: 'GET'
      }).then(form => {
        this.release = form.release
      })
    },
    getOpenCategory () {
      service({
        url: '/autoscaling/status',
        method: 'GET'
      }).then(formOpenCategory => {
        this.openCategory = formOpenCategory
      })
    },
    getQuotaserverScaleDown () {
      service({
        url: '/scaledown/status',
        method: 'GET'
      }).then(formQuotaserverScaleDown => {
        this.quotaserverScaleDown = formQuotaserverScaleDown
      })
    }
  }
}
</script>
