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
package com.xiaomi.mone.log.agent.service.impl;

import com.google.common.base.Preconditions;
import com.xiaomi.mone.log.agent.exception.AgentException;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.export.impl.RmqExporter;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.agent.output.RmqOutput;
import com.xiaomi.mone.log.agent.service.OutPutService;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.mone.log.api.model.meta.MQConfig;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.concurrent.ConcurrentHashMap;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_CONSUMER_GROUP;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/7 9:44 AM
 */
@Service(name = "RocketMQService")
@Slf4j
public class RocketMQService implements OutPutService {

    private ConcurrentHashMap<String, DefaultMQProducer> producerMap;

    public void init() {
        producerMap = new ConcurrentHashMap<>(128);
    }

    @Override
    public boolean compare(Output oldOutput, Output newOutput) {
        if (!oldOutput.getOutputType().equals(newOutput.getOutputType())) {
            return false;
        }
        RmqOutput newRmqOutput = (RmqOutput) newOutput;
        RmqOutput oldRmqOutput = (RmqOutput) oldOutput;
        if (newRmqOutput.equals(oldRmqOutput)) {
            return true;
        }
        return false;
    }

    @Override
    public void preCheckOutput(Output output) {
        RmqOutput rmqOutput = (RmqOutput) output;
        Preconditions.checkArgument(null != rmqOutput.getClusterInfo(), "rmqOutput.getClusterInfo can not be null");
        Preconditions.checkArgument(null != rmqOutput.getTopic(), "rmqOutput.getTopic can not be null");
        Preconditions.checkArgument(null != rmqOutput.getProducerGroup(), "rmqOutput.getProducerGroup can not be null");
    }

    @Override
    public MsgExporter exporterTrans(Output output) {
        RmqOutput rmqOutput = (RmqOutput) output;
        String nameSrvAddr = rmqOutput.getClusterInfo();
        DefaultMQProducer mqProducer = producerMap.get(nameSrvAddr);
        if (null == mqProducer) {
            mqProducer = initMqProducer(rmqOutput);
            producerMap.put(String.valueOf(nameSrvAddr), mqProducer);
        }

        RmqExporter rmqExporter = new RmqExporter(mqProducer);
        rmqExporter.setRmqTopic(rmqOutput.getTopic());
        rmqExporter.setBatchSize(rmqOutput.getBatchExportSize());

        return rmqExporter;
    }

    @Override
    public void removeMQ(Output output) {
    }

    @Override
    public Output configOutPut(LogPattern logPattern) {
        MQConfig mqConfig = logPattern.getMQConfig();
        RmqOutput output = new RmqOutput();
        output.setOutputType(RmqOutput.OUTPUT_ROCKETMQ);
        output.setClusterInfo(mqConfig.getClusterInfo());
        output.setProducerGroup(mqConfig.getProducerGroup());
        output.setAk(mqConfig.getAk());
        output.setSk(mqConfig.getSk());
        output.setTopic(mqConfig.getTopic());
        output.setPartitionCnt(mqConfig.getPartitionCnt());
        output.setTag(mqConfig.getTag());
        output.setProducerGroup(DEFAULT_CONSUMER_GROUP + (null == logPattern.getPatternCode() ? "" : logPattern.getPatternCode()));
        return output;
    }

    private DefaultMQProducer initMqProducer(RmqOutput rmqOutput) {
        DefaultMQProducer producer;
        if (StringUtils.isNotEmpty(rmqOutput.getAk()) && StringUtils.isNotEmpty(rmqOutput.getSk())) {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(rmqOutput.getAk(), rmqOutput.getSk()));
            producer = new DefaultMQProducer(rmqOutput.getProducerGroup() + "x", rpcHook, true, null);
        } else {
            producer = new DefaultMQProducer(rmqOutput.getProducerGroup() + "x", true);
        }
        producer.setNamesrvAddr(rmqOutput.getClusterInfo());
        try {
            producer.start();
            return producer;
        } catch (MQClientException e) {
            log.error("ChannelBootstrap.initMqProducer error, RocketmqConfig: {}", rmqOutput, e);
            throw new AgentException("initMqProducer exception", e);
        }
    }
}
