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
package com.xiaomi.mone.log.agent.rpc.processor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.channel.ChannelEngine;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineRpcLocator;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.vo.LogCmd;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class LogProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        LogCollectMeta req = remotingCommand.getReq(LogCollectMeta.class);

        log.info("logCollect config req:{}", GSON.toJson(req));

        RemotingCommand response = RemotingCommand.createResponseCommand(LogCmd.logRes);
        response.setBody("ok".getBytes());
        log.info("【config change】receive data：{}", GSON.toJson(req));
        metaConfigEffect(req);
        log.info("config change success");
        return response;
    }

    private synchronized void metaConfigEffect(LogCollectMeta req) {
        ChannelEngine channelEngine = Ioc.ins().getBean(ChannelEngine.class);
        // Whether the initialization is completed or not, wait for 30 s before executing
        int count = 0;
        while (true) {
            if (!channelEngine.isInitComplete()) {
                try {
                    TimeUnit.SECONDS.sleep(5L);
                    ++count;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (channelEngine.isInitComplete() || count >= 20) {
                break;
            }
        }
        if (CollectionUtils.isNotEmpty(req.getAppLogMetaList())) {
            try {
                List<ChannelDefine> channelDefines = ChannelDefineRpcLocator.agentTail2ChannelDefine(ChannelDefineRpcLocator.logCollectMeta2ChannelDefines(req));
                channelEngine.refresh(channelDefines);
            } catch (Exception e) {
                log.error("refresh config error,req:{}", GSON.toJson(req), e);
            }
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }


    @Override
    public int cmdId() {
        return LogCmd.logReq;
    }
}
