package com.xiaomi.youpin.docean.plugin.test.service;


import com.xiaomi.youpin.demo.mesh.service.DemoMeshService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshReference;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service
public class HealthService {


    @MeshReference(interfaceClass = TeslaGatewayService.class)
    private TeslaGatewayService apiService;


    @MeshReference(interfaceClass = DemoMeshService.class, app = "demo_app")
    private DemoMeshService dmeshService;

    public String health() {
        try {
            return "ok:" + apiService.ping();
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }


    public String call() {
        return dmeshService.hi();
    }

    public Integer sum(Integer a, Integer b) {
        return dmeshService.sum(a, b);
    }


}
