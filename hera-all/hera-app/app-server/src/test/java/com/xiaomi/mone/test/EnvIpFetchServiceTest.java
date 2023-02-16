package com.xiaomi.mone.test;

import com.google.gson.Gson;
import com.xiaomi.mone.app.AppBootstrap;
import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.service.env.DefaultNacosEnvIpFetch;
import com.xiaomi.mone.app.service.impl.HeraAppEnvServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/2/14 18:29
 */
@Slf4j
@SpringBootTest(classes = AppBootstrap.class)
public class EnvIpFetchServiceTest {

    @Autowired
    private DefaultNacosEnvIpFetch defaultNacosEnvIpFetch;

    @Autowired
    private HeraAppEnvServiceImpl heraAppEnvService;

    @Autowired
    private Gson gson;

    @Test
    public void fetchTest() throws Exception {
//        Long appBaseId = ;
//        Long appId = ;
//        String appName = "";
//        HeraAppEnvVo heraAppEnvVo = defaultNacosEnvIpFetch.fetch(appBaseId, appId, appName);
//        log.info("result:{}", gson.toJson(heraAppEnvVo));
    }

    @Test
    public void fetchIpsOpByAppTest() throws Exception {
//        Integer id = ;
//        String bindId = "";
//        String appName = "";
//        heraAppEnvService.handleAppEnv(id, bindId, appName);
    }
}
