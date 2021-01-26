package com.xiaomi.youpin.tesla.rcurve.proxy.controller;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.tesla.rcurve.proxy.ProxyRequest;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.HttpIngress;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * proxy http 的控制器
 */
@Controller
public class HttpProxyController {

    @Resource
    private HttpIngress httpIngress;

    @RequestMapping(path = "/proxy")
    public FullHttpResponse test(MvcContext ctx, ProxyRequest req) {
        ProxyContext context = new ProxyContext();
        context.setMethod(ctx.getMethod());
        context.setHeaders(ctx.getHeaders());
        context.setType(ProxyType.http);
        MeshResponse res = httpIngress.execute(context, req);
        return HttpResponseUtils.create(new Gson().toJson(res));
    }

}
