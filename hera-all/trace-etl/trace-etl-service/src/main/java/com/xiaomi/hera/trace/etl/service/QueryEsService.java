package com.xiaomi.hera.trace.etl.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.hera.trace.etl.common.TimeConverter;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerAttribute;
import com.xiaomi.hera.trace.etl.domain.jaegeres.JaegerProcess;
import com.xiaomi.hera.trace.etl.domain.tracequery.Span;
import com.xiaomi.hera.trace.etl.domain.tracequery.Trace;
import com.xiaomi.hera.trace.etl.domain.tracequery.TraceIdQueryVo;
import com.xiaomi.hera.trace.etl.domain.tracequery.TraceListQueryVo;
import com.xiaomi.hera.trace.etl.domain.tracequery.TraceQueryResult;
import com.xiaomi.mone.es.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 11:40 上午
 */
@Slf4j
public class QueryEsService {

    private EsClient esClient;

    private static final String SOURCE = "HERA";
    private static final String AREA = "all";

    public static final String TRACE_ID = "traceID";
    private static final String SERVICE_NAME = "serviceName";
    private static final String SERVICE_ENV = "service.env";
    private static final String PROCESS_SERVICE_NAME = "process.serviceName";
    private static final String OPERATION_NAME = "operationName";
    public static final String START_TIME_MILLIS = "startTimeMillis";
    public static final String START_TIME = "startTime";
    public static final String DURATION = "duration";
    public static final String TAGS = "tags";
    public static final String NESTED_PROCESS_TAGS = "process.tags";
    public static final String NESTED_LOG_FIELDS = "logs.fields";
    public static final String TAG_KEY = "key";
    public static final String TAG_VALUE = "value";
    public static final String[] NESTED_TAG_FIELD_LIST = new String[]{TAGS, NESTED_PROCESS_TAGS, NESTED_LOG_FIELDS};

    private final static Gson GSON = new GsonBuilder().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();

    public QueryEsService(EsClient esClient) {
        this.esClient = esClient;
    }

    public TraceQueryResult<List<String>> getOperations(String service, String index) {
        try {
            log.info("search operations by serviceName param : service=" + service + " index=" + index);
            SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
            sourceBuilder.size(0);
            sourceBuilder.query(QueryBuilders.termQuery(SERVICE_NAME, service));
            TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(OPERATION_NAME)
                    .field(OPERATION_NAME)
                    .size(10000);
            sourceBuilder.aggregation(aggregationBuilder);
            SearchResponse response = esClient.search(buildSearchRequest(sourceBuilder, index));
            Terms terms = response.getAggregations().get(OPERATION_NAME);
            List<String> services = new ArrayList<>();
            for (Terms.Bucket termsBucket : terms.getBuckets()) {
                services.add(termsBucket.getKeyAsString());
            }
            return new TraceQueryResult<>(services, services.size());
        } catch (Throwable t) {
            log.error("search operations error : ", t);
        }
        return null;
    }

