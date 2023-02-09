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

package com.xiaomi.youpin.tesla.agent.rpc;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.tesla.agent.bo.MqCommand;
import com.xiaomi.youpin.tesla.agent.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdpHandler {

    private static final String PWD = Config.ins().get("udp_pwd", "");

    public static String process(RpcClient client, String body) {
        log.info("mq service req:{}", body);
        if (StringUtils.isEmpty(body)) {
            return new Gson().toJson(RemotingCommand.createResponseCommand(500, ""));
        }
        MqCommand command = new Gson().fromJson(body, MqCommand.class);

        if (!(PWD).equals(command.getPwd())) {
            log.error("pwd error");
            RemotingCommand res = RemotingCommand.createResponseCommand(500, "pwd error");
            return new Gson().toJson(res);
        }

        NettyRequestProcessor processor = client.getProcessor(command.getCmd());
        try {
            RemotingCommand res = processor.processRequest(null, RemotingCommand.createResponseCommand(command.getCmd(), command.getBody()));
            return new Gson().toJson(res);
        } catch (Throwable ex) {
            log.error("error:{}", ex.getMessage());
            return new Gson().toJson(RemotingCommand.createResponseCommand(500, ex.getMessage()));
        }
    }
}
