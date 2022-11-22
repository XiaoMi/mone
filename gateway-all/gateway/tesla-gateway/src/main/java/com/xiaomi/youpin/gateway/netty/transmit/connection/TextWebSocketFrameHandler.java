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

import com.google.gson.Gson;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.Msg;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.filter.RequestContext;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.plugin.bo.Message;
import com.youpin.xiaomi.tesla.plugin.bo.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 支持长连接
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);


    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, User>> group = new ConcurrentHashMap<>();


    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Command(), 10, 10, TimeUnit.SECONDS);
    }


    static class Command implements Runnable {

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            try {
                Collection<ConcurrentHashMap<String, User>> values = group.values();
                values.forEach(it -> {
                    final ConcurrentHashMap<String, User> map = it;
                    List<User> timeOutUsers = map.values().stream().filter(u -> now - u.getLastUpdateTime().get() >= TimeUnit.SECONDS.toMillis(30)).collect(Collectors.toList());

                    //清理掉过期的user
                    timeOutUsers.stream().forEach(u -> {
                        logger.info("remove user:{} {}", u.getName(), u.getId());
                        map.remove(u.getId());

                        //通知插件
                        String url = u.getUrl();
                        if (StringUtils.isNotEmpty(url)) {
                            //封装成Http调用 (通知插件用户下线)
                            if (null != u.getLogoutConsumer()) {
                                u.getLogoutConsumer().accept("");
                            }
                        }
                    });
                });

            } catch (Exception ex) {
                //ignore
                logger.error("remove user error:{}", ex.getMessage());
            }
        }
    }


    private final Dispatcher dispatcher;
    private final RequestFilterChain filterChain;
    private final ApiRouteCache apiRouteCache;


    public TextWebSocketFrameHandler(Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache) {
        this.dispatcher = dispatcher;
        this.filterChain = filterChain;
        this.apiRouteCache = apiRouteCache;


    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //移除http的操作
            ctx.pipeline().remove(HttpHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String uri = "";
        try {
            Gson gson = new Gson();
            Map req = gson.fromJson(msg.text(), Map.class);
            Object uriObj = req.get("uri");
            if (null == uriObj) {
                logger.warn("{} req uri is null",ctx.channel().remoteAddress());
                return;
            }
            uri = uriObj.toString();
            logger.debug("uri:{} req:{}", uri, req);
        } catch (Exception ex) {
            TextWebSocketFrame frame = new TextWebSocketFrame(new Gson().toJson(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, ex.getMessage()+" ,msg:"+msg.text())));
            ctx.writeAndFlush(frame);
            return;
        }

        //封装成Http调用
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, Unpooled.wrappedBuffer(msg.text().getBytes()));

        //会从路由里获取路由信息
        ApiInfo apiInfo = apiRouteCache.get(uri);

        if (!Optional.ofNullable(apiInfo).isPresent()) {
            TextWebSocketFrame frame = new TextWebSocketFrame(new Gson().toJson(Result.fail(GeneralCodes.NotFound, uri+" not found server:"+ctx.channel().localAddress()+" client:"+ctx.channel().remoteAddress())));
            ctx.writeAndFlush(frame);
            return;
        }

        //这里会异步处理
        dispatcher.dispatcher(dispatcherFunction(ctx.channel(), request, apiInfo), (res) -> {
        }, apiInfo);
    }


    private User getUser(String groupName, String uid, String channelId) {
        ConcurrentHashMap<String, User> map = group.get(groupName);
        if (null == map) {
            return new User("0", "", "", channelId);
        }
        return map.get(uid);
    }

    private Function<String, Object> dispatcherFunction(Channel channel, FullHttpRequest request, ApiInfo apiInfo) {
        return (str) -> {
            RequestContext context = new RequestContext();

            //注册用户
            context.setRegConsumer(regUserConsumer(channel, apiInfo.getUrl()));

            //发送消息
            context.setSendConsumer(sendConsumer());

            //获取组信息
            context.setGroupFunction(groupName -> group.get(groupName));

            //私有ping协议
            context.setPingConsumer(pingConsumer());

            Attribute<ChannelUser> attr = channel.attr(AttributeKeys.playerKey);
            if (null != attr.get()) {
                String id = attr.get().getUid();
                String group = attr.get().getGroup();
                context.setUser(getUser(group, id, channel.id().toString()));
            } else {
                context.setUser(new User("0", "", "", channel.id().toString()));
            }

            logger.debug("user:{}", context.getUser());

            FullHttpResponse res = filterChain.doFilter(apiInfo, request, context);
            String content = HttpResponseUtils.getContent(res);
            //封装成websocket的结果
            TextWebSocketFrame frame = new TextWebSocketFrame(content);
            channel.writeAndFlush(frame);
            return "";
        };
    }

    private Consumer<Message> sendConsumer() {
        return message -> {
            String groupStr = message.getGroup();
            String receiverId = message.getReceiverId();

            ConcurrentHashMap<String, User> m = group.get(groupStr);
            if (null != m) {
                //发给个人
                if (StringUtils.isNotEmpty(receiverId)) {
                    User u = m.get(receiverId);
                    if (null != u) {
                        u.send(message);
                    }
                } else {
                    //发给整个分组
                    m.values().forEach(it -> it.send(message));
                }

            }
        };
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        Attribute<ChannelUser> attr = ctx.channel().attr(AttributeKeys.playerKey);
        if (null != attr) {
            ChannelUser user = attr.get();
            if (null == user) {
                return;
            }
            String groupStr = user.getGroup();
            ConcurrentHashMap<String, User> m = group.get(groupStr);
            if (null != m) {
                m.remove(user.getUid());
                logger.info("channelInactive:{} uid:{}", ctx.channel().id(), user.getUid());
                String url = user.getUrl();
                if (StringUtils.isNotEmpty(url)) {
                    //封装成Http调用 (通知插件用户下线)
                    notifyPluginLogout(ctx.channel(), user.getUid(), url);
                }
            }
        }
    }

    private void notifyPluginLogout(Channel channel, String uid, String url) {
        Map<String, Object> msg = new HashMap<>(2);
        msg.put("cmd", "logout");
        msg.put("id", uid);

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, Unpooled.wrappedBuffer(new Gson().toJson(msg).getBytes()));
        ApiInfo apiInfo = apiRouteCache.get(url);
        //这里会异步处理
        dispatcher.dispatcher(dispatcherFunction(channel, request, apiInfo), (res) -> {
        }, apiInfo);
    }


    /**
     * 注册(登陆)
     *
     * @return
     */
    private Consumer<User> regUserConsumer(final Channel channel, String url) {
        return user -> {
            //发送消息
            user.setMessageConsumer(msg -> {
                channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(msg)));
            });

            //用户下线的通知
            user.setLogoutConsumer(msg -> {
                try {
                    notifyPluginLogout(channel, user.getId(), url);
                } catch (Throwable ex) {
                    //ignore
                }
            });

            channel.attr(AttributeKeys.playerKey).set(new ChannelUser(user.getGroup(), user.getId(), channel.id().toString(), System.currentTimeMillis(), user.getUrl()));
            final String cid = channel.id().toString();
            user.setChannelId(cid);
            user.setChannel(channel);
            String groupName = user.getGroup();
            group.compute(groupName, (k, us) -> {
                if (null == us) {
                    us = new ConcurrentHashMap<>(1000);
                    us.put(user.getId(), user);
                    return us;
                }
                us.put(user.getId(), user);
                return us;
            });
        };
    }


    /**
     * ping的回调
     * 只有登录的会有实际影响
     *
     * @return
     */
    private Consumer<User> pingConsumer() {
        return user -> {
            if (null != user && !user.getId().equals("0")) {
                String groupStr = user.getGroup();
                String uid = user.getId();
                User u = getUser(groupStr, uid, user.getChannelId());
                if (null != u) {
                    u.getLastUpdateTime().set(user.getLastUpdateTime().get());
                }
            }
        };
    }
}
