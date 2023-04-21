package com.xiaomi.mone.app.service.mq;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.dao.HeraAppRoleDao;
import com.xiaomi.mone.app.dao.HeraBaseInfoDao;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppRole;
import com.xiaomi.mone.app.service.impl.HeraAppBaseInfoServiceImpl;
import com.xiaomi.mone.app.service.mq.model.HeraAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/7/8 9:56 上午
 */

@Service("rocketMqConsumerHera")
@Slf4j
public class RocketMqHeraAppConsumer {

    @Value("${rocket.mq.hera.app.topic}")
    private String consumerTopic;

    @Value("${rocket.mq.hera.app.tag}")
    private String consumerTag;

    @Value("${rocket.mq.producer.group}")
    private String consumerGroup;

    @NacosValue(value = "${rocket.mq.srvAddr}", autoRefreshed = true)
    private String namesrvAddr;

    //默认为空，根据需要配置
    @NacosValue(value = "${rocketmq.ak}", autoRefreshed = true)
    private String ak;

    //默认为空，根据需要配置
    @NacosValue(value = "${rocketmq.sk}", autoRefreshed = true)
    private String sk;

    private DefaultMQPushConsumer heraAppMQPushConsumer;

    @Autowired
    HeraBaseInfoDao heraBaseInfoDao;

    @Autowired
    HeraAppService hearAppService;

    @Autowired
    HeraAppRoleDao heraAppRoleDao;

    @Autowired
    HeraAppBaseInfoServiceImpl heraBaseInfoService;

    private AtomicBoolean rocketMqStartedStatus = new AtomicBoolean(false);

