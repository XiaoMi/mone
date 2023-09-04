package com.xiaomi.hera.trace.etl.es.config;

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.es.ProcessorConf;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class EsProcessConfig {

    private static final Logger log = LoggerFactory.getLogger(EsProcessConfig.class);

    @Resource(name = "jaegerEsClient")
    private EsClient esClient;

    @Value("${es.bulk_actions}")
    private int bulkActions;
    @Value("${es.byte_size}")
    private int byteSize;
    @Value("${es.concurrent_request}")
    private int concurRequest;
    @Value("${es.flush_interval}")
    private int flushInterval;
    @Value("${es.retry_num}")
    private int retryNum;
    @Value("${es.retry_interval}")
    private int retryInterval;

    @Bean
    public EsProcessor esProcessor() {
        return new EsProcessor(new ProcessorConf(bulkActions, byteSize, concurRequest, flushInterval, retryNum, retryInterval, esClient, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
//                            log.info("before send to es,desc:{}", request.getDescription());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                log.info("success send to es,desc:{}", request.getDescription());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("fail send {} message to es,desc:{},failure:{}", request.numberOfActions(), request.getDescription(), failure.getMessage());
            }
        }));
    }

    /**
     * 当前es工程并未用到
     * @return
     */
    @Bean("errorEsProcessor")
    public EsProcessor errorEsProcessor() {
        return new EsProcessor(new ProcessorConf(bulkActions, byteSize, concurRequest, flushInterval, retryNum, retryInterval, esClient, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
//                            log.info("before send to es,desc:{}", request.getDescription());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                log.info("success send to es,desc:{}", request.getDescription());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("fail send {} error trace message to es,desc:{},failure:{}", request.numberOfActions(), request.getDescription(), failure);
            }
        }));
    }
}
