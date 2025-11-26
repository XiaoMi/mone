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

package com.xiaomi.youpin.tesla.file.server.netty;

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class HttpChannelInitlalizer extends ChannelInitializer<SocketChannel> {


    private SslContext sslContext;

    private static boolean ssl = Cons.SSL;
    private static boolean limit = Cons.LIMIT;
    private static int maxContentLength = Cons.MAXCONTENTLENGTH;

    public HttpChannelInitlalizer() {
        if (ssl) {
            try {
                SelfSignedCertificate certificate = new SelfSignedCertificate("youpinfs.com");
                sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
            } catch (Throwable ex) {
                log.warn("error:{}", ex.getMessage());
            }
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (limit) {
            //流量整形的能力,可粗略的认为是是限流(读和写) 100m
            pipeline.addLast(new ChannelTrafficShapingHandler(Cons.WRITELIMIT * 1024 * 1024, Cons.READLIMIT * 1024 * 1024, 100));
        }

        if (ssl) {
            //同时支持http 和 https
            pipeline.addLast(new OptionalSslHandler(sslContext));
        }

        pipeline.addLast(new HttpServerCodec());

        pipeline.addLast(new HttpUploadHandler());
        pipeline.addLast(new HttpObjectAggregator(maxContentLength * 1024 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpChannelHandler());
    }

}
