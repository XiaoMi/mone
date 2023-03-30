package com.xiaomi.mone.log.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.Test;

import static com.xiaomi.mone.log.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description dubbo泛化调用
 * @date 2022/12/21 16:08
 */
@Slf4j
public class DubboTest {

    @Test
    public void test() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("api-gateway-test");
        application.setQosEnable(false);

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("nacos://127.0.0.1:80");
        registry.setRegister(false);

        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface("com.xiaomi.mone.log.api.service.PublishConfigService");
        reference.setVersion("");
        reference.setGroup("staging");
        reference.setGeneric("true");

        reference.setApplication(application);
        reference.setRegistry(registry);

//        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
//        bootstrap.application(application)
//                .registry(registry)
//                .reference(reference)
//                .start();

        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);

        Object result = genericService.$invoke("getAllAgentList", new String[]{}, new Object[]{});
        log.info("result:{}", GSON.toJson(result));
    }
}
