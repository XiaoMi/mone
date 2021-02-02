/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.jcommon.log;

import com.xiaomi.infra.galaxy.rpc.thrift.Credential;
import com.xiaomi.infra.galaxy.rpc.thrift.UserType;
import com.xiaomi.infra.galaxy.talos.admin.TalosAdmin;
import com.xiaomi.infra.galaxy.talos.client.SimpleTopicAbnormalCallback;
import com.xiaomi.infra.galaxy.talos.client.TalosClientConfig;
import com.xiaomi.infra.galaxy.talos.producer.TalosProducer;
import com.xiaomi.infra.galaxy.talos.producer.TalosProducerConfig;
import com.xiaomi.infra.galaxy.talos.producer.UserMessageCallback;
import com.xiaomi.infra.galaxy.talos.producer.UserMessageResult;
import com.xiaomi.infra.galaxy.talos.thrift.DescribeTopicRequest;
import com.xiaomi.infra.galaxy.talos.thrift.Message;
import com.xiaomi.infra.galaxy.talos.thrift.Topic;
import com.xiaomi.infra.galaxy.talos.thrift.TopicTalosResourceName;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author goodjava@qq.com
 */
public class TalosClient {

    private TalosProducer producer;

    @Setter
    private String accessKey;

    @Setter
    private String accessSecret;

    @Setter
    private String topicName;

    @Setter
    private String endpoint;

    private AtomicBoolean initSuccess = new AtomicBoolean(false);

    public TalosClient() {
    }

    public TalosClient(String accessKey, String accessSecret, String topicName, String endpoint) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.topicName = topicName;
        this.endpoint = endpoint;
    }

    public void init() {
        Properties properties = new Properties();
        properties.setProperty("galaxy.talos.service.endpoint", endpoint);
        TalosClientConfig talosClientConfig = new TalosClientConfig(properties);
        TalosProducerConfig talosProducerConfig = new TalosProducerConfig(properties);

        Credential credential = new Credential();
        credential.setSecretKeyId(accessKey).setSecretKey(accessSecret).setType(UserType.DEV_XIAOMI);

        TalosAdmin talosAdmin = new TalosAdmin(talosClientConfig, credential);

        try {
            Topic batchTopic = talosAdmin.describeTopic(new DescribeTopicRequest(topicName));
            TopicTalosResourceName batchTopicTalosResourceName = batchTopic.getTopicInfo().getTopicTalosResourceName();

            BatchMessageSendCallBack callBack = new BatchMessageSendCallBack();
            TalosProducer producer = new TalosProducer(talosProducerConfig, credential,
                    batchTopicTalosResourceName,
                    new SimpleTopicAbnormalCallback(),
                    callBack);
            this.producer = producer;
            System.out.println("youpin log init success");
            initSuccess.set(true);
        } catch (Throwable ex) {
            System.err.println("youpin log init error " + ex.getMessage());
        }
    }

    public void shutdown() {
        try {
            if (this.producer != null) {
                this.producer.shutdown();
            }
        } catch (Exception e) {
            System.out.println("failed to shutdown talos-producer");
        }
        initSuccess.set(false);
    }


    private class BatchMessageSendCallBack implements UserMessageCallback {

        @Override
        public void onSuccess(UserMessageResult userMessageResult) {
        }

        @Override
        public void onError(UserMessageResult userMessageResult) {
            for (Message message : userMessageResult.getMessageList()) {
                System.err.println("send message: " + message + " error");
            }
        }
    }


    public boolean sendMsg(String msgStr) {
        if (initSuccess.get() != true) {
            return false;
        }
        List<Message> msgs = new ArrayList<>();
        Message msg = new Message(ByteBuffer.wrap(msgStr.getBytes()));
        msgs.add(msg);
        try {
            producer.addUserMessage(msgs);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
