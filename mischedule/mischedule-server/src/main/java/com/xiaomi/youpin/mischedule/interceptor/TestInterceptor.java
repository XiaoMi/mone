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

package com.xiaomi.youpin.mischedule.interceptor;

import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskInterceptor;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.youpin.mischedule.api.service.bo.TaskStepDo;
import com.xiaomi.youpin.mischedule.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class TestInterceptor implements TaskInterceptor {

    @Autowired
    private Dao dao;

    @Value("${server.type}")
    private String serverType;

    @Override
    public void beforeTask(TaskParam taskParam, TaskContext taskContext) {

    }

    @Override
    public void afterTask(TaskResult taskResult) {

    }

    @Override
    public void onSuccess(int i, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        String detailUrl = "";
        if (serverType == "staging") {
            detailUrl += "http://127.0.0.1:8093/detail?id=" + taskParam.getTaskId();
        } else if (serverType == "c3"||serverType=="c4"){
            detailUrl += "http://xxxx/detail?id=" + taskParam.getTaskId();
        }

        TaskWithBLOBs taskData = taskContext.getTaskData();

        if (!StringUtils.isEmpty(taskParam.getCron())) {
            // send email
            String creator = taskParam.getCreator();
            String name = taskParam.getTaskDef().getName();

            long now = System.currentTimeMillis();

            String body = now + "完成，点击" + detailUrl + "查看";
            EmailService.send(creator+"@xxxxx.com", name + "执行报告", body);
        }

        long now = System.currentTimeMillis();

        TaskStepDo taskStepDo = new TaskStepDo();
        taskStepDo.setTaskId(taskData.getId());
        taskStepDo.setUpdated(now);
        taskStepDo.setSuccessNum(taskData.getSuccessNum());
        taskStepDo.setFailureNum(taskData.getFailureNum());

        dao.insert(taskStepDo);

        log.info("task:{} taskData: {} success", i, taskData);
    }

    @Override
    public void onFailure(int i, TaskParam taskParam, TaskContext taskContext, TaskResult taskResult) {
        TaskWithBLOBs taskData = taskContext.getTaskData();

        long now = System.currentTimeMillis();

        TaskStepDo taskStepDo = new TaskStepDo();
        taskStepDo.setTaskId(taskData.getId());
        taskStepDo.setUpdated(now);
        taskStepDo.setSuccessNum(taskData.getSuccessNum());
        taskStepDo.setFailureNum(taskData.getFailureNum());

        dao.insert(taskStepDo);

        log.info("task:{} taskData: {} failure", i, taskData);
    }
}
