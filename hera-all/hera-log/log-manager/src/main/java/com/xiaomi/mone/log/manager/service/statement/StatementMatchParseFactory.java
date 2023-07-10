/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.statement;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.mone.log.api.enums.EsOperatorEnum;
import com.xiaomi.mone.log.api.enums.EsOperatorMatchEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.api.enums.EsOperatorEnum.*;
import static com.xiaomi.mone.log.api.enums.EsOperatorMatchEnum.ALL_MATCH_OPERATOR;
import static com.xiaomi.mone.log.api.enums.EsOperatorMatchEnum.KV_MATCH_OPERATOR;
import static com.xiaomi.mone.log.common.Constant.SYMBOL_COLON;
import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.MatchKVPrefix;

public class StatementMatchParseFactory {

    private static final String LOG_LEVEL_KEY = "level";
    private static final String NOT_MATCH_KEY = " not ";
    private static final String AND_MATCH_KEY = " and ";
    private static final String OR_MATCH_KEY = " or ";
    public static final String DOUBLE_QUOTATION_MARK_SEPARATOR = "\"";

    private static Map<EsOperatorEnum, Map<EsOperatorMatchEnum, StatementMatchParse>> operateMatchMap = Maps.newHashMap();

    /**
     * 组装对应的关系
     */
    static {
        Map<EsOperatorMatchEnum, StatementMatchParse> andOperateMap = Maps.newHashMap();
        andOperateMap.put(ALL_MATCH_OPERATOR, new AndAllStatementMatchParse());
        andOperateMap.put(KV_MATCH_OPERATOR, new MustStatementMatchParse());

        Map<EsOperatorMatchEnum, StatementMatchParse> notOperateMap = Maps.newHashMap();
        notOperateMap.put(ALL_MATCH_OPERATOR, new NotAllStatementMatchParse());
        notOperateMap.put(KV_MATCH_OPERATOR, new MustNotStatementMatchParse());

        Map<EsOperatorMatchEnum, StatementMatchParse> orOperateMap = Maps.newHashMap();
        orOperateMap.put(ALL_MATCH_OPERATOR, new OrAllStatementMatchParse());
        orOperateMap.put(KV_MATCH_OPERATOR, new OrStatementMatchParse());

        operateMatchMap.put(AND_OPERATOR, andOperateMap);
        operateMatchMap.put(NOT_OPERATOR, notOperateMap);
        operateMatchMap.put(OR_OPERATOR, orOperateMap);
    }

