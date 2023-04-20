package com.xiaomi.mone.log.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * logstore 配置，对应一个es index
 */
@Data
@EqualsAndHashCode
public class SinkConfig {
    private Long logstoreId;
    private String logstoreName;
    /**
     * timestamp 必选
     */
    private String keyList;
    /**
     * key:logtailId
     */
    @EqualsAndHashCode.Exclude
    private List<LogtailConfig> logtailConfigs;

    private String esIndex;

    private EsInfo esInfo;

    public void updateStoreParam(SinkConfig sinkConfig) {
        this.logstoreId = sinkConfig.getLogstoreId();
        this.logstoreName = sinkConfig.getLogstoreName();
        this.keyList = sinkConfig.getKeyList();
        this.esIndex = sinkConfig.getEsIndex();
        this.esInfo = sinkConfig.getEsInfo();
    }

}