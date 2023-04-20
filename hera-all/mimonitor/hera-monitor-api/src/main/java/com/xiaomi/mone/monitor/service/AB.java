package com.xiaomi.mone.monitor.service;

/**
 * @author gaoxihui
 * @date 2021/7/6 1:49 下午
 */
public interface AB {
    void testA();

    public String testError() throws Exception;

    public String testSlowQuery(com.xiaomi.youpin.dubbo.request.RequestContext requestContext)  throws InterruptedException;
}
