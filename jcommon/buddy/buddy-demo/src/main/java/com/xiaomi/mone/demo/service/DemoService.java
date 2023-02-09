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

package com.xiaomi.mone.demo.service;

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.mone.demo.anno.Secured;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 10:18
 */
public class DemoService implements IDemoService {


    private String name = "zzy";

    private ExecutorService pool = Executors.newFixedThreadPool(1);

    private Redis redis;

    private NutDao dao;

    public DemoService() {

    }


    public DemoService(String name) {
        this.name = name;
    }

    public void init() {
        redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();

        dao = new NutDao();
        SimpleDataSource ds = new SimpleDataSource();
        try {
            ds.setDriverClassName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ds.setUsername("root");
        ds.setPassword("123456");
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test");
        dao.setDataSource(ds);
    }

    @Override
    @Secured(user = "root")
    public String test(String param) {
//        runnable.run();
        return "res:" + param;
    }

    public String hi() {
        return "hi zzy";
    }

    public String redis() {
        redis.set("name", "zzy");
        return redis.get("name");
    }

    public void submit(Runnable runnable) {
        runnable.run();
    }

    public void submit(Callable callable) throws Exception {
        Object res = callable.call();
        System.out.println("res:" + res);
    }


    public String nutz() {
        List<Record> list = dao.query("user", null);
        return list.toString();
    }

    /**
     * 使用okhttp
     *
     * @return
     * @throws IOException
     */
    public String okhttp() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https:www.baidu.com")
                .build();

        Response res = client.newCall(request).execute();
        return res.body().string();
    }


    public String tao() {
        return "tao";
    }

    public void run() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run");
                latch.countDown();
            }
        });
        latch.await();
//        pool.shutdownNow();
    }

}
