package com.xiaomi.youpin.client.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.client.MoneHttpClient;
import com.xiaomi.data.push.client.bo.HttpResult;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/8/18 09:23
 */
public class MoneHttpClientTest {

    private Gson gson = new Gson();


    //1151->1000
    //4801->10000
    //wb->5981->100
    @Test
    public void testGet() {
        Stopwatch sw = Stopwatch.createStarted();
//        String url = "http://10.225.177.190:80/api/z/oss/hi";
        String url = "http://127.0.0.1:8999/a";
        MoneHttpClient client = new MoneHttpClient(10, 1000);
        IntStream.range(0, 3).forEach(i -> {
//            String res = client.get(url + "/" + i, ImmutableMap.of("connection","close"), 200000);
            HttpResult res = client.get(url + "/" + i, ImmutableMap.of(), 2000);
            System.out.println(new String(res.getData()));
        });
        System.out.println(client.info());
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testPost() {
        Stopwatch sw = Stopwatch.createStarted();
        String url = "http://127.0.0.1:8999/p";
        MoneHttpClient client = new MoneHttpClient(10, 1000);
        JsonObject obj = new JsonObject();
        obj.addProperty("id", 12334);
        IntStream.range(0, 1000).forEach(i -> {
            HttpResult res = client.post(url, ImmutableMap.of(), gson.toJson(obj).getBytes(), 2000);
            System.out.println(new String(res.getData()));
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }
}
