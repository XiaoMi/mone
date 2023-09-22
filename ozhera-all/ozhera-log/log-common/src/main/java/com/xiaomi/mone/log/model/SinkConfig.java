/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * LogStore configuration, corresponding to an ES index
 */
@Data
@EqualsAndHashCode
public class SinkConfig {
    private Long logstoreId;
    private String logstoreName;
    /**
     * timestamp is required
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