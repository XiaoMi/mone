/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.http.docs.core;

import com.google.gson.Gson;
import com.xiaomi.mone.http.docs.core.beans.HttpApiCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo doc cache.
 */
public class HttpApiDocsCache {

    /**
     * module cache.
     */
    private static final Map<String, HttpModuleCacheItem> apiModulesCache = new ConcurrentHashMap<>(16);

    /**
     * API details cache in module.
     */
    private static final Map<String, HttpApiCacheItem> apiParamsAndRespCache = new ConcurrentHashMap<>(16);

    private static List<HttpModuleCacheItem> allApiModuleInfo = null;

    private static final Gson gson = new Gson();

    public static void addApiModule(String key, HttpModuleCacheItem moduleCacheItem) {
        apiModulesCache.put(key, moduleCacheItem);
    }

    public static void addApiParamsAndResp(String key, HttpApiCacheItem apiParamsAndResp) {
        apiParamsAndRespCache.put(key, apiParamsAndResp);
    }

    public static String getAllApiModuleStr(){
        return gson.toJson(apiModulesCache);
    }

    public static String getAllApiParamsAndRespStr(){
        return gson.toJson(apiParamsAndRespCache);
    }

    public static List<HttpModuleCacheItem> getAllApiModuleInfo() {
        if (allApiModuleInfo == null) {
            allApiModuleInfo = new ArrayList<>(apiModulesCache.size());
            apiModulesCache.forEach((k, v) -> {
                allApiModuleInfo.add(v);
            });
        }
        return allApiModuleInfo;
    }

}
