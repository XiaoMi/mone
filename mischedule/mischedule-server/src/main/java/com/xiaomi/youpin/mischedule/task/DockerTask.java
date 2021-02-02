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

package com.xiaomi.youpin.mischedule.task;

import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
import com.xiaomi.youpin.mischedule.docker.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author goodjava@qq.com
 *
 * support build
 */
@Component
@Slf4j
public class DockerTask extends AbstractTask {

    @Autowired
    private DockerService dockerService;

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        String param = taskParam.get("param");
        if (StringUtils.isEmpty(param)) {
            log.warn("param is empty");
            return TaskResult.Failure("param is empty");
        }
        DockerParam dockerParam = new Gson().fromJson(param, DockerParam.class);
        return dockerService.build(dockerParam, taskContext);
    }
}
