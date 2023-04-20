package com.xiaomi.mone.log.manager.service;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.utils.PinYin4jUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.xiaomi.mone.log.common.Constant.COMMON_MQ_PREFIX;
import static com.xiaomi.mone.log.common.Constant.COMMON_MQ_SUFFIX;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:31
 */
public interface MqConfigService {

    default List<String> generateCommonTagTopicName(String orgId) {
        return IntStream.range(0, COMMON_MQ_SUFFIX.size()).mapToObj(value -> {
            String suffix = COMMON_MQ_SUFFIX.get(value);
            if (StringUtils.isNotBlank(orgId)) {
                return String.format("%s_%s_%s", COMMON_MQ_PREFIX, orgId, suffix);
            }
            return String.format("%s_%s", COMMON_MQ_PREFIX, suffix);
        }).collect(Collectors.toList());
    }

    default String generateSimpleTopicName(Long appId, String appName, String source, Long tailId) {
        if (StringUtils.isNotEmpty(appName)) {
            // 汉字转拼音
            appName = PinYin4jUtils.getAllPinyin(appName);
        }
        // 处理特殊字符
        List<String> collect = ReUtil.RE_KEYS.stream()
                .map(character -> character.toString()).collect(Collectors.toList());
        String topicName = String.format("%s_%s_%s_%s", appId, appName, tailId, source);
        topicName = StrUtil.removeAny(topicName, collect.toArray(new String[0]));
        return topicName;
    }

    default String generateSimpleTopicName(Long id, String name) {
        if (StringUtils.isNotEmpty(name)) {
            // 汉字转拼音
            name = PinYin4jUtils.getAllPinyin(name);
        }
        // 处理特殊字符
        List<String> collect = ReUtil.RE_KEYS.stream()
                .map(character -> character.toString()).collect(Collectors.toList());
        String topicName = String.format("%s_%s", id, name);
        topicName = StrUtil.removeAny(topicName, collect.toArray(new String[0]));
        return topicName;
    }

    MilogAppMiddlewareRel.Config generateConfig(String ak, String sk, String nameServer, String serviceUrl,
                                                String authorization, String orgId, String teamId, Long exceedId,
                                                String name, String source, Long id);

    List<DictionaryDTO> queryExistsTopic(String ak, String sk, String nameServer, String serviceUrl,
                                         String authorization, String orgId, String teamId);

    /**
     * 创建几个公共topic,且开启标签过滤
     *
     * @return
     */
    List<String> createCommonTagTopic(String ak, String sk, String nameServer, String serviceUrl,
                                      String authorization, String orgId, String teamId);
}
