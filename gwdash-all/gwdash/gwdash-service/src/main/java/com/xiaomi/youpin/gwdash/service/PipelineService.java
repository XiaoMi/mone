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

import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.gwdash.rocketmq.CodeCheckerHandler;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
import com.xiaomi.youpin.quota.bo.ExpansionBo;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class PipelineService {

    @Autowired
    private Dao dao;

    @Autowired
    private CodeCheckerService codeCheckerService;

    @Autowired
    private ProjectCompilationService projectCompilationService;

    @Autowired
    private DockerfileService dockerfileService;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;

    @Autowired
    private LogService logService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private HealthServiceImp healthServiceImp;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FeiShuService feiShuService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private Redis redis;

    @Reference(group = "${ref.quota.service.group}", interfaceClass = QuotaService.class, check = false, retries = 0)
    private QuotaService quotaService;

    @Value("${docker.home.work.base}")
    private String dockerHomeWorkBase;

    private static final String suffix = ".git";

    public void updateProjectPipeline(ProjectPipeline projectPipeline) {
        dao.update(projectPipeline);
    }

    public Result<ProjectPipeline> getProjectPipeline(long projectId, long envId) {
        ProjectPipeline projectPipeline = dao.fetch(ProjectPipeline.class,
            Cnd.where("projectId", "=", projectId)
                .and("envId", "=", envId)
                .and(Cnd.exps("status", "=", PipelineStatusEnum.NEW.getId())
                    .or("status", "=", PipelineStatusEnum.RUNNING.getId()))
                .desc("id"));
        if (null != projectPipeline) {
            DeployInfo deployInfo = projectPipeline.getDeployInfo();
            if (null != deployInfo && deployInfo.getStatus() == TaskStatus.success.ordinal()) {
                projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
                dao.update(projectPipeline);
                return Result.success(null);
            }
            dao.fetchLinks(projectPipeline, null);
            String commitId = projectPipeline.getDeploySetting().getCommitId();
            projectPipeline.setReview(reviewService.getReviewStatus(projectId, commitId));
        }
        return Result.success(projectPipeline);
    }

    public Result<ProjectPipeline> getProjectPipeline(long envId) {
        ProjectPipeline projectPipeline = dao.fetch(ProjectPipeline.class,
            Cnd.where("envId", "=", envId)
                .and(Cnd.exps("status", "=", PipelineStatusEnum.NEW.getId())
                    .or("status", "=", PipelineStatusEnum.RUNNING.getId()))
                .desc("id"));
        if (null != projectPipeline) {
            dao.fetchLinks(projectPipeline, null);
        }
        return Result.success(projectPipeline);
    }

    public Result<ProjectPipeline> getProjectPipelineById(long id) {
        ProjectPipeline projectPipeline = dao.fetch(ProjectPipeline.class, id);
        if (null != projectPipeline) {
            dao.fetchLinks(projectPipeline, null);
        }
        return Result.success(projectPipeline);
    }

    public Result<ProjectPipeline> getProjectPipelineOfEnv(long envId) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return new Result<>(1, "项目环境不存在", null);
        }
        long pipelineId = projectEnv.getPipelineId();
        return getProjectPipelineById(pipelineId);
    }

    public Result<String> getCompileLog(long compilationId) {
        return Result.success(logService.getLog(LogService.ProjectCompilation, compilationId));
    }

    public Result<String> getDockerBuildLog(long compilationId) {
        return Result.success(logService.getLog(LogService.ProjectDockerBuild, compilationId));
    }

    public Result<String> getCodeCheckLog(long codeCheckId) {
        return Result.success(logService.getLog(LogService.ProjectCodeCheck, codeCheckId));
    }

    public Result<ProjectPipeline> startCodeCheck(long projectId, long envId, SessionAccount account) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }

        GitlabAccessToken gitlabAccessToken =
            gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(2, "需授权access token", null);
        }

        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        int deployType = deploySetting.getDeployType();
        if (deployType == DeployTypeEnum.DOCKERFILE.getId()) {
            long now = System.currentTimeMillis();
            ProjectCodeCheckRecord projectCodeCheckRecord = new ProjectCodeCheckRecord();
            projectCodeCheckRecord.setStep(3);
            projectCodeCheckRecord.setStatus(TaskStatus.success.ordinal());
            projectCodeCheckRecord.setCtime(now);
            projectCodeCheckRecord.setUtime(now);
            dao.insert(projectCodeCheckRecord);
            projectPipeline.setCodeCheckId(projectCodeCheckRecord.getId());
            projectPipeline.setProjectCodeCheckRecord(projectCodeCheckRecord);
            dao.update(projectPipeline);
            return Result.success(projectPipeline);
        }
        // 开启代码检测
        TaskParam taskParam = new TaskParam();
        Map<String, String> param = new HashMap<>();
        param.put("gitUrl", deploySetting.getGitUrl());
        param.put("branch", deploySetting.getCommitId());
        param.put("gitUser", gitlabAccessToken.getName());
        param.put("gitPw", gitlabAccessToken.getToken());
        param.put("tag", CodeCheckerHandler.tag);
        param.put("param", new Gson().toJson(param));
        taskParam.setParam(param);
        ProjectCodeCheckRecord projectCodeCheckRecord = codeCheckerService.startCodeChecker(taskParam).getData();
        projectPipeline.setCodeCheckId(projectCodeCheckRecord.getId());
        projectPipeline.setProjectCodeCheckRecord(projectCodeCheckRecord);
        dao.update(projectPipeline);
        return Result.success(projectPipeline);
    }

    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> startCompile(SessionAccount account, long projectId, long envId) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }

        GitlabAccessToken gitlabAccessToken =
            gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(2, "需授权access token", null);
        }

        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        int langType = deploySetting.getDeployType();
        if (langType == DeployTypeEnum.DOCKERFILE.getId()) {
            DockerParam dockerParam = new DockerParam();
            dockerParam.setGitUrl(deploySetting.getGitUrl());
            dockerParam.setGitUser(gitlabAccessToken.getUsername());
            dockerParam.setDockerfilePath(deploySetting.getDockerfilePath());
            dockerParam.setGitToken(gitlabAccessToken.getToken());
            StringBuffer appName = new StringBuffer();
            appName.append(deploySetting.getGitGroup());
            appName.append(".");
            appName.append(deploySetting.getGitName());
            dockerParam.setAppName(appName.toString().toLowerCase());
            dockerParam.setBranch(deploySetting.getCommitId());
            Map dockerParams = new HashMap();
            dockerParams.put("profile", deploySetting.getMvnProfile());
            dockerParam.setDockerParams(dockerParams);
            ProjectCompileRecord projectCompileRecord = dockerfileService.startDockerBuild(dockerParam).getData();
            projectPipeline.setCompilationId(projectCompileRecord.getId());
            projectPipeline.setProjectCompileRecord(projectCompileRecord);
            dao.update(projectPipeline);
            return Result.success(projectPipeline);
        }

        // 开启新的构建&部署流程
        long now = System.currentTimeMillis();
        projectPipeline.setStatus(PipelineStatusEnum.RUNNING.getId());
        projectPipeline.setUtime(now);
        CompileParam compileParam = new CompileParam();

        String alias = LabelUtils.getLabelValue(deploySetting.getDockerLabels(), "alias");
        compileParam.setAlias(alias);

        compileParam.setGitUrl(deploySetting.getGitUrl());
        compileParam.setBranch(deploySetting.getCommitId());
        compileParam.setProfile(deploySetting.getMvnProfile());
        compileParam.setBuildPath(deploySetting.getBuildPath());
        compileParam.setJarPath(deploySetting.getBuildJarPath());
        compileParam.setRepoType(deploySetting.getXmlSetting());
        compileParam.setGitName(gitlabAccessToken.getName());
        compileParam.setGitToken(gitlabAccessToken.getToken());
        compileParam.setCustomParams(deploySetting.getCustomParams());
        ProjectCompileRecord projectCompileRecord = projectCompilationService.startCloudCompile(compileParam);
        projectPipeline.setCompilationId(projectCompileRecord.getId());
        projectPipeline.setProjectCompileRecord(projectCompileRecord);
        dao.update(projectPipeline);

        return Result.success(projectPipeline);
    }

    /**
     * 需要开启事务,上下游操作数据库的操作比较多
     *
     * @param projectId
     * @param envId
     * @return
     */
    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> startDeploy(long projectId, long envId) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }
        ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建项目", null);
        }
        final DeploySetting deploySetting = projectPipeline.getDeploySetting();
        final int deployType = deploySetting.getDeployType();
        // 物理机部署
        if (deployType == DeployTypeEnum.MACHINE.getId()) {
            projectDeploymentService.createDeployInfo(projectPipeline, false).getData();
            dao.update(projectPipeline);
            return Result.success(projectPipeline);
        }
        // 容器部署
        else if (DeployTypeEnum.isDocker(deployType)) {
            Pair<Boolean, List<MachineBo>> pair = projectDeploymentService.dockerDeploy(projectPipeline).getData();
            projectPipeline.getDeploySetting().setEnvMachineBo(pair.getValue().stream().map(it -> {
                EnvMachineBo bo = new EnvMachineBo();
                bo.setIp(it.getIp());
                bo.setUsed(true);
                bo.setCpuCore(it.getCpuCore());
                return bo;
            }).collect(Collectors.toList()));
            //生成批次
            projectDeploymentService.createDeployInfo(projectPipeline, true).getData();
            dao.update(projectPipeline);
            return Result.success(projectPipeline);
        }
        return new Result<>(1, "需要指定部署方式", null);
    }

    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> startFort(long projectId, long envId) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }

        ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();

        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建项目", null);
        }

        return projectDeploymentService.startBatch(projectPipeline, 0);
    }

    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> startBatch(long projectId, long envId, int batchNum) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }

        ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建项目", null);
        }

        return projectDeploymentService.startBatch(projectPipeline, batchNum);
    }

    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> retryBatch(long projectId, long envId, int batchNum) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }
        ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建项目", null);
        }

        return projectDeploymentService.retryBatch(projectPipeline, batchNum);
    }

    @Transactional("masterTransactionManager")
    public Result<ProjectPipeline> startPipeline(long projectId, long envId, long pipelineId, String commitId, SessionAccount account, boolean force) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null != projectPipeline) {
            return new Result<>(1, "有运行中pipeline, 需先关闭", null);
        }

        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv) {
            return new Result<>(2, "环境没有找到", null);
        }

        //  commit id check
        if (pipelineId == 0 && projectEnvService.isOnline(projectEnv.getGroup())) {
            if (force) {
                log.info("Emergency release: project: {}, commitId: {}, group: {}", projectEnv.getProjectName(), commitId, projectEnv.getGroup());
                Review refusedReview = reviewService.getReview(projectId, commitId, ReviewStatusEnum.REFUSE.getCode());
                if (refusedReview != null) {
                    return new Result<>(1, "已驳回项目不能紧急发布", null);
                }
                Result<Project> project = projectService.getProjectById(projectId);
                String deployNum = redis.get(Keys.deployKey(projectId));
                log.info("project Emergency release num: {}, project id: {}", deployNum, projectId);
                if (!StringUtils.isEmpty(deployNum) && !"null".equals(deployNum) && project.getData().getDeployLimit() <= Integer.parseInt(deployNum)) {
                    return new Result<>(1, "项目紧急发布次数已达上线", null);
                }
            } else {
                Review passedReview = reviewService.getReview(projectId, commitId, ReviewStatusEnum.PASS.getCode());
                if (passedReview == null) {
                    log.error("commit has not passed, commit id: {}", commitId);
                    return new Result<>(1, "commit 未审核, 需先先审核", null);
                }
            }
        }

        // 部署时关闭健康监测
        healthServiceImp.stopAppHealthCheck(envId);

        final int deployType = projectEnv.getDeployType();
        if (0 == deployType) {
            return new Result<>(3, "需选择部署方式", null);
        }
        String deploymentAuthorityName = DeploymentAuthorityEnum.getDeploymentAuthorityName(projectEnv.getAuthority());
        if (StringUtils.isEmpty(deploymentAuthorityName)) {
            return new Result<>(3, "需选择部署权限", null);
        }

        // 回滚操作只走部署流程, 复用之前的状态
        projectPipeline = dao.fetch(ProjectPipeline.class, pipelineId);
        if (null != projectPipeline) {
            // 复用之前构建结果, 需参数修正
            dao.fetchLinks(projectPipeline, null);
            projectPipeline.setId(0);
            ProjectCodeCheckRecord projectCodeCheckRecord = projectPipeline.getProjectCodeCheckRecord();
            projectCodeCheckRecord.setId(0);
            ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
            projectCompileRecord.setId(0);
            dao.insert(projectCodeCheckRecord);
            dao.insert(projectCompileRecord);
            projectPipeline.setCodeCheckId(projectCodeCheckRecord.getId());
            projectPipeline.setCompilationId(projectCompileRecord.getId());
            projectPipeline.setDeployInfo(null);
            projectPipeline.setBuildType("rollback");
            DeploySetting deploySetting = projectPipeline.getDeploySetting();
            if (null != deploySetting) {
                commitId = deploySetting.getCommitId();
            } else {
                commitId = "";
            }
        } else {
            // 开启新的pipeline流程
            projectPipeline = new ProjectPipeline();
        }

        long now = System.currentTimeMillis();
        projectPipeline.setEnvId(envId);
        projectPipeline.setProjectId(projectId);
        projectPipeline.setRollbackId(projectEnv.getPipelineId());
        projectPipeline.setUsername(account.getUsername());
        projectPipeline.setStatus(PipelineStatusEnum.NEW.getId());
        projectPipeline.setCtime(now);
        projectPipeline.setUtime(now);

        // 保存当前环境的pipeline配置快照
        // todo: 检查参数是否合法
        ProjectEnvBuildSetting buildSetting = dao.fetch(ProjectEnvBuildSetting.class, Cnd.where("env_id", "=", envId));
        if (null == buildSetting) {
            return new Result<>(3, "需配置构建参数", null);
        }
        Project project = dao.fetch(Project.class, Cnd.where("id", "=", projectId));
        if (null == project) {
            return new Result<>(6, "对应项目不存在", null);
        }
        ProjectEnvDeploySetting envDeploySetting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));
        if (null == envDeploySetting) {
            return new Result<>(4, "需配置部署设置", null);
        }
        String gitUrl = project.getGitAddress();
        if (!gitUrl.endsWith(suffix)) {
            gitUrl += suffix;
        }
        DeploySetting deploySetting = new DeploySetting();
        deploySetting.setDeployType(deployType);
        deploySetting.setDeploymentAuthorityName(deploymentAuthorityName);
        deploySetting.setBuildPath(buildSetting.getBuildDir());
        deploySetting.setBuildJarPath(buildSetting.getJarDir());
        deploySetting.setCustomParams(buildSetting.getCustomParams());
        deploySetting.setXmlSetting(buildSetting.getXmlSetting());
        deploySetting.setJvmParams(envDeploySetting.getJvmParams());
        // 物理机部署
        if (deployType == DeployTypeEnum.MACHINE.getId()) {
            ProjectEnvPolicy policy = dao.fetch(ProjectEnvPolicy.class, Cnd.where("env_id", "=", envId));
            if (null == policy) {
                return new Result<>(4, "需配置部署策略", null);
            }
            List<EnvMachineBo> envMachineBos = projectEnvService.getMachines(envId).getData();
            if (null == envMachineBos) {
                return new Result<>(5, "部署机器列表空", null);
            }
            envMachineBos = envMachineBos.stream().filter(it -> it.isUsed()).collect(Collectors.toList());
            if (0 == envMachineBos.size()) {
                return new Result<>(5, "部署机器列表空", null);
            }
            deploySetting.setPolicyDeployment(policy.getDeployment());
            deploySetting.setPolicyStop(policy.getStop());
            deploySetting.setPolicyBatchNum(policy.getBatchNum());
            deploySetting.setDeploySettingPath(envDeploySetting.getPath());
            deploySetting.setDeploySettingHeapSize(envDeploySetting.getHeapSize());
            deploySetting.setEnvMachineBo(envMachineBos);
        }
        // 容器部署
        else if (DeployTypeEnum.isDocker(deployType)) {
            ProjectEnvPolicy policy = dao.fetch(ProjectEnvPolicy.class, Cnd.where("env_id", "=", envId));
            if (null == policy) {
                return new Result<>(4, "需配置部署策略", null);
            }
            deploySetting.setPolicyDeployment(policy.getDeployment());
            deploySetting.setPolicyStop(policy.getStop());
            deploySetting.setPolicyBatchNum(policy.getBatchNum());
            deploySetting.setDockerCup(Integer.valueOf(envDeploySetting.getCpu()));
            deploySetting.setDockerfilePath(envDeploySetting.getDockerfilePath());
            deploySetting.setDockerVolume(envDeploySetting.getVolume());
            long memory = envDeploySetting.getMemory();
            if (memory < 2048) {
                return new Result<>(7, "容器内存配置不能低于2G", null);
            }
            deploySetting.setDockerMem(BizUtils.memToDockerMem(memory));
            deploySetting.setDockerReplicate(envDeploySetting.getReplicate());
            deploySetting.setMaxDockerReplicate(envDeploySetting.getMaxReplicate());

            // 初始化参数
            final String workHome = project.getGitGroup() + "_" + project.getGitName();
            deploySetting.setDeploySettingPath(dockerHomeWorkBase + workHome + "/");
            deploySetting.setDeploySettingHeapSize(envDeploySetting.getMemory());
            // 部署日志
            String logPath = envDeploySetting.getLogPath();
            if (null == logPath || StringUtils.isEmpty(logPath.trim())) {
                return new Result<>(6, "需在容器配置中配置日志路径", null);
            }
            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
            }
            String labels = envDeploySetting.getLabels();
            if (StringUtils.isEmpty(labels)) {
                labels = "log_path=" + logPath.trim();
            } else {
                labels = labels + ",log_path=" + logPath.trim();
            }
            deploySetting.setDockerLogPath(logPath);
            deploySetting.setDockerLabels(labels);
        }
        //健康监测地址
        deploySetting.setHealthCheckUrl(envDeploySetting.getHealthCheckUrl());
        deploySetting.setHealthCheckTaskId(projectEnv.getHealthCheckTaskId());
        deploySetting.setGitUrl(gitUrl);
        deploySetting.setGitGroup(project.getGitGroup());
        deploySetting.setGitName(project.getGitName());
        ProjectGen projectGen = project.getProjectGen();
        if (null != projectGen && !StringUtils.isEmpty(projectGen.getType())) {
            deploySetting.setLangType(projectGen.getType());
        }
        deploySetting.setBranch(projectEnv.getBranch());
        deploySetting.setCommitId(commitId);
        deploySetting.setMvnProfile(projectEnv.getProfile());
        projectPipeline.setDeploySetting(deploySetting);
        dao.insert(projectPipeline);
        // 飞书通知
        StringBuffer sb = new StringBuffer();
        sb.append("应用部署中");
        sb.append("\n部署者: ");
        sb.append(account.getName());
        sb.append("\n部署项目: ");
        sb.append(project.getId());
        sb.append("-");
        sb.append(project.getName());
        sb.append("\n部署仓库: ");
        sb.append(project.getGitGroup());
        sb.append("/");
        sb.append(project.getGitName());
        sb.append("\n部署环境: ");
        sb.append(projectEnv.getId());
        sb.append("-");
        sb.append(projectEnv.getName());
        sb.append("\n部署方式: ");
        sb.append(DeployTypeEnum.getDeployName(projectEnv.getDeployType()));
        feiShuService.sendMsg(account.getUsername(), sb.toString());
        if (projectEnvService.isOnline(projectEnv.getGroup()) && Objects.equals(true, force)) {
            reviewService.saveEmergencyLog(projectId, commitId, account.getUsername());
            redis.incr(Keys.deployKey(projectId));
            redis.expire(Keys.deployKey(projectId), (int) TimeUnit.DAYS.toSeconds(31));
        }
        return Result.success(projectPipeline);
    }

    /**
     * 流程检测(判断是否被批准)
     *
     * @param projectId
     * @param envId
     * @param commitId
     * @param accountId
     */
    private void flowCheck(long projectId, long envId, String commitId, Long accountId) {
        log.info("flow check projectId:{} envId:{} commitId:{} accountId:{}", projectId, envId, commitId, accountId);
        boolean res = flowService.isAllow(projectId, envId, commitId, accountId);
        if (!res) {
            throw new RuntimeException("flow check error!");
        }
    }

    @Transactional("masterTransactionManager")
    public Result<Boolean> closePipeline(long projectId, long envId, SessionAccount account, String cmd) {
        ProjectPipeline projectPipeline = getProjectPipeline(projectId, envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "当前pipeline已经关闭", false);
        }
        if (StringUtils.isEmpty(cmd)
            || !("success".equals(cmd)
            || "fail".equals(cmd))) {
            return new Result<>(1, "cmd参数错误", false);
        }
        long now = System.currentTimeMillis();

        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();

        if ("success".equals(cmd)) {
            ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
            if (null == projectCompileRecord
                || projectCompileRecord.getStatus() != NotifyMsg.STATUS_SUCESSS) {
                return new Result<>(1, "不允许标记为部署成功", false);
            }
            DeployInfo deployInfo = projectPipeline.getDeployInfo();
            if (null == deployInfo) {
                return new Result<>(1, "不允许标记为部署成功", false);
            }
            // 更新环境状态，下线不在本次部署列表的机器
            projectDeploymentService.offlineLastMachine(projectPipeline);
            if (null != projectEnv) {
                projectEnv.setPipelineId(projectPipeline.getId());
                deployInfo.setStep(2);
                deployInfo.setStatus(NotifyMsg.STATUS_SUCESSS);
                dao.update(projectEnv);
                StringBuffer sb = new StringBuffer();
                sb.append("应用部署成功");
                sb.append("\n部署环境: ");
                sb.append(projectEnv.getId());
                sb.append("-");
                sb.append(projectEnv.getName());
                feiShuService.sendMsg("", sb.toString());
            }
        } else if ("fail".equals(cmd)) {
            if (null != projectEnv) {
                StringBuffer sb = new StringBuffer();
                sb.append("应用部署失败");
                sb.append("\n部署环境: ");
                sb.append(projectEnv.getId());
                sb.append("-");
                sb.append(projectEnv.getName());
                feiShuService.sendMsg("", sb.toString());
            }
            // 收集pipeline失败的原因
            collectErrorInfo(projectPipeline);
            // todo: 失败逻辑处理
        }
        projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
        projectPipeline.setUtime(now);
        dao.update(projectPipeline);
        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        int deployType = deploySetting.getDeployType();
        if (deployType == DeployTypeEnum.MACHINE.getId()) {
            healthServiceImp.startAppHealthCheck(envId);
        } else if (DeployTypeEnum.isDocker(deployType)) {
            int taskId = projectDeploymentService.startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
            log.info("close pipeline health check start taskId:{}", taskId);
        }
        return Result.success(true);
    }


    public Result<Boolean> dockerScale(long projectId, long envId, long replicate) {
        return dockerScale(projectId, envId, replicate, false);
    }

    @Transactional("masterTransactionManager")
    public Result<Boolean> dockerScale(long projectId, long envId, long replicate, boolean forceShrink) {
        ProjectPipeline projectPipeline = getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", false);
        }

        ProjectCompileRecord projectCompileRecord =
            dao.fetch(ProjectCompileRecord.class,
                Cnd.where("id", "=", projectPipeline.getCompilationId()));

        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建才能上线", false);
        }

        return projectDeploymentService.dockerScale(projectPipeline, replicate, forceShrink);
    }

    public Result<ExpansionBo> dockerScaleNum(long projectId, long envId) {
        ProjectPipeline projectPipeline = getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "pipeline不存在", null);
        }

        ProjectCompileRecord projectCompileRecord =
            dao.fetch(ProjectCompileRecord.class,
                Cnd.where("id", "=", projectPipeline.getCompilationId()));

        if (null == projectCompileRecord
            || projectCompileRecord.getStatus() != TaskStatus.success.ordinal()) {
            return new Result<>(2, "需要先构建才能上线", null);
        }

        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setProjectId(projectId);
        quotaInfo.setBizId(envId);
        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        quotaInfo.setPorts(LabelUtils.getLabesPorts(deploySetting.getDockerLabels()));
        quotaInfo.setCpu(deploySetting.getDockerCup());
        quotaInfo.setMem(deploySetting.getDockerMem());
        quotaInfo.setNum((int) deploySetting.getDockerReplicate());
        return Result.success(quotaService.getExpansionInfo(quotaInfo).getData());
    }

    private void collectErrorInfo(ProjectPipeline projectPipeline) {
        ProjectCodeCheckRecord projectCodeCheckRecord = projectPipeline.getProjectCodeCheckRecord();
        ErrorMessage errorMessage = new ErrorMessage();
        int status = 0;
        if (null == projectCodeCheckRecord
            || (status = projectCodeCheckRecord.getStatus()) == TaskStatus.running.ordinal()) {
            errorMessage.setCode(1);
            errorMessage.setMessage("代码静态检测挂起");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
        if (status == TaskStatus.failure.ordinal()) {
            errorMessage.setCode(2);
            errorMessage.setMessage("代码静态检测没通过");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
        ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        if (null == projectCodeCheckRecord
            || (status = projectCompileRecord.getStatus()) == TaskStatus.running.ordinal()) {
            errorMessage.setCode(1);
            errorMessage.setMessage("代码构建挂起");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
        if (status == TaskStatus.failure.ordinal()) {
            errorMessage.setCode(2);
            errorMessage.setMessage("代码构建没通过");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo
            || (status = deployInfo.getStatus()) == TaskStatus.running.ordinal()) {
            errorMessage.setCode(1);
            errorMessage.setMessage("部署挂起");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
        if (status == TaskStatus.failure.ordinal()) {
            errorMessage.setCode(1);
            errorMessage.setMessage("部署失败");
            projectPipeline.setErrorMessage(errorMessage);
            return;
        }
    }

    public Map<String, Object> statistics() {
        // 总发布次数
        int releaseCount = dao.count(ProjectPipeline.class, null);
        // 总发布成功次数
        int successCount = dao.count(ProjectPipeline.class, null);

        LocalDateTime sevenTime = LocalDateTime.now().minusDays(6);
        // 7天前long类型时间
        long sevenLongTime = sevenTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("7 time{}", sevenLongTime);

        // 7天总发布次数
        int sevenReleaseCount = dao.count(ProjectPipeline.class, Cnd.where("ctime", ">=", sevenLongTime));
        // 7天总发布成功次数
        int sevenSuccessCount = dao.count(ProjectPipeline.class, Cnd.where("ctime", ">=", sevenLongTime));

        // 成功率
        log.info("sevenReleaseCount：{}", sevenReleaseCount);
        log.info("sevenSuccessCount：{}", sevenSuccessCount);
        int successRate = sevenSuccessCount / sevenReleaseCount * 100;
        // 项目总数
        int projectCount = dao.count(Project.class, null);

        // docker数量
        GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("key", "=", Consts.DOCKER_COUNT_KEY));

        Map<String, Object> map = new HashMap<>();
        map.put("projectCount", projectCount);
        map.put("releaseCount", releaseCount);
        map.put("successRate", successRate);
        if (gwStatistics != null) {
            map.put("dockerCount", Integer.valueOf(gwStatistics.getValue()));
        } else {
            map.put("dockerCount", 0);
        }

        return map;
    }

    public Map<String, Object> statisticsChart() {
        Map<String, Object> map = new HashMap<>();

        // 项目类型
        AtomicInteger spring = new AtomicInteger();
        AtomicInteger docean = new AtomicInteger();
        AtomicInteger filter = new AtomicInteger();
        AtomicInteger plugin = new AtomicInteger();
        AtomicInteger other = new AtomicInteger();

        Map<String, Object> projectMap = new HashMap<>();
        List<Project> projectList = dao.query(Project.class, null);
        for (Project project : projectList) {
            if (project.getProjectGen() != null) {
                if (project.getProjectGen().toString().contains("spring")) {
                    spring.addAndGet(1);
                } else if (project.getProjectGen().toString().contains("docean")) {
                    docean.addAndGet(1);
                } else if (project.getProjectGen().toString().contains("filter")) {
                    filter.addAndGet(1);
                } else if (project.getProjectGen().toString().contains("plugin")) {
                    plugin.addAndGet(1);
                } else {
                    other.addAndGet(1);
                }
            }
        }
        projectMap.put("spring", spring);
        projectMap.put("docean", docean);
        projectMap.put("filter", filter);
        projectMap.put("plugin", plugin);
        projectMap.put("other", other);
        map.put("projectType", projectMap);

        // 各环境发布
        Map<String, Object> evnMap = new HashMap<>();
        List<ProjectEnv> projectEnvList = dao.query(ProjectEnv.class, null);
        projectEnvList.forEach(it -> {

            if (evnMap.get(it.getGroup()) == null) {
                evnMap.put(it.getGroup(), 1);
            } else {
                evnMap.put(it.getGroup(), Integer.valueOf(evnMap.get(it.getGroup()).toString()) + 1);
            }

        });
        map.put("environment", evnMap);
        return map;
    }

    public Map<Object, Object> statistics7days() {
        LocalDateTime sevenTime = LocalDateTime.now().minusDays(6);
        long sevenLongTime = sevenTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        log.info("7 time{}", sevenLongTime);

        Map<Object, Object> map = new HashMap<>();
        Map<Object, Object> resultMap = new HashMap<>();
        Map<Object, Object> countMap = new HashMap<>();
        Map<Object, Object> successMap = new HashMap<>();
        Map<Object, Object> failMap = new HashMap<>();
        List<ProjectPipeline> projectPipelineList = dao.query(ProjectPipeline.class, Cnd.where("ctime", ">=", sevenLongTime));
        projectPipelineList.forEach(it -> {
            // 每日发布次数 不判断status
            LocalDateTime localDateTime = this.getDateTimeOfTimestamp(it.getCtime());
            String key = localDateTime.toString().split("T")[0];

            if (countMap.get(key) == null) {
                countMap.put(key, 1);
            } else {
                countMap.put(key, Integer.parseInt(countMap.get(key).toString()) + 1);
            }

            // 每日发布结果 需要判断status
            if (it.getDeployInfo() != null) {

                if (it.getDeployInfo().getStatus() == TaskStatus.success.ordinal()) {
                    if (successMap.get(key) == null) {
                        successMap.put(key, 1);
                    } else {
                        successMap.put(key, Integer.parseInt(successMap.get(key).toString()) + 1);
                    }
                } else if (it.getDeployInfo().getStatus() == TaskStatus.failure.ordinal()) {
                    if (failMap.get(key) == null) {
                        failMap.put(key, 1);
                    } else {
                        failMap.put(key, Integer.parseInt(failMap.get(key).toString()) + 1);
                    }
                }
            }


        });
        resultMap.put("success", successMap);
        resultMap.put("fail", failMap);
        map.put("resultMap", resultMap);
        map.put("countMap", countMap);
        return map;
    }

    public LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

}
