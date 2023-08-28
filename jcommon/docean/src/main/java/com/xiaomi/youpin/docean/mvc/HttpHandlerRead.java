package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;


/**
 * @author goodjava@qq.com
 * @date 2023/8/21 16:56
 */
public class HttpHandlerRead {


    public static void read(ChannelHandlerContext ctx, HttpObject httpObject, HttpServerConfig config) {
        if (httpObject instanceof FullHttpRequest request) {
            var uri = HttpRequestUtils.getBasePath(request);
            byte[] body = RequestUtils.getData(config, uri, request);
            Mvc.ins().dispatcher(config, ctx, request, uri, body);
        }
    }

}
