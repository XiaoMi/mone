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
package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.dto.RocketMqResponseDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.manager.service.CommonRocketMqService;
import com.xiaomi.mone.log.manager.service.MqConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:31
 */
@Service
@Slf4j
public class RocketMqConfigService implements MqConfigService, CommonRocketMqService {

    private Map<String, DefaultMQProducer> mqMap = new HashMap<>();

    @Override
    public MilogAppMiddlewareRel.Config generateConfig(String ak, String sk, String nameServer, String serviceUrl,
                                                       String authorization, String orgId, String teamId, Long exceedId,
                                                       String name, String source, Long id) {
        MilogAppMiddlewareRel.Config config = new MilogAppMiddlewareRel.Config();
        DefaultMQProducer defaultMQProducer = mqMap.get(nameServer);
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer("hera_log");
            defaultMQProducer.setNamesrvAddr(nameServer);
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                log.info("create mq producer error,nameServer:{}", nameServer, e);
                throw new MilogManageException("create mq producer error", e);
            }
            mqMap.put(nameServer, defaultMQProducer);
        }
        String topicName = generateSimpleTopicName(id, name);
        try {
            String brokerName = "tj1-b2c-systech-infra03.kscn";
            defaultMQProducer.createTopic(brokerName, topicName, 1);
        } catch (MQClientException e) {
            log.info("create mq common topic error,nameServer:{}", nameServer, e);
            throw new MilogManageException("create mq common topic error", e);
        }
        config.setTopic(topicName);
        config.setPartitionCnt(1);
        return config;
    }

    @Override
    public List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String teamId) {
        List<DictionaryDTO> dictionaryDTOS = Lists.newArrayList();
        return dictionaryDTOS;
    }

    @Override
    public List<String> createCommonTagTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String brokerName) {
        DefaultMQProducer defaultMQProducer = mqMap.get(nameServer);
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer("hera_log");
            defaultMQProducer.setNamesrvAddr(nameServer);
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                log.info("create mq producer error,nameServer:{}", nameServer, e);
                throw new MilogManageException("create mq producer error", e);
            }
            mqMap.put(nameServer, defaultMQProducer);
        }
        List<String> commonTagTopicNames = generateCommonTagTopicName(orgId);
        try {
            for (String commonTagTopicName : commonTagTopicNames) {
//                String brokerName = "tj1-b2c-systech-infra03.kscn";
                defaultMQProducer.createTopic(brokerName, commonTagTopicName, 1);
            }
        } catch (MQClientException e) {
            log.info("create mq common topic error,nameServer:{}", nameServer, e);
            throw new MilogManageException("create mq common topic error", e);
        }
        return commonTagTopicNames;
    }

    /**
     * 1.先查询是否存在
     * 2.不存在则创建
     */
    public boolean createSubscribeGroup(String serviceUrl, String authorization, String orgId,
                                        Long spaceId, Long storeId, Long tailId, Long milogAppId) {
        return false;
    }

    public List<RocketMqResponseDTO.SubGroup> querySubGroupList(String serviceUrl, String authorization, String orgId) {
        return Lists.newArrayList();
    }

    public boolean deleteSubscribeGroup(String serviceUrl, String authorization, String orgId, Long spaceId, Long storeId, Long tailId) {
        return false;
    }

}
