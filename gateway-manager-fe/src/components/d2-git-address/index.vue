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
    <el-form-item label="git地址" class='gitaddress' ref="gitFormitem" required='true'>
        <el-form-item style="display:inline-block" prop="domain">
          <el-select v-model="domain" placeholder="请选择git仓库地址" :disabled='disabled||disabledDomain'>
            <el-option
              v-for="item in gitAddressGroup"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item style="display:inline-block; width:25%" prop="gitGroup">
            <el-input v-model="gitGroup" :disabled='disabled' placeholder="请输入组名"></el-input>
        </el-form-item>
        <el-input value="/" placeholder disabled style="width: 7%"></el-input>
        <el-form-item style="display:inline-block" prop="gitName">
            <el-input  v-model="gitName" :disabled='disabled' placeholder="请输入项目名"></el-input>
        </el-form-item>
    </el-form-item>
</template>

<script>
import Common from '@/mixins/common'

export default {
  props: {
    disabled: {
      type: Boolean,
      required: true
    },
    disabledDomain: {
      type: Boolean,
      required: false
    },
    domain: {
      type: String || undefined,
      required: true
    },
    gitName: {
      type: String || undefined,
      required: true
    },
    gitGroup: {
      type: String || undefined,
      required: true
    }
  },
  data () {
    return {
    }
  },
  mixins: [Common],
  computed: {
  },
  created () {
    this.initGitAddressGroup()
  },
  watch: {
    gitName (newVal) {
      this.$emit('update:gitName', newVal)
    },
    gitGroup (newVal) {
      this.$emit('update:gitGroup', newVal)
    },
    domain (newVal) {
      this.$emit('update:domain', newVal)
    }
  },
  methods: {}
}
</script>
<style lang="scss" scoped>
    .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item{
        margin-bottom:9px
    }
</style>
