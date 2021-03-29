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

package com.xiaomi.youpin.gateway.common;

import com.alibaba.nacos.common.util.Md5Utils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.ScriptHandler;
import com.youpin.xiaomi.tesla.bo.ScriptInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class ScriptManager {

    private ScriptEngineManager factory = new ScriptEngineManager();

    @Autowired
    private Redis redis;

    @Getter
    private ScriptEngine engine;

    private Cache<String, ScriptInfo> cache;

    private ConcurrentHashMap<Long, String> map = new ConcurrentHashMap<>();

    public ScriptManager() {
        engine = factory.getEngineByName("groovy");
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
    }


    public synchronized ScriptInfo loadScriptInfo(Long sid) throws ExecutionException {
        String id = String.valueOf(sid);
        return cache.get(id, () -> {
            log.info("load script info from redis:{}", sid);
            String scriptInfoStr = redis.get(Keys.scriptKey(id));
            ScriptInfo info = new Gson().fromJson(scriptInfoStr, ScriptInfo.class);
            if (StringUtils.isNotEmpty(scriptInfoStr) && StringUtils.isNotEmpty(info.getScript())) {
                String oldScript = map.get(sid);
                if (!info.getScript().equals(oldScript)) {
                    removeScriptInfo(sid);
                }
                map.put(sid, info.getScript());
            }
            return info;
        });
    }

    @SneakyThrows
    public void removeScriptInfo(Long sid) {
        String script = this.map.get(sid);
        if (null != script) {
            int cacheSize = clearCache(script);
            log.info("removeScriptInfo cache size:{}", cacheSize);
        }
        cache.invalidate(sid);
    }


    /**
     * 脚本更新后,得把原来的script cache 清除掉
     * 不然有内存泄露的问题(groovy.lang.GroovyClassLoader$InnerLoader)
     *
     * @param script
     * @return
     */
    public int clearCache(String script) {
        MutableInt i = new MutableInt(0);
        TeslaSafeRun.runEx(() -> {
            GroovyScriptEngineImpl en = (GroovyScriptEngineImpl) this.getEngine();
            en.clearCache(script);
            i.setValue(en.cacheSize());
        });
        return i.getValue();
    }


    public Object invoke(String script, String functionName, Map<String, Object> bindValues, Object... args) {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            Object res = ((Invocable) engine).invokeFunction(functionName, args);
            return res;
        } catch (ScriptException e) {
            log.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage());
        }
        return null;
    }


    public Object invoke(String id, String script, String functionName, Map<String, Object> bindValues, Object... args) {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            Object res = ((Invocable) engine).invokeFunction(functionName, args);
            return res;
        } catch (ScriptException e) {
            log.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public void invokeBefore(String script, ApiInfo apiInfo, Object req, Map<String, Object> bindValues) {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            ScriptHandler handler = ((Invocable) engine).getInterface(ScriptHandler.class);
            handler.before(apiInfo, req);
        } catch (ScriptException e) {
            log.error(e.getMessage());
        }
    }

    public Object invokeAfter(String script, ApiInfo apiInfo, Object req, Object response, Map<String, Object> bindValues) {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            ScriptHandler handler = ((Invocable) engine).getInterface(ScriptHandler.class);
            return handler.after(apiInfo, req, response);
        } catch (ScriptException e) {
            log.warn(e.getMessage(), e);
        }
        return response;
    }


    public Object invokeAround(String script, ApiInfo apiInfo, Object req, Object response) {
        try {
            engine.eval(script);
            ScriptHandler handler = ((Invocable) engine).getInterface(ScriptHandler.class);
            return handler.after(apiInfo, req, response);
        } catch (ScriptException e) {
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}
