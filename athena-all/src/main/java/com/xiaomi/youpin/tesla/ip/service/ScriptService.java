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

import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2021/12/19
 */
@Slf4j
public class ScriptService {

    private ScriptEngineManager factory = new ScriptEngineManager(ScriptService.class.getClassLoader());

    private ScriptEngine engine;


    private ScriptService() {
        engine = factory.getEngineByName("groovy");
    }

    @SneakyThrows
    public static String getScript(String name) {
        PromptInfo info = Prompt.getPrompt(69, name);
        if (null == info || null == info.getData()) {
            throw new RuntimeException("No corresponding prompt found.");
        }
        return info.getData();
    }

    private static final class LazyHolder {
        private static final ScriptService ins = new ScriptService();
    }

    public static ScriptService ins() {
        return LazyHolder.ins;
    }

    public Object invoke(String script, String functionName, Map<String, Object> bindValues, Object... args) {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            Object res = ((Invocable) engine).invokeFunction(functionName, args);
            log.info("invoke res:" + res);
            return res;
        } catch (Throwable e) {
            log.error("invoke script error:" + e.getMessage(), e);
            return e;
        }
    }

}
