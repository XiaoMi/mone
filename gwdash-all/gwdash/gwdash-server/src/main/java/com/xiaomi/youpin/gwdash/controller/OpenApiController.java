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

import com.google.common.collect.Maps;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.RequestParam;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
import com.xiaomi.youpin.gwdash.common.GwDashVersion;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvDeploySetting;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xiaomi.youpin.gwdash.exception.CommonError.InvalidParamError;

/**
 * @author goodjava@qq.com
 * @author liuyuchong
 */
@Slf4j
@RestController
public class OpenApiController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private WebSSHService webSSHService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PipelineService pipelineService;

    /**
     * 获取docker资源列表
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/docker/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> resourceList(@RequestBody RequestParam param) {
        com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> result = resourceService.getResourceList(1, 100, 0, Maps.newHashMap());
        if (result.getCode() == 0) {
            return Result.success(result.getData());
        }
        return Result.success(Maps.newHashMap());
    }


    /**
     * 获取所有机器列表
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/machine/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<String>> machineList(@RequestBody RequestParam param) {
        List<String> list = agentManager.clientList();
        return Result.success(list);
    }


    @RequestMapping(value = "/open/plantsshkey", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> plantSshKey(@RequestBody RequestParam param) {
        webSSHService.plantSshKey(param.getAddress());
        return Result.success(true);
    }


    @RequestMapping(value = "/open/removesshkey", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> removeSshKey(@RequestBody RequestParam param) {
        webSSHService.removeSshKey(param.getAddress());
        return Result.success(true);
    }

    /**
     * 用来测试接口
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/test", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<String> test(@RequestBody RequestParam param) {
        return Result.success(new GwDashVersion().toString());
    }


    /**
     * docker nuke
     */
    @RequestMapping(value = "/open/docker/nuke", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> nuke(@RequestBody RequestParam param,
                               @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
                               @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
        log.info("open api nuke params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(InvalidParamError);
        }
        return projectEnvService.setDockerNuke(envId, ip);
    }

    /**
     * docker shutdown
     */
    @RequestMapping(value = "/open/docker/shutdown", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> shutdown(@RequestBody RequestParam param,
                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
                                @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
        log.info("open api shutdown params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(InvalidParamError);
        }
        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip,true);
    }


    /**
     * docker online
     */
    @RequestMapping(value = "/open/docker/online", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> online(@RequestBody RequestParam param,
                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
                                @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
        log.info("open api online params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(InvalidParamError);
        }
        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip,false);
    }

    /**
     * docker 扩容 缩容
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/docker/scale", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> scale(@RequestBody RequestParam param,
                                @org.springframework.web.bind.annotation.RequestParam("projectId") long projectId,
                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
                                @org.springframework.web.bind.annotation.RequestParam("replicate") long replicate) {
        return pipelineService.dockerScale(projectId, envId, replicate);
    }


    /**
     * 根据项目名称或者项目id获取环境列表id （envId）
     */
    @RequestMapping(value = "/open/env/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<ProjectEnv>> envList(@RequestBody RequestParam param,
                                            @org.springframework.web.bind.annotation.RequestParam(required = false) Long projectId,
                                            @org.springframework.web.bind.annotation.RequestParam(required = false) String projectName) {
        log.info("params for open env list, projectId: {}, projectName: {}", projectId, projectName);
        if (projectId == null && StringUtils.isEmpty(projectName)) {
            return Result.fail(InvalidParamError);
        }
        if (projectId == null) {
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                log.error("project not exist, projectName: {}", projectName);
                return Result.fail(InvalidParamError);
            }
            projectId = project.getId();
        }
        return projectEnvService.getList(projectId);
    }

    /**
     * docker 根据envId获取机器列表
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/docker/machines", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> machines(@RequestBody RequestParam param,
                                    @org.springframework.web.bind.annotation.RequestParam("envId") long envId) {
        return projectEnvService.getDockerStatus(envId);
    }


    /**
     * docker 更新容器配置
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/docker/settings/save", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> save(@RequestBody RequestParam<ProjectEnvDeploySetting> param) {

        ProjectEnvDeploySetting projectEnvDeploySetting = param.getParam();
        if (projectEnvDeploySetting == null) {
            return Result.fail(InvalidParamError);
        }

        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvDeploySetting.getEnvId()).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
            String logPath = projectEnvDeploySetting.getLogPath();
            if (null == logPath || org.springframework.util.StringUtils.isEmpty(logPath.trim())) {
                return new Result<>(6, "需配置日志路径", null);
            }

            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
            }
            String jvmParams = projectEnvDeploySetting.getJvmParams();
            if (!org.springframework.util.StringUtils.isEmpty(jvmParams)) {
                projectEnvDeploySetting.setJvmParams(jvmParams.trim());
            }
            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
            long replicate = projectEnvDeploySetting.getReplicate();
            if (maxReplicate < replicate) {
                return new Result<>(1, "最大实例不能小于实例数", false);
            }
            ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(projectEnv.getId()).getData();
            if (null != pipeline) {
                long curReplicate = pipeline.getDeploySetting().getDockerReplicate();
                if (maxReplicate < curReplicate) {
                    return new Result<>(1, "最大实例不能小于当前部署已有实例数", false);
                }
            }
        }
        return projectEnvService.saveDeployment(projectEnvDeploySetting);
    }


}
