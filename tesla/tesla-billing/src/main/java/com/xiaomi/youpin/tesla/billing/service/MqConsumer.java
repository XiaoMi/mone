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

package com.xiaomi.youpin.tesla.billing.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.billing.bo.CostBo;
import com.xiaomi.youpin.tesla.billing.common.BillingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service
public class MqConsumer {


    @Resource
    private DefaultMQPushConsumer consumer;

    @Resource
    private CostService costService;


    public void init1() {
        log.info("MqConsumer init");
        try {
            consumer.subscribe("test_docean", "*");
        } catch (MQClientException e) {
            log.error(e.getMessage());
            throw new BillingException(e.getMessage());
        }

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            msgs.stream().forEach(msg -> {
                CostBo costBo = new Gson().fromJson(new String(msg.getBody()), CostBo.class);
                log.info("costBo:{}", costBo);
                if (costBo.getType().equals("begin")) {
                    costService.beginCost(costBo);
                }

                if (costBo.getType().equals("stop")) {
                    costService.stopCost(costBo);
                }

            });

            //返回消费状态
            //CONSUME_SUCCESS 消费成功
            //RECONSUME_LATER 消费失败，需要稍后重新消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        //调用start()方法启动consumer
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("fail to start consume, nameSerAddress");
        }
        log.info("Consumer Started.");

    }


}
