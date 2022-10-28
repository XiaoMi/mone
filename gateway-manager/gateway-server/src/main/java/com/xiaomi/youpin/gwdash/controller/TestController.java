package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.SimplePipleEnvBo;
import com.xiaomi.youpin.gwdash.service.ApiGroupInfoService;
import com.xiaomi.youpin.gwdash.service.MilogProviderService;
import com.xiaomi.youpin.gwdash.service.TeslaGatewayServiceGroup;
import com.youpin.xiaomi.tesla.bo.Ping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author goodjava@qq.com
 */
@RestController
@Slf4j
public class TestController {

    @Resource
    private TeslaGatewayServiceGroup serviceGroup;


    /**
     * 测试dubbo 调用(泛化,动态提供group)
     * @return
     */
    @GetMapping("/test/dubbo/call")
    public String test() {
        Ping ping = new Ping();
        ping.setData("ping");
        ping.setTime(System.currentTimeMillis());
        Object res = null;
        try {
            res = serviceGroup.callDubbo(ping, "com.youpin.xiaomi.tesla.service.TeslaGatewayService", "ping", "1", "", new String[]{
                    "com.youpin.xiaomi.tesla.bo.Ping"
            },"");
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return "ok:" + res;
    }

}
