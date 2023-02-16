package com.xiaomi.mone.log.manager.domain;

import com.google.gson.Gson;
import com.xiaomi.mone.log.manager.dao.LogstoreDao;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseGraphMapper;
import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogAnalyseGraphDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogAnalyseDataPreQuery;
import com.xiaomi.mone.log.manager.model.vo.LogAnalyseDataQuery;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class AnalyseLog {
    @Resource
    private LogstoreDao logstoreDao;

    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogAnalyseGraphMapper graphMapper;

    public LogAnalyseDataDTO getData(LogAnalyseDataPreQuery query) throws IOException {
        return getData(query.getStoreId(), query.getFieldName(), query.getTypeCode(), query.getStartTime(), query.getEndTime());
    }

    public LogAnalyseDataDTO getData(LogAnalyseDataQuery query) throws IOException {
        LogAnalyseGraphDO graph = graphMapper.selectById(query.getGraphId());
        return getData(graph.getStoreId(), graph.getFieldName(), graph.getGraphType(), query.getStartTime(), query.getEndTime());
    }

    private LogAnalyseDataDTO getData(Long stroeId, String fieldName, Integer graphType, Long startTime, Long endTime) throws IOException {
        if (stroeId == null || fieldName == null) {
            return null;
        }
        MilogLogStoreDO logStore = logstoreDao.queryById(stroeId);
        EsService esService = esCluster.getEsService(logStore.getEsClusterId());
        String esIndex = logStore.getEsIndex();
        // 过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timestamp").from(startTime).to(endTime));
        boolQueryBuilder.filter(QueryBuilders.termQuery("logstore", logStore.getLogstoreName()));

        // 聚合条件
        AggregationBuilder groupAggBuilder = AggregationBuilders.terms("group_aggs").size(4)
                .field(fieldName)
                .executionHint("map");  // 若可知该层聚合结果数量很小，设置成map可提升性能。

        // 构造查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(groupAggBuilder);
        searchSourceBuilder.timeout(new TimeValue(3000));
        SearchRequest request = new SearchRequest(esIndex);
        request.source(searchSourceBuilder);

        // 查询
        SearchResponse response  = esService.search(request);

        // 解析
        List<Map<String, Object>> dataArray = new ArrayList<>();
        Map<String, Object> ferray;
        if (Objects.nonNull(response) && Objects.equals(response.status(), RestStatus.OK)
            && Objects.nonNull(response.getAggregations()) && Objects.nonNull(response.getAggregations().get("group_aggs"))) {
                Terms groupResult = response.getAggregations().get("group_aggs");
                for (Terms.Bucket groupBucket : groupResult.getBuckets()) {
                    ferray = new HashMap<>();
                    Object key = groupBucket.getKey();
                    long docCount = groupBucket.getDocCount();
                    ferray.put("field", key);
                    ferray.put("count", docCount);
                    dataArray.add(ferray);
                }
            }
        return new LogAnalyseDataDTO(new Gson().toJson(dataArray));
    }



}
