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
  <div class="container">
        <el-select v-model="address"
        filterable
        placeholder="ip或者机器名搜索"
        :filter-method="searchMethod"
        @blur="resetFilterClientList($event)"

         class="selector">
            <el-option v-for="item in filteredClientList" :key="item.id+'-'+item.agentPort" :label="item.showName" :value="`${item.ip}:${item.agentPort}`" >
            <span style="float: left">{{ item.showName }}</span>
      <span style="float: right; color: #8492a6; font-size: 13px">{{ item.ip }}</span>
            </el-option>
          </el-select>
    <div
    class="terminal"
    id="terminal"
    v-contextmenu:contextmenu
    :style="{backgroundColor:bgColor,color: fontColor}"

  >
    <div class="header">
      <span>{{address||"请选择一台机器"}}</span>
    </div>
    <div id="xterm-wrapper">
      <div
        v-for="(tab, index) in terminals"
        :key="index"
        class="xterm-tabs"
        v-show="index == currentTab"
      >
        <div v-if="tab.children.length >= 4" class="xterm-tab-item">
          <el-row type="flex" class="el-row-4">
            <el-col :span="12" v-for="(item, k) in tab.children.slice(0,2)" :key="k" class="el-col">
              <div class="terminal-pane" :id="item.name"></div>
            </el-col>
          </el-row>
          <el-row type="flex" class="el-row-4">
            <el-col :span="12" v-for="(item, k) in tab.children.slice(2)" :key="k" class="el-col">
              <div class="terminal-pane" :id="item.name"></div>
            </el-col>
          </el-row>
        </div>
        <div v-else class="xterm-tab-item">
          <el-row type="flex">
            <el-col
              :span="(24/tab.children.length)"
              v-for="(item, k) in tab.children"
              :key="k"
              class="el-col"
            >
              <div class="terminal-pane" :id="item.name"></div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <v-contextmenu ref="contextmenu" class="contextmenu">
      <v-contextmenu-item @click="dialogVisible = true">设置</v-contextmenu-item>
    </v-contextmenu>
    <config-modal :visible.sync="dialogVisible" @setTheme="handleChangeTheme"></config-modal>
  </div>
  </div>
</template>
<script>
import SockJS from "sockjs-client"

import uuidv4 from "uuid/v4"
import { WebLinksAddon } from "xterm-addon-web-links"
import request from "@/plugin/axios/index"

import Terminal from "./Xterm"
import ConfigModal from "./components/Config"
// import mockData from './test.js'

function isInRect (rect, event) {
  if (
    event.clientY >= rect.top &&
    event.clientY <= rect.top + rect.height &&
    event.clientX >= rect.left &&
    event.clientX <= rect.left + rect.width
  ) {
    return true
  } else {
    return false
  }
}

