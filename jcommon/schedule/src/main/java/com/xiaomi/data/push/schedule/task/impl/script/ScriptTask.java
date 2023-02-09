package com.xiaomi.data.push.schedule.task.impl.script;

import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by zhangzhiyong on 09/06/2018.
 * 执行groovy脚本
 */
@Component
public class ScriptTask extends AbstractTask {

    @Task(name = "scriptTask")
    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        String script = param.get("script");

        String function = param.get("function");
        String params = param.get("params");

        Object scriptResult = null;
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            engine.eval(script);
            Invocable invocable = (Invocable) engine;
            scriptResult = invocable.invokeFunction(function, params.split(","));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        TaskResult result = TaskResult.Success();
        result.setData(new Gson().toJson(scriptResult));
        return result;
    }
}
