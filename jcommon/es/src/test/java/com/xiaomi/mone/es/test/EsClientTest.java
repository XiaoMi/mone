/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.es.test;

import com.xiaomi.mone.es.EsClient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


/**
 * @author goodjava@qq.com
 */
public class EsClientTest {


    @Test
    public void testInsertDoc() throws IOException {
        EsClient client = new EsClient("127.0.0.1", 9200);
        IntStream.range(0,1).parallel().forEach(i->{
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("message", "error!");
            jsonMap.put("time", System.currentTimeMillis());
            jsonMap.put("level","error");
            try {
                client.insertDoc("post", jsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Test
    public void testCreateIndex() throws IOException {
        EsClient client = new EsClient("127.0.0.1", 9200);
        String mapping = "     {\n" +
                "      \"properties\": {\n" +
                "        \"message\": {\n" +
                "          \"type\": \"text\"\n" +
                "        },\n" +
                "        \"level\": {\n" +
                "          \"type\": \"keyword\"\n" +
                "        },\n" +
                "        \"time\": {\n" +
                "          \"type\": \"long\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n";
        client.createIndex(mapping);
    }

    @Test
    public void testGet() throws IOException {
        EsClient client = new EsClient("127.0.0.1", 9200);
        GetRequest getRequest = new GetRequest("test","_doc","e1ee9790-d26c-4383-aea4-f438180fd95a");
        GetResponse res = client.get(getRequest);
        System.out.println(res);
    }
}
