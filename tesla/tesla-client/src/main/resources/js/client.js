    /*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

var socket;
    var version = "tesla-client:2019-04-22:0.0.3";

    //addr:ws://127.0.0.1:9999/ws
    function connect(addr,onmessage,onopen=function(event){},onclose=function(){event}) {
        console.log("version:"+version);
        if (window.WebSocket) {
                try {
                    socket = new WebSocket(addr);
                    socket.onmessage = function (event) {
                          console.log("receive:"+event.data);
                          onmessage(event.data);
                    }

                    socket.onopen = function (event) {
                          console.log("websocket open:"+event);
                          onopen("open:"+event);
                    }

                    socket.onclose = function (event) {
                          console.log("websocket close:"+event);
                          onclose("clsoe:"+event);
                    }
                }catch (error) {
                    console.log("error:"+error);
                }
        } else {
            console.log("don't support websocket")
        }
    }


//    function reg(uri) {
//        var msg = {"id":-1,"uri":uri,"data":[]};
//        send(msg);
//    }


    /**
    send message
    */
    function send(message) {
        if(socket.readyState == WebSocket.OPEN){
            socket.send(message)
        }else{
            console.log("don't open");
        }
    }