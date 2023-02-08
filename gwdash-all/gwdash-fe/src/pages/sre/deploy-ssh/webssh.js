/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

function WSSHClient () {
};

WSSHClient.prototype._generateEndpoint = function () {
  let protocol = ""
  if (window.location.protocol === 'https:') {
    protocol = 'wss://'
  } else {
    protocol = 'ws://'
  }
  var endpoint = protocol + window.location.origin + '/ws/ssh'
  return endpoint
}

WSSHClient.prototype.connect = function (options) {
  var endpoint = this._generateEndpoint()

  if (window.WebSocket) {
    // 如果支持websocket
    this._connection = new WebSocket(endpoint)
  } else {
    // 否则报错
    options.onError('WebSocket Not Supported')
    return
  }

  this._connection.onopen = function () {
    options.onConnect()
  }

  this._connection.onmessage = function (evt) {
    var data = evt.data.toString()
    // data = base64.decode(data);
    options.onData(data)
  }

  this._connection.onclose = function (evt) {
    options.onClose()
  }
}

WSSHClient.prototype.send = function (data) {
  this._connection.send(JSON.stringify(data))
}

WSSHClient.prototype.sendInitData = function (options) {
  // 连接参数
  this._connection.send(JSON.stringify(options))
}

WSSHClient.prototype.sendClientData = function (data) {
  // 发送指令
  this._connection.send(JSON.stringify({ "operate": "command", "command": data }))
}

export default WSSHClient
