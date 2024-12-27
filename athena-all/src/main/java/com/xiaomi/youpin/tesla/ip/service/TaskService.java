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
import com.xiaomi.youpin.tesla.ip.common.ApiCall;
import com.xiaomi.youpin.tesla.ip.common.ApiRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/9 14:04
 */
public class TaskService {


    public List<String> tasks(String name) {
        ApiCall call = new ApiCall();
        Map<String, String> m = new HashMap<>(1);
        m.put("user", name);
        String str = call.postCall(ApiCall.TASK_API, new Gson().toJson(m), 5000);
        return new Gson().fromJson(str, ApiRes.class).getData();
    }

}
