package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.es.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.PagedResp;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReqRespLogRecord;
import run.mone.mimeter.dashboard.bo.report.SearchApiLogReq;
import run.mone.mimeter.dashboard.service.EsLogService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.search.sort.SortOrder.ASC;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/6
 */
@Slf4j
@Service
public class EsLogServiceImpl implements EsLogService {

    @Autowired
    private EsClient esClient;

    @Value("${es.index.apilog}")
    private String esIndexApilog;

    @Override
    public Result<PagedResp<List<ReqRespLogRecord>>> searchApiLogs(SearchApiLogReq req) {
        try {
            SearchRequest searchRequest = new SearchRequest(esIndexApilog);
            SearchSourceBuilder qb = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            if (req.getStartTs() != null  && req.getStartTs() > 0) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp").from(req.getStartTs());
                boolQueryBuilder = boolQueryBuilder.must(rangeQueryBuilder);
            }
            if (req.getEndTs() != null && req.getEndTs() > 0) {
                RangeQueryBuilder rangeQueryBuilder1 = QueryBuilders.rangeQuery("timestamp").to(req.getEndTs());
                boolQueryBuilder = boolQueryBuilder.must(rangeQueryBuilder1);
            }
            if (StringUtils.isNotEmpty(req.getApiUri())) {
                TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("uri", StringUtils.trim(req.getApiUri()));
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder2);
            }

            if (req.getSceneId() != null && req.getSceneId() > 0) {
                TermQueryBuilder termQueryBuilder3 = QueryBuilders.termQuery("sceneId", req.getSceneId());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder3);
            }
            if (req.getRtMin() != null && req.getRtMin() > 0) {
                RangeQueryBuilder rangeQueryBuilder4 = QueryBuilders.rangeQuery("rt").from(req.getRtMin());
                boolQueryBuilder = boolQueryBuilder.must(rangeQueryBuilder4);
            }
            if (req.getRtMax() != null && req.getRtMax() > 0) {
                RangeQueryBuilder rangeQueryBuilder5 = QueryBuilders.rangeQuery("rt").to(req.getRtMax());
                boolQueryBuilder = boolQueryBuilder.must(rangeQueryBuilder5);
            }
            if (req.getRespCode() != null && req.getRespCode() > 0) {
                TermQueryBuilder termQueryBuilder6 = QueryBuilders.termQuery("code", req.getRespCode());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder6);
            }
            if (req.getFailed() != null) {
                TermQueryBuilder termQueryBuilder7 = QueryBuilders.termQuery("failed", req.getFailed());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder7);
            }
            if (StringUtils.isNotBlank(req.getReportId())) {
                TermQueryBuilder termQueryBuilder8 = QueryBuilders.termQuery("reportId", req.getReportId());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder8);
            }
            if (req.getApiId() != null && req.getApiId() > 0) {
                TermQueryBuilder termQueryBuilder9 = QueryBuilders.termQuery("apiId", req.getApiId());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder9);
            }
            if (StringUtils.isNotEmpty(req.getApiMethod())) {
                TermQueryBuilder termQueryBuilder10 = QueryBuilders.termQuery("method", StringUtils.trim(req.getApiMethod()));
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder10);
            }
            if (req.getSerialId() != null && req.getSerialId() > 0) {
                TermQueryBuilder termQueryBuilder11 = QueryBuilders.termQuery("serialId", req.getSerialId());
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder11);
            }
            if (StringUtils.isNotEmpty(req.getTraceId())) {
                TermQueryBuilder termQueryBuilder12 = QueryBuilders.termQuery("traceId", StringUtils.trim(req.getTraceId()));
                boolQueryBuilder = boolQueryBuilder.must(termQueryBuilder12);
            }

            qb.query(boolQueryBuilder);
            qb.from((req.getPageNo() - 1) * req.getPageSize())
                    .size(req.getPageSize())
                    .timeout(new TimeValue(3000));

            qb = qb.sort("timestamp", ASC);
            searchRequest.source(qb);
            SearchResponse res = esClient.search(searchRequest);
            SearchHits searchHits = res.getHits();
            long count = searchHits.getTotalHits().value;

            List<ReqRespLogRecord> records = new ArrayList<>();
            Gson gson = new Gson();
            Arrays.stream(searchHits.getHits()).forEach(it -> {
                String jsonSource = it.getSourceAsString();
                ReqRespLogRecord reqRespLogRecord = gson.fromJson(jsonSource, ReqRespLogRecord.class);
                records.add(reqRespLogRecord);
            });

            PagedResp<List<ReqRespLogRecord>> apilogRes = new PagedResp();
            apilogRes.setTotal(count);
            apilogRes.setData(records);

            return Result.success(apilogRes);

        } catch (Exception e) {
            log.error("ApiStatServiceImpl.searchApiLogs error, ", e);
            return null;
        }
    }
}
