package com.xiaomi.mone.log.manager.domain;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.mapper.MilogEsIndexMapper;
import com.xiaomi.mone.log.manager.model.pojo.LogEsIndexDO;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TraceLog {
    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogEsIndexMapper esIndexMapper;

    public TraceLogDTO getTraceLog(String traceId, String region) throws IOException {
        if (StringUtils.isEmpty(traceId)) {
            return null;
        }
        SearchSourceBuilder qb = new SearchSourceBuilder();
        qb.query(QueryBuilders.termQuery("traceId", traceId));
        List<LogEsIndexDO> indexList;
        if (StringUtils.isEmpty(region)) { // region为空查询国内所有region
            indexList = esIndexMapper.selectAreaIndexList("cn");
        } else {
            indexList = esIndexMapper.selectRegionIndexList(region);
        }
        if (indexList == null || indexList.isEmpty()) {
            return TraceLogDTO.emptyData();
        }
        CountDownLatch countDownLatch = new CountDownLatch(indexList.size());
        AsyncSearchObj asyncSearchObj = new AsyncSearchObj(countDownLatch);
        for (LogEsIndexDO esIndexDO : indexList) {
            EsService esService = esCluster.getEsService(esIndexDO.getClusterId());
            if (esService == null) {
                countDownLatch.countDown();
                log.warn("[Esdata.getTraceLog] es客户端[{}]未生成", Constant.ES_SERV_BEAN_PRE + esIndexDO.getClusterId());
                continue;
            }
            SearchRequest searchRequest = new SearchRequest(esIndexDO.getIndexName());
            searchRequest.source(qb);
            esService.searchAsync(searchRequest, asyncSearchObj.getListenerStack().pop());
        }
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("日志查询错误, 查询trace日志报错 countdownlatch timeout, error is [{}]", e.getMessage());
        }
        return new TraceLogDTO(asyncSearchObj.getLogSet());
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
                        // TODO 比较ID
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