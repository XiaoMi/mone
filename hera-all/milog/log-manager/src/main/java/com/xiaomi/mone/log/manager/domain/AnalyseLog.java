package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.domain.analyse.AggrCalcu;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseGraphMapper;
import com.xiaomi.mone.log.manager.model.bo.CalcuAggrParam;
import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAnalyseGraphDO;
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
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AnalyseLog {
    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogAnalyseGraphMapper graphMapper;

    @Resource
    private AggrCalcu aggrCalcu;

    public LogAnalyseDataDTO getData(LogAnalyseDataPreQuery query) throws IOException {
        return getData(query.getStoreId(), query.getFieldName(), query.getTypeCode(), query.getGraphParam(), query.getStartTime(), query.getEndTime());
    }

    public LogAnalyseDataDTO getData(LogAnalyseDataQuery query) throws IOException {
        MilogAnalyseGraphDO graph = graphMapper.selectById(query.getGraphId());
        return getData(graph.getStoreId(), graph.getFieldName(), graph.getGraphType(), graph.getGraphParam(), query.getStartTime(), query.getEndTime());
    }

    private LogAnalyseDataDTO getData(Long storeId, String fieldName, Integer graphType, String graphParam, Long startTime, Long endTime) throws IOException {
        if (storeId == null || fieldName == null) {
            return null;
        }
        MilogLogStoreDO logStore = logstoreDao.queryById(storeId);
        EsService esService = esCluster.getEsService(logStore.getEsClusterId());
        String esIndex = logStore.getEsIndex();

        // 过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timestamp").from(startTime).to(endTime - 1000));
        boolQueryBuilder.filter(QueryBuilders.termQuery("logstore", logStore.getLogstoreName()));

        AggregationBuilder aggrs = aggrCalcu.getAggr(new CalcuAggrParam(graphType, graphParam, fieldName, startTime, endTime));
        if (aggrs == null) {
            return null;
        }

        // 构造查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(aggrs);
        searchSourceBuilder.timeout(new TimeValue(1, TimeUnit.MINUTES));
        SearchRequest request = new SearchRequest(esIndex);
        request.source(searchSourceBuilder);

        // 查询
        SearchResponse response = esService.search(request);

        return aggrCalcu.formatRes(graphType, response);
    }

}
