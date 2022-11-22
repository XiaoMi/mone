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

package com.xiaomi.youpin.tesla.test;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 2020/9/26
 */
public class ThreadTest {

    @Test
    public void testHttp() throws IOException, InterruptedException {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        Future<?> f = pool.submit(new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("start");

            HttpGet get = new HttpGet("http://127.0.0.1:9999");
            RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).build();
            get.setConfig(config);
            CloseableHttpResponse res = null;
            try {
                res = httpClient.execute(get);
                System.out.println(res.getStatusLine().getStatusCode());
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }));
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                f.get(1,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //这里是打不断的,因为底层是socker 的read 处理(java.net.SocketInputStream.socketRead)
            f.cancel(true);
            try {
                //通过close socket 可以做到关闭
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();

        new Thread(()->{
            for(;;) {
                System.out.println(pool.getActiveCount());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        System.out.println("finish");
        Thread.currentThread().join(6000);
    }

}
