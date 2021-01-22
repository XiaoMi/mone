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
  <div class="system-log-con" 
  :style="{backgroundColor:bgColor,color: fontColor}"  >
    <div class="header">系统日志(beta)</div>
    <div :id="domId" class="system-log">
  
  </div>
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import Terminal from "@/pages/sre/deploy-ssh/Xterm";
import { WebLinksAddon } from "xterm-addon-web-links";
import request from "@/plugin/axios/index";
export default {
  name: "SystemLog",
  props: {
    ip: {
      type: String,
      required: true,
      default() {
        return "";
      }
    },
    logPath: {
      type: String,
      required: true,
      default() {
        return "";
      }
    }
  },
  data() {
    return {
      domId: "systemLog",
      term: null,
      socket: null
    };
  },
  computed: {
    bgColor() {
      return "#000";
    },
    fontColor() {
      return "#fff";
    }
  },
  create() {},
  mounted() {
    this.createTerminal(() => {
      this.Connect2Ws();
    });
  },
  methods: {
    createTerminal(callback) {
      let term = new Terminal({
        theme: this.theme,
        rows: 100,
        cols: 100,
        cursorStyle: "block", // 光标样式  null | 'block' | 'underline' | 'bar'
        scrollback: 800, //回滚
        tabStopWidth: 8, //制表宽度
        screenKeys: true
      });
      this.term = term;
      term.loadAddon(new WebLinksAddon());
      term.open(document.getElementById(this.domId));
      term.on("data", data => {
        let msg = this.data2Command(data);
        this.socket && this.socket.send(msg);
      });
      this.$nextTick(() => {
        this.term.fit();
        window.term = this.term;
        callback && callback();
      });
    },
    Connect2Ws() {
      this.socket = new SockJS(window.location.origin + "/ws/agentSystemlog");
      this.socket.onopen = () => {
        this.term.writeln("socket enabled");
        this.term.writeln("waiting to connect");
        this.ping();
      };
      this.socket.onmessage = ({ data }) => {
        this.term.write(data);
      };
    },
    ping() {
      let msg = this.data2Command("", "connect");
      this.socket.send(msg);
    },
    data2Command(command, operate = "command") {
      let HostPort = this.ip.replace("/", "").split(":");
      let host = HostPort[0];
      let port = HostPort[1];
      let logPath = this.logPath.replace("\r", "");
      if (!logPath || logPath.length === 0) {
        logPath = "/var/log/system.log";
      }
      // rm
      // host ="127.0.0.1";
      // port = "22";
      let msgBase = {
        operate,
        host,
        agentPort:port,
        command,
        logPath
      };

      return JSON.stringify(msgBase);
    }
  }
};
</script>

<style lang="scss" scoped>
.system-log-con {
  padding: 0px 10px;
  height: 500px;
}
.header {
  width: 100%;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  font-weight: bolder;
  padding: 0 10px;
  box-sizing: border-box;
  border-bottom: 1px solid #909399;
}
.system-log {
  position: relative;
  width: 100%;
  height: 460px;
}
</style>