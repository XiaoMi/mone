package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 1/10/21
 * rocketmq 代理的处理类
 */
@Slf4j
@Component
public class RocketMqEntry implements UdsProcessor {

    private Gson gson = new Gson();

    @Resource
    private DefaultMQProducer producer;

    @Resource
    private DefaultMQPushConsumer consumer;

    @Resource
    private UdsServer udsServer;

    private String topic = "proxy_topic_zzy";

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put("rocketmq", this);

        Safe.runAndLog(() -> {
            consumer.subscribe(topic, "*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    list.forEach(it -> {
                        log.info("receive message:{}", new String(it.getBody()));
                        String tags = it.getTags();
                        udsCall(tags, new String(it.getBody()));
                    });
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        });
    }

    public boolean udsCall(String app, String message) {
        try {
            UdsCommand request = UdsCommand.createRequest();
            request.setData(message);
            request.setApp(app);
            request.setCmd("rocketmq_consumer");
            request.setTimeout(1000L);
            UdsCommand res = udsServer.call(request);
            if (res.getCode() != 0) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public void processRequest(UdsCommand req) {
        UdsCommand res = UdsCommand.createResponse(req.getId());
        switch (req.getMethodName()) {
            case "send": {
                String app = gson.fromJson(req.getParams()[0], String.class);
                String body = gson.fromJson(req.getParams()[1], String.class);
                log.info("send message:{} to rocketmq {}", body, app);
                Message message = new Message();
                message.setTopic(topic);
                message.setTags(app);
                message.setBody(body.getBytes());
                message.setTags(req.getApp());
                try {
                    producer.send(message);
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Send.send(req.getChannel(), res);
                break;
            }
        }

    }
}
