package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.model.StatisticsQuery;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsServiceImpl {


    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private EsCluster esCluster;


    /**
     * 单个tail每小时数据量统计
     */
    public Result<Map<String, Long>> queryTailStatisticsByHour(StatisticsQuery statisticsQuery) throws IOException {

        Map<String, Long> result = new LinkedHashMap<>();
        if (statisticsQuery.getStartTime() == null || statisticsQuery.getStartTime() == 0) {
            statisticsQuery.setStartTime(Utils.getTodayTime().get("start"));
            statisticsQuery.setEndTime(Utils.getTodayTime().get("end"));
        }
        MilogLogStoreDO milogLogstoreDO = logstoreDao.queryById(statisticsQuery.getLogstoreId());
        if (milogLogstoreDO == null) {
            return Result.success();
        }
        EsService esService = esCluster.getEsService(milogLogstoreDO.getEsClusterId());
        String esIndexName = milogLogstoreDO.getEsIndex();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders
                .rangeQuery("timestamp")
                .gte(statisticsQuery.getStartTime())
                .lte(statisticsQuery.getEndTime()));
        boolQueryBuilder.filter(QueryBuilders.termQuery("logstore", milogLogstoreDO.getLogstoreName()));
        boolQueryBuilder.filter(QueryBuilders.termQuery("tail", statisticsQuery.getTail()));
        AbstractAggregationBuilder aggregation = AggregationBuilders
                .dateHistogram("agg")
                .field("timestamp")
                .calendarInterval(DateHistogramInterval.hours(1))
                .format("yyyy-MM-dd HH:mm:ss")
                .timeZone(ZoneId.of("+08:00"))
                .minDocCount(0L);
        builder.query(boolQueryBuilder);
        builder.aggregation(aggregation);
        SearchRequest searchRequest = new SearchRequest(esIndexName);
        searchRequest.source(builder);
        SearchResponse searchResponse = esService.search(searchRequest);
        ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) searchResponse.getAggregations().asMap().get("agg");
        for (Histogram.Bucket bucket : parsedDateHistogram.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return Result.success(result);
    }


    /**
     * 当日单个store内所有tail的数据量排序top5
     */
    public Result<Map<String, Long>> queryStoreTopTailStatisticsByDay(StatisticsQuery statisticsQuery) throws IOException {

        if (statisticsQuery.getStartTime() == null || statisticsQuery.getStartTime() == 0) {
            statisticsQuery.setStartTime(Utils.getTodayTime().get("start"));
            statisticsQuery.setEndTime(Utils.getTodayTime().get("end"));
        }
        MilogLogStoreDO logstoreDO = logstoreDao.queryById(statisticsQuery.getLogstoreId());
        if (logstoreDO == null) {
            return Result.success();
        }
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders
                .rangeQuery("timestamp")
                .from(statisticsQuery.getStartTime())
                .to(statisticsQuery.getEndTime()));
        boolQueryBuilder.filter(QueryBuilders
                .termQuery("logstore", logstoreDO.getLogstoreName()));
        TermsAggregationBuilder tailAggr = AggregationBuilders.terms("tail");
        tailAggr.field("tail");
        tailAggr.order(BucketOrder.count(false));
        searchSourceBuilder.query(boolQueryBuilder).aggregation(tailAggr).size(0);
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(logstoreDO.getEsIndex());
        EsService esService = esCluster.getEsService(logstoreDO.getEsClusterId());
        SearchResponse searchResponse = esService.search(searchRequest);
        ParsedStringTerms aggregations = searchResponse.getAggregations().get("tail");
        Map<String, Long> result = new LinkedHashMap<>();
        for (Terms.Bucket bucket : aggregations.getBuckets()) {
            result.put(String.valueOf(bucket.getKey()), bucket.getDocCount());
        }
        return Result.success(result);
    }

    /**
     * 单个space内所有store数据量排序top5
     */
    public Result<Map<String, Long>> querySpaceTopStoreByDay(StatisticsQuery statisticsQuery) throws IOException {

        Map<String, Long> result = new LinkedHashMap<>();
        if (statisticsQuery.getStartTime() == null || statisticsQuery.getStartTime() == 0) {
            statisticsQuery.setStartTime(Utils.getTodayTime().get("start"));
            statisticsQuery.setEndTime(Utils.getTodayTime().get("end"));
        }
        List<MilogLogStoreDO> logstoreList = logstoreDao.getMilogLogstoreBySpaceId(statisticsQuery.getSpaceId());
        if (null != logstoreList && logstoreList.size() > 0) {
            for (MilogLogStoreDO logstoreDO : logstoreList) {
                SearchRequest searchRequest = new SearchRequest();
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                boolQueryBuilder.filter(QueryBuilders
                        .rangeQuery("timestamp")
                        .from(statisticsQuery.getStartTime())
                        .to(statisticsQuery.getEndTime()));
                boolQueryBuilder.filter(QueryBuilders
                        .termQuery("logstore", logstoreDO.getLogstoreName()));
                TermsAggregationBuilder tailAggr = AggregationBuilders.terms("logstore");
                tailAggr.field("logstore");
                tailAggr.order(BucketOrder.count(false));
                searchSourceBuilder.query(boolQueryBuilder).aggregation(tailAggr).size(0);
                searchRequest.source(searchSourceBuilder);
                searchRequest.indices(logstoreDO.getEsIndex());
                EsService esService = esCluster.getEsService(logstoreDO.getEsClusterId());
                SearchResponse searchResponse = esService.search(searchRequest);
                ParsedStringTerms aggregations = searchResponse.getAggregations().get("logstore");
                for (Terms.Bucket bucket : aggregations.getBuckets()) {
                    result.put(String.valueOf(bucket.getKey()), bucket.getDocCount());
                }
            }
        }
        return Result.success(result);
    }
}
