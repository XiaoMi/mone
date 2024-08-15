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

package com.xiaomi.youpin.tesla.ip.common;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/2 10:56
 */
public class ApiCall {

    private static final String END_POINT = "http://127.0.0.1:8999";

    public static final String MUSIC_API = END_POINT + "/music";
    public static final String TEXT_API = END_POINT + "/text";
    public static final String IMAGE_API = END_POINT + "/image";
    public static final String CODE_API = END_POINT + "/code";
    public static final String USER_API = END_POINT + "/user";
    public static final String TASK_API = END_POINT + "/task";
    public static final String SPIDER_API = END_POINT + "/spider";

    public static final Map<String, Integer> TagIndex = new HashMap<>();

    public List<String> call(String url) {
        return null;
    }

    public String postCall(String url, String params) {
        return postCall(url, params, 1000);
    }

    public String postCall(String url, String params, int timeout) {
        return null;
    }


    public String callOne(String url) {
        return null;
    }

    public String callTag(String url, String tag) {
        return "";
    }

    public String callIt(String url, String name) {
        return "";
    }


    public static void main(String[] args) {
        ApiCall call = new ApiCall();
        //System.out.println(list);

        Map<String, Object> m = new HashMap<>();
        m.put("name", "zzy");
        m.put("cmd", "get");
        String res = call.postCall(CODE_API, new Gson().toJson(m));
        System.out.println(res);
    }
}
