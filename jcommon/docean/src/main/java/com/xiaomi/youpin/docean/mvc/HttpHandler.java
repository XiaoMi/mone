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

package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private HttpServerConfig config;

    public HttpHandler(HttpServerConfig config) {
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (config.isUserWs() && Cons.WebSocketPath.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
            return;
        }
        HttpHandlerRead.read(ctx,request,this.config);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof DecoderException) {
            DecoderException ex = (DecoderException) cause;
            if (ex.getMessage().contains("certificate_unknown")) {
                return;
            }
        }
        log.info("remote address:{} error:{}", ctx.channel().remoteAddress(), cause.getMessage());
        if (null != ctx.channel() && ctx.channel().isOpen() && ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }
}
