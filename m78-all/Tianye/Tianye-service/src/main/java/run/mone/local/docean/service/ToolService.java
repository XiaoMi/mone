package run.mone.local.docean.service;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.po.FunctionInfo;

import javax.annotation.Resource;
import javax.script.ScriptException;

/**
 * @author goodjava@qq.com
 * @date 2024/2/26 18:08
 */
@Service
@Slf4j
public class ToolService {


    @Resource
    private GroovyService groovyService;

    /**
     * 调用指定的函数信息对象中的脚本函数。
     *
     * @param functionInfo 包含脚本和函数名称的对象
     * @param param        传递给脚本函数的参数
     * @return 脚本函数执行后返回的JsonElement对象
     */
    public JsonElement call(FunctionInfo functionInfo, JsonElement param) throws ScriptException, NoSuchMethodException {
        String script = functionInfo.getScript();
        String name = functionInfo.getFunctionName();
        return (JsonElement) groovyService.invoke(script, name, Maps.newHashMap(), param, null);
    }

}
