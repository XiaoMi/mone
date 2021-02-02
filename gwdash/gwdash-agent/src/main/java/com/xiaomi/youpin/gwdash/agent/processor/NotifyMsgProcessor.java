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

package com.xiaomi.youpin.gwdash.agent.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.service.DataHubService;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * 处理客户端发送过来的通知信息
 */
@Slf4j
public class NotifyMsgProcessor implements NettyRequestProcessor {

    private DataHubService dataHubService;

    public NotifyMsgProcessor (DataHubService dataHubService) {
        this.dataHubService = dataHubService;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        NotifyMsg msg = remotingCommand.getReq(NotifyMsg.class);
        Map<String, String> attachments = msg.getAttachments();
        log.info("msg: {}", msg);
        if (null != attachments && StringUtils.isNotEmpty(attachments.get("msgType"))) {
            DataMessage dataMessage = new DataMessage();
            dataMessage.setId(msg.getBizId());
            dataMessage.setMsgType(attachments.get("msgType"));
            dataMessage.setData(new Gson().toJson(msg));
            dataHubService.sendMessage(dataMessage);
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
