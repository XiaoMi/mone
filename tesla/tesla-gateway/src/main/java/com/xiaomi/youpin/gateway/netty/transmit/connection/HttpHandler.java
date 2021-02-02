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

package com.xiaomi.youpin.gateway.netty.transmit.connection;

import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.filter.RequestContext;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;


/**
 * @author goodjava@qq.com
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    private static final ConcurrentHashMap<String, Future> futureMap = new ConcurrentHashMap<>();

    private final Dispatcher dispatcher;
    private final RequestFilterChain filterChain;
    private final ApiRouteCache apiRouteCache;

    private final static boolean userWs = true;

    public static void closeFuture() {
        futureMap.forEachValue(2, v -> {
            try {
                v.cancel(true);
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
            }
        });
        futureMap.clear();
    }

    public static int futureNum() {
        return futureMap.size();
    }

    public HttpHandler(Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache) {
        //不自动回收msg
        super(false);
        this.dispatcher = dispatcher;
        this.filterChain = filterChain;
        this.apiRouteCache = apiRouteCache;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        //长连接的支持
        if (userWs && TeslaConstants.WebSocketPath.equalsIgnoreCase(request.uri())) {
            logger.info("http handler websocket");
            ctx.fireChannelRead(request);
        } else {
            try {
                if (checkDecoderResult(ctx, request)) {
                    logger.warn("check decoder result error:{}", request.refCnt());
                    ReferenceCountUtil.release(request);
                    return;
                }

                String uri = HttpRequestUtils.getBasePath(request);
                //会从路由里获取路由信息
                ApiInfo apiInfo = apiRouteCache.get(uri);
                logger.debug("call uri:{}", uri);
                if (apiInfo == null) {
                    logger.info("HttpHandler.nullapiinfo, uri is: {}, request header is: {}", uri, request.headers());
                }

                final long begin = System.currentTimeMillis();
                String callId = TraceId.uuid();
                //这里会异步处理
                dispatcher.dispatcher((str) -> {
                    try {
                        RequestContext context = new RequestContext();
                        context.setBegin(begin);
                        context.setIp(Utils.getClientIp(request, ctx.channel()));
                        context.setChannel(ctx.channel());
                        HttpResponse res = filterChain.doFilter(apiInfo, request, context);
                        return res;
                    } catch (Throwable ex) {
                        logger.error("HttpHandler error:" + ex.getMessage(), ex);
                        return ex.getMessage();
                    }
                }, (response) -> {
                    TeslaSafeRun.run(() -> {
                        logger.debug("release:{} {}", uri, request.refCnt());
                        //自己手动释放
                        if (request.refCnt() > 1) {
                            logger.info("HttpHandler release refCnt > 1 :{}", request.refCnt());
                            ReferenceCountUtil.release(request, request.refCnt());
                        } else {
                            ReferenceCountUtil.release(request);
                        }
                    });
                    futureMap.remove(callId);
                    if (null == response) {
                        logger.warn("http hander res = null id:{}", apiInfo.getId());
                    } else if (response instanceof String && StringUtils.isNotEmpty(response.toString())) {
                        ctx.writeAndFlush(HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, response.toString())));
                    } else {
                        ctx.writeAndFlush(response);
                    }
                }, apiInfo, f -> futureMap.put(callId, f));
            } catch (Throwable ex) {
                logger.info("channelRead0 error:{}", ex.getMessage());
                TeslaSafeRun.run(() -> {
                    ReferenceCountUtil.release(request);
                });
            }

        }
    }

    private boolean checkDecoderResult(ChannelHandlerContext ctx, FullHttpRequest request) {
        DecoderResult dr = request.decoderResult();
        if (dr.isFailure()) {
            ctx.writeAndFlush(HttpResponseUtils.create(Result.fail(GeneralCodes.ParamError, Msg.msgFor400, dr.cause().getMessage())));
            return true;
        }
        return false;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("remote address:{} error:{}", ctx.channel().remoteAddress(), cause.getMessage());
        if (null != ctx.channel()) {
            ctx.channel().close();
        }
    }
}
