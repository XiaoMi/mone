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

import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/24 09:53
 */
public class HttpTest {


    @Test
    public void testSplit() {
        String abc= "a";
        System.out.println(abc.split(",",3)[0]);
    }


    @Test
    public void testGet() throws IOException {
//        HttpGet get = new HttpGet("http://www.baidu.com");
//
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        CloseableHttpResponse res = httpClient.execute(get);
//        HttpEntity entity = res.getEntity();
//
//
//        byte[] data = HttpClient.getBytes(entity);
//        System.out.println(Arrays.toString(data));
//        System.out.println(new String(data));
//
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);

        set.stream().forEach(it ->{
            if (it == 1) {
                return;
            }
            System.out.println(it);
        });
    }
}
