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

import com.google.gson.Gson;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvDeploySetting;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvPolicy;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.hermes.bo.AuditRecordBo;
import com.xiaomi.youpin.hermes.service.OperateLogService;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.bo.UpgradeBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.tesla.agent.po.DockerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tsingfu
 */
@RestController
@Slf4j
public class ProjectEnvController {

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PipelineService pipelineService;

    @Reference(check = false, interfaceClass = OperateLogService.class, group = "${ref.hermes.service.group}")
    private OperateLogService operateLogService;

    @Reference(group = "${ref.quota.service.group}", interfaceClass = QuotaService.class, check = false, retries = 0)
    private QuotaService quotaService;

    @RequestMapping(value = "/api/project/env/list", method = RequestMethod.GET)
    public Result<List<ProjectEnv>> getList(@RequestParam("projectId") long projectId) {
        return projectEnvService.getList(projectId);
    }

    @RequestMapping(value = "/api/project/env/info", method = RequestMethod.GET)
    public Result<ProjectEnv> getProjectEnvById(@RequestParam("id") long id) {
        return projectEnvService.getProjectEnvById(id);
    }

    @RequestMapping(value = "/api/project/env/add", method = RequestMethod.POST)
    public Result<Boolean> add(
            HttpServletRequest request,
            @RequestBody ProjectEnv projectEnvBo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        saveLog(request,projectEnvBo);
        if (projectService.isMember(projectEnvBo.getProjectId(), account)) {
            return projectEnvService.add(projectEnvBo);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/edit", method = RequestMethod.POST)
    public Result<Boolean> edit(
            HttpServletRequest request,
            @RequestBody ProjectEnv projectEnvBo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnvBo.getProjectId(), account)) {
            saveLog(request,projectEnvBo);
            return projectEnvService.edit(projectEnvBo);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/del", method = RequestMethod.POST)
    public Result<Boolean> delete(
            HttpServletRequest request,
            @RequestParam("id") long id) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(id).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isOwner(projectEnv.getProjectId(), account)) {
            saveLog(request,id);
            return projectEnvService.delete(projectEnv);
        }
        return new Result<>(1, "项目owner才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/deployment/list", method = RequestMethod.POST)
    public Result<Map<String, Object>> getEnvDeployList(
            @RequestParam("envId") long envId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "100") int pageSize) {
        return projectEnvService.getEnvDeployList(envId, page, pageSize);
    }

