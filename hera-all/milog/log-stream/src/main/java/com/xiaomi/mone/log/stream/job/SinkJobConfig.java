package com.xiaomi.mone.log.stream.job;

import com.xiaomi.mone.log.model.EsInfo;
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
}
