package com.xiaomi.mone.monitor.service.impl;

import com.xiaomi.mone.monitor.service.AA;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.GrafanaApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gaoxihui
 * @date 2021/7/6 1:54 下午
 */
@Slf4j
@Service(registry = "registryConfig",interfaceClass = AA.class, retries = 0, group = "${dubbo.group}",timeout = 5000)
public class AAImpl implements AA {

    @Autowired
    private AppMonitorService appMonitorService;

    @Override
    public void testA() {
        //TODO
        log.info("=================Dubbo 服务 AA被调用=================");
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
    public String testSlowQuery() throws InterruptedException {
        Thread.sleep(1001);
        return "ok";
    }

    @Override
    public void appPlatMove(Integer OProjectId, Integer OPlat, Integer NProjectId, Integer Nplat, Integer newIamId, String NprojectName) {
        appMonitorService.appPlatMove(OProjectId, OPlat, NProjectId, Nplat, newIamId, NprojectName,false);
    }

}
