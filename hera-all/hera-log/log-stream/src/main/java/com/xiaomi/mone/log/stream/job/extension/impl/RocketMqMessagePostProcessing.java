package com.xiaomi.mone.log.stream.job.extension.impl;

import com.xiaomi.mone.log.stream.common.LogStreamConstants;
import com.xiaomi.mone.log.stream.job.SinkJobConfig;
import com.xiaomi.mone.log.stream.job.extension.MqMessagePostProcessing;
import com.xiaomi.youpin.docean.anno.Service;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/2 15:39
 */
@Service(name = "rocketmq" + LogStreamConstants.postProcessingProviderBeanSuffix)
public class RocketMqMessagePostProcessing implements MqMessagePostProcessing {
    @Override
    public void postProcessing(SinkJobConfig sinkJobConfig, String message) {

    }
}
