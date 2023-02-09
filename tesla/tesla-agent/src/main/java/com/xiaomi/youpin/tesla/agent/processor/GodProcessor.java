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

package com.xiaomi.youpin.tesla.agent.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.GodReq;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author goodjava@qq.com
 * god 操作 processor
 * 简单封装
 */
@Deprecated
@Component
public class GodProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {

        GodReq req = remotingCommand.getReq(GodReq.class);

        switch (req.getCmd()) {
            case "status": {
                Pair<Integer, List<String>> pair = ProcessUtils.process("xxxx", "god status");
                return RemotingCommand.createResponseCommand(AgentCmd.godRes, new Gson().toJson(pair).getBytes());
            }
            case "stop": {
                Pair<Integer, List<String>> pair = ProcessUtils.process("xxxx", "god stop " + req.getName());
                return RemotingCommand.createResponseCommand(AgentCmd.godRes, new Gson().toJson(pair).getBytes());
            }
            case "start": {
                Pair<Integer, List<String>> pair = ProcessUtils.process("xxxx", "god start " + req.getName());
                return RemotingCommand.createResponseCommand(AgentCmd.godRes, new Gson().toJson(pair).getBytes());
            }
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.godReq;
    }
}
