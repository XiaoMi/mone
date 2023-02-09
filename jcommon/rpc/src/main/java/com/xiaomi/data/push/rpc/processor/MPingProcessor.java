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

package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.bo.MPPing;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.common.RemotingHelper;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * @author goodjava@qq.com
 *
 * 使用msgpack
 */
@Slf4j
public class MPingProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        MPPing req = request.getReq(MPPing.class);
        log.info("MPingResProcessor ping:{}", req.getData());
        MPPing pong = new MPPing();
        pong.setData("pong");
        RemotingCommand response = RemotingCommand.createMsgpackResponse(RpcCmd.mpPingRes, pong);

        //存入user 信息
        Optional.ofNullable(req.getUser()).ifPresent(it->{
            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            AgentChannel ch = AgentContext.ins().map.get(remoteAddress);
            Optional.ofNullable(ch).ifPresent(c-> c.setUser(it));
        });

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
