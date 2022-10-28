///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;
//import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
//import com.xiaomi.youpin.gwdash.annotation.OperationLog;
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.common.*;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.service.*;
//import com.xiaomi.youpin.quota.bo.QuotaInfo;
//import com.xiaomi.youpin.quota.bo.UpgradeBo;
//import com.xiaomi.youpin.quota.service.QuotaService;
//import com.xiaomi.youpin.tesla.agent.po.DockerInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//public class ProjectEnvController {
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private GwdashApiServiceImpl gwdashApiService;
//
//    @Autowired
//    private NginxService nginxService;
//
//
//    @Autowired
//    private CustomConfigService customConfigService;
//
//
//    @Reference(group = "${ref.quota.service.group}", interfaceClass = QuotaService.class, check = false, retries = 0, timeout = 3000)
//    private QuotaService quotaService;
//
//    @RequestMapping(value = "/api/project/env/list", method = RequestMethod.GET)
//    public Result<List<ProjectEnv>> getList(@RequestParam("projectId") long projectId) {
//        return projectEnvService.getList(projectId);
//    }
//
//    @RequestMapping(value = "/api/project/env/info", method = RequestMethod.GET)
//    public Result<ProjectEnv> getProjectEnvById(@RequestParam("id") long id) {
//        return projectEnvService.getProjectEnvById(id);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/add", method = RequestMethod.POST)
//    public Result<Boolean> add(@RequestBody ProjectEnv projectEnvBo) {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnvBo.getProjectId(), account)) {
//            return projectEnvService.add(projectEnvBo);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/edit", method = RequestMethod.POST)
//    public Result<Boolean> edit(@RequestBody ProjectEnv projectEnvBo) {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnvBo.getProjectId(), account)) {
//            return projectEnvService.edit(projectEnvBo);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.DEL)
//    @RequestMapping(value = "/api/project/env/del", method = RequestMethod.POST)
//    public Result<Boolean> delete(@RequestParam("id") long id) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(id).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isOwner(projectEnv.getProjectId(), account)) {
//            return projectEnvService.delete(projectEnv);
//        }
//        return new Result<>(1, "项目owner才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/deployment/list", method = RequestMethod.POST)
//    public Result<Map<String, Object>> getEnvDeployList(
//            @RequestParam("envId") long envId,
//            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//            @RequestParam(value = "pageSize", required = false, defaultValue = "100") int pageSize) {
//        return projectEnvService.getEnvDeployList(envId, page, pageSize);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/build/save", method = RequestMethod.POST)
//    public Result<Boolean> saveBuildSetting(@RequestBody ProjectEnvBuildSettingBo buildSettingBo) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(buildSettingBo.getEnvId()).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.saveBuildSetting(buildSettingBo);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/build/get", method = RequestMethod.GET)
//    public Result<ProjectEnvBuildSettingBo> getBuildSetting(@RequestParam("envId") long envId) {
//        return projectEnvService.getBuildSetting(envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/nginx/get", method = RequestMethod.GET)
//    public Result<HttpService> getNginxSetting (@RequestParam("envId") long envId) {
//        return nginxService.getNginxServiceByEnvId(envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/nginx/create", method = RequestMethod.POST)
//    public Result<Boolean> creatNginxSetting (
//            @RequestParam("envId") long envId,
//            @RequestParam("port") long port,
//            @RequestParam("sdk") boolean sdk,
//            @RequestParam("group") String group,
//            @RequestParam("upstreamName") String upstreamName,
//            @RequestParam("configPath") String configPath
//    ) {
//        HttpService httpService = nginxService.getNginxServiceByEnvId(envId).getData();
//        if (null != httpService) {
//            return new Result<Boolean>(0, "nginx配置已经存在", false);
//        }
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(0, "部署环境不存在", false);
//        }
//        Project project = projectService.getProjectById(projectEnv.getProjectId()).getData();
//        if (null == project) {
//            return new Result<>(0, "项目不存在", false);
//        }
//        String serviceName = "mione:" + project.getId() + "_" + projectEnv.getId() +"_" + project.getGitGroup() + "_" + project.getGitName();
//        return nginxService.createNginxService(envId, port, sdk, serviceName, upstreamName, configPath, group);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/setting/nginx/update", method = RequestMethod.POST)
//    public Result<Boolean> updateNginxSetting(
//            @RequestParam("envId") long envId,
//            @RequestParam("port") long port,
//            @RequestParam("sdk") boolean sdk,
//            @RequestParam("group") String group,
//            @RequestParam("upstreamName") String upstreamName,
//            @RequestParam("configPath") String configPath) {
//        HttpService httpService = nginxService.getNginxServiceByEnvId(envId).getData();
//        if (null == httpService) {
//            return new Result<Boolean>(0, "更新对象不存在", false);
//        }
//        return nginxService.editNginxService(httpService.getId(), port, sdk, httpService.getServiceName(), upstreamName, configPath, group);
//    }
//
//    @OperationLog(type = OperationLog.LogType.DEL)
//    @RequestMapping(value = "/api/project/env/setting/nginx/delete", method = RequestMethod.GET)
//    public Result<Boolean> removeNginxSetting(@RequestParam("envId") long envId) {
//        HttpService httpService = nginxService.getNginxServiceByEnvId(envId).getData();
//        if (null == httpService) {
//            return new Result<Boolean>(0, "删除对象不存在", false);
//        }
//        return nginxService.delNginxService(httpService.getId());
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/policy/save", method = RequestMethod.POST)
//    public Result<Boolean> savePolicy(@RequestBody ProjectEnvPolicy projectEnvPolicyBo) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvPolicyBo.getEnvId()).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.savePolicy(projectEnvPolicyBo);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/obtain/extension/machines", method = RequestMethod.POST)
//    public Result<List<EnvMachineBo>> getExtensionMachines(@RequestParam("envId") long envId) {
//        return projectEnvService.getExtensionMachines(envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/extend/machines", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> extendMachines(@RequestBody ExtensionMachineBo extensionMachineBo) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(extensionMachineBo.getEnvId()).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.extendMachines(extensionMachineBo);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/health/status", method = RequestMethod.POST)
//    public Result<String> getHealthCheckStatus(@RequestParam("envId") long envId) {
//        return projectEnvService.getHealthCheckStatus(envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/health/check/init", method = RequestMethod.POST)
//    public Result<Boolean> initHealthCheck(@RequestParam("envId") long envId) {
//        boolean success = projectEnvService.initHealthCheck(envId);
//        if (!success) {
//            return new Result<>(1, "project pipleline或环境不存在", false);
//        }
//        return Result.success(true);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/health/check/clear", method = RequestMethod.POST)
//    public Result<Boolean> clearHealthCheck(@RequestParam("envId") long envId) {
//        boolean success = projectEnvService.clearHealthCheck(envId);
//        if (!success) {
//            return new Result<>(1, "project pipleline或环境不存在", false);
//        }
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/project/env/health/check/status", method = RequestMethod.POST)
//    public Result<Integer> getDockerHealCheckStatus(@RequestParam("envId") long envId) {
//        return Result.success(projectEnvService.getDockerHealthCheckStatus(envId));
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/health/check/open", method = RequestMethod.POST)
//    public Result<Boolean> openHealthCheck(@RequestParam("envId") long envId) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.openHealthCheck(envId);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/health/check/close", method = RequestMethod.POST)
//    public Result<Boolean> closeHealthCheck(@RequestParam("envId") long envId) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//
//            return projectEnvService.closeHealthCheck(envId);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/current/release", method = RequestMethod.POST)
//    public Result<List<Map<String, Object>>> getReleaseStatus(
//            @RequestParam("envId") long envId) {
//        return projectEnvService.getReleaseStatus(envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/current/docker", method = RequestMethod.POST)
//    public Result<Map<String, Object>> getDockerStatus(
//            @RequestParam("envId") long envId) {
//        return projectEnvService.getDockerStatus(envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/container/url", method = RequestMethod.GET)
//    public Result<String> getContainerUrl(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        return projectEnvService.getContainerUrl(envId, ip);
//    }
//
//    @RequestMapping(value = "/api/project/env/current/dockerUsageRate", method = RequestMethod.POST)
//    public Result<DockerInfo> getDockerUsageRate(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "部署环境不存在", null);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.getDockerUsageRate(envId, ip);
//        }
//        return new Result<>(1, "项目成员才可操作", null);
//    }
//
//    @RequestMapping(value = "/api/project/env/log/snapshot", method = RequestMethod.POST)
//    public Result<String> getLogSnapshot(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip,
//            @RequestParam(value = "lines", defaultValue = "50", required = false) String lines) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", null);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.getLogSnapshot(envId, ip, lines);
//        }
//        return new Result<>(1, "项目成员才可操作", null);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/current/online", method = RequestMethod.POST)
//    public Result<Boolean> setCurrentOnline(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.setCurrentOnline(envId, ip);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/current/offline", method = RequestMethod.POST)
//    public Result<Map<String, Object>> setCurrentOffline(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", null);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.setCurrentOffline(envId, ip);
//        }
//        return new Result<>(1, "项目成员才可操作", null);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/current/nuke", method = RequestMethod.POST)
//    public Result<Boolean> setCurrentNuke(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.setCurrentNuke(envId, ip);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/docker/nuke", method = RequestMethod.POST)
//    public Result<Boolean> setDockerNuke(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        Result<Boolean> checkResult = paramsCheck(envId);
//        if (checkResult.getCode() != 0) {
//            return checkResult;
//        }
//        return projectEnvService.setDockerNuke(envId, ip);
//    }
//
//    /**
//     * 机器关机
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/power/off", method = RequestMethod.GET)
//    public Result<Boolean> powerOff(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip
//    ) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//
//        return projectEnvService.powerOff(ip, envId, projectEnv.getProjectId());
//    }
//
//    /**
//     * 机器开机
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/power/on", method = RequestMethod.GET)
//    public Result<Boolean> powerOn(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip
//    ) {
//
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//
//        SessionAccount account = loginService.getAccountFromSession();
//        log.info("power on userName:{} ip:{}", account.getUsername(), ip);
//        return projectEnvService.startPowerOnTask(account.getUsername(), ip, envId, projectEnv.getProjectId());
//    }
//
//    private Result<Boolean> paramsCheck(long envId) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
//            return new Result<>(1, "项目成员才可操作", false);
//        }
//        return Result.success(null);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/docker/shutDown", method = RequestMethod.POST)
//    public Result<Boolean> shutDownDockerMachine(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        Result<Boolean> checkResult = paramsCheck(envId);
//        if (checkResult.getCode() != 0) {
//            return checkResult;
//        }
//        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, true);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/docker/online", method = RequestMethod.POST)
//    public Result<Boolean> onlineDockerMachine(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        Result<Boolean> checkResult = paramsCheck(envId);
//        if (checkResult.getCode() != 0) {
//            return checkResult;
//        }
//        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip, false);
//    }
//
//    /**
//     * 手动漂移
//     *
//     * @return
//     * @author zhangjunyi 许峥
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/docker/drift", method = RequestMethod.POST)
//    public Result<Boolean> machineDrift(@RequestParam("envId") long envId,
//                                        @RequestParam("ip") String ip,
//                                        @RequestParam("targetIp") String targetIp) {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (BizUtils.isSuperRole(account)) {
//            return projectEnvService.drift(envId, ip, targetIp);
//        }
//        return new Result<>(1, "前联系米效团队操作", false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/docker/multiDrift", method = RequestMethod.POST)
//    public Result<Boolean> machineMultiDrift(@RequestBody MultiDriftReq param) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (BizUtils.isSuperRole(account)) {
//            return projectEnvService.multiDrift(param.getEnvIds(), param.getIp(), param.getTargetIp());
//        }
//        return new Result<>(1, "前联系米效团队操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/policy/get", method = RequestMethod.POST)
//    public Result<ProjectEnvPolicy> getPolicy(@RequestParam("envId") long envId) {
//        return projectEnvService.getPolicy(envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/machine/list", method = RequestMethod.POST)
//    public Result<List<EnvMachineBo>> getMachines(@RequestParam("envId") long envId) {
//        return projectEnvService.getMachines(envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/setting/machine/set", method = RequestMethod.POST)
//    public Result<Boolean> setMachines(@RequestBody List<EnvMachineBo> list) {
//        return projectEnvService.setMachines(list);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/deployment/save", method = RequestMethod.POST)
//    public Result<Boolean> saveDeployment(@RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvDeploySetting.getEnvId()).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
//            long cpu = Long.valueOf(projectEnvDeploySetting.getCpu());
//            long memery = projectEnvDeploySetting.getMemory();
//            long replicate = projectEnvDeploySetting.getReplicate();
//
//            if (!(BizUtils.isSuperRole(account))
//                    && (cpu > Consts.CPU_LIMIT
//                    || memery > Consts.MEM_LIMIT
//                    || replicate > Consts.INSTANCE_LIMIT)) {
//                return new Result<>(1, "资源配置超限制，请联系米效团队升配置", null);
//            }
//
//            String logPath = projectEnvDeploySetting.getLogPath();
//            if (null == logPath || StringUtils.isEmpty(logPath.trim())) {
//                return new Result<>(6, "需配置日志路径", null);
//            }
//
//            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
//                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
//            }
//
//            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
//            if (maxReplicate < replicate) {
//                return new Result<>(1, "最大实例不能小于实例数", false);
//            }
//            ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(projectEnv.getId()).getData();
//            if (null != pipeline) {
//                long curReplicate = pipeline.getDeploySetting().getDockerReplicate();
//                if (maxReplicate < curReplicate) {
//                    return new Result<>(1, "最大实例不能小于当前部署已有实例数", false);
//                }
//            }
//        }
//
//        String jvmParams = projectEnvDeploySetting.getJvmParams();
//        if (!StringUtils.isEmpty(jvmParams)) {
//            projectEnvDeploySetting.setJvmParams(jvmParams.trim());
//        } else {
//            projectEnvDeploySetting.setJvmParams("");
//        }
//
//        if (projectService.isMember(projectEnv.getProjectId(), account)) {
//            return projectEnvService.saveDeployment(projectEnvDeploySetting);
//        }
//        return new Result<>(1, "项目成员才可操作", false);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/deployment/upgrade/", method = RequestMethod.POST)
//    public Result<List<UpgradeBo>> checkUpgrade(
//            @RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
//        long envId = projectEnvDeploySetting.getEnvId();
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", null);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
//            return new Result<>(1, "项目成员才可操作", null);
//        }
//        QuotaInfo quotaInfo = new QuotaInfo();
//        quotaInfo.setBizId(projectEnv.getId());
//        quotaInfo.setProjectId(projectEnv.getProjectId());
//        quotaInfo.setCpu(Integer.valueOf(projectEnvDeploySetting.getCpu()));
//        quotaInfo.setMem(BizUtils.memToDockerMem(projectEnvDeploySetting.getMemory()));
//        quotaInfo.setPorts(LabelUtils.getLabesPorts(projectEnvDeploySetting.getLabels()));
//        quotaInfo.setNum((int) projectEnvDeploySetting.getMaxReplicate());
//        List<UpgradeBo> list = quotaService.getUpgradeInfo(quotaInfo).getData();
//        if (null != list && list.stream().filter(it -> !(it.isCanUpgrade())).findFirst().isPresent()) {
//            return new Result<>(0, "升级配置失败", list);
//        }
//        return new Result<>(0, "", list);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/deployment/resource/", method = RequestMethod.POST)
//    public Result<Boolean> saveComputingResource(@RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
//        long envId = projectEnvDeploySetting.getEnvId();
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        ProjectEnvDeploySetting deploySetting = projectEnvService.getDeployment(envId).getData();
//        if (null == deploySetting) {
//            return new Result<>(1, "配置记录不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
//            return new Result<>(1, "项目成员才可操作", false);
//        }
//        long cpu = Long.valueOf(projectEnvDeploySetting.getCpu());
//        long memery = projectEnvDeploySetting.getMemory();
//        long replicate = projectEnvDeploySetting.getReplicate();
//        boolean isAllowed = !(BizUtils.isSuperRole(account))
//                && (cpu > Consts.CPU_LIMIT
//                || memery > Consts.MEM_LIMIT
//                || replicate > Consts.INSTANCE_LIMIT);
//        if (isAllowed) {
//            return new Result<>(1, "资源配置超限制，请联系米效团队升配置", null);
//        }
//        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
//            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
//            if (maxReplicate < replicate) {
//                return new Result<>(1, "最大实例不能小于实例数", false);
//            }
//            ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(projectEnv.getId()).getData();
//            if (null != pipeline) {
//                long curReplicate = pipeline.getDeploySetting().getDockerReplicate();
//                if (maxReplicate < curReplicate) {
//                    return new Result<>(1, "最大实例不能小于当前部署已有实例数", false);
//                }
//            }
//        }
//        deploySetting.setCpu(projectEnvDeploySetting.getCpu());
//        deploySetting.setMemory(projectEnvDeploySetting.getMemory());
//        deploySetting.setReplicate(projectEnvDeploySetting.getReplicate());
//        deploySetting.setMaxReplicate(projectEnvDeploySetting.getMaxReplicate());
//        return projectEnvService.saveDeployment(deploySetting);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD)
//    @RequestMapping(value = "/api/project/env/setting/deployment/other/", method = RequestMethod.POST)
//    public Result<Boolean> saveDeploymentSetting(@RequestBody ProjectEnvDeploySetting projectEnvDeploySetting) {
//        long envId = projectEnvDeploySetting.getEnvId();
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        ProjectEnvDeploySetting deploySetting = projectEnvService.getDeployment(envId).getData();
//        if (null == deploySetting) {
//            return new Result<>(1, "配置信息不存在", false);
//        }
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!projectService.isMember(projectEnv.getProjectId(), account)) {
//            return new Result<>(1, "项目成员才可操作", false);
//        }
//        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
//            String logPath = projectEnvDeploySetting.getLogPath();
//            if (null == logPath || StringUtils.isEmpty(logPath.trim())) {
//                return new Result<>(6, "需配置日志路径", null);
//            }
//            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
//                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
//            }
//        }
//        String jvmParams = projectEnvDeploySetting.getJvmParams();
//        if (!StringUtils.isEmpty(jvmParams)) {
//            deploySetting.setJvmParams(jvmParams.trim());
//        } else {
//            deploySetting.setJvmParams("");
//        }
//        deploySetting.setLogPath(projectEnvDeploySetting.getLogPath());
//        deploySetting.setLabels(projectEnvDeploySetting.getLabels());
//        deploySetting.setVolume(projectEnvDeploySetting.getVolume());
//        deploySetting.setHealthCheckUrl(projectEnvDeploySetting.getHealthCheckUrl());
//        deploySetting.setDockerfilePath(projectEnvDeploySetting.getDockerfilePath());
//        deploySetting.setDockerParams(projectEnvDeploySetting.getDockerParams());
//        deploySetting.setJaeger(projectEnvDeploySetting.isJaeger());
//        TCustomeConfig isOpneWhiteList = customConfigService.get("trace_white_list_isopen");
//        TCustomeConfig whiteList = customConfigService.get("trace_white_list");
//        String projectId = String.valueOf(projectEnvService.getProjectEnvById(envId).getData().getProjectId());
//        if ("true".equals(isOpneWhiteList.getContent())){
//            if (!(null != isOpneWhiteList && null != whiteList && "true".equals(isOpneWhiteList.getContent()) && whiteList.getContent().contains("\"" + projectId + "\""))) {
//                deploySetting.setJaeger(false);
//            }
//        }
//        return projectEnvService.saveDeployment(deploySetting);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/deployment/get", method = RequestMethod.POST)
//    public Result<ProjectEnvDeploySetting> getDeployment(@RequestParam("envId") long envId) {
//        return projectEnvService.getDeployment(envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/commits", method = RequestMethod.POST)
//    public Result<List<GitlabCommit>> getCommits(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("branch") String branch) throws GitAPIException, IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        return projectEnvService.getAuditCommits(account, projectId, branch);
//    }
//
//    @RequestMapping(value = "/api/project/env/allow/commits", method = RequestMethod.POST)
//    public Result<List<GitlabCommit2>> getAllowCommits(
//            @RequestParam("envId") long envId) throws GitAPIException, IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        return projectEnvService.getAllowCommits(account, envId);
//    }
//
//    @RequestMapping(value = "/api/project/env/history/commits", method = RequestMethod.POST)
//    public Result<List<GitlabCommit2>> getHistoryCommits(
//            @RequestParam("envId") long envId) throws GitAPIException, IOException {
//        SessionAccount account = loginService.getAccountFromSession();
//        return projectEnvService.getHistoryCommits(account, envId);
//    }
//
//    @RequestMapping(value = "/api/project/getCpuAndMemoryAlertInfo")
//    public Result<Object> getCpuAndMemoryAlertInfo(@RequestParam("envId") Long envId) {
//        Object ret = projectEnvService.getDockInfoFromRedis(envId);
//        return Result.success(ret);
//    }
//
//    @RequestMapping(value = "/api/project/qps", method = RequestMethod.GET)
//    public Result<Map<Object, Integer>> qps() {
//        return projectEnvService.qps();
//    }
//
//
//    @RequestMapping(value = "/api/project/env/docker/inspect", method = RequestMethod.GET)
//    public Result<String> inspect(
//            @RequestParam("envId") long envId,
//            @RequestParam("ip") String ip) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", null);
//        }
////        SessionAccount account = loginService.getAccountFromSession(request);
////        if (projectService.isMember(projectEnv.getProjectId(), account)) {
////            saveLog(request, envId);
////
////        }
//        return projectEnvService.getDockerInspect(envId, ip);
//        //return new Result<>(1, "项目成员才可操作", null);
//    }
//
//    @RequestMapping(value = "/api/project/env/setting/limit", method = RequestMethod.GET)
//    public  Result<Map<String,Object>> getLimitInfo(@RequestParam("env_id")long envId){
//        String requestId = requestId();
//        log.info("getLimitInfo requestId:[{}], envId:[{}]", requestId, envId);
//        ProjectEnvDeploySettingLimit limitInfo = projectEnvService.getLimitInfo(requestId, envId);
//
//        Map<String,Object> result = new HashMap<>();
//        result.put("requestId", requestId);
//        result.put("limitedCpu", limitInfo.getCpuLimit());
//        result.put("limitedMemory", limitInfo.getMemoryLimit());
//        result.put("limitedInstances", limitInfo.getInstanceLimit());
//
//        return Result.success(result);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/setting/limit", method = RequestMethod.POST)
//    public  Result<Object> updateLimit(@RequestBody String body){
//        String requestId = requestId();
//        log.info("getLimitInfo requestId:[{}], body:[{}]", requestId, body);
//        Map<String,Object> result = new HashMap<>();
//        result.put("requestId", requestId);
//
//        JsonObject tokenJson = null;
//        try {
//            tokenJson = new Gson().fromJson(body, JsonObject.class);
//        }catch (Exception e){
//            return new Result<>(1,"限制信息不是JSON格式",result);
//        }
//
//        if(tokenJson == null || tokenJson.isJsonNull()){
//            return new Result<>(1,"没有传入限制参数",result);
//        }
//
//        long envId = 0L;
//        int limitedCpu = 0;
//        int limitedMemory = 0;
//        int limitedInstance = 0;
//        if(tokenJson.has("env_id") && !tokenJson.get("env_id").isJsonNull()){
//            envId = tokenJson.get("env_id").getAsLong();
//        }else{
//            return new Result<>(1,"CPU限制数量为必填",result);
//        }
//
//        if(tokenJson.has("limitedCpu") && !tokenJson.get("limitedCpu").isJsonNull()){
//            limitedCpu = tokenJson.get("limitedCpu").getAsInt();
//            if(limitedCpu <= 0){
//                return new Result<>(1,"CPU限制数量必须大于0",result);
//            }
//        }else{
//            return new Result<>(1,"CPU限制数量为必填",result);
//        }
//
//        if(tokenJson.has("limitedMemory") && !tokenJson.get("limitedMemory").isJsonNull()){
//            limitedMemory = tokenJson.get("limitedMemory").getAsInt();
//            if(limitedMemory <= 0){
//                return new Result<>(1,"内存限制数量必须大于0",result);
//            }
//        }else{
//            return new Result<>(1,"内存限制数量为必填",result);
//        }
//
//        if(tokenJson.has("limitedInstances") && !tokenJson.get("limitedInstances").isJsonNull()){
//            limitedInstance = tokenJson.get("limitedInstances").getAsInt();
//            if(limitedInstance <= 0){
//                return new Result<>(1,"实例限制数量必须大于0",result);
//            }
//        }else{
//            return new Result<>(1,"容器实例限制数量为必填",result);
//        }
//
//        ProjectEnvDeploySettingLimit limitInfo = projectEnvService.saveOrUpdateLimitInfo(requestId, envId, limitedCpu, limitedMemory, limitedInstance);
//
//        result.put("limitedCpu", limitInfo.getCpuLimit());
//        result.put("limitedMemory", limitInfo.getMemoryLimit());
//        result.put("limitedInstances", limitInfo.getInstanceLimit());
//
//        return Result.success(result);
//    }
//
//
//    private String requestId(){
//        return UUID.randomUUID().toString().replace("-","");
//    }
//
//}
