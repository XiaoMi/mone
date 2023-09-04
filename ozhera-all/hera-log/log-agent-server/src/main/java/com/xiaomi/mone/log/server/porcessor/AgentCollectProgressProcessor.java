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
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.server.common.Version;
import com.xiaomi.mone.log.server.service.DefaultLogProcessCollector;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description 与agent通信的接收器----采集进度
 * @date 2021/8/19 15:32
 */
@Slf4j
@Component
public class AgentCollectProgressProcessor implements NettyRequestProcessor {

    @Resource
    DefaultLogProcessCollector processService;

    private static Version version = new Version();

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        log.debug("接受到了agent发过来的信息");
        RemotingCommand response = RemotingCommand.createResponseCommand(Constant.RPCCMD_AGENT_CODE);
        String body = new String(request.getBody(), StandardCharsets.UTF_8);
        UpdateLogProcessCmd cmd = GSON.fromJson(body, UpdateLogProcessCmd.class);
        log.debug("agent发过来的客户端的请求:{}", cmd.getIp());
        if (null == processService && Ioc.ins().containsBean(DefaultLogProcessCollector.class.getCanonicalName())) {
            processService = Ioc.ins().getBean(DefaultLogProcessCollector.class);
        }
        if (null != processService) {
            processService.collectLogProcess(cmd);
        }
        response.setBody(version.toString().getBytes());
        response.setBody(Constant.SUCCESS_MESSAGE.getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