    @RequestMapping(value = "/api/project/env/setting/build/save", method = RequestMethod.POST)
    public Result<Boolean> saveBuildSetting(
            HttpServletRequest request,
            @RequestBody ProjectEnvBuildSettingBo buildSettingBo) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(buildSettingBo.getEnvId()).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,buildSettingBo);
            return projectEnvService.saveBuildSetting(buildSettingBo);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/setting/build/get", method = RequestMethod.GET)
    public Result<ProjectEnvBuildSettingBo> getBuildSetting(@RequestParam("envId") long envId) {
        return projectEnvService.getBuildSetting(envId);
    }

    @RequestMapping(value = "/api/project/env/setting/policy/save", method = RequestMethod.POST)
    public Result<Boolean> savePolicy(
            HttpServletRequest request,
            @RequestBody ProjectEnvPolicy projectEnvPolicyBo) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvPolicyBo.getEnvId()).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,projectEnvPolicyBo);
            return projectEnvService.savePolicy(projectEnvPolicyBo);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/obtain/extension/machines", method = RequestMethod.POST)
    public Result<List<EnvMachineBo>> getExtensionMachines(
            HttpServletRequest request,
            @RequestParam("envId") long envId) {
        return projectEnvService.getExtensionMachines(envId);
    }

    @RequestMapping(value = "/api/project/env/extend/machines", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> extendMachines(
            HttpServletRequest request,
            @RequestBody ExtensionMachineBo extensionMachineBo) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(extensionMachineBo.getEnvId()).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            return projectEnvService.extendMachines(extensionMachineBo);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/health/status", method = RequestMethod.POST)
    public Result<String> getHealthCheckStatus(@RequestParam("envId") long envId) {
        return projectEnvService.getHealthCheckStatus(envId);
    }

    @RequestMapping(value = "/api/project/env/health/check/init", method = RequestMethod.POST)
    public Result<Boolean> initHealthCheck(@RequestParam("envId") long envId) {
        boolean success = projectEnvService.initHealthCheck(envId);
        if (!success) {
            return new Result<>(1, "project pipleline或环境不存在", false);
        }
        return Result.success(true);
    }

    @RequestMapping(value = "/api/project/env/health/check/clear", method = RequestMethod.POST)
    public Result<Boolean> clearHealthCheck(@RequestParam("envId") long envId,HttpServletRequest request) {
        boolean success = projectEnvService.clearHealthCheck(envId);
        saveLog(request,envId);
        if (!success) {
            return new Result<>(1, "project pipleline或环境不存在", false);
        }
        return Result.success(true);
    }

    @RequestMapping(value = "/api/project/env/health/check/status", method = RequestMethod.POST)
    public Result<Integer> getDockerHealCheckStatus(@RequestParam("envId") long envId,HttpServletRequest request) {
        saveLog(request,envId);
        return Result.success(projectEnvService.getDockerHealthCheckStatus(envId));
    }

    @RequestMapping(value = "/api/project/env/health/check/open", method = RequestMethod.POST)
    public Result<Boolean> openHealthCheck(
            HttpServletRequest request,
            @RequestParam("envId") long envId) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        saveLog(request,envId);

        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            return projectEnvService.openHealthCheck(envId);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/health/check/close", method = RequestMethod.POST)
    public Result<Boolean> closeHealthCheck(
            HttpServletRequest request,
            @RequestParam("envId") long envId) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,envId);

            return projectEnvService.closeHealthCheck(envId);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/current/release", method = RequestMethod.POST)
    public Result<List<Map<String, Object>>> getReleaseStatus(
            @RequestParam("envId") long envId,HttpServletRequest request) {
        saveLog(request,envId);

        return projectEnvService.getReleaseStatus(envId);
    }

    @RequestMapping(value = "/api/project/env/current/docker", method = RequestMethod.POST)
    public Result<Map<String, Object>> getDockerStatus(
            @RequestParam("envId") long envId) {
        return projectEnvService.getDockerStatus(envId);
    }

    @RequestMapping(value = "/api/project/env/current/dockerUsageRate", method = RequestMethod.POST)
    public Result<DockerInfo> getDockerUsageRate(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "部署环境不存在", null);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            return projectEnvService.getDockerUsageRate(envId, ip);
        }
        return new Result<>(1, "项目成员才可操作", null);
    }

    @RequestMapping(value = "/api/project/env/log/snapshot", method = RequestMethod.POST)
    public Result<String> getLogSnapshot(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", null);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,envId);
            return projectEnvService.getLogSnapshot(envId, ip);
        }
        return new Result<>(1, "项目成员才可操作", null);
    }

    @RequestMapping(value = "/api/project/env/current/online", method = RequestMethod.POST)
    public Result<Boolean> setCurrentOnline(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,envId);
            return projectEnvService.setCurrentOnline(envId, ip);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/current/offline", method = RequestMethod.POST)
    public Result<Map<String, Object>> setCurrentOffline(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", null);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,envId);
            return projectEnvService.setCurrentOffline(envId, ip);
        }
        return new Result<>(1, "项目成员才可操作", null);
    }

    @RequestMapping(value = "/api/project/env/current/nuke", method = RequestMethod.POST)
    public Result<Boolean> setCurrentNuke(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,envId);
            return projectEnvService.setCurrentNuke(envId, ip);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/docker/nuke", method = RequestMethod.POST)
    public Result<Boolean> setDockerNuke(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        Result<Boolean> checkResult = paramsCheck(request, envId);
        if (checkResult.getCode() != 0) {
            return checkResult;
        }
        saveLog(request,envId);

        return projectEnvService.setDockerNuke(envId, ip);
    }

    /**
     * 机器关机
     */
    @RequestMapping(value = "/api/project/env/power/off", method = RequestMethod.GET)
    public Result<Boolean> powerOff(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip
    ) {
        saveLog(request,envId);

        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }

        return projectEnvService.powerOff(ip, envId, projectEnv.getProjectId());
    }

    /**
     * 机器开机
     */
    @RequestMapping(value = "/api/project/env/power/on", method = RequestMethod.GET)
    public Result<Boolean> powerOn(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip
    ) {

        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        log.info("power on userName:{} ip:{}", account.getUsername(), ip);
        saveLog(request,envId);

        return projectEnvService.startPowerOnTask(account.getUsername(), ip, envId, projectEnv.getProjectId());
    }

    private Result<Boolean> paramsCheck(HttpServletRequest request, long envId) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
            return new Result<>(1, "项目成员才可操作", false);
        }
        return Result.success(null);
    }

    @RequestMapping(value = "/api/project/env/docker/shutDown", method = RequestMethod.POST)
    public Result<Boolean> shutDownDockerMachine(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        Result<Boolean> checkResult = paramsCheck(request, envId);
        if (checkResult.getCode() != 0) {
            return checkResult;
        }
        saveLog(request,envId);

        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, true);
    }

    @RequestMapping(value = "/api/project/env/docker/online", method = RequestMethod.POST)
    public Result<Boolean> onlineDockerMachine(
            HttpServletRequest request,
            @RequestParam("envId") long envId,
            @RequestParam("ip") String ip) {
        Result<Boolean> checkResult = paramsCheck(request, envId);
        if (checkResult.getCode() != 0) {
            return checkResult;
        }
        saveLog(request,envId);

        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, false);
    }

    /**
     * 手动漂移
     *
     * @return
     * @author zhangjunyi 许峥
     */
    @RequestMapping(value = "/api/project/env/docker/drift", method = RequestMethod.POST)
    public Result<Boolean> machineDrift(HttpServletRequest request,
                                        @RequestParam("envId") long envId,
                                        @RequestParam("ip") String ip,
                                        @RequestParam("targetIp") String targetIp) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (BizUtils.isSuperRole(account)) {
            saveLog(request,envId);
            return projectEnvService.drift(envId, ip, targetIp);
        }
        return new Result<>(1, "前联系米效团队操作", false);
    }

    @RequestMapping(value = "/api/project/env/setting/policy/get", method = RequestMethod.POST)
    public Result<ProjectEnvPolicy> getPolicy(@RequestParam("envId") long envId) {
        return projectEnvService.getPolicy(envId);
    }

    @RequestMapping(value = "/api/project/env/setting/machine/list", method = RequestMethod.POST)
    public Result<List<EnvMachineBo>> getMachines(@RequestParam("envId") long envId) {
        return projectEnvService.getMachines(envId);
    }

    @RequestMapping(value = "/api/project/env/setting/machine/set", method = RequestMethod.POST)
    public Result<Boolean> setMachines(@RequestBody List<EnvMachineBo> list,HttpServletRequest request) {
        saveLog(request,list);
        return projectEnvService.setMachines(list);
    }

    @RequestMapping(value = "/api/project/env/setting/deployment/save", method = RequestMethod.POST)
    public Result<Boolean> saveDeployment(
            HttpServletRequest request,
            @RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvDeploySetting.getEnvId()).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        long cpu = Long.valueOf(projectEnvDeploySetting.getCpu());
        long memery = projectEnvDeploySetting.getMemory();
        long replicate = projectEnvDeploySetting.getReplicate();

        if (!(BizUtils.isSuperRole(account))
                && (cpu > Consts.CPU_LIMIT
                    || memery > Consts.MEM_LIMIT
                    || replicate > Consts.INSTANCE_LIMIT)) {
            return new Result<>(1, "资源配置超限制，请联系米效团队升配置", null);
        }

        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
            String logPath = projectEnvDeploySetting.getLogPath();
            if (null == logPath || StringUtils.isEmpty(logPath.trim())) {
                return new Result<>(6, "需配置日志路径", null);
            }

            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
            }

            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
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

        String jvmParams = projectEnvDeploySetting.getJvmParams();
        if (!StringUtils.isEmpty(jvmParams)) {
            projectEnvDeploySetting.setJvmParams(jvmParams.trim());
        } else {
            projectEnvDeploySetting.setJvmParams("");
        }

        if (projectService.isMember(projectEnv.getProjectId(), account)) {
            saveLog(request,projectEnvDeploySetting);

            return projectEnvService.saveDeployment(projectEnvDeploySetting);
        }
        return new Result<>(1, "项目成员才可操作", false);
    }

    @RequestMapping(value = "/api/project/env/setting/deployment/upgrade/", method = RequestMethod.POST)
    public Result<List<UpgradeBo>> checkUpgrade (
            HttpServletRequest request,
            @RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
        long envId = projectEnvDeploySetting.getEnvId();
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", null);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
            return new Result<>(1, "项目成员才可操作", null);
        }
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setBizId(projectEnv.getId());
        quotaInfo.setProjectId(projectEnv.getProjectId());
        quotaInfo.setCpu(Integer.valueOf(projectEnvDeploySetting.getCpu()));
        quotaInfo.setMem(BizUtils.memToDockerMem(projectEnvDeploySetting.getMemory()));
        quotaInfo.setPorts(LabelUtils.getLabesPorts(projectEnvDeploySetting.getLabels()));
        quotaInfo.setNum((int) projectEnvDeploySetting.getMaxReplicate());
        List<UpgradeBo> list = quotaService.getUpgradeInfo(quotaInfo).getData();
        if (null != list && list.stream().filter(it -> !(it.isCanUpgrade())).findFirst().isPresent()) {
            return new Result<>(0, "升级配置失败", list);
        }
        return new Result<>(0, "", list);
    }

    @RequestMapping(value = "/api/project/env/setting/deployment/resource/", method = RequestMethod.POST)
    public Result<Boolean> saveComputingResource (
            HttpServletRequest request,
            @RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
        long envId = projectEnvDeploySetting.getEnvId();
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        ProjectEnvDeploySetting deploySetting = projectEnvService.getDeployment(envId).getData();
        if (null == deploySetting) {
            return new Result<>(1, "配置记录不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
            return new Result<>(1, "项目成员才可操作", false);
        }
        long cpu = Long.valueOf(projectEnvDeploySetting.getCpu());
        long memery = projectEnvDeploySetting.getMemory();
        long replicate = projectEnvDeploySetting.getReplicate();
        boolean isAllowed = !(BizUtils.isSuperRole(account))
                && (cpu > Consts.CPU_LIMIT
                || memery > Consts.MEM_LIMIT
                || replicate > Consts.INSTANCE_LIMIT);
        if (isAllowed) {
            return new Result<>(1, "资源配置超限制，请联系米效团队升配置", null);
        }
        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
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
        deploySetting.setCpu(projectEnvDeploySetting.getCpu());
        deploySetting.setMemory(projectEnvDeploySetting.getMemory());
        deploySetting.setReplicate(projectEnvDeploySetting.getReplicate());
        deploySetting.setMaxReplicate(projectEnvDeploySetting.getMaxReplicate());
        return projectEnvService.saveDeployment(deploySetting);
    }

    @RequestMapping(value = "/api/project/env/setting/deployment/other/", method = RequestMethod.POST)
    public Result<Boolean> saveDeploymentSetting(
            HttpServletRequest request,
            @RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
        long envId = projectEnvDeploySetting.getEnvId();
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        ProjectEnvDeploySetting deploySetting = projectEnvService.getDeployment(envId).getData();
        if (null == deploySetting) {
            return new Result<>(1, "配置信息不存在", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
            return new Result<>(1, "项目成员才可操作", false);
        }
        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
            String logPath = projectEnvDeploySetting.getLogPath();
            if (null == logPath || StringUtils.isEmpty(logPath.trim())) {
                return new Result<>(6, "需配置日志路径", null);
            }
            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
            }
        }
        String jvmParams = projectEnvDeploySetting.getJvmParams();
        if (!StringUtils.isEmpty(jvmParams)) {
            deploySetting.setJvmParams(jvmParams.trim());
        } else {
            deploySetting.setJvmParams("");
        }
        deploySetting.setLogPath(projectEnvDeploySetting.getLogPath());
        deploySetting.setLabels(projectEnvDeploySetting.getLabels());
        deploySetting.setVolume(projectEnvDeploySetting.getVolume());
        deploySetting.setHealthCheckUrl(projectEnvDeploySetting.getHealthCheckUrl());
        return projectEnvService.saveDeployment(deploySetting);
    }

    @RequestMapping(value = "/api/project/env/setting/deployment/get", method = RequestMethod.POST)
    public Result<ProjectEnvDeploySetting> getDeployment(@RequestParam("envId") long envId) {
        return projectEnvService.getDeployment(envId);
    }

    @RequestMapping(value = "/api/project/env/commits", method = RequestMethod.POST)
    public Result<List<GitlabCommit>> getCommits(
            HttpServletRequest request,
            @RequestParam("projectId") long projectId,
            @RequestParam("branch") String branch) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectEnvService.getAuditCommits(account, projectId, branch);
    }

    @RequestMapping(value = "/api/project/env/allow/commits", method = RequestMethod.POST)
    public Result<List<GitlabCommit2>> getAllowCommits(
            HttpServletRequest request,
            @RequestParam("envId") long envId) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectEnvService.getAllowCommits(account, envId);
    }

    @RequestMapping(value = "/api/project/env/history/commits", method = RequestMethod.POST)
    public Result<List<GitlabCommit2>> getHistoryCommits(
            HttpServletRequest request,
            @RequestParam("envId") long envId) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectEnvService.getHistoryCommits(account, envId);
    }

    @RequestMapping(value = "/api/project/getCpuAndMemoryAlertInfo")
    public Result<Object> getCpuAndMemoryAlertInfo(@RequestParam("envId") Long envId) {
        Object ret = projectEnvService.getDockInfoFromRedis(envId);
        return Result.success(ret);
    }
    private void saveLog(HttpServletRequest request,Object params){

        SessionAccount account = loginService.getAccountFromSession(request);
        if(account.equals(null)||StringUtils.isEmpty(account.getUsername())){
            log.info("hermes log failed ,no such acount");
            return;
        }
        AuditRecordBo auditRecordBo = new AuditRecordBo();
        auditRecordBo.setUsername(account.getUsername());
        auditRecordBo.setOperation(request.getRequestURI());
        auditRecordBo.setParams(new Gson().toJson(params));
        auditRecordBo.setKey("");
        try {
            operateLogService.auditRecord(auditRecordBo);
        } catch (Throwable e) {
            log.error("hermes log failed,hermes error " + e.getMessage(), e);
        }
    }

}
