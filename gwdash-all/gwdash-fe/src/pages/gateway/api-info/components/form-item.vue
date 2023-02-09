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
  <el-select
    v-if="param.type == 'select'"
    :value="value"
    @input="onInput"
    placeholder="请选择"
  >
    <el-option
      v-for="item in param.options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
  <el-checkbox-group
    v-else-if="param.type == 'checkbox'"
    :value="value"
    @input="onInput">
    <el-checkbox
        v-for="item in param.options || []"
        :key="item.value"
        :label="item.value">{{item.label}}</el-checkbox>
  </el-checkbox-group>
  <el-radio-group
    v-else-if="param.type == 'radio'"
    :value="value"
    @input="onInput">
    <el-radio
      v-for="item in param.options || []"
      :key="item.value"
      :label="item.value"
    >{{item.label}}</el-radio>
  </el-radio-group>
  <el-input
    v-else-if="param.type == 'pw'"
    type="password"
    :value="value"
    @input="onInput"
    autocomplete="off"/>
  <d2-mde
    v-else-if="param.type == 'md'"
    :value="value"
    @input="onInput"
    placeholder="markdown描述"/>
  <codemirror
    v-else-if="param.type == 'codemirror'"
    :value="value"
    @input="onInput"
    :options="cmOptions"
  ></codemirror>
  <el-input
    v-else-if="param.type == 'textarea'"
    :value="value"
    @input="onInput"
    type="textarea"
  />
  <el-input
    v-else
    :value="value"
    @input="onInput"
  />
</template>
<script>
export default {
  name: 'yp-form-item',
  data () {
    return {
      cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        mode: 'text/javascript',
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      }
    }
  },
  props: {
    value: '',
    param: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    onInput (value) {
      this.$emit('input', value)
    }
  }
}
</script>
<style lang="scss" scoped>
.el-form-item .el-form-item {
  margin-bottom: 22px;
}
</style>
