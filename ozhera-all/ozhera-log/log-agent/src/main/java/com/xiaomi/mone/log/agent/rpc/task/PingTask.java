/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.rpc.task;

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.mone.log.api.model.meta.AppLogMeta;
import com.xiaomi.mone.log.api.model.vo.PingReq;
import com.xiaomi.mone.log.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingTask extends Task {

    private static final AtomicBoolean docker = new AtomicBoolean(false);

    public static final AtomicBoolean stop = new AtomicBoolean(false);

    private static final AtomicBoolean load = new AtomicBoolean(false);

    private static final AtomicReference<String> aip = new AtomicReference<>("");

    private static final String IP = NetUtil.getLocalIp();


    public PingTask(RpcClient client) {
        super(() -> {
            if (stop.get()) {
                return;
            }
            try {
                PingReq ping = new PingReq();
                ping.setIp(IP);
                if (!load.get()) {
                    ping.setMessage("load");
                } else {
                    ping.setMessage("ping:" + System.currentTimeMillis());
                }
                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.pingReq);
                req.setBody(GSON.toJson(ping).getBytes());

                client.sendMessage(client.getServerAddrs(), req, responseFuture -> {
                    if (null == responseFuture.getResponseCommand()) {
                        return;
                    }
                    String body = new String(responseFuture.getResponseCommand().getBody());
                    if (!load.get()) {
                        AppLogMeta alm = GSON.fromJson(body, AppLogMeta.class);
                        load.set(true);
                        log.info("load config finish:{}", alm);
                    }
                    RpcClient.startLatch.countDown();
                    log.info("ping res:{}", body);
                });

            } catch (Exception ex) {
                log.error("ping error:{}", ex.getMessage());
            }
        }, 10);
    }


}
