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
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class GetInfoTask extends Task {

    private RpcServer rpcServer;

    public GetInfoTask(final RpcServer rpcServer) {
        super(() -> {
            run(rpcServer);
        }, 5);
    }


    private static void run(RpcServer rpcServer) {
        rpcServer.sendMessageToAll(RemotingCommand.createRequestCommand(RpcCmd.getInfoReq), (f) -> {
            log.info(new String(f.getResponseCommand().getBody()));
        });
    }
}
