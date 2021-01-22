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

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.bo.PredictConfigBo;
import com.xiaomi.youpin.gwdash.bo.openApi.*;
import com.xiaomi.youpin.gwdash.common.PredictStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.PredictConfig;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.xiaomi.youpin.oracle.api.service.bo.PredictResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service(interfaceClass = OpenApiService.class, retries = 0, group = "${dubbo.group}")
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    private PredictService predictService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private MachineManagementServiceImp machineManagementService;


    @Override
    public Result<List<PredictConfigBo>> getConfigs() {
        List<PredictConfig> configs = predictService.getActiveConfigs();
        return Result.success(configs.stream().map(e -> {
            PredictConfigBo bo = new PredictConfigBo();
            bo.setProjectId(e.getProjectId());
            bo.setDomain(e.getDomain());
            bo.setType(e.getType());
            bo.setQps(e.getQps());
            bo.setPredictOn(PredictStatusEnum.ON.getCode() == e.getStatus());
            return bo;
        }).collect(Collectors.toList()));
    }

    /**
     * 根据项目名称或者项目id获取环境列表id （envId）
     */
    @Override
    public Result<List<ProjectEnvBo>> envList(ProjectEnvRequest request) {
        log.info("params for open env list, projectId: {}, projectName: {}", request.getProjectId(), request.getProjectName());
        Long projectId = request.getProjectId();
        String projectName = request.getProjectName();
        if (projectId == null && StringUtils.isEmpty(request.getProjectName())) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        if (projectId == null) {
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                log.error("project not exist, projectName: {}", projectName);
                return Result.fail(GeneralCodes.ParamError, "参数错误");
            }
            projectId = project.getId();
        }
        com.xiaomi.youpin.gwdash.common.Result<List<ProjectEnv>> list = projectEnvService.getList(projectId);
        List<ProjectEnv> projectEnvs = list.getData();
        if (CollectionUtils.isEmpty(projectEnvs)) {
            return Result.success(null);
        }
        return Result.success(projectEnvs.stream().map(e -> {
            ProjectEnvBo bo = new ProjectEnvBo();
            BeanUtils.copyProperties(e, bo);
            return bo;
        }).collect(Collectors.toList()));
    }


    /**
     * docker 根据envId获取机器列表
     */
    @Override
    public Result<Map<String, Object>> envMachines(ProjectEnvRequest request) {
        if (request.getEnvId() == null) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        return Result.success(projectEnvService.getDockerStatus(request.getEnvId()).getData());
    }

    @Override
    public Result<ReplicateBo> getReplicateInfo(ProjectEnvRequest request) {
        if (request.getEnvId() == null) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        try {
            ReplicateBo replicateBo = projectEnvService.getReplicateInfo(request.getEnvId());
            return Result.success(replicateBo);
        } catch (IllegalArgumentException e) {
            log.error(e.toString());
            return Result.fail(GeneralCodes.ParamError, e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            return Result.fail(GeneralCodes.ParamError, e.getMessage());
        }
    }


    /**
     * docker nuke
     */
    @Override
    public Result<Boolean> nuke(DockerOptRequest request) {
        Long envId = request.getEnvId();
        String ip = request.getIp();
        if (envId == null || StringUtils.isBlank(ip)) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }

        log.info("open api nuke params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        return Result.success(projectEnvService.setDockerNuke(envId, ip).getData());
    }


    /**
     * docker shutdown
     */
    @Override
    public Result<Boolean> shutdown(DockerOptRequest request) {
        Long envId = request.getEnvId();
        String ip = request.getIp();
        if (envId == null || StringUtils.isBlank(ip)) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }

        log.info("open api shutdown params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        return Result.success(projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, true).getData());
    }


    /**
     * docker online
     */
    @Override
    public Result<Boolean> online(DockerOptRequest request) {
        Long envId = request.getEnvId();
        String ip = request.getIp();
        if (envId == null || StringUtils.isBlank(ip)) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }

        log.info("open api online params: envId: {}, ip: {}", envId, ip);
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        return Result.success(projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, false).getData());
    }


    /**
     * docker 扩容 缩容
     */
    @Override
    public Result<Boolean> scale(DockerOptRequest request) {
        if (request.getProjectId() == null || request.getEnvId() == null || request.getReplicate() == null || request.getReplicate() < 0) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        return Result.success(pipelineService.dockerScale(request.getProjectId(), request.getEnvId(), request.getReplicate()).getData());
    }

    @Override
    public Result<Boolean> tag(DockerTagRequest request) {
        if (StringUtils.isBlank(request.getIp()) || MapUtils.isEmpty(request.getLabels())) {
            return Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        MachineBo machineBo = new MachineBo();
        BeanUtils.copyProperties(request, machineBo);
        return Result.success(machineManagementService.insertOrUpdate(machineBo).getData());
    }

    @Override
    public void setPredictData(String domain, PredictResult predictResult) {
        predictService.setPredict(domain, predictResult);
    }
}
