package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/8/21 16:56
 */
public class HttpHandlerRead {


    public static void read(ChannelHandlerContext ctx, HttpObject obj, HttpServerConfig config) {
        if (obj instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) obj;
            String uri = HttpRequestUtils.getBasePath(request);
            MvcRequest req = new MvcRequest();
            byte[] body = RequestUtils.getData(config, uri, request, (params) -> req.setParams((Map<String, String>) params));
            String method = request.method().name();
            MvcContext context = new MvcContext();
            context.setRequest(request);
            context.setMethod(method);
            context.setHandlerContext(ctx);
            context.setPath(uri);
            context.setCookie(config.isCookie());
            req.setHeaders(RequestUtils.headers(request));
            context.setHeaders(req.getHeaders());
            req.setMethod(method);
            req.setPath(uri);
            req.setBody(body);
            MvcResponse response = new MvcResponse();
            response.setCtx(ctx);
            Mvc.ins().dispatcher(context, req, response);
        }

    }

}
