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

package com.xiaomi.youpin.tesla.file.server.service;

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class BaseService {

    public static final String DATAPATH = Cons.DATAPATH;

    public static void send(ChannelHandlerContext ctx, String message) {
        send(ctx, OK, message);
    }

    public static void send(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        try {
            ByteBuf msg = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, msg);
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, msg.readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (Throwable ex) {
            log.error("send error:{}", ex.getMessage());
        }
    }


    public static boolean checkParams(ChannelHandlerContext ctx, String name, String token) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(token)) {
            BaseService.send(ctx, "error:" + name + ":bad:request");
            return false;
        }
        return true;
    }

    public static boolean checkToken(ChannelHandlerContext ctx, String name, String token, String path) {
        if (!new TokenService().check(name, token, path)) {
            BaseService.send(ctx, "error:" + name + ":token:error");
            return false;
        }
        return true;
    }


}
