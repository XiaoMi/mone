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

package com.xiaomi.youpin.client.test;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class HttpClientV3Test {


    @Test
    public void testHttps() {
        String res = HttpClientV2.get("https://127.0.0.1:9999/version?name=a&token=a", Maps.newHashMap());
        System.out.println(res);
        String res2 = HttpClientV2.get("http://127.0.0.1:9999/version?name=a&token=a", Maps.newHashMap());
        System.out.println(res2);
    }


    @Test
    public void testCHttp() {
        ExecutorService pool = Executors.newFixedThreadPool(200);
        int[] ii = new int[]{8848, 8849};
        IntStream.range(0, 1000).forEach(it -> {
            IntStream.range(0, 600).parallel().forEach(i -> {
                pool.submit(() -> {
                    try {
                        String res = HttpClientV2.get("http://127.0.0.1:" + ii[i % 2] + "/nacos/v1/ns/instances?serviceName=nacos.naming.serviceName", Maps.newHashMap());
                        System.out.println(res);
                    } catch (Throwable ex) {
                        System.out.println(ex.getMessage());
                    }
                });
            });
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }


    @Test
    public void testDownload2() {
        HttpClientV2.download("http://www.baidu.com/index.html", 1000, new File("/tmp/index.html"));
    }


    @Test
    public void testUpload() throws IOException {
        HttpClientV2.upload("http://127.0.0.1:9999/upload?name=zzy2", Files.readAllBytes(Paths.get("/tmp/data/download/test")));
    }


    @Test
    public void testDownload() {
        byte[] res = HttpClientV2.download("http://127.0.0.1:9999/download/test?token=dprqfzzy123!", 1000);
        System.out.println(res.length);
    }

    @Test
    public void testGet() {
        IntStream.range(0, 1).forEach(it -> {
            String res = new HttpClientV3().get("http://xxxx/mtop/arch/im", Maps.newHashMap());
            System.out.println("res:" + res);
        });
    }


    @Test
    public void testGet3() {
        HttpClientV4 client = new HttpClientV4();
        IntStream.range(0, 1).parallel().forEach(it -> {
            Response res = client.get("https://www.jianshu.com/p/067820da332e", Maps.newHashMap(), 1000);
//            Response res = client.get("http://www.baidu.com", Maps.newHashMap(), 1000);
            System.out.println("res:" + res.getCode() + ":" + new String(res.getData()));
        });
    }


    @Test
    public void testGet2() {
        HttpClientV4 client = new HttpClientV4();
        IntStream.range(0, 1).parallel().forEach(it -> {
            Response res = client.get("https://www.163.com/", Maps.newHashMap(), 1000);
            System.out.println("res:" + new String(res.getData()));
        });
    }


    //1491=100  8140=1000
    @Test
    public void testPost() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "json");
        long begin = System.currentTimeMillis();
        IntStream.range(0, 1).parallel().forEach(it -> {
            String res = new HttpClientV3().post("http://xxxx/mtop/arch/im", "{\"cmd\":\"test\"}", headers);
            System.out.println("res:" + res);

        });
        System.out.println(System.currentTimeMillis() - begin);
    }


    //1219=100  2392=1000
    @Test
    public void testPost2() {
        Map<String, String> headers = Maps.newHashMap();
//        headers.put("Content-Type", "json");
        long begin = System.currentTimeMillis();
        IntStream.range(0, 100000).parallel().forEach(it -> {
            try {
                String res = new HttpClientV2().post("http://xxxx/headwater/commonrec", "", headers);
                System.out.println("res:" + res);
            } catch (Exception e) {

            }

        });
        System.out.println(System.currentTimeMillis() - begin);
    }


    //3278 3114 = 1000
    @Test
    public void testPost1() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "json");
        long begin = System.currentTimeMillis();
        IntStream.range(0, 1000).parallel().forEach(it -> {
            try {
                String res = HttpClient.post("http://xxxx/mtop/arch/im", "{\"cmd\":\"test\"}", headers);
//                System.out.println("res:" + res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });
        System.out.println(System.currentTimeMillis() - begin);
    }


    //2582 =1000
    @Test
    public void testPost3() {
        Map<String, String> headers = Maps.newHashMap();
//        headers.put("Content-Type", "json");
        HttpClientV4 client = new HttpClientV4();
        long begin = System.currentTimeMillis();
        IntStream.range(0, 100).parallel().forEach(it -> {
            try {
                Response res = client.post("http://xxxxx/mtop/arch/im", "{\"cmd\":\"test\"}", headers, 200);
                System.out.println("res:" + res.getCode() + " " + new String(res.getData()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });
        System.out.println(System.currentTimeMillis() - begin);
    }


    @Test
    public void testHttp() {
        String createRepositoryUrl = "http://xxxx/api/v4/projects?private_token=";
        String body = "{\"name\": \"test1234567912\",\"namespace_id\": 3578}";
        Map<String, String> headers = new HashMap<>();
        headers.put("host", "v9.git.n.xiaomi.com");
        headers.put("Content-Type", "application/json");
        String res = HttpClientV2.post(createRepositoryUrl, body, headers, 10000);
        System.out.println(res);
    }
}