    @PostConstruct
    public void start() throws MQClientException {

        try {
            boolean b = rocketMqStartedStatus.compareAndSet(false, true);
            if (!b) {
                log.error("RocketMqHeraAppConsumer.heraAppMQPushConsumer start failed, it has started!!");
                return;
            }

            log.info("RocketMqHeraAppConsumer.heraAppMQPushConsumer init start!!");
            if (StringUtils.isNotEmpty(ak)
                    && StringUtils.isNotEmpty(sk)) {
                SessionCredentials credentials = new SessionCredentials(ak, sk);
                RPCHook rpcHook = new AclClientRPCHook(credentials);
                heraAppMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
            } else {
                heraAppMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
            }
            heraAppMQPushConsumer.setNamesrvAddr(namesrvAddr);
            heraAppMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);


            heraAppMQPushConsumer.subscribe(consumerTopic, consumerTag);
            heraAppMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
                try {
                    list.stream().forEach(it -> {
                        log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer received message : MsgId: {}, Topic: {} Tags:{}", it.getMsgId(), it.getTopic(), it.getTags());
                        consumeMessage(it);
                    });
                } catch (Exception e) {
                    log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer message error: {}", e.getMessage(), e);
                }

                return ConsumeOrderlyStatus.SUCCESS;
            });


            log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer init end!!");

            heraAppMQPushConsumer.start();
            log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer has started!!");

        } catch (MQClientException e) {
            log.error("RocketMqHeraAppConsumer# heraAppMQPushConsumer start error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void consumeMessage(MessageExt message) {
        log.info("RocketMqHeraAppConsumer# consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
        try {
            byte[] body = message.getBody();
            HeraAppMessage heraAppMessage = new Gson().fromJson(new String(body), HeraAppMessage.class);
            log.info("RocketMqHeraAppConsumer# consumeMessage convert heraAppMessage : {}", heraAppMessage.toString());

            if (heraAppMessage.getDelete() != null && heraAppMessage.getDelete().intValue() == 1) {
                heraBaseInfoService.deleAppByBindIdAndPlat(heraAppMessage.getId(), heraAppMessage.getPlatformType());
            } else {
                HeraAppBaseInfo heraAppBaseInfo = heraAppMessage.baseInfo();
                HeraAppBaseInfo heraAppBaseInfo1 = saveOrUpdateHeraApp(heraAppBaseInfo);
                saveOrUpdateHeraAppRole(heraAppMessage.getJoinedMembers(), heraAppMessage.getId(), heraAppBaseInfo.getPlatformType());
            }

        } catch (Throwable ex) {
            log.error("mimonitor#appNameGrafanaMapping#consumeMessage error:" + ex.getMessage(), ex);
        }
    }

    private HeraAppBaseInfo saveOrUpdateHeraApp(HeraAppBaseInfo heraAppBaseInfo) {
        HeraAppBaseInfoModel queryInfo = new HeraAppBaseInfoModel();
        queryInfo.setBindId(heraAppBaseInfo.getBindId());
        queryInfo.setPlatformType(heraAppBaseInfo.getPlatformType());
        queryInfo.setStatus(0);
        List<HeraAppBaseInfo> query = heraBaseInfoService.query(queryInfo, null, null);
        if (CollectionUtils.isEmpty(query)) {
            heraBaseInfoDao.create(heraAppBaseInfo);
            log.info("create heraAppBaseInfo by rocketMq success,heraAppBaseInfo:{}", heraAppBaseInfo);
            return null;
        } else {
            if (query.size() > 1) {
                log.error("duplicate heraBaseInfo : {}", new Gson().toJson(query));
            }
            HeraAppBaseInfo heraAppBaseInfo1 = query.get(0);
            heraAppBaseInfo.setId(heraAppBaseInfo1.getId());
            heraAppBaseInfo.setStatus(0);
            heraAppBaseInfo.setUpdateTime(new Date());
            heraBaseInfoDao.update(heraAppBaseInfo);
            log.info("update heraAppBaseInfo by rocketMq success,heraAppBaseInfo:{}", heraAppBaseInfo);
            return heraAppBaseInfo1;
        }
    }

    private void saveOrUpdateHeraAppRole(List<String> members, String appId, Integer platFormType) {

        log.info("saveOrUpdateHeraAppRole appId:{},platFormType:{},members:{}", appId, platFormType, members);
        if (CollectionUtils.isEmpty(members)) {
            return;
        }

        HeraAppRole role = new HeraAppRole();
        role.setRole(0);
        role.setAppId(appId);
        role.setAppPlatform(platFormType);
        role.setStatus(0);
//        role.setUser(member);

        List<HeraAppRole> query = heraAppRoleDao.query(role, null, 2000);

        if (CollectionUtils.isEmpty(query)) {

            List<HeraAppRole> roles = members.stream().filter(a -> org.apache.commons.lang3.StringUtils.isNotBlank(a)).map(t -> {
                HeraAppRole r = new HeraAppRole();
                r.setRole(0);
                r.setAppId(appId);
                r.setAppPlatform(platFormType);
                r.setStatus(0);
                r.setUser(t);
                return r;
            }).collect(Collectors.toList());

            heraAppRoleDao.batchCreate(roles);

            return;
        }

        /**
         * 删除本次参数不包含的成员（需先进行此步骤）
         */
        List<HeraAppRole> delRoles = query.stream().filter(t -> !members.contains(t.getUser())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(delRoles)) {
            delRoles.forEach(t -> {
                heraAppRoleDao.delById(t.getId());
            });
        }


        /**
         * 添加本次新增的成员
         */
        members.removeAll(query.stream().map(t -> t.getUser()).collect(Collectors.toList()));

        if (CollectionUtils.isNotEmpty(members)) {
            members.forEach(t -> {

                if (!StringUtils.isBlank(t)) {
                    HeraAppRole roleAdd = new HeraAppRole();
                    roleAdd.setRole(0);
                    roleAdd.setAppId(appId);
                    roleAdd.setAppPlatform(platFormType);
                    roleAdd.setStatus(0);
                    roleAdd.setUser(t);
                    heraAppRoleDao.create(roleAdd);
                }

            });
        }


    }

}
