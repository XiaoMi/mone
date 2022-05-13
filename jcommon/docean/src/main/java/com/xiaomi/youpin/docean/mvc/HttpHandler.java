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

import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


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
        MvcRequest req = new MvcRequest();
        byte[] body = RequestUtils.getData(request, (params) -> req.setParams((Map<String, String>) params));
        String method = request.method().name();
        MvcContext context = new MvcContext();
        context.setRequest(request);
        context.setMethod(method);
        context.setHandlerContext(ctx);
        context.setPath(uri);
        req.setHeaders(RequestUtils.headers(request));
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
