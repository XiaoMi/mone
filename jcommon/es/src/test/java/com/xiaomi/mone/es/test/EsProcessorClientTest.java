package com.xiaomi.mone.es.test;

import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.es.ProcessorConf;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EsProcessorClientTest {

    @Test
    public void bulkInsert() throws InterruptedException {
        String ip = "";
        ProcessorConf conf = new ProcessorConf(100, 5, 2, 100, 3, 5, new EsClient(ip, "", ""), new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                System.out.println("before insert" + request);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                System.out.println("success after,request:" + request.getDescription() + " resopnse:" + Arrays.toString(response.getItems()));
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                System.out.println("success after,request:" + request + " failure:" + failure);
            }
        });
        EsProcessor processor = new EsProcessor(conf);
        try {
            String indexName = "youpin_insert_test-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", System.currentTimeMillis());
            int n = 10001;
            int count = 0;
            while (true) {
                processor.bulkInsert(indexName, data);
                count++;
                if (count == n) {
                    break;
                }
            }
            Thread.sleep(6000l);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}