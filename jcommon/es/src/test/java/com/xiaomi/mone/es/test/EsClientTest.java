package com.xiaomi.mone.es.test;

import com.xiaomi.mone.es.EsClient;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

import static org.elasticsearch.search.sort.SortOrder.DESC;


/**
 * @author goodjava@qq.com
 */
public class EsClientTest {


    @Test
    public void testInsertDoc() throws IOException {
        String ip = "";
        EsClient client = new EsClient(ip, "", "");
        IntStream.range(0, 10).parallel().forEach(i -> {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("message", "error!");
            jsonMap.put("time", System.currentTimeMillis());
            jsonMap.put("level", "error");
            jsonMap.put("record_id", "1");
            try {
                client.insertDoc("youpin_arch_traffic_recording", jsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Test
    public void testCreateIndex() throws IOException {
        EsClient client = new EsClient("127.0.0.1:9200", "", "");
        String mapping = "     {\n" +
                "      \"properties\": {\n" +
                "        \"message\": {\n" +
                "          \"type\": \"text\"\n" +
                "        },\n" +
                "        \"level\": {\n" +
                "          \"type\": \"keyword\"\n" +
                "        },\n" +
                "        \"record_id\": {\n" +
                "          \"type\": \"text\"\n" +
                "        },\n" +
                "        \"time\": {\n" +
                "          \"type\": \"long\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n";
        client.createIndex("test", mapping);
    }

    @Test
    public void testGet() throws IOException {
        EsClient client = new EsClient("127.0.0.1:9200", "", "");
        GetRequest getRequest = new GetRequest("tesla-traffic", "_doc", "a745293b-d083-4e7c-b7b1-5724e5fcce30");
        GetResponse res = client.get(getRequest);
        System.out.println(res);
    }

    @Test
    public void testDelete() throws IOException {
        EsClient client = new EsClient("127.0.0.1:9200", "", "");
        DeleteRequest delRequest = new DeleteRequest("tesla-traffic", "_doc", "a745293b-d083-4e7c-b7b1-5724e5fcce30");
        DeleteResponse res = client.delete(delRequest);
        System.out.println(res);
    }

    @Test
    public void testUpdate() throws IOException {
        EsClient client = new EsClient("127.0.0.1:9200", "", "");
        Map<String, Object> map = new HashMap<>();
        map.put("creator", "dingpei");
        UpdateRequest updateRequest = new UpdateRequest("tesla-traffic", "_doc", "4619d576-a4ff-4c44-99a2-59923c605c39").doc(map);
        UpdateResponse res = client.update(updateRequest);
        System.out.println(res);
    }


    @Test
    public void testSearch() throws IOException {
        String ip = "";
        EsClient client = new EsClient(ip, "", "");

        SearchRequest searchRequest = new SearchRequest("mione-production-jaeger-span");
        SearchSourceBuilder qb = new SearchSourceBuilder();
        qb.query(QueryBuilders.rangeQuery("timestamp").from("1630892373339").to("1630895973339"));
        qb.query(QueryBuilders.termQuery("traceId", "milog"));
        qb.from(0).size(10).timeout(new TimeValue(2000));
        qb = qb.sort("timestamp", DESC);
        searchRequest.source(qb);
        SearchResponse res = client.search(searchRequest);
        System.out.println(res.getHits().getTotalHits().value);
        System.out.println(res);
    }

    @Test
    public void testSearchFeishu() throws IOException {
        String queryText = "状况";
        EsClient client = new EsClient("zjynewretail.api.es.srv:80", "xiaomi", "xiaomi");
        // search
        SearchRequest searchRequest = new SearchRequest("zgq_common_feishu");
        SearchSourceBuilder qb = new SearchSourceBuilder();
        qb.query(QueryBuilders.termQuery("body.content", queryText));
        qb.from(0).size(10).timeout(new TimeValue(2000));
        qb = qb.sort("_score", DESC);
        // hightlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("body.content");
        qb.highlighter(highlightBuilder);
        searchRequest.source(qb);
        // 解析搜索结果
        SearchResponse res = client.search(searchRequest);
        SearchHit[] hits = res.getHits().getHits();
        if (hits == null || hits.length == 0) {
            return;
        }
        for (SearchHit hit : hits) {
            // kv搜索结果
            Map<String, Object> kvRes = hit.getSourceAsMap();
            // json搜索结果
            String jsonRes = hit.getSourceAsString();
            // 高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null && !highlightFields.isEmpty()) {
                Collection<HighlightField> HighlightFieldCollection = highlightFields.values();
                for (HighlightField highlightField : HighlightFieldCollection) {
                    // 处理高亮，一般是把字段原样反回给前端解析
                    highlightField.getName();
                    highlightField.getFragments()[0].string();
                }
            }
        }
    }

    @Test
    public void bulkInsert() {
        String ip = "";
        EsClient client = new EsClient(ip, "", "");
        try {
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> data;
            for (int i = 0; i < 10; i++) {
                data = new HashMap<>();
                data.put("message", "ccc" + i);
                data.put("timestamp", new Date());
                dataList.add(data);
            }
            System.out.println(client.bulkInsert("auto_create_index-2021.08.05", dataList));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCreateIndexTemplate() throws IOException {
        String ip = "";
        EsClient client = new EsClient(ip, "", "");
    }

    @Test
    public void insertDocAsyn() throws InterruptedException {
        String ip = "";
        EsClient client = new EsClient(ip, "", "");
        String index = "youpin_insert_test-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        Map<String, Object> data = new HashMap<>();
        data.put("aa", "11");
        data.put("timestamp", System.currentTimeMillis());
        ActionListener listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse response) {
                System.out.println(Thread.currentThread().getName());
                System.out.println(response);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        };
        client.insertDocAsyn(index, data, listener);
        System.out.println("main" + Thread.currentThread().getName());
        Thread.sleep(1000);
    }


    @Test
    public void getClusterHealth() throws IOException {
//        EsClient client = new EsClient("awsmbdw.api.es.srv:80", "1c0064217ffb42c2a95f854344f925cd");
//        SearchRequest searchRequest = new SearchRequest("zgq_common_milog_staging_nginx_awsmb_1");
//        client.search(searchRequest);

    }
}
