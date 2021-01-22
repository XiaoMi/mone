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
      <el-form label-width="80px" size="mini">
        <el-form-item label="cmd">
          <el-input v-model="form.cmd" />
        </el-form-item>
        <el-form-item label="命令">
          <el-input type="textarea" rows="3" v-model="form.body" />
        </el-form-item>
        <el-form-item label="节点">
          <el-select v-model="form.address">
            <el-option v-for="item in clientList" :key="item" :label="item" :value="item" />
          </el-select>
          <el-button @click="getClientList" type="primary" icon="el-icon-refresh"></el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendCmd()">执行</el-button>
          <el-button type="primary" @click="sendAllCmd">全部执行</el-button>
          
        </el-form-item>
        <el-form-item label="执行结果">
          <codemirror
            v-model="form.feedback"
            :options="{
                        tabSize: 4,
                        indentUnit: 4,
                        readOnly: 'nocursor',
                        mode: 'text/javascript',
                        theme: 'base16-dark',
                        lineNumbers: true,
                        line: true,
                        smartIndent: true
                    }"
          />
        </el-form-item>
      </el-form>
    </d2-module>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index";
import qs from "qs";
import SockJS from "sockjs-client";
import { parse } from 'path';

export default {
  data() {
    return {
      form: { feedback: "",
          // cmd:5000,
          // body:`{"shellCmd":"ls","path":"/tmp/"}`
       },
      clientList: []
    };
  },
  created() {
    this.getClientList();
  },
  beforeDestroy() {
    this.socket && this.socket.close();
  },
  methods: {
    getClientList() {
      request({
        url: "/dpagent/list"
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        this.clientList = res.list;
      });
    },
    sendCmd(list) {
      const form = this.form;
     if(!this.form.address&&!list){
       this.$message({
         type:"error",
         message:"请选择一个节点"
       })
       return;
     }
      request({
        url: "/dpagent/cmd",
        method: "post",
        data: qs.stringify({
          ...form,
          address:list&&list.length>0?list.join(","):this.form.address,
          uuid: userInfo.uuid
        })
      }).then(data => {
        let retString ="";
        try {
          retString= JSON.stringify(JSON.parse(data),null,2);
        } catch (error) {
          retString = data
        }
        form.feedback = `${retString}\n${form.feedback}`;
      });
    },
    sendAllCmd(){
      this.sendCmd(this.clientList);
    }
  },
 
};
</script>