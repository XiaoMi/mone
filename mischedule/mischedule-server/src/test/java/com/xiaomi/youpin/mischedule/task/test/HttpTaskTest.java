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

package com.xiaomi.youpin.mischedule.task.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.http.HttpTask;
import com.xiaomi.data.push.schedule.task.impl.http.HttpTaskParam;
import org.junit.Test;

import java.util.HashMap;

public class HttpTaskTest {


    @Test
    public void testTask() {
        HttpTask task = new HttpTask();
        TaskParam param = new TaskParam();
        HttpTaskParam httpTaskParam = new HttpTaskParam();
        httpTaskParam.setMethodType("get");
        httpTaskParam.setUrl("http://www.baidu.com");
        httpTaskParam.setHeaders(Maps.newHashMap());
        param.put("param", new Gson().toJson(httpTaskParam));
        TaskContext context = new TaskContext();
        TaskResult res = task.execute(param, context);
        System.out.println(res.getData());
    }

    @Test
    public void testTask2() {
        HttpTask task = new HttpTask();
        TaskParam param = new TaskParam();
        param.put("param", "{\n" +
                "    \"url\": \"http://xxxx/scheduled/urgePay\",\n" +
                "    \"methodType\": \"post\",\n" +
                "    \"body\": \"{\\\"uniKey\\\":\\\"4200421613801413_UrgePay\\\",\\\"id\\\":1,\\\"opt\\\":1,\\\"url\\\":\\\"https://xxxx/shop/orderlist?status=3\\\",\\\"uid\\\":\\\"168390116\\\",\\\"phone\\\":\\\"\\\",\\\"msgType\\\":0,\\\"msg\\\":\\\"\\\",\\\"parms\\\":{\\\"summaryId\\\":1,\\\"data\\\":{}}}\",\n" +
                "    \"headers\": {\"content-type\":\"application/json\"}\n" +
                "}");
        TaskContext context = new TaskContext();
        TaskResult res = task.execute(param, context);
        System.out.println(res);
    }


    @Test
    public void testTask3() {
        HttpTask task = new HttpTask();
        TaskParam param = new TaskParam();
        param.put("param","{\"url\":\"http://127.0.0.1:11318/api/flushData/xData?startDate\\u003d\",\"methodType\":\"get\"}");
        TaskContext context = new TaskContext();
        TaskResult res = task.execute(param, context);
        System.out.println(res);
    }



    @Test
    public void testHttpClient() {
//        String body= "{\"uniKey\":\"4200421613801413_UrgePay\",\"id\":1,\"opt\":1,\"url\":\"https://home.mi.com/shop/orderlist?status=3\",\"uid\":\"168390116\",\"phone\":\"\",\"msgType\":0,\"msg\":\"\",\"parms\":{\"summaryId\":1,\"data\":{}}}";
        String body= "{}";
        HashMap<String, String> header = Maps.newHashMap();
        header.put("content-type","application/json");
        String res = HttpClientV2.post("http://xxxx/scheduled/urgePay", body, header);
        System.out.println(res);
    }


    @Test
    public void testHttpClient2() {
        String body= "{}";
        HashMap<String, String> header = Maps.newHashMap();
        header.put("content-type","application/json");
        String res = HttpClientV2.post("http://xxxx/api/flushData/xData?startDate=", body, header);
        System.out.println(res);
    }
}
