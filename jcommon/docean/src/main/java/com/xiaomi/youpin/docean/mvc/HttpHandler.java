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

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.mvc.download.DownloadService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private boolean userWs = true;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (userWs && Cons.WebSocketPath.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
            return;
        }
        String uri = HttpRequestUtils.getBasePath(request);
        byte[] body = null;

        MvcRequest req = new MvcRequest();

        if (request.method().equals(HttpMethod.GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            Map<String, String> params = decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
            req.setParams(params);
            body = new Gson().toJson(params).getBytes();
        }

        if (request.method().equals(HttpMethod.POST)) {
            body = HttpRequestUtils.getRequestBody(request);
        }

        String method = request.method().name();
        MvcContext context = new MvcContext();
        context.setRequest(request);
        context.setMethod(method);
        context.setHandlerContext(ctx);
        context.setPath(uri);
        req.setHeaders(request.headers().entries().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        context.setHeaders(req.getHeaders());
        req.setMethod(method);
        req.setPath(uri);
        req.setBody(body);
        MvcResponse response = new MvcResponse();
        response.setCtx(ctx);
        Mvc.ins().dispatcher(context, req, response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("remote address:{} error:{}", ctx.channel().remoteAddress(), cause.getMessage());
        if (null != ctx.channel() && ctx.channel().isOpen() && ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }
}
