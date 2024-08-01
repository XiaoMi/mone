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
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.xiaomi.youpin.tesla.ip.bo.UserBo;
import com.xiaomi.youpin.tesla.ip.common.ApiCall;
import com.xiaomi.youpin.tesla.ip.common.ApiRes;
import com.xiaomi.youpin.tesla.ip.common.Context;
import com.xiaomi.youpin.tesla.ip.common.Events;
import com.xiaomi.youpin.tesla.ip.common.UltramanEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 12:52
 */
public class UserService extends AbstractService {

    private Gson gson = new Gson();


    public List<String> users() {
        ApiCall apiCall = new ApiCall();
        return apiCall.call(ApiCall.USER_API);
    }


    public List<UserBo> userBoList() {
        ApiCall apiCall = new ApiCall();
        Map<String,String> m = new HashMap<>(1);
        m.put("cmd","getMembers");
        String res = apiCall.postCall(ApiCall.TASK_API, gson.toJson(m), 5000);
        ApiRes ar = gson.fromJson(res,ApiRes.class);
        return ar.getData().stream().map(it-> gson.fromJson(it,UserBo.class)).collect(Collectors.toList());
    }


    @Override
    public void execute(Context context, AnActionEvent e) {
        this.next(context, e);
    }

    public static void main(String... args) {
        UserService us = new UserService();
        List<UserBo> list = us.userBoList();
        System.out.println(list);
    }
}
