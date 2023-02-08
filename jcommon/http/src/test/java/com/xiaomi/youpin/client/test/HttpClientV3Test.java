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
    public void testDns() {
        String res = HttpClientV2.get("http://www.baaafaidu.com", Maps.newHashMap());
        System.out.println(res);
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

}