export default {
  name: "Terminal",
  data () {
    return {
      address: "",
      clientList: [],
      filteredClientList: [],
      term: null,
      terminals: [],
      socket: null,
      currentTab: 0,
      cmdChecke: "",
      dialogVisible: false,
      theme: window.localStorage.getItem("theme")
        ? JSON.parse(window.localStorage.getItem("theme"))
        : null
    }
  },
  computed: {
    bgColor () {
      if (this.theme) {
        return this.theme.background
      } else {
        return "#000"
      }
    },
    fontColor () {
      if (this.theme) {
        return this.theme.foreground
      } else {
        return "#fff"
      }
    }
  },
  components: {
    ConfigModal
  },
  methods: {
    getClientList () {
      request({
        url: "/dpagent/getAgentDetailList"
      }).then(res => {
        // res=mockData.data
        if (!Array.isArray(res.list)) return
        this.clientList = this.fixClientList(res.list)
        this.filteredClientList = Object.assign([], this.clientList)
      })
    },
    fixClientList (list) {
      return list.map(client => {
        let showName = client.name || client.hostname || client.ip
        return Object.assign({}, client, { showName })
      })
    },
    resetFilterClientList (e) {
      if (e.target.value !== "") return
      this.filteredClientList = Object.assign([], this.clientList)
    },
    searchMethod (keyword) {
      if (!keyword) {
        this.filteredClientList = this.clientList
      } else {
        this.filteredClientList = this.clientList.filter(it => {
          return (it.name && it.name.includes(keyword)) || (it.hostname && it.hostname.includes(keyword)) || (it.ip && it.ip.includes(keyword))
        })
      }
    },

    createTerminal (container, callback, cwd = null) {
      let terminalname = "terminal" + uuidv4()

      let term = new Terminal({
        theme: this.theme,
        rows: 100,
        cols: 100,
        cursorBlink: true, // 光标闪烁
        cursorStyle: "block", // 光标样式  null | 'block' | 'underline' | 'bar'
        scrollback: 800, // 回滚
        tabStopWidth: 8, // 制表宽度
        screenKeys: true
      })
      this.term = term
      term.loadAddon(new WebLinksAddon())

      let pane = { term: term, name: terminalname }

      container.children.push(pane)
      container.currentPane = container.children.length - 1
      callback && callback()
      term.on("data", data => {
        // this.socket.emit(terminalname + "-input", data);

        if (data.charCodeAt() === 127) {
          this.cmdChecke = this.cmdChecke.substring(0, this.cmdChecke.length - 1)
          // eslint-disable-next-line
        } else if (data !== "\r" && data !== "OD" && data !== "OC") {
          this.cmdChecke += data
        } else {
          if (this.containExit(this.cmdChecke) && data === '\r') {
            this.sendErrorMsg()
            this.socket && this.socket.close()
          }
          this.cmdChecke = ""
        }

        let msg = this.data2Command(data)
        this.socket && this.socket.send(msg)
      })

      window.addEventListener("resize", () => {
        term.fit()
      })
      // this.socket.emit("create", { name: terminalname, cwd });

      this.$nextTick(() => {
        term.open(document.getElementById(terminalname))
        // console.log(term.rows);
        container.children.forEach(item => {
          let termEle = document.getElementById(item.name)
          if (item.term.element !== termEle.children[0]) {
            termEle.innerHTML = ""
            termEle.append(item.term.element)
          }
          item.term.fit()
          item.rect = item.term.element.getBoundingClientRect()
        })
      })
    },
    containExit (str) {
      let trimedStr = str.replace(/\s/g, "&&").replace(";", "&&")
      let pipe = trimedStr.split("|")
      let And = trimedStr.split("&&")
      // console.log("check ",trimedStr,pipe,And);
      if (pipe.includes("exit") || And.includes("exit") || trimedStr === 'exit') {
        return true
      }
      return false
    },
    sendErrorMsg (msg) {
      let errorMsg = msg || "\n检测到exit命令 自动断开socket，如需继续使用请切换机器或者刷新页面"
      this.address = ""
      this.term.writeln("")
      this.term.writeln(errorMsg)
    },
    data2Command (command, operate = "command") {
      let HostPort = this.address.replace("/", "").split(":")
      let host = HostPort[0]
      let port = HostPort[1]
      // rm
      // host ="127.0.0.1";
      // port = "22";
      let msgBase = {
        operate,
        host,
        agentPort: port,
        command
      }
      return JSON.stringify(msgBase)
    },
    ping () {
      let msg = this.data2Command("", "connect")
      this.socket.send(msg)
    },
    handleCreateTab () {
      // 新建Tab
      let tab = { name: "tab0", children: [] }
      this.createTerminal(tab, () => {
        this.terminals.push(tab)
        this.currentTab = this.terminals.length - 1
      })
    },
    handleChangeTheme (val) {
      this.theme = val
      this.terminals.forEach(tab => {
        tab.children.forEach(pane => {
          pane.term.setOption("theme", val)
        })
      })

      window.localStorage.setItem("theme", JSON.stringify(val))
    },

    close () {
      if (this.terminals.length > 0) {
        this.terminals.forEach(tab => {
          if (tab.children) {
            tab.children.forEach(({ term, name }) => {
              term.destroy()
              // this.socket.emit(name + "-exit");
            })
          }
        })
      }
      this.socket && this.socket.close()
    },
    Connect2Ws () {
      this.socket = new SockJS(window.location.origin + "/ws/ssh")
      this.socket.onopen = () => {
        this.term.writeln("socket enabled")
        this.term.writeln("waiting to connect")
        this.ping()
      }
      this.socket.onmessage = ({ data }) => {
        this.term.write(data)
      }
    }
  },

  created () {
    this.getClientList()
    window.onkeydown = window.onkeyup = window.onkeypress = () => {
      let _key = window.event.keyCode
      if (window.event.ctrlKey && _key === 68) {
        this.sendErrorMsg()
        this.socket && this.socket.close()
      }
    }
  },

  mounted () {
    if (this.terminals.length === 0) {
      let tab = { name: "tab0", children: [] }
      this.createTerminal(tab, () => {
        this.terminals.push(tab)
        this.currentTab = this.terminals.length - 1
      })
    }
  },
  watch: {
    address: function (newVal) {
      if (newVal.length === 0 || !newVal) return
      this.term.writeln("")
      this.term.writeln(`Switching to ${newVal}`)
      this.Connect2Ws()
    }
  },

  beforeDestroy () {
    this.close()
  }
}
</script>

<style lang="scss" scoped>

.selector{
  margin-bottom: 10px;
}
#terminal {
  position: relative;
  height: 600px;
  overflow: hidden;
  padding-top: 40px;
  z-index: 1002;

  .header {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    font-weight: bolder;
    padding: 0 10px;
    box-sizing: border-box;
    border-bottom: 1px solid #909399;

    * {
      box-sizing: border-box;
    }
    .menu-list {
      list-style: none;
      float: right;
      height: 40px;
      li {
        padding: 0 10px;
        line-height: 40px;
        cursor: pointer;
        float: left;
      }
    }
    .terminal-select {
      width: 120px;
      margin-right: 5px;
    }
    .el-icon-plus,
    .el-icon-delete {
      font-size: 18px;
    }
  }

  #xterm-wrapper {
    width: 100%;
    height: 600px;
    overflow: scroll;
    .xterm-tabs,
    .xterm-tab-item,
    .el-row {
      width: 100%;
      height: 100%;
    }

    .el-row-4 {
      height: 50%;
    }

    .el-row-4:last-child {
      border-top: 1px solid #dcdfe6;
    }

    .el-col {
      padding: 10px;
    }

    .terminal-pane {
      width: 100%;
      height: 100%;
    }

    .xterm {
      opacity: 0.6;
    }
    .xterm.focus {
      opacity: 1;
    }
  }
}

.contextmenu {
  background-color: #c0c4cc;
  border: 0;
  color: #303133;
  min-width: 100px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
}
.el-col-12:not(:last-child),
.el-col-8:not(:last-child) {
  border-right: 1px solid #dcdfe6;
  height: 100%;
}
</style>
