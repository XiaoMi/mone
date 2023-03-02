package com.xiaomi.mone.log.manager.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.dao.MilogAppTopicRelDao;
import com.xiaomi.mone.log.manager.model.bo.RocketMqStatisticParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.vo.RocketMQStatisCommand;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Strings;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/6/30 11:08
 */
@Service
@Slf4j
public class RocketMqService implements CommonRocketMqService {

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private Gson gson;
    /**
     * 当前组织下的topic列表
     */
    private List<TopicInfo> topicInfos = Lists.newArrayList();

    @Resource
    private RocketMqService rocketMqService;

    /**
     * http方式创建topic
     *
     * @param topicName
     * @return
     */
    public String httpCreateTopic(String topicName) {
        String returnPost = HttpClientV6.post(rocketmqAddress + "/topic/createTopic", createTopicBodyParams(topicName),
                getSendMqHeader(""));
        log.info("【RocketMQ创建topic】返回值:{}", returnPost);
        RocketMqResponseDTO<Boolean> responseDTO = gson.fromJson(returnPost, RocketMqResponseDTO.class);
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
            log.info("【RocketMQ创建topic】:成功", returnPost);
            log.info("创建完的topic name:{}", topicName);
            return "success";
        } else {
            log.error("【RocketMQ创建topic】:失败,失败原因：{}", returnPost);
            return returnPost;
        }
    }

    @SneakyThrows
    public Result<RocketMqStatisticDTO> httpGetProducerTps(RocketMQStatisCommand command) {
        RocketMqStatisticDTO resp = null;
        try {
            RocketMqStatisticParam param = constractRmqStaticticParam(command);
            String req = new Gson().toJson(param);
            String url = getStatisticUrl(param.getMetirc());
            if (StringUtils.isEmpty(url)) {
                return new Result<>(CommonError.UnknownError.getCode(), "wrong metric type", resp);
            }
            String response = HttpClientV6.post(rocketmqAddress + url, req, getSendMqHeader(""), 30000);
            RocketMqResponseDTO<List<RmqStatisticRulst>> ret = new Gson().fromJson(response, new TypeToken<RocketMqResponseDTO<List<RmqStatisticRulst>>>() {
            }.getType());
            log.debug("response:{}", response);
            if (ret != null && ret.getCode() == Constant.SUCCESS_CODE) {
                resp = constractRMSD(ret);
                return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), resp);
            } else {
                return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), resp);
            }
        } catch (Exception e) {
            log.warn("get producerTps statistics err:", e);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), resp);
        }
    }

    private RocketMqStatisticDTO constractRMSD(RocketMqResponseDTO<List<RmqStatisticRulst>> param) {
        RocketMqStatisticDTO ret = new RocketMqStatisticDTO();
        ret.setCost(param.getCost());
        ret.setRequestId(param.getRequestId());
        ArrayList<StatisticRulst> statisticRulsts = new ArrayList<>();
        param.getData().forEach(v -> {
            StatisticRulst sr = new StatisticRulst();
            StringBuilder append = new StringBuilder().append("cluster:").append(v.getCluster()).append(";")
                    .append("topic:").append(v.getTopic()).append(";");
            if (v.getProperties().getGroup() != null) {
                append.append("group:").append(v.getProperties().getGroup()).append(";");
            }
            if (v.getProperties().getBroker() != null) {
                append.append("broker:").append(v.getProperties().getBroker()).append(";");
            }
            String name = append.toString();
            sr.setName(name);
            sr.setTimestamps(v.getTimestamps());
            statisticRulsts.add(sr);
        });
        ret.setData(statisticRulsts);
        return ret;
    }

    private RocketMqStatisticParam constractRmqStaticticParam(RocketMQStatisCommand command) {
        RocketMqStatisticParam param = new RocketMqStatisticParam();
        param.setBegin(command.getBegin());
        param.setEnd(command.getEnd());
        param.setAggregator(command.getAggreator());
        param.setMetirc(command.getMetric());
        param.setTopicList(new ArrayList<String>() {{
            add(command.getTopic());
        }});
        param.setGroupList(new ArrayList<String>() {{
            add("*");
        }});
        param.setBroker("*");
        param.setClient("*");
        return param;
    }

    private String getStatisticUrl(String metric) {
        switch (metric) {
            case ROCKETMQ_GROUP_DIFF:
                return "/statistic/groupDiff";
            case ROCKETMQ_CLIENT_CONSUME_FAILED_MSG_COUNT:
                return "/statistic/consumeFailCount";
            case ROCKETMQ_CONSUMER_TPS:
                return "/statistic/consumerTps";
            case ROCKETMQ_PRODUCER_TPS:
                return "/statistic/producerTps";
            case ROCKETMQ_PRODUCER_OFFSET:
                return "/statistic/producerOffset";
            default:
                return "";
        }
    }

    private TopicInfo dealWithTopicName() {
        if (topicInfos.isEmpty()) {
            queryExistTopic();
        }
        Collections.shuffle(topicInfos);
        int randomIndex = new Random().nextInt(topicInfos.size());
        return topicInfos.get(randomIndex);
    }


    /**
     * 删除topic
     *
     * @param topicName
     * @return
     */
    public String deleteTopic(Long appId, Long tenantId, String appName, String topicName) {
        String rocketmqAddress = CommonRocketMqService.rocketmqAddress + "/topic/deleteByName/" + topicName;
        HttpClientV5.HttpResult httpResult = HttpClientV5.request(rocketmqAddress,
                getSendMqHeader2List(""), null, "", METHOD_DELETE);
        log.info("【RocketMQ删除topic】返回值:{}", new Gson().toJson(httpResult));
        RocketMqResponseDTO<Boolean> responseDTO = gson.fromJson(httpResult.content, RocketMqResponseDTO.class);
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
            log.info("【RocketMQ删除topic】:成功", httpResult.content);
            //入库
            String source = "";
            try {
                source = MoneUserContext.getCurrentUser().getZone();
            } finally {
            }
            milogAppTopicRelDao.deleteAppTopicRelDb(appId, appName, tenantId, source);
            return "删除topic成功";
        } else {
            log.error("【RocketMQ创建topic】:失败,失败原因：{}", httpResult.content);
        }
        return "删除topic失败";
    }

    /**
     * 初始化查询topic列表
     */
    private void init() {
        queryExistTopicSchedule();
    }

    private void queryExistTopicSchedule() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            topicInfos.clear();
            queryExistTopic();
        }, 10, 5, TimeUnit.MINUTES);
    }

    /**
     * 查询出公共的topic
     *
     * @return
     */
    public Set<String> queryExistTopic() {
        Set<String> existTopics = Sets.newHashSet();
        String returnGet = HttpClientV6.get(rocketmqAddress + "/topic/getTopicList", getSendMqHeader(""));
        log.info("【RocketMQ查询topic列表】返回值:{}", returnGet);
        RocketMqResponseDTO<List<LinkedTreeMap>> responseDTO = gson.fromJson(returnGet, RocketMqResponseDTO.class);
        try {
            if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                    && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
                log.info("【RocketMQ查询topic列表】:成功", returnGet);
                List<LinkedTreeMap> dataList = responseDTO.getData();
                dataList.forEach(data -> {
                    TopicInfo topicInfo = gson.fromJson(gson.toJson(data), TopicInfo.class);
                    if (Objects.equals(rocketmqOrgId, topicInfo.getOrgId()) && !topicInfos.contains(topicInfo)
                            && StringUtils.endsWithIgnoreCase(topicInfo.getName(), COMMON_MESSAGE)) {
                        topicInfos.add(topicInfo);
                    }
                    if (Objects.equals(rocketmqOrgId, topicInfo.getOrgId())) {
                        existTopics.add(topicInfo.getName());
                    }
                });
            } else {
                log.error("【RocketMQ查询topic列表】:失败,失败原因：{}", returnGet);
            }
        } catch (Exception e) {
            log.error(String.format("【RocketMQ查询topic列表】:返回值转化异常，返回值：%s:", returnGet), e);
        }
        return existTopics;
    }

    /**
     * 创建订阅者组
     *
     * @param spaceId
     * @param storeId
     * @param tailId
     * @param appId
     * @return
     */
    public boolean createConsumerGroup(Long spaceId, Long storeId, Long tailId, Long appId) {
        String tagName = DEFAULT_CONSUMER_GROUP + Utils.createTag(spaceId, storeId, tailId);
        String returnPost = HttpClientV6.post(rocketmqAddress + "/subGroup/createSubGroup", createConsumerGroupParams(tagName, ""),
                getSendMqHeader(""));
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

    /**
     * 删除订阅者组
     *
     * @param spaceId
     * @param storeId
     * @param tailId
     * @return
     */
    public boolean deleteConsumerGroup(Long spaceId, Long storeId, Long tailId) {
        String groupName = DEFAULT_CONSUMER_GROUP + Utils.createTag(spaceId, storeId, tailId);
        String rocketmqAddress = CommonRocketMqService.rocketmqAddress + "/subGroup/deleteSubGroup/" + groupName;
        HttpClientV5.HttpResult httpResult = HttpClientV5.request(rocketmqAddress,
                getSendMqHeader2List(""), null, "", METHOD_DELETE);
        log.info("【++】返回值:{}", httpResult.content);
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

    /**
     * 更新topic权限
     *
     * @return
     */
    public boolean updateTopiSubGroupAuth(String topicName) {
        String returnPost = HttpClientV6.post(rocketmqAddress + "/topic/update/topicPerms", updateTopicAuthParams(topicName),
                getSendMqHeader(""));
        log.info("【RocketMQ更新topic权限】返回值:{}", returnPost);
        RocketMqResponseDTO<Boolean> responseDTO = gson.fromJson(returnPost, RocketMqResponseDTO.class);
        if (responseDTO.getCode().compareTo(Constant.SUCCESS_CODE) == 0
                && Strings.equals(Constant.SUCCESS_MESSAGE, responseDTO.getMessage())) {
            log.info("【RocketMQ更新topic权限】:成功,{}", returnPost);
            return true;
        } else {
            log.error("【RocketMQ更新topic权限】:失败,失败原因：{}", returnPost);
        }
        return false;
    }

}
