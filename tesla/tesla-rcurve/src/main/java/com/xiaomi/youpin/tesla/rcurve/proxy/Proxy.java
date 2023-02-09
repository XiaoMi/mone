package com.xiaomi.youpin.tesla.rcurve.proxy;

import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
public interface Proxy<Req extends Serializable, Res extends Serializable> {

    Res execute(ProxyContext context, Req request);

    String type();

    default String version() {
        return "0.0.1:2021-01-09";
    }
}
