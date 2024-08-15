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

package com.xiaomi.youpin.tesla.ip.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.ip.bo.SpiderUrl;
import com.xiaomi.youpin.tesla.ip.common.ApiCall;
import com.xiaomi.youpin.tesla.ip.common.ApiRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/10 10:47
 */
public class SpiderService {

    private Gson gson = new Gson();

    public List<SpiderUrl> list(String name) {
        ApiCall call = new ApiCall();
        Map<String, String> m = new HashMap<>();
        m.put("name", name);
        String res = call.postCall(ApiCall.SPIDER_API, gson.toJson(m), 3000);
        List<String> list = gson.fromJson(res, ApiRes.class).getData();
        return list.stream().map(it -> gson.fromJson(it, SpiderUrl.class)).collect(Collectors.toList());
    }

}
