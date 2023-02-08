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
package com.xiaomi.mone.dubbo.docs.core;

import com.google.gson.Gson;
import com.xiaomi.mone.dubbo.docs.core.beans.ApiCacheItem;
import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dubbo doc cache.
 */
public class DubboApiDocsCache {

    /**
     * module cache.
     */
    private static final Map<String, ModuleCacheItem> apiModulesCache = new ConcurrentHashMap<>(16);

    /**
     * API details cache in module.
     */
    private static final Map<String, ApiCacheItem> apiParamsAndRespCache = new ConcurrentHashMap<>(16);


    /**
     * API basic info cache in module.
     */
    private static String basicApiModuleInfo = null;

    private static final Gson gson = new Gson();

    public static void addApiModule(String key, ModuleCacheItem moduleCacheItem) {
        apiModulesCache.put(key, moduleCacheItem);
    }

    public static void addApiParamsAndResp(String key, ApiCacheItem apiParamsAndResp) {
        apiParamsAndRespCache.put(key, apiParamsAndResp);
    }

    public static String getAllApiModuleStr() {
        return gson.toJson(apiModulesCache);
    }

    public static String getAllApiParamsAndRespStr(){
        return gson.toJson(apiParamsAndRespCache);
    }

    public static String getBasicApiModuleInfo() {
        if (basicApiModuleInfo == null) {
            List<ModuleCacheItem> tempList = new ArrayList<>(apiModulesCache.size());
            apiModulesCache.forEach((k, v) -> tempList.add(v));
            basicApiModuleInfo = gson.toJson(tempList);
        }
        return basicApiModuleInfo;
    }

}
