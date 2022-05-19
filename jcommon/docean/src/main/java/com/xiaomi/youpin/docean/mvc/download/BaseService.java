package com.xiaomi.youpin.docean.mvc.download;

import com.xiaomi.youpin.docean.common.DoceanConfig;
import com.xiaomi.youpin.docean.common.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class BaseService {

    public static final String DATAPATH = DoceanConfig.ins().get("download_file_path", "/tmp");

    public static void send(ChannelHandlerContext ctx, String message) {
        send(ctx, OK, message);
    }

    public static void send(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        try {
            ByteBuf msg = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, msg);
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, msg.readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (Throwable ex) {
            log.error("send error:{}", ex.getMessage());
        }
    }


    public static boolean checkParams(ChannelHandlerContext ctx, String name, String token) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(token)) {
            BaseService.send(ctx, "error:" + name + ":bad:request");
            return false;
        }
        return true;
    }

    public static boolean checkToken(ChannelHandlerContext ctx, String name, String token, String path) {
        return true;
    }


}
