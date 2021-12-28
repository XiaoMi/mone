package com.xiaomi.youpin.gateway.es;

import com.xiaomi.mone.es.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingpei
 */
@Slf4j
@Configuration
public class EsConfig {

    @Value("${es.isinit}")
    private String isNeedInit;

    @Value("${es.address}")
    private String esAddress;

    @Value("${es.user}")
    private String esUser;

    @Value("${es.pwd}")
    private String esPwd;

    @Bean
    public EsClient esClient() {
        if ("true".equals(isNeedInit)) {
            log.info("init es");
            EsClient esClient = new EsClient(esAddress, esUser, esPwd);
            return esClient;
        } else {
            return null;
        }
    }
}
