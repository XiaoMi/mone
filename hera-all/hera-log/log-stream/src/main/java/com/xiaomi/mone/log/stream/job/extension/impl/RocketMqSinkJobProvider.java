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

import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.parse.LogParserFactory;
import com.xiaomi.mone.log.stream.common.LogStreamConstants;
import com.xiaomi.mone.log.stream.common.SinkJobEnum;
import com.xiaomi.mone.log.stream.job.LogDataTransfer;
import com.xiaomi.mone.log.stream.job.SinkJobConfig;
import com.xiaomi.mone.log.stream.job.extension.MqMessageProduct;
import com.xiaomi.mone.log.stream.job.extension.SinkJob;
import com.xiaomi.mone.log.stream.job.extension.SinkJobProvider;
import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqConfig;
import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqPlugin;
import com.xiaomi.mone.log.stream.sink.SinkChain;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * @author shanwb
 * @date 2023-04-07
 */
@Service(name = "rocketmq" + LogStreamConstants.sinkJobProviderBeanSuffix)
public class RocketMqSinkJobProvider implements SinkJobProvider {

    @Override
    public SinkJob getSinkJob(SinkJobConfig sinkJobConfig) {
        SinkJob sinkJob = null;
        SinkJobEnum jobType = SinkJobEnum.valueOf(sinkJobConfig.getJobType());

        MqMessageProduct mqMessageProduct = new RocketMqMessageProduct();
        EsMessageSender esMessageSender = new EsMessageSender(sinkJobConfig.getIndex(), mqMessageProduct);
        EsProcessor esProcessor = EsPlugin.getEsProcessor(sinkJobConfig.getEsInfo(), mqMessageDTO -> esMessageSender.compensateSend(mqMessageDTO));
        esMessageSender.setEsProcessor(esProcessor);

        SinkChain sinkChain = sinkJobConfig.getSinkChain();
        LogParser logParser = LogParserFactory.getLogParser(
                sinkJobConfig.getParseType(), sinkJobConfig.getKeyList(), sinkJobConfig.getValueList(),
                sinkJobConfig.getParseScript(), sinkJobConfig.getTopic(), sinkJobConfig.getTail(),
                sinkJobConfig.getTag(), sinkJobConfig.getLogStoreName());

        LogDataTransfer dataTransfer = new LogDataTransfer(sinkChain, logParser, esMessageSender, sinkJobConfig);
        dataTransfer.setJobType(jobType);

        RocketmqConfig rocketmqConfig = RocketmqPlugin.buildRocketmqConfig(sinkJobConfig.getAk(), sinkJobConfig.getSk(), sinkJobConfig.getClusterInfo(),
                sinkJobConfig.getTopic(), sinkJobConfig.getTag(), jobType);
        DefaultMQPushConsumer rocketMqConsumer = RocketmqPlugin.getRocketMqConsumer(rocketmqConfig);
        sinkJob = new RocketMqSinkJob(rocketmqConfig, rocketMqConsumer, dataTransfer);

        return sinkJob;
    }

    @Override
    public SinkJob getBackupJob(SinkJobConfig sinkJobConfig) {
        return null;
    }
}
