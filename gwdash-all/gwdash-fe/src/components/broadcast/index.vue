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
    <div
      class='broadcast-shell'
      ref='broadcastShell'
      @click='showBroadBox'>
      <div class='broadcast_label'>
        <div>
          <i class="fa fa-gg-circle"></i>
          <span>消息通知</span>
        </div>
      </div>
      <div class="broadcast_box" v-show="isOpen" ref='broadcastBox'>
        <div class="broadcast-header">
          <div class="header-text">广播通知</div>
          <div class="broadcast-close" @click.stop='closeBroadBox'>X</div>
        </div>
        <div class="message-box" ref="msgBox">
          <template v-for="(messageObj,index) in messageList">
              <MiddleBar :key="index" v-if="messageObj.operation==='open'|| messageObj.operation==='connected'||messageObj.operation==='disconnected'" :message="messageObj"></MiddleBar>
              <LeftBar :key="index" v-else-if="messageObj.operation==='receive'" :message="messageObj"></LeftBar>
              <RightBar :key="index" v-else-if="messageObj.operation==='send'" :message="messageObj"></RightBar>
          </template>
        </div>
        <div class="send-message-box">
          <el-input v-model="broadcastInput" @enter.ctrl.exact="send2Server" placeholder="ctrl+enter 发送" size="mini"></el-input>
          <el-button size="mini" @click="send2Server">发送</el-button>
        </div>
      </div>
    </div>
</template>

<script>
import SockJS from "sockjs-client"
import service from "@/plugin/axios/index"
import LeftBar from "./components/LeftBar.vue"
import RightBar from "./components/RightBar.vue"
import MiddleBar from "./components/MiddleBar.vue"
import drag from './drag'
import { setTimeout } from 'timers'

export default {
  name: "broadcast-box",
  data () {
    return {
      broadcastInput: "",
      socket: null,
      messageList: [],
      userInfo: {},
      isOpen: false
    }
  },
  async created () {
    let res = await this.loginCheck()
    if (res) {
      this.initSocket()
      // this.initKeyBroad()
    } else {
      console.log("获取用户信息异常，不再初始化broadcast")
    }
  },
  mounted () {
    drag({
      domShell: this.$refs.broadcastShell
    })
  },
  methods: {
    loginCheck () {
      return new Promise((resolve, reject) => {
        return service({
          url: "/account/own"
        })
          .then(res => {
            resolve(true)
          })
      })
    },
    // initKeyBroad () {
    //   window.onkeypress = () => {
    //     let _key = window.event.keyCode
    //     if (window.event.ctrlKey && _key === 13 && this.broadcastInput.length > 0) {
    //       this.send2Server()
    //     }
    //   }
    // },
    initSocket () {
      this.socket = new WebSocket("ws://" + window.location.host + "/ws/broadcast")
      this.socket.addEventListener("open", () => {
        let messageObj = {
          operation: "open",
          message: "broadcast socket opened"
        }
        this.showMessage(messageObj)
      })
      this.socket.addEventListener("message", ({ data }) => {
        let message = this.fixMessageData(data)
        // this.messageList.push(message)
        if (!this.isOpen) {
          this.noticeToMainBroad(message)
        }
        this.append2List(message)
      })
      this.socket.addEventListener("close", e => {
        console.log("error:", e)
      })
    },
    initSocket2 () {
      this.socket = new SockJS(window.location.origin + "/ws/broadcast")

      this.socket.onopen = () => {
        let messageObj = {
          operation: "open",
          message: "broadcast socket opened"
        }
        this.showMessage(messageObj)
        // this.ping();
      }
      this.socket.onmessage = ({ data }) => {
        let message = this.fixMessageData(data)
        // this.messageList.push(message)
        if (!this.isOpen) {
          this.noticeToMainBroad(message)
        }
        this.append2List(message)
      }
      this.socket.onclose = (data) => {
        console.log("error:", data)
      }
    },
    noticeToMainBroad (messageObj) {
      let noticeMsg = messageObj.name + ": " + messageObj.message
      if (messageObj.operation !== "receive") {
        // 只接受别人发的信息
        return
      }
      const h = this.$createElement
      this.$notify({
        title: '广播通知',
        message: h('i', { style: 'color: teal' }, noticeMsg)
      })
    },
    fixMessageData (message) {
      return this.repalceSendByMe(message)
    },
    repalceSendByMe (messageObj) {
      if (typeof messageObj === 'string') { messageObj = JSON.parse(messageObj) }
      if (this.userInfo.username === messageObj.username && messageObj.operation === "receive") {
        messageObj.operation = "send"
      }
      return messageObj
    },
    showMessage (message) {
      this.append2List(message)
    },
    scroll2Bottom () {
      let dom = this.$refs['msgBox']
      if (!dom) return
      let scrollHeight = dom.scrollHeight
      let scrollTop = dom.scrollTop
      if (scrollHeight > scrollTop) {
        dom.scrollTop = scrollHeight
      }
    },
    append2List (msgObj) {
      this.messageList.push(msgObj)
      setTimeout(() => {
        this.scroll2Bottom()
      }, 0)
    },
    send2Server () {
      let inputText = this.broadcastInput
      if (inputText.length > 0) {
        let msg = this.data2Message(inputText)
        let localMsg = JSON.parse(msg)
        localMsg.operation = "send"
        // this.messageList.push(localMsg)
        this.socket && this.socket.send(msg)
        this.broadcastInput = ""
      }
    },
    data2Message (msg) {
      this.userInfo = window.userInfo
      if (serverEnv === 'local') {
        this.userInfo.username = "xx_replace_xx"
        this.userInfo.name = "xx_replace_xx"
      }
      let msgBase = {
        operation: 'send',
        username: this.userInfo.username,
        name: this.userInfo.name,
        message: msg
      }
      return JSON.stringify(msgBase)
    },
    showBroadBox () {
      const domShell = this.$refs.broadcastShell
      const domBox = this.$refs.broadcastBox
      domBox.classList.remove('box-right', 'box-left', 'box-bottom')
      if (parseInt(domShell.style.left) + domShell.offsetWidth >= window.innerWidth) {
        domBox.classList.add('box-right')
      }
      if (domShell.style.left === '0px') {
        domBox.classList.add('box-left')
      }
      if (parseInt(domShell.style.top) + domShell.offsetHeight >= window.innerHeight) {
        domBox.classList.add('box-bottom')
      }
      this.isOpen = true
    },
    closeBroadBox () {
      this.isOpen = false
    }
  },
  beforeDestroy () {
    this.socket.close()
  },
  components: {
    LeftBar, RightBar, MiddleBar
  }
}
</script>

