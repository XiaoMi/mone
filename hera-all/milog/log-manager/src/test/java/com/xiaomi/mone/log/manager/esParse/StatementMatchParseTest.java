package com.xiaomi.mone.log.manager.esParse;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.service.statement.AndAllStatementMatchParse;
import com.xiaomi.mone.log.manager.service.statement.MustStatementMatchParse;
import com.xiaomi.mone.log.manager.service.statement.QueryEntity;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class StatementMatchParseTest {

    EsService esService;
    AndAllStatementMatchParse andAllStatementMatchParse;

    MustStatementMatchParse mustStatementMatchParse;

    @Before
    public void initService() {
        esService = new EsService("zjynewretail.api.es.srv:80", "zgq_xiaoneng_7788", "zgq@mione#99");
        andAllStatementMatchParse = new AndAllStatementMatchParse();
        mustStatementMatchParse = new MustStatementMatchParse();
    }

    @Test
    public void testMatchAllQuery() throws IOException {
        List<QueryEntity> arrays = Lists.newArrayList();
        arrays.add(QueryEntity.builder().field("").fieldValue("getGoodsTradeInfoFromCache").build());
        arrays.add(QueryEntity.builder().field("").fieldValue("hit size 50").build());
        BoolQueryBuilder queryBuilder = andAllStatementMatchParse.matchBuild(arrays);

        SearchRequest searchRequest = getSearchRequest(queryBuilder);
        SearchResponse searchResponse = esService.search(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> result = hit.getSourceAsMap();
            log.info("result:{}", result);
        }
    }

    @NotNull
    private static SearchRequest getSearchRequest(BoolQueryBuilder queryBuilder) {
        String esIndexName = "zgq_common_milog_staging_app_private_1";
        SearchRequest searchRequest = new SearchRequest(esIndexName);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(queryBuilder);
        searchRequest.source(builder);
        return searchRequest;
    }

    @Test
    public void testMustMatchQuery() throws IOException {
        List<QueryEntity> arrays = Lists.newArrayList();
        arrays.add(QueryEntity.builder().field("message").fieldValue("getGoodsTradeInfoFromCache").build());
        arrays.add(QueryEntity.builder().field("message").fieldValue("hit size 50").build());
        arrays.add(QueryEntity.builder().field("linenumber").fieldValue("324264").build());
        BoolQueryBuilder queryBuilder = mustStatementMatchParse.matchBuild(arrays);

        SearchRequest searchRequest = getSearchRequest(queryBuilder);
        SearchResponse searchResponse = esService.search(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> result = hit.getSourceAsMap();
            log.info("result:{}", result);
        }
    }
}
