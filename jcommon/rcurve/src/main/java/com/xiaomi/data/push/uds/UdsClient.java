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
import com.xiaomi.data.push.uds.WheelTimer.UdsWheelTimer;
import com.xiaomi.data.push.uds.context.TraceContext;
import com.xiaomi.data.push.uds.context.TraceEvent;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.handler.UdsClientConnetManageHandler;
import com.xiaomi.data.push.uds.handler.UdsClientHandler;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.Address;
import run.mone.api.IClient;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 * <p>
 * 只支持mac和linux
 */
@Slf4j
public class UdsClient implements IClient<UdsCommand> {

    private static UdsWheelTimer wheelTimer = new UdsWheelTimer();

    private ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

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

    public static ConcurrentHashMap<Long, HashMap<String, Object>> reqMap = new ConcurrentHashMap<>();

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
                    .option(ChannelOption.SO_BACKLOG, 5000)
                    .option(ChannelOption.SO_RCVBUF, 65535)
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
        } finally {
            log.info("client close host:{} port:{}", this.host, this.port);
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
            CompletableFuture<Object> future = new CompletableFuture<>();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("future", future);
            hashMap.put("async", req.isAsync());
            hashMap.put("returnType", req.getReturnClass());
            reqMap.put(req.getId(),hashMap);
            Channel channel = this.channel;
            if (null == channel || !channel.isOpen()) {
                log.warn("client channel is close");
                throw new UdsException("client channel is close");
            }
            log.debug("start send,id:{}", id);
            Send.send(channel, req);
            //异步还是同步
            if (req.isAsync()) {
                req.setCompletableFuture(future);
                wheelTimer.newTimeout(() -> {
                    log.warn("check async udsClient time out auto close:{},{}", req.getId(), req.getTimeout());
                    reqMap.remove(req.getId());
                }, req.getTimeout()+350);
                return req;
            }
            return (UdsCommand) future.get(req.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (Throwable ex) {
            log.error("client call:{} error:{}", req.getCmd(), ex.getMessage(), ex);
            throw new UdsException("cal error:" + ex);
        } finally {
            if (!req.isAsync()) {
                reqMap.remove(id);
            }
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
        this.processorMap.put(processor.cmd(), Pair.of(processor, ExecutorServiceUtils.creatThreadPoolHasName(processor.poolSize(), processor.cmd(), this.pool)));
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
        SafeRun.run(() -> {
            if (null != this.channel && this.channel.isOpen()) {
                log.info("close channel:{}", this.channel);
                this.channel.close();
            }
        });
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
