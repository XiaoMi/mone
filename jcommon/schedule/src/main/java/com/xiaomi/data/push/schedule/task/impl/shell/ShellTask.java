package com.xiaomi.data.push.schedule.task.impl.shell;

import com.google.gson.Gson;
import com.xiaomi.data.push.annotation.Task;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhiyong on 09/06/2018.
 * 在机器上执行shell命令
 */
@Component
public class ShellTask extends AbstractTask {


    @Task(name = "shellTask")
    @Override
    public TaskResult execute(TaskParam param, TaskContext context) {
        String path = param.get("path");
        String cmd = param.get("cmd");
        TaskResult result = TaskResult.Success();
        Pair<Integer, List<String>> res = ProcessUtils.process(path, cmd);
        result.setData(new Gson().toJson(res));
        return result;
    }
}