    public static BoolQueryBuilder getStatementMatchParseQueryBuilder(String message, List<String> keyPrefixList) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<OperatorSlice> operatorSliceList = analyseTransformOs(message, keyPrefixList);
        List<OperatorData> operatorDataList = sliceTransformOd(operatorSliceList);
        for (OperatorData operatorData : operatorDataList) {
            BoolQueryBuilder queryBuilder = getSplitQueryBuilder(keyPrefixList, operatorData);
            boolQueryBuilder.filter(queryBuilder);
        }
        return boolQueryBuilder;
    }

    private static List<OperatorData> sliceTransformOd(List<OperatorSlice> operatorSliceList) {
        List<OperatorData> operatorSlices = Lists.newArrayList();
        Map<EsOperatorEnum, List<OperatorSlice>> operatorEnumListMap = operatorSliceList.stream().collect(Collectors.groupingBy(OperatorSlice::getOperatorEnum));
        for (Map.Entry<EsOperatorEnum, List<OperatorSlice>> enumListEntry : operatorEnumListMap.entrySet()) {
            operatorSlices.add(OperatorData.builder().operatorEnum(enumListEntry.getKey()).messageList(enumListEntry.getValue().stream().map(OperatorSlice::getMessage).collect(Collectors.toList())).build());
        }
        return operatorSlices;
    }

    private static BoolQueryBuilder getSplitQueryBuilder(List<String> keyPerfixList, OperatorData operatorSlice) {
        List<QueryEntity> allMatchEntities = Lists.newArrayList();
        List<QueryEntity> kvMatchEntities = Lists.newArrayList();
        filterMatchEntities(keyPerfixList, operatorSlice, kvMatchEntities, allMatchEntities);
        Map<EsOperatorMatchEnum, StatementMatchParse> matchParseMap = operateMatchMap.get(operatorSlice.getOperatorEnum());
        BoolQueryBuilder allMatchBuilder = new BoolQueryBuilder();
        BoolQueryBuilder kvMatchBuilder = new BoolQueryBuilder();
        if (CollectionUtils.isNotEmpty(allMatchEntities)) {
            allMatchBuilder = matchParseMap.get(allMatchEntities.get(0).getMatchEnum()).matchBuild(allMatchEntities);
        }
        if (CollectionUtils.isNotEmpty(kvMatchEntities)) {
            kvMatchBuilder = matchParseMap.get(kvMatchEntities.get(0).getMatchEnum()).matchBuild(kvMatchEntities);
        }
        if (CollectionUtils.isNotEmpty(allMatchEntities) && CollectionUtils.isNotEmpty(kvMatchEntities)) {
            allMatchBuilder.filter(kvMatchBuilder);
            return allMatchBuilder;
        }
        if (CollectionUtils.isNotEmpty(allMatchEntities) && CollectionUtils.isEmpty(kvMatchEntities)) {
            return allMatchBuilder;
        }
        if (CollectionUtils.isEmpty(allMatchEntities) && CollectionUtils.isNotEmpty(kvMatchEntities)) {
            return kvMatchBuilder;
        }
        return allMatchBuilder;
    }

    private static void filterMatchEntities(List<String> keyPerfixList, OperatorData operatorSlice, List<QueryEntity> kvMatchEntities, List<QueryEntity> allMatchEntities) {
        for (String message : operatorSlice.getMessageList()) {
            String kvPrefix = MatchKVPrefix(message, keyPerfixList);
            if (StringUtils.isNotBlank(kvPrefix)) {
                String field = StringUtils.substringBefore(kvPrefix, SYMBOL_COLON);
                String fieldValue = StrUtil.trimStart(StringUtils.substringAfter(message, SYMBOL_COLON));
//                if (Objects.equals(LOG_LEVEL_KEY, field)) {
//                    fieldValue = String.format("%-5s", fieldValue.trim());
//                }
                kvMatchEntities.add(QueryEntity.builder()
                        .field(field)
                        .fieldValue(fieldValue)
                        .matchEnum(KV_MATCH_OPERATOR)
                        .build());
            } else {
                allMatchEntities.add(QueryEntity.builder()
                        .fieldValue(StrUtil.trimStart(message))
                        .matchEnum(ALL_MATCH_OPERATOR)
                        .build());
            }
        }
    }

    public static List<OperatorSlice> analyseTransformOs(String message, List<String> keyPrefixList) {
        List<OperatorSlice> operatorSlices = Lists.newArrayList();
        String kvPrefix = MatchKVPrefix(message, keyPrefixList);
        String[] msgArrays = StringUtils.substringsBetween(message, "\"", "\"");
        if (StringUtils.isBlank(kvPrefix) && null != msgArrays && msgArrays.length == 1) {
            handleAndLogic(message, operatorSlices);
            return operatorSlices;
        }
        if (message.contains(AND_MATCH_KEY)) {
            handleAndLogic(message, operatorSlices);
        } else if (message.contains(NOT_MATCH_KEY)) {
            handleNotLogic(message, operatorSlices);
        } else if (message.contains(OR_MATCH_KEY)) {
            operatorSlicesAdd(operatorSlices, message, EsOperatorEnum.OR_OPERATOR);
        } else {
            buildOperatorToSlices(operatorSlices, EsOperatorEnum.AND_OPERATOR, message);
        }
        return operatorSlices;
    }

    /**
     * 处理包含and的逻辑
     *
     * @param message
     * @param operatorSlices
     */
    private static void handleAndLogic(String message, List<OperatorSlice> operatorSlices) {
        List<String> andList = splitBySeparator(message, AND_MATCH_KEY);
        for (String andPer : andList) {
            //判断是否是匹配到了关键字，如果是则需要
            if (!andPer.contains(NOT_MATCH_KEY) && !andPer.contains(OR_MATCH_KEY)) {
                buildOperatorToSlices(operatorSlices, AND_OPERATOR, andPer);
            } else if (andPer.contains(NOT_MATCH_KEY) && !andPer.contains(OR_MATCH_KEY)) {
                if (andPer.startsWith(NOT_MATCH_KEY)) {
                    operatorSlicesAdd(operatorSlices, andPer, NOT_OPERATOR);
                } else {
                    List<String> matchList = splitBySeparator(andPer, NOT_OPERATOR.getCode());
                    for (int i = 0; i < matchList.size(); i++) {
                        if (i == 0) {
                            buildOperatorToSlices(operatorSlices, AND_OPERATOR, matchList.get(i));
                        } else {
                            buildOperatorToSlices(operatorSlices, NOT_OPERATOR, matchList.get(i));
                        }
                    }
                }
            } else if (!andPer.contains(NOT_MATCH_KEY) && andPer.contains(OR_MATCH_KEY)) {
                if (andPer.startsWith(OR_MATCH_KEY)) {
                    operatorSlicesAdd(operatorSlices, andPer, OR_OPERATOR);
                } else {
                    List<String> matchList = splitBySeparator(andPer, OR_OPERATOR.getCode());
                    for (int i = 0; i < matchList.size(); i++) {
                        if (i == 0) {
                            buildOperatorToSlices(operatorSlices, AND_OPERATOR, matchList.get(i));
                        } else {
                            buildOperatorToSlices(operatorSlices, OR_OPERATOR, matchList.get(i));

                        }
                    }
                }
            }
        }
    }

    /**
     * 处理不包含and只包含not的逻辑
     *
     * @param message
     * @param operatorSlices
     */
    private static void handleNotLogic(String message, List<OperatorSlice> operatorSlices) {
        List<String> notList = splitBySeparator(message, NOT_MATCH_KEY);
        for (int i = 0; i < notList.size(); i++) {
            String notPer = notList.get(i);
            if (notList.size() > 1 && i == 0) {
                if (!notPer.contains(OR_MATCH_KEY)) {
                    buildOperatorToSlices(operatorSlices, EsOperatorEnum.AND_OPERATOR, notPer);
                } else {
                    operatorSlicesAdd(operatorSlices, notPer, EsOperatorEnum.OR_OPERATOR);
                }
            } else {
                if (!notPer.contains(OR_MATCH_KEY)) {
                    buildOperatorToSlices(operatorSlices, EsOperatorEnum.NOT_OPERATOR, notPer);
                } else {
                    operatorSlicesAdd(operatorSlices, notPer, EsOperatorEnum.OR_OPERATOR);
                }
            }
        }
    }

    public static List<String> splitBySeparator(String message, String separator) {
        List<String> sparMsgLists = Lists.newArrayList();
        while (StringUtils.isNotBlank(message)) {
            int startIndex = 0;
            int endIndex;
            if (message.startsWith(DOUBLE_QUOTATION_MARK_SEPARATOR)) {
                endIndex = message.indexOf(separator, message.indexOf(DOUBLE_QUOTATION_MARK_SEPARATOR, 1));
            } else {
                endIndex = message.indexOf(separator);
            }
            if (endIndex == -1) {
                endIndex = message.length();
            }
            sparMsgLists.add(StringUtils.substring(message, startIndex, endIndex));
            message = StringUtils.substring(message, endIndex + separator.length());
        }
        return sparMsgLists.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    private static void operatorSlicesAdd(List<OperatorSlice> operatorSlices, String message, EsOperatorEnum operatorEnum) {
        List<String> matchList = splitBySeparator(message, operatorEnum.getCode());
        for (String matchPer : matchList) {
            buildOperatorToSlices(operatorSlices, operatorEnum, matchPer);
        }
    }

    private static void buildOperatorToSlices(List<OperatorSlice> operatorSlices, EsOperatorEnum operatorEnum, String matchPer) {
        operatorSlices.add(OperatorSlice.builder().operatorEnum(operatorEnum).message(matchPer).build());
    }

}
