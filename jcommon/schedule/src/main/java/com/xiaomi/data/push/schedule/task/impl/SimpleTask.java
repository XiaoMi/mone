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
