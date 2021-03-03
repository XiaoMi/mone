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

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcVersion;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingProcessor implements NettyRequestProcessor {

    /**
     * 对入参的处理
     */
    private Function<String, String> function;


    public PingProcessor() {
    }

    public PingProcessor(Function<String, String> function) {
        this.function = function;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        log.info("PingResProcessor ping:{}", new String(request.getBody()));

        RemotingCommand response = RemotingCommand.createResponseCommand(RpcCmd.pingRes);

        if (null != function) {
            String res = function.apply(new String(request.getBody()));
            response.setBody(res.getBytes());
            return response;
        }

        RpcVersion version = new RpcVersion();
        response.setBody(("pong:" + version + ":" + new Date()).getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
