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

import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.ScriptHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class ScriptManager {

    private ScriptEngineManager factory = new ScriptEngineManager();

    private ScriptEngine engine;

    public ScriptManager() {
        engine = factory.getEngineByName("groovy");
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
