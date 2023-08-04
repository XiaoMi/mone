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

import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.common.RemotingHelper;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.meta.AppLogMeta;
import com.xiaomi.mone.log.api.model.vo.PingReq;
import com.xiaomi.mone.log.server.common.Version;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/24 11:38
 */
@Slf4j
public class PingProcessor implements NettyRequestProcessor {

    public static Map<String, Long> agentHeartTimeStampMap = new ConcurrentHashMap<>(1024);

    private static Version version = new Version();

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channelHandlerContext.channel());
        RemotingCommand response = RemotingCommand.createResponseCommand(RpcCmd.pingRes);
        String body = new String(remotingCommand.getBody());
        PingReq pr = GSON.fromJson(body, PingReq.class);

        AgentChannel ch = AgentContext.ins().map.get(remoteAddress);
        if (null != ch) {
            ch.setIp(pr.getIp());
        }
        response.setBody(version.toString().getBytes());
        if (null != pr && StringUtils.isNotBlank(pr.getIp())) {
            agentHeartTimeStampMap.put(pr.getIp(), Instant.now().toEpochMilli());
        }

        if (pr.getMessage().equals("load")) {
            AppLogMeta meta = new AppLogMeta();
            meta.setAppName("log-manager");
            meta.setAppId(ThreadLocalRandom.current().nextLong());
            response.setBody(GSON.toJson(meta).getBytes());
        }

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
