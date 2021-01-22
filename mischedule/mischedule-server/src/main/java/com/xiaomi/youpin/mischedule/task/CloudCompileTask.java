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
import com.xiaomi.data.push.schedule.task.TaskStatus;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.cloudcompile.CloudCompileService;
import com.xiaomi.youpin.mischedule.cloudcompile.GolangCompileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * <p>
 * 代码编译
 */
@Component
@Slf4j
public class CloudCompileTask extends AbstractTask {

    @Autowired
    private CloudCompileService cloudCompileService;


    @Autowired
    private GolangCompileService golangCompileService;


    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        log.info("invoke compile task :{}", new Gson().toJson(taskParam));
        String bo = taskParam.get("param");
        if (StringUtils.isEmpty(bo)) {
            log.warn("request is empty");
            return TaskResult.Failure("request is empty");
        }
        CompileParam requestBo = new Gson().fromJson(bo, CompileParam.class);
        try {
            if (StringUtils.isEmpty(requestBo.getLanguage())) {
                cloudCompileService.compile(requestBo);
            } else if (requestBo.getLanguage().equals("golang")) {
                golangCompileService.compile(requestBo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return TaskResult.Failure(e.getMessage());
        }

        String bid = Optional.ofNullable(taskContext.get("bid")).orElse(UUID.randomUUID().toString());
        return new TaskResult(TaskStatus.Success.code, "Success", bid);
    }
}
