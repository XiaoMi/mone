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
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.handler.UdsServerHandler;
import com.xiaomi.data.push.uds.po.RpcServerInfo;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.Timeout;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IServer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 * <p>
 * uds server 同时也支持remote模式(还是传统的tcp,方便windows用户调试)
 */
@Slf4j
public class UdsServer implements IServer<UdsCommand> {

    private ConcurrentHashMap<String, Pair<UdsProcessor, ExecutorService>> processorMap = new ConcurrentHashMap<>();

    private ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    // 创建时间轮，可以根据需要调整tick时间和轮子大小
    private UdsWheelTimer wheelTimer = new UdsWheelTimer();


    /**
     * 是否使用remote模式(标准tcp)
     */
    @Setter
    private boolean remote;

    @Setter
    private String host;

    @Setter
    private int port;

    private String path;

    @Setter
    private String name;

    @Setter
    private boolean reg;

    private Consumer<RpcServerInfo> regConsumer;

    public static ConcurrentHashMap<Long, CompletableFuture<UdsCommand>> reqMap = new ConcurrentHashMap<>();


    public UdsServer() {
    }

    public void putProcessor(UdsProcessor processor) {
        this.processorMap.put(processor.cmd(), Pair.of(processor, ExecutorServiceUtils.creatThreadPoolHasName(processor.poolSize(), processor.cmd(), this.pool)));
    }


    @Override
    @SneakyThrows
    public void start(String path) {
        this.path = path;
        delPath();
        boolean mac = CommonUtils.isMac();
        try {
            EventLoopGroup bossGroup = NetUtils.getEventLoopGroup(this.remote);
            EventLoopGroup workerGroup = NetUtils.getEventLoopGroup(this.remote);

            ServerBootstrap serverBootstrap =
                    new ServerBootstrap().group(bossGroup, workerGroup)
                            .channel(NetUtils.getServerChannelClass(mac, this.remote))
                            .option(ChannelOption.SO_BACKLOG, 5000)
                            .option(ChannelOption.SO_RCVBUF, 65535)
                            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new UdsServerHandler(processorMap));
                }
            };

            serverBootstrap.childHandler(initializer);
            ChannelFuture future =
                    serverBootstrap.bind("0.0.0.0", this.port).addListener((ChannelFutureListener) future1 -> {
                        if (future1.isSuccess()) {
                            log.info("bind:{}", this.remote ? this.host + ":" + this.port : path);
                        }
                    }).awaitUninterruptibly();

            Throwable cause = future.cause();
            if (cause != null) {
                log.error(cause.getMessage(), cause);
            }

            if (Optional.ofNullable(this.regConsumer).isPresent()) {
                RpcServerInfo si = this.getServerInfo();
                log.info("reg server :{}", si);
                this.regConsumer.accept(si);
            }

        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public RpcServerInfo getServerInfo() {
        RpcServerInfo si = new RpcServerInfo();
        String host = this.host;
        int port = this.port;
        String name = this.name;
        boolean reg = this.reg;
        si.setHost(host);
        si.setPort(port);
        si.setName(name);
        si.setReg(reg);
        return si;
    }

    @Override
    public ConcurrentHashMap<String, UdsProcessor> getProcessorMap() {
        ConcurrentHashMap<String, UdsProcessor> res = new ConcurrentHashMap<>();
        this.processorMap.forEach((k, v) -> {
            res.put(k, v.getKey());
        });
        return res;
    }

    public void destory() {
        log.info("destory");
        delPath();
    }


    private void delPath() {
        if (null == this.path || this.path.trim().equals("")) {
            return;
        }
        SafeRun.run(() -> {
            if (Files.exists(Paths.get(this.path))) {
                Files.delete(Paths.get((this.path)));
            }
        });
    }


    /**
     * 同步发送
     *
     * @param req
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public UdsCommand call(UdsCommand req) {
        Stopwatch sw = Stopwatch.createStarted();
        TraceContext context = new TraceContext();
        context.enter();
        long id = req.getId();

        // 创建超时任务
        Timeout timeout = wheelTimer.newTimeout(() -> {
            CompletableFuture<UdsCommand> future = reqMap.remove(id);
            if (future != null && !future.isDone()) {
                future.completeExceptionally(
                        new TimeoutException("Request timeout: " + req.getTimeout())
                );
            }
        }, req.getTimeout());

        try {
            String app = req.getApp();
            CompletableFuture<UdsCommand> future = new CompletableFuture<>();

            // 添加完成时取消定时任务的回调
            future.whenComplete((k, v) -> timeout.cancel());

            reqMap.put(id, future);
            Channel channel = UdsServerContext.ins().channel(app);
            if (null == channel || !channel.isOpen()) {
                throw new UdsException("app:" + app + " channel is close");
            }
            Send.send(channel, req);
            return future.get(req.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (Throwable ex) {
            log.error("call time out:{} {} {} {} {} timeout:{}", req.getApp(), req.getCmd(), req.getServiceName(), req.getMethodName(), ex.getMessage(), req.getTimeout());
            throw new UdsException(ex);
        } finally {
            reqMap.remove(id);
            context.exit(new TraceEvent("server", sw.elapsed(TimeUnit.MILLISECONDS)));
        }
    }

    @Override
    public void setRegConsumer(Consumer<RpcServerInfo> regConsumer) {
        this.regConsumer = regConsumer;
    }
}
