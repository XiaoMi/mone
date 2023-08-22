package com.xiaomi.youpin.docean.mvc.http2;

import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.HttpHandlerRead;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.*;
import io.netty.util.AsciiString;

/**
 * @author goodjava@qq.com
 * @date 2023/8/22 14:26
 */
public class Http2UpgradeCodecFactory implements HttpServerUpgradeHandler.UpgradeCodecFactory {

    private HttpServerConfig config;

    public Http2UpgradeCodecFactory(HttpServerConfig config) {
        this.config = config;
    }

    @Override
    public HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence protocol) {
        if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
            return new Http2ServerUpgradeCodec(
                    Http2FrameCodecBuilder.forServer().build(),
                    new Http2MultiplexHandler(new Http2MultiplexHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new Http2StreamFrameToHttpObjectCodec(true));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
                                    HttpHandlerRead.read(ctx, msg, config);
                                }
                            });
                        }
                    })));
        } else {
            return null;
        }
    }
}
