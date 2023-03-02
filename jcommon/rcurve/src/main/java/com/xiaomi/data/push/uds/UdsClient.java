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

import com.google.common.base.Stopwatch;
import com.xiaomi.data.push.common.*;
import com.xiaomi.data.push.uds.context.TraceContext;
import com.xiaomi.data.push.uds.context.TraceEvent;
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
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.Address;
import run.mone.api.IClient;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 * <p>
 * 只支持mac和linux
 */
@Slf4j
public class UdsClient implements IClient<UdsCommand> {

    private ExecutorService pool = new ThreadPoolExecutor(200, 200, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));

    @Getter
    private volatile Channel channel;

    private volatile boolean shutdown;

    private final String id;

    private final ConcurrentHashMap<String, Pair<UdsProcessor<UdsCommand, UdsCommand>, ExecutorService>> processorMap = new ConcurrentHashMap<>();

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


    private EventLoopGroup getEventLoopGroup() {
        return NetUtils.getEventLoopGroup();
    }


    @Override
    public void start(String path) {
        boolean mac = CommonUtils.isMac();
        log.info("start client system is mac:{} host:{} port:{} remote:{}", mac, this.host, this.port, remote);
        EventLoopGroup group = null;
        try {
            group = getEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NetUtils.getClientChannelClass(mac, this.remote))
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
            this.channel = f.channel();
            f.sync();
        } catch (Throwable ex) {
            UdsClientContext.ins().exceptionCaught(ex);
            log.error("start error:{} restart", ex.getMessage());
            if (null != group) {
                group.shutdownGracefully();
            }
            if (shutdown) {
                return;
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

    @Override
    public UdsCommand call(UdsCommand req) {
        Stopwatch sw = Stopwatch.createStarted();
        TraceContext context = new TraceContext();
        context.enter();
        long id = req.getId();
        try {
            CompletableFuture<UdsCommand> future = new CompletableFuture<>();
            reqMap.put(req.getId(), future);
            Channel channel = this.channel;
            if (null == channel || !channel.isOpen()) {
                log.warn("client channel is close");
                throw new UdsException("client channel is close");
            }
            Send.send(channel, req);
            return future.get(req.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (Throwable ex) {
            log.error("client call:{} error:{}", req.getCmd(), ex.getMessage());
            throw new UdsException("cal error:" + ex);
        } finally {
            reqMap.remove(id);
            context.exit(new TraceEvent("client", sw.elapsed(TimeUnit.MILLISECONDS)));
        }
    }

    @Override
    public ConcurrentHashMap<String, UdsProcessor<UdsCommand, UdsCommand>> getProcessorMap() {
        ConcurrentHashMap<String, UdsProcessor<UdsCommand, UdsCommand>> res = new ConcurrentHashMap<>();
        this.processorMap.forEach((k, v) -> {
            res.put(k, v.getKey());
        });
        return res;
    }

    public void putProcessor(UdsProcessor processor) {
        this.processorMap.put(processor.cmd(), Pair.of(processor, ExecutorServiceUtils.creatThreadPool(processor.poolSize(), this.pool)));
    }

    public void oneWay(UdsCommand req) {
        try {
            req.setOneway(true);
            Channel channel = UdsClientContext.ins().channel.get();
            Send.send(channel, req);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            throw new UdsException("one way error:" + ex);
        }
    }

    @Override
    public void shutdown() {
        log.info("client shutdown:{}", this.channel);
        this.shutdown = true;
    }

    @Override
    public Address address() {
        Address address = new Address();
        address.setIp(this.host);
        address.setPort(this.port);
        return address;
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }
}
