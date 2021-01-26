package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/10/21
 * mysql 对外访问
 */
@Slf4j
@Component
public class MysqlEgress extends BaseEgress {

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put("mysql", this);
    }


    @Override
    public String getBeanName(String app) {
        return "dao:" + app;
    }
}
