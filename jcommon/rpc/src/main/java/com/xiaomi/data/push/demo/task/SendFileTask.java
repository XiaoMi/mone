/*
 *  Copyright 2020 Xiaomi
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

package com.xiaomi.data.push.demo.task;

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 */
public class SendFileTask extends Task {

    public SendFileTask(RpcServer rpcServer) {
        super(() -> {
            File f = new File("/tmp/t");
            if (f.exists()) {
                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.sfileReq);
                try {
                    req.setBody(Files.readAllBytes(Paths.get("/tmp/t")));
                    req.addExtField("path", "/tmp/t");
                    req.addExtField("targetPath", "/tmp/tt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rpcServer.sendMessageToAll(req, null);
            }
        }, 5);
    }
}
