package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingpei@xiaomi.com
 * @modify goodjava@qq.com
 * @date 1/11/21
 * nacos 对外访问
 */
@Slf4j
@Component
public class NacosEgress extends BaseEgress{


    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put(cmd(), this);
    }

    @Override
    public String cmd() {
        return "nacos";
    }


    @Override
    public String getBeanName(String app) {
        return NacosConfig.class.getName();
    }
}
