package com.xiaomi.youpin.docean.plugin.es.antlr4;

import com.google.gson.Gson;
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.youpin.docean.plugin.es.antlr4.common.util.EsQueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:21
 */
@Slf4j
public class EsQueryTest {

    private EsClient client;
    private Gson gson;
    private String esIndexName;
    private SearchResponse searchResponse;

    @Before
    public void init() {
        String esAddress = "127.0.0.1:80";
        String user = "test";
        String password = "test";
        client = new EsClient(esAddress, user, password);
        gson = new Gson();
        esIndexName = "zgq_common_milog_staging_app_private_1";
    }

    @After
    public void end() {
        if (null != searchResponse) {
            SearchHit[] hits = searchResponse.getHits().getHits();
            if (hits == null || hits.length == 0) {
                return;
            }
            for (SearchHit hit : hits) {
                Map<String, Object> ferry = hit.getSourceAsMap();
                log.info("result:{}", ferry);
            }
        }
    }

    @Test
    public void commonTest() {
        System.out.println(EsQueryUtils.getEsQuery("(a : 1 OR (b : 4 OR c:3)) AND d : 5"));
    }

    @Test
    public void test1() throws IOException {
        String str = "\"/mtop/arch/openApi/beat/\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        SearchSourceBuilder sourceBuilder = EsQueryUtils.getSearchSourceBuilder(str);
        SearchRequest searchRequest = new SearchRequest(new String[]{esIndexName}, sourceBuilder);
        searchResponse = client.search(searchRequest);
        System.out.println(esQuery);
    }

    @Test
    public void test2() throws IOException {
//        String str = "traceId:1ae050e3393bcdf4445ae8e2f61d302d or \"/mtop/promotion/coupon/getConfigList\"";
        String str = "traceId:1ae050e3393bcdf4445ae8e2f61d302d and \"/mtop/arch/openApi/beat\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        SearchSourceBuilder sourceBuilder = EsQueryUtils.getSearchSourceBuilder(str);
        SearchRequest searchRequest = new SearchRequest(new String[]{esIndexName}, sourceBuilder);
        searchResponse = client.search(searchRequest);
        System.out.println(esQuery);
    }

    @Test
    public void test3() throws IOException {
        String str = "level : ERROR and HeaderExchangeClient  and 1690970637889 and logip:10.38.162.100";
        String esQuery = EsQueryUtils.getEsQuery(str);
        SearchSourceBuilder sourceBuilder = EsQueryUtils.getSearchSourceBuilder(str);
        SearchRequest searchRequest = new SearchRequest(new String[]{esIndexName}, sourceBuilder);
        searchResponse = client.search(searchRequest);
        System.out.println(esQuery);
    }

    @Test
    public void test4() {
        String str = "level:test and \"messageTest and message:\"我们的\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test5() {
        String str = "level:test or messageTest and message:\"我们的\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test6() {
        String str = "(level:test or messageTest) and message:\"我们的\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test7() {
        String str = "(not level:test) or messageTest and message:\"我们的\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test8() {
        String str = "(not level:test) or (not level: messageTest) and message:\"我们的\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test9() throws IOException {
        String str = "appName exist";
        String esQuery = EsQueryUtils.getEsQuery(str);
        SearchSourceBuilder sourceBuilder = EsQueryUtils.getSearchSourceBuilder(str);
        SearchRequest searchRequest = new SearchRequest(new String[]{esIndexName}, sourceBuilder);
        searchResponse = client.search(searchRequest);
        System.out.println(esQuery);
    }

    @Test
    public void test10() throws IOException {
        String str = "appName not_exist";
        String esQuery = EsQueryUtils.getEsQuery(str);
        SearchSourceBuilder sourceBuilder = EsQueryUtils.getSearchSourceBuilder(str);
        SearchRequest searchRequest = new SearchRequest(new String[]{esIndexName}, sourceBuilder);
        searchResponse = client.search(searchRequest);
        System.out.println(esQuery);
    }

    @Test
    public void test11() {
        String str = " (not http) and 8088";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    @Test
    public void test12() {
        String str = "10.38.201.233";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }

    /**
     * 某个key的值不等于value
     */
    @Test
    public void test13(){
//        String str = "level  != INFO";
        String str = "level  != \"INFO\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }
    @Test
    public void test14(){
//        String str = "linenumber< 128";
//        String str = "level in [\"ERROR\",\"INFO\"]";
        String str = "message : \"send mq message, topic: kfs-return-visit-result\"";
        String esQuery = EsQueryUtils.getEsQuery(str);
        System.out.println(esQuery);
    }


    @Test
    public void test15(){
//        String str = "linenumber< 128";
//        String str = "level in [\"ERROR\",\"INFO\"]";
        String str = "message : \"~send mq message, topic: kfs-return-visit-result\"";
        SearchSourceBuilder esQuery = EsQueryUtils.getSearchSourceBuilder(str);
        System.out.println(esQuery.query());
    }

    @Test
    public void test16(){
        String str = "230727436807811 and not \"OfflineOrderServiceImpl.orderOfflineList\"";
        SearchSourceBuilder esQuery = EsQueryUtils.getSearchSourceBuilder(str);
        System.out.println(esQuery.query());
    }

    @Test
    public void test17(){
        String str = "230727436807811 not \"OfflineOrderServiceImpl.orderOfflineList\"";
        SearchSourceBuilder esQuery = EsQueryUtils.getSearchSourceBuilder(str);
        System.out.println(esQuery.query());
    }

    @Test
    public void test18(){
        String str = "linenumber:39058 and logip:10.126.248.201 and tail:\"matrix_dk-system-api-c4\"";
        SearchSourceBuilder esQuery = EsQueryUtils.getSearchSourceBuilder(str);
        System.out.println(esQuery.query());
    }
}
