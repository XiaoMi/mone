package com.xiaomi.data.push.schedule.task.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangzhiyong
 * @date 08/06/2018
 * 用来做演示的task
 */
@Component
@Slf4j
public class SimpleTask extends AbstractTask {

    @Task(name = "simpleTask")
    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        log.info("simple task run");
        TaskResult res = TaskResult.Success();
        int key = param.getInt("key");
        Map<String, String> m = Maps.newHashMap();
        m.put("key", String.valueOf(key + 1000));
        res.setData(new Gson().toJson(m));
        return res;
    }
}
