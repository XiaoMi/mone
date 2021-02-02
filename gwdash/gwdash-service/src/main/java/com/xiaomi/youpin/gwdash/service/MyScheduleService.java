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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.mischedule.api.service.ScheduleService;
import com.xiaomi.youpin.mischedule.api.service.bo.RequestBo;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyScheduleService {

    @Reference(interfaceClass = ScheduleService.class, check = false, group = "${ref.mischedule.service.group}")
    private ScheduleService scheduleService;

    public Result<Boolean> pause(int taskId) {
        return scheduleService.pause(taskId);
    }

    public Result<Boolean> start(int taskId) {
        return scheduleService.start(taskId);
    }

    public Result<Boolean> delete(int taskId) {
        return scheduleService.delTask(taskId);
    }

    public Result<Task> getTaskInfo(int taskId) {
        return scheduleService.getTask(taskId);
    }

    public Result<Boolean> submitTask(RequestBo requestBo) {
        return scheduleService.submitTask(requestBo);
    }

    public Result<Integer> submitTask(TaskParam taskParam) {
        taskParam.setCreator("mione");
        return scheduleService.submitTask(taskParam);
    }

    /**
     * 修改任务
     *
     * @param id    任务id
     * @param param 参数编程json
     * @return
     */
    public Result<Boolean> modifyTaskParam(Integer id, String param) {
        return scheduleService.modify(id, param);
    }
}
