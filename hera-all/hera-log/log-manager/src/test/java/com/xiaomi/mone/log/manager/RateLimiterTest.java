/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/4 15:43
 */
public class RateLimiterTest {

    @Test
    public void test1() {
        RateLimiter rateLimiter = RateLimiter.create(5);
        while (true) {
            System.out.println("get 1 tokens:" + rateLimiter.acquire() + "s");
        }
    }

    @Test
    public void testSmoothBursty2() {
        RateLimiter r = RateLimiter.create(2);
        while (true) {
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("end");
        }
    }



    public static void main(String[] args) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        String str = "[{\"ruleId\":30022, \"filterRegex\":\"OrderGateWayImpl\\.[a-zA-Z]+\\s+error:.*\"}]";
        str = str.replaceAll("\\\\", "\\\\\\\\");


//        List<FlinkAlertRule> alertRules =gson.fromJson(str, new TypeToken<List<FlinkAlertRule>>() {
//        }.getType());
//        System.out.println(alertRules);
    }
}
