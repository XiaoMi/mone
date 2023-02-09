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

package com.xiaomi.youpin.mischedule.task.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.graph.GraphTask;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.task.test.common.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Slf4j
public class DagTaskTest extends BaseTest {


    @Autowired
    private GraphTask graphTask;


    @Autowired
    private TaskManager taskManager;


    @Test
    public void testTask() throws IOException {
        TaskParam taskParam = new TaskParam();
        taskParam.setTaskDef(new TaskDefBean(STaskDef.GraphTask));
        String script = new String(Files.readAllBytes(Paths.get("src/main/resources/task/task.json")));
        taskParam.put("task_config", script);
        TaskContext taskContext = new TaskContext();
        taskManager.submitTask(taskParam,taskContext,true);
    }


    @Test
    public void testJson() throws IOException {
        String str = new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/mischedule/mischedule-server/src/main/resources/task/task.json")));
        GraphTaskContext context = new Gson().fromJson(str, GraphTaskContext.class);
        System.out.println(context);
    }

}
