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
package com.xiaomi.mone.log.stream.job;

import com.xiaomi.mone.log.model.EsInfo;
import com.xiaomi.mone.log.stream.common.SinkJobEnum;
import com.xiaomi.mone.log.stream.sink.SinkChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 16:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinkJobConfig extends LogConfig {
    private String mqType;
    private String ak;
    private String sk;
    private String clusterInfo;
    private String topic;
    private String tag;
    private String index;
    private String keyList;
    private String valueList;
    private String parseScript;
    private String logStoreName;
    private SinkChain sinkChain;
    private String tail;
    private EsInfo esInfo;
    private Integer parseType;
    /**
     * @see SinkJobEnum#name()
     */
    private String jobType;
}
