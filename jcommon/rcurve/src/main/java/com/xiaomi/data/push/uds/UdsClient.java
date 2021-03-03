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

package com.xiaomi.data.push.uds;

import com.xiaomi.data.push.common.CommonUtils;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.common.UdsException;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.handler.UdsClientConnetManageHandler;
import com.xiaomi.data.push.uds.handler.UdsClientHandler;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 * <p>
 * 只支持mac和linux
 */
@Slf4j
public class UdsClient {

    @Getter
    private volatile Channel channel;

    private final String id;

    @Getter
    private final ConcurrentHashMap<String, UdsProcessor> processorMap = new ConcurrentHashMap<>();

    @Setter
    private boolean remote;

    @Setter
    private String host;

    @Setter
    private int port;

    public static ConcurrentHashMap<Long, CompletableFuture<UdsCommand>> reqMap = new ConcurrentHashMap<>();

    public UdsClient(String id) {
        log.info("id:{}", id);
        this.id = id;
    }

    private Class getChannelClass(boolean mac) {
        if (this.remote) {
            if (CommonUtils.isWindows()) {
                return NioSocketChannel.class;
            }

            return mac ? KQueueSocketChannel.class : EpollSocketChannel.class;
        }
        return mac ? KQueueDomainSocketChannel.class : EpollDomainSocketChannel.class;
    }


    private EventLoopGroup getEventLoopGroup() {
        if (CommonUtils.isWindows()) {
            return new NioEventLoopGroup();
        }
        return CommonUtils.isMac() ? new KQueueEventLoopGroup() : new EpollEventLoopGroup();
    }


    public void start(String path) {
        boolean mac = CommonUtils.isMac();
        log.info(" start mac:{}", mac);
        EventLoopGroup group = null;
        try {
            group = getEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(getChannelClass(mac))
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new UdsClientConnetManageHandler(true, UdsClient.this, path));
                            ch.pipeline().addLast(new UdsClientHandler(processorMap));
                        }
                    });
            ChannelFuture f = b.connect(remote ? new InetSocketAddress(this.host, this.port) : new DomainSocketAddress(path));
            this.channel = f.sync().channel();
        } catch (Throwable ex) {
            UdsClientContext.ins().exceptionCaught(ex);
            log.error("start error:{}", ex.getMessage());
            if (null != group) {
                group.shutdownGracefully();
            }
            CommonUtils.sleep(2);
            start(path);
        }
    }


    public void call(Object msg) {
        UdsCommand command = UdsCommand.createRequest();
        command.setObj(msg);
        Send.send(this.channel, command);
    }


    public UdsCommand call(UdsCommand req) {
        long id = req.getId();
        try {
            CompletableFuture<UdsCommand> future = new CompletableFuture<>();
            reqMap.put(req.getId(), future);
            Channel channel = UdsClientContext.ins().channel.get();
            if (null == channel || !channel.isOpen()) {
                log.warn("server channel is close");
                throw new UdsException("server channel is close");
            }
            Send.send(channel, req);
            return future.get(req.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (Throwable ex) {
            log.error("client call:{} error:{}", req.getCmd(), ex.getMessage());
            throw new UdsException("cal:" + ex);
        } finally {
            reqMap.remove(id);
        }
    }

    public void oneWay(UdsCommand req) {
        try {
            req.setOneway(true);
            Channel channel = UdsClientContext.ins().channel.get();
            Send.send(channel, req);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new UdsException("one way:" + ex);
        }
    }

}
