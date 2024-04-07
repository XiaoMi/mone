package run.mone.local.docean.service;

import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/21 18:05
 */
@Service
@Slf4j
public class GroovyService {

    private ScriptEngineManager factory = new ScriptEngineManager(GroovyService.class.getClassLoader());

    private ScriptEngine engine;

    public void init() {
        log.info("init");
        engine = factory.getEngineByName("groovy");
    }


    public GroovyService() {

    }


    public Object invoke(String script, String functionName, Map<String, Object> bindValues, JsonElement args, Object context) throws ScriptException, NoSuchMethodException {
        try {
            bindValues.entrySet().stream().forEach(it -> engine.put(it.getKey(), it.getValue()));
            engine.eval(script);
            Object res = ((Invocable) engine).invokeFunction(functionName, args, context);
            log.info("invoke res:" + res);
            return res;
        } catch (Throwable e) {
            log.error("invoke script error:" + e.getMessage(), e);
            throw e;
        }
    }


}
