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

package com.xiaomi.data.push.demo;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.common.NetUtils;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.processor.GetInfoProcessor;
import com.xiaomi.data.push.rpc.processor.SFile2Processor;
import com.xiaomi.data.push.rpc.processor.SFileProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DemoClient {

    public static void main(String... args) throws InterruptedException {
        RpcClient client = new RpcClient("127.0.0.1:80", "demo_server1");
        client.setReconnection(false);
        ClientInfo clientInfo = new ClientInfo("demo_client", NetUtils.getLocalAddress().getHostAddress(), 0, "0.0.1");
        client.setClientInfo(clientInfo);
        client.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.getInfoReq, new GetInfoProcessor()),
                new Pair<>(RpcCmd.sfileReq, new SFileProcessor()),
                new Pair<>(RpcCmd.sfileReq2, new SFile2Processor())
        ));
        client.setTasks(Lists.newArrayList(
                new Task(() -> {
                    try {
                        log.info("send ping");
                        RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.pingReq);
                        req.setBody("ping".getBytes());
                        client.sendMessage(client.getServerAddrs(), req, f -> log.info(new String(f.getResponseCommand().getBody())));

//                        MPPing p = new MPPing();
//                        p.setData("ppiinngg");
//                        client.sendMessage(client.getServerAddrs(), RemotingCommand.createMsgPackRequest(RpcCmd.mpPingReq, p), responseFuture -> {
//                            MPPing pong = responseFuture.getResponseCommand().getReq(MPPing.class);
//                            log.info("--->" + pong.getData());
//                        });
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }, 5)
        ));
        client.init();
        client.start();
        client.getClient().createChannel();
    }

}
