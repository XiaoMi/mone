package com.xiaomi.mone.monitor.service.rocketmq;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.app.api.model.HeraAppMqInfo;
import com.xiaomi.mone.monitor.dao.HeraAppRoleDao;
import com.xiaomi.mone.monitor.dao.HeraBaseInfoDao;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
import com.xiaomi.mone.monitor.dao.model.HeraAppRole;
import com.xiaomi.mone.monitor.service.AppGrafanaMappingService;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.GrafanaService;
import com.xiaomi.mone.monitor.service.HeraBaseInfoService;
import com.xiaomi.mone.monitor.service.rocketmq.model.RocketMqReceiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${rocketmq.consumer.topic}")
    private String consumerTopic;

    @Value("${rocketmq.consumer.tag}")
    private String consumerTag;

    @Value("${rocketmq.group}")
    private String consumerGroup;

    @Value("${rocketmq.namesrv.addr}")
    private String namesrvAddr;

    @NacosValue("${rocketmq.ak}")
    private String ak;

    @NacosValue("${rocketmq.sk}")
    private String sk;

    private DefaultMQPushConsumer heraAppMQPushConsumer;

    @Autowired
    HeraBaseInfoDao heraBaseInfoDao;

    @Autowired
    HeraAppRoleDao heraAppRoleDao;

    @Autowired
    GrafanaService grafanaService;

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Autowired
    AppMonitorService appMonitorService;

    private AtomicBoolean rocketMqStartedStatus = new AtomicBoolean(false);

    public void start() throws MQClientException {

        try {
            boolean b = rocketMqStartedStatus.compareAndSet(false, true);
            if(!b){
                log.error("RocketMqHeraAppConsumer.heraAppMQPushConsumer start failed, it has started!!");
                return;
            }

            log.info("RocketMqHeraAppConsumer.heraAppMQPushConsumer init start!!");
//            if (StringUtils.isNotEmpty(ak)
//                    && StringUtils.isNotEmpty(sk)) {
//                SessionCredentials credentials = new SessionCredentials(ak, sk);
//                RPCHook rpcHook = new AclClientRPCHook(credentials);
//                heraAppMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
//            } else {
//                heraAppMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
//            }

            heraAppMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
            heraAppMQPushConsumer.setNamesrvAddr(namesrvAddr);
            heraAppMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            heraAppMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);

            heraAppMQPushConsumer.subscribe(consumerTopic,consumerTag);
            heraAppMQPushConsumer.registerMessageListener((MessageListenerOrderly)(list, consumeOrderlyContext) -> {
                try {
                    list.stream().forEach(it -> {
                        log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer received message : MsgId: {}, Topic: {} Tags:{}", it.getMsgId(), it.getTopic(), it.getTags());
                        consumeMessage(it);
                    });
                } catch (Exception e) {
                    log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer message error: {}", e.getMessage(),e);
                }

                return ConsumeOrderlyStatus.SUCCESS;
            });


            log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer init end!!");

            heraAppMQPushConsumer.start();
            log.info("RocketMqHeraAppConsumer# heraAppMQPushConsumer has started!!");

        } catch (MQClientException e) {
            log.error("RocketMqHeraAppConsumer# heraAppMQPushConsumer start error: {}" , e.getMessage(),e);
            throw e;
        }
    }

    private void consumeMessage(MessageExt message) {
        log.info("RocketMqHeraAppConsumer# consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
        try {
            Gson gson = new Gson();
            RocketMqReceiver<HeraAppMqInfo> heraAppMessage = gson.fromJson(new String(message.getBody()), new TypeToken<RocketMqReceiver<HeraAppMqInfo>>(){}.getType());
            log.info("RocketMqHeraAppConsumer# consumeMessage convert heraAppMessage : {}",heraAppMessage.toString());
            if (heraAppMessage.getData() == null) {
                return;
            }
            HeraAppBaseInfo heraApp = gson.fromJson(gson.toJson(heraAppMessage.getData().getAfterAppBaseInfo()), HeraAppBaseInfo.class);
            appGrafanaMappingService.createTmpByAppBaseInfo(heraApp);

//            if (heraAppMessage.getDelete() != null && heraAppMessage.getDelete().intValue() ==1) {
//                heraBaseInfoService.deleAppByBindIdAndPlat(heraAppMessage.getId(),heraAppMessage.getPlatformType());
//            }else{
//                HeraAppBaseInfo heraAppBaseInfo = heraAppMessage.baseInfo();
//                HeraAppBaseInfo heraAppBaseInfo1 = saveOrUpdateHeraApp(heraAppBaseInfo);
//                saveOrUpdateHeraAppRole(heraAppMessage.getJoinedMembers(),heraAppMessage.getId(),heraAppBaseInfo.getPlatformType());
//
//                boolean appNameModify = !heraAppBaseInfo.getAppName().equals(heraAppBaseInfo1.getAppName());
//
//                if(heraAppBaseInfo1 == null || appNameModify){
//                    appGrafanaMappingService.createTmpByAppBaseInfo(heraAppBaseInfo);
//                }
//
//                /**
//                 * 项目更名后，名称同步更新参与、关注列表，报警信息根据项目名称重建
//                 */
//                if(appNameModify){
//
//                    Integer projectId = Integer.valueOf(heraAppBaseInfo1.getBindId());
//                    Integer plat = heraAppBaseInfo1.getPlatformType();
//                    Integer iamId = heraAppBaseInfo.getIamTreeId() == null ? heraAppBaseInfo1.getIamTreeId() : heraAppBaseInfo.getIamTreeId();
//                    appMonitorService.appPlatMove(projectId,plat,projectId,plat,iamId,heraAppBaseInfo.getAppName(),true);
//
//                }
//
//            }

        } catch (Throwable ex) {
            log.error("mimonitor#appNameGrafanaMapping#consumeMessage error:" + ex.getMessage(), ex);
        }
    }

    private HeraAppBaseInfo saveOrUpdateHeraApp(HeraAppBaseInfo heraAppBaseInfo){
        HeraAppBaseInfo queryInfo = new HeraAppBaseInfo();
        queryInfo.setBindId(heraAppBaseInfo.getBindId());
        queryInfo.setPlatformType(heraAppBaseInfo.getPlatformType());
        queryInfo.setStatus(0);
        List<HeraAppBaseInfo> query = heraBaseInfoService.query(queryInfo, null, null);
        if(CollectionUtils.isEmpty(query)){
            heraBaseInfoDao.create(heraAppBaseInfo);
            log.info("create heraAppBaseInfo by rocketMq success,heraAppBaseInfo:{}",heraAppBaseInfo);
            return null;
        }else{
            if(query.size() > 1){
                log.error("duplicate heraBaseInfo : {}",new Gson().toJson(query));
            }
            HeraAppBaseInfo heraAppBaseInfo1 = query.get(0);
            heraAppBaseInfo.setId(heraAppBaseInfo1.getId());
            heraAppBaseInfo.setStatus(0);
            heraBaseInfoDao.update(heraAppBaseInfo);
            log.info("update heraAppBaseInfo by rocketMq success,heraAppBaseInfo:{}",heraAppBaseInfo);
            return heraAppBaseInfo1;
        }
    }

    private void saveOrUpdateHeraAppRole(List<String> members,String appId,Integer platFormType){

        log.info("saveOrUpdateHeraAppRole appId:{},platFormType:{},members:{}",appId,platFormType,members);
        if(CollectionUtils.isEmpty(members)){
            return;
        }

        HeraAppRole role = new HeraAppRole();
        role.setRole(0);
        role.setAppId(appId);
        role.setAppPlatform(platFormType);
        role.setStatus(0);
//        role.setUser(member);

        List<HeraAppRole> query = heraAppRoleDao.query(role, null, 2000);

        if(CollectionUtils.isEmpty(query)){

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
        if(CollectionUtils.isNotEmpty(delRoles)){
            delRoles.forEach(t->{
                heraAppRoleDao.delById(t.getId());
            });
        }


        /**
         * 添加本次新增的成员
         */
        members.removeAll(query.stream().map(t-> t.getUser()).collect(Collectors.toList()));

        if(CollectionUtils.isNotEmpty(members)){
            members.forEach(t -> {

                if(!StringUtils.isBlank(t)){
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
