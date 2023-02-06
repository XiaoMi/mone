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
import com.xiaomi.youpin.gwdash.common.BizUtils;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.quota.bo.ExpansionBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tsingfu
 *
 * 需要确保第一个参数是projectId
 * AOP会使用
 *
 * 项目编译部署相关
 */
@RestController
@Slf4j
public class PipelineController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PipelineService pipelineService;

    @RequestMapping(value = "/api/pipeline/present", method = RequestMethod.POST)
    public Result<ProjectPipeline> getPipeline(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId) {
        return pipelineService.getProjectPipeline(projectId, envId);
    }

    @RequestMapping(value = "/api/pipeline/info", method = RequestMethod.GET)
    public Result<ProjectPipeline> getProjectPipelineById(
            // aop权限校验
            @RequestParam("projectId") long projectId,
            @RequestParam("id") long id) {
        return pipelineService.getProjectPipelineById(id);
    }

    @RequestMapping(value = "/api/pipeline/startPipeline", method = RequestMethod.POST)
    public Result<ProjectPipeline> startPipeline(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam(name = "commitId", required = false) String commitId,
            @RequestParam(value = "pipelineId", required = false, defaultValue = "0") long pipelineId,
            @RequestParam(value = "force", required = false) boolean force,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return pipelineService.startPipeline(projectId, envId, pipelineId, commitId, account,force);
    }

    @RequestMapping(value = "/api/pipeline/closePipeline", method = RequestMethod.POST)
    public Result<Boolean> closePipeline(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam("cmd") String cmd,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return pipelineService.closePipeline(projectId, envId, account, cmd);
    }

    @RequestMapping(value = "/api/pipeline/startCompile", method = RequestMethod.POST)
    public Result<ProjectPipeline> startCompile(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return pipelineService.startCompile(account, projectId, envId);
    }

    @RequestMapping(value = "/api/pipeline/startCodeCheck", method = RequestMethod.POST)
    public Result<ProjectPipeline> startCodeCheck(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return pipelineService.startCodeCheck(projectId, envId, account);
    }

    @RequestMapping(value = "/api/pipeline/startDeploy", method = RequestMethod.POST)
    public Result<ProjectPipeline> startDeploy(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId) {
        return pipelineService.startDeploy(projectId, envId);
    }

    @RequestMapping(value = "/api/pipeline/docker/scale", method = RequestMethod.POST)
    public Result<Boolean> dockerScale (
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam("replicate") long replicate,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!(BizUtils.isSuperRole(account))
                &&replicate > Consts.INSTANCE_LIMIT) {
            return new Result<>(1, "副本数超出限制，请联系效能团队添加", false);
        }
        return pipelineService.dockerScale(projectId, envId, replicate, true);
    }

    @RequestMapping(value = "/api/pipeline/docker/scaleNum", method = RequestMethod.POST)
    public Result<ExpansionBo> dockerScaleNum (
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            HttpServletRequest request) {
        return pipelineService.dockerScaleNum(projectId, envId);
    }

    @RequestMapping(value = "/api/pipeline/startFort", method = RequestMethod.POST)
    public Result<ProjectPipeline> startFort(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId) {
        return pipelineService.startFort(projectId, envId);
    }

    @RequestMapping(value = "/api/pipeline/startBatch", method = RequestMethod.POST)
    public Result<ProjectPipeline> startBatch(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam("batchNum") int batchNum) {
        return pipelineService.startBatch(projectId, envId, batchNum);
    }

    @RequestMapping(value = "/api/pipeline/retryBatch", method = RequestMethod.POST)
    public Result<ProjectPipeline> retryBatch(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId,
            @RequestParam("batchNum") int batchNum) {
        return pipelineService.retryBatch(projectId, envId, batchNum);
    }

    /**
     * 项目总数，总发布次数，最近7天成功率
     * @return
     */
    @RequestMapping(value = "/api/pipeline/statistics", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> statistics() {
        Map<String, Object> map = pipelineService.statistics();
        log.info("PipelineController statistics result:{}", map);
        return Result.success(map);
    }

    /**
     * 项目类型，各环节发布比
     * @return
     */
    @RequestMapping(value = "/api/pipeline/statisticsChart", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> statisticsChart() {
        Map<String, Object> map = pipelineService.statisticsChart();
        log.info("PipelineController statisticsChart result:{}", map);
        return Result.success(map);
    }

    /**
     * 7日内发布成功失败次数和发布次数
     * @return
     */
    @RequestMapping(value = "/api/pipeline/statistics7days", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> statistics7days() {
        Map<Object, Object> map = pipelineService.statistics7days();
        log.info("PipelineController statistics7days result:{}", map);
        return Result.success(map);
    }

}
