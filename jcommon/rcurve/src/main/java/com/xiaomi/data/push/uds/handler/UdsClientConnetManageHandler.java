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

package com.xiaomi.data.push.uds.handler;

import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdsClientConnetManageHandler extends ChannelDuplexHandler {

    private final boolean reconnection;

    private final UdsClient udsClient;

    private final String path;

    public UdsClientConnetManageHandler(boolean reconnection, UdsClient udsClient, String path) {
        this.reconnection = reconnection;
        this.udsClient = udsClient;
        this.path = path;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("uds client channel active");
        UdsClientContext.ins().channel.set(ctx.channel());
    }

    /**
     * 不活跃的时候可以设置重连策略
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("client channelInactive");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        if (this.reconnection) {
            eventLoop.schedule(() -> {
                log.info("client reconnection:{}", path);
                udsClient.start(path);
            }, 1L, TimeUnit.SECONDS);
        }
        super.channelInactive(ctx);
    }


}
