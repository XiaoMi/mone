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
package com.xiaomi.mone.log.server.porcessor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.server.service.AgentConfigAcquirer;
import com.xiaomi.mone.log.server.service.DefaultAgentConfigAcquirer;
import com.xiaomi.youpin.docean.Ioc;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description The receiver that communicates with the agent ---- the agent starts to get the configuration
 * @date 2021/8/19 15:32
 */
@Slf4j
public class AgentConfigProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(Constant.RPCCMD_AGENT_CONFIG_CODE);
        String ip = new String(request.getBody());
        log.info("agent start get metadata config，agent ip:{}", ip);

        AgentConfigAcquirer agentConfigService = Ioc.ins().getBean(DefaultAgentConfigAcquirer.class);

        LogCollectMeta logCollectMeta = agentConfigService.getLogCollectMetaFromManager(ip);
        String responseInfo = GSON.toJson(logCollectMeta);
        log.info("agent start get metadata config info:{}", responseInfo);
        response.setBody(responseInfo.getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
