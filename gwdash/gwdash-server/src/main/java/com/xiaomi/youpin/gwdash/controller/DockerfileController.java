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
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.DockerfileService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class DockerfileController {

    @Autowired
    private DockerfileService dockerfileService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/api/application/docker/startDeploy", method = RequestMethod.POST)
    public Result<ProjectPipeline> startDeploy(
            HttpServletRequest request,
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam(value = "pipelineId", required = false, defaultValue = "0") long pipelineId,
            @RequestParam(name = "commitId") String commitId
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return dockerfileService.startDeploy(account, projectId, envId, pipelineId, commitId);
    }

    @RequestMapping(value = "/api/application/docker/restartDeploy", method = RequestMethod.POST)
    public Result<ProjectPipeline> restartDeploy(
            HttpServletRequest request,
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam(value = "pipelineId", required = false, defaultValue = "0") long pipelineId,
            @RequestParam(name = "commitId") String commitId
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return dockerfileService.restartDeploy(account, projectId, envId, pipelineId, commitId);
    }
}
