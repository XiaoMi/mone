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

package com.xiaomi.mione.mquic.demo.server.handler;

import com.xiaomi.mione.mquic.demo.server.dispatcher.Dispatcher;
import com.xiaomi.mione.mquic.demo.server.manager.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelManager channelManager;

    private Dispatcher dispatcher;

    public ServerHandler(ChannelManager channelManager, Dispatcher dispatcher) {
        this.channelManager = channelManager;
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelManager.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelManager.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(CharsetUtil.UTF_8).trim();
        try {
            dispatcher.execute(message, ctx.channel());
        } finally {
            byteBuf.release();
        }
    }


}
