package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @author dingpei@xiaomi.com
 * @date 1/10/21
 * redis 代理的处理类
 */
@Slf4j
@Component
public class RedisEgress extends BaseEgress {

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put(cmd(), this);
    }


    @Override
    public String getBeanName(String app) {
        return "redis:" + app;
    }

    @Override
    public String cmd() {
        return "redis";
    }
}
