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

package com.xiaomi.youpin.tesla.agent.service;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.bo.MqCommand;
import com.xiaomi.youpin.tesla.agent.common.Config;
import com.xiaomi.youpin.tesla.agent.common.TagUtils;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * <p>
 * 通过消息队列处理进来的请求
 * 方便mischedule等服务的调用
 */
@Slf4j
@Component(desc = "rocketmq service")
public class MqService implements IService {

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Resource
    private RpcClient client;

    public MqService(RpcClient client) {
        this.client = client;
    }

    public MqService() {
    }

    @Log
    @Override
    public void init() {
        try {
            defaultMQPushConsumer = new DefaultMQPushConsumer(Config.ins().get("consumerGroup", ""));
            defaultMQPushConsumer.setNamesrvAddr(Config.ins().get("namesrvAddr", ""));
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            String tag = TagUtils.getTag();
            log.info("tag:{}", tag);
            defaultMQPushConsumer.subscribe(Config.ins().get("topic", ""), tag);
            defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
                list.stream().forEach(it -> {
                    String body = new String(it.getBody());
                    log.info("mq service req:{}", body);
                    MqCommand command = new Gson().fromJson(body, MqCommand.class);
                    NettyRequestProcessor processor = client.getProcessor(command.getCmd());
                    try {
                        processor.processRequest(null, RemotingCommand.createResponseCommand(command.getCmd(), command.getBody()));
                    } catch (Throwable ex) {
                        log.error("error:{}", ex.getMessage());
                    }
                });
                return ConsumeOrderlyStatus.SUCCESS;
            });
            log.info("mq service init finish");
        } catch (Throwable ex) {
            log.error("error:{}", ex.getMessage());
        }
    }

}
