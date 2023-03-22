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
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.handler.UdsServerHandler;
import com.xiaomi.data.push.uds.po.RpcServerInfo;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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

    private ExecutorService pool = new ThreadPoolExecutor(200, 200,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100));

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
        this.processorMap.put(processor.cmd(), Pair.of(processor, ExecutorServiceUtils.creatThreadPool(processor.poolSize(), this.pool)));
    }


    @Override
    @SneakyThrows
    public void start(String path) {
        this.path = path;
        delPath();
        boolean mac = CommonUtils.isMac();
        EventLoopGroup bossGroup = NetUtils.getEventLoopGroup();
        EventLoopGroup workerGroup = NetUtils.getEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .channel(NetUtils.getServerChannelClass(mac, this.remote))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new UdsServerHandler(processorMap));
                        }
                    });

            SocketAddress s = this.remote ? new InetSocketAddress(this.host, this.port) : new DomainSocketAddress(path);
            ChannelFuture f = b.bind(s);
            log.info("bind:{}", this.remote ? this.host + ":" + this.port : path);

            if (Optional.ofNullable(this.regConsumer).isPresent()) {
                RpcServerInfo si = this.getServerInfo();
                log.info("reg server :{}", si);
                this.regConsumer.accept(si);
            }

            f.channel().closeFuture().sync();
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
        try {
            String app = req.getApp();
            CompletableFuture<UdsCommand> future = new CompletableFuture<>();
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
