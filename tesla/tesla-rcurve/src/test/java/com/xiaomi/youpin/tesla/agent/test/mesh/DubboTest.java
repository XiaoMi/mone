package com.xiaomi.youpin.tesla.agent.test.mesh;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.proxy.MeshRequest;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.proxy.MeshService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
public class DubboTest {

    @Before
    public void setUp() {
        Ioc.ins().init("com.xiaomi.youpin");
    }

    @Test
    public void testCall() {
        ApplicationConfig appConfig = Ioc.ins().getBean(ApplicationConfig.class);
        RegistryConfig registryConfig = Ioc.ins().getBean(RegistryConfig.class);
        ReferenceConfig<MeshService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(appConfig);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setInterface(MeshService.class);
        MeshService service = referenceConfig.get();
        MeshRequest request = new MeshRequest();
        request.setMethodName("$version$");
        MeshResponse res = service.invoke(request);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void testGson() {
        String a = "[\"a\",\"b\"]";
        String[] members = new Gson().fromJson(a, new TypeToken<String[]>(){}.getType());
        System.out.println(members);
    }
}
