package com.xiaomi.youpin.docean.plugin.test.service;


import com.xiaomi.youpin.demo.mesh.service.DemoMeshService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshReference;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service
public class HealthService {


    @Value("$nnn")
    private String nnn;

    @Value("${nnn}")
    private String nnnn;


    @org.springframework.beans.factory.annotation.Value("${nnn}")
    private String nnnnn;


    @org.springframework.beans.factory.annotation.Value("${nnn}")
    private int n;

    @org.springframework.beans.factory.annotation.Value("${kkk:4444}")
    private int k;


    @org.springframework.beans.factory.annotation.Value("${kkkk:true}")
    private boolean kk;


//    @MeshReference(interfaceClass = TeslaGatewayService.class)
//    private TeslaGatewayService apiService;


    @MeshReference(interfaceClass = DemoMeshService.class, app = "demo_app")
    private DemoMeshService dmeshService;


    @Resource
    private ITestService testService;

    public String health() {
        try {
//            return "ok:" + apiService.ping();
            return "ok:";
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String test() {
        return testService.test() + nnn + ":" + nnnn + ":" + nnnnn + ":" + n + k;
    }


    public String call() {
        return dmeshService.hi();
    }

    public Integer sum(Integer a, Integer b) {
        return dmeshService.sum(a, b);
    }


}
