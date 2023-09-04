package com.xiaomi.hera.trace.etl.manager.config;

import com.xiaomi.hera.trace.etl.api.service.TraceEtlService;
import com.xiaomi.hera.trace.etl.mapper.HeraTraceEtlConfigMapper;
import com.xiaomi.hera.trace.etl.service.ManagerService;
import com.xiaomi.hera.trace.etl.service.QueryEsService;
import com.xiaomi.mone.es.EsClient;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    private EsClient esClient;

    @Reference(cluster = "broadcast", interfaceClass = TraceEtlService.class, group = "${dubbo.group}", check = false)
    private TraceEtlService traceEtlService;

    @Bean
    public ManagerService managerService(){
        return new ManagerService(heraTraceEtlConfigMapper,traceEtlService);
    }

    @Bean
    public QueryEsService queryEsService(){
        return new QueryEsService(esClient);
    }
}
