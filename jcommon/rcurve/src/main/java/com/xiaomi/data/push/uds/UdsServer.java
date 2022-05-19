package com.xiaomi.data.push.uds;

import com.xiaomi.data.push.common.CommonUtils;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.common.UdsException;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.handler.UdsServerHandler;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsServer {


    @Getter
    private ConcurrentHashMap<String, UdsProcessor> processorMap = new ConcurrentHashMap<>();

    @Setter
    private boolean remote;

    @Setter
    private String host;

    @Setter
    private int port;

    public static ConcurrentHashMap<Long, CompletableFuture<UdsCommand>> reqMap = new ConcurrentHashMap<>();


    public UdsServer() {
    }

    private Class getChannelClass(boolean mac) {
        if (this.remote) {
            return mac ? KQueueServerSocketChannel.class : EpollServerSocketChannel.class;
        }
        return mac ? KQueueServerDomainSocketChannel.class : EpollServerDomainSocketChannel.class;
    }

    @SneakyThrows
    public void start(String path) {
        boolean mac = CommonUtils.isMac();
        EventLoopGroup bossGroup = mac ? new KQueueEventLoopGroup() : new EpollEventLoopGroup();
        EventLoopGroup workerGroup = mac ? new KQueueEventLoopGroup() : new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .channel(getChannelClass(mac))
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
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
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
    public UdsCommand call(UdsCommand req) {
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
        }
    }

}
