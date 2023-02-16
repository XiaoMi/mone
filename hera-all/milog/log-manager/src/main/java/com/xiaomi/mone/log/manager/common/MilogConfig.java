package com.xiaomi.mone.log.manager.common;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.google.gson.Gson;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/8 17:26
 */
@Configuration
public class MilogConfig {

    private String serverAddr = Config.ins().get("nacosAddr", "");
    private String defaultNacosAddres = Config.ins().get("defaultNacosAddres", "");

    private String nameSpace;

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService(defaultNacosAddres);
    }

    @Bean
    public NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(serverAddr);
        nacosNaming.init();
        return nacosNaming;
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }

}
