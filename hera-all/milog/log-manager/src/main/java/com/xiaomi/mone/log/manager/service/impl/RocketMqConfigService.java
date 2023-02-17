package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.dao.MilogAppMiddlewareRelDao;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.dto.RocketMqResponseDTO;
import com.xiaomi.mone.log.manager.model.dto.TopicInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.CommonRocketMqService;
import com.xiaomi.mone.log.manager.service.MqConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.nutz.lang.Strings;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_CONSUMER_GROUP;
import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:31
 */
@Service
@Slf4j
public class RocketMqConfigService implements MqConfigService, CommonRocketMqService {

    private Gson gson = new Gson();
    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    private Map<String, DefaultMQProducer> mqMap = new HashMap<>();

    @Override
    public MilogAppMiddlewareRel.Config generateConfig(String ak, String sk, String nameServer, String serviceUrl,
                                                       String authorization, String orgId, String teamId, Long exceedId,
                                                       String name, String source, Long id) {
        MilogAppMiddlewareRel.Config config = new MilogAppMiddlewareRel.Config();
        DefaultMQProducer defaultMQProducer = mqMap.get(nameServer);
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer("hera_log");
            defaultMQProducer.setNamesrvAddr(nameServer);
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                log.info("create mq producer error,nameServer:{}", nameServer, e);
                throw new MilogManageException("create mq producer error", e);
            }
            mqMap.put(nameServer, defaultMQProducer);
        }
        String topicName = generateSimpleTopicName(id, name);
        try {
            String brokerName = "tj1-b2c-systech-infra03.kscn";
            defaultMQProducer.createTopic(brokerName, topicName, 1);
        } catch (MQClientException e) {
            log.info("create mq common topic error,nameServer:{}", nameServer, e);
            throw new MilogManageException("create mq common topic error", e);
        }
        config.setTopic(topicName);
        config.setPartitionCnt(1);
        return config;
    }

    @Override
    public List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String teamId) {

        List<DictionaryDTO> dictionaryDTOS = Lists.newArrayList();
        String returnGet = HttpClientV6.get(rocketmqAddress + "/topic/getTopicList", getSendMqHeader(authorization));
        log.info("【RocketMQ查询topic列表】返回值:{}", returnGet);
        try {
            RocketMqResponseDTO<List<LinkedTreeMap>> responseDTO = gson.fromJson(returnGet, RocketMqResponseDTO.class);

            if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                    && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
                log.info("【RocketMQ查询topic列表】:成功", returnGet);
                List<LinkedTreeMap> dataList = responseDTO.getData();
                dataList.forEach(data -> {
                    TopicInfo topicInfo = gson.fromJson(gson.toJson(data), TopicInfo.class);
                    if (Objects.equals(orgId, topicInfo.getOrgId())) {
                        DictionaryDTO<String> dictionaryDTO = new DictionaryDTO<>();
                        dictionaryDTO.setValue(topicInfo.getName());
                        dictionaryDTO.setLabel(topicInfo.getName());
                        dictionaryDTOS.add(dictionaryDTO);
                    }
                });
            } else {
                log.error("【RocketMQ查询topic列表】:失败,失败原因：{}", returnGet);
            }
        } catch (Exception e) {
            log.error(String.format("【RocketMQ查询topic列表】:返回值转化异常，返回值：%s:", returnGet), e);
        }
        return dictionaryDTOS;
    }

    @Override
    public List<String> createCommonTagTopic(String ak, String sk, String nameServer, String serviceUrl, String authorization, String orgId, String brokerName) {
        DefaultMQProducer defaultMQProducer = mqMap.get(nameServer);
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer("hera_log");
            defaultMQProducer.setNamesrvAddr(nameServer);
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                log.info("create mq producer error,nameServer:{}", nameServer, e);
                throw new MilogManageException("create mq producer error", e);
            }
            mqMap.put(nameServer, defaultMQProducer);
        }
        List<String> commonTagTopicNames = generateCommonTagTopicName(orgId);
        try {
            for (String commonTagTopicName : commonTagTopicNames) {
//                String brokerName = "tj1-b2c-systech-infra03.kscn";
                defaultMQProducer.createTopic(brokerName, commonTagTopicName, 1);
            }
        } catch (MQClientException e) {
            log.info("create mq common topic error,nameServer:{}", nameServer, e);
            throw new MilogManageException("create mq common topic error", e);
        }
        return commonTagTopicNames;
    }

    /**
     * 1.先查询是否存在
     * 2.不存在则创建
     */
    public boolean createSubscribeGroup(String serviceUrl, String authorization, String orgId,
                                        Long spaceId, Long storeId, Long tailId, Long milogAppId) {
        String groupName = DEFAULT_CONSUMER_GROUP + Utils.createTag(spaceId, storeId, tailId);
        List<RocketMqResponseDTO.SubGroup> subGroups = querySubGroupList(serviceUrl, authorization, orgId);
        List<RocketMqResponseDTO.SubGroup> groupList = subGroups.parallelStream().filter(subGroup -> subGroup.getName().equals(groupName)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(groupList)) {
            return true;
        }
        String returnPost = HttpClientV6.post(serviceUrl + "/subGroup/createSubGroup", createConsumerGroupParams(groupName, orgId),
                getSendMqHeader(authorization));
        log.info("【RocketMQ创建ConsumerGroup】返回值:{}", returnPost);
        RocketMqResponseDTO<Boolean> responseDTO = gson.fromJson(returnPost, RocketMqResponseDTO.class);
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
            log.info("【RocketMQ创建ConsumerGroup】:成功,{}", returnPost);
            return true;
        } else {
            log.error("【RocketMQ创建ConsumerGroup】:失败,失败原因：{}", returnPost);
        }
        return false;
    }

    public List<RocketMqResponseDTO.SubGroup> querySubGroupList(String serviceUrl, String authorization, String orgId) {
        String querySubGroupListGet = HttpClientV6.get(serviceUrl + "/subGroup/querySubGroupList", getSendMqHeader(authorization));
        log.info("【RocketMQ查询ConsumerGroup】,url:{}返回值:{}", serviceUrl, querySubGroupListGet);
        RocketMqResponseDTO<List<RocketMqResponseDTO.SubGroup>> responseDTO = new Gson().fromJson(querySubGroupListGet, new TypeToken<RocketMqResponseDTO<List<RocketMqResponseDTO.SubGroup>>>() {
        }.getType());
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())
                && CollectionUtils.isNotEmpty(responseDTO.getData())) {
            return responseDTO.getData().stream().filter(subGroup -> subGroup.getOrgId().equals(orgId)).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public boolean deleteSubscribeGroup(String serviceUrl, String authorization, String orgId, Long spaceId, Long storeId, Long tailId) {
        String groupName = DEFAULT_CONSUMER_GROUP + Utils.createTag(spaceId, storeId, tailId);
        String rocketmqAddress = CommonRocketMqService.rocketmqAddress + "/subGroup/deleteSubGroup/" + groupName;
        HttpClientV5.HttpResult httpResult = HttpClientV5.request(rocketmqAddress,
                getSendMqHeader2List(authorization), null, "", METHOD_DELETE);
        log.info("【RocketMQ删除ConsumerGroup】返回值:{}", httpResult.content);
        RocketMqResponseDTO<Boolean> responseDTO = gson.fromJson(httpResult.content, RocketMqResponseDTO.class);
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
            log.info("【RocketMQ删除ConsumerGroup】:成功,{}", httpResult.content);
            return true;
        } else {
            log.error("【RocketMQ删除ConsumerGroup】:失败,失败原因：{}", httpResult.content);
        }
        return false;
    }

    public boolean generateTopicTag(Long configId, Long milogAppId, Long spaceId, Long storeId, Long tailId) {
        String tag = Utils.createTag(spaceId, storeId, tailId);
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogAppId, configId, tailId);
        if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
            milogAppMiddlewareRels.forEach(milogAppMiddlewareRel -> {
                MilogAppMiddlewareRel.Config config = milogAppMiddlewareRel.getConfig();
                String oldTag = config.getTag();
                StringBuilder sb = new StringBuilder();
                if (StringUtils.isNotEmpty(oldTag)) {
                    sb.append(oldTag).append(SYMBOL_COMMA);
                }
                sb.append(tag);
                config.setTag(sb.toString());
                milogAppMiddlewareRelDao.updateTopicRelMqConfig(milogAppMiddlewareRel.getId(), config);
            });
        }
        return true;
    }

    private TopicInfo dealWithTopicName(List<TopicInfo> existTopics) {
        Collections.shuffle(existTopics);
        int randomIndex = new Random().nextInt(existTopics.size());
        return existTopics.get(randomIndex);
    }

    public Set<String> queryExistTopic(Long middlewareId) {
        MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryById(middlewareId);
        List<DictionaryDTO> dictionaryDTOS = queryExistsTopic(middlewareConfig.getAk(), middlewareConfig.getSk(), middlewareConfig.getNameServer(),
                middlewareConfig.getServiceUrl(), middlewareConfig.getAuthorization(), middlewareConfig.getOrgId(), middlewareConfig.getTeamId());
        Set<String> existTopics = dictionaryDTOS.stream().map(dictionaryDTO -> dictionaryDTO.getValue().toString()).collect(Collectors.toSet());
        return existTopics;
    }
}
