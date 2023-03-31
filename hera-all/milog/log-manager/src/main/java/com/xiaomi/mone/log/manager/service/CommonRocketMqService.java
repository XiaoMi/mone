package com.xiaomi.mone.log.manager.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.manager.model.bo.RocketMqStatisticParam;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/6/30 11:09
 */
public interface CommonRocketMqService {

    String authorization = Config.ins().get("rocketmq_authorization", "");

    String clientVerify = Config.ins().get("rocketmq_x-ssl-client-verify", "");

    String clientDn = Config.ins().get("rocketmq_x-ssl-client-dn", "");

    String rocketmqAddress = Config.ins().get("rocketmq_address", "");

    String rocketmqAk = Config.ins().get("rocketmq_ak", "");

    String rocketmqSk = Config.ins().get("rocketmq_sk", "");

    String rocketmqNamesrvAddr = Config.ins().get("rocketmq_namesrv_addr", "");

    String rocketmqOrgId = Config.ins().get("rocketmq_org_id", "");

    String userGroupIds = Config.ins().get("rocketmq_user_group_ids", "CI102316,CI102322");

    List<String> brokers = Arrays.stream(Config.ins().get("rocketmq_brokers", "").split(",")).collect(Collectors.toList());
    Integer queueEachBroker = 1;
    Integer queueTotalCount = 1;

    String METHOD_DELETE = "DELETE";
    String MQ_AUTHORIZATION = "authorization";
    String MQ_X_SSL_CLIENT_VERIFY = "x-ssl-client-verify";
    String MQ_X_SSL_CLIENT_DN = "x-ssl-client-dn";
    String MQ_ORG_ID = "orgId";
    String MQ_NAME = "name";
    String MQ_BROKERS = "brokers";
    String MQ_QUEUEEACH_BROKER = "queueEachBroker";
    String MQ_QUEUE_TOTAL_COUNT = "queueTotalCount";
    String MQ_CONTENT_TYPE = "Content-Type";

    /**
     * 封装mq的header陈map
     *
     * @return
     */
    default Map<String, String> getSendMqHeader(String userAuthorization) {
        Map<String, String> headMap = Maps.newHashMap();
        if (StringUtils.isEmpty(userAuthorization)) {
            headMap.put(MQ_AUTHORIZATION, authorization);
        } else {
            headMap.put(MQ_AUTHORIZATION, userAuthorization);
        }
        headMap.put(MQ_X_SSL_CLIENT_VERIFY, clientVerify);
        headMap.put(MQ_X_SSL_CLIENT_DN, clientDn);
        headMap.put(MQ_CONTENT_TYPE, "application/json");
        return headMap;
    }

    /**
     * 封装mq的header成list
     *
     * @return
     */
    default List<String> getSendMqHeader2List(String authorization) {
        List<String> lists = Lists.newArrayList();
        getSendMqHeader(authorization).entrySet().stream().forEach(map -> {
            lists.add(map.getKey());
            lists.add(map.getValue());
        });
        return lists;
    }

    /**
     * 创建mq中topic的一些公共参数
     *
     * @param topicName
     * @return
     */
    default String createTopicBodyParams(String topicName) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put(MQ_ORG_ID, rocketmqOrgId);
        paramsMap.put(MQ_NAME, topicName);
        paramsMap.put(MQ_BROKERS, brokers);
        paramsMap.put(MQ_QUEUEEACH_BROKER, queueEachBroker);
        paramsMap.put(MQ_QUEUE_TOTAL_COUNT, queueTotalCount);
        return new Gson().toJson(paramsMap);
    }

    /**
     * 创建consumerGroup的参数
     *
     * @param name
     * @return
     */
    default String createConsumerGroupParams(String name, String orgId) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        if (StringUtils.isNotEmpty(orgId)) {
            paramsMap.put(MQ_ORG_ID, orgId);
        } else {
            paramsMap.put(MQ_ORG_ID, rocketmqOrgId);
        }
        paramsMap.put(MQ_NAME, name);
        return new Gson().toJson(paramsMap);
    }

    default String updateTopicAuthParams(String topicName) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put(MQ_NAME, topicName);
        List<Map<String, String>> mapList = Lists.newArrayList();
        Arrays.stream(userGroupIds.split(",")).forEach(groupId -> {
            Map<String, String> orgMap = new TreeMap<>();
            orgMap.put("teamId", groupId);
            orgMap.put("value", "PUB|SUB");
            mapList.add(orgMap);
        });
        paramsMap.put("teamPerms", mapList);
        System.out.println(new Gson().toJson(paramsMap));
        return new Gson().toJson(paramsMap);
    }

    default String createTopicStatisticBodyParams(String topic, String group, String broker, String aggregator, Long begin, Long end) {
        RocketMqStatisticParam rmqsc = new RocketMqStatisticParam();
        rmqsc.setTopicList(new ArrayList<String>() {{
            add(topic);
        }});
        rmqsc.setBroker("*");
        rmqsc.setAggregator(aggregator);
        rmqsc.setBegin(begin);
        rmqsc.setEnd(end);
        return new Gson().toJson(rmqsc);
    }

}
