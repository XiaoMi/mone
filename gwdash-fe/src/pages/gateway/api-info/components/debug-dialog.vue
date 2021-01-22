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
  <el-dialog
    :visible="visible"
    @update:visible="updateVisible"
    width="800px"
    :close-on-click-modal="false"
    class="debug-dialog"
    @closed="resetForm"
    top="5vh"
  >
    <el-form
        :model="form"
        status-icon
        label-width="120px"
        ref="form"
        size="mini"
    >
    <div>
        <p class="sub-t">基本信息</p>
        <div class="flex-box">
             <el-form-item label="Aid" prop="aid">
        <el-input v-model="form.aid" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>

      <el-form-item label="Url" prop="url">
        <el-input v-model="form.url" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>

      <el-form-item label="Http方法" prop="httpMethod">
        <el-input v-model="form.httpMethod" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>

      <el-form-item label="Timeout" prop="timeout">
        <el-input v-model="form.timeout" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>

      <el-form-item label="创建时间" prop="ctime">
        <el-input v-model="form.ctime" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>

      <el-form-item label="更新时间" prop="utime">
        <el-input v-model="form.utime" autocomplete="off" :disabled="true"></el-input>
      </el-form-item>
        </div>
       
    </div>
    <div class="config">
        <p  class="sub-t">配置参数</p>
         <el-form-item label="Headers" prop="headers">
            <el-input type="textarea" :placeholder="placeholerText.header" v-model="form.headers" :rows="4" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="请求参数" prop="params">
            <el-input type="textarea" :placeholder="placeholerText.reqParams" v-model="form.params" :rows="4" autocomplete="off"></el-input>
        </el-form-item>
    </div>
     <div class="debug-result">
         <p  class="sub-t">调试结果</p>
         <el-form-item label="请求结果" prop="result">
            <el-input type="textarea" v-model="form.result" :rows="4" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="filter日志" prop="filterLog">
            <el-input type="textarea" v-model="form.filterLog" :rows="4" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="脚本日志" prop="scriptLog">
            <el-input type="textarea" v-model="form.scriptLog" :rows="4" autocomplete="off"></el-input>
        </el-form-item>
    </div>

    </el-form>
      <el-dialog
      width="40%"
      title=" curl Text"
      :visible.sync="innerVisible"
      append-to-body>
     <div style="display:flex">
          <el-input v-model="curlText" size="mini" id="debug-curltext"></el-input>
      <el-button @click="clipBoardHandler" size="mini" type="primary">复制到剪切板</el-button>
     </div>
    </el-dialog>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleRowDebugCancel" size="mini">取 消</el-button>
      <el-button type="primary" size="mini" @click="handleRowDebug">开始调试</el-button>
      <el-button type="primary" size="mini" @click="makeCurlText">生成Curl</el-button>
      <el-button type="primary" size="mini" @click="resetRowDebug">重置</el-button>
    </div>
  </el-dialog>
</template>
<script>
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'

