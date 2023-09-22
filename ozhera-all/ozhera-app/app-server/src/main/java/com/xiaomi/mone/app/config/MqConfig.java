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

package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @NacosValue("${rocket.mq.srvAddr}")
    private String namesrvAddr;

    @NacosValue("${rocketmq.ak}")
    private String ak;

    @NacosValue("${rocketmq.sk}")
    private String sk;

    @Bean("defaultMQProducer")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        if (StringUtils.isNotEmpty(ak)
                && StringUtils.isNotEmpty(sk)) {
            SessionCredentials credentials = new SessionCredentials(ak, sk);
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            DefaultMQProducer producer = new DefaultMQProducer("heraAppModify", rpcHook);
            producer.setNamesrvAddr(namesrvAddr);
            producer.start();
            return producer;
        }
        DefaultMQProducer producer = new DefaultMQProducer("heraAppModify");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();
        return producer;
    }


}
