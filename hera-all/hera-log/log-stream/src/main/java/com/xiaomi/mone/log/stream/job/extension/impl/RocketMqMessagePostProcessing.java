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
