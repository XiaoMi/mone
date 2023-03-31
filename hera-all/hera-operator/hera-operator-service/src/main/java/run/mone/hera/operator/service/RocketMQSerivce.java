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
package run.mone.hera.operator.service;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/2/25 11:35 AM
 */
@Service
@Slf4j
public class RocketMQSerivce {

    private String brokerAddr = "rocketmq-broker-0-master:10911";
    private int queueSize = 1;

    private String[] topics = new String[]{
            "hera_app_operate",
            "hera_app_ip_change",
            "hear_log_message_compensate",
            "mone_hera_staging_trace_etl_server",
            "mone_hera_staging_trace_etl_es"};

    public void createTopic(String namesrvAddr) {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("initGroup");
            consumer.setNamesrvAddr(namesrvAddr);
            ClientConfig clientConfig = consumer.cloneClientConfig();
            MQClientInstance mqClientInstance = new MQClientInstance(clientConfig, 0, consumer.buildMQClientId());
            mqClientInstance.start();
            MQClientAPIImpl mqClientAPIImpl = mqClientInstance.getMQClientAPIImpl();
            // 创建topic
            for (String topic : topics){
                createTopic(mqClientAPIImpl, topic);
        }
        } catch (Throwable t) {
            log.error("create rocketMQ topic error", t);
        }
    }

    private void createTopic(MQClientAPIImpl mqClientAPIImpl, String topicName) throws Exception {
        Set<String> topics = getTopics(mqClientAPIImpl);
        if (!topics.contains(topicName)) {
            TopicConfig topicConfig = new TopicConfig(topicName, queueSize, queueSize, 6);
            mqClientAPIImpl.createTopic(brokerAddr, "defaultTopicName", topicConfig, 2000L);
        } else {
            log.info("已存在topic名称为" + topicName + "的主题，无法执行添加操作！");
        }
    }

    private Set<String> getTopics(MQClientAPIImpl mqClientAPIImpl) {
        Set<String> topics = new HashSet<>();
        long timeoutMillis = 2000L;
        try {
            TopicList topicList = mqClientAPIImpl.getTopicListFromNameServer(timeoutMillis);
            Set<String> topicSet = topicList.getTopicList();
            topics.addAll(topicSet);
            TopicList systemTopicList = mqClientAPIImpl.getSystemTopicList(timeoutMillis);
            Set<String> sysTopicSet = systemTopicList.getTopicList();
            topics.addAll(sysTopicSet);
            return topics;
        } catch (RemotingException | MQClientException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
