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
  <div class="node-info">
    <el-form ref="form" label-position="top">
        <el-form-item label="当前Api">
          <el-input :value="value['url']" disabled=""></el-input>
        </el-form-item>
        <div class="el-form-item">
          <p>
            编辑Api的参数
            <el-tooltip placement="top">
              <div slot="content">
                *从header中取值<br/>
                *request.header{name}<br/>
                * 从get的url参数中取值<br/>
                * request.param{name}<br/>
                * 从post的body中取值<br/>
                * request.body{name}
              </div>
              <el-button type="small" icon="el-icon-question">取值参考</el-button>
            </el-tooltip>
          </p>
            <codemirror class="codeMirror" v-model="value.paramExtract" :options="cmOptions" ></codemirror>
        </div>
        <el-form-item label="是否result">
              <el-switch v-model="value['result']"></el-switch>
        </el-form-item>
    </el-form>
    <!-- <div class="footer-btns">
      <el-button plain  size="mini" @click="updateCurrentInfo()">保存当前节点</el-button>
    </div> -->
  </div>
</template>

<script>

import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/base16-dark.css'
import 'codemirror/addon/lint/lint.js'
import 'codemirror/addon/lint/json-lint.js'
import 'codemirror/addon/lint/lint.css'

export default {
  name: 'nodeInfo',
  data () {
    return {
      cmOptions: {
        tabSize: 2,
        indentUnit: 2,
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true,
        height: 100,
        cursorHeight: 0.85,
        // json校验
        mode: 'application/json',
        gutters: ['CodeMirror-lint-markers'],
        lint: true
      }
    }
  },

  props: {
    value: {
      type: Object,
      default: function () {
        return {
          paramExtract: '',
          result: false
        }
      }

    }
  },
  methods: {

  },
  components: {
  }

}
</script>

<style lang="scss" scoped>

.codeMirror{
  line-height: 1.3
}
</style>
<style lang="scss">
.node-info{
    height: 400px;
    padding:0 10px 0;
    overflow-x: hidden;
    overflow-y:scroll;
  .CodeMirror{
    height: auto;
  }
  h2{
    margin:5px auto;
  }
  &::-webkit-scrollbar{
    display: none;
  }
  .param-extract-t{
    font-size:16px;
    margin:5px 0;
    font-weight:bold;
  }
}

</style>
