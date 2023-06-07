package com.xiaomi.mone.log.stream.job.extension;

import com.xiaomi.mone.log.stream.job.SinkJobConfig;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/2 15:38
 */
public interface MqMessagePostProcessing {
    void postProcessing(SinkJobConfig sinkJobConfig, String message);
}
