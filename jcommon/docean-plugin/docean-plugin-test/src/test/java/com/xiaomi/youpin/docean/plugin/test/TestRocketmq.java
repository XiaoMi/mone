package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TestRocketmq {

    @Resource
    private DefaultMQProducer producer;

    @Resource
    private DefaultMQPushConsumer consumer;

    private ExecutorService pool = Executors.newFixedThreadPool(1);


    private String message = "2021-08-23 13:37:26,719|INFO ||main|c.a.dubbo.registry.nacos.NacosRegistry| [DUBBO] Register: consumer://"+""+"/com.xiaomi.mone.log.api.service.AgentConfigService?application=milog_manager&category=consumers&check=false&dubbo=2.0.2&dubbo_version=2.7.0_0.0.1_2020-11-25&interface=com.xiaomi.mone.log.api.service.AgentConfigService&methods=getLogCollectMetaFromManager&pid=18391&side=consumer&timeout=1000&timestamp=1629697046346, dubbo version: 2.7.0-youpin-SNAPSHOT, current host:";


    public void produce() throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {

        Message m = new Message("test_docean",// topic
                "",// tag
                ("begin").getBytes(RemotingHelper.DEFAULT_CHARSET)// body
        );

        producer.send(m);
        //发送10条消息到Topic为TopicTest，tag为TagA，消息内容为msgbody拼接上i的值
        for (int i = 0; i < 10000000; i++) {
            try {
//                pool.submit(()->{
                sendMsg();
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //发送完消息之后，调用shutdown()方法关闭producer
        producer.shutdown();
    }

    private void sendMsg() {
        SendResult sendResult = null;
        try {
            Message msg = new Message("test_docean",// topic
                    "",// tag
                    message.getBytes(RemotingHelper.DEFAULT_CHARSET)// body
            );
//                    producer.send(msg, new SendCallback() {
//                        @Override
//                        public void onSuccess(SendResult sendResult) {
//                            System.out.println("==>" + sendResult);
//                        }
//
//                        @Override
//                        public void onException(Throwable throwable) {
//
//                        }
//                    });
            producer.sendOneway(msg);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //打印返回结果，可以看到消息发送的状态以及一些相关信息
//        log.info("send result is: {}", sendResult);
    }

    public void consume() {
        try {
            consumer.subscribe("test_docean", "*");
        } catch (MQClientException e) {
            log.error("fail to consume msg");
        }

        //设置一个Listener，主要进行消息的逻辑处理
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {

                log.info(Thread.currentThread().getName() + " Receive New Messages: " + msgs);

                //返回消费状态
                //CONSUME_SUCCESS 消费成功
                //RECONSUME_LATER 消费失败，需要稍后重新消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
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
