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

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.oracle.api.service.ScheduleScalingService;
import com.xiaomi.youpin.oracle.api.service.bo.ScheduleBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
public class ScheduleScalingController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    @Reference(check = false, interfaceClass = ScheduleScalingService.class, group = "${ref.oracle.service.group}")
    private ScheduleScalingService scheduleScalingService;


    @PostMapping("/api/schedule/create")
    public Result<Long> createSchedule(HttpServletRequest request, @RequestBody ScheduleBo schedule) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(schedule.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", null);
        }
        return Result.success(scheduleScalingService.createScheduleAction(schedule));
    }

    @PostMapping("/api/schedule/update")
    public Result<Boolean> updateSchedule(HttpServletRequest request, @RequestBody ScheduleBo scheduleBo) {
        ScheduleBo currentSchedule = scheduleScalingService.getSchedule(scheduleBo.getScheduleId());
        if (currentSchedule == null) {
            return new Result(2, "schedule not found", false);
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(currentSchedule.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", null);
        }
        scheduleScalingService.updateScheduleAction(scheduleBo);
        return Result.success(true);
    }

    @PostMapping("/api/schedule/start")
    public Result<Boolean> startSchedule(HttpServletRequest request, @RequestParam long scheduleId) {
        ScheduleBo scheduleBo = scheduleScalingService.getSchedule(scheduleId);
        if (scheduleBo == null) {
            return new Result(2, "schedule not found", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(scheduleBo.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", false);

        }
        return Result.success(scheduleScalingService.startScheduleAction(scheduleId));
    }

    @PostMapping("/api/schedule/pause")
    public Result<Boolean> pauseSchedule(HttpServletRequest request, @RequestParam long scheduleId) {
        ScheduleBo scheduleBo = scheduleScalingService.getSchedule(scheduleId);
        if (scheduleBo == null) {
            return new Result(2, "schedule not found", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(scheduleBo.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", false);

        }
        return Result.success(scheduleScalingService.pauseScheduleAction(scheduleId));
    }

    @PostMapping("/api/schedule/delete")
    public Result<Boolean> deleteSchedule(HttpServletRequest request, @RequestParam long scheduleId) {
        ScheduleBo scheduleBo = scheduleScalingService.getSchedule(scheduleId);
        if (scheduleBo == null) {
            return new Result(2, "schedule not found", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(scheduleBo.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", false);

        }

        return Result.success(scheduleScalingService.deleteScheduledAction(scheduleId));
    }

    @PostMapping("/api/schedule/deleteAll")
    public Result<Boolean> deleteAllSchedules(HttpServletRequest request, @RequestParam long projectId,
                                              @RequestParam long envId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(projectId, account)) {
            return new Result(2, "应用owner才能操作", false);
        }
        scheduleScalingService.deleteAll(projectId, envId);
        return Result.success(true);
    }

    @GetMapping("/api/schedule/getAll")
    public Result<List<ScheduleBo>> getSchedules(HttpServletRequest request,
                                                 @RequestParam long projectId, @RequestParam long envId) {
        return Result.success(scheduleScalingService.getSchedules(projectId, envId));
    }

    @GetMapping("/api/schedule/get")
    public Result<ScheduleBo> getSchedule(@RequestParam long scheduleId) {
        return Result.success(scheduleScalingService.getSchedule(scheduleId));
    }

}