export default {
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        form: {
            type: Object,
            default: () => ({})
        }
    },
    data(){
        return {
            innerVisible:false,
            curlText:"",
            placeholerText:{
                header:`{"Content-Type":"application/json"}`,
                reqParams:`[{},{"baseParam":{"source":"mione"},"pageIdx":0, "pageSize":"10"}]`
            }
        }
    },
    methods: {
        updateVisible (newValue) {
            this.$emit('update:visible', newValue)
        },
        resetForm(){
            this.$emit("resetDebugForm",{})
        },
        makeCurlText(){
        let Method = (this.form.httpMethod&&this.form.httpMethod.toUpperCase())||"GET"
        let url=this.getPrefix()+this.form.url;
        let header =this.getHeader()||"";
        let data = this.form.params||"";
        let showResult=false;
        if(data===null||data.trim()===""){
            showResult =true;
        }else{
                try {
            data= JSON.stringify(JSON.parse(data))
            showResult=true;
            } catch (error) {
                this.$message({
                    type:"error",
                    message:"请求参数有误"
                })
            }
        }
    
        if(!showResult){
            return;
        }
            let ret=`curl -X ${Method} '${url}' ${header} -d '${data}'`
            this.curlText=ret;
            this.innerVisible=true;
        },
        clipBoardHandler(){
            let textInput = document.querySelector(`#debug-curltext`)
            textInput.select();
            if(document.execCommand){
                let isCopySucceed = document.execCommand("copy")
                if(isCopySucceed){
                    this.$message.success("已复制到剪切板")
                    this.innerVisible=false;
                }else{
                    this.$message.error("复制失败，请手动复制")
                }
            }else{
                this.$message.error("请使用最新版本的chrome浏览器！")
            }
        },
        getHeader(){
            let str = this.form.headers;
            let map = {};
            if(str==null||str.trim()===""){
                return "";
            }
            try {
                map =JSON.parse(str);
               let headerStr = this.obj2Str(map)
               return headerStr

            } catch (error) {
                this.$message({
                    type:"error",
                    message:"header格式有误，请使用json"
                })  
              
            }
            return "";

        },
        obj2Str(map){
            let mapArr = Object.entries(map)
            let list = mapArr.map(kv=>{
                return ` -H '${kv[0]}:${kv[1]}'`
            })
            let ret = ""
            list.forEach(str=>{
                ret+=str
            })
            return retF
        },
        getPrefix(){
            let hostname=""
            switch (window.serverEnv){
                case "local":hostname= `xxxx`;break;
                case "staging":hostname=`xxxxx`;break;
                case "intranet":hostname=`xxxx`;break;
                case "c4":hostname=`xxxxx`;break;
                case "c3":hostname=`xxxxx`;break;
                default:break;
            }
            let protocol = window.serverEnv!=="c3"&&window.serverEnv!=="c4"?"http":"https"
            return `${protocol}://${hostname}`
        },
        handleRowDebugCancel () {
            this.updateVisible(false)
            // this.$message({
            //     message: '取消调试',
            //     type: 'warning'
            // })
        },
        handleRowDebug () {
            this.$refs['form'].validate((valid) => {
                if (!valid) {
                    this.$message({
                        message: '请检查参数',
                        type: 'warning'
                    })
                    return false
                }

                service({
                    url: '/apiinfo/debug',
                    method: 'post',
                    data: {
                        aid: this.form.aid,
                        url: this.form.url,
                        httpMethod: this.form.httpMethod,
                        headers: this.form.headers,
                        timeout: this.form.timeout,
                        params: this.form.params
                    }
                }).then(res => {
                    res.ctime = bizutil.timeFormat(res.ctime)
                    res.utime = bizutil.timeFormat(res.utime)
                    if (res.ext) {
                        let ext = JSON.parse(res.ext)
                        res.scriptLog = ext.scriptDebug
                    }
                    this.$emit('update:form', res)
                }).catch(() => {
                }).finally(() => {
                    this.$refs['form'].clearValidate()
                })
            })
        },
        resetRowDebug () {
            this.form.result = ''
            this.form.scriptLog = ''
            this.form.filterLog = ''
        }
    }
}
</script>
<style lang="scss" scoped>
   
    .flex-box{
        display: flex;
        flex-wrap: wrap;
        .el-form-item{
           flex:0 0 48%;
        }
    }
    .sub-t{
        font-weight: bold;
        padding: 0 0 0 10px;;
        margin: 0 auto 5px;
    }
</style>
<style lang="scss">
 .debug-dialog{
     .el-form-item{
         margin-bottom:8px;
     }
     .el-dialog__header{
         padding: 10px;
     }
      .el-dialog__body{
            padding:10px;
        }
        .el-dialog__body{
            padding:10px;
        }
         .el-dialog__footer{
             padding-top:2px;
            padding-bottom:10px;
        }
}
</style>

