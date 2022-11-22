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

package com.xiaomi.youpin.gateway.service;

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.docean.common.EasyClassLoader;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.http.Http;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/29 11:39
 */
@Slf4j
@Service
public class ScriptJarManager {

    @Autowired
    private Redis redis;

    @Autowired
    private Dubbo dubbo;

    @Autowired
    private Nacos nacos;

    @Autowired
    private Http http;

    private ConcurrentHashMap<String, EasyClassLoader> classLoaderMap = new ConcurrentHashMap<>();


    private static final byte[][] JAR_LOAD_LOCK = new byte[][]{new byte[0], new byte[0], new byte[0],new byte[0], new byte[0], new byte[0],new byte[0], new byte[0]};



    @SneakyThrows
    public void loadJar(String key, String jarUrl) {
    }


    @SneakyThrows
    public Object getInstance(String key, String className) {
        Class<?> clazz = classLoaderMap.get(key).loadClass(className);
        return clazz.newInstance();
    }



    private String getUrl(String key, String url) {
        return url;
    }




}
