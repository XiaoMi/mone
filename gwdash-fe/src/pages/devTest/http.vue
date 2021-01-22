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
    <div class="devTest-box">
      <div class="input side-box">
        <div class="side-box-header">
          Input
        </div>
        <el-form ref="form" :rules="rules" :model="input" size="mini" label-width="100px" style="width:550px;">
            <el-form-item
             label="url"
             prop="url">
              <el-input placeholder="" width="100px" size="mini" @change="handleChangeUrl" v-model.trim="input.url"></el-input>
            </el-form-item>

            <el-form-item
              prop="method"
               label="method">
               <el-select v-model="input.method" placeholder="请选择">
                  <el-option
                    v-for="method in methodList"
                    :key="method"
                    :label="method"
                    :value="method">
                  </el-option>
                </el-select>
            </el-form-item>

            <el-form-item
               prop="headers"
               label="headers">
              <el-input
                placeholder='eg. {"Content-Type": "application/json"}'
                size="mini" v-model.trim="input.headers"></el-input>
            </el-form-item>

            <el-form-item
                prop="body"
                label="body">
                 <codemirror v-model="input.body" :options="common_cmOptions"></codemirror>
            </el-form-item>

            <el-form-item
               prop="paramsType"
               label="timeout"
               >
              <el-input
               placeholder=''
                size="small"
               v-model.trim="input.timeout"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" size="mini" @click="onSubmit">调试</el-button>
            </el-form-item>
        </el-form>
      </div>
      <div class="output side-box">
          <div class="side-box-header">
          Output
        </div>
        <div class="output-data">
             <codemirror v-model="output" :options="common_cmOptions2"></codemirror>
        </div>
      </div>
    </div>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import { type } from 'os';

  export default {
    data(){
      const checkHeaders = (rule, value, cb) => {
        if (value.trim().length == 0) return
        try {
          JSON.parse(value)
          cb()
        } catch(err) {
          cb('必须是严格 JSON 格式')
        }
      }
      return {
        input:{
          url:"",
          method:"",
          timeout: "",
          body: "",
          headers: "",
        },
        methodList:["GET", "POST"],
        output:"",
        common_cmOptions: {
          tabSize: 4,
          indentUnit: 4,
          mode: "text/json",
          theme: "base16-dark",
          lineNumbers: true,
          line: true,
          smartIndent: true
        },
          common_cmOptions2: {
          tabSize: 4,
          readOnly:'nocursor',
          indentUnit: 4,
          mode: "text/json",
          theme: "base16-dark",
          lineNumbers: true,
          line: true,
          smartIndent: true
        },
        rules: {
          url: [
            { required: true, message: "必填字段", trigger: "blur" }
          ],
          method: [
            { required: true, message: "必填字段", trigger: "blur" }
          ],
          headers: [
            { validator: checkHeaders, trigger: "blur" }
          ]
        }
      }
    },
    methods:{
      onSubmit(){
          this.$refs["form"].validate((valid) => {
          if (valid) {
            service({
              url:"/devTest/httpTest",
              method:"POST",
              data: {
                ...this.input,
                headers: JSON.parse(this.input.headers),
              }
            })
            .then(res=>{
              if(typeof res!== 'string'){
                res=JSON.stringify(res,null,3)
              }
              this.output = res;
            })
          } else {
            return false;
          }
        });
      },
    }
  }
</script>

<style lang="scss" scoped>
.side-box-header{
  padding: 5px;
  border-bottom: 1px solid #dedede;
  margin-bottom: 10px;
}
.side-box{
  flex: 1;
  padding: 10px;
}

</style>