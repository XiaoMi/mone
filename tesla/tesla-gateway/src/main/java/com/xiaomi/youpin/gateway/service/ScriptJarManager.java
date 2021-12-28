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

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.base.Stopwatch;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.mione.serverless.Context;
import com.xiaomi.mione.serverless.Event;
import com.xiaomi.mione.serverless.Handler;
import com.xiaomi.mione.serverless.Result;
import com.xiaomi.youpin.docean.common.EasyClassLoader;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.function.imp.RocketMqImp;
import com.xiaomi.youpin.gateway.http.Http;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.UUID;
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
    private Dao dao;

    @Autowired
    private RocketMqImp rocketMq;

    @Autowired
    private Http http;


    private ConcurrentHashMap<String, EasyClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>();

    @NacosValue("${dlServerUrl:http://127.0.0.1:9999}")
    private String dlServerUrl;

    @NacosValue("${dlToken:}")
    private String token = "";

    private static final byte[][] JAR_LOAD_LOCK = new byte[][]{new byte[0], new byte[0], new byte[0],new byte[0], new byte[0], new byte[0],new byte[0], new byte[0]};

    private File download(String key, String name) {
        File file = new File("/tmp/" + UUID.randomUUID() + "_" + key);
        HttpClientV2.download(this.dlServerUrl + "/download?name=" + name + "&token=" + this.token, 3000, file);
        return file;
    }


    @SneakyThrows
    public void loadJar(String key, String jarUrl) {
        if (!classLoaderMap.containsKey(key)) {
            // 比String.intern()更优
            synchronized (JAR_LOAD_LOCK[key.hashCode() % JAR_LOAD_LOCK.length]) {
                log.info("load jar key:{},jarUrl:{}", key, jarUrl);
                File file = download(key, getUrl(key, jarUrl));
                URL url = file.toURI().toURL();
                EasyClassLoader loader = new EasyClassLoader(new URL[]{url});
                classLoaderMap.putIfAbsent(key, loader);
            }
        }
    }


    @SneakyThrows
    public Object getInstance(String key, String className) {
        Class<?> clazz = classLoaderMap.get(key).loadClass(className);
        return clazz.newInstance();
    }


    @SneakyThrows
    public Result execute(Event event, String key, String className, String url , Context context) {
        if (log.isDebugEnabled()) {
            log.debug("execute key:{} {}", key, className);
        }
        loadJar(key, url);
        Handler h = getHandler(key, className);
        return h.execute(event, context);
    }

    private Handler getHandler(String key, String className) {
        return this.handlerMap.compute(key, (k, v) -> {
            if (null == v) {
                Handler handler = (Handler) getInstance(key, className);
                handler.init(dubbo, http, nacos);
                return handler;
            }
            return v;
        });
    }


    private String getUrl(String key, String url) {
        return url;
    }


    public void releaseJar(String key) {
        classLoaderMap.remove(key);
        this.handlerMap.remove(key);
    }


}
