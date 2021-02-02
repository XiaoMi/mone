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

package com.xiaomi.youpin.mischedule.config;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingTask extends Task {

    public PingTask(RpcClient client) {
        super(() -> {
            try {
                PingReq ping = new PingReq();
                ping.setIp(client.getClient().getAddress().get());
                ping.setTime(System.currentTimeMillis());
                ping.setMessage("ping");
                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.pingReq);
                req.setBody(new Gson().toJson(ping).getBytes());
                Channel c = client.getClient().createChannel();
                if (null == c) {
                    log.info("channel is null");
                    return;
                }
                client.sendMessage(client.getServerAddrs(), req, responseFuture -> {
                    if (null != responseFuture && null != responseFuture.getResponseCommand()) {
                        log.info("{}", new String(responseFuture.getResponseCommand().getBody()));
                    }
                });

            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }, 5);
    }
}
