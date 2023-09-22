package com.xiaomi.youpin.docean.mvc.util;

import com.xiaomi.youpin.docean.mvc.MvcResponse;
import io.netty.handler.codec.http.*;

/**
 * @author goodjava@qq.com
 * @date 2023/8/17 10:18
 */
public class Jump {

    public static void jump(MvcResponse response, String location) {
        FullHttpResponse response302 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response302.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        response302.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response302.headers().set(HttpHeaderNames.LOCATION, location);
        HttpUtil.setKeepAlive(response302, true);
        response.getCtx().writeAndFlush(response302);
    }

}
