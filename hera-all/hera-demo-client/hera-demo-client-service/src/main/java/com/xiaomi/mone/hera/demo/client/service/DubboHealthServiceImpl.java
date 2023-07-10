package com.xiaomi.mone.hera.demo.client.service;

import com.xiaomi.hera.trace.annotation.Trace;
import com.xiaomi.mone.hera.demo.client.api.service.DubboHealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

@Service(timeout = 3000, group = "${dubbo.group}")
@Slf4j
public class DubboHealthServiceImpl implements DubboHealthService {

    @Reference(group = "${dubbo.group}",version = "1.0",timeout = 3000,retries = 0,check = false)
    private com.xiaomi.youpin.zxw_test2.api.service.DubboHealthService dubboHealthService;

    @Override
    public int remoteHealth(int size) {
        try {
            dubboHealthService.simple(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public void remoteHealth2() {
        try {
            testMethod();
            dubboHealthService.health();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Trace
    private void testMethod(){
        testMethod2();
    }

    private void testMethod2() {

    }


}
