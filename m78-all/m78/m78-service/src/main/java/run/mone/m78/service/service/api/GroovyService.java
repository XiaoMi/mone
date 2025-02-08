package run.mone.m78.service.service.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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


     public GroovyService() {
        engine = factory.getEngineByName("groovy");
    }


    /**
     * 执行指定的脚本函数，并返回结果
     *
     * @param script 要执行的脚本内容
     * @param functionName 要调用的函数名称
     * @param bindValues 需要绑定到脚本引擎中的变量
     * @param args 调用函数时传递的参数
     * @return 函数执行的结果，如果发生异常则返回异常对象
     */
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
