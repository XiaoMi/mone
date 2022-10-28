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
//package com.xiaomi.youpin.gwdash.service;
//
//import com.google.gson.Gson;
//import com.xiaomi.data.push.schedule.task.TaskDefBean;
//import com.xiaomi.data.push.schedule.task.TaskParam;
//import com.xiaomi.youpin.gwdash.bo.DeploySetting;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.PipelineStatusEnum;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.rocketmq.DockerfileHandler;
//import com.xiaomi.youpin.mischedule.STaskDef;
//import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
//import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.rpc.RpcException;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_BASE;
//
//@Slf4j
//@Service
//public class DockerfileService {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private MyScheduleService myScheduleService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectDeploymentService projectDeploymentService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private GitlabService gitlabService;
//
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    public static final String tags = "DockerBuild";
//
//    public Result<ProjectPipeline> startDeploy(SessionAccount account, long projectId, long envId, long pipelineId, String commitId) {
//        Result<ProjectPipeline> result = pipelineService.startPipeline(projectId, envId, pipelineId, commitId, account,false,false);
//        ProjectPipeline projectPipeline = result.getData();
//        if (null != projectPipeline) {
//            return dockerBuild(account, projectId, envId, commitId, projectPipeline);
//        }
//        return result;
//    }
//
//    public Result<ProjectPipeline> dockerBuild(SessionAccount account, long projectId, long envId, String commitId, ProjectPipeline projectPipeline) {
//        GitlabAccessToken gitlabAccessToken =
//                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "需授权gitlab access token", null);
//        }
//        Project project = projectService.getProjectById(projectId).getData();
//        if (null == project) {
//            return new Result<>(2, "项目不存在", null);
//        }
//        ProjectEnv projectEnv =
//                projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(3, "环境不存在", null);
//        }
//        long now = System.currentTimeMillis();
//        ProjectCompileRecord compileRecord = new ProjectCompileRecord();
//        compileRecord.setPipelineId(projectPipeline.getId());
//        compileRecord.setStep(1);
//        compileRecord.setStatus(TaskStatus.running.ordinal());
//        compileRecord.setTime(0);
//        compileRecord.setCtime(now);
//        compileRecord.setUtime(now);
//        dao.insert(compileRecord);
//        projectPipeline.setCompilationId(compileRecord.getId());
//        projectPipeline.setProjectCompileRecord(compileRecord);
//        dao.update(projectPipeline);
//        DeploySetting deploySetting = projectPipeline.getDeploySetting();
//        String gitGroup = project.getGitGroup();
//        String gitName = project.getGitName();
//        String gitUrl = GIT_BASE + gitGroup + "/" + gitName + ".git";
//        TaskParam taskParam = new TaskParam();
//        taskParam.setNotify("mqNotify");
//        taskParam.setTaskDef(new TaskDefBean(STaskDef.DockerTask));
//        DockerParam dockerParam = new DockerParam();
//        dockerParam.setId(compileRecord.getId());
//        dockerParam.setTags(DockerfileHandler.tag);
//        dockerParam.setGitUrl(gitUrl);
//        dockerParam.setGitUser(gitlabAccessToken.getUsername());
//        dockerParam.setGitToken(gitlabAccessToken.getToken());
//        dockerParam.setAppName(gitGroup + "." + gitName);
//        dockerParam.setBranch(commitId);
//        dockerParam.setDockerfilePath(deploySetting.getDockerfilePath());
//        taskParam.put("param", new Gson().toJson(dockerParam));
//        try {
//            myScheduleService.submitTask(taskParam);
//        } catch (RpcException e) {
//            log.info("DockerDeploymentService#dockerBuild, {}", e);
//            compileRecord.setStatus(TaskStatus.failure.ordinal());
//            dao.update(compileRecord);
//            return new Result<>(1, e.getMessage(), projectPipeline);
//        }
//        return Result.success(projectPipeline);
//    }
//
//    public Result<ProjectCompileRecord> startDockerBuild(DockerParam dockerParam) {
//        long now = System.currentTimeMillis();
//        ProjectCompileRecord compileRecord = new ProjectCompileRecord();
//        compileRecord.setStep(1);
//        compileRecord.setStatus(TaskStatus.running.ordinal());
//        compileRecord.setTime(0);
//        compileRecord.setCtime(now);
//        compileRecord.setUtime(now);
//        dao.insert(compileRecord);
//        TaskParam taskParam = new TaskParam();
//        taskParam.setNotify("mqNotify");
//        taskParam.setTaskDef(new TaskDefBean(STaskDef.DockerTask));
//        dockerParam.setId(compileRecord.getId());
//        dockerParam.setTags(DockerfileHandler.tag);
//        taskParam.put("param", new Gson().toJson(dockerParam));
//        try {
//            myScheduleService.submitTask(taskParam);
//        } catch (RpcException e) {
//            log.info("DockerDeploymentService#dockerBuild, {}", e);
//            compileRecord.setStatus(TaskStatus.failure.ordinal());
//            dao.update(compileRecord);
//            return new Result<>(1, e.getMessage(), compileRecord);
//        }
//        return Result.success(compileRecord);
//    }
//
//    public Result<ProjectPipeline> restartDeploy(SessionAccount account, long projectId, long envId, long pipelineId, String commitId) {
//        ProjectPipeline projectPipeline = pipelineService.getProjectPipeline(projectId, envId).getData();
//        if (null != projectPipeline) {
//            projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
//            dao.update(projectPipeline);
//        }
//        startDeploy(account, projectId, envId, pipelineId, commitId);
//        return Result.success(projectPipeline);
//    }
//
//    public Result<ProjectPipeline> dockerDeploy (ProjectCompileRecord compileRecord) {
//        long pipelineId = compileRecord.getPipelineId();
//        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
//        if (null == projectPipeline) {
//            return Result.success(null);
//        }
//        projectDeploymentService.dockerDeploy(projectPipeline);
//        dao.update(projectPipeline);
//        return Result.success(projectPipeline);
//    }
//}
