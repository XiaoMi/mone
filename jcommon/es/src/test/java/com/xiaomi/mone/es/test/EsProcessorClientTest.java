//package com.xiaomi.mone.es.test;
//
//import com.google.common.reflect.TypeToken;
//import com.google.gson.Gson;
//import com.xiaomi.data.push.nacos.NacosConfig;
//import com.xiaomi.mone.es.EsClient;
//import com.xiaomi.mone.es.EsProcessor;
//import com.xiaomi.mone.es.ProcessorConf;
//import org.elasticsearch.action.bulk.BulkProcessor;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.junit.Test;
//
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class EsProcessorClientTest {
//
//    @Test
//    public void bulkInsert() throws InterruptedException {
//
//        String str = "{\"@timestamp\":\"2024-06-20T19:39:15.871+08:00\",\"@version\":\"1\",\"message\":\"hello world data test wtt~\",\"logger_name\":\"com.xiaomi.ai.Application\",\"thread_name\":\"http-nio-10010-exec-3\",\"level\":\"INFO\",\"level_value\":20000,\"LOG_NAME\":\"ai-workflow\",\"SENTRY_ENABLED\":\"false\",\"user_name\":\"wangjunfei3\",\"user_team\":\"ncl7150\",\"request_uri\":\"/hello\",\"trace_id\":\"9cf73bfe51e877a83806ac01b6630815\",\"trace_flags\":\"01\",\"span_id\":\"c13434f908acebb4\"}";
//        Map<String, Object> data = new Gson().fromJson(str, new TypeToken<Map<String, Object>>() {
//        }.getType());
//        data.put("timeStamp", System.currentTimeMillis());
//
//        NacosConfig config = new NacosConfig();
//        config.setDataId("zzy_new");
////        config.init();
//
//        String ip = "zjydw.api.es.srv:80";
//        String user = config.getConfig("es_user");
//        String pwd = config.getConfig("es_password");
//        String token = "4244b7014a5c44fea63bea711c7697fe";
//        String catalog = "es_zjy_log";
//        String database = "default";
//
//        EsClient esClient = new EsClient(ip, token, catalog, database);
//        ProcessorConf conf = new ProcessorConf(100, 5, 1, 100, 3, 5, esClient, new BulkProcessor.Listener() {
//            @Override
//            public void beforeBulk(long executionId, BulkRequest request) {
//                System.out.println("before insert" + request);
//            }
//
//            @Override
//            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                System.out.println("success after,request:" + request.getDescription() + " resopnse:" + Arrays.toString(response.getItems()));
//            }
//
//            @Override
//            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
//                System.out.println("success after,request:" + request + " failure:" + failure);
//            }
//        });
//        EsProcessor processor = new EsProcessor(conf);
//        try {
//            String indexName = "prod_hera_index_95956-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
//            int n = 1;
//            int count = 0;
//            while (true) {
////                processor.bulkInsert(indexName, data);
//                processor.bulkInsert(indexName, data);
//                count++;
//                if (count == n) {
//                    break;
//                }
//            }
////            Thread.sleep(10000l);
//            System.in.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}