    public TraceQueryResult<List<Trace>> getList(TraceListQueryVo vo) {
        try {
            log.info("search trace list param : " + vo);
            SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            sourceBuilder.query(boolQueryBuilder);
            long startTime = vo.getStart() == null ? 0 : TimeUnit.MICROSECONDS.toMillis(vo.getStart());
            long endTime = vo.getEnd() == null ? 0 : TimeUnit.MICROSECONDS.toMillis(vo.getEnd());
            long minDuration = StringUtils.isEmpty(vo.getMinDuration()) ? 0 : TimeConverter.getMicro(vo.getMinDuration());
            long maxDuration = StringUtils.isEmpty(vo.getMaxDuration()) ? 0 : TimeConverter.getMicro(vo.getMaxDuration());
            List<JaegerAttribute> tags = getTags(vo.getTags());
            // deal serverEnv
            tags = dealServerEnv(tags, vo.getServerEnv());
            if (startTime != 0 && endTime != 0) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(START_TIME_MILLIS).gte(startTime).lte(endTime));
            }
            if (minDuration != 0 || maxDuration != 0) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(DURATION);
                if (minDuration != 0) {
                    rangeQueryBuilder.gte(minDuration);
                }
                if (maxDuration != 0) {
                    rangeQueryBuilder.lte(maxDuration);
                }
                boolQueryBuilder.must(rangeQueryBuilder);
            }
            if (!Strings.isNullOrEmpty(vo.getOperation())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery(OPERATION_NAME, vo.getOperation()));
            }
            if (StringUtils.isNotEmpty(vo.getService())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery(PROCESS_SERVICE_NAME, vo.getService()));
            }
            if (tags != null && tags.size() > 0) {
                for (JaegerAttribute tag : tags) {
                    boolQueryBuilder.must(buildTagQuery(tag));
                }
            }
            TermsAggregationBuilder builder = AggregationBuilders.terms(TRACE_ID)
                    .size(vo.getLimit())
                    .order(BucketOrder.aggregation(START_TIME, false))
                    .field(TRACE_ID)
                    .subAggregation(AggregationBuilders.max(START_TIME).field(START_TIME));
            sourceBuilder.aggregation(builder);
            // get traceIds
            SearchResponse response = esClient.search(buildSearchRequest(sourceBuilder, TimeConverter.getIndexNamesByTimes(vo.getIndex(), startTime, endTime)));
            Terms terms = response.getAggregations().get(TRACE_ID);
            List<String> traceIds = new ArrayList<>(20);
            for (Terms.Bucket termsBucket : terms.getBuckets()) {
                traceIds.add(termsBucket.getKeyAsString());
            }
            List<Trace> traces = queryMultiTraceSpans(traceIds, startTime, endTime, vo.getIndex());
            return new TraceQueryResult<>(traces, traces.size());
        } catch (Throwable t) {
            log.error("search traces from es error : ", t);
        }
        return null;
    }


    public TraceQueryResult<List<Trace>> getByTraceId(String traceId, TraceIdQueryVo vo) {
        long startTime = vo.getStartTime() == null ? 0 : vo.getStartTime();
        long endTime = vo.getEndTime() == null ? 0 : vo.getEndTime();
        log.info("search by traceId param : " + vo + ", traceId : " + traceId);
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        sourceBuilder.query(QueryBuilders.termQuery(TRACE_ID, traceId).boost(2));
        sourceBuilder.sort(START_TIME, SortOrder.ASC);
        sourceBuilder.size(1000);
        try {
            SearchResponse response = esClient.search(buildSearchRequest(sourceBuilder, TimeConverter.getIndexNamesByTimes(vo.getIndex(), startTime, endTime)));
            List<Span> jaegerSpans = new ArrayList<>(response.getHits().getHits().length);
            for (SearchHit searchHit : response.getHits().getHits()) {
                Span jaegerSpanInDB = GSON.fromJson(new InputStreamReader(searchHit.getSourceRef().streamInput()), Span.class);
                complateSpan(jaegerSpanInDB);
                jaegerSpans.add(jaegerSpanInDB);
            }
            Trace trace = getTrace(jaegerSpans);
            return new TraceQueryResult<>(Collections.singletonList(trace), 1);
        } catch (Throwable t) {
            log.error("search trace by traceId error : ", t);
        }
        return null;
    }

    private List<Trace> queryMultiTraceSpans(List<String> traceIds, long startTime, long endTime, String index) throws IOException {
        if (traceIds == null || traceIds.size() == 0) {
            return new ArrayList<>();
        }
        MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
        for (String traceId : traceIds) {
            SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            sourceBuilder.query(boolQueryBuilder);
            boolQueryBuilder.must(QueryBuilders.termQuery(TRACE_ID, traceId).boost(2));
            sourceBuilder.size(1000);
            sourceBuilder.terminateAfter(1000);
            sourceBuilder.sort(START_TIME_MILLIS);
            SearchRequest searchRequest = buildSearchRequest(sourceBuilder, TimeConverter.getIndexNamesByTimes(index, startTime, endTime));
            searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
            multiSearchRequest.add(searchRequest);
        }
        MultiSearchResponse tracesResp = esClient.getEsOriginalClient().msearch(multiSearchRequest, RequestOptions.DEFAULT);
        MultiSearchResponse.Item[] responseItems = tracesResp.getResponses();
        List<Trace> jaegerTraces = new ArrayList<>(responseItems.length);
        for (MultiSearchResponse.Item responseItem : responseItems) {
            SearchResponse response = responseItem.getResponse();
            List<Span> jaegerSpans = new ArrayList<>(response.getHits().getHits().length);
            for (int i = 0; i < response.getHits().getHits().length; i++) {
                Span jaegerSpanInDB = GSON.fromJson(new InputStreamReader(response.getHits().getAt(i).getSourceRef().streamInput()), Span.class);
                complateSpan(jaegerSpanInDB);
                jaegerSpans.add(jaegerSpanInDB);
            }
            jaegerTraces.add(getTrace(jaegerSpans));
        }
        return jaegerTraces;
    }

    private Trace getTrace(List<Span> spans) {
        Trace trace = new Trace();
        trace.setSpans(spans);
        trace.setTraceID(spans.get(0).getTraceID());
        trace.setProcesses(getProcess(spans));
        trace.setSource(SOURCE);
        trace.setArea(AREA);
        return trace;
    }

    private void complateSpan(Span span){
        span.setProcessID(span.getProcess().getServiceName());
    }

    private Map<String, JaegerProcess> getProcess(List<Span> spans) {
        Map<String, JaegerProcess> result = new HashMap<>();
        for (Span span : spans) {
            result.put(span.getProcess().getServiceName(), span.getProcess());
        }
        return result;
    }

    private List<JaegerAttribute> getTags(String tags) {
        if (StringUtils.isEmpty(tags)) {
            return null;
        }
        List<JaegerAttribute> jaegerAttributes = new ArrayList<>();
        try {
            tags = URLDecoder.decode(tags, "UTF-8");
            Map<String, String> tagMap = GSON.fromJson(tags, MAP_TYPE);
            for (String key : tagMap.keySet()) {
                JaegerAttribute attr = new JaegerAttribute();
                attr.setKey(key);
                attr.setValue(tagMap.get(key));
                jaegerAttributes.add(attr);
            }
        } catch (Throwable t) {
            log.error("parse String tags to JaegerAttribute error : ", t);
        }
        return jaegerAttributes;
    }

    private List<JaegerAttribute> dealServerEnv(List<JaegerAttribute> tags, String serverEnv){
        if(StringUtils.isEmpty(serverEnv)){
            return tags;
        }
        if(tags == null){
            tags = new ArrayList<>();
        }
        JaegerAttribute attr = new JaegerAttribute();
        attr.setKey(SERVICE_ENV);
        attr.setValue(serverEnv);
        tags.add(attr);
        return tags;
    }

    private BoolQueryBuilder buildTagQuery(JaegerAttribute tag) {
        BoolQueryBuilder tagBoolQueryBuilder = QueryBuilders.boolQuery();
        for (String nestedTagField : NESTED_TAG_FIELD_LIST) {
            MatchQueryBuilder tagKeyQuery = QueryBuilders.matchQuery(
                    String.format("%s.%s", nestedTagField, TAG_KEY), tag.getKey());
            RegexpQueryBuilder tagValueQuery = QueryBuilders.regexpQuery(
                    String.format("%s.%s", nestedTagField, TAG_VALUE), tag.getValue());
            tagBoolQueryBuilder.should(QueryBuilders.nestedQuery(nestedTagField,
                    QueryBuilders.boolQuery().must(tagKeyQuery).must(tagValueQuery), ScoreMode.Avg));
        }
        return tagBoolQueryBuilder;
    }

    private SearchRequest buildSearchRequest(SearchSourceBuilder sourceBuilder, String... index) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

}
