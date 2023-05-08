package com.xiaomi.mone.app.service.mq;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraMetaDataMessage;
import com.xiaomi.mone.app.api.service.HeraMetaDataService;
import com.xiaomi.mone.app.dao.mapper.HeraMetaDataMapper;
import com.xiaomi.mone.app.model.HeraMetaData;
import com.xiaomi.mone.app.model.HeraMetaDataPort;
import com.xiaomi.mone.app.util.HeraMetaDataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/5/4 6:14 PM
 */
@Service
@Slf4j
public class RocketMqHeraMetaDataConsumer {

    @NacosValue(value = "${rocket.mq.hera.metadata.topic}",autoRefreshed = true)
    private String consumerTopic;

    @NacosValue(value = "${rocket.mq.hera.metadata.producer.group}",autoRefreshed = true)
    private String consumerGroup;

    @NacosValue(value = "${rocket.mq.srvAddr}", autoRefreshed = true)
    private String namesrvAddr;

    //默认为空，根据需要配置
    @NacosValue(value = "${rocketmq.ak}", autoRefreshed = true)
    private String ak;

    //默认为空，根据需要配置
    @NacosValue(value = "${rocketmq.sk}", autoRefreshed = true)
    private String sk;

    @Autowired
    private HeraMetaDataService heraMetaDataService;

    private HeraMetaDataMapper heraMetaDataMapper;

    public RocketMqHeraMetaDataConsumer(HeraMetaDataMapper heraMetaDataMapper){
        this.heraMetaDataMapper = heraMetaDataMapper;
    }

    private DefaultMQPushConsumer heraMetaDataMQPushConsumer;

    private AtomicBoolean rocketMqStartedStatus = new AtomicBoolean(false);

    private Gson gson = new Gson();

    @PostConstruct
    public void start() throws MQClientException {

        try {
            boolean b = rocketMqStartedStatus.compareAndSet(false, true);
            if (!b) {
                log.error("RocketMqHeraMetaDataConsumer.heraMetaDataMQPushConsumer start failed, it has started!!");
                return;
            }

            log.info("RocketMqHeraMetaDataConsumer.heraMetaDataMQPushConsumer init start!!");
            if (StringUtils.isNotEmpty(ak)
                    && StringUtils.isNotEmpty(sk)) {
                SessionCredentials credentials = new SessionCredentials(ak, sk);
                RPCHook rpcHook = new AclClientRPCHook(credentials);
                heraMetaDataMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
            } else {
                heraMetaDataMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
            }
            heraMetaDataMQPushConsumer.setNamesrvAddr(namesrvAddr);
            heraMetaDataMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

            heraMetaDataMQPushConsumer.subscribe(consumerTopic, "*");
            heraMetaDataMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
                try {
                    list.stream().forEach(it -> {
                        log.info("RocketMqHeraMetaDataConsumer# heraMetaDataMQPushConsumer received message : MsgId: {}, Topic: {} Tags:{}", it.getMsgId(), it.getTopic(), it.getTags());
                        consumeMessage(it);
                    });
                } catch (Exception e) {
                    log.info("RocketMqHeraMetaDataConsumer# heraMetaDataMQPushConsumer message error: {}", e.getMessage(), e);
                }

                return ConsumeOrderlyStatus.SUCCESS;
            });


            log.info("RocketMqHeraMetaDataConsumer# heraMetaDataMQPushConsumer init end!!");

            heraMetaDataMQPushConsumer.start();
            log.info("RocketMqHeraMetaDataConsumer# heraMetaDataMQPushConsumer has started!!");

        } catch (MQClientException e) {
            log.error("RocketMqHeraMetaDataConsumer# heraMetaDataMQPushConsumer start error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void consumeMessage(MessageExt message) {
        log.info("RocketMqHeraMetaDataConsumer# consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
        try {
            byte[] body = message.getBody();
            HeraMetaDataMessage heraMetaDataMessage = gson.fromJson(new String(body), HeraMetaDataMessage.class);
            log.info("RocketMqHeraMetaDataConsumer# consumeMessage convert heraMetaDataMessage : {}", heraMetaDataMessage.toString());

            HeraMetaData heraMetaData = HeraMetaDataConvertUtil.messageConvertTo(heraMetaDataMessage);

            if("insert".equals(heraMetaDataMessage.getOperator())){
                if(getOne(heraMetaDataMessage.getMetaId(), heraMetaDataMessage.getHost()) == null){
                    Date date = new Date();
                    heraMetaData.setCreateTime(date);
                    heraMetaData.setUpdateTime(date);
                    heraMetaDataMapper.insert(heraMetaData);
                }
            }
        } catch (Throwable ex) {
            log.error("RocketMqHeraMetaDataConsumer#consumeMessage error:" + ex.getMessage(), ex);
        }
    }

    private HeraMetaData getOne(Integer metaId, String ip){
        QueryWrapper<HeraMetaData> queryWrapper = new QueryWrapper();
        queryWrapper.eq("meta_id", metaId);
        queryWrapper.eq("host", ip);
        return heraMetaDataMapper.selectOne(queryWrapper);
    }
}
