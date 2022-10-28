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
 *//*


package com.xiaomi.youpin.gwdash.config;

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

    @Value("${rocketmq.namesrv.addr}")
    private String namesrvAddr;

    @Value("${rocketmq.ak}")
    private String ak;

    @Value("${rocketmq.sk}")
    private String sk;

    @Value("${rocketmq.gwdash.ak}")
    private String gwdashAk;

    @Value("${rocketmq.gwdash.sk}")
    private String gwdashSk;

    @Value("${rocketmq.namesrv.addr.gwdash}")
    private String namesrvAddrGwdash;

    @Bean("defaultMQProducer")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        if (StringUtils.isNotEmpty(ak)
                && StringUtils.isNotEmpty(sk)) {
            SessionCredentials credentials = new SessionCredentials(ak, sk);
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            DefaultMQProducer producer = new DefaultMQProducer("gwdashGroup", rpcHook);
            producer.setNamesrvAddr(namesrvAddr);
            producer.start();
            return producer;
        }
        DefaultMQProducer producer = new DefaultMQProducer("gwdashGroup");
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();
        return producer;
    }

    @Bean("gwdashEventMQProducer")
    public DefaultMQProducer gwdashEventMQProducer() throws MQClientException {
        if (StringUtils.isNotEmpty(gwdashAk)
                && StringUtils.isNotEmpty(gwdashSk)) {
            SessionCredentials credentials = new SessionCredentials(gwdashAk, gwdashSk);
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            DefaultMQProducer producer = new DefaultMQProducer("gwdashGroup", rpcHook);
            producer.setNamesrvAddr(namesrvAddrGwdash);
            //不设置此参数 多个clint依然使用同一套配置会导致本producer send 消息报错
            producer.setUnitName("abc");
            producer.start();
            return producer;
        }
        DefaultMQProducer producer = new DefaultMQProducer("gwdashGroup");
        producer.setNamesrvAddr(namesrvAddrGwdash);
        producer.setUnitName("abc");
        producer.start();
        return producer;
    }
}
*/
