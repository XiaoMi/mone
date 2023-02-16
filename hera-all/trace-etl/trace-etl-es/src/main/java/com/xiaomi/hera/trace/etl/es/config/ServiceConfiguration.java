package com.xiaomi.hera.trace.etl.es.config;

import com.xiaomi.hera.trace.etl.mapper.HeraTraceEtlConfigMapper;
import com.xiaomi.hera.trace.etl.service.ManagerService;
import com.xiaomi.hera.trace.etl.service.WriteEsService;
import com.xiaomi.hera.trace.etl.util.es.EsTraceUtil;
import com.xiaomi.mone.es.EsProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/28 10:32 上午
 */
@Configuration
public class ServiceConfiguration {

    @Autowired
    private HeraTraceEtlConfigMapper heraTraceEtlConfigMapper;

    @Autowired
    private EsProcessor esProcessor;

    @Resource(name = "errorEsProcessor")
    private EsProcessor errorEsProcessor;

    @Bean
    public ManagerService managerService(){
        return new ManagerService(heraTraceEtlConfigMapper);
    }

    @Bean
    public WriteEsService writeEsService(){
        return new WriteEsService(new EsTraceUtil(esProcessor, errorEsProcessor));
    }
}
