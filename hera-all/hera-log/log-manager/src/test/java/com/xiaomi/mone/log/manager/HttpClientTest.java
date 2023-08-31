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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV6;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/25 15:58
 */
@Slf4j
public class HttpClientTest {

    @Test
    public void test() {
        Gson gson = new Gson();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("projectName", "");
        paramsMap.put("appId","" );
        paramsMap.put("appName", "");
        paramsMap.put("appCreator", "xxx");
        paramsMap.put("appCreatTime", System.currentTimeMillis());
        paramsMap.put("funcId", "");
        paramsMap.put("funcName", "eqrere");
        paramsMap.put("logPath", "");
        paramsMap.put("appType", "");
        paramsMap.put("appTypeText", "serverLess");
        paramsMap.put("machineRoom", "cn");

        String api = "http://127.0.0.1:7788/open/api/milog/access/mifass";
        String params = gson.toJson(paramsMap);
        HashMap<String, String> hashMap = Maps.newHashMap();
        hashMap.put("Content-Type", "application/json");
        String pr = HttpClientV6.post(api, params, hashMap);
        log.warn("hera log create, param:{}, result:{}", params, pr);

    }
}
