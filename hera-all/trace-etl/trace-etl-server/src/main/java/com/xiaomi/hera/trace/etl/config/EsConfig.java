package com.xiaomi.hera.trace.etl.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.es.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingpei
 */
@Configuration
@Slf4j
public class EsConfig {

    @NacosValue("${es.trace.address}")
    private String traceAddress;
    @NacosValue("${es.trace.username}")
    private String traceUserName;
    @NacosValue("${es.trace.password}")
    private String tracePassword;

    @Bean(name = "errorEsClient")
    public EsClient esClient() {
        try {
            EsClient esClient = new EsClient(traceAddress, traceUserName, tracePassword);
            log.info("init error message es");
            return esClient;
        } catch (Exception e) {
            log.error("init es error : ", e);
        }
        return null;
    }

    @Bean(name = "jaegerEsClient")
    public EsClient jaegerEsClient() {
        try {
            EsClient esClient = new EsClient(traceAddress, traceUserName, tracePassword);
            log.info("init jaeger es");
            return esClient;
        } catch (Exception e) {
            log.error("init es error : ", e);
        }
        return null;
    }

}
