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

package com.xiaomi.data.push.udp;

import com.xiaomi.data.push.udp.handler.UdpServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdpServer {

    private String host;
    private int port;
    private Function<String, String> function;

    public UdpServer(String host, int port, Function<String, String> function) {
        this.host = host;
        this.port = port;
        this.function = function;
    }

    private Channel channel;

    private NioEventLoopGroup acceptGroup;


    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        acceptGroup = new NioEventLoopGroup();
        bootstrap.group(acceptGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new UdpServerHandler(function));
                    }
                });

        channel = bootstrap.bind(host, port).sync().channel();
        log.info("UdpServer start success port:{}", port);
    }

    public void await() throws InterruptedException {
        channel.closeFuture().await();
    }

    public void shutdown() {
        acceptGroup.shutdownGracefully();
    }

}
