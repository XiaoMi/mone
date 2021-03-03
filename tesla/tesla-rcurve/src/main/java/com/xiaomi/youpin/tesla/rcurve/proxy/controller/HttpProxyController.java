package com.xiaomi.youpin.tesla.rcurve.proxy.controller;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcResult;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.ProxyRequest;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.HttpIngress;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * proxy http 的控制器
 */
@Slf4j
@Controller
public class HttpProxyController {

    @Resource
    private HttpIngress httpIngress;

    @Value("$use_actor")
    private String useActor;


    @RequestMapping(path = "/proxy")
    public Object proxy(MvcContext ctx, ProxyRequest req) {
        return execute(ctx, req);
    }

    public Object execute(MvcContext ctx, ProxyRequest req) {
        ProxyContext context = new ProxyContext();
        context.setMethod(ctx.getMethod());
        context.setHeaders(ctx.getHeaders());
        context.setHandlerContext(ctx.getHandlerContext());
        context.setType(ProxyType.http);
        MeshResponse res = httpIngress.execute0(context, req);
        if (res.getCode() == -999) {
            MvcResult result = new MvcResult<>();
            result.setCode(-999);
            return result;
        }
        return HttpResponseUtils.create(new Gson().toJson(res));
    }

}
