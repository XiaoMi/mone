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

package com.xiaomi.youpin.tesla.file.server.netty;

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import com.xiaomi.youpin.tesla.file.server.common.FileServerVersion;
import com.xiaomi.youpin.tesla.file.server.common.UserSecretConfig;
import com.xiaomi.youpin.tesla.file.server.service.BaseService;
import com.xiaomi.youpin.tesla.file.server.service.DownloadService;
import com.xiaomi.youpin.tesla.file.server.service.TokenService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.*;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class HttpChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.startsWith(Cons.HEALTH)) {
            BaseService.send(ctx, new FileServerVersion().toString());
            return;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(uri);

        String defaultId = UUID.randomUUID().toString();
        String id = decoder.parameters().getOrDefault("id", Arrays.asList(defaultId)).get(0);
        String name = decoder.parameters().getOrDefault("name", Arrays.asList("")).get(0);
        String token = decoder.parameters().getOrDefault("token", Arrays.asList("")).get(0);
        String userKey = decoder.parameters().getOrDefault("userKey", Arrays.asList("")).get(0);
        String userSecret = decoder.parameters().getOrDefault("userSecret", Arrays.asList("")).get(0);

        // 验证用户key和secret
        if (!UserSecretConfig.validateUser(userKey, userSecret)) {
            log.warn("Invalid user key or secret: {}", userKey);
            BaseService.send(ctx, "error:Invalid user key or secret");
            return;
        }

        uri = decoder.path();

        log.info("call uri:{} name:{} id:{}", uri, name, id);



        if (!BaseService.checkParams(ctx, name, token)) {
            log.warn("param error");
            return;
        }


        if (!BaseService.checkToken(ctx, name, token, uri)) {
            log.warn("token error");
            return;
        }


        //下载
        if (uri.startsWith(Cons.DOWNLOAD)) {
            new DownloadService().download(ctx, request, userKey, name, id);
            return;
        }

        //获取token
        if (uri.startsWith(Cons.GETTOKEN)) {
            BaseService.send(ctx, new TokenService().generateToken(name));
            return;
        }

        BaseService.send(ctx, new FileServerVersion().toString());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("error:{}", cause.getMessage());
        if (ctx.channel().isActive()) {
            BaseService.send(ctx, INTERNAL_SERVER_ERROR, "error:" + cause.getMessage());
        }
        ctx.close();
    }


}
