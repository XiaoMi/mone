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

import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvBo;
import com.xiaomi.youpin.gwdash.service.MyScheduleService;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tsingfu
 */
@RestController
@Slf4j
@RequestMapping("/api/health/check")
public class HealthCheckController {

    @Autowired
    private MyScheduleService myScheduleService;

    @Autowired
    private Dao dao;

    @RequestMapping(value = "/pause", method = RequestMethod.GET)
    public Result<Boolean> pause (HttpServletRequest request, @RequestParam int taskId) {
       return myScheduleService.pause(taskId);
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public Result<Boolean> start (HttpServletRequest request, @RequestParam int taskId) {
        return myScheduleService.start(taskId);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result<Map<String, Object>> info (HttpServletRequest request, @RequestParam int taskId) {
        Map<String, Object> map = new HashMap<>();
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, Cnd.where("health_check_task_id", "=", taskId));
        if (null != projectEnv) {
            map.put("projectEnv", projectEnv);
            Project project = dao.fetch(Project.class, Cnd.where("id", "=", projectEnv.getProjectId()));
            if (null != project) {
                map.put("project", project);
            }
        }
        return Result.success(map);
    }
}
