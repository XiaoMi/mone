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
package com.xiaomi.mone.log.manager.domain;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.mapper.MilogEsIndexMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsIndexDO;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TraceLog {
    private static final String TIME_STAMP = "timestamp";
    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogEsIndexMapper esIndexMapper;

    public TraceLogDTO getTraceLog(String traceId, String region, String generationTime, String level) throws IOException {
        if (StringUtils.isEmpty(traceId)) {
            return null;
        }
        SearchSourceBuilder qb = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = getBoolQueryBuilder(traceId, generationTime, level);
        qb.query(queryBuilder);
        List<MilogEsIndexDO> indexList;
        if (StringUtils.isEmpty(region)) {
            // Region is empty to query all regions in the country
            indexList = esIndexMapper.selectAreaIndexList("cn");
        } else {
            indexList = esIndexMapper.selectRegionIndexList(region);
        }
        if (indexList == null || indexList.isEmpty()) {
            return TraceLogDTO.emptyData();
        }
        List<ClusterIndexVO> clusterIndexVOS = indexList.stream()
                .map(MilogEsIndexDO::toClusterIndexVO).distinct().collect(Collectors.toList());
        return EsAsyncSearch(clusterIndexVOS, qb);
    }

    @NotNull
    private BoolQueryBuilder getBoolQueryBuilder(String traceId, String generationTime, String level) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.termQuery("traceId", traceId));
        if (StringUtils.isNotBlank(level)) {
            queryBuilder.filter(QueryBuilders.termQuery("level", level));
        }
        if (StringUtils.isNotEmpty(generationTime)) {
            long time = new DateTime(generationTime).getTime();
            long startTime = time - TimeUnit.HOURS.toMillis(1);
            long endTime = time + TimeUnit.HOURS.toMillis(1);
            queryBuilder.filter(QueryBuilders.rangeQuery("timestamp").from(startTime).to(endTime));
        }
        return queryBuilder;
    }

    public TraceLogDTO EsAsyncSearch(List<ClusterIndexVO> indexList, SearchSourceBuilder qb) {
        CountDownLatch countDownLatch = new CountDownLatch(indexList.size());
        AsyncSearchObj asyncSearchObj = new AsyncSearchObj(countDownLatch);
        for (ClusterIndexVO esIndexDO : indexList) {
            EsService esService = esCluster.getEsService(esIndexDO.getClusterId());
            if (esService == null) {
                countDownLatch.countDown();
                log.warn("[Esdata.getTraceLog] es client [{}] is not generated", Constant.ES_SERV_BEAN_PRE + esIndexDO.getClusterId());
                continue;
            }
            SearchRequest searchRequest = new SearchRequest(esIndexDO.getIndexName());
            searchRequest.source(qb);
            esService.searchAsync(searchRequest, asyncSearchObj.getListenerStack().pop());
        }
        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Log query error, query trace log error countdownlatch timeout, error is [{}]", e.getMessage(), e);
        }
        Set<String> logSet = asyncSearchObj.getLogSet();
        return new TraceLogDTO(sortedLogWithTime(logSet));
    }

    @Nullable
    private Set<String> sortedLogWithTime(Set<String> logSet) {
        if (CollectionUtils.isNotEmpty(logSet)) {
            logSet = logSet.stream().sorted((o1, o2) -> {
                try {
                    JSONObject obj1 = JSON.parseObject(String.valueOf(o1));
                    JSONObject obj2 = JSON.parseObject(String.valueOf(o2));
                    if (obj1.getLong(TIME_STAMP) == null) {
                        return 1;
                    }
                    if (obj2.getLong(TIME_STAMP) == null) {
                        return -1;
                    }
                    return obj2.getLong(TIME_STAMP).compareTo(obj1.getLong(TIME_STAMP));
                } catch (Exception e) {
                    log.error("compare exception", e);
                }
                return 0;
            }).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return logSet;
    }

    @Data
    private class AsyncSearchObj {
        private Set<String> logSet;
        private Stack<ActionListener<SearchResponse>> listenerStack = new Stack<>();

        public AsyncSearchObj(CountDownLatch countDownLatch) {
            this.logSet = Collections.synchronizedSet(new TreeSet<>(new Comparator<String>() {
                Gson gson = new Gson();

                @Override
                public int compare(String o1, String o2) {
                    if (gson.fromJson(o1, Map.class).get("timestamp") == null) {
                        return 1;
                    }
                    if (gson.fromJson(o2, Map.class).get("timestamp") == null) {
                        return -1;
                    }
                    int res = (int) (Double.parseDouble(String.valueOf(gson.fromJson(o1, Map.class).get("timestamp"))) - Double.parseDouble((String.valueOf(gson.fromJson(o2, Map.class).get("timestamp")))));
                    if (res == 0) {
                        // return (int) (Double.parseDouble(String.valueOf(gson.fromJson(o1, Map.class).get("_id"))) - Double.parseDouble((String.valueOf(gson.fromJson(o2, Map.class).get("_id")))));
                        return 1;
                    }
                    return res;
                }
            }));
            for (int i = 0; i < countDownLatch.getCount(); i++) {
                ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(SearchResponse searchResponse) {
                        SearchHit[] hits = searchResponse.getHits().getHits();
                        if (hits == null || hits.length == 0) {
                            countDownLatch.countDown();
                            return;
                        }
                        for (SearchHit hit : hits) {
                            logSet.add(hit.getSourceAsString());
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        countDownLatch.countDown();
                        log.error("[Esdata.getTraceLog] search has failure, error is [{}]", e.getMessage());
                    }
                };
                listenerStack.push(listener);
            }
        }
    }

}