<style lang="scss" scoped>
.broadcast-text-box{
  position: fixed;
  left: 20px;
  bottom: 20px;
  width: 400px;
  height: 300px;
  z-index: 1033;
  padding: 10px;
  background: #f7f7f7;
  border-radius: 10px;
  overflow: hidden;
}
.broadcast-header{
  width: 100%;
  background: #fff;
  margin-bottom: 10px;
  font-size: 16px;
  border-bottom: 1px solid #dedede;
  padding: 5px 3px;
  overflow: hidden;
  .header-text{
    float: left;
  }
  .broadcast-close{
    float: right;
    display: block;
    width: 18px;
    height: 18px;
    text-align: center;
    line-height: 18px;
    color: red;
    border-radius: 50%;
    cursor: pointer;
  }
}
.message-box{
  height: 220px;
  overflow-y: scroll;
  &::-webkit-scrollbar-thumb {
  border-radius: 10px;
  box-shadow   : inset 0 0 5px rgba(0, 0, 0, 0.2);
  background   : #535353;
  }
}
.send-message-box{
  position: absolute;
  padding: 10px;
  bottom: 10px;
  left:0;
  width: 100%;
  box-sizing: border-box;
  display: flex;
}

.broadcast-shell {
  width: 38px;
  position: fixed;
  top: 680px;
  right: 0px;
  cursor: pointer;
  z-index: 1001;
  color: #1890ff;
  .broadcast_label {
    border-radius: 4px;
    background: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    div {
      width: 20px;
      text-align: center;
      padding: 10px 6px 12px;
      i {
        font-size: 20px;
        margin-bottom: 6px;
      }
      span {
        font-size: 14px;
      }
    }
  }
}
.broadcast_box {
  position: absolute;
  width: 400px;
  height: 300px;
  padding: 10px;
  background: #f7f7f7;
  border-radius: 10px;
  overflow: hidden;
}
.box-right {
  top: 0px;
  right: 45px;
}
.box-left {
  top: 0px;
  left: 45px;
}
.box-bottom {
  top: auto;
  bottom: 0px
}
</style>
