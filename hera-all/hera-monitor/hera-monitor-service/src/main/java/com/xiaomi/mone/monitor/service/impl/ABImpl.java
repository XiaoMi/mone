package com.xiaomi.mone.monitor.service.impl;

import com.xiaomi.mone.monitor.service.AB;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/7/6 1:54 下午
 */
@Slf4j
@Service(registry = "registryConfig",interfaceClass = AB.class, retries = 0, group = "${dubbo.group}",timeout = 5000)
public class ABImpl implements AB {
    @Override
    public void testA() {
        //TODO
        log.info("=================Dubbo 服务 AB被调用=================");
    }

    @Override
    public String testError() throws Exception {
        try{
            return "ok";
        }finally {
            throw new Exception("test");
        }

    }

    @Override
    public String testSlowQuery(com.xiaomi.youpin.dubbo.request.RequestContext requestContext) throws InterruptedException {
        Map<String, String> headers = requestContext.getHeaders();
        log.info("headers============" + headers);
        Thread.sleep(1001);
        return "ok";
    }
}
